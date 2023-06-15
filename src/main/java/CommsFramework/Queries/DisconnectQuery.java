package CommsFramework.Queries;

import CommsFramework.Enums.Action;
import CommsFramework.Enums.Key;
import CommsFramework.Enums.Status;
import lombok.Getter;
import org.json.JSONObject;

public class DisconnectQuery {
    private final Action action = Action.disconnect;
    @Getter private Status status;

    public DisconnectQuery(Status status) {this.status = status; }

    private DisconnectQuery() {}
    public JSONObject serialize()
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Key.action.name(), action.getID());
        return jsonObject;
    }

    public static DisconnectQuery deserialize()
    {
        return new DisconnectQuery();
    }
}
