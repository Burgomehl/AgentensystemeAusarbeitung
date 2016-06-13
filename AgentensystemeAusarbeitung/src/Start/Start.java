package Start;

import agent.GuiAgent;
import agent.MyAgent;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class Start {

	public static void main(String[] args) {
		try {
			MyAgent.log.info("Starte Clienten nun");
			Runtime runtime = Runtime.instance();
			Profile profile = new ProfileImpl("localhost", -1, null, false);
			AgentContainer container = runtime.createAgentContainer(profile);
			AgentController guiAgent = container.createNewAgent("UI", GuiAgent.class.getName(), args);
			guiAgent.start();
			AgentController agent1 = container.createNewAgent("adam", MyAgent.class.getName(), args);
			agent1.start();
			AgentController agent2 = container.createNewAgent("eva", MyAgent.class.getName(), args);
			agent2.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
