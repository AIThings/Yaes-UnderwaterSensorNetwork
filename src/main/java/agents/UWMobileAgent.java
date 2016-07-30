package main.java.agents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import main.java.agents.pathplanner.GreedyPlanner;
import main.java.agents.pathplanner.iAgentPathPlanner;
import main.java.pathplanning.DStarLitePP;
import main.java.pathplanning.Learning.Action;
import main.java.underwater.UWConstants;
import main.java.underwater.UWContext;
import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.model.Perception;
import yaes.sensornetwork.model.SensorNetworkMessageConstants;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SensorNode;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.PPMTraversal;
import yaes.world.physical.path.PlannedPath;

/**
 * This class creates a simple UnderWater Mobile Sensor Agent
 * 
 * @author SaadKhan
 *
 */
public class UWMobileAgent extends AbstractSensorAgent
    implements UWConstants, Serializable {
    private static final long serialVersionUID = 1L;
    private iAgentPathPlanner pathplanner;
    private Location startLocation;
    private Location localDestination;
    private double speed;
    private PlannedPath plannedpath;
    private Location nextLocation;

    // PathTraversal traversal = new PathTraversal(path);
    private ProgressState state;
    public static HashMap<ProgressState, ArrayList<Action>> stateActionTable;
    // private ArrayList<Location> pathUWMobileAgent;
    private Random rand;
    protected PPMTraversal ppmtraversal;

    private LearningMethod method;
    private double VoI;
    SensorNode src = getSensorWorld().getSensorNodes().get(0);
    SensorNode dest = getSensorWorld().getSensorNodes()
            .get(getSensorWorld().getSensorNodes().size() - 1);

    public UWMobileAgent(String name, SensorNetworkWorld sensorWorld,
        Location startloc, Location destLoc) {
        super(name, sensorWorld);
        this.setStartLocation(startloc);
        this.setLocalDestination(destLoc);

        this.plannedpath = new PlannedPath(this.getStartLocation(),
                this.getLocalDestination());

        switch (this.method) {
        case ASTAR:
            break;
        case ASTAR_OBSTACLES:
            break;
        case GENETIC_OPTIMIZATION:
            break;
        case GREEDY_LEARNING:
            this.pathplanner = new GreedyPlanner();
            break;
        case LAWNMOVER:
            break;
        case PROBABLISTIC_GREEDY:
            break;
        case QLEARNING:
            break;
        case QLEARNING_OBSTACLES:
            break;
        case QLEARNING_OBSTACLES_VISUAL:
            break;
        case QLEARNING_VISUAL:
            break;
        case RANDOM:
            break;
        default:
            break;

        }
    }

    /**
     * The action for a mobile agent
     */
    @Override
    public void action() {
        super.action();
        pathplanner.move(this);
    }

    @Override
    protected void handleReceivedMessage(ACLMessage message) {
        if (message.getValue(SensorNetworkMessageConstants.FIELD_CONTENT)
                .equals(SensorNetworkMessageConstants.MESSAGE_DATA)) {
            this.setVoI(this.getVoI() + (double) message.getValue(FIELD_VOI));
            this.getSensorWorld().getSimulationOutput().update(Var_Measured_VoI,
                    (double) message.getValue(FIELD_VOI));
            this.getSensorWorld().getSimulationOutput().update(
                    Var_Measured_VoI_Max,
                    (double) message.getValue(FIELD_VOI_MAX));
            this.cleanConversations();
        }
    }

    @Override
    protected void handleIntruderPresence(Perception p) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void handleOverheardMessage(ACLMessage message) {
        // TODO Auto-generated method stub

    }

    /**
     * Returns the intended move but it does not execute it.
     * 
     * This is called from action (where it will execute it), or it is called
     * from the game, where it will correspond to a "D" play
     * 
     */

    public Location getIntendedMove() {
        if (this.getNode().getLocation().equals(this.getLocalDestination())) {
            // look for the initial location
            Location initialLoc =
                    new Location(this.getRand().nextInt(UWContext.mapWidth),
                            this.getRand().nextInt(UWContext.mapHeight));
            while (UWContext.isLocationOccupied(initialLoc,
                    UWContext.emGlobalCost, UWContext.PROP_OBSTACLE))
                initialLoc =
                        new Location(this.getRand().nextInt(UWContext.mapWidth),
                                this.getRand().nextInt(UWContext.mapHeight));

            DStarLitePP dStar = new DStarLitePP(UWContext.emGlobalCost,
                    initialLoc, this.getLocalDestination());
            this.plannedpath = dStar.searchPath();
        }

        this.setNextLocation(this.getNode().getLocation());
        Location loc = plannedpath.getNextLocation(this.getNode().getLocation(),
                (int) speed);
        if (loc != null)
            return loc;
        else
            return localDestination;
    }

    public PlannedPath getPlannedPath() {
        return plannedpath;
    }

    public void setPlannedPath(PlannedPath path) {

        this.plannedpath = path;
    }

    /**
     * @return the state
     */
    public ProgressState getState() {
        return this.state;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setState(ProgressState state) {
        this.state = state;
    }

    /**
     * @return the stateActionTable
     */
    public HashMap<ProgressState, ArrayList<Action>> getStateActionTable() {
        return stateActionTable;
    }

    /**
     * @param stateActionTable
     *            the stateActionTable to set
     */
    public static void setStateActionTable(
        HashMap<ProgressState, ArrayList<Action>> stateActionTable) {
        UWMobileAgent.stateActionTable = stateActionTable;
    }

    /**
     * @return the rand
     */
    public Random getRand() {
        return rand;
    }

    /**
     * @param rand
     *            the rand to set
     */
    public void setRand(Random rand) {
        this.rand = rand;
    }

    /**
     * @return the method
     */
    public LearningMethod getMethod() {
        return method;
    }

    /**
     * @param method
     *            the method to set
     */
    public void setMethod(LearningMethod method) {
        this.method = method;
    }

    /**
     * @return the voI
     */
    public double getVoI() {
        return VoI;
    }

    /**
     * @param voI
     *            the voI to set
     */
    public void setVoI(double voI) {
        VoI = voI;
    }

    /**
     * @return the sinkSpeed
     */
    public double getSinkSpeed() {
        return speed;
    }

    /**
     * @param sinkSpeed
     *            the sinkSpeed to set
     */
    public void setSinkSpeed(double sinkSpeed) {
        this.speed = sinkSpeed;
    }

    public Location getNextLocation() {
        return nextLocation;
    }

    public void setNextLocation(Location nextLocation) {
        this.nextLocation = nextLocation;
    }

    public Location getLocalDestination() {
        return localDestination;
    }

    public void setLocalDestination(Location localDestination) {
        this.localDestination = localDestination;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public PPMTraversal getPPMtraversal() {
        return this.ppmtraversal;
    }

    protected void setPPMtraversal(PPMTraversal ppmtraversal) {
        this.ppmtraversal = ppmtraversal;
    }
}
