package CommsFramework.Queries;

import CommsFramework.Enums.Action;
import CommsFramework.Enums.Key;
import CommsFramework.Enums.Loot;
import lombok.Getter;
import org.json.JSONObject;

public class FindChestQuery {
    private final Action action = Action.findChest;
    @Getter private Loot loot;

    public FindChestQuery(Loot loot)
    {
        this.loot = loot;
    }

    private FindChestQuery() {}
    public JSONObject serialize()
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Key.action.name(), action.getID());
        jsonObject.put(Key.loot.name(), loot.getID());
        return jsonObject;
    }

    public static FindChestQuery deserialize(JSONObject jsonObject)
    {
        FindChestQuery query = new FindChestQuery();
        query.loot = Loot.getByID(jsonObject.getInt(Key.loot.name()));
        return query;
    }

}
