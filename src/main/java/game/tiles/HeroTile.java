package game.tiles;

import finder.geometry.Point;
import game.Room;
import game.Tile;
import game.TileTypes;
import gui.controls.Brush;
import gui.controls.Drawer;
import gui.controls.InteractiveMap;
import gui.controls.Brush.BrushUsage;
import gui.controls.Brush.NeighborhoodStyle;

public class HeroTile extends Tile {

	public HeroTile()
	{
		m_type = TileTypes.HERO;
		setBrushUsage();
	}
	
	public HeroTile(Point p, TileTypes type)
	{
		super(p, type);
		setBrushUsage();
	}
	
	public HeroTile(int x, int y, TileTypes type)
	{
		super(x, y, type);
		setBrushUsage();
	}
	
	public HeroTile(Point p, int typeValue)
	{
		super(p, typeValue);
		setBrushUsage();
	}
	
	public HeroTile(int x, int y, int typeValue)
	{
		super(x, y, typeValue);
		setBrushUsage();
	}
	
	public HeroTile(Tile copyTile)
	{
		super(copyTile);
		m_type = TileTypes.HERO;
		setBrushUsage();
	}
	
	@Override
	public void PaintTile(Point currentCenter, Room room, Drawer drawer, InteractiveMap interactiveCanvas)
	{
		interactiveCanvas.getCell(currentCenter.getX(), currentCenter.getY()).
		setImage(interactiveCanvas.getImage(m_type, interactiveCanvas.scale));
	}
	
	@Override
	public Brush modification(Brush brush)
	{
		brush.setImmutable(true);
		return brush;
	}
	
	@Override
	protected void setBrushUsage()
	{
		super.setBrushUsage();
		SetImmutable(true);
	}
	
	@Override
	public Tile copy()
	{
		return new HeroTile(this);
	}
	
}
