package main.java.environment;

import main.java.underwater.UWContext;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.location.Location;
import yaes.world.physical.map.IAccessibilityChecker;
import yaes.world.physical.map.IMap;

public class MapLocationAccessibility implements IAccessibilityChecker {

    public boolean isAccessible(IMap imap, Location location) {
        EnvironmentModel environment = (EnvironmentModel) imap;
        if (Double.valueOf(
                (String) environment.getPropertyAt(UWContext.PROP_OBSTACLE,
                        location.getX(), location.getY())) > 0) {
            return false;
        }
        return true;
    }

}
