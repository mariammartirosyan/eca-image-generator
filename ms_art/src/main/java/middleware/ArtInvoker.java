package middleware;

import BLL.*;
import config.Config;
import org.imageGenerationCloud.Main;
import org.imageGenerationCloud.artcontent.Art;
import org.imageGenerationCloud.enums.EImageTypes;
import org.imageGenerationCloud.enums.EMSOperations;
import org.imageGenerationCloud.enums.EMSTypes;
import org.imageGenerationCloud.exceptions.OperationTypeException;
import org.imageGenerationCloud.messages.Message;
import org.imageGenerationCloud.messages.MessageContent;
import org.imageGenerationCloud.requestmanagement.addresses.CfAddressInfoForMs;
import org.imageGenerationCloud.requestmanagement.addresses.LocalAddressInfo;
import org.imageGenerationCloud.requestmanagement.addresses.SenderAddress;
import org.imageGenerationCloud.requestmanagement.interfaces.IInvoker;
import org.imageGenerationCloud.requestmanagement.tasks.RequestorTask;

import java.awt.image.BufferedImage;
import java.util.Objects;

import static org.imageGenerationCloud.marshaller.Marshaller.marshal;
import static org.imageGenerationCloud.marshaller.Marshaller.unmarshal;
import static org.imageGenerationCloud.requestmanagement.RequestHandlerUtils.parseAddress;
import static org.imageGenerationCloud.requestmanagement.msutils.MsCommConfig.messageProcessingQueue;
import static org.imageGenerationCloud.requestmanagement.msutils.MsCommConfig.tcpSendingQueue;

public class ArtInvoker implements Runnable, IInvoker {
    String currentTcpAddress = LocalAddressInfo.IP + ":" + LocalAddressInfo.TCP_PORT + ":" + "TCP";

    @Override
    public void run() {
        try {
            while (true) {
                invoke(messageProcessingQueue.take());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void invoke(String marshalledMessage) throws Exception {

        // unmarshall received message and identify operation to be invoked
        Message receivedMsg = unmarshal(marshalledMessage);
        System.out.println("receivedMsg");
        System.out.println(marshalledMessage);

        MessageContent receivedMsgContent = receivedMsg.getMessageContent();

        String operation = receivedMsgContent.getOperation();
        EMSOperations emsOperation = EMSOperations.valueOf(operation);

        // establish connection with cf or invoke business logic based on operation name
        // send a response to cf via requestor
        switch (emsOperation) {
            case CONNECTION -> {
                // in short, first we read connection details which are sent as the first msg parameter
                // then we identify socket connection type, save cf address and send a response
                // with ms instance address details of the according socket type

                String cfAddress = receivedMsg.getSenderAddress();
                SenderAddress parsedCfAddress = parseAddress(cfAddress);

                if (Objects.equals(parsedCfAddress.requestType(), "TCP") && (CfAddressInfoForMs.IP == null
                        || CfAddressInfoForMs.TCP_PORT == 0)) {
                    System.out.println("Connecting ms to cf via TCP");

                    CfAddressInfoForMs.IP = parsedCfAddress.ip();
                    CfAddressInfoForMs.TCP_PORT = parsedCfAddress.port();
                    tcpSendingQueue.put(new RequestorTask(
                            getConnectionMessage(LocalAddressInfo.TCP_PORT, parsedCfAddress.requestType()),
                            parsedCfAddress.ip(), parsedCfAddress.port()));
                }
            }
            case GENERATEART -> {
                Art artDTO = receivedMsgContent.getArt();
                int width = artDTO.getWidth();
                int height = artDTO.getHeight();
                int generationCount = artDTO.getSimulatedGenCount();
                int accountNumber = artDTO.getAccountID();

                Config config = new Config();
                var cellCalculationThreadCount = config.getCellCalculationThreadCount();

                ImageGenerator imageGenerator = null;
                if (artDTO.getImageType().equals(EImageTypes.GEOMETRIC.toString())) {
                    imageGenerator = new GeometricImageGenerator(width, height, generationCount, cellCalculationThreadCount, new GeometricRuleApplier(90), new GeometricImageService());
                } else if (artDTO.getImageType().equals(EImageTypes.COLORFUL.toString())){
                    imageGenerator = new ColorfulImageGenerator(width, height, generationCount, cellCalculationThreadCount, new ColorfulRuleApplier(), new ColorfulImageService());
                }
                BufferedImage image = imageGenerator.generate();
                String base64 = Base64Converter.imageToBase64(image);

                Art artResponse = new Art(0, null, accountNumber, width, height, artDTO.getSimulatedGenCount(), artDTO.getImageType(), base64);
                Message artMessageResponse = new Message(EMSTypes.STORAGE.toString(), currentTcpAddress,null,
                        new MessageContent(EMSOperations.SAVEART.toString(), artResponse, null, 0, 0));
                String marshalledArtMessage = marshal(artMessageResponse);
                System.out.println("sentMsg");
                System.out.println(marshalledArtMessage);
                tcpSendingQueue.put(new RequestorTask(marshalledArtMessage,
                        CfAddressInfoForMs.IP, CfAddressInfoForMs.TCP_PORT));
            }
            default -> throw new OperationTypeException("Wrong operation for MS ART.");
        }
    }

    public String getConnectionMessage(int port , String requestType) {
        Message connectionMessage = new Message(EMSTypes.ART.toString(), LocalAddressInfo.IP + ":"
                + port + ":" + requestType, null,
                new MessageContent(EMSOperations.CONNECTION.toString(), null, null, 0, 0));
        return marshal(connectionMessage);
    }
}
