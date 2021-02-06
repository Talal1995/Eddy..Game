package game;

import project.Move;
import project.RealTimeController;
import util.Point;

import java.util.Iterator;

public class Arrow {
    private Point position;
    private Move direction;

    private RealTimeController controller;

    private Enemy owner;
    private Player player;

    public Arrow(RealTimeController controller, Point position, Move direction, Enemy owner, Player player) {
        this.controller = controller;
        this.position = position;
        this.direction = direction;
        this.owner = owner;
        this.player = player;
    }

    public void destroyArrow() {
        controller.removeArrow(this);
        owner.setHasShot(false);
    }

    public Move getDirection() {
        return this.direction;
    }

    public Point getPosition() {
        return this.position;
    }

    public void move(Move direction) {
        switch (direction) {
            case UP:
                position.setX(position.getX() - 1);
                break;
            case RIGHT:
                position.setY(position.getY() + 1);
                break;
            case DOWN:
                position.setX(position.getX() + 1);
                break;
            case LEFT:
                position.setY(position.getY() - 1);
                break;
            default:
                break;
        }
    }
    /*
    Checks if the arrow has hit a wall(1) or enemy (3) and removes if so that´s the case.
    If the arrow hits a player(6) the arrow is removed and deals damage to/ hits the player.
     */
    public void checkTile(int[][] matrix, Iterator<Arrow> iter) {
        int tile = matrix[position.getX()][position.getY()];
        if(tile == 1 || tile == 3) {
            iter.remove();
            destroyArrow();
        } else if (tile == 6) {
            iter.remove();
            destroyArrow();
            player.takeHit();
            System.out.println(player.getHits());
        }
    }
}
