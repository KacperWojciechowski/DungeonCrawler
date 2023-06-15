package GameLogic.Enemies;

public class EnemyInLocation {
    public Enemy enemy;
    public Integer room;

    public EnemyInLocation() {}
    public EnemyInLocation(Enemy enemy, Integer room){
        this.enemy = enemy;
        this.room = room;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public Integer getRoom() {
        return room;
    }
}
