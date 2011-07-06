package uk.ac.imperial.presage2.helloworld;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.environment.EnvironmentRegistrationRequest;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.SharedStateAccessException;
import uk.ac.imperial.presage2.util.environment.AbstractEnvironment;
import uk.ac.imperial.presage2.util.location.Area;
import uk.ac.imperial.presage2.util.location.HasArea;
import uk.ac.imperial.presage2.util.location.ParticipantLocationService;

import com.google.inject.Inject;

public class HelloEnvironment extends AbstractEnvironment implements HasArea {

	final private Logger logger = Logger.getLogger(HelloEnvironment.class);
	
	final private Area simArea;

	@Inject
	public HelloEnvironment(Area simArea) {
		super();
		this.simArea = simArea;
	}

	@Override
	protected Set<EnvironmentService> generateServices(
			EnvironmentRegistrationRequest request) {
		final Set<EnvironmentService> services = new HashSet<EnvironmentService>();
		try {
			services.add(new ParticipantLocationService(request.getParticipant(), this, this));
		} catch(SharedStateAccessException e) {
			logger.warn("Unable to add ParticipantLocationService to services for participant "+ request.getParticipantID() +", error accessing shared state.", e);
		}
		return services;
	}

	public Area getArea() {
		return this.simArea;
	}

}
