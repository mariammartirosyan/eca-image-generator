package org.imageGenerationCloud.servers;

import org.imageGenerationCloud.requestmanagement.addresses.LocalAddressInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class TcpServer implements Runnable {
    public BlockingQueue<Socket> requestQueue;

    public TcpServer(BlockingQueue<Socket> requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void run() {
        ServerSocket tcpServerSocket = null;

        try {
            // initialize a tcp server socket
            tcpServerSocket = new ServerSocket(LocalAddressInfo.TCP_PORT, 50,
                    InetAddress.getByName(LocalAddressInfo.IP));

            try {
                while (true) {
                    // start accepting requests, thread will be blocked until a request comes
                    System.out.println("Waiting for TCP connection on port: " + LocalAddressInfo.TCP_PORT);

                    Socket request = tcpServerSocket.accept();
                    // handle request in a separate thread
                    requestQueue.add(request);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + LocalAddressInfo.TCP_PORT);
            System.exit(1);
        } finally {
            try {
                tcpServerSocket.close();
            } catch (IOException e) {
                System.err.println("Could not close port " + LocalAddressInfo.TCP_PORT);
                System.exit(1);
            }
        }
    }
}
