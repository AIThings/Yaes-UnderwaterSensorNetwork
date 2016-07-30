package main.java.pathplanning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import main.java.sensorutils.DStarLite;
import main.java.sensorutils.MapLocationAccessibility;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.PlannedPath;

/**
 * Implements D* Lite Algorithm for path planning
 * 
 * @author Saad Arif
 * 
 */
public class DStarLitePP extends DStarLite<Location> implements Serializable {

    private static final long serialVersionUID = 1L;
    EnvironmentModel _map;

    /**
     * Instantiates A* search over the given map.
     * 
     * @param map
     * @param start
     * @param goal
     */
    public DStarLitePP(EnvironmentModel map, Location start, Location goal) {

        super(start, goal);
        _map = map;
    }

    /**
     * Returns a list of neighbors to the current location, excluding neighbors
     * that are not accessible due to obstacles or outside the map
     * 
     * @param s
     *            the current cell
     * @return a list of neighboring cells
     */
    protected Collection<Location> nbrs(Location s) {
        List<Location> nbrs = new ArrayList<Location>();
        Location loc;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0)
                    continue;
                loc = new Location(s.getX() + x, s.getY() + y);

                if (loc.getX() > _map.getXHigh() || loc.getX() < _map.getXLow()
                        || loc.getY() > _map.getYHigh()
                        || loc.getY() < _map.getYLow())
                    continue;
                MapLocationAccessibility mapAccess =
                        new MapLocationAccessibility();
                if (!mapAccess.isAccessible(_map, loc))
                    continue;
                nbrs.add(loc);
            }
        }
        return nbrs;
    }

    @Override
    protected Collection<Location> succ(Location s) {
        return nbrs(s);
    }

    @Override
    protected Collection<Location> pred(Location s) {
        return nbrs(s);
    }

    @Override
    protected double h(Location a, Location b) {
        return a.distanceTo(b);
    }

    @Override
    protected double c(Location a, Location b) {
        double cost = a.distanceTo(b);
        if (cost > 2)
            return Double.POSITIVE_INFINITY;
        else
            return cost;
    }

    /**
     * Invokes the D* Lite search algo
     * 
     * @return
     */
    public PlannedPath searchPath() {
        final long startTime = System.currentTimeMillis();
        // TextUi.println("D* Lite between " + super._start.toString() + " and "
        // + super._goal.toString());
        List<Location> locationList = plan();
        PlannedPath path = new PlannedPath(super._start, super._goal);
        if (locationList.isEmpty()) {
            path = null;
            // TextUi.println("No Path found...\ttime = "
            // + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
            return path;
        }
        for (Location l : locationList) {
            path.addLocation(l);
        }
        // TextUi.println("Path found...\ttime = "
        // + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
        // TextUi.println("Path cost = " + path.getPathLenght());
        // TextUi.println("Path: " + path);
        return path;
    }

    public void setStart(Location start) {
        updateStart(start);
    }
}
