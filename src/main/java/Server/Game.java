package Server;

import CommsFramework.Action;
import CommsFramework.Key;
import CommsFramework.SenderCallback;
import CommsFramework.Status;
import GameLogic.MapGraph;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Game {
    private final SenderCallback senderCallback;
    JSONObject lastValidRequest = null;
    boolean initSucceeded = false;
    private final Semaphore exitSem;
    private MapGraph map;
    private int playerLocation;
    private int north = -1;
    private int south = -1;
    private List<Integer> visitedLocations = new ArrayList<>();
    public Game(SenderCallback senderCallback, Semaphore exitSem)
    {
        this.senderCallback = senderCallback;
        this.exitSem = exitSem;
    }

    public void init()
    {
        map = new MapGraph();
        Random generate = new Random(1234);
        playerLocation = generate.nextInt(map.getRoomsCount());

        initSucceeded = true;
        // create map
    }

    private void tryToRecoverFromWrongQuery() {
        System.out.println("[Querry] Trying to recover from wrong querry");
        if (lastValidRequest != null) {
            System.out.println("[Querry] Last valid querry available");
            JSONObject response = new JSONObject();
            response.put(Key.action.name(), Action.undefined.getID());
            senderCallback.send(response);
            processRequest(lastValidRequest);
        } else {
            System.out.println("[Querry] Last valid querry not available");
            JSONObject response = new JSONObject();
            response.put(Key.action.name(), Action.undefined.getID());
            senderCallback.send(response);
            processStart();
        }
    }

    private boolean exitMessageArrived(JSONObject msg)
    {
        return msg.isEmpty();
    }
    public void update(JSONObject msg)
    {
        try {
            System.out.println("[Querry] JSON String: " + msg);
            if (Action.getByID(msg.getInt(Key.action.toString())) != Action.undefined) {
                processRequest(msg);
            } else {
                tryToRecoverFromWrongQuery();
            }
        } catch (JSONException e) {
            System.out.println("[Querry] Malformed querry");
            tryToRecoverFromWrongQuery();
        }
    }

    private void processStart()
    {
        System.out.println("[Querry] Processing start");
        JSONObject startMsg = new JSONObject();
        startMsg.put(Key.action.toString(), Action.start.getID());
        if (initSucceeded) {
            startMsg.put(Key.status.name(), Status.Ok.getID());
        } else {
            startMsg.put(Key.status.name(), Status.Error.getID());
            exitSem.release();
        }
        senderCallback.send(startMsg);
        enterRoom();
    }

    private void enterRoom()
    {
        JSONObject enterStartRoomMsg = new JSONObject();
        enterStartRoomMsg.put(Key.action.name(), Action.enterRoom.getID());

        checkAvailableNeighbouringRooms(enterStartRoomMsg);

        // TODO: implement potions
        enterStartRoomMsg.put(Key.hpPotionAvailable.name(), false);
        enterStartRoomMsg.put(Key.manaPotionAvailable.name(), false);
        senderCallback.send(enterStartRoomMsg);
    }
    private void checkAvailableNeighbouringRooms(JSONObject msg) {
        List<Integer> neighbouringRooms = map.getNeighboursOf(playerLocation);
        boolean northIsPresent = false;
        boolean westIsPresent = false;
        boolean eastIsPresent = false;
        boolean southIsPresent = false;

        for (Integer neighbour : neighbouringRooms)
        {
            if (neighbour == playerLocation - 1) westIsPresent = true;
            if (neighbour == playerLocation + 1) eastIsPresent = true;
            if (neighbour < playerLocation - 1) {northIsPresent = true; north = neighbour;}
            if (neighbour > playerLocation + 1) {southIsPresent = true; south = neighbour;}
        }

        msg.put(Key.pathNorth.name(), northIsPresent);
        msg.put(Key.pathWest.name(), westIsPresent);
        msg.put(Key.pathEast.name(), eastIsPresent);
        msg.put(Key.pathSouth.name(), southIsPresent);
    }

    private void processDisconnect()
    {
        System.out.println("[Querry] Processing disconnect\n-------------------\n");
        JSONObject msg = new JSONObject();
        msg.put(Key.action.name(), Action.disconnect.getID());
        msg.put(Key.status.name(), Status.Ok.getID());
        senderCallback.send(msg);
        exitSem.release(3);
    }
    private void processRequest(JSONObject msg)
    {
        System.out.println("[Querry] Processing request");

        if (Action.start == Action.getFromJSON(msg)) {
            processStart();
        } else if (Action.disconnect == Action.getFromJSON(msg)) {
            processDisconnect();
        } else if (Action.goNorth == Action.getFromJSON(msg)) {
            processGoNorth();
        } else if (Action.goWest == Action.getFromJSON(msg)) {
            processGoWest();
        } else if (Action.goEast == Action.getFromJSON(msg)) {
            processGoEast();
        } else if (Action.goSouth == Action.getFromJSON(msg)) {
            processGoSouth();
        }
    }

    private void processGoSouth() {
        playerLocation = south;
        resetSavedNeighbours();

        // TODO: Randomize enemy
        // TODO: Randomize chest
        enterRoom();
    }

    private void processGoEast() {
        playerLocation = playerLocation+1;
        resetSavedNeighbours();

        // TODO: Randomize enemy
        // TODO: Randomize chest
        enterRoom();
    }

    private void processGoWest() {
        playerLocation = playerLocation-1;
        resetSavedNeighbours();

        // TODO: Randomize enemy
        // TODO: Randomize chest
        enterRoom();
    }

    private void processGoNorth() {
        playerLocation = north;
        resetSavedNeighbours();

        // TODO: Randomize enemy
        // TODO: Randomize chest
        enterRoom();
    }

    private void resetSavedNeighbours() {
        north = -1;
        south = -1;
    }
}
