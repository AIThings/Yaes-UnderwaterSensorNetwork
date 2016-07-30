package main.java.agents.pathplanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import main.java.agents.ProgressState;
import main.java.agents.UWMobileAgent;
import main.java.pathplanning.AStarLearning;
import yaes.sensornetwork.model.SensorNode;
import yaes.world.physical.path.PPMTraversal;
import yaes.world.physical.path.PlannedPath;
import yaes.world.physical.path.ProgrammedPathMovement;

public class AStarPlanner implements iAgentPathPlanner {

    @Override
    public PPMTraversal planPath(UWMobileAgent agent, PlannedPath plannedpath) {

        SensorNode src = agent.getSensorWorld().getSensorNodes().get(0);
        SensorNode dest = agent.getSensorWorld().getSensorNodes()
                .get(agent.getSensorWorld().getSensorNodes().size() - 1);

        AStarLearning learning = new AStarLearning(agent, src, dest);
        HashMap<ProgressState, ProgressState> pathMap =
                learning.returnStatePath();
        ArrayList<ProgressState> path = new ArrayList<ProgressState>();

        // backtraverse from goal to start node
        ProgressState s = new ProgressState(dest.getName(), dest.getLocation());
        path.add(s);
        do {
            path.add(pathMap.get(path.get(path.size() - 1)));
        } while (pathMap.get(path.get(path.size() - 1)).getStateName() != src
                .getName());

        path.add(new ProgressState(src.getName(), src.getLocation()));
        Collections.reverse(path);
        // path.addAll(pathMap.values());
        for (ProgressState state : path)
            if (!(state == null))
                agent.getPlannedPath().addLocation(state.getLocation());

        plannedpath.setSource(plannedpath.getLocationAt(0));
        plannedpath.setDestination(
                plannedpath.getLocationAt(plannedpath.getPathSize() - 1));
        ProgrammedPathMovement ppm = new ProgrammedPathMovement();
        ppm.addSetLocation(plannedpath.getLocationAt(0));
        ppm.addFollowPath(agent.getPlannedPath(), agent.getSinkSpeed());
        return new PPMTraversal(ppm, 0);
    }

}
