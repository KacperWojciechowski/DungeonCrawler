package CommsFramework.Queries;

import CommsFramework.Enums.Action;
import CommsFramework.Enums.Key;
import GameLogic.Player;
import lombok.Getter;
import org.json.JSONObject;

public class CheckStatsQuery {
    private final Action action = Action.checkStats;

    @Getter int hp;
    @Getter int mana;
    @Getter int damage;
    @Getter int intelligence;
    @Getter int vitality;
    @Getter int skillDamage;
    @Getter int skillCost;
    @Getter int manaPotionsCount;
    @Getter int hpPotionsCount;
    @Getter int manaLimit;
    @Getter int hpLimit;

    public CheckStatsQuery(Player player)
    {
        hp = player.getHp();
        mana = player.getMana();
        damage = player.getDamage();
        intelligence = player.getIntelligence();
        vitality = player.getVitality();
        skillDamage = player.getSkillDamage();
        skillCost = player.getSkillCost();
        manaPotionsCount = player.getManaPotionsCount();
        hpPotionsCount = player.getHpPotionsCount();
        manaLimit = player.getMana_limit();
        hpLimit = player.getHp_limit();
    }

    private CheckStatsQuery() {}
    public JSONObject serialize()
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Key.action.name(), action.getID());
        jsonObject.put(Key.playerMana.name(), mana);
        jsonObject.put(Key.playerHp.name(), hp);
        jsonObject.put(Key.playerVitality.name(), vitality);
        jsonObject.put(Key.playerDamage.name(), damage);
        jsonObject.put(Key.playerIntelligence.name(), intelligence);
        jsonObject.put(Key.playerSkillCost.name(), skillCost);
        jsonObject.put(Key.playerSkillDamage.name(), skillDamage);
        jsonObject.put(Key.playerManaPotionsCount.name(), manaPotionsCount);
        jsonObject.put(Key.playerHpPotionsCount.name(), hpPotionsCount);
        jsonObject.put(Key.playerManaLimit.name(), manaLimit);
        jsonObject.put(Key.playerHpLimit.name(), hpLimit);
        return jsonObject;
    }

    public static CheckStatsQuery deserialize(JSONObject jsonObject)
    {
        CheckStatsQuery query = new CheckStatsQuery();
        query.mana = jsonObject.getInt(Key.playerMana.name());
        query.hp = jsonObject.getInt(Key.playerHp.name());
        query.vitality = jsonObject.getInt(Key.playerVitality.name());
        query.damage = jsonObject.getInt(Key.playerDamage.name());
        query.intelligence = jsonObject.getInt(Key.playerIntelligence.name());
        query.skillCost = jsonObject.getInt(Key.playerSkillCost.name());
        query.skillDamage = jsonObject.getInt(Key.playerSkillDamage.name());
        query.manaPotionsCount = jsonObject.getInt(Key.playerManaPotionsCount.name());
        query.hpPotionsCount = jsonObject.getInt(Key.playerHpPotionsCount.name());
        query.manaLimit = jsonObject.getInt(Key.playerManaLimit.name());
        query.hpLimit = jsonObject.getInt(Key.playerHpLimit.name());
        return query;
    }
}
