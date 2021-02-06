package util.eventrouting.events;

import game.Dungeon;
import util.eventrouting.PCGEvent;

public class StartDungeon extends PCGEvent {
    private int dungeonID;
    private Dungeon dungeon;
    private int width;
    private int height;

    public StartDungeon(Dungeon payload, int dungeonID, int width, int height)
    {
        this.dungeon = payload;
        this.dungeonID = dungeonID;
        this.width = width;
        this.height = height;

        setPayload(payload);
    }

    public int getDungeonID()
    {
        return dungeonID;
    }

    public Dungeon getDungeon()
    {
        return dungeon;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }
}
