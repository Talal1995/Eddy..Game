package game;

import project.RealTimeController;
import util.Point;
import util.config.ConfigurationUtility;
import util.config.MissingConfigurationException;

import util.eventrouting.EventRouter;
import util.eventrouting.Listener;
import util.eventrouting.PCGEvent;
import util.eventrouting.events.PlayerMovementEvent;

public class Wall extends Entity  implements Listener{

        private boolean walkthrough;
        private boolean seethrough;
        private boolean movable;
        private boolean doesDamage;
        private int damageValue;
        private boolean destructable;
        private RealTimeController controller;

        private int tileValue;

        private Point position;

        private static String defaultConfig = "config/GameRules.json";
        private static ApplicationConfig instance = null;
        private ConfigurationUtility config;

        public Wall(int tileValue, RealTimeController controller) {  //type not neccesarily needed if we only have 1 type of wall
            this.tileValue = tileValue;

            try {
                config = new ConfigurationUtility(defaultConfig, true);
            } catch (MissingConfigurationException e) {
                e.printStackTrace();
            }
            this.controller = controller;

                walkthrough = config.getBoolean("Walls.walkthrough");
                seethrough = config.getBoolean("Walls.seethrough");
                movable = config.getBoolean("Walls.movable");
                doesDamage = config.getBoolean("Walls.Damage.doesDamage");
                damageValue = config.getInt("Walls.Damage.damageValue");
                destructable = config.getBoolean("Walls.destructable");

            EventRouter.getInstance().registerListener(this, new PlayerMovementEvent(null, null, null));
        }



        public Point getPosition(){
            return position;
        }

        public void setPosition(Point p){
            position = p;
        }

        public void giveDamage(Player player) {
            player.TakeDamage(this.damageValue);
        }


        public void handleEvent(Entity sender){
           if (sender instanceof Player){
               if (movable){
                   controller.moveObject(position,((Player) sender).getDirection());
               }
               if (doesDamage){
                   giveDamage((Player)sender);
               }
           }
        }

    /*
    When pinged by a PlayerMovementEvent the wall handles if the player tries to walk on the specific Wall-tile.
    If Walls is defined as movable, the wall gets pushed by the players direction.
    If Walls is defined as walkthrough, the player steps and stands on top of the wall.
     */
    public void ping(PCGEvent e) {
            if(e instanceof PlayerMovementEvent) {
                PlayerMovementEvent event = (PlayerMovementEvent) e;
                if(event.getTile(event.getPosition()) == 1) {
                    if (event.getEntity() instanceof Player) {
                        Player player = (Player) event.getEntity();
                        setPosition(event.getPosition());
                        if (movable) {
                            controller.moveObject(position, (player.getDirection()));
                            player.setMove(false);
                        }
                        if (walkthrough) {
                            player.setMove(true);
                        }
                    }
                }
            }
    }
}
