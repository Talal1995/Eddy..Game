package util.algorithms;

import java.util.List;
import java.util.Optional;

import java.util.ArrayList;
import game.Room;
import util.Point;
import util.Util;


public class Pathfinder {
	//private Point2D goal;
    private Room room;

    public Pathfinder(Room room)
    {
        this.room = room;
    }

	//A* algorithm 
    public Node[] find(Point start, Point goal)
    {
        List<Node> openList = new ArrayList<Node>();
        List<Node> closedList = new ArrayList<Node>();
        //this.goal = goal;

        //init open list and init
        Node nStart = new Node(Util.manhattanDistance(start, goal), start, null);
        nStart.g = 0;
        openList.add(nStart);

        while (openList.size() > 0)
        {
            Node current = popNode(openList);
            if (current.equals(goal)) 
            	return expandTreeFromLast(current);

            //Add current to close list
            closedList.add(current);

            //Get all children for current node
            List<Point> children = room.getAvailableCoords(current.position);

            for (Point child : children)
            {
                if (existsPointInArrayNodes(closedList, child)) continue;

                double g = current.g + 1;
                Node n = findNodeAtPoint(openList,child);
                if(n == null){
                	n = new Node(g + Util.manhattanDistance(child, goal), child, current);
                	n.g = g;
                	openList.add(n);
                } else if (g >= n.g){
                	continue;
                }
                
                n.parent = current;
                n.g = g;
                n.f = g + Util.manhattanDistance(child, goal);
                
            }

            //Sort openList by its f
            //openList = qSortOpenList(openList);
            openList.sort((a,b) -> Double.compare(a.f, b.f));
        }

        // Didn't find a path. Oops.
        return new Node[0];
    }

    private Node[] expandTreeFromLast(Node last)
    {
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(last);

        while(last.parent != null)
        {
            last = last.parent;
            nodes.add(last);
        }

        return nodes.stream().toArray(Node[]::new);
    }

    private boolean existsPointInArrayNodes(List<Node> closed_list, Point point)
    {
        for(Node n : closed_list)
        {
            if (n.equals(point)) 
            	return true;
        }
        return false;
    }
    
    private Node findNodeAtPoint(List<Node> nodes, Point point){
    	Optional<Node> n = nodes.stream().filter(x -> x.equals(point)).findFirst();
    	if(n.isPresent())
    		return n.get();
    	return null;
    }

    private Node popNode(List<Node> nodes)
    {
        return nodes.remove(0);
    }

    public void setMap(Room room)
    {
        this.room = room;
    }
}
