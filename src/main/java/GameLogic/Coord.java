package GameLogic;

import java.util.List;

public class Coord {
    public int x;
    public int y;
    public Coord() {}
    public Coord(int x, int y) {this.x = x; this.y = y;}
    public static int NO_INDEX = -1;
    @Override
    public boolean equals(Object o)
    {
        if (o == this) return true;
        else {
            Coord coord = (Coord) o;
            return coord.x == this.x && coord.y == this.y;
        }
    }
}
