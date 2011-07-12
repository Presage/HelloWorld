package uk.ac.imperial.presage2.helloworld;

import java.util.HashSet;
import java.util.Set;

import uk.ac.imperial.presage2.core.environment.ActionHandler;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.network.NetworkConstraint;
import uk.ac.imperial.presage2.util.environment.AbstractEnvironmentModule;
import uk.ac.imperial.presage2.util.location.AreaService;
import uk.ac.imperial.presage2.util.location.HasArea;
import uk.ac.imperial.presage2.util.location.LocationService;
import uk.ac.imperial.presage2.util.location.MoveHandler;
import uk.ac.imperial.presage2.util.network.NetworkModule;
import uk.ac.imperial.presage2.util.network.NetworkRangeConstraint;

import com.google.inject.AbstractModule;

public class HelloModule extends AbstractModule {

	@Override
	protected void configure() {
		// environment services
		Set<Class<? extends EnvironmentService>> environmentServices = new HashSet<Class<? extends EnvironmentService>>();
		environmentServices.add(LocationService.class);
		environmentServices.add(AreaService.class);

		// action handlers
		Set<Class<? extends ActionHandler>> actionHandlers = new HashSet<Class<? extends ActionHandler>>();
		actionHandlers.add(MoveHandler.class);

		// install abstract environment module
		install(new AbstractEnvironmentModule(HelloEnvironment.class,
				environmentServices, actionHandlers));
		bind(HasArea.class).to(HelloEnvironment.class); // bind area for
														// handlers/services

		// install constrained network
		Set<Class<? extends NetworkConstraint>> constaints = new HashSet<Class<? extends NetworkConstraint>>();
		constaints.add(NetworkRangeConstraint.class);
		install(NetworkModule.constrainedNetworkModule(constaints)
				.withNodeDiscovery());

	}

}
