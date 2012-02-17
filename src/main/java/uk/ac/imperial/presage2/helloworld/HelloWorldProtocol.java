package uk.ac.imperial.presage2.helloworld;

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.messaging.Performative;
import uk.ac.imperial.presage2.core.network.Message;
import uk.ac.imperial.presage2.core.network.NetworkAdaptor;
import uk.ac.imperial.presage2.core.network.UnicastMessage;
import uk.ac.imperial.presage2.core.simulator.SimTime;
import uk.ac.imperial.presage2.util.fsm.Action;
import uk.ac.imperial.presage2.util.fsm.AndCondition;
import uk.ac.imperial.presage2.util.fsm.EventTypeCondition;
import uk.ac.imperial.presage2.util.fsm.FSM;
import uk.ac.imperial.presage2.util.fsm.FSMException;
import uk.ac.imperial.presage2.util.fsm.StateType;
import uk.ac.imperial.presage2.util.fsm.Transition;
import uk.ac.imperial.presage2.util.protocols.ConversationCondition;
import uk.ac.imperial.presage2.util.protocols.ConversationSpawnEvent;
import uk.ac.imperial.presage2.util.protocols.FSMConversation;
import uk.ac.imperial.presage2.util.protocols.FSMProtocol;
import uk.ac.imperial.presage2.util.protocols.InitialiseConversationAction;
import uk.ac.imperial.presage2.util.protocols.MessageAction;
import uk.ac.imperial.presage2.util.protocols.MessageTypeCondition;
import uk.ac.imperial.presage2.util.protocols.SpawnAction;
import uk.ac.imperial.presage2.util.protocols.TimeoutCondition;

public class HelloWorldProtocol extends FSMProtocol {

	private final String name;

	private final Logger logger;

	enum States {
		START, HELLO_SENT, WORLD_RECEIVED, HELLO_RECEIVED, TIMED_OUT
	};

	enum Transitions {
		SEND_HELLO, RECEIVE_WORLD, RECEIVE_HELLO, TIMEOUT
	};

	public HelloWorldProtocol(String agentName, NetworkAdaptor network) {
		super("HELLO WORLD", FSM.description(), network);
		this.name = agentName;
		logger = Logger.getLogger(HelloWorldProtocol.class.getName() + ", "
				+ name);
		try {
			this.description
					.addState(States.START, StateType.START)

					// initiator fsm
					.addState(States.HELLO_SENT)
					.addState(States.WORLD_RECEIVED, StateType.END)
					.addState(States.TIMED_OUT, StateType.END)
					.addTransition(
							Transitions.SEND_HELLO,
							new EventTypeCondition(ConversationSpawnEvent.class),
							States.START, States.HELLO_SENT, new SpawnAction() {
								@Override
								public void processSpawn(
										ConversationSpawnEvent event,
										FSMConversation conv, Transition trans) {
									logger.info("sending hello");
									conv.getNetwork().sendMessage(
											new UnicastMessage<Object>(
													Performative.PROPOSE,
													"HELLO", SimTime.get(),
													conv.getNetwork()
															.getAddress(),
													conv.recipients.get(0)));
								}
							})
					.addTransition(
							Transitions.RECEIVE_WORLD,
							new AndCondition(new MessageTypeCondition("WORLD"),
									new ConversationCondition()),
							States.HELLO_SENT, States.WORLD_RECEIVED,
							new MessageAction() {
								@Override
								public void processMessage(Message<?> message,
										FSMConversation conv,
										Transition transition) {
									logger.info("Received world!");
								}
							})

					// replier fsm
					.addState(States.HELLO_RECEIVED, StateType.END)
					.addTransition(Transitions.RECEIVE_HELLO,
							new MessageTypeCondition("HELLO"), States.START,
							States.HELLO_RECEIVED,
							new InitialiseConversationAction() {
								@Override
								public void processInitialMessage(
										Message<?> message,
										FSMConversation conv, Transition trans) {
									logger.info("responding to hello");
									conv.getNetwork().sendMessage(
											new UnicastMessage<Object>(
													Performative.AGREE,
													"WORLD", SimTime.get(),
													conv.getNetwork()
															.getAddress(),
													message.getFrom()));
								}
							})

					.addTransition(Transitions.TIMEOUT,
							new TimeoutCondition(3), States.HELLO_SENT,
							States.TIMED_OUT, new Action() {
								@Override
								public void execute(Object event,
										Object entity, Transition transition) {
									logger.info("timed out");
								}
							});
		} catch (FSMException e) {
			throw new RuntimeException(e);
		}
	}

}
