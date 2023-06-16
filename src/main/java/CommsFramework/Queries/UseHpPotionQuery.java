package CommsFramework.Queries;

import CommsFramework.Enums.Action;
import CommsFramework.Enums.Key;
import org.json.JSONObject;

public class UseHpPotionQuery {
    private final Action action = Action.drinkHpPotion;

    public UseHpPotionQuery() {}

    public JSONObject serialize()
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Key.action.name(), action.getID());
        return jsonObject;
    }

    public static UseHpPotionQuery deserialize(JSONObject jsonObject)
    {
        return new UseHpPotionQuery();
    }
}
