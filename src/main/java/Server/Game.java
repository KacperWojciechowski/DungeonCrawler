package Server;

import CommsFramework.Action;
import CommsFramework.Key;
import CommsFramework.SenderCallback;
import CommsFramework.Status;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Semaphore;

public class Game {
    private final SenderCallback senderCallback;
    JSONObject lastValidRequest = null;
    boolean initSucceeded = false;
    private final Semaphore exitSem;
    public Game(SenderCallback senderCallback, Semaphore exitSem)
    {
        this.senderCallback = senderCallback;
        this.exitSem = exitSem;
    }

    public void init()
    {
        initSucceeded = true;
        // create map
    }
    public void update(JSONObject msg)
    {
        System.out.println(Action.getByID(msg.getInt(Key.action.toString())) != Action.undefined);
        if (Action.getByID(msg.getInt(Key.action.toString())) != Action.undefined) {
            lastValidRequest = msg;
            processRequest(msg);
        } else if (lastValidRequest != null) {
            processRequest(lastValidRequest);
        } else {
            JSONObject response = new JSONObject();
            response.put(Key.action.name(), Action.start.getID());
            response.put(Key.status.name(), Status.Error.getID());
            response.put(Key.retransmit.name(), Status.Error.getID());
            senderCallback.send(response);
        }
    }

    private void processStart()
    {
        System.out.println("Processing start");
        JSONObject startMsg = new JSONObject();
        startMsg.put(Key.action.toString(), Action.start.getID());
        if (initSucceeded) {
            startMsg.put(Key.status.name(), Status.Ok.getID());
        } else {
            startMsg.put(Key.status.name(), Status.Error.getID());
            exitSem.release();
        }
        senderCallback.send(startMsg);

        JSONObject enterStartRoomMsg = new JSONObject();
        enterStartRoomMsg.put(Key.action.name(), Action.debug.getID());
        enterStartRoomMsg.put(Key.pathNorth.name(), true);
        enterStartRoomMsg.put(Key.pathWest.name(), false);
        enterStartRoomMsg.put(Key.pathEast.name(), true);
        enterStartRoomMsg.put(Key.pathSouth.name(), false);
        enterStartRoomMsg.put(Key.hpPotionAvailable.name(), false);
        enterStartRoomMsg.put(Key.manaPotionAvailable.name(), false);
        senderCallback.send(enterStartRoomMsg);
    }
    private void processRequest(JSONObject msg)
    {
        System.out.println("Processing request");
        if (Action.getByID(msg.getInt(Key.action.toString())) == Action.start) {
            processStart();
        } else {
            System.out.println(msg.getInt(Key.action.name()));
            System.out.println(Action.start.getID());
        }
    }
}
