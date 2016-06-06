package agent;

import java.util.ArrayList;

import com.google.gson.Gson;

import behaviour.MessageBehaviour;
import behaviour.SearchBehaviour;
import data.Direction;
import data.Field;
import de.aim.antworld.agent.AntWorldConsts;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class MyAgent extends AbstractAgent {

	@Override
	protected void addBehaviours() {
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
					} else if (msg.getPerformative() == ACLMessage.REFUSE) {
						state = 2;
					}
				} else {
					block();
				}

			}
		});

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
