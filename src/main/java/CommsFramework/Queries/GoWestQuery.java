package CommsFramework.Queries;

import CommsFramework.Enums.Action;
import CommsFramework.Enums.Key;
import org.json.JSONObject;

public class GoWestQuery {
    private final Action action = Action.goWest;

    public GoWestQuery() {}
    public JSONObject serialize()
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Key.action.name(), action.getID());
        return jsonObject;
    }

    public static GoWestQuery deserialize(JSONObject msg)
    {
        return new GoWestQuery();
    }
}
