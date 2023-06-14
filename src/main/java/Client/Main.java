package Client;

import CommsFramework.Receiver;
import CommsFramework.Writer;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

    private static void receive()
    {
        while (!receiverSem.tryAcquire())
        {
            try {
                readyToReceiveSem.acquire();
                recvQueueSem.acquire();
                if (!recvQueue.isEmpty())
                {
                    JSONObject msg = recvQueue.get(0);
                    recvQueue.remove(0);
                    recvQueueSem.release();
                    game.process(msg);
                } else {
                    recvQueueSem.release();
                }
            } catch (InterruptedException e) {
                System.out.println("[System] Reception non-blocking wait interrupted");
            }
        }
    }

    public static void main(String[] args)
    {
        String hostname = "127.0.0.1";
        int port = 2011;

        try (
                Socket clientSocket = new Socket(hostname, port);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            Writer writer = new Writer(out, sendQueue, writerSem, sendQueueSem, readyToSendSem);
            Receiver receiver = new Receiver(in, recvQueue, receiverSem, recvQueueSem, readyToReceiveSem);
            game = new Game((JSONObject msg) -> {
                try {
                    sendQueueSem.acquire();
                    sendQueue.add(msg);
                    sendQueueSem.release();
                    readyToSendSem.release();
                } catch (InterruptedException e)
                {
                    System.out.println("[System] Sending semaphore aquisition interrupted");
                }}, receiverSem);

            writer.start();
            receiver.start();
            game.init();

            receive();

            writerSem.release();
            receiverSem.release();
            writer.join();
            receiver.join();
        } catch (IOException e) {
            System.out.println("[System] Connection dropped. The game will now exit.");
        } catch (InterruptedException e) {
            System.out.println("[System] Graceful shutdown interrupted");
        }
    }
}
