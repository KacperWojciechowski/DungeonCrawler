package GameLogic;

import java.util.Random;

public class PixelMap {
    private final int rows = 50;
    private final int columns = 50;
    private final int[][] map = new int[rows][columns];

    public static final int ROOM = 0;
    public static final int WALL = 1;
    private void randomizeMap()
    {
        Random generate = new Random(1234);

        for (int x = 0; x < map.length; x++)
        {
            for (int y = 0; y < map[0].length; y++)
            {
                map[x][y] = generate.nextInt(2);
            }
        }
    }
    PixelMap()
    {
        randomizeMap();
    }
    public int getRowsCount()
    {
        return rows;
    }
    public int getColumnsCount()
    {
        return columns;
    }
    public int getField(Coord coord)
    {
        return map[coord.x][coord.y];
    }

    public boolean hasNeighbour(Coord coord)
    {
        return (coord.x < rows) && (coord.x >= 0) && (coord.y < columns) && (coord.y >= 0);
    }
}
