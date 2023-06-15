package CommsFramework.Queries;

import CommsFramework.Enums.Action;
import CommsFramework.Enums.Key;
import org.json.JSONObject;

public class UndefinedQuery {
    private final Action action = Action.undefined;

    public UndefinedQuery() {}

    public JSONObject serialize()
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Key.action.name(), action.getID());
        return jsonObject;
    }

    public static UndefinedQuery deserialize()
    {
        return new UndefinedQuery();
    }
}
