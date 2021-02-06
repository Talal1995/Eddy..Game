package game;

import util.config.ConfigurationUtility;

import util.config.MissingConfigurationException;
import util.eventrouting.EventRouter;
import util.eventrouting.Listener;
import util.eventrouting.PCGEvent;
import util.eventrouting.events.PlayerMovementEvent;

public class Treasure extends Entity implements Listener {

    private static String defaultConfig = "config/GameRules.json";
    private static ApplicationConfig instance = null;
    private ConfigurationUtility config;

    private int value;
    private boolean spawnEnemies;
    private boolean spawnBoss;
    private boolean giveHealth;
    private int healthValue;

    private int tileValue;


    public Treasure(int tileValue){
        this.tileValue = tileValue;

        try {
            config = new ConfigurationUtility(defaultConfig, true);
        } catch (MissingConfigurationException e) {
            e.printStackTrace();
        }

        value = config.getInt("Treasure.pointsValue");
        spawnEnemies = config.getBoolean("Treasure.spawnEnemies");
        spawnBoss = config.getBoolean("Treasure.spawnBoss");
        giveHealth = config.getBoolean("Treasure.giveHealth");
        healthValue = config.getInt("Treasure.healthValue");

        EventRouter.getInstance().registerListener(this, new PlayerMovementEvent(null, null, null));
    }

    @Override
    public void ping(PCGEvent e) {
        if(e instanceof PlayerMovementEvent) {
            PlayerMovementEvent event = (PlayerMovementEvent) e;
            if (event.getTile(event.getPosition()) == tileValue) {
                if (event.getEntity() instanceof Player) {
                    Player player = (Player) event.getEntity();
                    player.setDestroyTile(true);
                    System.out.println("Set destroy tile to true");
                    player.givePoints(value);
                    player.takeChest();
                    if(giveHealth)
                        player.TakeDamage(-healthValue);
                }
            }
        }
    }
}
