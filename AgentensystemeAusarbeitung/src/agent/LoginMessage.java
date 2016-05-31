package agent;

import de.aim.antworld.agent.AntWorldConsts;

public class LoginMessage {
	String type;
	String color = AntWorldConsts.ANT_COLOR_RED;
	String test;
	public LoginMessage(String type) {
		this.type = type;
	}
}
