package CommsFramework.Queries;

import CommsFramework.Enums.Action;
import CommsFramework.Enums.Key;
import org.json.JSONObject;

public class GoNorthQuery {
    private final Action action = Action.goNorth;

    public GoNorthQuery() {}

    public JSONObject serialize()
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Key.action.name(), action.getID());
        return jsonObject;
    }

    public static GoNorthQuery deserialize(JSONObject msg)
    {
        return new GoNorthQuery();
    }
}
