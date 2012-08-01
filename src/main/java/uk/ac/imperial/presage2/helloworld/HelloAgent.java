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
import uk.ac.imperial.presage2.util.location.HasLocation;
import uk.ac.imperial.presage2.util.location.Location;
import uk.ac.imperial.presage2.util.location.Move;
import uk.ac.imperial.presage2.util.location.ParticipantLocationService;
import uk.ac.imperial.presage2.util.location.area.AreaService;
import uk.ac.imperial.presage2.util.participant.AbstractParticipant;
import uk.ac.imperial.presage2.util.participant.HasCommunicationRange;
import uk.ac.imperial.presage2.util.participant.HasPerceptionRange;
import uk.ac.imperial.presage2.util.protocols.Protocol;

/**
 * @author Sam Macbeth
 * 
 */
public class HelloAgent extends AbstractParticipant implements HasLocation,
		HasPerceptionRange, HasCommunicationRange {

	class AgentState {

		Location loc;

		double perceptionRange;

		double communicationRange;

	}

	private AgentState state = new AgentState();

	private ParticipantLocationService locationService;

	private AreaService areaService;

	Protocol helloWorld;

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
			this.areaService = this.getEnvironmentService(AreaService.class);
		} catch (UnavailableServiceException e) {
			logger.warn(e);
			this.locationService = null;
		}
		// add hello world protocol
		helloWorld = new HelloWorldProtocol(this.getName(), network);
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

	@Override
	protected void processInput(Input in) {
		if (helloWorld.canHandle(in)) {
			helloWorld.handle(in);
		}
	}

	@Override
	protected Set<ParticipantSharedState> getSharedState() {
		Set<ParticipantSharedState> ss = super.getSharedState();
		// shared state for ParticipantLocationService
		ss.add(ParticipantLocationService.createSharedState(this.getID(),
				this.getLocation()));
		// shared state for network communication range
		ss.add(CommunicationRangeService.createSharedState(getID(),
				this.getCommunicationRange()));

		return ss;
	}

	Location target;

	@SuppressWarnings("deprecation")
	@Override
	public void execute() {
		// timeout protocols
		helloWorld.incrementTime();
		// update my location
		setLocation(locationService.getAgentLocation(getID()));
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
			Set<NetworkAddress> alreadyTalkingTo = helloWorld
					.getActiveConversationMembers();
			int convCount = alreadyTalkingTo.size();
			for (NetworkAddress a : this.network.getConnectedNodes()) {
				logger.info("I'm connected to: " + a);
				// spawn a conversation if I'm not already taking them them
				// (limit 5 convs)
				if (!alreadyTalkingTo.contains(a) && convCount < 5) {
					helloWorld.spawn(a);
					convCount++;
				}
			}
		} catch (UnsupportedOperationException e) {
			logger.warn("Can't get connected nodes", e);
		}

		// reset target location if I am within 5 of it.
		if (target != null && target.distanceTo(getLocation()) < 5) {
			target = null;
		}
		// create a target location if I don't have one.
		if (target == null) {
			target = new Location(
					Random.randomInt(this.areaService.getSizeX()),
					Random.randomInt(this.areaService.getSizeY()));
		}
		// move towards this target location
		Move move = this.getLocation().getMoveTo(target, 5);

		logger.info("Attempting move: " + move);

		// submit move action to the environment.
		try {
			environment.act(move, getID(), authkey);
		} catch (ActionHandlingException e) {
			logger.warn(e);
		}
	}

	@Override
	public void setLocation(Location l) {
		this.state.loc = l;
	}
}
