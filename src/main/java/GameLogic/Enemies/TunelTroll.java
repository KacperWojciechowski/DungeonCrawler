package GameLogic.Enemies;

import java.util.Random;

public class TunelTroll extends Enemy {
    public TunelTroll()
    {
        name = "Giant Tunel Troll";
        Random generate = new Random();
        hp = generate.nextInt(20) + 60;
        damage = generate.nextInt(15) + 15;
    }
}
