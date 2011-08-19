package uk.ac.imperial.presage2.helloworld;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.messaging.Performative;
import uk.ac.imperial.presage2.core.network.NetworkAddress;
import uk.ac.imperial.presage2.core.network.UnicastMessage;

public class HelloMessage extends UnicastMessage<Object> {

	public HelloMessage(NetworkAddress from, NetworkAddress to, Time timestamp) {
		super(Performative.PROPOSE, from, to, timestamp);
	}

}
