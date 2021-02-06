package gui.utils;

import util.eventrouting.EventRouter;
import util.eventrouting.events.ChangeCursor;
import util.eventrouting.events.EndRoom;
import util.eventrouting.events.InitialRoom;

public class DungeonDrawer 
{
	private static DungeonDrawer instance = null;
	
	protected ShapeBrush brush;
//	protected List<ShapeBrush> allBrushes
	
	public enum DungeonBrushes
	{
		MOVEMENT,
		ROOM_CONNECTOR,
		PATH_FINDING,
		INITIAL_ROOM,
		END_ROOM
	}
	
	public DungeonBrushes dungeonBrush;
	
	private DungeonDrawer()
	{
		brush = new MoveElementBrush();
		dungeonBrush = DungeonBrushes.MOVEMENT;
	}
	
	public static DungeonDrawer getInstance()
	{
		if(instance == null)
		{
			instance = new DungeonDrawer();
		}
		
		return instance;
	}
	
	public void changeBrushTo(DungeonBrushes difBrush)
	{
		dungeonBrush = difBrush;
		changeBrush();
	}
	
	public void nextBrush()
	{
//		dungeonBrush = DungeonBrushes.values()[dungeonBrush.ordinal() + 1 % DungeonBrushes.ROOM_CONNECTOR];
		
		switch(dungeonBrush)
		{
			case MOVEMENT:
				dungeonBrush = DungeonBrushes.ROOM_CONNECTOR;
				break;
			case ROOM_CONNECTOR:
				dungeonBrush = DungeonBrushes.PATH_FINDING;
				break;
			case PATH_FINDING: 
				dungeonBrush = DungeonBrushes.MOVEMENT;
				break;
		}
		
		changeBrush();
	}
	
	public void changeToConnector()
	{
		brush = new RoomConnectorBrush();
	}
	
	public void changeToMove()
	{
		brush = new MoveElementBrush();
	}
	
	public ShapeBrush getBrush()
	{
		return brush;
	}
	
	private void changeBrush()
	{
		EventRouter.getInstance().postEvent(new ChangeCursor(""));
		switch(dungeonBrush)
		{
			case MOVEMENT:
				brush = new MoveElementBrush();
				break;
			case ROOM_CONNECTOR:
				brush = new RoomConnectorBrush();
				break;
			case PATH_FINDING: 
				brush = new PathBrush();
				break;
			case INITIAL_ROOM:
				brush = new SetImportantRoom(new InitialRoom(null, null));
				EventRouter.getInstance().postEvent(new ChangeCursor("/graphics/dungeonbrushes/heroBrush.png"));
				break;
			case END_ROOM:
				brush = new SetImportantRoom(new EndRoom(null, null));
				break;
		}
	}
}
