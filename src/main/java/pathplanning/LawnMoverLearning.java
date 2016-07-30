package pathplanning;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import agents.ProgressState;
import underwater.UWConstants;
import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.model.SensorNode;

public class LawnMoverLearning extends Learning
    implements Serializable, UWConstants {
    private static final long serialVersionUID = -8399257301052077338L;
    private ProgressState initialState_;
    private ProgressState goalState_;
    private Set<ProgressState> closedSet;
    private HashMap<ProgressState, ProgressState> navigatedStateMap;

    public LawnMoverLearning(AbstractSensorAgent agent, SensorNode src,
        SensorNode dest) {
        super(agent, src, dest);
        this.initialState_ = new ProgressState(src.getName(), src.getLocation());
        this.goalState_ = new ProgressState(dest.getName(), dest.getLocation());
    }

    public HashMap<ProgressState, ProgressState> returnStatePath() {
        this.closedSet = new HashSet<ProgressState>(); // The nodes already
                                                       // visited
        this.navigatedStateMap =
                new LinkedHashMap<ProgressState, ProgressState>();
        HashMap<Action, ProgressState> priorityList =
                new HashMap<Action, ProgressState>();
        closedSet.add(initialState_); /// add current state to the closedSet
        ProgressState currentState = initialState_;
        do {
            for (ProgressState neighborState : this
                    .getNeighborStateList(currentState)) {
                if (!closedSet.contains(neighborState)) { // if the neighborNode
                                                          // has not been
                                                          // visited
                    double deltaX = currentState.getLocation().getX()
                            - neighborState.getLocation().getX();
                    double deltaY = currentState.getLocation().getY()
                            - neighborState.getLocation().getY();
                    if (deltaX < 0)
                        priorityList.put(Action.RIGHT, neighborState);
                    else if (deltaX > 0)
                        priorityList.put(Action.LEFT, neighborState);
                    else if (deltaY < 0)
                        priorityList.put(Action.DOWN, neighborState);
                    else if (deltaY > 0)
                        priorityList.put(Action.UP, neighborState);
                }
            }

            if (priorityList.containsKey(Action.RIGHT)) {
                navigatedStateMap.put(priorityList.get(Action.RIGHT),
                        currentState);
                currentState = priorityList.get(Action.RIGHT);
            } else if (priorityList.containsKey(Action.LEFT)) {
                navigatedStateMap.put(priorityList.get(Action.LEFT),
                        currentState);
                currentState = priorityList.get(Action.LEFT);
            } else if (priorityList.containsKey(Action.DOWN)) {
                navigatedStateMap.put(priorityList.get(Action.DOWN),
                        currentState);
                currentState = priorityList.get(Action.DOWN);
            } else if (priorityList.containsKey(Action.UP)) {
                navigatedStateMap.put(priorityList.get(Action.UP),
                        currentState);
                currentState = priorityList.get(Action.UP);
            }
            closedSet.add(currentState);
            priorityList.clear();

        } while (!currentState.equals(goalState_));

        return navigatedStateMap;
    }
}
