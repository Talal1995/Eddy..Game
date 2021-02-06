package project;

import util.Point;

import java.util.HashMap;
import java.util.UUID;

public class GameState {

    private HashMap<UUID, int[][]> matrix;
    private Point playerPosition = new Point(0,0);
    private int currentTileType = 0;
    private UUID initialRoom;
    private int[] gameStats;
    private int[] playerStats;

    public GameState(HashMap<UUID, int[][]> matrix, UUID initialRoom, Point playerPosition, int currentTileType, int[] gameStats, int[] playerStats) {
        this.initialRoom = initialRoom;
        this.matrix = matrix;
        this.playerPosition = playerPosition;
        this.currentTileType = currentTileType;
        this.gameStats = gameStats;
        this.playerStats = playerStats;
    }

    public int[] getGameStats() {
        return gameStats;
    }

    public void setGameStats(int[] gameStats) {
        this.gameStats = gameStats;
    }

    public int[] getPlayerStats() {
        return playerStats;
    }

    public void setPlayerStats(int[] playerStats) {
        this.playerStats = playerStats;
    }

    public HashMap<UUID, int[][]> getMatrix() {
        return matrix;
    }

    public void setMatrix(HashMap<UUID, int[][]> matrix) {
        this.matrix = matrix;
    }

    public Point getPlayerPosition() {
        return playerPosition;
    }

    public void setPlayerPosition(Point playerPosition) {
        this.playerPosition = playerPosition;
    }

    public int getCurrentTileType() {
        return currentTileType;
    }

    public void setCurrentTileType(int currentTileType) {
        this.currentTileType = currentTileType;
    }

    public UUID getInitialRoom() {
        return initialRoom;
    }

    public void setInitialRoom(UUID initialRoom) {
        this.initialRoom = initialRoom;
    }
}
