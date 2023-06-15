package Server;

import CommsFramework.Enums.Action;
import CommsFramework.Enums.Key;
import CommsFramework.Enums.Loot;
import CommsFramework.Enums.Status;
import CommsFramework.Interfaces.SenderCallback;
import CommsFramework.Queries.StartQuery;
import GameLogic.Enemies.*;
import GameLogic.Map.MapGraph;
import GameLogic.Player;
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
    private int north = -1;
    private int south = -1;
    private final List<Integer> visitedLocations = new ArrayList<>();
    private Player player;
    private JSONObject lastRespose;
    private final List<EnemyInLocation> enemiesInLocations = new ArrayList<>();
    private Enemy enemy;

    public Game(SenderCallback senderCallback, Semaphore exitSem)
    {
        this.senderCallback = senderCallback;
        this.exitSem = exitSem;
    }

    public void init()
    {
        map = new MapGraph();
        Random generate = new Random(1234);
        player = new Player();
        player.setLocation(generate.nextInt(map.getRoomsCount()));
        visitedLocations.add(player.getLocation());

        initSucceeded = true;
    }

    private void tryToRecoverFromWrongQuery() {
        System.out.println("[Querry] Trying to recover from wrong querry");
        if (lastValidRequest != null) {
            System.out.println("[Querry] Last valid querry available");
            JSONObject response = new JSONObject();
            response.put(Key.action.name(), Action.undefined.getID());
            senderCallback.send(response);
            lastRespose = response;
            processRequest(lastValidRequest);
        } else {
            System.out.println("[Querry] Last valid querry not available");
            JSONObject response = new JSONObject();
            response.put(Key.action.name(), Action.undefined.getID());
            senderCallback.send(response);
            lastRespose = response;
            JSONObject dummyStartMsg = new JSONObject();
            dummyStartMsg.put(Key.action.name(), Action.start);
            dummyStartMsg.put(Key.status.name(), initSucceeded ? Status.Ok : Status.Error);
            processStart(dummyStartMsg);
        }
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

    private void processStart(JSONObject msg)
    {
        System.out.println("[Querry] Processing start");
        JSONObject startMsg = new JSONObject();
        StartQuery startQuery = new StartQuery(initSucceeded ? Status.Ok : Status.Error);
        //startMsg.put(Key.action.toString(), Action.start.getID());
        //if (initSucceeded) {
        //    startMsg.put(Key.status.name(), Status.Ok.getID());
        //} else {
        //    startMsg.put(Key.status.name(), Status.Error.getID());
        //    exitSem.release();
        //}
        if (!initSucceeded)
        {
            exitSem.release(3);
        }
        senderCallback.send(startQuery.serialize());
        lastRespose = startMsg;
        lastValidRequest = msg;
        enterRoom();
    }

    private void enterRoom()
    {
        JSONObject enterRoomMsg = new JSONObject();
        enterRoomMsg.put(Key.action.name(), Action.enterRoom.getID());

        checkAvailableNeighbouringRooms(enterRoomMsg);

        // TODO: implement potions
        enterRoomMsg.put(Key.hpPotionAvailable.name(), false);
        enterRoomMsg.put(Key.manaPotionAvailable.name(), false);
        enterRoomMsg.put(Key.visited.name(), visitedLocations.contains(player.getLocation()));
        senderCallback.send(enterRoomMsg);
        lastRespose = enterRoomMsg;
        visitedLocations.add(player.getLocation());
    }
    private void checkAvailableNeighbouringRooms(JSONObject msg) {
        List<Integer> neighbouringRooms = map.getNeighboursOf(player.getLocation());
        boolean northIsPresent = false;
        boolean westIsPresent = false;
        boolean eastIsPresent = false;
        boolean southIsPresent = false;

        for (Integer neighbour : neighbouringRooms)
        {
            if (neighbour == player.getLocation() - 1) westIsPresent = true;
            if (neighbour == player.getLocation() + 1) eastIsPresent = true;
            if (neighbour < player.getLocation() - 1) {northIsPresent = true; north = neighbour;}
            if (neighbour > player.getLocation() + 1) {southIsPresent = true; south = neighbour;}
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
        lastRespose = msg;
        exitSem.release(3);
    }
    private void processRequest(JSONObject msg)
    {
        System.out.println("[Querry] Processing request");

        if (Action.start == Action.getFromJSON(msg)) {
            processStart(msg);
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
        } else if (Action.checkStats == Action.getFromJSON(msg)) {
            processCheckStats();
        } else if (Action.flee == Action.getFromJSON(msg)) {
            processFlee();
        }
    }

    private void processFlee() {
        enemy = null;
        player.setLocation(player.getPreviousLocation());
        resetSavedNeighbours();
        enterRoom();
    }

    private void processCheckStats() {
        System.out.println("[Querry] Processing check stats");
        JSONObject msg = new JSONObject();
        msg.put(Key.action.name(), Action.checkStats.getID());
        msg.put(Key.playerMana.name(), player.getMana());
        msg.put(Key.playerHp.name(), player.getHp());
        msg.put(Key.playerVitality.name(), player.getVitality());
        msg.put(Key.playerDamage.name(), player.getDamage());
        msg.put(Key.playerIntelligence.name(), player.getIntelligence());
        msg.put(Key.playerSkillCost.name(), player.getSkillCost());
        msg.put(Key.playerSkillDamage.name(), player.getSkillDamage());
        msg.put(Key.playerManaPotionsCount.name(), player.getManaPotionsCount());
        msg.put(Key.playerHpPotionsCount.name(), player.getHpPotionsCount());
        msg.put(Key.playerManaLimit.name(), player.getMana_limit());
        msg.put(Key.playerHpLimit.name(), player.getHp_limit());
        senderCallback.send(msg);
        // allow player to select another choice from the previous action
        senderCallback.send(lastRespose);
    }

    private void processGoSouth() {
        System.out.println("[Querry] Processing go South");
        player.setLocation(south);
        resetSavedNeighbours();

        if (!visitedLocations.contains(player.getLocation()))
        {
            if (randomizeEnemy()) return;
            randomizeLoot();
        }
        enterRoom();
    }

    private boolean randomizeEnemy() {
        Enemy localEnemy = getEnemyFoundInThisLocation();
        if (localEnemy != null)
        {
            this.enemy = localEnemy;
            processFoundEnemy(localEnemy);
            return true;
        }
        else
        {
            Random generate = new Random();
            // randomize whether an enemy is present
            if (generate.nextInt(3) == 0)
            {
                int enemyChoice = generate.nextInt(3);
                switch(enemyChoice)
                {
                    case 0 -> localEnemy = new Rat();
                    case 1 -> localEnemy = new Kobold();
                    case 2 -> localEnemy = new TunelTroll();
                }
                enemiesInLocations.add(new EnemyInLocation(localEnemy, player.getLocation()));
                this.enemy = localEnemy;
                processFoundEnemy(localEnemy);
                return true;
            } else {
                return false;
            }
        }
    }

    private void processFoundEnemy(Enemy localEnemy) {
        JSONObject msg = new JSONObject();
        msg.put(Key.action.name(), Action.findEnemy.getID());
        msg.put(Key.enemyName.name(), localEnemy.getName());
        msg.put(Key.enemyAlive.name(), true);
        senderCallback.send(msg);
    }

    private Enemy getEnemyFoundInThisLocation() {
        Enemy ret = null;
        for (EnemyInLocation eil : enemiesInLocations) {
            if (eil.getRoom() == player.getLocation()) {
                ret = eil.getEnemy();
                break;
            }
        }
        return ret;
    }

    private void processGoEast() {
        System.out.println("[Querry] Processing go East");
        player.setLocation(player.getLocation() + 1);
        resetSavedNeighbours();

        if (!visitedLocations.contains(player.getLocation()))
        {
            if (randomizeEnemy()) return;
            randomizeLoot();
        }
        enterRoom();
    }

    private void randomizeLoot() {
        Random generate = new Random();
        // generate chest with 33% probability
        if (generate.nextInt(6) == 0) {
            Loot loot = Loot.getByID(generate.nextInt(5) + 1);
            upgradePlayerAccordingToLoot(loot);

            JSONObject msg = new JSONObject();
            msg.put(Key.action.name(), Action.findChest.getID());
            msg.put(Key.loot.name(), loot.getID());
            senderCallback.send(msg);
        }
    }

    private void upgradePlayerAccordingToLoot(Loot loot) {
        switch(loot)
        {
            case damageBoost -> player.upgradeDamage();
            case vitalityBoost -> player.upgradeVitality();
            case intelligenceBoost -> player.upgradeIntelligence();
            case hpPotion -> player.findHpPotion();
            case manaPotion -> player.findManaPotion();
        }
    }

    private void processGoWest() {
        System.out.println("[Querry] Processing go West");
        player.setLocation(player.getLocation() - 1);
        resetSavedNeighbours();

        if (!visitedLocations.contains(player.getLocation()))
        {
            if (randomizeEnemy()) return;
            randomizeLoot();
        }
        enterRoom();
    }

    private void processGoNorth() {
        System.out.println("[Querry] Processing go North");
        player.setLocation(north);
        resetSavedNeighbours();

        if (!visitedLocations.contains(player.getLocation()))
        {
            if (randomizeEnemy()) return;
            randomizeLoot();
        }
        enterRoom();
    }

    private void resetSavedNeighbours() {
        north = -1;
        south = -1;
    }
}
