package GameLogic.Map;

import java.util.ArrayList;
import java.util.List;

public class MapGraph {
    List<List<Integer>> graph;

    private List<Coord> getRoomsListFromPixelMap(PixelMap pixelMap)
    {
        List<Coord> list = new ArrayList<>();
        for (int x = 0; x < pixelMap.getRowsCount(); x++)
        {
            for (int y = 0; y < pixelMap.getColumnsCount(); y++)
            {
                if (pixelMap.getField(new Coord(x, y)) == PixelMap.ROOM)
                {
                    list.add(new Coord(x, y));
                }
            }
        }
        return list;
    }
    private void appendNeighbourToGraph(int roomIndex, Coord neighbour, List<Coord> listOfRooms, PixelMap pixelMap)
    {
        if (pixelMap.hasNeighbour(neighbour) && listOfRooms.contains(neighbour))
        {
            graph.get(roomIndex).add(listOfRooms.indexOf(neighbour));
        }
    }
    private void turnIntoGraph(List<Coord> listOfRooms, PixelMap pixelMap)
    {
        graph = new ArrayList<>(listOfRooms.size());
        for (int i = 0; i < listOfRooms.size(); i++)
        {
            graph.add(new ArrayList<>());
        }

        for (Coord room : listOfRooms)
        {
            int currentRoomIndex = listOfRooms.indexOf(room);

            Coord topNeighbour = new Coord(room.x, room.y - 1);
            Coord bottomNeighbour = new Coord(room.x, room.y + 1);
            Coord leftNeighbour = new Coord(room.x-1, room.y);
            Coord rightNeighbour = new Coord(room.x + 1, room.y);

            appendNeighbourToGraph(currentRoomIndex, topNeighbour, listOfRooms, pixelMap);
            appendNeighbourToGraph(currentRoomIndex, bottomNeighbour, listOfRooms, pixelMap);
            appendNeighbourToGraph(currentRoomIndex, leftNeighbour, listOfRooms, pixelMap);
            appendNeighbourToGraph(currentRoomIndex, rightNeighbour, listOfRooms, pixelMap);
        }
    }
    public MapGraph()
    {
        PixelMap pixelMap = new PixelMap();
        List<Coord> listOfRooms = getRoomsListFromPixelMap(pixelMap);
        turnIntoGraph(listOfRooms, pixelMap);
    }
    public List<Integer> getNeighboursOf(int node)
    {
        return graph.get(node);
    }
    public int getRoomsCount()
    {
        return graph.size();
    }
}
