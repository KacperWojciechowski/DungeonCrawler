package Server;

import CommsFramework.Receiver;
import CommsFramework.Writer;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Main {
    private static final List<JSONObject> recvQueue = new ArrayList<>();
    private static final Semaphore recvQueueSem = new Semaphore(1);
    private static final List<JSONObject> sendQueue = new ArrayList<>();
    private static final Semaphore sendQueueSem = new Semaphore(1);
    private static final Semaphore writerSem = new Semaphore(0);
    private static final Semaphore receiverSem = new Semaphore(0);
    private static final Semaphore readyToSendSem = new Semaphore(0);
    private static final Semaphore readyToReceiveSem = new Semaphore(0);
    private static Game game;

    public static void receive() {
        while(!receiverSem.tryAcquire())
        {
            try {
                readyToReceiveSem.acquire();
                if (receiverSem.tryAcquire()) break;

                recvQueueSem.acquire();
                if (!recvQueue.isEmpty())
                {
                    JSONObject msg = recvQueue.get(0);
                    recvQueue.remove(0);
                    recvQueueSem.release();
                    game.update(msg);
                } else {
                    recvQueueSem.release();
                }
            } catch (InterruptedException e) {
                System.out.println("[System] Reception non-blocking wait interrupted");
            }
        }
    }

    private static void resetGlobals()
    {
        writerSem.drainPermits();
        receiverSem.drainPermits();
        readyToReceiveSem.drainPermits();
        readyToSendSem.drainPermits();
        sendQueue.clear();
        recvQueue.clear();
        sendQueueSem.drainPermits();
        sendQueueSem.release();
        recvQueueSem.drainPermits();
        recvQueueSem.release();
    }

    public static void main(String[] args)
    {
        int port = 2011;
        while (true)
        {
            try (
                    ServerSocket serverSocket = new ServerSocket(port);
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                System.out.println("[System] Player connected");
                resetGlobals();

                Writer writer = new Writer(out, sendQueue, writerSem, sendQueueSem, readyToSendSem);
                Receiver receiver = new Receiver(in, recvQueue, receiverSem, recvQueueSem, readyToReceiveSem);
                game = new Game((JSONObject msg) -> {
                    try {
                        sendQueueSem.acquire();
                        sendQueue.add(msg);
                        sendQueueSem.release();
                        readyToSendSem.release();
                    } catch (InterruptedException e) {
                        System.out.println("[System] Sending semaphore aquisition interrupted");
                    }
                }, receiverSem);
                game.init();


                writer.start();
                receiver.start();

                receive();

                writerSem.release();
                readyToSendSem.release();
                receiverSem.release();
                readyToReceiveSem.release();

                writer.join();
                receiver.join();
            } catch (IOException | InterruptedException e) {
                System.out.println("[System] Graceful shutdown interrupted");
            }
        }
    }
}
