package CommsFramework.Queries;

import CommsFramework.Enums.Action;
import CommsFramework.Enums.Key;
import CommsFramework.Enums.Status;
import org.json.JSONObject;

public class StartQuery {
    private final Action action = Action.start;
    private Status status;

    public StartQuery(Status status)
    {
        this.status = status;
    }

    private StartQuery() {}

    public JSONObject serialize()
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Key.action.name(), Action.start.getID());
        jsonObject.put(Key.status.name(), status.getID());
        return jsonObject;
    }

    public static StartQuery deserialize(JSONObject msg)
    {
        StartQuery query = new StartQuery();
        query.status = Status.getByID(msg.getInt(Key.status.name()));
        return query;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
