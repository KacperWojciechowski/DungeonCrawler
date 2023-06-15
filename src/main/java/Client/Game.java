package Client;

import CommsFramework.*;
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Key.action.name(), Action.start.getID());
        senderCallback.send(jsonObject);
    }

    private void processEnterRoom(JSONObject msg)
    {
        if (msg.getBoolean(Key.visited.name()))
            System.out.println("Looks like you already visited this room.");
        System.out.println("As you turn around, all you can see are pathways," +
                " leading off into different directions. What do you choose to do ?");
        List<Action> availableActions = new ArrayList<>();
        int counter = 1;
        if (msg.getBoolean(Key.pathNorth.toString())) {
            System.out.println(counter + ". Go North");
            availableActions.add(Action.goNorth);
            counter++;
        }
        if (msg.getBoolean(Key.pathWest.toString())) {
            System.out.println(counter + ". Go West");
            availableActions.add(Action.goWest);
            counter++;
        }
        if (msg.getBoolean(Key.pathEast.toString())) {
            System.out.println(counter + ". Go East");
            availableActions.add(Action.goEast);
            counter++;
        }
        if (msg.getBoolean(Key.pathSouth.toString())) {
            System.out.println(counter + ". Go South");
            availableActions.add(Action.goSouth);
            counter++;
        }
        if (msg.getBoolean(Key.hpPotionAvailable.toString())) {
            System.out.println(counter + ". Drink HP potion");
            availableActions.add(Action.drinkHpPotion);
            counter++;
        }
        if (msg.getBoolean(Key.manaPotionAvailable.toString())) {
            System.out.println(counter + ". Drink Mana potion");
            availableActions.add(Action.drinkManaPotion);
            counter++;
        }
        System.out.println(counter + ". Check stats");
        availableActions.add(Action.checkStats);
        counter++;
        System.out.println(counter + ". Exit game");

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
            JSONObject response = new JSONObject();
            response.put(Key.action.toString(), availableActions.get(choice-1).getID());
            senderCallback.send(response);
        }
        else {
            JSONObject response = new JSONObject();
            response.put(Key.action.name(), Action.disconnect.getID());
            senderCallback.send(response);
            exitSem.release(3);
        }
    }

    void processUndefined()
    {
        System.out.println("[System] Querry recognition failure by the server. Last action will be re-done.");
    }

    void processStart(JSONObject msg)
    {
        if (Status.getByID(msg.getInt(Key.status.toString())) == Status.Ok)
        {
            System.out.println("You wake up in an empty, cold room, with no recognition how you got here, neither where" +
                    "HERE is exactly. The only things you have is your trusty sword, and one magic spell you learned in" +
                    "your youth from a travelling wizard.");
            lastValidRequest = msg;
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
    }
    public void process(JSONObject msg)
    {
        if (Action.debug == Action.getFromJSON(msg)) {
            processDebug(msg);
        } else if (Action.start == Action.getFromJSON(msg)) {
            processStart(msg);
        } else if (Action.enterRoom == Action.getFromJSON(msg)) {
            processEnterRoom(msg);
        } else if (Action.undefined == Action.getFromJSON(msg)) {
            processUndefined();
        } else if (Action.findEnemy == Action.getFromJSON(msg)) {
            // processFindEnemy(msg);
        } else if (Action.fight == Action.getFromJSON(msg)) {
            // processFight(msg);
        } else if (Action.checkStats == Action.getFromJSON(msg)) {
            processCheckStats(msg);
        } else if (Action.findChest == Action.getFromJSON(msg)) {
            processFindChest(msg);
        }
    }
    private void processFindChest(JSONObject msg) {
        System.out.print("In one of the corners of the room, you find a rusted chest. As you open it, you find ");
        if (Loot.damageBoost == Loot.getFromJSON(msg)) {
            System.out.println("a shiny sword, in somehow better shape than your current one. Your damage increases.");
        } else if (Loot.vitalityBoost == Loot.getFromJSON(msg)) {
            System.out.println("an old armor. Despite some scratches and hints of rust, it's in somehow better shape " +
                    "than your current one. Your vitality increases");
        } else if (Loot.intelligenceBoost == Loot.getFromJSON(msg)) {
            System.out.println("a dusty old scroll. As you read its content, you gain a better understanding of the magic" +
                    "you're using. Your intelligence increases.");
        } else if (Loot.hpPotion == Loot.getFromJSON(msg)) {
            System.out.println("an hp potion. You pack it in your bag to use it later.");
        } else if (Loot.manaPotion == Loot.getFromJSON(msg)) {
            System.out.println("a mana potion. You think this might come in handy, as you put it in your bag.");
        }
    }
    private void processCheckStats(JSONObject msg) {
        System.out.println("Here are your current stats:");
        System.out.println("HP: " + msg.getInt(Key.playerHp.name()) + " / " + msg.getInt(Key.playerHpLimit.name()));
        System.out.println("Mana: " + msg.getInt(Key.playerMana.name()) + " / " + msg.getInt(Key.playerManaLimit.name()));
        System.out.println("Damage: " + msg.getInt(Key.playerDamage.name()));
        System.out.println("Skill:");
        System.out.println("-Damage: " + msg.getInt(Key.playerSkillDamage.name()));
        System.out.println("-Cost: " + msg.getInt(Key.playerSkillCost.name()) + " mana");
        System.out.println("Vitality: " + msg.getInt(Key.playerVitality.name()));
        System.out.println("Intelligence: " + msg.getInt(Key.playerIntelligence.name()));
        System.out.println("Hp potions in bag: " + msg.getInt(Key.playerHpPotionsCount.name()));
        System.out.println("Mana potions in bag: " + msg.getInt(Key.playerManaPotionsCount.name()));
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
            System.out.println("[Querry] Malformed querry");
            tryToRecoverFromWrongQuery();
        }
    }

    private void tryToRecoverFromWrongQuery() {
        System.out.println("[Querry] Trying to recover from wrong querry");
        if (lastValidRequest != null) {
            System.out.println("[Querry] Last valid querry available");
            JSONObject response = new JSONObject();
            response.put(Key.action.name(), Action.undefined.getID());
            senderCallback.send(response);
            process(lastValidRequest);
        } else {
            System.out.println("[Querry] Fatal error occured with no recovery option. Please restart the game");
            JSONObject response = new JSONObject();
            response.put(Key.action.name(), Action.disconnect.getID());
            senderCallback.send(response);
            exitSem.release(3);
        }
    }
}
