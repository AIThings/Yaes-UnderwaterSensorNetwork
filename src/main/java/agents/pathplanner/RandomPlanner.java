package agents.pathplanner;

import java.io.Serializable;
import java.util.Random;

import agents.UWMobileAgent;
import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.model.SensorNode;
import yaes.world.physical.path.PPMGenerator;
import yaes.world.physical.path.PPMTraversal;
import yaes.world.physical.path.PlannedPath;
import yaes.world.physical.path.ProgrammedPathMovement;

public class RandomPlanner implements iAgentPathPlanner, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public PPMTraversal planPath(UWMobileAgent agent, PlannedPath plannedpath) {
		Random rand = agent.getRand();
		int destNodeIndex = rand.nextInt(agent.getSensorWorld().getSensorNodes().size());
		SensorNode node = null;
		do {
			node = agent.getSensorWorld().getSensorNodes().get(destNodeIndex).getAgent().getNode();
			agent.setLocalDestination(node.getLocation());
		} while (!node.isEnabled());

		double speed = agent.getSinkSpeed();
		double speedStdDev = 0.0;
		ProgrammedPathMovement ppm = PPMGenerator.followPathWithConstantSpeed(speed, speedStdDev, rand,
				agent.getNode().getLocation(), agent.getLocalDestination());

		return new PPMTraversal(ppm, 0);
	}

	@Override
	public void move(UWMobileAgent agent) {
		agent.getNode().setLocation(agent.getIntendedMove());
	}
}
