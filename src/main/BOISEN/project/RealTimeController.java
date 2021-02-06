package project;

import game.*;

import util.Point;

import java.util.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import util.config.ConfigurationUtility;
import util.config.MissingConfigurationException;
import util.eventrouting.EventRouter;
import util.eventrouting.events.PlayerMovementEvent;

import javax.swing.*;


public class RealTimeController{

    private Dungeon dungeon;
    private static Room room;
    private Serialization serialization = new Serialization();
    private Point playerPosition = new Point(0,0);
    private int currentTileType = 0;
    private ArrayList<Room> rooms;
    private Set<RoomEdge> connections;
    List<Point> enemyCoordinatesList;
    private GameWindow gameWindow;
    private GameRules gameRules;
    ArrayList<Enemy> enemyEntityList = new ArrayList<Enemy>();
    private Wall walls;
    private Treasure treasure;
    private Player player;
    private GameInfo gameInfo;
    HashMap<UUID, int[][]> matrixCounter = new HashMap<UUID, int[][]>();
    private UUID RoomID;
    private AnimationHandler animator;
    private ArrayList<Arrow> arrowList;
    private int enemiesCount = 0;
    private int treasuresCount = 0;
    private boolean autoRestart = false;
    ArrayList<Point> treasures = new ArrayList<>();
    private boolean playerDestroyTile;
    GameState initialGameState;
    Stack<GameState> gameStates = new Stack<GameState>();


    /*
    FLOOR(0),
    WALL(1),
    TREASURE(2),
    ENEMY(3),
    DOOR(4),
    ENEMY_BOSS(5),
    HERO(6),
    NONE(7);
     */

    public RealTimeController(Dungeon dungeon){
        this.dungeon = dungeon;
        room = dungeon.getInitialRoom();
        rooms = dungeon.getAllRooms();
        loadEnemies();
        player = new Player();
        walls = new Wall(1, this);
        treasure = new Treasure(2);
        connections = dungeon.network.incidentEdges(room);
        gameRules = new GameRules();

        gameInfo = new GameInfo();
        playerDestroyTile = false;

        arrowList = new ArrayList<Arrow>();
        saveInitialState();
        animator = new AnimationHandler(this);
        animator.start();
        findTreasures();
        enemiesCount = getAmountOfEnemies();
        treasuresCount = getAmountOfTreasures();
    }

    public void findTreasures(){
        for (int i = 0; i < room.matrix.length; i++) {
            for (int j = 0; j < room.matrix[i].length; j++) {
                if(room.matrix[i][j] == 2){
                    treasures.add(new Point(i, j));
                }
            }
        }
    }
    /*
    Saves gamestates an a stack to enable going "back in time" and undo player moves.
     */
    public void saveDungeonState(){
        HashMap<UUID, int[][]> tempMatrix = new HashMap<UUID, int[][]>();
        for(Room room : dungeon.getAllRooms()){
            int[][] m = new int[room.getMatrix().length][room.getMatrix()[0].length];
            for (int i = 0; i < room.getMatrix().length; i++) {
                for (int j = 0; j < room.getMatrix()[0].length; j++) {

                    m[i][j] = room.getMatrix()[i][j];
                }
            }

            tempMatrix.put(room.specificID, m);

        }
        Point currentPoint = new Point(playerPosition.getX(), playerPosition.getY());
        int currentTile = new Integer(currentTileType);

        int[] gameStats = new int[gameInfo.getAllStats().length];
        int[] playerStats = new int[player.getStats().length];
        gameStates.push(new GameState(tempMatrix, room.specificID, currentPoint, currentTile, gameInfo.getAllStats(), player.getStats()));
    }
    /*
    Saves the initial state of the game for restarting the game.
     */
    public void saveInitialState(){
        HashMap<UUID, int[][]> tempMatrix = new HashMap<UUID, int[][]>();
        for(Room room : dungeon.getAllRooms()){
            int[][] m = new int[room.getMatrix().length][room.getMatrix()[0].length];
            for (int i = 0; i < room.getMatrix().length; i++) {
                for (int j = 0; j < room.getMatrix()[0].length; j++) {
                    m[i][j] = room.getMatrix()[i][j];
                }
            }

            tempMatrix.put(room.specificID, m);

        }
        Point currentPoint = new Point(playerPosition.getX(), playerPosition.getY());
        int currentTile = new Integer(currentTileType);

        int[] gameStats = new int[gameInfo.getAllStats().length];
        int[] playerStats = new int[player.getStats().length];
        initialGameState = new GameState(tempMatrix, room.specificID, currentPoint, currentTile, gameInfo.getAllStats(), player.getStats());
        System.out.println("Initial saved!");
    }
    /*
    Pops the latest saved gamestate and goes back one player move in the game.
     */
    public void undoMove(){
        matrixCounter.get(RoomID)[playerPosition.getX()][playerPosition.getY()]--;
        if (!gameStates.empty()) {
            GameState gameState = gameStates.pop();
            arrowList.clear();
            enemyEntityList.clear();

            for (Room room : dungeon.getAllRooms()) {
                UUID rid = room.specificID;
                room.setMatrix(gameState.getMatrix().get(rid));
                room.updateEnemies();
            }

            for (Room room : dungeon.getAllRooms()) {
                if(room.specificID == gameState.getInitialRoom()){
                    RealTimeController.room = room;
                     break;
                }
            }


            currentTileType = gameState.getCurrentTileType();
            playerPosition = gameState.getPlayerPosition();

            //Look more into these
            loadEnemies();


            gameWindow.updateRoomGraphics(room);
            gameWindow.updateGraphics();

            player.loadStats(gameState.getPlayerStats());
            gameInfo.loadStats(gameState.getGameStats());

            gameInfo.setChestsOpened(player.getChestsOpened());
            gameInfo.setHitsTaken(player.getHits());
            gameInfo.setPoints(player.getPoints());

        }
    }

    public void restartGame(){
            arrowList.clear();
            enemyEntityList.clear();

            for (Room room : dungeon.getAllRooms()) {
                UUID rid = room.specificID;
                room.setMatrix(initialGameState.getMatrix().get(rid));
                room.updateEnemies();
            }

            for (Room room : dungeon.getAllRooms()) {
                if(room.specificID == initialGameState.getInitialRoom()){
                    RealTimeController.room = room;
                    break;
                }
            }


            currentTileType = initialGameState.getCurrentTileType();
            playerPosition = initialGameState.getPlayerPosition();

            //Look more into these
            loadEnemies();


            gameWindow.updateRoomGraphics(room);
            gameWindow.updateGraphics();

            player.loadStats(initialGameState.getPlayerStats());
            gameInfo.loadStats(initialGameState.getGameStats());

            gameInfo.setChestsOpened(player.getChestsOpened());
            gameInfo.setHitsTaken(player.getHits());
            gameInfo.setPoints(player.getPoints());
    }
    /*
    ID and room size for every room in the dungeon is saved in a hashmap.
     */
    public void setUpMatrixCounter(ArrayList<Room> rooms){
        for(Room room : rooms){
            int[][] roomCounter = new int[room.getHeight()][room.getWidth()];
            UUID id = room.specificID;
            matrixCounter.put(id, roomCounter);
        }
    }

    public void setUUID(UUID id){
        this.RoomID = id;
    }

    public void loadEnemies(){
        try {
            enemyCoordinatesList = room.getEnemies();
            for (Point e : enemyCoordinatesList){
                if (room.matrix[e.getY()][e.getX()] == 3){   //X and Y reversed here
                    enemyEntityList.add(new Enemy(3,new Point(e.getY(),e.getX())));
                }
                else if (room.matrix[e.getY()][e.getX()] == 5){
                    enemyEntityList.add(new Enemy(5,new Point(e.getY(),e.getX())));
                }
            }

        } catch (IndexOutOfBoundsException e){
        }
    }
    public int getAmountOfEnemies(){
        int count = 0;
        for (Room room :
                dungeon.getAllRooms()) {
            for (int i = 0; i < room.matrix.length; i++) {
                for (int j = 0; j < room.matrix[0].length; j++) {
                    if (room.matrix[i][j] == 3 || room.matrix[i][j] == 7){
                        count++;
                    }
                }
            }
        }
        System.out.println(count);
        return count;
    }
    public int getAmountOfTreasures(){
        int count = 0;
        for (Room room :
                dungeon.getAllRooms()) {
            for (int i = 0; i < room.matrix.length; i++) {
                for (int j = 0; j < room.matrix[0].length; j++) {
                    if (room.matrix[i][j] == 2){
                        count++;
                    }
                }
            }
        }
        System.out.println(count);
        return count;
    }
    public void setGameWindow(GameWindow g){
        gameWindow = g;
    }

    public void enterDoor(){

        if (currentTileType == 4){ //checks that the player is standing on a door tile
            saveDungeonState();
            room.matrix[playerPosition.getX()][playerPosition.getY()] = 4; // re-sets the position of the player in the old room to a door before leaving
            for(RoomEdge r : connections){
                if (r.from.equals(this.room) || r.to.equals(this.room)) //checks that the edge we are investigating belongs to the room we are currently in,
                    //this is a problematic solution since there is a posibility our "from position" in room one is identical to a "fromPosition" in the room we want to go to
                    if(comparePositions(playerPosition, r.fromPosition)){
                        this.room = r.to;
                        gameWindow.updateRoomGraphics(this.room);
                        connections = dungeon.network.incidentEdges(this.room);
                        moveRoom(new Point(r.toPosition.getY(), r.toPosition.getX()));
                        gameInfo.setDoorsOpened(gameInfo.getDoorsOpened() + 1);
                    }
                    else if (comparePositions(playerPosition, r.toPosition)){
                        this.room = r.from;
                        gameWindow.updateRoomGraphics(this.room);
                        connections = dungeon.network.incidentEdges(this.room);
                        moveRoom(new Point(r.fromPosition.getY(), r.fromPosition.getX()));
                        gameInfo.setDoorsOpened(gameInfo.getDoorsOpened() + 1);
                    }
            }
            arrowList.clear();
            gameWindow.updateGraphics();
        }
        //UPDATE ENEMYLIST TO BE CURRENT ROOM
        room.localConfig.getWorldCanvas().drawMapVisible();
        room.removeBitMap();
        RoomID = room.specificID;
        enemyEntityList.clear();
        loadEnemies();
    }
    public HashMap<UUID, int[][]> getMatrixCounter(){
        return matrixCounter;
    }
    //@fixme the X and Y orientation within the room matrix is reversed compared to other stuctures such as the MutableNetwork
    public boolean comparePositions(Point p1, Point p2){
        if(p1.getX() == p2.getY() && p1.getY() == p2.getX()){
            return true;
        }
        return  false;
    }

    public void moveRoom(Point p){
        room.matrix[p.getX()][p.getY()] = 6;
        currentTileType = 4;
        System.out.println("updated player position from " + playerPosition.getX() +" "+ playerPosition.getY() + " to " + p.getX() + " " + p.getY());
        playerPosition = p;

    }

    public void updatePlayerPosition(Move direction) {

        Point p = null;
        try {
            switch (direction) {
                case LEFT:
                    p = new Point(playerPosition.getX(), playerPosition.getY() - 1);
                    break;
                case RIGHT:
                    p = new Point(playerPosition.getX(), playerPosition.getY() + 1);
                    break;
                case UP:
                    p = new Point(playerPosition.getX() - 1, playerPosition.getY());
                    break;
                case DOWN:
                    p = new Point(playerPosition.getX() + 1, playerPosition.getY());
                    break;
            }
            player.setDirection(direction);

            saveDungeonState();
            EventRouter.getInstance().postEvent(new PlayerMovementEvent(player, room.matrix, p));

            if(player.canMove()) {

                if(playerDestroyTile) {

                    room.matrix[playerPosition.getX()][playerPosition.getY()] = 0; // sets old position to display a floor tile
                    playerDestroyTile = false;

                }
                else {

                    room.matrix[playerPosition.getX()][playerPosition.getY()] = currentTileType; // sets his old position to display the tile he stood on earlier
                }
                currentTileType = room.matrix[p.getX()][p.getY()]; //saves the tileType just stepped on
                playerPosition = p;
                room.matrix[playerPosition.getX()][playerPosition.getY()] = 6;

                updateGameInfo();

                gameWindow.updateGraphics();

            } else {
                player.setMove(true);
            }

            checkArrows();

            matrixCounter.get(RoomID)[playerPosition.getX()][playerPosition.getY()]++;
            playerDestroyTile = player.destroyTile();
            player.setDestroyTile(false);

            if (isGameFinished()){ ShowEndGameDialog();}

        } catch (ArrayIndexOutOfBoundsException e){
        }


    }

    public void playerAttack() {
        Point target = null;
        switch (player.getDirection() ) {
            case UP:
                target = new Point(playerPosition.getX() - 1 , playerPosition.getY() );
                break;
            case RIGHT:
                target = new Point(playerPosition.getX()  , playerPosition.getY() + 1);
                break;
            case DOWN:
                target = new Point(playerPosition.getX() + 1 , playerPosition.getY() );
                break;
            case LEFT:
                target = new Point(playerPosition.getX()  , playerPosition.getY() - 1);
                break;
        }

        Iterator<Enemy> enemyIterator = enemyEntityList.iterator();

        while(enemyIterator.hasNext()) {
            Enemy i = enemyIterator.next();

            if(i.getPosition().getX() == target.getX() && i.getPosition().getY() == target.getY()) {
                i.takeDamage(player.getCloseAttack());
                gameInfo.setAttacksMade(gameInfo.getAttacksMade() + 1);
                if (i.seeIfDead()) {
                    gameInfo.setEnemiesKilled(gameInfo.getEnemiesKilled() + 1);
                    player.givePoints(20);
                    enemyIterator.remove();
                    enemyEntityList.remove(i);
                    room.matrix[target.getX()][target.getY()] = 0;

                    Iterator<Point> pointIterator = room.getEnemies().iterator();

                    while(pointIterator.hasNext()) {
                        Point pi = pointIterator.next();

                        if (pi.getX() == target.getY() && pi.getY() == target.getX()) {
                            pointIterator.remove();
                            room.getEnemies().remove(pi);
                        }
                    }

                }
            }
        }

        updateGameInfo();
        gameWindow.updateGraphics();
        if (isGameFinished()){ ShowEndGameDialog();}
    }

    public void saveHeatMapSerializable(){
        try {
            serialization.Serialize(matrixCounter);
            HashMap<UUID, int[][]> temp = serialization.DeSerialize();
            for (UUID uuid : temp.keySet()) {
                System.out.println(uuid.toString());
            }
        }
        catch(Exception e){e.printStackTrace();}
    }

    /*
    Sets percentage of steps on specific tiles in relation to other tiles in the heatmap.
     */
    public void setMatrixPercentage() {
        int max = 0;
        for (int[][] roomCounter : matrixCounter.values()) {
            for (int i = 0; i < roomCounter.length; i++) {
                for (int j = 0; j < roomCounter[i].length; j++) {
                    if (roomCounter[i][j] > max){
                        max = roomCounter[i][j];
                    }
                }
            }
        }
        for (int[][] roomCounter : matrixCounter.values()) {
            for (int i = 0; i < roomCounter.length; i++) {
                for (int j = 0; j < roomCounter[i].length; j++) {
                    int steps = roomCounter[i][j];
                    double percentage = Math.round(((steps/max) * 100) / 100);
                }
            }
        }
    }

    public void moveObject(Point fromPosition, Move direction) {
        Point toPosition = null;
        try {
            switch (direction) {
                case LEFT:
                    toPosition = new Point(fromPosition.getX(), fromPosition.getY() - 1);
                    break;
                case RIGHT:
                    toPosition = new Point(fromPosition.getX(), fromPosition.getY() + 1);
                    break;
                case UP:
                    toPosition = new Point(fromPosition.getX() - 1, fromPosition.getY());
                    break;
                case DOWN:
                    toPosition = new Point(fromPosition.getX() + 1, fromPosition.getY());
                    break;
            }

            if (room.matrix[fromPosition.getX()][fromPosition.getY()] == 3) { //enemy specific behaviour
                //change direction if it hits a wall or other enemy
                if (room.matrix[toPosition.getX()][toPosition.getY()] == 1 || room.matrix[toPosition.getX()][toPosition.getY()] == 3 ||
                        room.matrix[toPosition.getX()][toPosition.getY()] == 4){
                    Enemy e = getEnemyFromPosition(fromPosition);
                    e.setDirection(e.getDirection().opposite());
                    //move if terrain ahead is floor
                }else if (room.matrix[toPosition.getX()][toPosition.getY()] == 0){
                    room.matrix[toPosition.getX()][toPosition.getY()] = room.matrix[fromPosition.getX()][fromPosition.getY()];
                    room.matrix[fromPosition.getX()][fromPosition.getY()] = 0;
                    updateEnemyList(fromPosition, toPosition);
                }
            }
            //if not an enemy just do regular floor check
           else if (room.matrix[toPosition.getX()][toPosition.getY()] == 0) { //checks that the tile the object tries to move to is a floor tile
                room.matrix[toPosition.getX()][toPosition.getY()] = room.matrix[fromPosition.getX()][fromPosition.getY()];
                room.matrix[fromPosition.getX()][fromPosition.getY()] = 0;
            }
            gameWindow.updateGraphics();
        }catch (ArrayIndexOutOfBoundsException e){
            if (room.matrix[fromPosition.getX()][fromPosition.getY()] == 3) {//change direction if it hits edge of array
                Enemy en = getEnemyFromPosition(fromPosition);
                en.setDirection(en.getDirection().opposite());
            }
        }
    }

    public Enemy getEnemyFromPosition(Point position){
        for(Enemy e : enemyEntityList){
            if (position == e.getPosition()){
                return e;
            }
        }
        return null;
    }

    public void updateEnemyList(Point fromPosition, Point toPosition){
        for (int i = 0; i < room.getEnemies().size(); i++){
            if (comparePositions(fromPosition,new Point(enemyCoordinatesList.get(i).getX(), enemyCoordinatesList.get(i).getY()))){  //THE PROBLEM IS HERE; THIS COMPARISON DOESNT CHECK OUT
                enemyCoordinatesList.get(i).setX(toPosition.getY()); //everything is reversed because of aids
                enemyCoordinatesList.get(i).setY(toPosition.getX());
                room.getEnemies().get(i).setY(toPosition.getX());
                room.getEnemies().get(i).setX(toPosition.getY());
                enemyEntityList.get(i).setPosition(new Point(toPosition.getX(), toPosition.getY()));
              //  System.out.println("UEL: " + r.getEnemies().get(i).getY() + " "+ r.getEnemies().get(i).getX());
            }
        }
    }
    public Point PlayerPosition(){
        return playerPosition;
    }
    public void performEnemyAction(){
        for(Enemy e : enemyEntityList){
            if(!checkPathForPlayer(e.getPosition(), e)){ // if there is no player in line of sight, move enemy
                moveObject(e.getPosition(), e.getDirection());
            }
            else {                //if player is in enemy line of sight, shot.
                if(!e.getHasShot()) { // if the enemy already has fired an arrow, it has to wait for the first arrow to be removed before firing again.
                    createArrow(e.getPosition(), e.getDirection(), e);
                    e.setHasShot(true);
                }
            }
        }
    }

    public void createArrow(Point position, Move direction, Enemy owner) {
        arrowList.add(new Arrow(this, new Point(position.getX(), position.getY()), direction, owner, player));
        System.out.println("Creating arrow, " + position.toString() + " " + direction);
    }

    public void removeArrow(Arrow arrow) {
        arrowList.remove(arrow);
    }

    /*
    Checks is the arrow hits a wall, player, enemy or edge of the map. If it does the arrow is removed.
     */
    public void checkArrows() {
        Iterator<Arrow> iter = arrowList.iterator();

        while (iter.hasNext()) {
            Arrow a = iter.next();

            if(a.getPosition().getX() < 0 || a.getPosition().getX() >= room.getRowCount()) {
                iter.remove();
                a.destroyArrow();
            }

            else if(a.getPosition().getY() < 0 || a.getPosition().getY() >= room.getColCount()) {
                iter.remove();
                a.destroyArrow();
            }

            else {
                a.checkTile(room.matrix, iter);
            }
        }

        gameWindow.updateGraphics();
    }

    public void updateArrows() {

        for (Arrow a : arrowList) {

            a.move(a.getDirection());

        }

        checkArrows();
    }
    public ArrayList<Arrow> getArrowList () {
        return arrowList;
    }

    /*
    Method used to check enemies line of sight, to see if the can see the player
     */
    public boolean checkPathForPlayer(Point p, Enemy enemy){

        boolean wallReached = false;
        boolean playerFound = false;
        int i = 1;
        Move direction = enemy.getDirection();

        try{
            switch (direction){
                case LEFT:
                    while(!wallReached || !playerFound){
                        if(room.matrix[p.getX()][p.getY() - i] == 6){
                            //shoot etc.
                            playerFound = true;
                            return playerFound;
                        } else if(room.matrix[p.getX()][p.getY() - i] == 1){
                            wallReached = true;
                            break;
                        }
                        i++;
                    }
                    break;
                case RIGHT:
                    while(!wallReached || !playerFound){
                        if(room.matrix[p.getX()][p.getY() + i] == 6){
                            //shoot etc.
                            playerFound = true;
                            return playerFound;

                        } else if(room.matrix[p.getX()][p.getY() + i] == 1){
                            wallReached = true;
                            break;
                        }
                        i++;
                    }
                    break;
                case UP:
                    while(!wallReached || !playerFound){
                        if(room.matrix[p.getX() - i][p.getY()] == 6) {
                            //shoot etc.
                            playerFound = true;
                            return playerFound;

                        }
                        else if(room.matrix[p.getX() - i][p.getY()] == 1){
                            wallReached = true;
                            break;
                        }
                        i++;
                    }
                    break;
                case DOWN:
                    while(!wallReached || !playerFound){
                        if(room.matrix[p.getX() + i][p.getY()] == 6){
                            //shoot etc.
                            playerFound = true;
                            return playerFound;

                        } else if(room.matrix[p.getX() + i][p.getY()] == 1){
                            wallReached = true;
                            break;
                        }
                        i++;
                    }
                    break;
            }
        }catch (ArrayIndexOutOfBoundsException ex){
            wallReached = true;
        }
        return playerFound;
    }

    /*
    Check player states and info from GameRules.json if the game end goals has been met.
     */
    public boolean isGameFinished(){
        try {
            ConfigurationUtility config = new ConfigurationUtility("config/GameRules.json", true);
            boolean collectAllTreasures = config.getBoolean("GameEndGoal.collectAllTreasure");
            boolean reachPoints = config.getBoolean("GameEndGoal.reachPointsValue");
            boolean killAllEnemies = config.getBoolean("GameEndGoal.killAllEnemies");
            int pointsGoal = config.getInt("GameEndGoal.pointsAmount");

            if (collectAllTreasures && reachPoints && killAllEnemies){//ALL THREE
                if (player.getChestsOpened() >= treasuresCount && player.getPoints() >= pointsGoal && gameInfo.getEnemiesKilled() >= enemiesCount){

                    System.out.println("You finished the game!");
                    return true;
                }
            }
            else if (collectAllTreasures && reachPoints ){ //TREASURES AND POINTS
                if (player.getChestsOpened() >= treasuresCount && player.getPoints() >= pointsGoal){

                    System.out.println("You finished the game!");
                    return true;
                }
            }
            else if (collectAllTreasures && killAllEnemies){ //TREASURES AND ENEMIES
                if (player.getChestsOpened() >= treasuresCount && gameInfo.getEnemiesKilled() >= enemiesCount){

                    System.out.println("You finished the game!");
                    return true;
                }
            }
            else if (reachPoints && killAllEnemies){ //POINTS AND ENEMIES
                if (player.getPoints() >= pointsGoal && gameInfo.getEnemiesKilled() >= enemiesCount){
                    //TREASURES AND ENEMIES
                    System.out.println("You finished the game!");
                    return true;
                }
            }
            else if (collectAllTreasures){
                if (player.getChestsOpened() >= treasuresCount){
                    //TREASURES
                    System.out.println("You finished the game!");
                    return true;
                }
            }
            else if (reachPoints){
                if (player.getPoints() >= pointsGoal){
                    //POINTS
                    System.out.println("You finished the game!");
                    return true;
                }
            }
            else if (killAllEnemies){
                if (gameInfo.getEnemiesKilled() >= enemiesCount){
                    //ENEMIES
                    System.out.println("You finished the game!");
                    return true;
                }
            }

        }catch (MissingConfigurationException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void ShowEndGameDialog(){
        if (!autoRestart) {
            int choice = JOptionPane.showOptionDialog(null,
                    "Do you want to restart the dungeon?",
                    "You finished the game!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, null, null);

            // interpret the user's choice
            if (choice == JOptionPane.YES_OPTION) {
                restartGame();
            }
        }
        else{
            restartGame();
        }
    }
    public void updateGameInfo(){
        gameInfo.setStepsTaken(gameInfo.getStepsTaken() + 1);
        gameInfo.setChestsOpened(player.getChestsOpened());
        gameInfo.setHitsTaken(player.getHits());
        gameInfo.setPoints(player.getPoints());
    }
    public void showGameInfo(){
        System.out.println(gameInfo.toString());
    }
    public GameInfo getGameInfo(){
        return gameInfo;
    }
    public void setAutoRestart(boolean restart){
        autoRestart = restart;
    }
}

