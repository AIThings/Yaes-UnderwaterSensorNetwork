package main.java.pathplanning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.java.agents.ProgressState;
import main.java.agents.UWAgent;
import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.agents.SensorRoutingHelper;
import yaes.sensornetwork.model.SensorNode;

public abstract class Learning implements Serializable {
    private static final long serialVersionUID = 2022662124852177106L;
    private AbstractSensorAgent agent;
    protected ProgressState goalState;
    protected ProgressState initialState;

    private HashMap<ProgressState, ArrayList<Action>> stateActionTable;

    public enum Action {
        LEFT, RIGHT, DOWN, UP
    }

    public Learning(AbstractSensorAgent agent, SensorNode src,
        SensorNode dest) {
        this.agent = agent;
        this.goalState = new ProgressState(dest.getName(), dest.getLocation());
        this.initialState = new ProgressState(src.getName(), src.getLocation());
        this.stateActionTable = new HashMap<ProgressState, ArrayList<Action>>();
    }

    /**
     * Returns the valid actions an agent can take in a current state The agent
     * compares its location with neighboring agents Based on the relative
     * location, it decide whether it can move in a current direction or not
     * 
     * @param state
     * @return
     */
    protected ArrayList<Action> validActions(ProgressState state) {
        List<AbstractSensorAgent> neighbors = this.getNeighbors(state);
        ArrayList<Action> actions = new ArrayList<Action>();
        for (AbstractSensorAgent neighbor : neighbors) {
            double deltaX = state.getLocation().getX()
                    - neighbor.getNode().getLocation().getX();
            double deltaY = state.getLocation().getY()
                    - neighbor.getNode().getLocation().getY();
            // only one action is valid per movement
            if (deltaX < 0)
                actions.add(Action.RIGHT);
            else if (deltaX > 0)
                actions.add(Action.LEFT);
            else if (deltaY > 0)
                actions.add(Action.UP);
            else if (deltaY < 0)
                actions.add(Action.DOWN);
        }

        return actions;
    }

    /**
     * Returns the agent associated with a node in a progressState
     * 
     * @param state
     * @return
     */
    protected AbstractSensorAgent getAgent(ProgressState state) {
        AbstractSensorAgent agent = null;
        List<AbstractSensorAgent> allAgents =
                SensorRoutingHelper.getSensorAgents(
                        this.getAgent().getSensorWorld(), UWAgent.class);
        for (AbstractSensorAgent retValAgent : allAgents)
            if (retValAgent.getNode().isEnabled())
                if (retValAgent.getNode().getName() == state.getStateName()) {
                    agent = retValAgent;
                    break;
                }
        return agent;
    }

    /**
     * Returns the list of neighbors of a state
     * 
     * @param state
     * @return
     */
    public ArrayList<AbstractSensorAgent> getNeighbors(ProgressState state) {
        AbstractSensorAgent agent = this.getAgent(state);
        // Finding the neighbors of the goalState Agent
        ArrayList<AbstractSensorAgent> neighbors =
                new ArrayList<AbstractSensorAgent>();
        List<AbstractSensorAgent> allAgents = SensorRoutingHelper
                .getSensorAgents(agent.getSensorWorld(), UWAgent.class);
        for (AbstractSensorAgent retValAgent : allAgents) {
            if (retValAgent.equals(agent))
                continue;
            if (SensorRoutingHelper.isConnected(agent, retValAgent)) {
                neighbors.add(retValAgent);
            }
        }
        return neighbors;
    }

    public ArrayList<ProgressState> getNeighborStateList(ProgressState state) {
        AbstractSensorAgent agent = this.getAgent(state);
        ArrayList<ProgressState> neighbors = new ArrayList<ProgressState>();
        List<AbstractSensorAgent> allAgents = SensorRoutingHelper
                .getSensorAgents(agent.getSensorWorld(), UWAgent.class);
        for (AbstractSensorAgent retValAgent : allAgents) {
            if (retValAgent.equals(agent))
                continue;
            if (SensorRoutingHelper.isConnected(agent, retValAgent)) {
                neighbors.add(new ProgressState(retValAgent.getNode().getName(),
                        retValAgent.getNode().getLocation()));
            }
        }
        return neighbors;
    }

    public HashMap<ProgressState, ArrayList<Action>> getStateActionTable() {
        return this.stateActionTable;
    }

    protected void setStateActionTable(
        HashMap<ProgressState, ArrayList<Action>> stateActionTable) {
        this.stateActionTable = stateActionTable;
    }

    /**
     * @return the goalState
     */
    public ProgressState getGoalState() {
        return this.goalState;
    }

    /**
     * @param goalState
     *            the goalState to set
     */
    public void setGoalState(ProgressState goalState) {
        this.goalState = goalState;
    }

    /**
     * @return the agent
     */
    public AbstractSensorAgent getAgent() {
        return this.agent;
    }

    /**
     * @param agent
     *            the agent to set
     */
    public void setAgent(AbstractSensorAgent agent) {
        this.agent = agent;
    }

    /**
     * returns initial state
     */
    public ProgressState getInitialState() {
        return initialState;
    }

    /**
     * sets initial state
     * 
     * @param initialState
     */
    public void setInitialState(ProgressState initialState) {
        this.initialState = initialState;
    }

}
