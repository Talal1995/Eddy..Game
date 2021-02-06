package project;

public class GameInfo {

    private int stepsTaken;
    private int hitsTaken;
    private int doorsOpened;
    private int chestsOpened;
    private int enemiesKilled;
    private int bossesKilled;
    private int attacksMade;
    private int points;


    public GameInfo() {
        stepsTaken = 0;
        hitsTaken = 0;
        doorsOpened = 0;
        chestsOpened = 0;
        enemiesKilled = 0;
        bossesKilled = 0;
        attacksMade = 0;
        points = 0;
    }

    public int getPoints() { return points; }

    public void setPoints(int points) { this.points = points; }

    public int getStepsTaken() {
        return stepsTaken;
    }

    public void setStepsTaken(int stepsTaken) {
        this.stepsTaken = stepsTaken;
    }

    public int getHitsTaken() {
        return hitsTaken;
    }

    public void setHitsTaken(int hitsTaken) {
        this.hitsTaken = hitsTaken;
    }

    public int getDoorsOpened() {
        return doorsOpened;
    }

    public void setDoorsOpened(int doorsOpened) {
        this.doorsOpened = doorsOpened;
    }

    public int getChestsOpened() {
        return chestsOpened;
    }

    public void setChestsOpened(int chestsOpened) {
        this.chestsOpened = chestsOpened;
    }

    public int getEnemiesKilled() {
        return enemiesKilled;
    }

    public void setEnemiesKilled(int enemiesKilled) {
        this.enemiesKilled = enemiesKilled;
    }

    public int getBossesKilled() {
        return bossesKilled;
    }

    public void setBossesKilled(int bossesKilled) {
        this.bossesKilled = bossesKilled;
    }

    public int getAttacksMade() {
        return attacksMade;
    }

    public void setAttacksMade(int attacksMade) {
        this.attacksMade = attacksMade;
    }

    public void resetStats(){
        setHitsTaken(0);
        setChestsOpened(0);
        setStepsTaken(0);
        setDoorsOpened(0);
        setAttacksMade(0);
        setBossesKilled(0);
        setEnemiesKilled(0);
        setPoints(0);
    }

    @Override
    public String toString()
    {
        String info = "Steps taken: " + stepsTaken +
                "\nHits taken: " + hitsTaken +
                "\nDoors opened: " + doorsOpened +
                "\nChests opened: " + chestsOpened +
                "\nPoints: " + points +
                "\nEnemies killed: " + enemiesKilled +
                "\nBosses killed: " + bossesKilled +
                "\nAttacks made: " + attacksMade;
        return info;
    }
    public int[] getAllStats(){
        int[] stats = new int[8];
        stats[0] = new Integer(stepsTaken);
        stats[1] = new Integer(hitsTaken);
        stats[2] = new Integer(doorsOpened);
        stats[3] = new Integer(chestsOpened);
        stats[4] = new Integer(points);
        stats[5] =new Integer(enemiesKilled);
        stats[6] =new Integer(bossesKilled);
        stats[6] =new Integer(attacksMade);
        return stats;
    }
    public void loadStats(int[] stats){
        if(stats.length != 8){
            System.out.println("Stats list must contain only 8 elements!");
        }
        else{
            stepsTaken = stats[0];
            hitsTaken = stats[1];
            doorsOpened = stats[2];
            chestsOpened = stats[3];
            points = stats[4];
            enemiesKilled = stats[5];
            bossesKilled = stats[6];
            attacksMade = stats[7];
        }
    }
}
