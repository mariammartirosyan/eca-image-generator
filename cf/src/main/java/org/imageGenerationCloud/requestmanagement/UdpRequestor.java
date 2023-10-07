package org.imageGenerationCloud.requestmanagement;

import org.imageGenerationCloud.requestmanagement.interfaces.IRequestor;
import org.imageGenerationCloud.requestmanagement.tasks.RequestorTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

public class UdpRequestor implements Runnable, IRequestor {
    BlockingQueue<RequestorTask> sendingQueue;

    public UdpRequestor(BlockingQueue<RequestorTask> sendingQueue) {
        this.sendingQueue = sendingQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                send(sendingQueue.take());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void send(RequestorTask task) {
        String ip = task.ip();
        int port = task.port();
        String message = task.message();

        try (DatagramSocket aSocket = new DatagramSocket()) {

            byte[] messageBytes = message.getBytes();
            InetAddress aHost = InetAddress.getByName(ip);
            DatagramPacket request = new DatagramPacket(messageBytes, messageBytes.length, aHost, port);
            aSocket.send(request);
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
