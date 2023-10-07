package org.imageGenerationCloud.servers;

import org.imageGenerationCloud.requestmanagement.addresses.LocalAddressInfo;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.BlockingQueue;

public class UdpServer implements Runnable {
    public BlockingQueue<DatagramPacket> requestQueue;

    public UdpServer(BlockingQueue<DatagramPacket> requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void run() {
        DatagramSocket aSocket = null;

        try {
            // initialize a udp server socket and buffer
            aSocket = new DatagramSocket(LocalAddressInfo.UDP_PORT, InetAddress.getByName(LocalAddressInfo.IP));

            try {
                while (true) {
                    System.out.println("Waiting for UDP connection on port: " + LocalAddressInfo.UDP_PORT);

                    // start accepting requests into a buffer, thread will be blocked until a request comes
                    byte[] buffer = new byte[10000];
                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(request);
                    // handle request in a separate thread
                    requestQueue.add(request);
                }
            } catch (IOException e) {
                System.out.println("IO: " + e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }
}
