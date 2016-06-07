package agent;

import de.aim.antworld.agent.AntWorldConsts;

public class InformMessage {
	String type;
	String color = AntWorldConsts.ANT_COLOR_RED;
	String test;
	public InformMessage(String type) {
		this.type = type;
	}
}
