package CommsFramework.Queries;

import CommsFramework.Enums.Action;
import CommsFramework.Enums.Key;
import org.json.JSONObject;

public class GoEastQuery {
    private final Action action = Action.goEast;

    public GoEastQuery() {}

    public JSONObject serialize()
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Key.action.name(), action.getID());
        return jsonObject;
    }

    public static GoEastQuery deserialize()
    {
        return new GoEastQuery();
    }
}
