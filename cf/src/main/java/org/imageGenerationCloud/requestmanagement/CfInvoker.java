package org.imageGenerationCloud.requestmanagement;

import org.imageGenerationCloud.communication.Detection;
import org.imageGenerationCloud.communication.MSInstance;
import org.imageGenerationCloud.enums.EErrorMessages;
import org.imageGenerationCloud.enums.EMSOperations;
import org.imageGenerationCloud.exceptions.OperationTypeException;
import org.imageGenerationCloud.messages.Message;
import org.imageGenerationCloud.messages.MessageContent;
import org.imageGenerationCloud.requestmanagement.interfaces.IInvoker;
import org.imageGenerationCloud.requestmanagement.tasks.RequestorTask;


import static org.imageGenerationCloud.marshaller.Marshaller.marshal;
import static org.imageGenerationCloud.marshaller.Marshaller.unmarshal;
import static org.imageGenerationCloud.requestmanagement.CfCommConfig.*;

public class CfInvoker implements Runnable, IInvoker {
    private final Detection detection;

    public CfInvoker(Detection detection) {
        this.detection = detection;
    }

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

    public void invoke(String marshalledMessage) throws Exception {

        // unmarshall received message and identify operation to be invoked
        Message receivedMsg = unmarshal(marshalledMessage);
        MessageContent receivedMsgContent = receivedMsg.getMessageContent();

        String operation = receivedMsgContent.getOperation();
        String errorMessage = receivedMsg.getErrorMessage();

        MSInstance msInstance = null;

        // check for errors, establish connection with ms or redirect the message to target ms
        if (errorMessage != null){
            EErrorMessages errorType = EErrorMessages.valueOf(errorMessage);

            switch (errorType) {
                case STORAGEOUTOFMEMORY -> {
                    msInstance = detection.getMSInstanceAddress(receivedMsg.getTargetMS(),
                            receivedMsg.getSenderAddress(), EErrorMessages.STORAGEOUTOFMEMORY);

                    receivedMsg.setErrorMessage(null);
                    String marshalledHandledMessage = marshal(receivedMsg);
                    tcpSendingQueue.put(new RequestorTask(marshalledHandledMessage, msInstance.getIp(), msInstance.getPort()));
                }
            }
        } else if (operation != null) {
            EMSOperations emsOperation = EMSOperations.valueOf(operation);

            if (emsOperation != EMSOperations.CONNECTION) {
                msInstance = detection.getMSInstanceAddress(receivedMsg.getTargetMS(),
                        receivedMsg.getSenderAddress(), null);
            }

            switch (emsOperation) {
                case CONNECTION -> detection.addMsConnectionDetails(receivedMsg.getTargetMS(),
                        receivedMsg.getSenderAddress());

                // TCP
                case GETART, GETALLARTBYACCOUNT, DELETEART, DELETEALLARTBYACCOUNT, GENERATEART, SAVEART -> {
                    tcpSendingQueue.put(new RequestorTask(marshalledMessage, msInstance.getIp(), msInstance.getPort()));
                }

                // UDP
                case RETURNART, RETURNALLARTBYACCOUNT -> {
                    udpSendingQueue.put(new RequestorTask(marshalledMessage, msInstance.getIp(), msInstance.getPort()));
                }
                default -> throw new OperationTypeException("The given operation name does not exist!");
            }
        }
    }
}
