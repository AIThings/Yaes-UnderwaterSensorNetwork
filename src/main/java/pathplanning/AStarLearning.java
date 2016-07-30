package main.java.pathplanning;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import main.java.agents.ProgressState;
import main.java.underwater.UWConstants;
import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.model.SensorNode;

public class AStarLearning extends Learning
    implements Serializable, UWConstants {
    private static final long serialVersionUID = 3506427826802693007L;
    public static HashMap<ProgressState, Double> g_x; // cost of traversal from
                                                      // starting point to point
                                                      // x
    private HashMap<ProgressState, Double> h_x; // cost of traversal from point
                                                // x to end point
    private HashMap<ProgressState, Double> f_x;
    private ProgressState initialState_;
    private SortedSet<Map.Entry<ProgressState, Double>> openSet;
    private Set<ProgressState> closedSet;
    private HashMap<ProgressState, ProgressState> navigatedStateMap;

    public AStarLearning(AbstractSensorAgent agent, SensorNode src,
        SensorNode dest) {
        super(agent, src, dest);
        this.initialState_ = new ProgressState(src.getName(), src.getLocation());
        this.setG_x(new HashMap<ProgressState, Double>());
        this.setH_x(new HashMap<ProgressState, Double>());
        this.setF_x(new HashMap<ProgressState, Double>());
        // this.g_x = this.getG(initialState);
        // this.h_x = this.getH(initialState);
        // this.f_x = this.getF(initialState);
        this.openSet = new TreeSet<Map.Entry<ProgressState, Double>>(
                new Comparator<Map.Entry<ProgressState, Double>>() {
                    @Override
                    public int compare(Map.Entry<ProgressState, Double> e1,
                        Map.Entry<ProgressState, Double> e2) {
                        // if the f-value is same, order the list with greater
                        // G-value among
                        // the same f-value items
                        if (e1.getValue().compareTo(e2.getValue()) == 0) {
                            return AStarLearning.g_x.get(e1.getKey()).compareTo(
                                    AStarLearning.g_x.get(e2.getKey()));
                        }
                        return e1.getValue().compareTo(e2.getValue());
                    }
                }); // creates an set with ordered values (lowest first)
    }

    public HashMap<ProgressState, ProgressState> returnStatePath() {
        this.closedSet = new HashSet<ProgressState>(); // The nodes already
                                                       // visited
        this.navigatedStateMap =
                new LinkedHashMap<ProgressState, ProgressState>();
        this.getG_x().put(initialState_, 0.0);
        this.getF_x().put(initialState_, this.getG_x().get(initialState_)
                + this.distBetweenState(initialState_, goalState));

        this.openSet.add(new AbstractMap.SimpleEntry<ProgressState, Double>(
                initialState_, this.getF_x().get(initialState_)));
        while (!openSet.isEmpty()) {
            ProgressState currentState = openSet.first().getKey(); // current :=
                                                                   // the node
                                                                   // in openset
                                                                   // having the
                                                                   // lowest
                                                                   // f_score[]
                                                                   // value
            if (currentState.equals(this.getGoalState())) {
                // for(ProgressState state: g_x.keySet()){
                // TextUi.println("StateName: " + state.getStateName() + "\t
                // g(x): " + g_x.get(state)
                // + "\t f(x): " + f_x.get(state) + "\t h(x): " +
                // h_x.get(state));
                // }
                return this.navigatedStateMap; // returns the constructed path
            }

            openSet.remove(openSet.first()); // remove current state from the
                                             // openSet
            closedSet.add(currentState); /// add current state to the closedSet
            for (ProgressState neighborState : this
                    .getNeighborStateList(currentState)) {
                if (closedSet.contains(neighborState))
                    continue;
                double tentativeG = this.getG_x().get(currentState)
                        + this.distBetweenState(currentState, neighborState);
                // if neighbor not in openset or tentative_g_score <
                // g_score[neighbor]
                if (!this.openSetContains(openSet, neighborState)
                        || tentativeG < this.getG_x().get(neighborState)) {
                    this.navigatedStateMap.put(neighborState, currentState);
                    // came_from[neighbor] := current
                    // g_score[neighbor] := tentative_g_score
                    // f_score[neighbor] := g_score[neighbor] +
                    // heuristic_cost_estimate(neighbor, goal)
                    // if neighbor not in openset
                    // add neighbor to openset
                    this.getG_x().put(neighborState, tentativeG);
                    this.getF_x().put(neighborState,
                            this.getG_x().get(neighborState)
                                    + this.distBetweenState(neighborState,
                                            goalState));
                    if (!this.openSetContains(openSet, neighborState)) {
                        this.openSet
                                .add(new AbstractMap.SimpleEntry<ProgressState, Double>(
                                        neighborState,
                                        this.getF_x().get(neighborState)));
                    }
                }
            }
        }
        return null;
    }

    public boolean openSetContains(SortedSet openSet, ProgressState state) {
        Iterator iter = openSet.iterator();
        while (iter.hasNext()) {
            Map.Entry<ProgressState, Double> entry =
                    (Map.Entry<ProgressState, Double>) iter.next();
            if (entry.getKey().equals(state)) {
                return true;
            }
        }
        return false;
    }
    // public double getH(ProgressState state){
    // return state.getLocation().distanceTo(this.getGoalState().getLocation());
    // }

    // public double getG(ProgressState state){
    // return
    // state.getLocation().distanceTo(this.getInitialState().getLocation());
    // }

    // public double getF(ProgressState state){
    // return getG(state) + getH(state);
    // }

    public double distBetweenState(ProgressState src, ProgressState dest) {
        return src.getLocation().distanceTo(dest.getLocation());
    }

    @Override
    public ProgressState getInitialState() {
        return initialState_;
    }

    public void setInitialState(ProgressState initialState) {
        this.initialState_ = initialState;
    }

    public HashMap<ProgressState, Double> getG_x() {
        return g_x;
    }

    public void setG_x(HashMap<ProgressState, Double> g_x) {
        AStarLearning.g_x = g_x;
    }

    public HashMap<ProgressState, Double> getH_x() {
        return h_x;
    }

    public void setH_x(HashMap<ProgressState, Double> h_x) {
        this.h_x = h_x;
    }

    public HashMap<ProgressState, Double> getF_x() {
        return f_x;
    }

    public void setF_x(HashMap<ProgressState, Double> f_x) {
        this.f_x = f_x;
    }
}
