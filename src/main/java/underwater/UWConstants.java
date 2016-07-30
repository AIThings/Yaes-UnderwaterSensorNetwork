package main.java.underwater;

import java.io.File;

import yaes.sensornetwork.constSensorNetwork;
import yaes.sensornetwork.model.constSensorNetworkWorld;

/**
 * This interface provides the constant used in the Underwater sensorNetwork
 * 
 * @author Fahad Khan
 *
 */
public interface UWConstants
    extends constSensorNetwork, constSensorNetworkWorld {

    // Creation of required directories for storing log, output and graphs
    public final static File logDir = new File("data/log");
    public final static File outputDir = new File("data/output");
    public final static File graphDir = new File("data/graphs");

    // TODO: Remember to keep the string information unqiue to avoid overwritten
    // data
    public static final String routeNumber = "RouteNumber";
    public static final String DECAYTIME = "VoIDecayTime";
    public static final String SINK_SPEED = "TheSinkSpeed";
    public static final String NUM_HOTSPOTS = "TheNumberOfHotSpots";
    public static final String TIME = "Time";
    public static final String CONTINUOUS_SAMPLING = "ContinousSample";
    public static final String Mobile_SinkCount = "TheNumberOfMobileSinks";
    // Map Constants
    public static final String ACCESSIBLE = "Accessible";
    public static final String OBSTACLES = "obstacle";

    public static enum LearningMethod {
        QLEARNING_VISUAL,
        ASTAR,
        QLEARNING_OBSTACLES_VISUAL,
        ASTAR_OBSTACLES,
        QLEARNING,
        QLEARNING_OBSTACLES,
        LAWNMOVER,
        RANDOM,
        GREEDY_LEARNING,
        PROBABLISTIC_GREEDY,
        GENETIC_OPTIMIZATION;
    }

    public static final String FIELD_VOI = "ValueOfInformation";
    public static final String FIELD_VOI_MAX = "ValueOfInformationMax";
    public static final String FIELD_VOI_AVG = "ValueOfInformationAvg";

    public static final String FIELD_VOI_DECAY_TIME = "DecayTimeForVoI";

    // metrics to be measured
    public static final String Metrics_VOI_SUM =
            "Metrics_Sum_ValueOfInformation";
    // metrics to be measured
    public static final String Var_Measured_VoI = "Measured_ValueOfInformation";
    public static final String Var_Measured_VoI_Max =
            "Measured_Max_ValueOfInformation";

    public static final String Metrics_VoI_Sum =
            "Metrics_IVE_DeltaValueOfInformation";
    public static final String Metrics_VoI_Instant = "Metrics_VoI_Instant";

    public static final String Metrics_VoI_AggregatedRatio =
            "Metrics_IVE_AverageValueOfInformation";

    public static final String Metrics_VoI_InstantRatio = "RatioVoI";

    public static final String randomSeed = "randomSeed";

    public static final String MAP_COLORED_BACKGROUND = "MapColoredBackground";
    public static final String MAP_BACKGROUND = "MapBackground";
    public static final String MAP_OBSTACLES = "MapObstacles";
    public static final String MAP_HEIGHT = "MapHeight";
    public static final String MAP_WIDTH = "MapWidth";
}
