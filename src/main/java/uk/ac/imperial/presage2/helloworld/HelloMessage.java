package uk.ac.imperial.presage2.helloworld;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.messaging.Performative;
import uk.ac.imperial.presage2.core.network.BroadcastMessage;
import uk.ac.imperial.presage2.core.network.NetworkAddress;

public class HelloMessage extends BroadcastMessage {

	public HelloMessage(NetworkAddress from,
			Time timestamp) {
		super(Performative.PROPOSE, from, timestamp);
	}

}
