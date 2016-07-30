package agents;

import java.io.Serializable;

import yaes.ui.format.Formatter;
import yaes.ui.format.ToStringDetailed;
import yaes.world.physical.location.Location;

/**
 * Implements the states required for Q-Learning Each state is a combination of
 * stateName and location The stateName by default is a nodeName and location is
 * the nodeLocation
 * 
 * @author SaadKhan
 *
 */

public class ProgressState implements ToStringDetailed, Serializable {

    private static final long serialVersionUID = -7364209370570711814L;
    private Location location;
    private String stateName;

    public ProgressState(String stateName, Location location) {
        this.location = location;
        this.stateName = stateName;
    }

    public ProgressState(String stateName) {
        this.setStateName(stateName);
    }

    public String toStringDetailed(int detailLevel) {
        Formatter fmt = new Formatter();
        // fmt.indent();
        fmt.add(this.getStateName() + "- Location : " + this.getLocation()
                + "\n");
        return fmt.toString();
    }

    @Override
    public String toString() {
        return toStringDetailed(MAX_DETAIL);
    }

    /**
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location
     *            sets the location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * @return the stateID
     */
    public String getStateName() {
        return stateName;
    }

    /**
     * @param stateID
     *            the stateID to set
     */
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((stateName == null) ? 0 : stateName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProgressState other = (ProgressState) obj;
        if (stateName == null) {
            if (other.stateName != null)
                return false;
        } else if (!stateName.equals(other.stateName))
            return false;
        return true;
    }
}
