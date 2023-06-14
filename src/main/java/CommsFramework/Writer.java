package CommsFramework;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Writer extends Thread {
    private final List<JSONObject> messages;
    private final PrintWriter socketWriter;
    private final Semaphore queueSem;
    private final Semaphore exitSem;

    private final Semaphore readyToSendSem;
    public Writer(PrintWriter socketWriter, List<JSONObject> messagesQueue, Semaphore exitSem, Semaphore queueSem, Semaphore readyToSendSem)
    {
        this.socketWriter = socketWriter;
        messages = messagesQueue;
        this.exitSem = exitSem;
        this.queueSem = queueSem;
        this.readyToSendSem = readyToSendSem;
    }

    @Override
    public synchronized void run()
    {
        while(!exitSem.tryAcquire())
        {
            try {
                readyToSendSem.acquire();
                queueSem.acquire();
                if (!messages.isEmpty()) {
                    JSONObject msg = messages.get(0);
                    messages.remove(0);
                    queueSem.release();
                    socketWriter.println(msg.toString());
                } else {
                    queueSem.release();
                }
            } catch (InterruptedException e) {
                System.out.println("[System] Sender non-blocking wait interruption");
            }
        }
    }
}
