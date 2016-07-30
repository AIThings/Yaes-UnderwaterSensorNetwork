package sensorutils;

public class DataPackage {

    public double time;
    public double value;
    public double decay;

    public DataPackage(double time, double value, double decay) {
        super();
        this.time = time;
        this.value = value;
        this.decay = decay;
    }

    @Override
    public String toString() {
        return "DataPackage [time=" + time + ", value=" + value + ", decay="
                + decay + "]";
    }

}
