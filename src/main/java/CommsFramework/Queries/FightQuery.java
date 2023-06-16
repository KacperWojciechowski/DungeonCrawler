package CommsFramework.Queries;

import CommsFramework.Enums.Action;
import CommsFramework.Enums.Key;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

public class FightQuery {
    private final Action action = Action.fight;
    @Getter @Setter private boolean enemyAlive = true;
    @Getter @Setter private int enemyAttacked = 0;
    @Getter @Setter private boolean playerAttacked = false;
    @Getter @Setter private boolean playerUsedSkill = false;
    @Getter @Setter private boolean playerAlive = true;
    @Getter @Setter private boolean hpPotionAvailable = false;
    @Getter @Setter private boolean manaPotionAvailable = false;
    @Getter @Setter private boolean playerSkillAvailable = false;

    public FightQuery() {}

    public JSONObject serialize()
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Key.action.name(), action.getID());
        jsonObject.put(Key.enemyAlive.name(), enemyAlive);
        jsonObject.put(Key.enemyAttacked.name(), enemyAttacked);
        jsonObject.put(Key.playerAttacked.name(), playerAttacked);
        jsonObject.put(Key.playerUsedSkill.name(), playerUsedSkill);
        jsonObject.put(Key.playerAlive.name(), playerAlive);
        jsonObject.put(Key.hpPotionAvailable.name(), hpPotionAvailable);
        jsonObject.put(Key.manaPotionAvailable.name(), manaPotionAvailable);
        jsonObject.put(Key.playerSkillAvailable.name(), playerSkillAvailable);
        return jsonObject;
    }

    public static FightQuery deserialize(JSONObject jsonObject)
    {
        FightQuery query = new FightQuery();
        query.enemyAlive = jsonObject.getBoolean(Key.enemyAlive.name());
        query.enemyAttacked = jsonObject.getInt(Key.enemyAttacked.name());
        query.playerAttacked = jsonObject.getBoolean(Key.playerAttacked.name());
        query.playerUsedSkill = jsonObject.getBoolean(Key.playerUsedSkill.name());
        query.playerAlive = jsonObject.getBoolean(Key.playerAlive.name());
        query.hpPotionAvailable = jsonObject.getBoolean(Key.hpPotionAvailable.name());
        query.manaPotionAvailable = jsonObject.getBoolean(Key.manaPotionAvailable.name());
        query.playerSkillAvailable = jsonObject.getBoolean(Key.playerSkillAvailable.name());
        return query;
    }
}
