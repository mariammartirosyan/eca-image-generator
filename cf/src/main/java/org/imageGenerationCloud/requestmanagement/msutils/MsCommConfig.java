package org.imageGenerationCloud.requestmanagement.msutils;

import org.imageGenerationCloud.artcontent.Art;
import org.imageGenerationCloud.enums.EMSTypes;
import org.imageGenerationCloud.exceptions.IpRangeException;
import org.imageGenerationCloud.requestmanagement.TcpRequestHandler;
import org.imageGenerationCloud.requestmanagement.TcpRequestor;
import org.imageGenerationCloud.requestmanagement.UdpRequestHandler;
import org.imageGenerationCloud.requestmanagement.UdpRequestor;
import org.imageGenerationCloud.requestmanagement.tasks.RequestorTask;
import org.imageGenerationCloud.servers.TcpServer;
import org.imageGenerationCloud.servers.UdpServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class MsCommConfig {

    // message receiving
    public static BlockingQueue<Socket> tcpRequestQueue = new LinkedBlockingQueue<>();
    public static BlockingQueue<DatagramPacket> udpRequestQueue = new LinkedBlockingQueue<>();
    // message processing
    public static BlockingQueue<String> messageProcessingQueue = new LinkedBlockingQueue<>();
    // message sending
    public static BlockingQueue<RequestorTask> tcpSendingQueue = new LinkedBlockingQueue<>();
    public static BlockingQueue<RequestorTask> udpSendingQueue = new LinkedBlockingQueue<>();
    //ms specific fields
    public static BlockingQueue<Art> artBlockingQueue = new LinkedBlockingQueue<>();
    public static BlockingQueue<List<Art>> artListBlockingQueue = new LinkedBlockingQueue<>();
    private final IMsCommConfigHelper helper;
    private final EMSTypes msType;


    public MsCommConfig(IMsCommConfigHelper helper, EMSTypes msType) throws IpRangeException, IOException {
        this.helper = helper;
        this.msType = msType;
        initWorkers();
        initServers();
    }


    private void initServers() throws IpRangeException, IOException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // initialize processes for listening to tcp and udp requests
        executor.execute(new TcpServer(tcpRequestQueue));
        if (msType != EMSTypes.ART) {
            executor.execute(new UdpServer(udpRequestQueue));
        }
    }

    private void initWorkers() {
        //TODO: separate threads n into variable

        int n1 = 10;
        ExecutorService messageReceivingExecutor = Executors.newFixedThreadPool(n1);
        for (int i = 0; i < n1 / 2; i++) {
            Runnable worker = new TcpRequestHandler(tcpRequestQueue, messageProcessingQueue);
            messageReceivingExecutor.execute(worker);
        }

        for (int i = 0; i < n1 / 2; i++) {
            Runnable worker = new UdpRequestHandler(udpRequestQueue, messageProcessingQueue);
            messageReceivingExecutor.execute(worker);
        }


        int n2 = 5;
        ExecutorService messageProcessingExecutor = Executors.newFixedThreadPool(n2);
        for (int i = 0; i < n2; i++) {
            Runnable worker = helper.getInvoker();
            messageProcessingExecutor.execute(worker);
        }


        int n3 = 10;
        ExecutorService messageSendingExecutor = Executors.newFixedThreadPool(n3);
        for (int i = 0; i < n3 / 2; i++) {
            Runnable worker = new TcpRequestor(tcpSendingQueue);
            messageSendingExecutor.execute(worker);
        }

        for (int i = 0; i < n3 / 2; i++) {
            Runnable worker = new UdpRequestor(udpSendingQueue);
            messageSendingExecutor.execute(worker);
        }
    }
}
