package agent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import Start.Start;
import behaviour.IBehaviour;
import behaviour.MessageBehaviour;
import behaviour.SearchBehaviour;
import behaviour.WaitForMessageBehaviour;
import data.Direction;
import data.Field;
import data.IMap;
import data.Map;
import data.MapAsArray;
import de.aim.antworld.agent.AntWorldConsts;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class MyAgent extends Agent implements IAgent {
	List<IBehaviour> behaviours;
	private String worldName = "";
	private IMap handler = new MapAsArray();
	private String inToReplyTo = "";
	private int state;
	public static final Logger log = LoggerFactory.getLogger(MyAgent.class);

	@Override
	protected void setup() {
		state = 0;

		/*
		 * Searches for agents and services within antworld2016 => saves the
		 * found antWorld agent into worldName
		 */
		addBehaviour(new OneShotBehaviour(this) {
			@Override
			public void action() {
				ServiceDescription filter = new ServiceDescription();
				filter.setName(AntWorldConsts.SEVICE_NAME);
				DFAgentDescription dfd = new DFAgentDescription();
				dfd.addServices(filter);

				DFAgentDescription[] search;
				try {
					search = DFService.search(myAgent, dfd);
					for (int i = 0; i < search.length; i++) {
						String localName = search[i].getName().getLocalName();
						log.info(localName + ":");
						if (localName.contains("antWorld")) {
							worldName = localName;
						}
					}
				} catch (FIPAException e) {
					e.printStackTrace();
				}
			}

		});

		/* Send logindata to the world, but still fails on it */
		addBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				Gson gson = new Gson();
				switch (state) {
				case (0):
					sendMessage(gson.toJson(new LoginMessage(AntWorldConsts.ANT_ACTION_LOGIN)));
					state = 9998;
					break;
				case (1):
					sendMessage(gson.toJson(new LoginMessage(AntWorldConsts.ANT_ACTION_DOWN)));
					state = 9999;
					break;
				case (2):
					sendMessage(gson.toJson(new LoginMessage(AntWorldConsts.ANT_ACTION_LEFT)));
					state = 9999;
					break;
				default:
					block();
					break;
				}
			}
		});

		/*
		 * Is waiting for messages that arrive at the agent, he will just print
		 * the answer
		 */
		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				MyAgent.log.info("Message Behaviour");
				ACLMessage msg = myAgent.receive();
				if (msg != null) {
					msg.getContent();
					msg.getSender();
					inToReplyTo = msg.getReplyWith();
					log.info(inToReplyTo);
					if (state == 9998) {
						state = 1;
					}
					if (msg.getPerformative() == ACLMessage.INFORM) {
						Gson gson = new Gson();
						Message m = gson.fromJson(msg.getContent(), Message.class);
						handler.addNewField(new Field(Integer.valueOf(m.currentFood), 0, 0, 0, 0, false, false),
								Direction.SOUTH);
						MyAgent.log.info("ausgabe" + msg.getContent());
					}else if(msg.getPerformative() == ACLMessage.REFUSE){
						state = 2;
					}
				} else {
					block();
				}

			}
		});

	}

	/**
	 * derigister the service
	 */
	@Override
	protected void takeDown() {
		// try {
		// DFAgentDescription dfd = new DFAgentDescription();
		// dfd.setName(getAID());
		// DFService.deregister(this, dfd);
		// log.info("Client wird jetzt aus geschaltet");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * send a message to antWorld2016
	 * 
	 * @param Message
	 */
	private void sendMessage(String Message) {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setSender(getAID());
		msg.addReceiver(new AID(worldName, AID.ISLOCALNAME));
		msg.setInReplyTo(inToReplyTo);
		msg.setContent(Message);
		msg.setLanguage("JSON");
		log.info("schicke: " + Message);
		send(msg);
	}

	public MyAgent() {
		behaviours = new ArrayList<>();
		behaviours.add(new SearchBehaviour());
		behaviours.add(new MessageBehaviour(this));
	}

}
