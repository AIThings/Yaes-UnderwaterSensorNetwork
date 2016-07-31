package agents.pathplanner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import agents.ProgressState;
import agents.UWMobileAgent;
import pathplanning.LawnMoverLearning;
import yaes.sensornetwork.model.SensorNode;
import yaes.world.physical.path.PPMTraversal;
import yaes.world.physical.path.PlannedPath;
import yaes.world.physical.path.ProgrammedPathMovement;

/**
 * The LawnMowerPlanner plans the path for an agent based on a simple zig zag
 * path planner
 * 
 * @author saadkhan
 *
 */
public class LawnMowerPlanner implements iAgentPathPlanner, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public PPMTraversal planPath(UWMobileAgent agent, PlannedPath plannedpath) {
		SensorNode src = null;
		SensorNode dest = null;
		int sensorNodeCount = agent.getSensorWorld().getSensorNodes().size();
		for (int counter = 0; counter < sensorNodeCount - 1; counter++) {
			SensorNode node = agent.getSensorWorld().getSensorNodes().get(counter);
			if (node.isEnabled()) {
				src = node;
				break;
			}
		}
		
		src = agent.getSensorWorld().getSensorNodes().get(12);
		
		for (int counter = 1; counter < sensorNodeCount - 1; counter++) {
			SensorNode node = agent.getSensorWorld().getSensorNodes().get(sensorNodeCount - counter);
			if (node.isEnabled()) {
				dest = node;
				break;
			}
		}

		dest = agent.getSensorWorld().getSensorNodes().get(99);
		
		LawnMoverLearning learning = new LawnMoverLearning(agent, src, dest);
		HashMap<ProgressState, ProgressState> pathMap = learning.returnStatePath();
		ArrayList<ProgressState> path = new ArrayList<ProgressState>();

		path.add(new ProgressState(src.getName(), src.getLocation()));

		pathMap.forEach((k, v) -> {
			path.add(k);
		});

		// Collections.reverse(path);
		// path.addAll(pathMap.values());
		for (ProgressState state : path)
			if (!(state == null))
				agent.getPlannedPath().addLocation(state.getLocation());

		plannedpath.setSource(plannedpath.getLocationAt(0));
		plannedpath.setDestination(plannedpath.getLocationAt(plannedpath.getPathSize() - 1));
		ProgrammedPathMovement ppm = new ProgrammedPathMovement();
		ppm.addSetLocation(plannedpath.getLocationAt(0));
		ppm.addFollowPath(agent.getPlannedPath(), agent.getSinkSpeed());

		return new PPMTraversal(ppm, 0);
	}

}
