package main.java.agents.pathplanner;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import main.java.agents.UWAgent;
import main.java.agents.UWMobileAgent;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.PPMGenerator;
import yaes.world.physical.path.PPMTraversal;
import yaes.world.physical.path.PlannedPath;
import yaes.world.physical.path.ProgrammedPathMovement;

/**
 * The greedy path planner for the mobile agent
 * 
 * @author SaadKhan
 *
 */
public class GreedyPlanner implements iAgentPathPlanner {
    private SortedSet<Map.Entry<UWAgent, Double>> voiStateMap =
            new TreeSet<Map.Entry<UWAgent, Double>>(
                    new Comparator<Map.Entry<UWAgent, Double>>() {
                        public int compare(Map.Entry<UWAgent, Double> e1,
                            Map.Entry<UWAgent, Double> e2) {
                            return e2.getValue().compareTo(e1.getValue());
                        }
                    });

    @Override
    public PPMTraversal planPath(UWMobileAgent agent, PlannedPath plannedpath) {
        Location localDestination =
                this.voiStateMap.first().getKey().getNode().getLocation();
        double speed = agent.getSinkSpeed();
        double speedStdDev = 0.0;
        ProgrammedPathMovement ppm = PPMGenerator.followPathWithConstantSpeed(
                speed, speedStdDev, agent.getRand(),
                agent.getNode().getLocation(), localDestination);

        return new PPMTraversal(ppm, 0);
    }

    public SortedSet<Map.Entry<UWAgent, Double>> getVoiStateMap() {
        return this.voiStateMap;
    }

    public void setVoiStateMap(
        SortedSet<Map.Entry<UWAgent, Double>> voiStateMap) {
        this.voiStateMap = voiStateMap;
    }

}
