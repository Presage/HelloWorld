package uk.ac.imperial.presage2.helloworld;

import java.util.HashSet;
import java.util.Set;

import uk.ac.imperial.presage2.core.network.NetworkConstraint;
import uk.ac.imperial.presage2.core.participant.Participant;
import uk.ac.imperial.presage2.core.plugin.PluginModule;
import uk.ac.imperial.presage2.core.simulator.InjectedSimulation;
import uk.ac.imperial.presage2.core.simulator.Parameter;
import uk.ac.imperial.presage2.core.simulator.Scenario;
import uk.ac.imperial.presage2.core.util.random.Random;
import uk.ac.imperial.presage2.util.environment.AbstractEnvironmentModule;
import uk.ac.imperial.presage2.util.location.Location;
import uk.ac.imperial.presage2.util.location.LocationService;
import uk.ac.imperial.presage2.util.location.LocationStoragePlugin;
import uk.ac.imperial.presage2.util.location.MoveHandler;
import uk.ac.imperial.presage2.util.location.ParticipantLocationService;
import uk.ac.imperial.presage2.util.location.area.Area;
import uk.ac.imperial.presage2.util.location.area.AreaService;
import uk.ac.imperial.presage2.util.network.NetworkModule;
import uk.ac.imperial.presage2.util.network.NetworkRangeConstraint;

import com.google.inject.AbstractModule;

public class HelloWorldSimulation extends InjectedSimulation {

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

		modules.add(Area.Bind.area2D(xSize, ySize));

		// install abstract environment module
		modules.add(new AbstractEnvironmentModule()
				.addActionHandler(MoveHandler.class)
				.addGlobalEnvironmentService(LocationService.class)
				.addGlobalEnvironmentService(AreaService.class)
				.addParticipantEnvironmentService(
						ParticipantLocationService.class)
				.addParticipantGlobalEnvironmentService(AreaService.class));

		// network
		Set<Class<? extends NetworkConstraint>> constraints = new HashSet<Class<? extends NetworkConstraint>>();
		constraints.add(NetworkRangeConstraint.class);
		modules.add(NetworkModule.constrainedNetworkModule(constraints)
				.withNodeDiscovery());

		modules.add(new PluginModule().addPlugin(LocationStoragePlugin.class));

		return modules;
	}

	@Override
	protected void addToScenario(Scenario s) {
		for (int i = 0; i < agentCount; i++) {
			Participant p = new HelloAgent(Random.randomUUID(), "helloagent"
					+ i, new Location(Random.randomInt(xSize),
					Random.randomInt(ySize)), 5, 5);
			getInjector().injectMembers(p);
			s.addParticipant(p);
		}
	}

}
