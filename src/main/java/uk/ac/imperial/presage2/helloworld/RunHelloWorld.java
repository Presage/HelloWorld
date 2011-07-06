package uk.ac.imperial.presage2.helloworld;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.participant.Participant;
import uk.ac.imperial.presage2.core.simulator.Scenario;
import uk.ac.imperial.presage2.core.simulator.Simulator;
import uk.ac.imperial.presage2.core.simulator.SingleThreadedSimulator;
import uk.ac.imperial.presage2.core.util.random.Random;
import uk.ac.imperial.presage2.util.location.Area;
import uk.ac.imperial.presage2.util.location.Discrete2DLocation;

import com.google.inject.AbstractModule;

public class RunHelloWorld {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// read args
		int finishTime =0;
		int xSize =0;
		int ySize =0;
		int agentCount =0;
		try {
			finishTime = Integer.parseInt(args[0]);
			xSize = Integer.parseInt(args[1]);
			ySize = Integer.parseInt(args[2]);
			agentCount = Integer.parseInt(args[3]);
		} catch (NumberFormatException e) {
			System.err.println("Missing arguments.");
			System.exit(2);
		}
		// build scenario
		final Scenario s = Scenario.Builder.createFromModules(
				new HelloModule(),
				Time.Bind.integerTime(finishTime),
				Area.Bind.area2D(xSize, ySize),
				new AbstractModule() {
					@Override
					protected void configure() {
						bind(Simulator.class).to(SingleThreadedSimulator.class);
					}
				});
		
		for(int i=0; i<agentCount; i++) {
			Participant p = new HelloAgent(Random.randomUUID(), "helloagent"+i, new Discrete2DLocation(Random.randomInt(xSize), Random.randomInt(ySize)), 10, 10);
			s.addParticipant(Scenario.Builder.injectMembers(p));
		}

		Simulator sim = Scenario.Builder.injector.getInstance(Simulator.class);
		
		sim.start();
		
	}

}
