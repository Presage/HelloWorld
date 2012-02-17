package uk.ac.imperial.presage2.helloworld;

import uk.ac.imperial.presage2.core.environment.SharedStateStorage;
import uk.ac.imperial.presage2.util.environment.AbstractEnvironment;
import uk.ac.imperial.presage2.util.location.area.Area;
import uk.ac.imperial.presage2.util.location.area.HasArea;

import com.google.inject.Inject;

public class HelloEnvironment extends AbstractEnvironment implements HasArea {

	final private Area simArea;

	@Inject
	public HelloEnvironment(Area simArea, SharedStateStorage sharedState) {
		super(sharedState);
		this.simArea = simArea;
	}

	public Area getArea() {
		return this.simArea;
	}

}
