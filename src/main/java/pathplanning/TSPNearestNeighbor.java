package main.java.pathplanning;

import java.util.ArrayList;
import java.util.Stack;

import yaes.sensornetwork.model.SensorNode;
import yaes.ui.text.TextUi;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.PlannedPath;

public class TSPNearestNeighbor {
    private int numberOfNodes;
    private Stack<Integer> stack;
    ArrayList<Integer> tspOrder;

    public TSPNearestNeighbor() {
        stack = new Stack<Integer>();
        tspOrder = new ArrayList<Integer>();
    }

    public ArrayList<Integer> tsp(int adjacencyMatrix[][]) {
        numberOfNodes = adjacencyMatrix[1].length - 1;
        int[] visited = new int[numberOfNodes + 1];
        visited[1] = 1;
        stack.push(1);
        int element, dst = 0, i;
        int min = Integer.MAX_VALUE;
        boolean minFlag = false;
        System.out.print(1 + "\t");
        tspOrder.add(1);

        while (!stack.isEmpty()) {
            element = stack.peek();
            i = 1;
            min = Integer.MAX_VALUE;
            while (i <= numberOfNodes) {
                if (adjacencyMatrix[element][i] > 1 && visited[i] == 0) {
                    if (min > adjacencyMatrix[element][i]) {
                        min = adjacencyMatrix[element][i];
                        dst = i;
                        minFlag = true;
                    }
                }
                i++;
            }
            if (minFlag) {
                visited[dst] = 1;
                stack.push(dst);
                System.out.print(dst + "\t");
                tspOrder.add(dst);
                minFlag = false;
                continue;
            }
            stack.pop();
        }
        return tspOrder;
    }

    public ArrayList<Location> tspPathPlanner(SensorNode node,
        ArrayList<Location> locations) {
        locations.add(0, node.getLocation());
        // DStarLitePP dStar;
        PlannedPath path;
        int adjacencyMatrix[][] =
                new int[locations.size() + 1][locations.size() + 1];
        int tracker = 1;
        int neighborTracker = 1;
        for (Location loc : locations) {
            neighborTracker = 1;
            for (Location neighborLoc : locations) {
                if (neighborLoc.equals(loc)) {
                    adjacencyMatrix[tracker][tracker] = 0;
                    continue;
                }
                // FIXME: Avoiding the D-Lite Paths for swift calculations
                // dStar = new DStarLitePP(context.emGlobalCost, loc,
                // neighborLoc);
                // path = dStar.searchPath();
                // adjacencyMatrix[tracker][neighborTracker++] = (int)
                // path.getPathLenght();
                adjacencyMatrix[tracker][neighborTracker++] =
                        (int) node.getLocation().distanceTo(neighborLoc);
            }
            tracker++;
        }

        for (int i = 1; i <= locations.size(); i++)
            for (int j = 1; j <= locations.size(); j++)
                if (adjacencyMatrix[i][j] == 1 && adjacencyMatrix[j][i] == 0)
                    adjacencyMatrix[j][i] = 1;

        TSPNearestNeighbor tspNearestNeighbor = new TSPNearestNeighbor();
        ArrayList<Integer> tspOrder = tspNearestNeighbor.tsp(adjacencyMatrix);
        ArrayList<Location> finalList = new ArrayList<Location>();
        TextUi.println(tspOrder.toString());
        for (int order : tspOrder) {
            finalList.add(locations.get(order - 1));
        }
        TextUi.println(finalList.toString());
        return finalList;
    }

}
