package finder.patterns;

import java.util.ArrayList;
import java.util.List;

import finder.geometry.Geometry;
import game.Room;

/**
 * The Pattern class is used to describe dungeon game design patterns.
 * 
 * @author Johan Holmberg
 * @author Alexander Baldwin
 */
public abstract class Pattern {
	
	protected Geometry boundaries = null;
	protected Room room = null;
	public boolean pathTowardsDeadEnd = true;
	
	/**
	 * Searches a map for instances of this pattern and returns a list of found
	 * instances.
	 * 
	 * @param room The map to search for patterns in.
	 * @param boundary A boundary in which the pattern is searched for.
	 * @return A list of found instances.
	 */
	public static List<Pattern> matches(Room room, Geometry boundary) {
		return new ArrayList<Pattern>();
	}

	/**
	 * Returns the geometry of this pattern.
	 * 
	 * @return The correspoinding geometry.
	 */
	public Geometry getGeometry() {
		return boundaries;
	}
	
	/**
	 * Returns a measure of the quality of this pattern
	 *  
	 * @return A number between 0.0 and 1.0 representing the quality of the pattern (where 1 is best)
	 */
	public double getQuality(){
		return 1.0;
	}
}
