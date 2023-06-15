package Client;

import CommsFramework.Enums.Action;
import CommsFramework.Enums.Key;
import CommsFramework.Enums.Loot;
import CommsFramework.Enums.Status;
import CommsFramework.Interfaces.SenderCallback;
import CommsFramework.Queries.*;
import GameLogic.Player;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Game {
    private final Semaphore exitSem;
    SenderCallback senderCallback;
    private JSONObject lastValidRequest;

    Game(SenderCallback senderCallback, Semaphore exitSem)
    {
        this.senderCallback = senderCallback;
        this.exitSem = exitSem;
    }
    public void init() {
        System.out.println("Welcome to DungeonCrawler v 1.0");
        System.out.println("Press any key to continue");

        try {
            System.in.read();
        } catch (IOException ignored) {
        }
        System.out.println();

        StartQuery startQuery = new StartQuery(Status.Ok);

        senderCallback.send(startQuery.serialize());
    }

    private void processEnterRoom(EnterRoomQuery query)
    {
        if (query.isVisited())
            System.out.println("Looks like you already visited this room.");
        System.out.println("As you turn around, all you can see are pathways," +
                " leading off into different directions. What do you choose to do ?");
        List<Action> availableActions = new ArrayList<>();
        int counter = 1;
        printAvailableChoices(query, availableActions);

        lastValidRequest = query.serialize();
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        while(choice < 1 || choice > availableActions.size() + 1)
        {
            System.out.print("Your choice: ");
            choice = scanner.nextInt();
        }
        System.out.println();

        if (choice != availableActions.size() + 1)
        {
            doSelectedAction(availableActions.get(choice-1));
        }
        else {
            DisconnectQuery disconnectQuery = new DisconnectQuery(Status.Ok);
            senderCallback.send(disconnectQuery.serialize());
            exitSem.release(3);
        }
    }

    private void doSelectedAction(Action action) {
        switch (action)
        {
            case goNorth -> {
                GoNorthQuery goNorthQuery = new GoNorthQuery();
                senderCallback.send(goNorthQuery.serialize());
            }
            case goWest -> {
                GoWestQuery goWestQuery = new GoWestQuery();
                senderCallback.send(goWestQuery.serialize());
            }
            case goEast -> {
                GoEastQuery goEastQuery = new GoEastQuery();
                senderCallback.send(goEastQuery.serialize());
            }
            case goSouth -> {
                GoSouthQuery goSouthQuery = new GoSouthQuery();
                senderCallback.send(goSouthQuery.serialize());
            }
            case drinkHpPotion -> {
                // TODO: implement
            }
            case drinkManaPotion -> {
                // TODO: implement
            }
            case checkStats -> {
                Player dummyPlayer = new Player();
                CheckStatsQuery checkStatsQuery = new CheckStatsQuery(dummyPlayer);
                senderCallback.send(checkStatsQuery.serialize());
            }
        }
    }

    private void printAvailableChoices(EnterRoomQuery query, List<Action> availableActions) {
        int counter = 1;
        if (query.isNorthIsPresent()) {
            System.out.println(counter + ". Go North");
            availableActions.add(Action.goNorth);
            counter++;
        }
        if (query.isWestIsPresent()) {
            System.out.println(counter + ". Go West");
            availableActions.add(Action.goWest);
            counter++;
        }
        if (query.isEastIsPresent()) {
            System.out.println(counter + ". Go East");
            availableActions.add(Action.goEast);
            counter++;
        }
        if (query.isSouthIsPresent()) {
            System.out.println(counter + ". Go South");
            availableActions.add(Action.goSouth);
            counter++;
        }
        if (query.isHpPotionAvailable()) {
            System.out.println(counter + ". Drink HP potion");
            availableActions.add(Action.drinkHpPotion);
            counter++;
        }
        if (query.isManaPotionAvailable()) {
            System.out.println(counter + ". Drink Mana potion");
            availableActions.add(Action.drinkManaPotion);
            counter++;
        }
        System.out.println(counter + ". Check stats");
        availableActions.add(Action.checkStats);
        counter++;
        System.out.println(counter + ". Exit game");
    }

    void processUndefined()
    {
        System.out.println("[System] Querry recognition failure by the server. Last action will be re-done.");
    }

    void processStart(StartQuery query)
    {
        if (query.getStatus() == Status.Ok)
        {
            System.out.println("You wake up in an empty, cold room, with no recognition how you got here, neither where " +
                    "HERE is exactly. The only things you have is your trusty sword, and one magic spell you learned in" +
                    "your youth from a travelling wizard.");
            lastValidRequest = query.serialize();
        }
        else
        {
            System.out.println("[System] Server-side game setup failure. Please restart the game.");
        }
    }

    void processDebug(JSONObject msg)
    {
        System.out.println("[Debug]");
        System.out.println(msg);
        System.out.println();
        lastValidRequest = msg;
    }
    public void process(JSONObject msg)
    {
        if (Action.debug == Action.getFromJSON(msg)) {
            processDebug(msg);
        } else if (Action.start == Action.getFromJSON(msg)) {
            processStart(StartQuery.deserialize(msg));
        } else if (Action.enterRoom == Action.getFromJSON(msg)) {
            processEnterRoom(EnterRoomQuery.deserialize(msg));
        } else if (Action.undefined == Action.getFromJSON(msg)) {
            processUndefined();
        } else if (Action.findEnemy == Action.getFromJSON(msg)) {
            processFindEnemy(FoundEnemyQuery.deserialize(msg));
        } else if (Action.fight == Action.getFromJSON(msg)) {
            // processFight(msg);
        } else if (Action.checkStats == Action.getFromJSON(msg)) {
            processCheckStats(CheckStatsQuery.deserialize(msg));
        } else if (Action.findChest == Action.getFromJSON(msg)) {
            processFindChest(FindChestQuery.deserialize(msg));
        }
    }

    private void processFindEnemy(FoundEnemyQuery query) {

        String enemyName = query.getEnemyName();
        System.out.println("As you enter the room, you notice " + enemyName + " standing in the middle. Looks like he " +
                "didn't notice you yet. What do you want to do ?");
        System.out.println("1. Fight");
        System.out.println("2. Flee to the previous room\n");
        lastValidRequest = query.serialize();
        int choice = 0;
        Scanner scanner = new Scanner(System.in);
        while (choice < 1 || choice > 2)
        {
            System.out.println("Your choice: ");
            choice = scanner.nextInt();
        }
        System.out.println();

        switch(choice)
        {
            case 1 -> fight();
            case 2 -> flee();
        }
    }

    private void fight() {
        // TODO: implement
        System.out.println("Not yet implemented");
    }

    private void flee() {
        FleeQuery fleeQuery = new FleeQuery();
        senderCallback.send(fleeQuery.serialize());
        System.out.println("You decided to flee. As you nervously look behind you, gladly the creature is not following you");
    }

    private void processFindChest(FindChestQuery query) {
        System.out.print("In one of the corners of the room, you find a rusted chest. As you open it, you find ");
        if (Loot.damageBoost == query.getLoot()) {
            System.out.println("a shiny sword, in somehow better shape than your current one. Your damage increases.");
        } else if (Loot.vitalityBoost == query.getLoot()) {
            System.out.println("an old armor. Despite some scratches and hints of rust, it's in somehow better shape " +
                    "than your current one. Your vitality increases");
        } else if (Loot.intelligenceBoost == query.getLoot()) {
            System.out.println("a dusty old scroll. As you read its content, you gain a better understanding of the magic" +
                    "you're using. Your intelligence increases.");
        } else if (Loot.hpPotion == query.getLoot()) {
            System.out.println("an hp potion. You pack it in your bag to use it later.");
        } else if (Loot.manaPotion == query.getLoot()) {
            System.out.println("a mana potion. You think this might come in handy, as you put it in your bag.");
        }
    }
    private void processCheckStats(CheckStatsQuery query) {
        System.out.println("Here are your current stats:");
        System.out.println("HP: " + query.getHp() + " / " + query.getHpLimit());
        System.out.println("Mana: " + query.getMana() + " / " + query.getManaLimit());
        System.out.println("Damage: " + query.getDamage());
        System.out.println("Skill:");
        System.out.println("-Damage: " + query.getSkillDamage());
        System.out.println("-Cost: " + query.getSkillCost() + " mana");
        System.out.println("Vitality: " + query.getVitality());
        System.out.println("Intelligence: " + query.getIntelligence());
        System.out.println("Hp potions in bag: " + query.getHpPotionsCount());
        System.out.println("Mana potions in bag: " + query.getManaPotionsCount());
        System.out.println("\n");
    }

    public void update(JSONObject msg) {
        try {
            if (Action.getByID(msg.getInt(Key.action.toString())) != Action.undefined) {
                process(msg);
            } else {
                tryToRecoverFromWrongQuery();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("[Querry] Malformed querry");
            tryToRecoverFromWrongQuery();
        }
    }

    private void tryToRecoverFromWrongQuery() {
        System.out.println("[Querry] Trying to recover from wrong querry");
        if (lastValidRequest != null) {
            System.out.println("[Querry] Last valid querry available");
            UndefinedQuery undefinedQuery = new UndefinedQuery();
            senderCallback.send(undefinedQuery.serialize());
            process(lastValidRequest);
        } else {
            System.out.println("[Querry] Fatal error occured with no recovery option. Please restart the game");
            DisconnectQuery disconnectQuery = new DisconnectQuery(Status.Ok);
            senderCallback.send(disconnectQuery.serialize());
            exitSem.release(3);
        }
    }
}
