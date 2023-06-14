package CommsFramework;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Receiver  extends Thread {
    private final List<JSONObject> messages;
    private final BufferedReader socketReader;
    private final Semaphore exitSem;
    private final Semaphore recvQueueSem;
    private final Semaphore readyToReceiveSem;
    public Receiver(BufferedReader socketReader, List<JSONObject> msgList, Semaphore exitSem, Semaphore recvQueueSem, Semaphore readyToReceiveSem)
    {
        this.messages = msgList;
        this.socketReader = socketReader;
        this.exitSem = exitSem;
        this.recvQueueSem = recvQueueSem;
        this.readyToReceiveSem = readyToReceiveSem;
    }

    @Override
    public synchronized void run()
    {
        while(!exitSem.tryAcquire())
        {
            try {
                String msg = socketReader.readLine();
                if (msg != null)
                {
                    JSONObject jsonObject = new JSONObject(msg);
                    recvQueueSem.acquire();
                    messages.add(jsonObject);
                    recvQueueSem.release();
                    readyToReceiveSem.release();
                }
                recvQueueSem.release();
            } catch (IOException | InterruptedException e) {
                exitSem.release(3);
                readyToReceiveSem.release();
            }
        }
    }
}
