package main.java.underwater;

import java.io.Serializable;
import java.util.Iterator;

import main.java.agents.UWAgent;
import yaes.framework.agent.ACLMessage;
import yaes.framework.simulation.IContext;
import yaes.framework.simulation.ISimulationCode;
import yaes.framework.simulation.RandomVariable.Probe;
import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.SimulationOutput;
import yaes.sensornetwork.SensorNetworkSimulation;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SensorNode;

/**
 * This class provides the simulation setup, pre-process and post-process for
 * the underwater sensor network
 * 
 * @author Fahad Khan
 *
 */
public class UWSimulation extends SensorNetworkSimulation
    implements Serializable, ISimulationCode, UWConstants {
    private static final long serialVersionUID = 5309120410800315099L;

    @Override
    public void postprocess(SimulationInput sip, SimulationOutput sop,
        IContext theContext) {
        sop.update(Metrics_TransmissionEnergySum,
                sop.getValue(SENSORNETWORK_TRANSMISSION_ENERGY, Probe.SUM));
        sop.update(Metrics_MessagesSentSum,
                sop.getValue(SENSORNETWORK_MESSAGES_SENT, Probe.SUM));
    }

    @Override
    public void setup(SimulationInput sip, SimulationOutput sop,
        IContext theContext) {
        final UWContext context = (UWContext) theContext;
        context.initialize(sip, sop);
        // create variables for measured VoI and measured max VoI
        sop.createVariable(Var_Measured_VoI, true);
        sop.createVariable(Var_Measured_VoI_Max, true);

        sop.createVariable(Metrics_VoI_InstantRatio, true);
        sop.createVariable(Metrics_VoI_Sum, true);
        sop.createVariable(Metrics_VoI_Instant, true);
        sop.createVariable(Metrics_VoI_AggregatedRatio, true);

        // sop.update(Var_Measured_VoI, 0.0);
        // sop.update(Var_Measured_VoI_Max, 0.0);

        // sop.update(Metrics_VoI_InstantRatio, 0.0);
        // sop.update(Metrics_VoI_AggregatedRatio, 0.0);
        // sop.update(Metrics_VoI_Sum, 0.0);
    }

    @Override
    public int update(double time, SimulationInput sip, SimulationOutput sop,
        IContext theContext) {

        UWContext context = (UWContext) theContext;
        SensorNetworkWorld sensorWorld = context.getWorld();
        sensorWorld.setTime((int) time);
        for (SensorNode element : sensorWorld.getSensorNodes()) {
            if (element.isEnabled())
                element.update();
            if (element.getAgent() instanceof UWAgent) {
                UWAgent agent = (UWAgent) element.getAgent();
                Iterator<Double> itr =
                        agent.getMessageBuffer().keySet().iterator();
                double finalVoI = 0.0;
                while (itr.hasNext()) {
                    double initialTime = itr.next();
                    ACLMessage msg = agent.getMessageBuffer().get(initialTime);
                    double t = time - initialTime;
                    finalVoI += (double) msg.getValue(FIELD_VOI) * Math.pow(
                            (double) msg.getValue(FIELD_VOI_DECAY_TIME), (t));
                }
            }
        }
        sensorWorld.getSinkNode().update();

        if (context.getVisualizer() != null) {
            context.getVisualizer().update();
            try {
                Thread.sleep(10);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        sensorWorld.messageFlush();

        double acheivedRatio = 0.0;
        if (sop.getValue(Var_Measured_VoI_Max, Probe.SUM) != 0)
            acheivedRatio = sop.getValue(Var_Measured_VoI, Probe.LASTVALUE)
                    / sop.getValue(Var_Measured_VoI_Max, Probe.LASTVALUE);
        sop.update(Metrics_VoI_InstantRatio, acheivedRatio);

        acheivedRatio = 0.0;
        if (sop.getValue(Var_Measured_VoI_Max, Probe.SUM) != 0)
            acheivedRatio = sop.getValue(Var_Measured_VoI, Probe.SUM)
                    / sop.getValue(Var_Measured_VoI_Max, Probe.SUM);
        sop.update(Metrics_VoI_AggregatedRatio, acheivedRatio);

        sop.update(Metrics_VoI_Sum, sop.getValue(Var_Measured_VoI, Probe.SUM));
        sop.update(Metrics_VoI_Instant,
                sop.getValue(Var_Measured_VoI, Probe.LASTVALUE));
        return 1;
    }

}
