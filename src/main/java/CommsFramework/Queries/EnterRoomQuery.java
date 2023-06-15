package CommsFramework.Queries;

import CommsFramework.Enums.Action;
import CommsFramework.Enums.Key;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

public class EnterRoomQuery {
    private final Action action = Action.enterRoom;
    @Getter @Setter boolean northIsPresent = false;
    @Getter @Setter boolean westIsPresent = false;
    @Getter @Setter boolean southIsPresent = false;
    @Getter @Setter boolean eastIsPresent = false;
    @Getter @Setter boolean hpPotionAvailable = false;
    @Getter @Setter boolean manaPotionAvailable = false;
    @Getter @Setter boolean visited = true;
    public EnterRoomQuery() {}

    public JSONObject serialize()
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Key.action.name(), action.getID());
        jsonObject.put(Key.pathNorth.name(), northIsPresent);
        jsonObject.put(Key.pathSouth.name(), southIsPresent);
        jsonObject.put(Key.pathEast.name(), eastIsPresent);
        jsonObject.put(Key.pathWest.name(), westIsPresent);
        jsonObject.put(Key.hpPotionAvailable.name(), hpPotionAvailable);
        jsonObject.put(Key.manaPotionAvailable.name(), manaPotionAvailable);
        jsonObject.put(Key.visited.name(), visited);
        return jsonObject;
    }

    public static EnterRoomQuery deserialize(JSONObject jsonObject)
    {
        EnterRoomQuery query = new EnterRoomQuery();
        query.northIsPresent = jsonObject.getBoolean(Key.pathNorth.name());
        query.southIsPresent = jsonObject.getBoolean(Key.pathSouth.name());
        query.eastIsPresent = jsonObject.getBoolean(Key.pathEast.name());
        query.westIsPresent = jsonObject.getBoolean(Key.pathWest.name());
        query.hpPotionAvailable = jsonObject.getBoolean(Key.hpPotionAvailable.name());
        query.manaPotionAvailable = jsonObject.getBoolean(Key.manaPotionAvailable.name());
        query.visited = jsonObject.getBoolean(Key.visited.name());
        return query;
    }
}
