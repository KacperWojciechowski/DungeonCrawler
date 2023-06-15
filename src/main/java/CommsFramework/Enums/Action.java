package CommsFramework.Enums;

import org.json.JSONObject;

public enum Action {
    undefined(0), start(1), goNorth(2), goWest(3), goEast(4), goSouth(5), attack(6),
    useSkill(7), checkStats(8), drinkHpPotion(9), drinkManaPotion(10), flee(11), enterRoom(12),
    findEnemy(13), debug(14), fight(15), disconnect(16), findChest(17);

    private final int ID;
    Action(int ID)
    {
        this.ID = ID;
    }

    public int getID()
    {
        return ID;
    }

    public static Action getByID(int ID)
    {
        Action action;
        switch (ID) {
            case 1 -> action = start;
            case 2 -> action = goNorth;
            case 3 -> action = goWest;
            case 4 -> action = goEast;
            case 5 -> action = goSouth;
            case 6 -> action = attack;
            case 7 -> action = useSkill;
            case 8 -> action = checkStats;
            case 9 -> action = drinkHpPotion;
            case 10 -> action = drinkManaPotion;
            case 11 -> action = flee;
            case 12 -> action = enterRoom;
            case 13 -> action = findEnemy;
            case 14 -> action = debug;
            case 15 -> action = fight;
            case 16 -> action = disconnect;
            case 17 -> action = findChest;
            default -> action = undefined;
        }
        return action;
    }

    public static Action getFromJSON(JSONObject msg)
    {
        return Action.getByID(msg.getInt(Key.action.toString()));
    }
}
