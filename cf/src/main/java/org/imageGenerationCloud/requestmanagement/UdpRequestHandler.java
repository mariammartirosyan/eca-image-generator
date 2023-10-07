package org.imageGenerationCloud.requestmanagement;

import org.imageGenerationCloud.requestmanagement.interfaces.IRequestHandler;

import java.net.DatagramPacket;
import java.util.concurrent.BlockingQueue;

import static org.imageGenerationCloud.requestmanagement.RequestHandlerUtils.pruneByteArray;

public class UdpRequestHandler implements Runnable, IRequestHandler<DatagramPacket> {
    BlockingQueue<DatagramPacket> requestQueue;
    BlockingQueue<String> messageProcessingQueue;

    public UdpRequestHandler(BlockingQueue<DatagramPacket> requestQueue,
                             BlockingQueue<String> messageProcessingQueue) {
        this.requestQueue = requestQueue;
        this.messageProcessingQueue = messageProcessingQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                receive(requestQueue.take());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void receive(DatagramPacket clientPacket) throws Exception {
        String message = new String(pruneByteArray(clientPacket.getData()));

        System.out.println("Invoking operation via UDP");
        messageProcessingQueue.put(message);
    }
}
