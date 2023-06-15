package GameLogic.Enemies;

public abstract class Enemy {
    protected int hp;
    protected int damage;
    protected String name;

    public int getHp() {
        return hp;
    }

    public int getDamage() {
        return damage;
    }

    public String getName() {
        return name;
    }

    public boolean takeDamage(int damage)
    {
        hp -= damage;
        return hp <= 0;
    }
}
