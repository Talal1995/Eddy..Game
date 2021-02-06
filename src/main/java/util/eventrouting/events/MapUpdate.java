package util.eventrouting.events;

import game.Room;
import util.eventrouting.PCGEvent;

/**
 * This event is used to post info on new maps.
 * 
 * @author Alexander Baldwin, Malmö University
 * @author Johan Holmberg, Malmö University
 */
public class MapUpdate extends AlgorithmEvent {
	
	/**
	 * Creates a new event.
	 * 
	 * @param payload The map to be sent.
	 */
	public MapUpdate(Room payload) {
		setPayload(payload);
	}
}
