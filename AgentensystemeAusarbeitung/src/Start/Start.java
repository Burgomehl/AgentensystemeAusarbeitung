package Start;



import agent.MyAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Start {

	public static void main(String[] args) {
		try {
			Logger l = LoggerFactory.getLogger(Start.class);
			
			l.info("Test1 und der Logger geht");
			Runtime runtime = Runtime.instance();
			Profile profile = new ProfileImpl("localhost",-1,null,false);
			AgentContainer container = runtime.createAgentContainer(profile);
			AgentController agent1 = container.createNewAgent("adam", MyAgent.class.getName(), args);
			agent1.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
