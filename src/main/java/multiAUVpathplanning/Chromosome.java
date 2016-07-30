package multiAUVpathplanning;

import java.util.ArrayList;
import java.util.Random;

public class Chromosome implements Comparable<Chromosome> {

    public int NumOfAUVs = -1;
    public int NumOfNodes = -1; // Not Initialized
    public int AUVTourMaxLength_1 = -1;
    public int AUVTourMaxLength_2 = -1;

    public int AUVTourMaxLength = -1;

    int[][] ChromosomeTour;

    double rawFitness = -1; // Fitness not yet evaluated
    double sclFitness = -1; // Fitness not yet scaled
    double proFitness = -1; // Fitness not yet proportionalized

    private static Random rand = new Random();

    public Chromosome(String InitType, int AUVs, int Nodes, Map M,
        String DistanceType) {
        NumOfAUVs = AUVs;
        NumOfNodes = Nodes;
        AUVTourMaxLength_1 = NumOfNodes / NumOfAUVs;
        AUVTourMaxLength_2 = (int) Math.ceil(NumOfNodes / NumOfAUVs);
        AUVTourMaxLength = AUVTourMaxLength_2;

        ChromosomeTour = new int[NumOfAUVs][AUVTourMaxLength];

        if (InitType == "Random 1") {
            int x = 0;
            for (int y = 0; y < NumOfAUVs; y++) {
                for (int z = 0; z < AUVTourMaxLength; z++) {
                    if (x < NumOfNodes) {
                        ChromosomeTour[y][z] = x;
                        x++;
                    } else {
                        ChromosomeTour[y][z] = -1;
                    }
                }
            }
            // Shuffling Randomly
            int Shuffle = rand.nextInt(30);
            for (int i = 0; i < Shuffle; i++) {
                SelfCrossover(0.5);
            }
        }
        if (InitType == "Random 2") {
            ArrayList<Integer> NodesPool = new ArrayList<Integer>();
            for (int i = 0; i < NumOfNodes; i++) {
                NodesPool.add(i, i);
            }
            int x = 0;
            for (int y = 0; y < NumOfAUVs; y++) {
                for (int z = 0; z < AUVTourMaxLength; z++) {
                    if (x < NumOfNodes) {
                        int Node = rand.nextInt(NodesPool.size());
                        ChromosomeTour[y][z] = NodesPool.get(Node);
                        NodesPool.remove(Node);
                        x++;
                    } else {
                        ChromosomeTour[y][z] = -1;
                    }
                }
            }
        }
        if (InitType == "Zig-Zag") {
            int x = 0;
            for (int y = 0; y < NumOfAUVs; y++) {
                for (int z = 0; z < AUVTourMaxLength; z++) {
                    if (x < NumOfNodes) {
                        ChromosomeTour[y][z] = x;
                        x++;
                    } else {
                        ChromosomeTour[y][z] = -1;
                    }
                }
            }
        }
        if (InitType == "Lawn-Mower") {
            int x = 0;
            int NodeToBeAssigned = -1;
            for (int y = 0; y < NumOfAUVs; y++) {
                for (int z = 0; z < AUVTourMaxLength; z++) {
                    MapTraversalLoop: for (int m = 0; m < M.SizeMetricY; m++) {
                        for (int n = 0; n < M.SizeMetricX; n++) {
                            if ((m % 2) == 0) {
                                NodeToBeAssigned =
                                        M.Nodes.get(m).get(n).NodeIdentifier;
                            } else if ((m % 2) == 1) {
                                NodeToBeAssigned = M.Nodes.get(m).get(
                                        (M.SizeMetricX - 1) - n).NodeIdentifier;
                            }
                            if (((M.SizeMetricY * m) + n) == x) {
                                break MapTraversalLoop;
                            }
                        }
                    }
                    if (x < NumOfNodes) {
                        ChromosomeTour[y][z] = NodeToBeAssigned;
                        x++;
                    } else {
                        ChromosomeTour[y][z] = -1;
                    }
                }
            }
        }
        if (InitType == "Greedy") {
            ArrayList<Node> A = M.SortedListBasedOnVoI();
            int x = 0;
            for (int z = 0; z < AUVTourMaxLength; z++) {
                for (int y = 0; y < NumOfAUVs; y++) {
                    if (x < NumOfNodes) {
                        ChromosomeTour[y][z] =
                                A.get(A.size() - 1).NodeIdentifier;
                        A.remove(A.size() - 1);
                        x++;
                    } else {
                        ChromosomeTour[y][z] = -1;
                    }
                }
            }
        }
        if (InitType == "Greedy Retrieve Across Path") {
            ArrayList<Node> A = M.SortedListBasedOnVoI();
            int[][] GreedyChromosomeTour = new int[NumOfAUVs][AUVTourMaxLength];
            int x = 0;
            for (int z = 0; z < AUVTourMaxLength; z++) {
                for (int y = 0; y < NumOfAUVs; y++) {
                    if (x < NumOfNodes) {
                        GreedyChromosomeTour[y][z] =
                                A.get(A.size() - 1).NodeIdentifier;
                        A.remove(A.size() - 1);
                        x++;
                    } else {
                        GreedyChromosomeTour[y][z] = -1;
                    }
                }
            }
            ChromosomeTour = M.AdjustChromosome(GreedyChromosomeTour, NumOfAUVs,
                    AUVTourMaxLength, "Euclidean");
        }
    }

    public void PrintChromosome(int Format) {
        if (Format == 1) {
            for (int i = 0; i < this.AUVTourMaxLength; i++) {
                System.out.print("\tV " + (i + 1));
            }
            System.out.println();
            for (int y = 0; y < this.NumOfAUVs; y++) {
                System.out.print("AUV " + (y + 1) + "\t");
                for (int z = 0; z < this.AUVTourMaxLength; z++) {
                    System.out.print(
                            "Node " + (this.ChromosomeTour[y][z] + 1) + "\t");
                }
                System.out.println();
            }
        }
    }

    public void PrintToursFitness(String FitnessBasis,
        boolean TranslateChromosomeForFitness, Map MyMap, double LastPacketTS,
        double AUV_Speed, double Distance_Scale, String DistanceType) {
        FitnessFunction F = new FitnessFunction();
        System.out.println("\tTour Fitness");
        for (int y = 0; y < this.NumOfAUVs; y++) {
            System.out.print("AUV " + (y + 1) + "\t");
            System.out.println(F.TourFitness(FitnessBasis,
                    TranslateChromosomeForFitness, this, MyMap, LastPacketTS, y,
                    AUV_Speed, Distance_Scale, DistanceType));
        }
        System.out.print("Total \t");
        System.out.println(F.doRawFitness(FitnessBasis,
                TranslateChromosomeForFitness, this, MyMap, LastPacketTS,
                AUV_Speed, Distance_Scale, DistanceType));
    }

    public void CopyChromosome(Chromosome ToBeCopiedChromosome) {
        for (int y = 0; y < this.NumOfAUVs; y++) {
            for (int z = 0; z < this.AUVTourMaxLength; z++) {
                this.ChromosomeTour[y][z] =
                        ToBeCopiedChromosome.ChromosomeTour[y][z];
            }
        }
    }

    public Chromosome CreateChildChromosome(Map M, String DistanceType) {
        Chromosome ChildChromosome = new Chromosome("Normal", this.NumOfAUVs,
                this.NumOfNodes, M, DistanceType);
        ChildChromosome.CopyChromosome(this);
        return ChildChromosome;
    }

    public void Mutation(double MutationRate) {
        String[] MutationTypes =
                { "Swap AUV Schedules", "Swap Visit Sequence Schedules" };
        String Type = MutationTypes[rand.nextInt(MutationTypes.length)];
        if (Type == "Swap AUV Schedules") {
            int z = rand.nextInt(NumOfAUVs);
            double chance = rand.nextDouble();
            if (chance < MutationRate) {
                // Select another AUV Schedule randomly
                int AUVSchedule = rand.nextInt(NumOfAUVs);
                // Swap
                int temp = 0;
                for (int y = 0; y < AUVTourMaxLength; y++) {
                    temp = this.ChromosomeTour[AUVSchedule][y];
                    this.ChromosomeTour[AUVSchedule][y] =
                            this.ChromosomeTour[z][y];
                    this.ChromosomeTour[z][y] = temp;
                }
            }
        } else if (Type == "Swap Visit Sequence Schedules") {
            int y = rand.nextInt(AUVTourMaxLength);
            double chance = rand.nextDouble();
            if (chance < MutationRate) {
                // Select another Visit Sequence Schedule randomly
                int VisitSequenceSchedule = rand.nextInt(AUVTourMaxLength);
                // Swap
                int temp = 0;
                for (int z = 0; z < NumOfAUVs; z++) {
                    temp = this.ChromosomeTour[z][VisitSequenceSchedule];
                    this.ChromosomeTour[z][VisitSequenceSchedule] =
                            this.ChromosomeTour[z][y];
                    this.ChromosomeTour[z][y] = temp;
                }
            }
        }
    }

    public void SelfCrossover(double CrossoverRate) {
        for (int y = 0; y < NumOfAUVs; y++) {
            for (int z = 0; z < AUVTourMaxLength; z++) {
                double chance = rand.nextDouble();
                if (chance < CrossoverRate) {
                    // Select another location randomly
                    int AUVSchedule = rand.nextInt(NumOfAUVs);
                    int VisitSequenceSchedule = rand.nextInt(AUVTourMaxLength);
                    // Swap
                    int temp = this.ChromosomeTour[y][z];
                    this.ChromosomeTour[y][z] =
                            this.ChromosomeTour[AUVSchedule][VisitSequenceSchedule];
                    this.ChromosomeTour[AUVSchedule][VisitSequenceSchedule] =
                            temp;
                }
            }
        }
    }

    @Override
    public int compareTo(Chromosome Comp) {
        if (this.rawFitness < Comp.rawFitness) {
            return -1;
        }
        if (this.rawFitness > Comp.rawFitness) {
            return 1;
        }
        return 0;
    }

    public void FillingChromosomeInterNodePaths(Map M) {

    }

}