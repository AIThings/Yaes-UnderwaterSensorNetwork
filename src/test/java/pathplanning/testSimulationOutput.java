package pathplanning;

import org.junit.Test;

import yaes.framework.simulation.SimulationOutput;
import yaes.ui.text.TextUi;

public class testSimulationOutput {
	String S1 = "Variable with Series";
	String S2 = "Variable without Series";
	@Test
	public void test() {
		
		SimulationOutput so = new SimulationOutput();
		so.createVariable(S1, true);
		so.getRandomVar(S1).enableTimeSeriesCollecting();

		so.createVariable(S2, false);
		int i = 0;
		do{
			so.update(S1, i*i);
			so.update(S2, i*i);
			TextUi.println("S1 = "+ so.getRandomVar(S1) + "\t S2 = " + so.getRandomVar(S2));
			i++;
		}
		while(i<10);
		

	}

}
