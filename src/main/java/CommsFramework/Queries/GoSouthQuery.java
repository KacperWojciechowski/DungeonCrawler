package CommsFramework.Queries;

import CommsFramework.Enums.Action;
import CommsFramework.Enums.Key;
import org.json.JSONObject;

public class GoSouthQuery {
    private final Action action = Action.goSouth;

    public GoSouthQuery() {}

    public JSONObject serialize()
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Key.action.name(), action.getID());
        return jsonObject;
    }

    public static GoSouthQuery deserialize()
    {
        return new GoSouthQuery();
    }
}
