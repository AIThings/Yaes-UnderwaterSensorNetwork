package agents.pathplanner;

import agents.UWMobileAgent;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.PPMTraversal;
import yaes.world.physical.path.PlannedPath;

/**
 * This interface defines the generic path planning required by all path
 * planners to implement. The default movement [move()] will change the sink
 * next location using PPMTravesal. But the default next location can be
 * overridden if required
 * 
 * @author SaadKhan
 *
 */

public interface iAgentPathPlanner {

    /**
     * The planned path for a mobile agent
     * 
     * @param agent
     * @param plannedpath
     * @return
     */
    public PPMTraversal planPath(UWMobileAgent agent, PlannedPath plannedpath);

    /**
     * The movement of a mobile agent at each update call of the simulator
     * 
     * @param agent
     */
    public default void move(UWMobileAgent agent) {
        Location currentLoc =
                agent.getPPMtraversal().getLocation(agent.getWorld().getTime());
        agent.getNode().setLocation(currentLoc);
        agent.getSensorWorld().move(agent.getSensorWorld().getSinkNode());
    }

}
