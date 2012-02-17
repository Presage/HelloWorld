package uk.ac.imperial.presage2.helloworld;

import java.util.HashSet;
import java.util.Set;

import uk.ac.imperial.presage2.core.network.NetworkConstraint;
import uk.ac.imperial.presage2.core.plugin.Plugin;
import uk.ac.imperial.presage2.util.environment.AbstractEnvironmentModule;
import uk.ac.imperial.presage2.util.location.LocationService;
import uk.ac.imperial.presage2.util.location.LocationStoragePlugin;
import uk.ac.imperial.presage2.util.location.MoveHandler;
import uk.ac.imperial.presage2.util.location.ParticipantLocationService;
import uk.ac.imperial.presage2.util.location.area.AreaService;
import uk.ac.imperial.presage2.util.location.area.HasArea;
import uk.ac.imperial.presage2.util.network.NetworkModule;
import uk.ac.imperial.presage2.util.network.NetworkRangeConstraint;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class HelloModule extends AbstractModule {

	@Override
	protected void configure() {

		// install abstract environment module
		install(new AbstractEnvironmentModule(HelloEnvironment.class)
				.addActionHandler(MoveHandler.class)
				.addGlobalEnvironmentService(LocationService.class)
				.addGlobalEnvironmentService(AreaService.class)
				.addParticipantEnvironmentService(
						ParticipantLocationService.class)
				.addParticipantGlobalEnvironmentService(AreaService.class));

		// bind area for handlers/services
		bind(HasArea.class).to(HelloEnvironment.class);

		// install constrained network
		Set<Class<? extends NetworkConstraint>> constaints = new HashSet<Class<? extends NetworkConstraint>>();
		constaints.add(NetworkRangeConstraint.class);
		install(NetworkModule.constrainedNetworkModule(constaints)
				.withNodeDiscovery());

		// plugins
		Multibinder<Plugin> pluginBinder = Multibinder.newSetBinder(binder(),
				Plugin.class);
		pluginBinder.addBinding().to(LocationStoragePlugin.class);

	}

}
