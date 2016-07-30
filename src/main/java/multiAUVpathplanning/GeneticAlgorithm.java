package multiAUVpathplanning;

//import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import yaes.sensornetwork.model.SensorNetworkWorld;

public class GeneticAlgorithm {

    private static Random rand = new Random();

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static double Distance_Scale = 1000; // Map distance between
                                                // consecutive nodes is 1000m
    public static double AUV_Speed = 10; // AUV speed/velocity is 10 m/s
    public static String DistanceType = "Euclidean"; // Map distance between
                                                     // consecutive nodes is
                                                     // 1000m
    public static int NumOfAUVs = 1;
    public static int NumofVNodes = 5; // No. of Nodes Horizontally in Mesh
    public static int NumofHNodes = 5; // No. of Nodes Vertically in Mesh
    public static int NumOfNodes = NumofVNodes * NumofHNodes;

    // Equal, Symmetrical, Random, External
    public static String Packet_Initializtion_Type = "Symmetrical";
    public static double Packet_Initializtion_Magnitude = 1;
    public static double Packet_Initializtion_DesiredVoIFactor = .5;
    public static double Packet_Initializtion_HS_Magnitude = 10000;
    public static double Packet_Initializtion_HS_DesiredVoIFactor = .01;
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int NumberOfRuns = 4;
    public static int NumberOfGens = 100;
    public static int PopulationSize = 100;

    public static double MutationRate = 1 / NumOfNodes;
    public static double CrossOverRate = 1 / NumOfNodes;

    public static boolean TranslateChromosomeForFitness = false;
    public static boolean TranslateChromosomePermanently = true;
    public static String FitnessBasis = "Retrieve";

    // Rank, Random, Rank & Random
    public static String Selection_Methdology = "Rank";

    // Retrieve, Transmit
    public static String VoI_Method_For_Fitness_Function = "Retrieve";

    // Random 1, Random 2, Lawn-Mower, Zig-Zag, Greedy
    public static String GA_Population_Initializtion_Type = "Random 2";
    public static Boolean Initialize_With_Seeds = false;
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int indexStatsArrayForCSV = 0;
    public static ArrayList<Chromosome> Population =
            new ArrayList<Chromosome>();
    public static double[][] StatsArrayForCSV =
            new double[NumberOfRuns * NumberOfGens][5];

    // Variable for Best Tour Chromosome
    public static Chromosome BestofAllRuns;
    public static int RunForBestChromosome;
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private SensorNetworkWorld MySW;

    public GeneticAlgorithm(SensorNetworkWorld sensorWorld, double Velocity,
        int NAUVs, int VerticalNodes, int HorizontalNodes) {
        this.MySW = sensorWorld;
        Distance_Scale = sensorWorld.getSensorNodes().get(1).getLocation()
                .distanceTo(sensorWorld.getSensorNodes().get(2).getLocation());
        AUV_Speed = Velocity;
        NumOfAUVs = NAUVs;
        NumofVNodes = VerticalNodes;
        NumofHNodes = HorizontalNodes;
        NumOfNodes = sensorWorld.getSensorNodes().size();
    }

    public static void main(String[] args) {
        RunOptimization();
    }

    public static void RunOptimization() {
        // Initialize Map
        Map MyMap = new Map(NumofVNodes, NumofHNodes);
        MyMap.InitializeMap("Mesh", Distance_Scale);

        // Initialize an AUV for packet initialization measurements
        AUV Explorer = new AUV(AUV_Speed, Distance_Scale, 0);

        // Initialize Packets for Nodes on the Map
        double LastPacketTS = IntializeNodePackets(NumofVNodes, NumofHNodes,
                Packet_Initializtion_Type, MyMap, Explorer, DistanceType,
                Packet_Initializtion_Magnitude,
                Packet_Initializtion_DesiredVoIFactor,
                Packet_Initializtion_HS_Magnitude,
                Packet_Initializtion_HS_DesiredVoIFactor);

        System.out.println("Final VoI Map");
        MyMap.PrintVoIMap(LastPacketTS);

        // Do not calculate fitness. Let fitness remain at -1. This chromosome
        // is redundant and should be replaced at first comparison.
        BestofAllRuns = new Chromosome(GA_Population_Initializtion_Type,
                NumOfAUVs, NumOfNodes, MyMap, DistanceType);

        // The Genetic Algorithm for finding
        for (int runs = 0; runs < NumberOfRuns; runs++) {
            System.out.println();
            System.out.println();
            System.out.println("Start of Run");
            int SeedNumber = -1;
            boolean WithSeed = false;
            if (runs < 4) {
                SeedNumber = runs;
                WithSeed = true;
            } else {
                WithSeed = false;
            }
            InitializePopulation(MyMap, LastPacketTS, WithSeed, SeedNumber);
            for (int generations =
                    0; generations < NumberOfGens; generations++) {
                CrossoverAndMutation(FitnessBasis,
                        TranslateChromosomeForFitness,
                        TranslateChromosomePermanently, MyMap, LastPacketTS,
                        CrossOverRate, MutationRate);
                Selection(Selection_Methdology);
                RecordStats(runs, generations);
            }
            if (BestofAllRuns.rawFitness < Population
                    .get(Population.size() - 1).rawFitness) {
                BestofAllRuns = Population.get(Population.size() - 1);
                RunForBestChromosome = runs;
            }
            PrintChromosomeAndStats(Population.get(Population.size() - 1), runs,
                    MyMap, LastPacketTS);
            ExterminatePopulation();
            System.out.println("End of Run");
        }
        System.out.println("Overall best Chromosome");
        PrintChromosomeAndStats(BestofAllRuns, RunForBestChromosome, MyMap,
                LastPacketTS);
    }

    public static void InitializePopulation(Map MyMap, double LastPacketTS,
        Boolean WithSeed, int SeedNumber) {
        for (int i = 0; i < PopulationSize; i++) {
            Chromosome Parent;
            if (WithSeed == true && i == 0 && SeedNumber == 0) {
                Parent = new Chromosome("Zig-Zag", NumOfAUVs, NumOfNodes, MyMap,
                        DistanceType);
            } else if (WithSeed == true && i == 0 && SeedNumber == 1) {
                Parent = new Chromosome("Lawn-Mower", NumOfAUVs, NumOfNodes,
                        MyMap, DistanceType);
            } else if (WithSeed == true && i == 0 && SeedNumber == 2) {
                Parent = new Chromosome("Greedy", NumOfAUVs, NumOfNodes, MyMap,
                        DistanceType);
            } else if (WithSeed == true && i == 0 && SeedNumber == 3) {
                Parent = new Chromosome("Greedy Retrieve Across Path",
                        NumOfAUVs, NumOfNodes, MyMap, DistanceType);
            } else {
                Parent = new Chromosome("Random 2", NumOfAUVs, NumOfNodes,
                        MyMap, DistanceType);
            }
            FitnessFunction X;
            X = new FitnessFunction();
            Parent.rawFitness = X.doRawFitness(FitnessBasis,
                    TranslateChromosomeForFitness, Parent, MyMap, LastPacketTS,
                    AUV_Speed, Distance_Scale, DistanceType);
            Population.add(Parent);
            // if ((WithSeed == true && SeedNumber == 0) || (WithSeed == true &&
            // SeedNumber == 1) || (WithSeed == true && SeedNumber == 2) ||
            // (WithSeed == true && SeedNumber == 3)) {
            if ((WithSeed == true && i == 0)) {
                System.out.println("Seed" + SeedNumber
                        + "\n-------------------------------------------------------------");
                Population.get(i).PrintChromosome(1);
                Population.get(i).PrintToursFitness(FitnessBasis,
                        TranslateChromosomeForFitness, MyMap, LastPacketTS,
                        AUV_Speed, Distance_Scale, DistanceType);
                System.out.println("");
            }
        }
    }

    public static void ExterminatePopulation() {
        Population.clear();
    }

    public static void CrossoverAndMutation(String FitnessBasis,
        boolean TranslateChromosomeForFitness,
        boolean TranslateChromosomePermanently, Map MyMap, double LastPacketTS,
        double CRate, double MRate) {
        for (int i = 0; i < PopulationSize; i++) {
            Chromosome Child = Population.get(i).CreateChildChromosome(MyMap,
                    DistanceType);
            Child.SelfCrossover(CRate);
            Child.Mutation(MRate);
            if (TranslateChromosomePermanently == true) {
                Child.ChromosomeTour = MyMap.AdjustChromosome(
                        Child.ChromosomeTour, Child.NumOfAUVs,
                        Child.AUVTourMaxLength, DistanceType);
            }
            FitnessFunction X;
            X = new FitnessFunction();
            Child.rawFitness = X.doRawFitness(FitnessBasis,
                    TranslateChromosomeForFitness, Child, MyMap, LastPacketTS,
                    AUV_Speed, Distance_Scale, DistanceType);
            Population.add(Child);
        }
    }

    public static void Selection(String Type) {
        if (Type == "Rank") {
            Collections.sort(Population);
            for (int i = 0; i < PopulationSize; i++) {
                Population.remove(0);
            }
        } else if (Type == "Random") {
            Collections.sort(Population);
            while (Population.size() > PopulationSize) {
                Population.remove(rand.nextInt(Population.size()));
            }
        } else if (Type == "Rank & Random") {
            Collections.sort(Population);
            int NumberOfElitist = PopulationSize / 5;
            while (Population.size() > PopulationSize) {
                Population.remove(
                        rand.nextInt(Population.size() - NumberOfElitist));
            }
        }
    }

    public static void RecordStats(int runs, int generations) {
        double[] FitnessOfGeneration = new double[PopulationSize];
        for (int i = 0; i < PopulationSize; i++) {
            FitnessOfGeneration[i] = Population.get(i).rawFitness;
        }
        StatsArrayForCSV[indexStatsArrayForCSV][0] = runs;
        StatsArrayForCSV[indexStatsArrayForCSV][1] = generations;
        // StatsArrayForCSV[indexStatsArrayForCSV][2] =
        // StdStats.max(FitnessOfGeneration);
        // StatsArrayForCSV[indexStatsArrayForCSV][3] =
        // StdStats.mean(FitnessOfGeneration);
        // StatsArrayForCSV[indexStatsArrayForCSV][4] =
        // StdStats.stddev(FitnessOfGeneration);
        // System.out.println("Best Fitness of Generation " + generations + " is
        // " + StatsArrayForCSV[indexStatsArrayForCSV][2]);
        // System.out.println("Average Fitness of Generation " + generations + "
        // is " + StatsArrayForCSV[indexStatsArrayForCSV][3]);
        // System.out.println("Std. Dev. Fitness of Generation " + generations +
        // " is " + StatsArrayForCSV[indexStatsArrayForCSV][4]);
        // System.out.println("-----------------------------");
        indexStatsArrayForCSV++;
    }

    public static void PrintStatsArray() {
        for (int i = 0; i < NumberOfRuns * NumberOfGens; i++) {
            System.out.print(StatsArrayForCSV[i][0] + "\t");
            System.out.print(StatsArrayForCSV[i][1] + "\t");
            System.out.print(StatsArrayForCSV[i][2] + "\t");
            System.out.print(StatsArrayForCSV[i][3] + "\t");
            System.out.print(StatsArrayForCSV[i][4] + "\t");
            System.out.println();
            System.out.println("-----------------------------");
        }
    }

    private static void ArrayToCSV(String sFileName) {
        try {
            FileWriter writer = new FileWriter(sFileName);
            writer.append("run,generation,best_gen_raw,avg_raw,std_dev_raw");
            writer.append('\n');
            for (int i = 0; i < NumberOfRuns * NumberOfGens; i++) {
                writer.append(Double.toString(StatsArrayForCSV[i][0]));
                writer.append(',');
                writer.append(Double.toString(StatsArrayForCSV[i][1]));
                writer.append(',');
                writer.append(Double.toString(StatsArrayForCSV[i][2]));
                writer.append(',');
                writer.append(Double.toString(StatsArrayForCSV[i][3]));
                writer.append(',');
                writer.append(Double.toString(StatsArrayForCSV[i][4]));
                writer.append('\n');
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void ArrayToCSVEachRun(String sFileName, int run) {
        try {
            FileWriter writer = new FileWriter(sFileName + ".csv");
            writer.append("run,generation,best_gen_raw,avg_raw,std_dev_raw");
            writer.append('\n');
            for (int i = 0; i < NumberOfGens; i++) {
                writer.append(String.format("%f",
                        StatsArrayForCSV[run * PopulationSize + i][0]));
                writer.append(',');
                writer.append(String.format("%f",
                        StatsArrayForCSV[run * PopulationSize + i][1]));
                writer.append(',');
                writer.append(String.format("%f",
                        StatsArrayForCSV[run * PopulationSize + i][2]));
                writer.append(',');
                writer.append(String.format("%f",
                        StatsArrayForCSV[run * PopulationSize + i][3]));
                writer.append(',');
                writer.append(String.format("%f",
                        StatsArrayForCSV[run * PopulationSize + i][4]));
                writer.append('\n');
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double IntializeNodePackets(int x, int y, String Method,
        Map M, AUV A, String DistanceType, double Magnitude,
        double DesiredVoIFactor, double HotSpotMagnitude,
        double HotSpotDesiredVoIFactor) {
        double Decay = -1; // to be set yet
        double HotSpotDecay = -1; // to be set yet
        double LatestTS = 0;
        if (Method != "External") {
            // Equal Initialization
            if (Method == "Equal") {
                for (int Xloc = 0; Xloc < x; Xloc++) {
                    for (int Yloc = 0; Yloc < y; Yloc++) {
                        LatestTS = 0;
                        for (int i = 0; i < 10; i++) {
                            LatestTS += 1;
                            Packet NewPacket = new Packet("Normal", Magnitude,
                                    Decay, LatestTS);
                            M.Nodes.get(Xloc).get(Yloc)
                                    .AcquirePacket(NewPacket);
                        }
                    }
                }
            }
            // Symmetrical Initialization
            else if (Method == "Symmetrical") {
                for (int Xloc = 0; Xloc < x; Xloc++) {
                    for (int Yloc = 0; Yloc < y; Yloc++) {
                        LatestTS = 5 * Math.pow(Xloc, 2) * Math.pow(Yloc, 2);
                        for (int i = 0; i < 10; i++) {
                            LatestTS += 1;
                            Packet NewPacket = new Packet("Normal", Magnitude,
                                    Decay, LatestTS);
                            M.Nodes.get(Xloc).get(Yloc)
                                    .AcquirePacket(NewPacket);
                        }
                    }
                }
            }
            // Random Initialization
            else if (Method == "Random") {
                int TS = 0;
                LatestTS = 0;
                for (int Xloc = 0; Xloc < x; Xloc++) {
                    for (int Yloc = 0; Yloc < y; Yloc++) {
                        TS = 0;
                        int RandNumOfPackets = rand.nextInt(1000) + 1; // Each
                                                                       // node
                                                                       // should
                                                                       // have
                                                                       // at
                                                                       // least
                                                                       // 1
                                                                       // packet
                        for (int i = 0; i < RandNumOfPackets; i++) {
                            TS += 10;
                            Packet NewPacket = new Packet("Normal", Magnitude,
                                    Decay, LatestTS);
                            M.Nodes.get(Xloc).get(Yloc)
                                    .AcquirePacket(NewPacket);
                            if (LatestTS < TS) {
                                LatestTS = TS;
                            }
                        }
                    }
                }
            }
            // Initializing Hot Spot
            LatestTS += 1;
            Packet NewPacket = new Packet("HotSpot", HotSpotMagnitude,
                    HotSpotDecay, LatestTS);
            M.Nodes.get(NumofVNodes - 1).get(NumofHNodes - 1)
                    .AcquirePacket(NewPacket);
        }

        else if (Method != "External") {
            // Initialize with External Information
        }
        // Setting up decays
        Decay = DetermineDecayConstant(M, A, Magnitude, DesiredVoIFactor, false,
                DistanceType, LatestTS);
        HotSpotDecay = DetermineDecayConstant(M, A, HotSpotMagnitude,
                HotSpotDesiredVoIFactor, true, DistanceType, LatestTS);
        for (int Xloc = 0; Xloc < x; Xloc++) {
            for (int Yloc = 0; Yloc < y; Yloc++) {
                for (int i = 0; i < M.Nodes.get(Xloc).get(Yloc).Packets
                        .size(); i++) {
                    String PacketClass = M.Nodes.get(Xloc).get(Yloc).Packets
                            .get(i).PacketDataClass;
                    if (PacketClass == "Normal") {
                        M.Nodes.get(Xloc).get(Yloc).Packets.get(i).VoIDecay =
                                Decay;
                    } else if (PacketClass == "HotSpot") {
                        M.Nodes.get(Xloc).get(Yloc).Packets.get(i).VoIDecay =
                                HotSpotDecay;
                    }
                }
            }
        }

        return LatestTS;
    }

    private static double DetermineDecayConstant(Map M, AUV A, double Magnitude,
        double DesiredVoIFactor, boolean ForHotspot, String DistanceType,
        Double LatestTS) {
        // t = S / V
        // VoI = A * e^-(Bt) => B = ln(VoI/A) * -1/t
        double Decay = 0;
        // double OffsetTime = LatestTS;
        double OffsetTime = 0;
        if (ForHotspot == false) {
            double TimeForAverageTourLength =
                    M.AverageTourLength(DistanceType) / A.AUVvelocity;
            Decay = Math.log((DesiredVoIFactor * Magnitude) / Magnitude)
                    * -(1 / (TimeForAverageTourLength + OffsetTime));
        } else {
            Node Source = M.GetNodeBasedOnId(0);
            Node Destination =
                    M.GetNodeBasedOnId((NumofVNodes * NumofHNodes) - 1);
            double DiagonalTravellingTime =
                    M.InterNodeDistance(Source, Destination, DistanceType)
                            / A.AUVvelocity;
            Decay = Math.log((DesiredVoIFactor * Magnitude) / Magnitude)
                    * -(1 / (DiagonalTravellingTime + OffsetTime));
        }
        return Decay;
    }

    public static void PrintChromosomeAndStats(Chromosome ToPrint,
        int RunNumber, Map M, double LastPacketTS) {
        System.out.println("-----------------------------");
        System.out.println("Best Chromosome for Run " + RunNumber);
        ToPrint.PrintChromosome(1);
        System.out.println("-----------------------------");
        ToPrint.PrintToursFitness(FitnessBasis, TranslateChromosomeForFitness,
                M, LastPacketTS, AUV_Speed, Distance_Scale, DistanceType);
        System.out.println("-----------------------------");
    }

}
