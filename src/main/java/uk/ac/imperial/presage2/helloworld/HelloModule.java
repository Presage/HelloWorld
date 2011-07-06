package uk.ac.imperial.presage2.helloworld;

import uk.ac.imperial.presage2.core.environment.ActionHandler;
import uk.ac.imperial.presage2.core.environment.EnvironmentConnector;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.network.BasicNetworkConnector;
import uk.ac.imperial.presage2.core.network.ConstrainedNetworkController;
import uk.ac.imperial.presage2.core.network.NetworkAddressFactory;
import uk.ac.imperial.presage2.core.network.NetworkChannel;
import uk.ac.imperial.presage2.core.network.NetworkConnector;
import uk.ac.imperial.presage2.core.network.NetworkConnectorFactory;
import uk.ac.imperial.presage2.core.network.NetworkConstraint;
import uk.ac.imperial.presage2.util.environment.AbstractEnvironment;
import uk.ac.imperial.presage2.util.environment.CommunicationRangeService;
import uk.ac.imperial.presage2.util.location.AreaService;
import uk.ac.imperial.presage2.util.location.HasArea;
import uk.ac.imperial.presage2.util.location.LocationService;
import uk.ac.imperial.presage2.util.location.MoveHandler;
import uk.ac.imperial.presage2.util.network.NetworkRangeConstraint;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;

public class HelloModule extends AbstractModule {

	@Override
	protected void configure() {
		// environment
		bind(HelloEnvironment.class).in(Singleton.class);
		bind(AbstractEnvironment.class).to(HelloEnvironment.class);
		bind(EnvironmentConnector.class).to(AbstractEnvironment.class);
		bind(EnvironmentServiceProvider.class).to(AbstractEnvironment.class);
		bind(EnvironmentSharedStateAccess.class).to(AbstractEnvironment.class);
		bind(HasArea.class).to(HelloEnvironment.class);

		// global environment services
		Multibinder<EnvironmentService> serviceBinder = Multibinder
				.newSetBinder(binder(), EnvironmentService.class);
		serviceBinder.addBinding().to(LocationService.class);
		serviceBinder.addBinding().to(CommunicationRangeService.class);
		serviceBinder.addBinding().to(AreaService.class);

		// action handlers
		Multibinder<ActionHandler> actionBinder = Multibinder.newSetBinder(
				binder(), ActionHandler.class);
		actionBinder.addBinding().to(MoveHandler.class);

		// network
		install(new FactoryModuleBuilder().implement(NetworkConnector.class,
				BasicNetworkConnector.class).build(
				NetworkConnectorFactory.class));
		bind(NetworkChannel.class).to(ConstrainedNetworkController.class).in(
				Singleton.class);
		install(new FactoryModuleBuilder().build(NetworkAddressFactory.class));

		// network constraints
		Multibinder<NetworkConstraint> constraintBinder = Multibinder
				.newSetBinder(binder(), NetworkConstraint.class);
		constraintBinder.addBinding().to(NetworkRangeConstraint.class);

	}

}
