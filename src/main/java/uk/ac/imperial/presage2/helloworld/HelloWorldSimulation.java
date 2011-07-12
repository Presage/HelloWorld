package uk.ac.imperial.presage2.helloworld;

import java.util.HashSet;
import java.util.Set;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.participant.Participant;
import uk.ac.imperial.presage2.core.simulator.InjectedSimulation;
import uk.ac.imperial.presage2.core.simulator.Parameter;
import uk.ac.imperial.presage2.core.simulator.Scenario;
import uk.ac.imperial.presage2.core.util.random.Random;
import uk.ac.imperial.presage2.util.location.Area;
import uk.ac.imperial.presage2.util.location.Discrete2DLocation;

import com.google.inject.AbstractModule;

public class HelloWorldSimulation extends InjectedSimulation {

	@Parameter(name = "finishTime")
	public int finishTime;

	@Parameter(name = "xSize")
	public int xSize;

	@Parameter(name = "ySize")
	public int ySize;

	@Parameter(name = "agentCount")
	public int agentCount;

	public HelloWorldSimulation(Set<AbstractModule> modules) {
		super(modules);
	}

	@Override
	protected Set<AbstractModule> getModules() {
		Set<AbstractModule> modules = new HashSet<AbstractModule>();
		modules.add(new HelloModule());
		modules.add(Time.Bind.integerTime(finishTime));
		modules.add(Area.Bind.area2D(xSize, ySize));
		return modules;
	}

	@Override
	protected void addToScenario(Scenario s) {
		for (int i = 0; i < agentCount; i++) {
			Participant p = new HelloAgent(Random.randomUUID(), "helloagent"
					+ i, new Discrete2DLocation(Random.randomInt(xSize),
					Random.randomInt(ySize)), 10, 10);
			getInjector().injectMembers(p);
			s.addParticipant(p);
		}
	}

}
