package project;

import game.ApplicationConfig;
import util.config.ConfigurationUtility;
import util.config.MissingConfigurationException;


public class GameRules {

    private static String defaultConfig = "config/GameRules.json";
    private static ApplicationConfig instance = null;
    private ConfigurationUtility config;

    public GameRules() {
        try {
            config = new ConfigurationUtility(defaultConfig, true);
        } catch (MissingConfigurationException e) {
            e.printStackTrace();
        }
    }
    public boolean getWalkThroughWalls(){
        return config.getBoolean("Walls.walkthrough");
    }

    public boolean getSeeThroughWalls(){
        return config.getBoolean("Walls.seethrough");
    }
    public boolean getMovableWalls(){
        return config.getBoolean("Walls.movable");
    }
    public boolean getWallDoesDamage(){
        return config.getBoolean("Walls.Damage.doesDamage");
    }
    public int getWallDamageValue(){
        return config.getInt("Walls.Damage.damageValue");
    }
    public boolean getWallDestructable(){
        return config.getBoolean("Walls.destructable");
    }
    public int getTreasurePointsValue(){
        return config.getInt("Treasure.pointsValue");
    }
    public boolean getTreasurespawnEnemies(){
        return config.getBoolean("Treasure.spawnEnemies");
    }

    public boolean getTreasurespawnBoss(){
        return config.getBoolean("Treasure.spawnBoss");
    }
    public boolean getTreasureGiveHealth(){
        return config.getBoolean("Treasure.giveHealth");
    }
    public  int getTreasureHealthValue(){
        return config.getInt("Treasure.healthValue");
    }
    public boolean getEndGameGoalcollectAllTreasure(){
        return config.getBoolean("GameEndGoal.collectAllTreasure");
    }
    public boolean getEndGameGoalreachPointsValue(){
        return config.getBoolean("GameEndGoal.reachPointsValue");
    }

    public boolean getEndGameGoalkillAllEnemies(){
        return config.getBoolean("GameEndGoal.killAllEnemies");
    }

    public  int getEndGameGoalpointsAmount(){
        return config.getInt("GameEndGoal.pointsAmount");
    }

}

