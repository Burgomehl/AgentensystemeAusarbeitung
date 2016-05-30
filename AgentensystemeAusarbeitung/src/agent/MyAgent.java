package agent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;

import behaviour.IBehaviour;
import behaviour.MessageBehaviour;
import behaviour.SearchBehaviour;
import data.Map;
import data.MapAsArray;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class MyAgent extends Agent implements IAgent {
	List<IBehaviour> behaviours;
	private String worldName = "";
	private MapAsArray handler;

	@Override
	protected void setup() {

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(this.getAID());
		// parse and create service description
		String[] services = { "antWorld2016" };
		for (int i = 0; i < services.length; ++i) {
			ServiceDescription sd = new ServiceDescription();
			sd.setType(services[i].trim());
			sd.setName(services[i].trim());
			dfd.addServices(sd);
		}
		try {
			DFService.register(this, dfd);
			System.out.println("reg");
		} catch (FIPAException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("hello world! my name is " + getLocalName());
		// for (IBehaviour iBehaviour : behaviours) {
		// if (iBehaviour instanceof Behaviour) {
		// addBehaviour((Behaviour)iBehaviour);
		// }
		// }
		addBehaviour(new OneShotBehaviour() {

			@Override
			public void action() {
				System.out.println("Startet");
			}
		});

		addBehaviour(new OneShotBehaviour(this) {

			@Override
			public void action() {
				ServiceDescription filter = new ServiceDescription();
				filter.setType("antWorld2016");
				DFAgentDescription dfd = new DFAgentDescription();
				dfd.addServices(filter);

				DFAgentDescription[] search;
				try {
					search = DFService.search(myAgent, dfd);
					System.out.println("buh");
					for (int i = 0; i < search.length; i++) {
						String localName = search[i].getName().getLocalName();
						System.out.println(localName + ":");
						if (localName.contains("antWorld")) {
							worldName = localName;
						}
						Iterator it = search[i].getAllServices();
						while (it.hasNext()) {
							ServiceDescription sd = (ServiceDescription) it.next();
							System.out.println(" - " + sd.getName());
						}
						System.out.println();
					}
				} catch (FIPAException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {

				Gson gson = new Gson();
				sendMessage(gson.toJson(new LoginMessage("ANT_ACTION_LOGIN")));
			}
		});
		
		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				Gson gson = new Gson();
				sendMessage(gson.toJson(new LoginMessage("ANT_ACTION_DOWN")));
			}
		});
		addBehaviour(new CyclicBehaviour() {
			Message m;

			@Override
			public void action() {
				System.out.println("Message Behaviour");
				ACLMessage msg = blockingReceive();
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
		});
	}

	@Override
	protected void takeDown() {
		try {
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			DFService.deregister(this, dfd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendMessage(String Message) {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setSender(getAID());
		msg.addReceiver(new AID(worldName, AID.ISLOCALNAME));

		msg.setContent(Message);
		System.out.println("schicke: "+Message);
		send(msg);
	}

	public MyAgent() {
		behaviours = new ArrayList<>();
		behaviours.add(new SearchBehaviour());
		behaviours.add(new MessageBehaviour(this));
	}

	public void runAgent() {

	}

}
