package pathplanning;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agents.ProgressState;
import agents.UWMobileAgent;
import underwater.UWConstants;
import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.model.SensorNode;
import yaes.world.physical.location.Location;

/**
 * This class implements the Q-Learning Algorithm
 * 
 * @author Fahad Khan
 *
 */
public class QLearning extends Learning implements UWConstants, Serializable {
    private static final long serialVersionUID = -3782924162163737403L;

    // A location is a state and can be cross mapped

    private HashMap<Map.Entry<ProgressState, Action>, Double> QTable;
    private HashMap<Map.Entry<ProgressState, Action>, Double> RTable;

    private double gamma = 0.5; // discount factor
    private double alpha = 1.0; // learning rate

    private boolean reachedGoalState = false;

    public QLearning(AbstractSensorAgent agent, SensorNode src,
        SensorNode dest) {
        super(agent, src, dest);
        this.QTable = new HashMap<Map.Entry<ProgressState, Action>, Double>();
        this.RTable = new HashMap<Map.Entry<ProgressState, Action>, Double>();
    }

    /**
     * This method initialize the states. Every node initializes a new state by
     * providing a name and a location to each state
     */
    public void initializeQLearningStates() {
        ArrayList<ProgressState> states = new ArrayList<ProgressState>();
        for (SensorNode node : getAgent().getSensorWorld().getSensorNodes())
            if (node.isEnabled())
                states.add(
                        new ProgressState(node.getName(), node.getLocation())); // adding
                                                                                // the
                                                                                // sensor
                                                                                // nodes
                                                                                // as
                                                                                // states
        for (ProgressState state : states)
            this.getStateActionTable().put(state, this.validActions(state)); // only
                                                                             // allow
                                                                             // valid
                                                                             // actions
        this.initializeTableEntries();
        this.setRewards();

    }

    /**
     * Initializing the entries in the Q-Table
     * 
     * @param states
     */
    private void initializeTableEntries() {
        for (Map.Entry<ProgressState, ArrayList<Action>> entry : this
                .getStateActionTable().entrySet()) {
            ArrayList<Action> actions = entry.getValue();
            for (Action action : actions) {
                Map.Entry<ProgressState, Action> retValentry =
                        new AbstractMap.SimpleEntry<ProgressState, Action>(
                                entry.getKey(), action);
                this.addToQTable(retValentry, 0.0); // initialize the Q-Table
                                                    // with empty entries
                this.addToRTable(retValentry, 0.0); // initialize the R-Table
                                                    // with empty entries
            }
        }
    }

    /**
     * Setting the rewards for moving from current state into the final state
     * Get the neighboring agents of goal and set their action towards goal as
     * 100
     * 
     * @param states
     */
    private void setRewards() {
        List<AbstractSensorAgent> neighbors = getNeighbors(this.goalState);
        AbstractSensorAgent goalAgent = this.getAgent(goalState);

        for (AbstractSensorAgent neighbor : neighbors) {
            for (Map.Entry<ProgressState, ArrayList<Action>> entry : this
                    .getStateActionTable().entrySet()) {
                if (neighbor.getNode().getName() != entry.getKey()
                        .getStateName())
                    continue;

                double deltaX = goalAgent.getNode().getLocation().getX()
                        - neighbor.getNode().getLocation().getX();
                double deltaY = goalAgent.getNode().getLocation().getY()
                        - neighbor.getNode().getLocation().getY();
                Map.Entry<ProgressState, Action> retValentry = null;
                if (deltaX > 0)
                    retValentry =
                            new AbstractMap.SimpleEntry<ProgressState, Action>(
                                    entry.getKey(), Action.EAST);
                else if (deltaX < 0)
                    retValentry =
                            new AbstractMap.SimpleEntry<ProgressState, Action>(
                                    entry.getKey(), Action.WEST);
                else if (deltaY > 0)
                    retValentry =
                            new AbstractMap.SimpleEntry<ProgressState, Action>(
                                    entry.getKey(), Action.SOUTH);
                else if (deltaY < 0)
                    retValentry =
                            new AbstractMap.SimpleEntry<ProgressState, Action>(
                                    entry.getKey(), Action.NORTH);

                this.getRTable().put(retValentry, 100.0);

            }
        }
    }

    /**
     * This method runs one step of the Q-Learning process
     * 
     * @throws Exception
     */
    public void learnQTables(UWMobileAgent agent) throws Exception {
        agent.getNode().setLocation(agent.getState().getLocation()); // get
                                                                     // current
                                                                     // state
                                                                     // and set
                                                                     // the
                                                                     // node's
                                                                     // location
                                                                     // accordingly
        ArrayList<Action> actions = getStateActionTable().get(agent.getState()); // get
                                                                                 // the
                                                                                 // valid
                                                                                 // actions
                                                                                 // possible
                                                                                 // in
                                                                                 // current
                                                                                 // state
        Collections.shuffle(actions, agent.getRand()); // Shuffling the actions
                                                       // list to select a
                                                       // random action
        // 1. Select the first element of shuffled action list (selecting random
        // action)
        // 2. Find the newLocation of the node after it selects the random
        // action
        // 3. Find the newStateName of the node after it selects the random
        // action
        Location newLocation = agent.getNode().getLocation();
        String stateName = agent.getState().getStateName();
        int actionIndex = 0;
        try {
            newLocation = this.neighborActionTable(agent)
                    .get(actions.get(actionIndex)).getNode().getLocation();
            stateName = this.neighborActionTable(agent)
                    .get(actions.get(actionIndex)).getNode().getName();
        } catch (Exception e) {
            // TextUi.print("The next state is blocked");
        }

        ProgressState nextState = new ProgressState(stateName, newLocation);
        this.updateQTable(agent.getState(), nextState,
                actions.get(actionIndex)); // updating the Q-Table
        agent.setState(nextState); // Changing agent's current state to the next
                                   // state

        if (agent.getState().equals(goalState))
            this.setReachedGoalState(true);
    }

    /**
     * Update the value in the Q-Table based on reward from current state,
     * maximum reward of any action in the next state.
     * 
     * @param state
     * @param nextState
     * @param action
     */
    public void updateQTable(ProgressState state, ProgressState nextState,
        Action action) {
        Map.Entry<ProgressState, Action> entry =
                new AbstractMap.SimpleEntry<ProgressState, Action>(state,
                        action);
        double reward = this.getRTable().get(entry);
        ArrayList<Double> qValues = new ArrayList<Double>();
        if (this.getAgent(nextState).getNode().isEnabled()) {
            ArrayList<Action> actions =
                    this.getStateActionTable().get(nextState);

            for (Action nextStateAction : actions) {
                Map.Entry<ProgressState, Action> retValentry =
                        new AbstractMap.SimpleEntry<ProgressState, Action>(
                                nextState, nextStateAction);
                qValues.add(this.getQTable().get(retValentry));
            }
        }
        // the updating function
        // double newQValue = reward + gamma * Collections.max(qValues);

        double newQValue = (1 - alpha) * this.getQTable().get(entry)
                + alpha * (reward + gamma * Collections.max(qValues));

        this.getQTable().remove(entry);
        this.getQTable().put(entry, newQValue);
    }

    /**
     * Returns the HashMap of the actions-state that lead from current state
     * into neighboring state The state is represeted by the agents
     * 
     * @return
     * @throws Exception
     */
    public HashMap<Action, AbstractSensorAgent> neighborActionTable(
        AbstractSensorAgent sensorAgent) throws Exception {
        UWMobileAgent agent = null;
        if (sensorAgent instanceof UWMobileAgent) {
            agent = (UWMobileAgent) sensorAgent;
        }
        // Constructing the neighborhood list of sensor agents
        ArrayList<AbstractSensorAgent> neighbors;
        try {
            neighbors = this.getNeighbors(agent.getState());
        } catch (Exception e) {
            throw new Exception("Empty NeighborList");
        }

        // Constructing the neighborhood table with entries of neighbor location
        // and the navigational direction of neighbor from current location
        HashMap<Action, AbstractSensorAgent> neighborActionTable =
                new HashMap<>();
        for (AbstractSensorAgent neighbor : neighbors) {
            if (neighbor.getNode().getLocation().getX() > agent.getState()
                    .getLocation().getX())
                neighborActionTable.put(Action.EAST, neighbor);
            if (neighbor.getNode().getLocation().getX() < agent.getState()
                    .getLocation().getX())
                neighborActionTable.put(Action.WEST, neighbor);
            if (neighbor.getNode().getLocation().getY() > agent.getState()
                    .getLocation().getY())
                neighborActionTable.put(Action.SOUTH, neighbor);
            if (neighbor.getNode().getLocation().getY() < agent.getState()
                    .getLocation().getY())
                neighborActionTable.put(Action.NORTH, neighbor);
        }

        return neighborActionTable;
    }

    /**
     * @return the qTable
     */
    public HashMap<Map.Entry<ProgressState, Action>, Double> getQTable() {
        return QTable;
    }

    /**
     * @param qTable
     *            the qTable to set
     */
    public void setQTable(
        HashMap<Map.Entry<ProgressState, Action>, Double> qTable) {
        QTable = qTable;
    }

    /**
     * @return the rTable
     */
    public HashMap<Map.Entry<ProgressState, Action>, Double> getRTable() {
        return RTable;
    }

    /**
     * @param rTable
     *            the rTable to set
     */
    public void setRTable(
        HashMap<Map.Entry<ProgressState, Action>, Double> rTable) {
        RTable = rTable;
    }

    /**
     * Add an entry into the R-Table
     * 
     * @param entry
     * @param reward
     */
    public void addToRTable(Map.Entry<ProgressState, Action> entry,
        double reward) {
        this.getRTable().put(entry, reward);
    }

    /**
     * Add an entry into the Q-Table
     * 
     * @param entry
     * @param reward
     */
    public void addToQTable(Map.Entry<ProgressState, Action> entry,
        double reward) {
        this.getQTable().put(entry, reward);
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    /**
     * @return the reachedGoalState
     */
    public boolean isReachedGoalState() {
        return reachedGoalState;
    }

    /**
     * @param reachedGoalState
     *            the reachedGoalState to set
     */
    public void setReachedGoalState(boolean reachedGoalState) {
        this.reachedGoalState = reachedGoalState;
    }

}
