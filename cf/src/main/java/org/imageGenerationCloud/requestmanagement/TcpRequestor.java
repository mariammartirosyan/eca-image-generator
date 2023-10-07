package org.imageGenerationCloud.requestmanagement;

import org.imageGenerationCloud.requestmanagement.interfaces.IRequestor;
import org.imageGenerationCloud.requestmanagement.tasks.RequestorTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class TcpRequestor implements Runnable, IRequestor {
    BlockingQueue<RequestorTask> sendingQueue;

    public TcpRequestor(BlockingQueue<RequestorTask> sendingQueue) {
        this.sendingQueue = sendingQueue;
    }

    @Override
    public void run() {
        RequestorTask task = null;

        while (true) {
            try {
                task = sendingQueue.take();
                send(task);
            } catch (IOException | InterruptedException e) {
                // System.out.println("TCP Connection failed: " + task.ip() + ":" + task.port());
            }
        }
    }

    @Override
    public void send(RequestorTask task) throws IOException {
        String ip = task.ip();
        int port = task.port();
        String message = task.message();

        // set connection timeout exactly as network scanning timeout to avoid workers blocking
        // https://stackoverflow.com/questions/4969760/setting-a-timeout-for-socket-operations
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(ip, port), 100);

        OutputStream s1out = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(s1out);

        // Send a string
        dos.writeUTF(message);

        // Close the connection
        dos.close();
        s1out.close();
        socket.close();
    }
}
