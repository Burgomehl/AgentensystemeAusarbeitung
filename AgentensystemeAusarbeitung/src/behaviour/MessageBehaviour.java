package behaviour;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class MessageBehaviour extends CyclicBehaviour implements IBehaviour{

	@Override
	public void action() {
		System.out.println("Message Behaviour");
		block();
	}

}
