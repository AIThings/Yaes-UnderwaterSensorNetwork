package main.java.agents;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import main.java.underwater.UWMessage;
import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.model.Perception;
import yaes.sensornetwork.model.SensingHistory;
import yaes.sensornetwork.model.SensorNetworkWorld;

/**
 * This class creates a simple Underwater static sensor node agent
 * 
 * @author SaadKhan
 *
 */
public class UWAgent extends AbstractSensorAgent implements Serializable {
    private static final long serialVersionUID = 4557468456161930860L;
    private HashMap<Double, ACLMessage> messageBuffer;
    private boolean transmitEnable = true;
    private double agentVoI_DecayRate = 1.0;
    private double valueOfData = 1.0;

    private boolean sampling = true;
    private int sampleDecision = 1;

    public UWAgent(String name, SensorNetworkWorld sensorWorld) {
        super(name, sensorWorld);
        this.setMessageBuffer(new HashMap<Double, ACLMessage>());

        // TODO Auto-generated constructor stub
    }

    @Override
    public void action() {
        if (isTransmitEnable() && isSampling()) {
            ACLMessage message =
                    UWMessage.createMessage(this.getNode().getName(),
                            this.getSensorWorld().getSinkNode().getName(),
                            this.getSensorWorld().getTime(), valueOfData,
                            agentVoI_DecayRate);
            message.setDestination(
                    this.getSensorWorld().getSinkNode().getName());
            getMessageBuffer().put(this.getSensorWorld().getTime(), message);
            setSampling(false); // turn off sampling by default
        }
        if (getSampleDecision() == 1)
            setSampling(true); // turn on sampling if required

        // if the node finds that sinkNode is in vicinity then transmit
        // the message
        /*
         * if(this.getSensorRangeShape().
         * contains(this.getSensorWorld().getSinkNode().getLocation().asPoint())
         * ) this.transmit(message);
         */
        // read out his stuff from the sensing manager
        final SensingHistory sensingHistory =
                getSensorWorld().getSensingHistory(this.getNode());
        final List<Perception> perceptions =
                sensingHistory.extractNewPerceptions();
        for (final Perception p : perceptions) {
            switch (p.getType()) {
            case IntruderPresence:
                break;
            case NoPerception:
                break;
            case Overhearing:
                break;
            case ReceivedMessage:
                break;
            case SinkNodePrescene: // the sinkNode moves and SinkNodePrescence
                                   // is activated
                if (isTransmitEnable()) {
                    ACLMessage msg = UWMessage
                            .createFinalMessage(this.getNode().getName(),
                                    this.getSensorWorld().getSinkNode()
                                            .getName(),
                                    this.getMessageBuffer(),
                                    this.getSensorWorld().getTime());
                    this.transmit(msg);
                    this.getMessageBuffer().clear(); // clear the messageBuffer
                                                     // after transmission
                    setTransmitEnable(false);
                }
                break;
            default:
                setTransmitEnable(true);
                break;
            }
        }
    }

    @Override
    protected void handleIntruderPresence(Perception p) {

    }

    @Override
    protected void handleOverheardMessage(ACLMessage message) {

    }

    @Override
    protected void handleReceivedMessage(ACLMessage message) {
        // TODO Auto-generated method stub

    }

    /**
     * @return the messageBuffer
     */
    public HashMap<Double, ACLMessage> getMessageBuffer() {
        return messageBuffer;
    }

    /**
     * @param messageBuffer
     *            the messageBuffer to set
     */
    public void setMessageBuffer(HashMap<Double, ACLMessage> messageBuffer) {
        this.messageBuffer = messageBuffer;
    }

    /**
     * @return the transmitEnable
     */
    public boolean isTransmitEnable() {
        return transmitEnable;
    }

    /**
     * @param transmitEnable
     *            the transmitEnable to set
     */
    public void setTransmitEnable(boolean transmitEnable) {
        this.transmitEnable = transmitEnable;
    }

    /**
     * @return the agentVoI_DecayRate
     */
    public double getAgentVoI_DecayRate() {
        return agentVoI_DecayRate;
    }

    /**
     * @param agentVoI_DecayRate
     *            the agentVoI_DecayRate to set
     */
    public void setAgentVoI_DecayRate(double agentVoI_DecayRate) {
        this.agentVoI_DecayRate = agentVoI_DecayRate;
    }

    /**
     * @return the sampling
     */
    public boolean isSampling() {
        return sampling;
    }

    /**
     * @param sampling
     *            the sampling to set
     */
    public void setSampling(boolean sampling) {
        this.sampling = sampling;
    }

    /**
     * @return the sampleDecision
     */
    public int getSampleDecision() {
        return sampleDecision;
    }

    /**
     * @param sampleDecision
     *            the sampleDecision to set
     */
    public void setSampleDecision(int sampleDecision) {
        this.sampleDecision = sampleDecision;
    }

    public double getValueOfData() {
        return valueOfData;
    }

    public void setValueOfData(double valueOfData) {
        this.valueOfData = valueOfData;
    }

}
