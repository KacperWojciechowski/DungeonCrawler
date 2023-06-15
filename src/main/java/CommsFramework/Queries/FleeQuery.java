package CommsFramework.Queries;

import CommsFramework.Enums.Action;
import CommsFramework.Enums.Key;
import org.json.JSONObject;

public class FleeQuery {
    private final Action action = Action.flee;

    public FleeQuery() {};

    public JSONObject serialize()
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Key.action.name(), action.getID());
        return jsonObject;
    }

    public static FleeQuery deserialize()
    {
        return new FleeQuery();
    }
}
