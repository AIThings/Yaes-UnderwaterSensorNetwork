package main.java.sensorutils;

import java.util.ArrayList;
import java.util.List;

import yaes.ui.text.TextUi;

public class VoiSensorNode {

    /**
     * Calculate the value of information
     * 
     * @param list
     * @return
     */
    public static double calculateVoI(List<DataPackage> list, double tcurrent) {
        double retval = 0;
        for (DataPackage dp : list) {
            retval += dp.value * Math.pow(dp.decay, (tcurrent - dp.time));
        }
        return retval;
    }

    public static void main(String arg[]) {
        TextUi.print("VoiSensorNode");
        double robotVoi = 0;
        List<DataPackage> collection = new ArrayList<>();
        for (double t = 0; t < 30; t++) {
            double value = 0;
            double decay = 0;
            if (t > 10 && t < 20) {
                // hot spot, high value but urgent
                value = 100; // high value
                decay = 0.9;
            } else {
                value = 10;
                decay = 0.99;
            }
            DataPackage ns = new DataPackage(t, value, decay);
            collection.add(ns);
            // visit by the robot
            if (t == 5 || t == 25 | t == 29) {
                robotVoi += calculateVoI(collection, t);
                collection.clear();
            }
            TextUi.println("Sensed:" + ns);
            TextUi.println("Voi at the node: " + calculateVoI(collection, t)
                    + " robot voi: " + robotVoi);
        }
    }

}
