package CommsFramework.Queries;

import CommsFramework.Enums.Action;
import CommsFramework.Enums.Key;
import CommsFramework.Enums.Status;
import lombok.Getter;
import org.json.JSONObject;

public class StartQuery {
    private final Action action = Action.start;
    @Getter private Status status;

    public StartQuery(Status status)
    {
        this.status = status;
    }

    private StartQuery() {}

    public JSONObject serialize()
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Key.action.name(), action.getID());
        jsonObject.put(Key.status.name(), status.getID());
        return jsonObject;
    }

    public static StartQuery deserialize(JSONObject msg)
    {
        StartQuery query = new StartQuery();
        query.status = Status.getByID(msg.getInt(Key.status.name()));
        return query;
    }
}
