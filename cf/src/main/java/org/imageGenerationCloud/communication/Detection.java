package org.imageGenerationCloud.communication;

import org.imageGenerationCloud.enums.EErrorMessages;
import org.imageGenerationCloud.enums.EMSOperations;
import org.imageGenerationCloud.enums.EMSTypes;
import org.imageGenerationCloud.exceptions.IpRangeException;
import org.imageGenerationCloud.exceptions.TerminationException;
import org.imageGenerationCloud.messages.Message;
import org.imageGenerationCloud.messages.MessageContent;
import org.imageGenerationCloud.requestmanagement.addresses.LocalAddressInfo;
import org.imageGenerationCloud.requestmanagement.addresses.SenderAddress;
import org.imageGenerationCloud.requestmanagement.tasks.RequestorTask;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.BlockingQueue;

import static org.imageGenerationCloud.marshaller.Marshaller.marshal;
import static org.imageGenerationCloud.requestmanagement.RequestHandlerUtils.parseAddress;

/**
 * Detect Ip and Ports which are given in the properties File
 */
public class Detection implements Runnable {
    private final Map<EMSTypes, List<MSInstance>> msInstanceMap = new HashMap<>();
    private final List<String> ips;
    private final List<String> ports;
    BlockingQueue<RequestorTask> tcpSendingQueue;
    BlockingQueue<RequestorTask> udpSendingQueue;

    public static String localDir = System.getProperty("user.dir");

    /**
     * @throws IOException if file not going to be found then throw this exception
     * @throws IpRangeException If the ipRangeFrom bigger then the ipRangeTo then this exception is going to be throw
     */
    public Detection(BlockingQueue<RequestorTask> tcpSendingQueue, BlockingQueue<RequestorTask> udpSendingQueue)
            throws IOException, IpRangeException {
        RangeCalculator rangeCalculator = new RangeCalculator(localDir + "//src//main//java//configuration.properties");
        this.ips = rangeCalculator.getIpList();
        this.ports = rangeCalculator.getPortList();

        this.tcpSendingQueue = tcpSendingQueue;
        this.udpSendingQueue = udpSendingQueue;

        configureMSInstanceMap();
    }

    public void run() {
        try {
            while (true) {
                scanNetwork();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Configures the Map with MS Types as a key and empty arrayList as a value
     */
    private void configureMSInstanceMap() {
        msInstanceMap.put(EMSTypes.ART, new ArrayList<>());
        msInstanceMap.put(EMSTypes.ACCOUNT, new ArrayList<>());
        msInstanceMap.put(EMSTypes.STORAGE, new ArrayList<>());
    }

    /**
     * @param details target details
     * Details are going to be splitted and then the instance will be added into the list of the given type
     */
    public synchronized void addMsConnectionDetails(String msType, String details) { // details: "ART:10....:1105:TCP"
        SenderAddress parsedAddress = parseAddress(details);

        MSInstance msInstance = new MSInstance(parsedAddress.ip(), parsedAddress.port(), parsedAddress.requestType());
        EMSTypes msTypeEnum = EMSTypes.valueOf(msType);

        msInstanceMap.get(msTypeEnum).add(msInstance);
        System.out.println("Discovered ms instance: " + msType + ":" + parsedAddress.ip() + ":"
                + parsedAddress.port() + ":" + parsedAddress.requestType());
    }

    /**
     * check connection for all given ips and given ports, if there is a connection
     * save it on the msTypeMap in form address(ip:port) with the information key=address(ip:port) & value=typeMS
     */
    public void scanNetwork() throws IOException, InterruptedException {
        for(String ip: ips) {
            for(String port : ports) {
                int portInt = Integer.parseInt(port);

                if (!isAddressInMap(ip, portInt)) {
                    tryTCPConnection(ip, portInt);
                    tryUDPConnection(ip, portInt);
                    Thread.sleep(100);
                }
            }
        }
    }

    public boolean isAddressInMap(String ip, int port) throws UnknownHostException {
        for (List<MSInstance> msList : msInstanceMap.values()){
            for (MSInstance ms : msList){
                if (InetAddress.getByName(ip).equals(InetAddress.getByName(ms.getIp()))
                        && port == ms.getPort()) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getConnectionMessage(String requestType, int port) {
        MessageContent messageContent = new MessageContent(EMSOperations.CONNECTION.toString(), null, null, 0, 0);
        Message message = new Message(null,
                LocalAddressInfo.IP  + ":" + port + ":" + requestType, null, messageContent);

        return marshal(message);
    }

    /**
     * Tries to connect using TCP connection type
     * @param ip
     * @param port
     * @throws IOException
     */
    private void tryTCPConnection(String ip, int port) throws InterruptedException {
        tcpSendingQueue.put(new RequestorTask(getConnectionMessage("TCP", LocalAddressInfo.TCP_PORT),
                ip, port));
    }

    /**
     * Tries to connect using UDP connection type
     * @param ip
     * @param port
     */
    private void tryUDPConnection(String ip, int port) throws InterruptedException {
        udpSendingQueue.put(new RequestorTask(getConnectionMessage("UDP", LocalAddressInfo.UDP_PORT),
                ip, port));
    }

    public List<String> getIps() {
        return ips;
    }

    public List<String> getPorts() {
        return ports;
    }

    /**
     * @param msType
     * @return the first instance from the list of instances with the given type
     * @throws TerminationException
     */
    public MSInstance getMSInstanceAddress(String msType, String senderAddress, EErrorMessages errorMessage)
            throws TerminationException {
        EMSTypes emsType = EMSTypes.valueOf(msType);
        List<MSInstance> msInstances = msInstanceMap.get(emsType);

        SenderAddress parsedAddress = parseAddress(senderAddress);

        if(msInstances.isEmpty()) {
            throw new TerminationException("There is no instance of type " + msType);
        }
        //TODO: add logic of choosing another instance depending on MS type, if received an error
        //from the previous one

        switch (emsType){
            case ART -> {
                int artInstAmount = msInstances.size();

                //https://www.baeldung.com/java-generating-random-numbers-in-range
                int index = (int) (Math.random() * (artInstAmount - 1));
                return msInstances.get(index);
            }
            case ACCOUNT, STORAGE -> {
                for (MSInstance ms : msInstances){
                    if (Objects.equals(ms.getSocketType(), parsedAddress.requestType())) {
                        if (errorMessage != null) {

                            switch (errorMessage) {
                                case STORAGEOUTOFMEMORY -> {
                                    if (ms.getPort() != parsedAddress.port()) return ms;
                                }
                            }
                        } else return ms;
                    }
                }
            }
        }

        throw new TerminationException("There is no instance of type " + msType +
                " with socket type " + parsedAddress.requestType());
    }
}

