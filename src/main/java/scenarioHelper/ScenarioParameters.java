package scenarioHelper;

import underwater.UWConstants;
import yaes.framework.simulation.SimulationInput;
import yaes.sensornetwork.constSensorNetwork;

/**
 * This class sets the scenario parameters for the environments, its modules
 * (the sensornodes) and sets others parameters like visual display and random
 * seed etc
 * 
 * @author Saad
 *
 */
public class ScenarioParameters implements UWConstants, constSensorNetwork {

    /**
     * This method sets the common parameters for the setup
     * 
     * @param model
     */
    public static void setCommonParams(SimulationInput model) {
        model.setParameter(randomSeed, 133);
        model.setParameter(VisualDisplay.NO);
        model.setParameter(SimControl_KeepTimeSeries, 0);
    }

    /**
     * This method sets the parameters for the network deployment
     * 
     * @param model
     */
    public static void setNetworkDeploymentParams(SimulationInput model) {
        model.setParameter(constSensorNetwork.SensorDeployment_SinkNodeX, 10.0);
        model.setParameter(constSensorNetwork.SensorDeployment_SinkNodeY, 10.0);
        model.setParameter(constSensorNetwork.SensorDeployment_SensorRange,
                70.0);
        model.setParameter(
                constSensorNetwork.SensorDeployment_TransmissionRange, 70.0); // for
                                                                               // 100
                                                                               // nodes
                                                                               // keep
                                                                               // it
                                                                               // 70
        model.setParameter(constSensorNetwork.SensorDeployment_SensorNodeCount,
                50);
    }

    /**
     * This method is used to set the map parameters that is to be set the
     * background for simulation environment
     * 
     * @param model
     */
    public static void setMapParams(SimulationInput model) {
        model.setParameter(MAP_WIDTH, 500);
        model.setParameter(MAP_HEIGHT, 374);
        model.setParameter(MAP_OBSTACLES, "Strait_Pixelated.png");
        model.setParameter(MAP_BACKGROUND, "Strait-Bkgnd.png");
        model.setParameter(MAP_COLORED_BACKGROUND,
                "Strait_of_Gibraltar_Colored.png");
    }

    /**
     * This sets the sensor node parameters for simulation
     * 
     * @param model
     */
    public static void setSensorNodeParams(SimulationInput model) {
        model.setParameter(DECAYTIME, 0.1);
        model.setParameter(SINK_SPEED, 1.0);
        model.setParameter(CONTINUOUS_SAMPLING, 1);
        model.setParameter(NUM_HOTSPOTS, 5);
    }
}
