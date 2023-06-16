package CommsFramework.Queries;

import CommsFramework.Enums.Action;
import CommsFramework.Enums.Key;
import org.json.JSONObject;

public class UseManaPotionQuery {
    private final Action action = Action.drinkManaPotion;

    public UseManaPotionQuery() {}

    public JSONObject serialize()
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Key.action.name(), action.getID());
        return jsonObject;
    }

    public static UseManaPotionQuery deserialize(JSONObject jsonObject)
    {
        return new UseManaPotionQuery();
    }
}
