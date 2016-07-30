package main.java.underwater;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import main.java.scenarioHelper.ScenarioParameters;
import yaes.Version;
import yaes.framework.simulation.Simulation;
import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.parametersweep.ExperimentPackage;
import yaes.framework.simulation.parametersweep.ParameterSweep;
import yaes.framework.simulation.parametersweep.ParameterSweep.ParameterSweepType;
import yaes.framework.simulation.parametersweep.ParameterSweepHelper;
import yaes.framework.simulation.parametersweep.ScenarioDistinguisher;
import yaes.ui.text.TextUi;

/**
 * This is the main file for the underwater sensor network. In this file we cam
 * set the location output directories, create options for user-input
 * simulation, define the simulation input parameters and
 * 
 * @author Fahad Khan
 *
 */
public class UnderWaterMain implements Serializable, UWConstants {
    private static final long serialVersionUID = 8443564240044510957L;
    private static final String MENU_SETUP_RUN =
            "Setup the UnderWater Sensor Network";
    // private static final String MENU_SETUP_ROUTE_1 = "Setup the UnderWater
    // Sensor Network Route 1";
    // private static final String MENU_SETUP_ROUTE_2 = "Setup the UnderWater
    // Sensor Network Route 2";
    private static final String MENU_SETUP_LAWNMOVER = "PathPlanning LawnMover";
    private static final String MENU_SETUP_QLEARNING =
            "PathPlanning Q-Learning";
    // private static final String MENU_SETUP_QLEARNING_VISUAL = "PathPlanning
    // Q-Learning (Visual)";
    private static final String MENU_SETUP_ASTAR = "PathPlanning Astar";
    private static final String MENU_SETUP_RANDOM =
            "PathPlanning Random Movement";
    private static final String MENU_SETUP_GREEDY =
            "PathPlanning Greedy Movement";

    // private static final String MENU_SETUP_QLEARNING_OBSTACLES =
    // "PathPlanning Q-Learning - Obstacles";
    // private static final String MENU_SETUP_QLEARNING_OBSTACLES_VISUAL =
    // "PathPlanning Q-Learning - Obstacles (Visual)";
    // private static final String MENU_SETUP_ASTAR_OBSTACLES = "PathPlanning
    // AStar - Obstacles";
    private static final String MENU_SETUP_COMPARE_SPEED =
            "Compare various speeds with decay time for VoI";
    private static final String MENU_SETUP_COMPARE_HOTSPOTS =
            "Compare effects of incremental hotspots on pathplanners";
    private static final String MENU_SETUP_COMPARE_EVO_VOI =
            "Time Series VoI for different path planners";
    private static final String MENU_SETUP_COMPARE_DECAYTIME =
            "Time Series for TAU effects on the VoI";

    private static final String MENU_CONTINUOUS_SAMPLING_NO =
            "With Continuous Node Sampling - NO";
    private static final String MENU_CONTINUOUS_SAMPLING_YES =
            "With Continuous Node Sampling - YES";

    private static LearningMethod method = LearningMethod.QLEARNING_VISUAL;
    private static int sampling = 1;

    // creation of output directories
    public static final File outputDir = new File("data/UnderWater/output");
    public static final File output2Dir = new File("data/UnderWater/output2");
    public static final File graphDir = new File("data/UnderWater/graphs");
    public static final File logDir = new File("data/UnderWater/log");
    public static final File cache = new File("data/UnderWater/cache");

    static {
        outputDir.mkdirs();
        graphDir.mkdirs();
        logDir.mkdirs();
        cache.mkdirs();
    }

    public static void main(String[] args) throws Exception {
        TextUi.println(Version.versionString());
        final List<String> menu = new ArrayList<String>();
        final List<String> menu2 = new ArrayList<String>();

        String result = null;
        String result2 = null;

        String defaultChoice = UnderWaterMain.MENU_SETUP_QLEARNING;
        // menu.add(UnderWaterMain.MENU_SETUP_RUN);
        // menu.add(UnderWaterMain.MENU_SETUP_ROUTE_1);
        // menu.add(UnderWaterMain.MENU_SETUP_ROUTE_2);
        menu.add(UnderWaterMain.MENU_SETUP_LAWNMOVER);
        menu.add(UnderWaterMain.MENU_SETUP_QLEARNING);
        // menu.add(UnderWaterMain.MENU_SETUP_QLEARNING_OBSTACLES);
        menu.add(UnderWaterMain.MENU_SETUP_ASTAR);
        menu.add(UnderWaterMain.MENU_SETUP_RANDOM);
        menu.add(UnderWaterMain.MENU_SETUP_GREEDY);
        // menu.add(UnderWaterMain.MENU_SETUP_ASTAR_OBSTACLES);
        // menu.add(UnderWaterMain.MENU_SETUP_QLEARNING_VISUAL);
        // menu.add(UnderWaterMain.MENU_SETUP_QLEARNING_OBSTACLES_VISUAL);
        menu.add(UnderWaterMain.MENU_SETUP_COMPARE_SPEED);
        menu.add(UnderWaterMain.MENU_SETUP_COMPARE_EVO_VOI);
        menu.add(UnderWaterMain.MENU_SETUP_COMPARE_DECAYTIME);
        menu.add(UnderWaterMain.MENU_SETUP_COMPARE_HOTSPOTS);

        String defaultChoice2 = UnderWaterMain.MENU_CONTINUOUS_SAMPLING_YES;
        menu2.add(MENU_CONTINUOUS_SAMPLING_YES);
        menu2.add(MENU_CONTINUOUS_SAMPLING_NO);

        if (result == null) {
            result = TextUi.menu(menu, defaultChoice, "Choose:");
            if (result2 == null)
                result2 = TextUi.menu(menu2, defaultChoice2, "Choose:");
            // result2 = defaultChoice2;
        }

        switch (result2) {
        case UnderWaterMain.MENU_CONTINUOUS_SAMPLING_YES:
            sampling = 1;
            break;
        case UnderWaterMain.MENU_CONTINUOUS_SAMPLING_NO:
            sampling = 0;
            break;
        default:
            break;

        }

        switch (result) {
        case UnderWaterMain.MENU_SETUP_LAWNMOVER:
            method = LearningMethod.LAWNMOVER;
            doSimpleRun();
            break;

        case UnderWaterMain.MENU_SETUP_QLEARNING:
            method = LearningMethod.QLEARNING;
            doSimpleRun();
            break;

        case UnderWaterMain.MENU_SETUP_RANDOM:
            method = LearningMethod.RANDOM;
            doSimpleRun();
            break;

        case UnderWaterMain.MENU_SETUP_GREEDY:
            method = LearningMethod.GREEDY_LEARNING;
            doSimpleRun();
            break;

        // case UnderWaterMain.MENU_SETUP_QLEARNING_OBSTACLES:
        // method = LearningMethod.QLEARNING_OBSTACLES;
        // doSimpleRun();
        // break;
        case UnderWaterMain.MENU_SETUP_ASTAR:
            method = LearningMethod.ASTAR;
            doSimpleRun();
            break;
        // case UnderWaterMain.MENU_SETUP_ASTAR_OBSTACLES:
        // method = LearningMethod.ASTAR_OBSTACLES;
        // doSimpleRun();
        // break;
        // case UnderWaterMain.MENU_SETUP_QLEARNING_VISUAL:
        // method = LearningMethod.QLEARNING_VISUAL;
        // doSimpleRun();
        // break;
        // case UnderWaterMain.MENU_SETUP_QLEARNING_OBSTACLES_VISUAL:
        // method = LearningMethod.QLEARNING_OBSTACLES_VISUAL;
        // doSimpleRun();
        // break;
        case UnderWaterMain.MENU_SETUP_COMPARE_SPEED:
            runFullSimulation();
            break;
        case UnderWaterMain.MENU_SETUP_COMPARE_DECAYTIME:
            runFullSimulationDecayTime();
            break;
        case UnderWaterMain.MENU_SETUP_COMPARE_EVO_VOI:
            runFullSimulationVoI();
            break;
        case UnderWaterMain.MENU_SETUP_COMPARE_HOTSPOTS:
            runFullHotSpotSimulation();
            break;

        default:
            break;
        }

        Toolkit.getDefaultToolkit().beep();
        TextUi.println("Done, exiting");
        System.exit(0);

    }

    /**
     * Performs a sample run of the value of deep inspection scenario
     * 
     * @param model
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     */
    public static void doSimpleRun()
        throws InstantiationException, IllegalAccessException, IOException {
        final SimulationInput sip = createDefaultSimulationInput();
        sip.setStopTime(1000);
        final UWContext context = new UWContext();
        // logDir.mkdirs();
        sip.setParameter(method);
        sip.setParameter(VisualDisplay.YES);
        // sip.setParameter(routeNumber, 0);
        Simulation.simulate(sip, UWSimulation.class, context, logDir);
    }

    public static void runFullSimulation() {
        SimulationInput model = createDefaultSimulationInput();
        model.setStopTime(500);
        // variable path planners
        // comparePathPlanners(model);
        compareVariableSpeed(model);
        // timescale
        // plotEvolutionOfVoI(model);
    }

    public static void runFullHotSpotSimulation() {
        SimulationInput model = createDefaultSimulationInput();
        model.setStopTime(600);
        compareVariableHotSpots(model);
    }

    public static void runFullSimulationDecayTime() {
        SimulationInput model = createDefaultSimulationInput();
        model.setStopTime(500);
        plotEvolutionOfVoIDecay(model);
    }

    // plot evolution of VoI
    public static void runFullSimulationVoI() {
        SimulationInput model = createDefaultSimulationInput();
        model.setStopTime(1000);
        plotEvolutionOfVoI(model);
    }

    /**
     * Creates the graphs for plotting the different values function of the
     * number of intruder nodes
     * 
     * @param model
     */
    private static void comparePathPlanners(SimulationInput model) {
        ExperimentPackage pack = new ExperimentPackage(outputDir, graphDir);
        pack.setModel(model);

        ParameterSweep sweepDiscrete = getPathPlannerTypes();
        pack.addParameterSweep(sweepDiscrete);
        // ParameterSweep sweepSpeed =
        // ParameterSweepHelper.generateParameterSweepDouble("TheSinkSpeed",
        // UWConstants.SINK_SPEED,
        // 2, 0.0, 10.0);
        // pack.addParameterSweep(sweepSpeed);
        ParameterSweep decayTime =
                ParameterSweepHelper.generateParameterSweepDouble(
                        "VoIDecayTime", UWConstants.DECAYTIME, 10, 0.5, 1.0);
        pack.addParameterSweep(decayTime);

        pack.setVariableDescription(Metrics_VoI_Sum, "Collected VoI");
        pack.setVariableDescription(Metrics_VoI_InstantRatio,
                "Collected VoI / Maximum Avaliable VoI");
        pack.setVariableDescription(Metrics_VoI_AggregatedRatio,
                "Collected VoI / Maximum Avaliable VoI");
        pack.setVariableDescription(UWConstants.DECAYTIME,
                "Decay Time of Value of Information");
        pack.initialize();
        pack.run();
        pack.generateGraph(Metrics_VoI_Sum, "Total Value of Information",
                "Metric_value_of_information");
        pack.generateGraph(Metrics_VoI_InstantRatio, "",
                "Metric_VoI_Ratio_Instant");
        pack.generateGraph(Metrics_VoI_AggregatedRatio, "",
                "Metric_VoI_Ratio_Aggregated");
    }

    /**
     * Creates the graphs for plotting the different values function of the
     * number of intruder nodes
     * 
     * @param model
     */
    private static void compareVariableSpeed(SimulationInput model) {
        ExperimentPackage pack = new ExperimentPackage(outputDir, graphDir);
        model.setParameter(LearningMethod.ASTAR);
        pack.setModel(model);
        ParameterSweep sweepDiscrete = getPathPlannerTypes();
        pack.addParameterSweep(sweepDiscrete);
        ParameterSweep sweepSpeed =
                ParameterSweepHelper.generateParameterSweepDouble(
                        "TheSinkSpeed", UWConstants.SINK_SPEED, 2, 0.0, 10.0);
        pack.addParameterSweep(sweepSpeed);
        pack.setVariableDescription(Metrics_VoI_Sum, "Collected VoI");
        pack.setVariableDescription(Metrics_VoI_InstantRatio,
                "Collected VoI / Maximum Avaliable VoI");
        pack.setVariableDescription(Metrics_VoI_AggregatedRatio,
                "Collected VoI / Maximum Avaliable VoI");
        pack.setVariableDescription(UWConstants.SINK_SPEED,
                "Speed of the sink node");
        pack.initialize();
        pack.run();
        pack.generateGraph(Metrics_VoI_Sum, "Total Value of Information",
                "Metric_value_of_information");
        pack.generateGraph(Metrics_VoI_InstantRatio, "",
                "Metric_VoI_Ratio_Instant");
        pack.generateGraph(Metrics_VoI_AggregatedRatio, "",
                "Metric_VoI_Ratio_Aggregated");
    }

    private static void compareVariableHotSpots(SimulationInput model) {
        ExperimentPackage pack = new ExperimentPackage(outputDir, graphDir);
        model.setParameter(LearningMethod.ASTAR);
        pack.setModel(model);
        ParameterSweep sweepDiscrete = getPathPlannerTypes();
        pack.addParameterSweep(sweepDiscrete);
        ParameterSweep sweepSpeed = ParameterSweepHelper
                .generateParameterSweepInteger("TheNumberOfHotSpots",
                        UWConstants.NUM_HOTSPOTS, 0, 5, 1);
        pack.addParameterSweep(sweepSpeed);

        ParameterSweep sweepRandom = ParameterSweepHelper
                .generateParameterSweepInteger("label", randomSeed, 0, 10); // was
                                                                            // 50
        sweepRandom.setType(ParameterSweepType.Repetition);
        pack.addParameterSweep(sweepRandom);

        pack.setVariableDescription(Metrics_VoI_Sum, "Collected VoI");
        pack.setVariableDescription(Metrics_VoI_InstantRatio,
                "Collected VoI / Maximum Avaliable VoI");
        pack.setVariableDescription(Metrics_VoI_AggregatedRatio,
                "Collected VoI / Maximum Avaliable VoI");
        pack.setVariableDescription(Metrics_VoI_Instant,
                "Current Collected Value of information");

        pack.setVariableDescription(UWConstants.NUM_HOTSPOTS,
                "Number of hotspots");
        pack.initialize();
        pack.run();
        pack.generateGraph(Metrics_VoI_Sum, "Total Value of Information",
                "Metric_value_of_information");
        pack.generateGraph(Metrics_VoI_InstantRatio, "",
                "Metric_VoI_Ratio_Instant");
        pack.generateGraph(Metrics_VoI_AggregatedRatio, "",
                "Metric_VoI_Ratio_Aggregated");
        pack.generateGraph(Metrics_VoI_Instant, "",
                "Current Collected Value of information");

    }

    /**
     * @param model
     */
    private static void plotEvolutionOfVoI(SimulationInput rootModel) {
        SimulationInput model = new SimulationInput(rootModel);
        ExperimentPackage pack = new ExperimentPackage(output2Dir, graphDir);
        pack.setModel(model);
        ParameterSweep sweepDiscrete = getPathPlannerTypes();
        pack.addParameterSweep(sweepDiscrete);
        pack.initialize();
        pack.run();
        pack.generateTimeSeriesGraph(Metrics_VoI_Sum, "Value of information",
                "Time", "ts_voi");
        pack.generateTimeSeriesGraph(Metrics_VoI_Instant,
                "Current Collected Value of information", "Time",
                "ts_voi_instant");
        pack.generateTimeSeriesGraph(Metrics_VoI_InstantRatio,
                "Instant VoI / Maximum Instant VoI", "Time",
                "ts_instant_voiratio");
        pack.generateTimeSeriesGraph(Metrics_VoI_AggregatedRatio,
                "VoI / Maximum VoI", "Time", "ts_sum_voiratio");

    }

    /**
     * @param model
     */
    private static void plotEvolutionOfVoIDecay(SimulationInput rootModel) {
        SimulationInput model = new SimulationInput(rootModel);
        ExperimentPackage pack = new ExperimentPackage(output2Dir, graphDir);
        pack.setModel(model);
        ParameterSweep sweepDiscrete = getDecayTypes();
        pack.addParameterSweep(sweepDiscrete);
        pack.initialize();
        pack.run();
        pack.generateTimeSeriesGraph(Metrics_VoI_Sum, "Value of information",
                "Time", "ts_voi");
        pack.generateTimeSeriesGraph(Metrics_VoI_InstantRatio,
                "Instant VoI / Maximum Instant VoI", "Time",
                "ts_instant_voiratio");
        pack.generateTimeSeriesGraph(Metrics_VoI_AggregatedRatio,
                "VoI / Maximum VoI", "Time", "ts_sum_voiratio");
    }

    /**
     * 
     * Add the different variants
     * 
     * @return
     */
    public static ParameterSweep getPathPlannerTypes() {
        ParameterSweep sweepDiscrete = new ParameterSweep("pathplanners");
        ScenarioDistinguisher sd = null;
        // The LawnMover Path Planner
        sd = new ScenarioDistinguisher("LPP");
        sd.setDistinguisher(LearningMethod.LAWNMOVER);
        sweepDiscrete.addDistinguisher(sd);
        // The Random Movement Path Planner
        sd = new ScenarioDistinguisher("RPP");
        sd.setDistinguisher(LearningMethod.RANDOM);
        sweepDiscrete.addDistinguisher(sd);
        // The GreedyMovement Path Planner
        sd = new ScenarioDistinguisher("GPP");
        sd.setDistinguisher(LearningMethod.GREEDY_LEARNING);
        sweepDiscrete.addDistinguisher(sd);
        // The GreedyMovement Path Planner
        sd = new ScenarioDistinguisher("PROB-GPP");
        sd.setDistinguisher(LearningMethod.PROBABLISTIC_GREEDY);
        sweepDiscrete.addDistinguisher(sd);
        return sweepDiscrete;
    }

    /**
     * 
     * Add the different variants for the decay time
     * 
     * @return
     */
    public static ParameterSweep getDecayTypes() {
        ParameterSweep sweepDiscrete = new ParameterSweep("DecayTypes");
        ScenarioDistinguisher sd = null;
        // Decay Time = 0.04
        sd = new ScenarioDistinguisher("Tau-0.04");
        sd.setDistinguisher(LearningMethod.ASTAR);
        sd.setDistinguisher(UWConstants.DECAYTIME, 0.04);
        sweepDiscrete.addDistinguisher(sd);
        // Decay Time = 0.2
        sd = new ScenarioDistinguisher("Tau-0.2");
        sd.setDistinguisher(LearningMethod.ASTAR);
        sd.setDistinguisher(UWConstants.DECAYTIME, 0.2);
        sweepDiscrete.addDistinguisher(sd);
        // Decay Time = 1.0
        sd = new ScenarioDistinguisher("Tau-1.0");
        sd.setDistinguisher(LearningMethod.ASTAR);
        sd.setDistinguisher(UWConstants.DECAYTIME, 1.0);
        sweepDiscrete.addDistinguisher(sd);
        return sweepDiscrete;
    }

    /**
     * Run the Route # 1 for the underwater UAV
     * 
     * @param model
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     */
    public static void doSimpleRoute1()
        throws InstantiationException, IllegalAccessException, IOException {
        final SimulationInput sip = createDefaultSimulationInput();
        sip.setParameter(routeNumber, 1);
        sip.setStopTime(10000);
        final UWContext context = new UWContext();
        // logDir.mkdirs();
        sip.setParameter(VisualDisplay.YES);

        Simulation.simulate(sip, UWSimulation.class, context, logDir);
    }

    /**
     * Run the Route # 2 for the underwater UAV
     * 
     * @param model
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     */
    public static void doSimpleRoute2()
        throws InstantiationException, IllegalAccessException, IOException {
        final SimulationInput sip = createDefaultSimulationInput();
        sip.setParameter(UWConstants.routeNumber, 2);
        sip.setStopTime(5000);
        final UWContext context = new UWContext();
        // logDir.mkdirs();
        sip.setParameter(VisualDisplay.YES);
        Simulation.simulate(sip, UWSimulation.class, context, logDir);
    }

    /**
     * Creates the default simulation input
     * 
     * @return
     */
    public static SimulationInput createDefaultSimulationInput() {
        SimulationInput model = new SimulationInput();
        model.setContextClass(UWContext.class);
        model.setSimulationClass(UWSimulation.class);
        model.setParameter(SensorAgentClass.UnderWater);

        ScenarioParameters.setNetworkDeploymentParams(model);
        ScenarioParameters.setCommonParams(model);
        ScenarioParameters.setSensorNodeParams(model);
        ScenarioParameters.setMapParams(model);
        return model;
    }
}
