package Server;

import CommsFramework.Enums.Action;
import CommsFramework.Enums.Key;
import CommsFramework.Enums.Loot;
import CommsFramework.Enums.Status;
import CommsFramework.Interfaces.SenderCallback;
import CommsFramework.Queries.*;
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
            UndefinedQuery response = new UndefinedQuery();
            senderCallback.send(response.serialize());
            lastRespose = response.serialize();
            processRequest(lastValidRequest);
        } else {
            System.out.println("[Querry] Last valid querry not available");
            UndefinedQuery response = new UndefinedQuery();
            senderCallback.send(response.serialize());
            lastRespose = response.serialize();
            StartQuery dummyStartQuery = new StartQuery(initSucceeded ? Status.Ok : Status.Error);
            processStart(dummyStartQuery);
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

    private void processStart(StartQuery query)
    {
        System.out.println("[Querry] Processing start");
        JSONObject startMsg = new JSONObject();
        StartQuery startQuery = new StartQuery(initSucceeded ? Status.Ok : Status.Error);
        if (!initSucceeded)
        {
            exitSem.release(3);
        }
        senderCallback.send(startQuery.serialize());
        lastRespose = startMsg;
        lastValidRequest = query.serialize();
        enterRoom();
    }

    private void enterRoom()
    {
        EnterRoomQuery enterRoomQuery = new EnterRoomQuery();
        checkAvailableNeighbouringRooms(enterRoomQuery);

        enterRoomQuery.setHpPotionAvailable(player.getHpPotionsCount() > 0);
        enterRoomQuery.setManaPotionAvailable(player.getManaPotionsCount() > 0);
        enterRoomQuery.setVisited(visitedLocations.contains(player.getLocation()));

        senderCallback.send(enterRoomQuery.serialize());
        lastRespose = enterRoomQuery.serialize();
        visitedLocations.add(player.getLocation());
    }
    private void checkAvailableNeighbouringRooms(EnterRoomQuery query) {
        List<Integer> neighbouringRooms = map.getNeighboursOf(player.getLocation());

        for (Integer neighbour : neighbouringRooms)
        {
            if (neighbour == player.getLocation() - 1) query.setWestIsPresent(true);
            if (neighbour == player.getLocation() + 1) query.setEastIsPresent(true);
            if (neighbour < player.getLocation() - 1) {query.setNorthIsPresent(true); north = neighbour;}
            if (neighbour > player.getLocation() + 1) {query.setSouthIsPresent(true); south = neighbour;}
        }
    }

    private void processDisconnect()
    {
        System.out.println("[Querry] Processing disconnect\n-------------------\n");
        DisconnectQuery disconnectQuery = new DisconnectQuery(Status.Ok);
        senderCallback.send(disconnectQuery.serialize());
        lastRespose = disconnectQuery.serialize();
        exitSem.release(3);
    }
    private void processRequest(JSONObject msg)
    {
        System.out.println("[Querry] Processing request");

        lastValidRequest = msg;
        if (Action.start == Action.getFromJSON(msg)) {
            processStart(StartQuery.deserialize(msg));
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
        CheckStatsQuery checkStatsQuery = new CheckStatsQuery(player);
        senderCallback.send(checkStatsQuery.serialize());
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
        FoundEnemyQuery foundEnemyQuery = new FoundEnemyQuery(localEnemy.getName());
        senderCallback.send(foundEnemyQuery.serialize());
        lastRespose = foundEnemyQuery.serialize();
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
            FindChestQuery findChestQuery = new FindChestQuery(loot);
            senderCallback.send(findChestQuery.serialize());
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
