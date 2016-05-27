package agent;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import behaviour.IBehaviour;
import behaviour.MessageBehaviour;
import behaviour.SearchBehaviour;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class MyAgent extends Agent implements IAgent {
	List<IBehaviour> behaviours;

	@Override
	protected void setup() {
		System.out.println("hello world! my name is " + getLocalName());
		// for (IBehaviour iBehaviour : behaviours) {
		// if (iBehaviour instanceof Behaviour) {
		// addBehaviour((Behaviour)iBehaviour);
		// }
		// }
		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				Gson gson = new Gson();
				System.out.println(gson.toJson(new LoginMessage()));
			}
		});
		addBehaviour(new CyclicBehaviour(this) {
			Message m ;
			@Override
			public void action() {
				System.out.println("Message Behaviour");
				ACLMessage msg = receive();
						blockingReceive();
				if (msg != null) {
					msg.getContent();
					msg.getSender();
					Gson gson = new Gson();
					m = gson.fromJson(msg.getContent(), Message.class);
					
				} else {
					block();
				}
			}
		});
	}

	public MyAgent() {
		behaviours = new ArrayList<>();
		behaviours.add(new SearchBehaviour());
		behaviours.add(new MessageBehaviour(this));
	}

	public void runAgent() {

	}

}
