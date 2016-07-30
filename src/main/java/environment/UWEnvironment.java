package main.java.environment;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import main.java.agents.UWAgent;
import yaes.sensornetwork.Environment;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SensorNode;
import yaes.world.physical.map.SimpleFreeGround;

/**
 * This class provides different environments for the Underwater SensorNetwork
 * 
 * @author Fahad Khan
 *
 */
public class UWEnvironment extends Environment implements Serializable {
    private static final long serialVersionUID = -5691617063123755471L;
    private List<Shape> shapes = new ArrayList<Shape>();
    private boolean active = false;

    /*
     * public UWEnvironment(){ super(); this.setFullArea(new
     * Rectangle2D.Double(0, 0, 1500, 1500)); this.setInterestArea(new
     * Rectangle2D.Double(200, 200, 1200, 1200));
     * this.setSensorDistributionArea(new Rectangle2D.Double(200, 200, 1200,
     * 1200)); }
     */
    public UWEnvironment() {
        super();
        this.setFullArea(new Rectangle2D.Double(0, 0, 500, 374));
        this.setInterestArea(new Rectangle2D.Double(100, 100, 200, 200));
        this.setSensorDistributionArea(
                new Rectangle2D.Double(100, 100, 200, 200));
        this.setTheMap(new SimpleFreeGround("Strait_of_Gibraltar_Colored.png"));
    }

    /*
     * public static Environment generateUWStandard() { Environment env = new
     * Environment(); env.setFullArea(new Rectangle2D.Double(0, 0, 1500, 1500));
     * env.setInterestArea(new Rectangle2D.Double(200, 200, 1200, 1200));
     * env.setSensorDistributionArea(new Rectangle2D.Double(200, 200, 1200,
     * 1200)); return env; }
     */

    /**
     * Applies the event to all the sensor nodes
     */
    public void applyObstacles(SensorNetworkWorld snw) {
        active = true;
        for (SensorNode sn : snw.getSensorNodes()) {
            for (Shape s : shapes) {
                if (s.contains(sn.getLocation().asPoint())) {
                    sn.setEnabled(false);
                }
            }
        }
    }

    /**
     * Applies the event to all the sensor nodes
     */
    public void applyROI(SensorNetworkWorld snw, Shape s, double valueOfData,
        double decayTime) {
        active = true;
        for (SensorNode sn : snw.getSensorNodes()) {
            if (s.contains(sn.getLocation().asPoint())) {
                UWAgent agent = (UWAgent) sn.getAgent();
                agent.setValueOfData(valueOfData);
                agent.setAgentVoI_DecayRate(decayTime);
            }
        }
    }

    /**
     * @return the shapes
     */
    public List<Shape> getShapes() {
        return shapes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
