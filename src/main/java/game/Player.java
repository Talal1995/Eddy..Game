package game;

import project.Move;
import util.config.ConfigurationUtility;
import util.config.MissingConfigurationException;

public class Player extends Entity{

    private int health;
    private int closeAttack;
    private int rangeAttack;
    private boolean canAttackClose;
    private boolean canAttackRange;
    private boolean isDead;
    private int rangeAttackDistance;
    private Move direction;
    private static String defaultConfig = "config/GameRules.json";
    private static ApplicationConfig instance = null;
    private ConfigurationUtility config;
    private boolean canMove;
    private boolean destroyTile;
    private int points;
    private int hitCounter;
    private int chestsOpened;

    public Player(){
        try{
            config = new ConfigurationUtility(defaultConfig, true);
        } catch (MissingConfigurationException e){
            e.printStackTrace();
        }

        health = config.getInt("Player.health");
        closeAttack = config.getInt("Player.closeAttackValue");
        rangeAttack = config.getInt("Player.closeAttackValue");
        rangeAttackDistance = config.getInt("Player.rangeAttackDistance");
        canAttackClose = config.getBoolean("Player.closeAttack");
        canAttackRange = config.getBoolean("Player.rangeAttack");
        canMove = true;
        destroyTile = false;
        isDead = false;
        points = 0;
    }

    public void TakeDamage(int damage){
        health = health - damage;
    }
    public void takeChest(){chestsOpened++;}
    public void takeHit() {hitCounter++;}

    public void setDirection(Move direction){
        this.direction = direction;
    }

    public Move getDirection(){
        return direction;
    }

    public void giveDamage(Enemy enemy, int damage){
               enemy.takeDamage(damage);
        }

        public int getRangeAttack(){
        return rangeAttack;
        }
        public int getCloseAttack(){
        return  closeAttack;
        }
        public boolean getCanAttackRange(){
        return canAttackRange;
        }

        public boolean getCanAttackClose(){
        return canAttackClose;
        }

        public int getrangeAttackDistance(){
        return  rangeAttackDistance;
        }

    public boolean seeIfDead(){
        return isDead;
    }

    public boolean canMove() {
        return canMove;
    }

    public void setMove(boolean bool) {
        canMove = bool;
    }

    public boolean destroyTile() {
        return destroyTile;
    }

    public void setDestroyTile (boolean bool) {
        destroyTile = bool;
    }

    public void givePoints(int points) {
        this.points += points;
    }

    public int getPoints() {
        return points;
    }

    public int getHits(){ return hitCounter;}
    public int getChestsOpened(){ return chestsOpened;}

    public void resetStats(){
        points = 0;
        hitCounter= 0;
        chestsOpened = 0;
    }
    public int[] getStats(){
        int[] stats = new int[3];
        stats[0] = new Integer(points);
        stats[1] = new Integer(hitCounter);
        stats[2] = new Integer(chestsOpened);
        return stats;
    }
    public void loadStats(int[] stats){
        if(stats.length != 3){
            System.out.println("Stats list must contain only 3 elements!");
        }
        else{
            points = stats[0];
            hitCounter = stats[1];
            chestsOpened = stats[2];
        }
    }
}
