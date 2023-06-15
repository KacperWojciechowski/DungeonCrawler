package GameLogic;

public class Player {
    int damage = 10;
    int skillDamage = 15;
    int skillCost = 2;
    int vitality = 10;
    int intelligence = 10;
    int hpPotionsCount = 0;
    int manaPotionsCount = 0;
    int location;
    int hp_limit;
    int hp;
    int mana_limit;
    int mana;
    public Player() {
        hp_limit = 10;
        mana_limit = 10;
        hp = hp_limit;
        mana = mana_limit;}

    public void upgradeDamage()
    {
        damage += 2;
    }

    public void upgradeVitality()
    {
        vitality += 1;
        hp_limit = 10 + (vitality - 10) * 2;
        hp += 2;
    }

    public void upgradeIntelligence()
    {
        intelligence += 1;
        mana_limit = 10 + (intelligence - 10) * 2;
        mana += 2;
        skillDamage = 15 + (intelligence - 10) * 3;
    }

    public void findHpPotion()
    {
        hpPotionsCount++;
    }
    public void useHpPotion()
    {
        hpPotionsCount--;
        hp = Math.min(hp + 5, hp_limit);
    }
    public void findManaPotion()
    {
        manaPotionsCount++;
    }
    public void useManaPotion()
    {
        manaPotionsCount--;
        mana = Math.min(mana + 5, mana_limit);
    }
    public void setLocation(int location)
    {
        this.location = location;
    }
    public int getLocation()
    {
        return location;
    }

    public int getDamage() {
        return damage;
    }

    public int getHp() {
        return hp;
    }

    public int getHp_limit() {
        return hp_limit;
    }

    public int getHpPotionsCount() {
        return hpPotionsCount;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getMana() {
        return mana;
    }

    public int getMana_limit() {
        return mana_limit;
    }

    public int getManaPotionsCount() {
        return manaPotionsCount;
    }

    public int getVitality() {
        return vitality;
    }

    public int getSkillDamage() {
        return skillDamage;
    }

    public int getSkillCost() {
        return skillCost;
    }
}
