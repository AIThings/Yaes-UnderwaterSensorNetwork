package agents.pathplanner;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import pathplanning.Learning.Action;
import agents.ProgressState;
import agents.UWMobileAgent;
import pathplanning.QLearning;
import yaes.sensornetwork.model.SensorNode;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.PPMTraversal;
import yaes.world.physical.path.PlannedPath;
import yaes.world.physical.path.ProgrammedPathMovement;

public class QLearningPlanner implements iAgentPathPlanner, Serializable {
	private static final long serialVersionUID = 1L;
    private QLearning qLearning;

    @Override
    public PPMTraversal planPath(UWMobileAgent agent, PlannedPath plannedpath) {
        SensorNode src = agent.getSensorWorld().getSensorNodes().get(0);
        SensorNode dest = agent.getSensorWorld().getSensorNodes()
                .get(agent.getSensorWorld().getSensorNodes().size() - 1);

        this.qLearning = new QLearning(agent, src, dest);
        this.qLearning.initializeQLearningStates();

        agent.setState(new ProgressState(src.getName(), src.getLocation()));

        int episodes = 1000;
        int j = 0;
        do {
            for (int i = 0; i < episodes; i++) {
                try {
                    this.qLearning.learnQTables(agent);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (qLearning.isReachedGoalState()) { // if reached Goal
                                                      // state then reset
                    agent.setState(qLearning.getInitialState());
                    agent.getNode().setLocation(
                            qLearning.getInitialState().getLocation());
                    qLearning.setReachedGoalState(false);
                    break;
                }
            }
            j++;
        } while (j < 500);
        PPMTraversal ppmtraversal = null;
        try {
            ppmtraversal = enableQlearning(agent);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ppmtraversal;
    }

    public PPMTraversal enableQlearning(UWMobileAgent agent) throws Exception {
        // TODO: Set the starting point for simulation testing
        SensorNode initial = agent.getSensorWorld().getSensorNodes().get(80);

        ProgrammedPathMovement ppm = new ProgrammedPathMovement();
        agent.setState(
                new ProgressState(initial.getName(), initial.getLocation()));
        agent.getNode().setLocation(initial.getLocation());
        ppm.addSetLocation(agent.getState().getLocation());
        do {
            agent.getPlannedPath().addLocation(agent.getState().getLocation());
            SortedSet<Map.Entry<Action, Double>> sortedActionSet =
                    new TreeSet<Map.Entry<Action, Double>>(
                            new Comparator<Map.Entry<Action, Double>>() {
                                @Override
                                public int compare(Map.Entry<Action, Double> e1,
                                    Map.Entry<Action, Double> e2) {
                                    return e2.getValue()
                                            .compareTo(e1.getValue());
                                }
                            });
            ArrayList<Action> actions =
                    this.qLearning.getStateActionTable().get(agent.getState()); // get
                                                                                // the
                                                                                // valid
                                                                                // actions
                                                                                // possible
                                                                                // in
                                                                                // current
                                                                                // state
            double value = 0.0;
            for (Action action : actions) {
                Map.Entry<ProgressState, Action> entry =
                        new AbstractMap.SimpleEntry<ProgressState, Action>(
                                agent.getState(), action);
                value = this.qLearning.getQTable().get(entry);
                Map.Entry<Action, Double> actionValueEntry =
                        new AbstractMap.SimpleEntry<Action, Double>(action,
                                value);
                // TextUi.println("State: "+this.getState().getStateName()
                // + "\t Action: " + actionValueEntry.getKey()
                // + "\t Q-Value: " +actionValueEntry.getValue());
                sortedActionSet.add(actionValueEntry);
            }

            boolean val = agent.getRand().nextInt(10) == 0;
            Action bestAction = null;
            if (!val)
                bestAction = sortedActionSet.first().getKey();
            else {
                Collections.shuffle(actions, agent.getRand());
                bestAction = actions.get(1);
            }
            Location newLocation = this.qLearning.neighborActionTable(agent)
                    .get(bestAction).getNode().getLocation();
            String stateName = this.qLearning.neighborActionTable(agent)
                    .get(bestAction).getNode().getName();
            ProgressState nextState = new ProgressState(stateName, newLocation);
            // TextUi.println("State: " +this.getState().getStateName()+ "\t
            // Action: " +bestAction+ "\t Next-state: "
            // +nextState.getStateName());
            agent.setState(nextState); // Changing agent's current state to the
                                       // next state
        } while (!agent.getState().equals(qLearning.getGoalState()));

        agent.getPlannedPath().addLocation(agent.getState().getLocation()); // adding
        // the goal
        // state
        // location

        agent.getPlannedPath()
                .setSource(agent.getPlannedPath().getLocationAt(0));
        agent.getPlannedPath().setDestination(agent.getPlannedPath()
                .getLocationAt(agent.getPlannedPath().getPathSize() - 1));
        ppm.addFollowPath(agent.getPlannedPath(), agent.getSinkSpeed());
        return new PPMTraversal(ppm, 0);
    }
}
