package util.eventrouting.events;

import game.Entity;
import util.Point;
import util.eventrouting.PCGEvent;

public class PlayerMovementEvent extends PCGEvent {

    private Entity entity;
    private Point position;
    private int[][] matrix;

    public PlayerMovementEvent(Entity entity, int[][] matrix, Point position){
        this.entity = entity;
        this.matrix = matrix;
        this.position = position;
    }

    public Entity getEntity(){
        return entity;
    }

    public int[][] getMatrix(){
        return matrix;
    }

    public Point getPosition(){
        return position;
    }

    public int getTile(Point position) {
        return matrix[position.getX()][position.getY()];
    }
}
