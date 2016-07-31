package agents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import agents.pathplanner.AStarPlanner;
import agents.pathplanner.GreedyPlanner;
import agents.pathplanner.LawnMowerPlanner;
import agents.pathplanner.QLearningPlanner;
import agents.pathplanner.RandomPlanner;
import agents.pathplanner.iAgentPathPlanner;
import environment.UWEnvironment;
import pathplanning.DStarLitePP;
import pathplanning.Learning.Action;
import underwater.UWConstants;
import underwater.UWContext;
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
    private final static Logger slf4jLogger = LoggerFactory.getLogger(UWContext.class);
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
    private PathPlannerMethodology method;
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
        
    }

    public void planPath(){
        switch (method) {
        case ASTAR:
        	this.pathplanner = new AStarPlanner();
            break;
        case ASTAR_OBSTACLES:
            break;
        case GENETIC_OPTIMIZATION:
            break;
        case GREEDY_LEARNING:
            this.pathplanner = new GreedyPlanner();
            break;
        case LAWNMOVER:
        	this.pathplanner = new LawnMowerPlanner();
            break;
        case PROBABLISTIC_GREEDY:
            break;
        case QLEARNING:
        	this.pathplanner = new QLearningPlanner();
            break;
        case QLEARNING_OBSTACLES:
            break;
        case QLEARNING_OBSTACLES_VISUAL:
            break;
        case QLEARNING_VISUAL:
            break;
        case RANDOM:
        	this.pathplanner = new RandomPlanner();
            break;
        default:
            break;
        }
        
        ppmtraversal = this.pathplanner.planPath(this, plannedpath);
        slf4jLogger.info("Traversed path information is " + ppmtraversal.toString() );
    }
    
    /**
     * The action for a mobile agent
     */
    @Override
    public void action() {
    	//collect the messages and transmits if required
        super.action();
        //the mobile path movement
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
            while (UWEnvironment.isLocationOccupied(initialLoc,
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
    public PathPlannerMethodology getMethod() {
        return method;
    }

    /**
     * @param method
     *            the method to set
     */
    public void setPathPlannerMethodology(PathPlannerMethodology method) {
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
