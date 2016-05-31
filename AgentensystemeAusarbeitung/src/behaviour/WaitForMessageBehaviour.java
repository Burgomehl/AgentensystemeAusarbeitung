package behaviour;

import com.google.gson.Gson;

import agent.IAgent;
import agent.Message;
import agent.MyAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class WaitForMessageBehaviour extends CyclicBehaviour implements IBehaviour {
	private Message m;
	private MyAgent agent;
	
	public WaitForMessageBehaviour(MyAgent agent) {
		this.agent = agent;
	}
	@Override
	public void action() {
		System.out.println("Message Behaviour");
		ACLMessage msg = agent.blockingReceive();
		if (msg != null) {
			msg.getContent();
			msg.getSender();
			Gson gson = new Gson();
			m = gson.fromJson(msg.getContent(), Message.class);
			System.out.println("ausgabe"+msg.getContent());
		} else {
			// block();
		}
	}
}
