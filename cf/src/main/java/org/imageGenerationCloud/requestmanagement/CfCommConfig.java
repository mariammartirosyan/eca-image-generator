package org.imageGenerationCloud.requestmanagement;

import org.imageGenerationCloud.communication.Detection;
import org.imageGenerationCloud.exceptions.IpRangeException;
import org.imageGenerationCloud.requestmanagement.tasks.RequestorTask;
import org.imageGenerationCloud.servers.TcpServer;
import org.imageGenerationCloud.servers.UdpServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class CfCommConfig {
    // message receiving
    public static BlockingQueue<Socket> tcpRequestQueue = new LinkedBlockingQueue<>();
    public static BlockingQueue<DatagramPacket> udpRequestQueue = new LinkedBlockingQueue<>();
    // message processing
    public static BlockingQueue<String> messageProcessingQueue = new LinkedBlockingQueue<>();
    // message sending
    public static BlockingQueue<RequestorTask> tcpSendingQueue = new LinkedBlockingQueue<>();
    public static BlockingQueue<RequestorTask> udpSendingQueue = new LinkedBlockingQueue<>();
    //cf specific field for invoker
    private final Detection detection;

    public CfCommConfig() throws IpRangeException, IOException {
        detection = new Detection(tcpSendingQueue, udpSendingQueue);
        initWorkers();
        initServers();
    }

    private void initServers() throws IpRangeException, IOException {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // initialize processes for network scanning, listening to tcp and udp requests
        executor.execute(new TcpServer(tcpRequestQueue));
        executor.execute(new UdpServer(udpRequestQueue));
        executor.execute(detection);


        System.out.println("ips: " + detection.getIps());
        System.out.println("ports: " + detection.getPorts());
    }

    private void initWorkers() {
        //TODO: separate num of threads into variable

        int n1 = 6;
        ExecutorService messageReceivingExecutor = Executors.newFixedThreadPool(n1);
        for (int i = 0; i < n1 / 2; i++) {
            Runnable worker = new TcpRequestHandler(tcpRequestQueue, messageProcessingQueue);
            messageReceivingExecutor.execute(worker);
        }

        for (int i = 0; i < n1 / 2; i++) {
            Runnable worker = new UdpRequestHandler(udpRequestQueue, messageProcessingQueue);
            messageReceivingExecutor.execute(worker);
        }


        int n2 = 6;
        ExecutorService messageProcessingExecutor = Executors.newFixedThreadPool(n2);
        for (int i = 0; i < n2; i++) {
            Runnable worker = new CfInvoker(detection);
            messageProcessingExecutor.execute(worker);
        }


        int n3 = 20;
        ExecutorService tcpMessageSendingExecutor = Executors.newFixedThreadPool(n3);
            for (int i = 0; i < n3; i++) {
            Runnable worker = new TcpRequestor(tcpSendingQueue);
                tcpMessageSendingExecutor.execute(worker);
        }

        int n4 = 20;
        ExecutorService udpMessageSendingExecutor = Executors.newFixedThreadPool(n4);

        for (int i = 0; i < n4; i++) {
            Runnable worker = new UdpRequestor(udpSendingQueue);
            udpMessageSendingExecutor.execute(worker);
        }
    }
}
