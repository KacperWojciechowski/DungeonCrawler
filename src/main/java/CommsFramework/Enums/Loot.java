package CommsFramework.Enums;

import org.json.JSONObject;

public enum Loot {
    undefined(0), damageBoost(1), vitalityBoost(2), intelligenceBoost(3), hpPotion(4), manaPotion(5);

    private final int ID;
    Loot(int ID)
    {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public static Loot getByID(int ID)
    {
        Loot loot = undefined;
        switch(ID)
        {
            case 1 -> loot = damageBoost;
            case 2 -> loot = vitalityBoost;
            case 3 -> loot = intelligenceBoost;
            case 4 -> loot = hpPotion;
            case 5 -> loot = manaPotion;
        }
        return loot;
    }
}
