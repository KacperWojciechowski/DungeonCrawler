package Client;

import CommsFramework.Action;
import CommsFramework.Key;
import CommsFramework.SenderCallback;
import CommsFramework.Status;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Game {
    private final Semaphore exitSem;
    SenderCallback senderCallback;
    Game(SenderCallback senderCallback, Semaphore exitSem)
    {
        this.senderCallback = senderCallback;
        this.exitSem = exitSem;
    }
    public void init() {
        System.out.println("Welcome to DungeonCrawler v 1.0");
        System.out.println("Press any key to continue");
        //Scanner scanner = new Scanner(System.in);

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
            System.out.print("Your choice");
            choice = scanner.nextInt();
        }
        System.out.println();

        JSONObject response = new JSONObject();
        if (choice != availableActions.size() + 1)
        {
            response.put(Key.action.toString(), availableActions.get(choice-1).getID());
        }
        else {
            exitSem.release(2);
        }
        senderCallback.send(response);
    }

    void processUndefined(JSONObject msg)
    {
        if (Status.getByID(msg.getInt(Key.retransmit.name())) == Status.Ok)
        {
            System.out.println("[System] Querry recognition failure by the server. Last action will be re-done.");
        } else {
            System.out.println("[System] Fatal communication error with the server. Please restart the game.");
        }
    }

    void processStart(JSONObject msg)
    {
        if (Status.getByID(msg.getInt(Key.status.toString())) == Status.Ok)
        {
            System.out.println("You wake up in an empty, cold room, with no recognition how you got here, neither where" +
                    "HERE is exactly. The only things you have is your trusty sword, and one magic spell you learned in" +
                    "your youth from a travelling wizard.");
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
        int action = msg.getInt(Key.action.toString());

        if (Action.debug == Action.getByID(action)) {
            processDebug(msg);
        } else if (Action.start == Action.getByID(action)) {
            processStart(msg);
        } else if (Action.enterRoom == Action.getByID(action)) {
            processEnterRoom(msg);
        } else if (Action.undefined == Action.getByID(action)) {
            processUndefined(msg);
        } else if (Action.findEnemy == Action.getByID(action)) {
            // processFindEnemy();
        } else if (Action.fight == Action.getByID(action)) {
            // processEnemyAttack();
        }
    }
}
