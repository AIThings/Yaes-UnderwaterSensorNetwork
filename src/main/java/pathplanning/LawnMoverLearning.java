package pathplanning;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import agents.ProgressState;
import underwater.UWConstants;
import underwater.UWContext;
import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.model.SensorNode;
import yaes.world.physical.location.Location;

public class LawnMoverLearning extends Learning implements Serializable, UWConstants {
	private static final long serialVersionUID = 1L;
	private final static Logger slf4jLogger = LoggerFactory.getLogger(UWContext.class);
	private ProgressState initialState_;
	private ProgressState goalState_;
	private Set<ProgressState> closedSet;
	private HashMap<ProgressState, ProgressState> navigatedStateMap;

	public LawnMoverLearning(AbstractSensorAgent agent, SensorNode src, SensorNode dest) {
		super(agent, src, dest);
		this.initialState_ = new ProgressState(src.getName(), src.getLocation());
		this.goalState_ = new ProgressState(dest.getName(), dest.getLocation());
	}

	public HashMap<ProgressState, ProgressState> returnStatePath() {
		slf4jLogger.info("Path planner for lawn mover has started to initialized all states (locations) ... ");
		this.closedSet = new HashSet<ProgressState>(); // The nodes already
														// visited
		this.navigatedStateMap = new LinkedHashMap<ProgressState, ProgressState>();
		HashMap<Action, ProgressState> priorityList = new HashMap<Action, ProgressState>();
		closedSet.add(initialState_); /// add current state to the closedSet
		ProgressState currentState = initialState_;
		
		do {
			ProgressState oldState = currentState;
			for (ProgressState neighborState : this.getNeighborStateList(currentState)) {
				
				// if the neighbor node has already been visited then do not
				// visit it again
				if (closedSet.contains(neighborState))
					continue;
				double deltaX = currentState.getLocation().getX() - neighborState.getLocation().getX();
				double deltaY = currentState.getLocation().getY() - neighborState.getLocation().getY();
				if (deltaX < 0)
					priorityList.put(Action.WEST, neighborState);
				else if (deltaX > 0)
					priorityList.put(Action.EAST, neighborState);
				else if (deltaY < 0)
					priorityList.put(Action.SOUTH, neighborState);
				else if (deltaY > 0)
					priorityList.put(Action.NORTH, neighborState);
			}
			/**
			 * Assign the priority movement according the N, E, S, W
			 */
			
			if (priorityList.containsKey(Action.SOUTH)) {
				navigatedStateMap.put(priorityList.get(Action.SOUTH), currentState);
				currentState = priorityList.get(Action.SOUTH);
			} else if (priorityList.containsKey(Action.EAST)) {
				navigatedStateMap.put(priorityList.get(Action.EAST), currentState);
				currentState = priorityList.get(Action.EAST);
			} else if (priorityList.containsKey(Action.NORTH)) {
				navigatedStateMap.put(priorityList.get(Action.NORTH), currentState);
				currentState = priorityList.get(Action.NORTH);
			} else if (priorityList.containsKey(Action.WEST)) {
				navigatedStateMap.put(priorityList.get(Action.WEST), currentState);
				currentState = priorityList.get(Action.WEST);
			}
			if (oldState.equals(currentState)){
//				navigatedStateMap.put(priorityList.get(Action.WEST), currentState);
//				currentState.setLocation(new Location(287, 185));
//				ProgressState hackedState = new ProgressState("S-Hacked- ", new Location(287, 185));
//				navigatedStateMap.put(priorityList.get(Action.NORTH), currentState);
//				currentState = hackedState;				
				break;
			}
			closedSet.add(currentState);
			priorityList.clear();
			slf4jLogger.info("Next movement is ... " + currentState.toString());
			
		} while (!currentState.equals(goalState_));
		slf4jLogger
				.info("Path planner for lawn mover has completed its initialization for all states (locations) ... ");
		return navigatedStateMap;
	}
}
