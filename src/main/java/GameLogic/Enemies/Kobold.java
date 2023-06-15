package GameLogic.Enemies;

import java.util.Random;

public class Kobold extends Enemy {
    public Kobold()
    {
        name = "Disfigured Kobold";
        Random generate = new Random();
        hp = generate.nextInt(15) + 10;
        damage = generate.nextInt(4) + 2;
    }
}
