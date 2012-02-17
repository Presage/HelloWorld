package uk.ac.imperial.presage2.helloworld;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.environment.EnvironmentRegistrationRequest;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.SharedStateAccessException;
import uk.ac.imperial.presage2.core.environment.SharedStateStorage;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.util.environment.AbstractEnvironment;
import uk.ac.imperial.presage2.util.location.ParticipantLocationService;
import uk.ac.imperial.presage2.util.location.area.Area;
import uk.ac.imperial.presage2.util.location.area.AreaService;
import uk.ac.imperial.presage2.util.location.area.HasArea;

import com.google.inject.Inject;

public class HelloEnvironment extends AbstractEnvironment implements HasArea {

	final private Logger logger = Logger.getLogger(HelloEnvironment.class);

	final private Area simArea;

	@Inject
	public HelloEnvironment(Area simArea, SharedStateStorage sharedState) {
		super(sharedState);
		this.simArea = simArea;
	}

	@Override
	protected Set<EnvironmentService> generateServices(
			EnvironmentRegistrationRequest request) {
		final Set<EnvironmentService> services = new HashSet<EnvironmentService>();
		try {
			services.add(new ParticipantLocationService(request
					.getParticipant(), this.sharedState, this));
			services.add(this.getEnvironmentService(AreaService.class));
		} catch (SharedStateAccessException e) {
			logger.warn(
					"Unable to add ParticipantLocationService to services for participant "
							+ request.getParticipantID()
							+ ", error accessing shared state.", e);
		} catch (UnavailableServiceException e) {
			logger.warn("Unable to add AreaService to services for participant"
					+ request.getParticipantID(), e);
		}
		return services;
	}

	public Area getArea() {
		return this.simArea;
	}

}
