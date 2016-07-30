package environment;

import underwater.UWContext;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.location.Location;
import yaes.world.physical.map.IAccessibilityChecker;
import yaes.world.physical.map.IMap;

public class MapLocationAccessibility implements IAccessibilityChecker {

	public boolean isAccessible(IMap imap, Location location) {
		EnvironmentModel environment = (EnvironmentModel) imap;
		String obstacleVal = environment.getPropertyAt(UWContext.PROP_OBSTACLE, location.getX(), location.getY()).toString();
		if (Double.valueOf(obstacleVal) > 0) {
			return false;
		}
		return true;
	}

}
