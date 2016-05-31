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
	private String inToReplyTo="";
	public static final Logger log = LoggerFactory.getLogger(MyAgent.class);
	

	@Override
	protected void setup() {
		/*Register the agent at the service antWorld2016, later maybe on some other services*/
//		DFAgentDescription dfd = new DFAgentDescription();
//		dfd.setName(this.getAID());
//		// parse and create service description
//		String[] services = { "antWorld2016" };
//		for (int i = 0; i < services.length; ++i) {
//			ServiceDescription sd = new ServiceDescription();
//			sd.setType(services[i].trim());
//			sd.setName(services[i].trim());
//			dfd.addServices(sd);
//		}
//		try {
//			DFService.register(this, dfd);
//			log.info("reg");
//		} catch (FIPAException e1) {
//			e1.printStackTrace();
//		}
		
		/*Searches for agents and services within antworld2016 => saves the found antWorld agent into worldName*/
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
//					SearchConstraints searchConstraints = new SearchConstraints();
//					searchConstraints.setMaxResults(1L);
//					search = DFService.searchUntilFound(myAgent, new AID(AntWorldConsts.SEVICE_NAME,AID.ISLOCALNAME),dfd,searchConstraints, 90000000L);
					log.info("buh");
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
				
				/*Send logindata to the world, but still fails on it*/
				addBehaviour(new OneShotBehaviour() {
					@Override
					public void action() {

						Gson gson = new Gson();
						sendMessage(gson.toJson(new LoginMessage(AntWorldConsts.ANT_ACTION_LOGIN)));
					}
				});
				
			}
			
			
		});
		


		/*Is waiting for messages that arrive at the agent, he will just print the answer*/
		addBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				MyAgent.log.info("Message Behaviour");
				ACLMessage msg = myAgent.blockingReceive();
				if (msg != null) {
					msg.getContent();
					msg.getSender();
					inToReplyTo = msg.getReplyWith();
					Gson gson = new Gson();
					Message m = gson.fromJson(msg.getContent(), Message.class);
					handler.addNewField(new Field(Integer.valueOf(m.currentFood), 0, 0, 0, 0, false, false), Direction.SOUTH);
					MyAgent.log.info("ausgabe"+msg.getContent());
				} else {
					// block();
				}
				
				/*Send movementdata to the world, but still fails on it*/
				addBehaviour(new OneShotBehaviour() {
					@Override
					public void action() {
						Gson gson = new Gson();
						sendMessage(gson.toJson(new LoginMessage("ANT_ACTION_DOWN")));
					}
				});
			}
		});
		

	}

	/**
	 * derigister the service 
	 */
	@Override
	protected void takeDown() {
//		try {
//			DFAgentDescription dfd = new DFAgentDescription();
//			dfd.setName(getAID());
//			DFService.deregister(this, dfd);
//			log.info("Client wird jetzt aus geschaltet");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	/**
	 * send a message to antWorld2016 
	 * @param Message
	 */
	private void sendMessage(String Message) {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setSender(getAID());
		msg.addReceiver(new AID(worldName, AID.ISLOCALNAME));
		msg.setInReplyTo(inToReplyTo);
		msg.setContent(Message);
		msg.setLanguage("JSON");
		log.info("schicke: "+Message);
		send(msg);
	}

	public MyAgent() {
		behaviours = new ArrayList<>();
		behaviours.add(new SearchBehaviour());
		behaviours.add(new MessageBehaviour(this));
	}


}
