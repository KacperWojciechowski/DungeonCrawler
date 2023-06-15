package CommsFramework.Queries;

import CommsFramework.Enums.Action;
import CommsFramework.Enums.Key;
import lombok.Getter;
import org.json.JSONObject;

public class FoundEnemyQuery {
    private final Action action = Action.findEnemy;

    @Getter private String enemyName;

    public FoundEnemyQuery(String enemyName) {
        this.enemyName = enemyName;
    }

    private FoundEnemyQuery() {}
    public JSONObject serialize()
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Key.action.name(), action.getID());
        jsonObject.put(Key.enemyName.name(), enemyName);
        return jsonObject;
    }

    public static FoundEnemyQuery deserialize(JSONObject jsonObject)
    {
        FoundEnemyQuery query = new FoundEnemyQuery();
        query.enemyName = jsonObject.getString(Key.enemyName.name());
        return query;
    }
}
