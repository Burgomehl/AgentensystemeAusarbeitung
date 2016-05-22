package behaviour;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class MessageBehaviour extends CyclicBehaviour implements IBehaviour{
	Agent a ; 
	
	public MessageBehaviour(Agent a) {
		this.a = a;
	}
	@Override
	public void action() {
		System.out.println("Message Behaviour");
		a.receive();
		block();
	}

}
