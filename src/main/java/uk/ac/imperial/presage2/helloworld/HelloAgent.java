/**
 * 
 */
package uk.ac.imperial.presage2.helloworld;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.environment.ParticipantSharedState;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.core.messaging.Input;
import uk.ac.imperial.presage2.core.network.NetworkAddress;
import uk.ac.imperial.presage2.core.util.random.Random;
import uk.ac.imperial.presage2.util.environment.CommunicationRangeService;
import uk.ac.imperial.presage2.util.location.Discrete2DLocation;
import uk.ac.imperial.presage2.util.location.HasLocation;
import uk.ac.imperial.presage2.util.location.Location;
import uk.ac.imperial.presage2.util.location.Move;
import uk.ac.imperial.presage2.util.location.ParticipantLocationService;
import uk.ac.imperial.presage2.util.participant.AbstractParticipant;
import uk.ac.imperial.presage2.util.participant.HasCommunicationRange;
import uk.ac.imperial.presage2.util.participant.HasPerceptionRange;

/**
 * @author Sam Macbeth
 * 
 */
public class HelloAgent extends AbstractParticipant implements HasLocation,
		HasPerceptionRange, HasCommunicationRange {

	class State {

		Location loc;

		double perceptionRange;

		double communicationRange;

	}

	private State state = new State();

	private ParticipantLocationService locationService;

	public HelloAgent(UUID id, String name, Location loc,
			double perceptionRange, double communicationRange) {
		super(id, name);
		state.loc = loc;
		state.perceptionRange = perceptionRange;
		state.communicationRange = communicationRange;
	}

	@Override
	public void initialise() {
		super.initialise();
		try {
			this.locationService = this
					.getEnvironmentService(ParticipantLocationService.class);
		} catch (UnavailableServiceException e) {
			logger.warn(e);
			this.locationService = null;
		}
	}

	public double getCommunicationRange() {
		return state.communicationRange;
	}

	public double getPerceptionRange() {
		return state.perceptionRange;
	}

	public Location getLocation() {
		return state.loc;
	}

	public void setLocation(Location l) {
		this.state.loc = l;
	}

	@Override
	protected void processInput(Input in) {
		if (in instanceof HelloMessage) {
			HelloMessage m = (HelloMessage) in;
			logger.info(m.getFrom().getId()
					+ " sent me a HelloMessage, how nice!");
		}
	}

	@Override
	protected Set<ParticipantSharedState<?>> getSharedState() {
		Set<ParticipantSharedState<?>> ss = super.getSharedState();
		// shared state for ParticipantLocationService
		ss.add(ParticipantLocationService.createSharedState(this.getID(), this));
		// shared state for network communication range
		ss.add(CommunicationRangeService.createSharedState(getID(), this));

		return ss;
	}

	@Override
	public void execute() {
		super.execute();

		logger.info("My location is: " + this.getLocation());

		// observe nearby agents
		for (Map.Entry<UUID, Location> agent : this.locationService
				.getNearbyAgents().entrySet()) {
			logger.info("I see agent: " + agent.getKey() + " at location: "
					+ agent.getValue());
		}

		// find who I'm connected to
		try {
			for (NetworkAddress a : this.network.getConnectedNodes()) {
				logger.info("I'm connected to: " + a);
				// say hello
				this.network.sendMessage(new HelloMessage(this.network
						.getAddress(), a, getTime()));
			}
		} catch (UnsupportedOperationException e) {
			logger.warn("Can't get connected nodes", e);
		}

		// random movement
		Move move = this.state.loc.getMoveTo(
				new Discrete2DLocation(Random.randomInt(50), Random
						.randomInt(50)), 5);

		logger.info("Attempting move: " + move);

		try {
			environment.act(move, getID(), authkey);
		} catch (ActionHandlingException e) {
			logger.warn(e);
		}
	}

}
