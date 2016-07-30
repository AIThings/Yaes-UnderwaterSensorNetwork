package main.java.agents.pathplanner;

import java.util.Random;

import main.java.agents.UWMobileAgent;
import yaes.world.physical.path.PPMGenerator;
import yaes.world.physical.path.PPMTraversal;
import yaes.world.physical.path.PlannedPath;
import yaes.world.physical.path.ProgrammedPathMovement;

public class RandomPlanner implements iAgentPathPlanner {

    @Override
    public PPMTraversal planPath(UWMobileAgent agent, PlannedPath plannedpath) {
        Random rand = agent.getRand();
        int destNodeIndex =
                rand.nextInt(agent.getSensorWorld().getSensorNodes().size());
        agent.setLocalDestination(agent.getSensorWorld().getSensorNodes()
                .get(destNodeIndex).getLocation());
        double speed = agent.getSinkSpeed();
        double speedStdDev = 0.0;
        ProgrammedPathMovement ppm = PPMGenerator.followPathWithConstantSpeed(
                speed, speedStdDev, rand, agent.getNode().getLocation(),
                agent.getLocalDestination());

        return new PPMTraversal(ppm, 0);
    }

    @Override
    public void move(UWMobileAgent agent) {
        agent.getNode().setLocation(agent.getIntendedMove());
    }
}
