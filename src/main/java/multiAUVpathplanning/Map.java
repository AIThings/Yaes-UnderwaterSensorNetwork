package main.java.multiAUVpathplanning;

import java.util.ArrayList;
import java.util.Collections;

public class Map {

    public ArrayList<ArrayList<Node>> Nodes = new ArrayList<ArrayList<Node>>();

    public int SizeMetricY = 0;
    public int SizeMetricX = 0;

    public double DistanceScale = -1;

    public Map(int Size_Y, int Size_X) {
        SizeMetricX = Size_X;
        SizeMetricY = Size_Y;
    }

    public void InitializeMap(String Topology, double DS) {
        DistanceScale = DS;
        if (Topology == "Mesh") {
            // Create Nodes
            int ID = 0;
            for (int i = 0; i < SizeMetricY; i++) {
                Nodes.add(i, new ArrayList<Node>());
                for (int j = 0; j < SizeMetricX; j++) {
                    Nodes.get(i).add(new Node(i, j, ID));
                    ID++;
                }
            }
            // Set Neighbors
            for (int i = 0; i < SizeMetricY; i++) {
                for (int j = 0; j < SizeMetricX; j++) {
                    for (int x = 0; x < 3; x++) {
                        for (int y = 0; y < 3; y++) {
                            int NeighborXindex = i + x - 1;
                            int NeighborYindex = j + y - 1;
                            if ((NeighborXindex) > -1 && (NeighborYindex) > -1
                                    && (NeighborXindex) < SizeMetricY
                                    && (NeighborYindex) < SizeMetricX) {
                                Nodes.get(i).get(j).Neighbors[x][y] =
                                        Nodes.get(NeighborXindex)
                                                .get(NeighborYindex);
                            } else {
                                Nodes.get(i).get(j).Neighbors[x][y] = null;
                            }
                        }
                    }
                }
            }
        }
    }

    public void PrintVoIMap(double TS) {
        for (int Xloc = 0; Xloc < SizeMetricY; Xloc++) {
            for (int Yloc = 0; Yloc < SizeMetricX; Yloc++) {
                System.out.printf("%10.3f",
                        Nodes.get(Xloc).get(Yloc).VoIOfferAtNode(TS));
            }
            System.out.println();
        }
    }

    public Node GetNodeBasedOnId(int ID) {
        Node NodeToFind = null;
        SearchLoop: for (int Xloc = 0; Xloc < SizeMetricY; Xloc++) {
            for (int Yloc = 0; Yloc < SizeMetricX; Yloc++) {
                if (Nodes.get(Xloc).get(Yloc).NodeIdentifier == ID) {
                    NodeToFind = Nodes.get(Xloc).get(Yloc);
                    break SearchLoop;
                }
            }
        }
        return NodeToFind;
    }

    public double InterNodeDistance(Node Source, Node Destination,
        String DistanceType) {
        double Distance = -1;
        double SX = Source.coordinate_X;
        double SY = Source.coordinate_Y;
        double DX = Destination.coordinate_X;
        double DY = Destination.coordinate_Y;
        if (DistanceType == "Manhattan") {
            Distance = Math.abs(SX - DX) + Math.abs(SY - DY);
        } else if (DistanceType == "Euclidean") {
            Distance = Math.sqrt(Math.pow(Math.abs(SX - DX), 2)
                    + Math.pow(Math.abs(SY - DY), 2));
        }
        return DistanceScale * Distance;
    }

    public double AverageNodeDistance(String DistanceType) {
        double AverageDistance = 0;
        for (int XlocS = 0; XlocS < SizeMetricY; XlocS++) {
            for (int YlocS = 0; YlocS < SizeMetricX; YlocS++) {
                for (int XlocD = 0; XlocD < SizeMetricY; XlocD++) {
                    for (int YlocD = 0; YlocD < SizeMetricX; YlocD++) {
                        Node Source = Nodes.get(XlocS).get(YlocS);
                        Node Destination = Nodes.get(XlocD).get(YlocD);
                        AverageDistance += InterNodeDistance(Source,
                                Destination, DistanceType);
                    }
                }
            }
        }
        int NumberofNodes = SizeMetricY * SizeMetricX;
        int NeighborsOfEachNode = (SizeMetricY * SizeMetricX) - 1;
        return (AverageDistance) / (NumberofNodes * NeighborsOfEachNode);
    }

    public double AverageTourLength(String DistanceType) {
        int NumberofNodes = SizeMetricY * SizeMetricX;
        return AverageNodeDistance(DistanceType) * NumberofNodes;
    }

    public Node ClosestNeighborToDestination(Node Source, Node Destination,
        String DistanceType) {
        Node MinimumDistanceNode = Source;
        double MinimumDistance =
                InterNodeDistance(Source, Destination, "Euclidean");
        double Distance = -1;
        double SNX = -1;
        double SNY = -1;
        double DX = Destination.coordinate_X;
        double DY = Destination.coordinate_Y;

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (Source.Neighbors[x][y] != null) {
                    SNX = Source.Neighbors[x][y].coordinate_X;
                    SNY = Source.Neighbors[x][y].coordinate_Y;
                    if (DistanceType == "Manhattan") {
                        Distance = Math.abs(SNX - DX) + Math.abs(SNY - DY);
                    } else if (DistanceType == "Euclidean") {
                        Distance = Math.sqrt(Math.pow(Math.abs(SNX - DX), 2)
                                + Math.pow(Math.abs(SNY - DY), 2));
                    }
                    if (MinimumDistance > Distance) {
                        MinimumDistance = Distance;
                        MinimumDistanceNode = Source.Neighbors[x][y];
                    }
                    // System.out.println("Node
                    // "+Source.Neighbors[x][y].NodeIdentifier+"
                    // "+Source.Neighbors[x][y].coordinate_X+","+Source.Neighbors[x][y].coordinate_Y+"
                    // -- "+Distance);
                }
            }
        }
        // System.out.println("Node "+MinimumDistanceNode.NodeIdentifier+"
        // "+MinimumDistanceNode.coordinate_X+","+MinimumDistanceNode.coordinate_Y+"
        // -- "+MinimumDistance);
        return MinimumDistanceNode;
    }

    public ArrayList<Node> FindNodesInPath(Node Source, Node Destination,
        String DistanceType) {
        ArrayList<Node> NodesInPath = new ArrayList<Node>();
        Node ToAddToPath = Source;
        while (Destination.NodeIdentifier != ToAddToPath.NodeIdentifier) {
            ToAddToPath = ClosestNeighborToDestination(ToAddToPath, Destination,
                    DistanceType);
            NodesInPath.add(ToAddToPath);
        }
        return NodesInPath;
    }

    public int[][] AdjustChromosome(int[][] Chromosome, int NumOfAUVs,
        int AUVTourMaxLength, String DistanceType) {
        int[][] AdjustedChromosome = new int[NumOfAUVs][AUVTourMaxLength];
        ArrayList<ArrayList<Node>> AdjustedChromosomeList =
                new ArrayList<ArrayList<Node>>();
        ArrayList<Node> NodesInPath = new ArrayList<Node>();
        ArrayList<Node> Temp = new ArrayList<Node>();
        //
        for (int i = 0; i < NumOfAUVs; i++) {
            AdjustedChromosomeList.add(i, new ArrayList<Node>());
            for (int j = 0; j < AUVTourMaxLength; j++) {
                if ((j + 1) < AUVTourMaxLength) {
                    Node SourceNode = GetNodeBasedOnId(Chromosome[i][j]);
                    Node DestinationNode =
                            GetNodeBasedOnId(Chromosome[i][j + 1]);
                    NodesInPath.add(NodesInPath.size(), SourceNode);
                    Temp = FindNodesInPath(SourceNode, DestinationNode,
                            DistanceType);
                    NodesInPath.addAll(NodesInPath.size(), Temp);
                    AdjustedChromosomeList.get(i).addAll(NodesInPath);
                    NodesInPath.clear();
                    Temp.clear();
                }
            }
        }
        //
        /*
         * for (int i = 0; i < AdjustedChromosomeList.size(); i++) { for (int j
         * = 0; j < AdjustedChromosomeList.get(i).size(); j++) { //Print
         * Something
         * System.out.print(AdjustedChromosomeList.get(i).get(j).NodeIdentifier+
         * 1+",\t"); } System.out.println(); }
         */
        //
        for (int j = 0; j < AUVTourMaxLength; j++) {
            for (int i = 0; i < NumOfAUVs; i++) {
                SearchAndDeleteAllInstances(i, j, AdjustedChromosomeList);
                AdjustedChromosome[i][j] =
                        AdjustedChromosomeList.get(i).get(j).NodeIdentifier;
            }
        }
        //
        /*
         * for (int i = 0; i < NumOfAUVs; i++) { for (int j = 0; j <
         * AUVTourMaxLength; j++) { //Print Something
         * System.out.print(AdjustedChromosome[i][j]+1+", "); }
         * System.out.println(); }
         */
        return AdjustedChromosome;
    }

    private void SearchAndDeleteAllInstances(int i, int j,
        ArrayList<ArrayList<Node>> ACList) {
        int NodeIDToDelete = ACList.get(i).get(j).NodeIdentifier;
        for (int m = 0; m < ACList.size(); m++) {
            for (int n = 0; n < ACList.get(m).size(); n++) {
                if (NodeIDToDelete == ACList.get(m).get(n).NodeIdentifier) {
                    if ((m == i) && (n == j)) {
                        // Do Nothing
                    } else {
                        // Mark for Deletion
                        ACList.get(m).remove(n);
                        n--;
                    }
                }
            }
        }
    }

    public ArrayList<Node> MapToList() {
        ArrayList<Node> NodeList = new ArrayList<Node>();
        for (int Xloc = 0; Xloc < SizeMetricY; Xloc++) {
            for (int Yloc = 0; Yloc < SizeMetricX; Yloc++) {
                NodeList.add(Nodes.get(Xloc).get(Yloc));
            }
        }
        return NodeList;
    }

    public ArrayList<Node> SortedListBasedOnVoI() {
        ArrayList<Node> NodeList = new ArrayList<Node>();
        for (int Xloc = 0; Xloc < SizeMetricY; Xloc++) {
            for (int Yloc = 0; Yloc < SizeMetricX; Yloc++) {
                NodeList.add(Nodes.get(Xloc).get(Yloc));
            }
        }
        ArrayList<Node> NodeListSorted = NodeList;
        Collections.sort(NodeListSorted);
        return NodeListSorted;
    }

}
