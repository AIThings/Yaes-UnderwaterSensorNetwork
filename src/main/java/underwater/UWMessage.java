package underwater;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.model.SensorNetworkMessageConstants;

public class UWMessage extends ACLMessage
    implements UWConstants, SensorNetworkMessageConstants, Serializable {
    private static final long serialVersionUID = -4671210749728774239L;

    public UWMessage(String destination, String sender,
        ACLMessage otherMessage) {
        super(destination, sender, otherMessage);
        // TODO Auto-generated constructor stub
    }

    /**
     * Creates a message for a gradient based on an observation. If the
     * perception is NoPerception, then it will send a message with the
     * perception put to 0.
     * 
     * @return
     */
    public static ACLMessage createMessage(String sender, String destination,
        double timestamp, double valueOfData, double decayTimeVoI) {
        final ACLMessage message =
                new ACLMessage(sender, ACLMessage.Performative.INFORM);
        message.setDestination(destination);
        message.setValue(FIELD_CONTENT, MESSAGE_DATA);
        message.setValue(FIELD_INTENSITY, 0); // sets default VoI of message
        message.setValue(FIELD_TIMESTAMP, timestamp);
        message.setValue(FIELD_VOI, valueOfData);
        message.setValue(FIELD_VOI_DECAY_TIME, decayTimeVoI);
        return message;
    }

    public static ACLMessage createFinalMessage(String sender,
        String destination, HashMap<Double, ACLMessage> messageBuffer,
        double finalTime) {
        final ACLMessage message =
                new ACLMessage(sender, ACLMessage.Performative.INFORM);
        message.setDestination(destination);
        message.setValue(FIELD_CONTENT, MESSAGE_DATA);
        message.setValue(FIELD_INTENSITY, 0); // intensity for coloring of a
                                              // message
        message.setValue(FIELD_TIMESTAMP, finalTime);

        Iterator<Double> itr = messageBuffer.keySet().iterator();
        double finalVoI = 0.0, originalVoI = 0.0;
        while (itr.hasNext()) {
            double initialTime = itr.next();
            ACLMessage msg = messageBuffer.get(initialTime);
            double time = finalTime - initialTime;
            // TODO: Fix the exponential decay
            // double val = (double) msg.getValue(FIELD_VOI) *
            // Math.pow(((double) msg.getValue(FIELD_VOI_DECAY_TIME)),
            // initialTime) ;
            // finalVoI += (double) msg.getValue(FIELD_VOI) *
            // Math.exp(-1 * (double) msg.getValue(FIELD_VOI_DECAY_TIME) * time)
            // ;
            finalVoI += (double) msg.getValue(FIELD_VOI) * Math
                    .pow((double) msg.getValue(FIELD_VOI_DECAY_TIME), (time));
            originalVoI += (double) msg.getValue(FIELD_VOI);
        }
        // TODO: Set VoI in a message
        message.setValue(FIELD_VOI, finalVoI);
        message.setValue(FIELD_VOI_MAX, originalVoI);
        // message.setValue(FIELD_VOI_AVG,
        // (double)(finalVoI/messageBuffer.size()));

        // message.setContent(message);
        return message;
    }
}
