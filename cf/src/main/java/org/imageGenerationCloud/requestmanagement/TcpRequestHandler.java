package org.imageGenerationCloud.requestmanagement;

import org.imageGenerationCloud.requestmanagement.interfaces.IRequestHandler;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.*;
import java.util.concurrent.BlockingQueue;

public class TcpRequestHandler implements Runnable, IRequestHandler<Socket> {
    BlockingQueue<Socket> requestQueue;
    BlockingQueue<String> messageProcessingQueue;

    public TcpRequestHandler(BlockingQueue<Socket> requestQueue,
                             BlockingQueue<String> messageProcessingQueue) {
        this.requestQueue = requestQueue;
        this.messageProcessingQueue = messageProcessingQueue;
    }

    public void run() {
        try {
            while (true) {
                receive(requestQueue.take());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void receive(Socket s1) throws Exception {
        InputStream s1In = s1.getInputStream();
        DataInputStream dis = new DataInputStream(s1In);
        String message = dis.readUTF();

        System.out.println("Invoking operation via TCP");
        messageProcessingQueue.put(message);

        // When done, just close the connection and exit
        dis.close();
        s1In.close();
        s1.close();
    }
}
