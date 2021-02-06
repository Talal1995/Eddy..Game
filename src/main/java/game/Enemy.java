package game;

import project.Move;
import util.Point;
import util.config.ConfigurationUtility;
import util.config.MissingConfigurationException;

import util.eventrouting.EventRouter;
import util.eventrouting.Listener;
import util.eventrouting.PCGEvent;
import util.eventrouting.events.PlayerMovementEvent;

import java.util.Random;

public class Enemy extends Entity implements Listener{

    private int health;
    private int attack;
    private boolean isDead;
    private int treasure = 0;
    private Point position;
    private boolean canShoot;
    private Move direction = Move.NEUTRAL;

    private Random rng = new Random();
    private static String defaultConfig = "config/GameRules.json";
    private static ApplicationConfig instance = null;
    private ConfigurationUtility config;

    private boolean hasShot = false;

    public Enemy(int type, Point position) {
        try {
            config = new ConfigurationUtility(defaultConfig, true);
        } catch (MissingConfigurationException e) {
            e.printStackTrace();
        }
        this.position = position;
    if (type == 3){
        health = config.getInt("Enemy.health");
        attack = config.getInt("Enemy.attack");
        canShoot = config.getBoolean("Enemy.shooting");
        isDead = false;
        direction = Move.values()[rng.nextInt(4)]; //Gives an enemy an random direction to start with.
       // System.out.println(direction.toString() + " ");

    } else if (type == 5) {
        health = config.getInt("Boss.health");
        attack = config.getInt("Boss.attack");
        canShoot = config.getBoolean("Boss.shooting");
        isDead = false;
      }
    EventRouter.getInstance().registerListener(this, new PlayerMovementEvent(null, null,null));
    }

    public void takeDamage(int damage) {
        health = health - damage;
        if(health <=0){
            isDead = true;
        }
        System.out.println("Enemy health: " + health);
    }

    public Point getPosition(){
        return position;
    }

    public void setPosition(Point p){
        this.position = p;
    }
    public void giveDamage(Player player) {
        player.TakeDamage(this.attack);
    }

    public boolean seeIfDead() {
        return isDead;
    }
    public void pickUpTresure(int value){
        treasure += value;
    }

    public boolean getEnemyShooting(){
        return canShoot;
    }

    public void setDirection(Move direction){
        this.direction = direction;
    }
    public Move getDirection(){
        return direction;
    }

    /*
    When pinged by a PlayerMovementEvent the enemy handles if the player tries to walk on the specific Enemy-tile.
    If the enemy stands on a tile, the player can not move to that tile.
     */
    public void ping(PCGEvent e) {
        if(e instanceof PlayerMovementEvent) {
            PlayerMovementEvent event = (PlayerMovementEvent) e;
            if(event.getTile(event.getPosition()) == 3) {
                if (event.getEntity() instanceof Player) {
                    Player player = (Player) event.getEntity();
                    //setPosition(event.getPosition());
                    if (position.getX() == event.getPosition().getX()) {
                        if (position.getY() == event.getPosition().getY()) {
                            player.setMove(false);
                        }
                    }
                }
            }
        }
    }

    public boolean getHasShot() {
        return hasShot;
    }

    public void setHasShot(boolean set) {
        hasShot = set;
    }

}