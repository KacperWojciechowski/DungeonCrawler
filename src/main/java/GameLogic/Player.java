package GameLogic;

import lombok.Getter;

public class Player {
    @Getter int damage = 10;
    @Getter int skillDamage = 15;
    @Getter int skillCost = 2;
    @Getter int vitality = 10;
    @Getter int intelligence = 10;
    @Getter int hpPotionsCount = 0;
    @Getter int manaPotionsCount = 0;
    @Getter int location;
    @Getter int hp_limit = 10;
    @Getter int hp = hp_limit;
    @Getter int mana_limit = 10;
    @Getter int mana = mana_limit;
    @Getter int previousLocation;
    public Player() {}

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

    public int useSkill() {
        if (mana >= skillCost)
        {
            mana -= skillCost;
            return skillDamage;
        } else {
            return 0;
        }
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
        this.previousLocation = this.location;
        this.location = location;
    }

    public boolean takeDamage(int damage)
    {
        hp -= damage;
        return hp <= 0;
    }
}
