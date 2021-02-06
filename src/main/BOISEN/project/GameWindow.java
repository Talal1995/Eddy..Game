package project;

import game.Arrow;
import game.Dungeon;
import game.Room;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class GameWindow {

    private Dungeon d;
    private GridPane pane = new GridPane();
    private ArrayList<Image> images = new ArrayList<>();
    private RealTimeController controller;
    private static Room r;
    private Stage testStage = new Stage();

    private boolean mapView;
    Pane map;

    Scene gameScene = new Scene(pane);
    Scene mapScene;

    private Stage infoStage = new Stage();
    InfoGUI infoMap;
    Scene infoScene;
    Long start = System.currentTimeMillis();
    Long end;
    final Set<KeyCode> pressedKeys;


    // TODO: 15/10/2020 ENTITIES ARE SUBSCRIBED TO PING EVENTS, after being pinged, checks structure built by jakob and talal

    public GameWindow(Dungeon d, RealTimeController controller) {
        this.d = d;
        map = new Pane(d.dPane);
        mapScene = new Scene(map);
        r = d.getInitialRoom();
        this.controller = controller;
        infoStage.setOnHiding(event -> {
            mapView= false;
        });
        loadImages();
        setUpPane();
        controller.setUpMatrixCounter(d.getAllRooms());
        controller.setUUID(d.getInitialRoom().specificID);
        pressedKeys = new HashSet<KeyCode>();
    }

    public void loadImages() {
        images.add(new Image("res/floor.png"));
        images.add(new Image("res/wall.png"));
        images.add(new Image("res/treasure.png"));
        images.add(new Image("res/enemy.png"));
        images.add(new Image("res/door.png"));
        images.add(new Image("res/enemy.png"));
        images.add(new Image("res/heroTile.png"));
        images.add(new Image("res/arrowUp.png"));
        images.add(new Image("res/arrowRight.png"));
        images.add(new Image("res/arrowDown.png"));
        images.add(new Image("res/arrowLeft.png"));
    }

    public void setUpPane() {
        updateGraphics();
        testStage.setScene(gameScene);
        testStage.show();
        setListeners(gameScene);
        mapView = false;
        infoStage.close();
    }

    public void setUpMap() {
        updateGraphics();
        for (Room room : d.getAllRooms()) {
            if (room != r) {
                room.fillBitMap();
                room.localConfig.getWorldCanvas().drawMapHidden();
            }
        }
        testStage.setScene(mapScene);
        testStage.show();
        setListeners(mapScene);
        mapView = true;

    }
    public void setUpInfo(){
        long time = System.currentTimeMillis() - start;
        time = time / 1000;
        infoMap = new InfoGUI(controller.getGameInfo(), time);
        infoScene = new Scene(infoMap);
        mapView = true;
        infoStage.setScene(infoScene);
        infoStage.show();
        setListeners(infoScene);
    }

    public void setUpHeatMap(){
        updateGraphics();
        for (Room room : d.getAllRooms()) {
                room.fillBitMap();
                room.localConfig.getWorldCanvas().drawMapPercentage(controller.getMatrixCounter(), room);
        }
        testStage.setScene(mapScene);
        testStage.show();
        setListeners(mapScene);
        mapView = true;
    }
    public void setUpPlayerDeath(int x, int y){
        updateGraphics();
        r.fillBitMap();
        r.localConfig.getWorldCanvas().drawPlayerDeath(controller.getMatrixCounter(), r, x, y);
        testStage.setScene(mapScene);
        testStage.show();
        setListeners(mapScene);
        mapView = true;
        //controller.saveHeatMapSerializable();
    }
    public void updateRoomGraphics(Room room) {
        r = room;
        updateGraphics();
    }

    public void setListeners(Scene s) {
        s.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (!mapView) {
                    KeyCode key = event.getCode();
                    if(!pressedKeys.contains(key)) {
                        pressedKeys.add(key);

                        switch (event.getCode()) {
                            case W:
                                //   if (playerPosition.getX() > 0)
                                controller.updatePlayerPosition(Move.UP);
                                break;
                            case A:
                                //  if (playerPosition.getY() > 0)
                                controller.updatePlayerPosition(Move.LEFT);
                                break;
                            case S:
                                // if (r.getRowCount() - 1 > playerPosition.getX())
                                controller.updatePlayerPosition(Move.DOWN);
                                break;
                            case D:
                                //  if (r.getColCount() - 1 > playerPosition.getY())
                                controller.updatePlayerPosition(Move.RIGHT);
                                break;
                            case E:
                                //  if (r.getColCount() - 1 > playerPosition.getY())
                                controller.playerAttack();
                                break;
                            case SPACE:
                                controller.enterDoor();
                                break;
                            case M:
                                setUpMap();
                                break;
                            case H:
                                setUpHeatMap();
                                break;
                            case I:
                                setUpInfo();
                                break;
                            case G:
                                setUpPlayerDeath(controller.PlayerPosition().getY(),controller.PlayerPosition().getX());
                                break;
                            case R:
                                controller.undoMove();
                                updateGraphics();
                        }
                    }

                } else {
                    switch (event.getCode()) {
                        case M:
                        case H:
                        case G:
                            setUpPane();
                            break;
                        case I:
                            infoStage.close();
                    }
                }
            }
        });

        s.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                pressedKeys.remove(event.getCode());
            }
        });

    }
    public void updateGraphics() {

        ArrayList<Arrow> arrowList = controller.getArrowList();

        //CLEAR THE PANE
        pane.getChildren().clear();
        //BELOW REDRAWS THE PANE FROM SCRATCH
        for (int x = 0; x < r.getColCount(); x++) {
            for (int y = 0; y < r.getRowCount(); y++) {
                ImageView v = new ImageView();

                boolean foundArrow = false;
                for (Arrow a : arrowList) {
                    if (a.getPosition().getX() == y && a.getPosition().getY() == x) {
                        foundArrow = true;
                        switch (a.getDirection()) {
                            case UP:
                                v.setImage(images.get(7));
                                pane.add(v, x, y);
                                break;
                            case RIGHT:
                                v.setImage(images.get(8));
                                pane.add(v, x, y);
                                break;
                            case DOWN:
                                v.setImage(images.get(9));
                                pane.add(v, x, y);
                                break;
                            case LEFT:
                                v.setImage(images.get(10));
                                pane.add(v, x, y);
                                break;
                            default:
                                break;
                        }
                        break;
                    }
                }
                if (foundArrow)
                    continue;

                switch (r.matrix[y][x]) {
                    case 0:
                        v.setImage(images.get(0));
                        pane.add(v, x, y);
                        break;
                    case 1:
                        v.setImage(images.get(1));
                        pane.add(v, x, y);
                        break;
                    case 2:
                        v.setImage(images.get(2));
                        pane.add(v, x, y);
                        break;
                    case 3:
                        v.setImage(images.get(3));
                        pane.add(v, x, y);
                        break;
                    case 4:
                        v.setImage(images.get(4));
                        pane.add(v, x, y);
                        break;
                    case 5:
                        v.setImage(images.get(5));
                        pane.add(v, x, y);
                        break;
                    case 6:
                        v.setImage(images.get(6));
                        pane.add(v, x, y);
                        break;
                }
            }
        }
    }
}
