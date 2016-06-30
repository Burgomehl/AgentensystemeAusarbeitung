package Start;

import agent.GuiAgent;
import agent.ThiefAgent;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class Start {

	public static void main(String[] args) {
		try {
			int ants = 1;
			String server = "localhost";
			System.out.println(args.length);
			if(args.length == 1){
				ants = Integer.parseInt(args[0]);
				System.out.println(ants);
			}else if(args.length == 2){
				ants = Integer.parseInt(args[0]);
				server = args[1];
				System.out.println(server);
			}
			ThiefAgent.log.info("Starte Clienten nun");
			Runtime runtime = Runtime.instance();
			Profile profile = new ProfileImpl(server, -1, null, false);
			profile.setParameter(Profile.SERVICES, "jade.core.messaging.TopicManagementService");
			AgentContainer container = runtime.createAgentContainer(profile);
			AgentController guiAgent = container.createNewAgent("UI", GuiAgent.class.getName(), args);
			guiAgent.start();
			for (int i = 0; i < ants; ++i) {
				AgentController agent2 = container.createNewAgent("GaBe-0" + i, ThiefAgent.class.getName(), args);
				agent2.start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
