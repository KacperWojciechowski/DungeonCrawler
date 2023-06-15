package GameLogic.Enemies;

import java.util.Random;

public class Rat extends Enemy {
    public Rat() {
        name = "Voracious Rat";
        Random generate = new Random();
        hp = generate.nextInt(15)+2;
        damage = generate.nextInt(2)+1;
    }
}
