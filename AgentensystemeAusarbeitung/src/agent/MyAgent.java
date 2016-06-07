package agent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.gson.Gson;

import behaviour.MessageBehaviour;
import behaviour.SearchBehaviour;
import data.Cell;
import data.Cord;
import de.aim.antworld.agent.AntWorldConsts;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class MyAgent extends AbstractAgent {
	private Queue<String> messages = new LinkedList<>();
	private boolean login = false;
	private Cord lastCord;

	@Override
	protected void addBehaviours() {
		/* Send logindata to the world, but still fails on it */
		addBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				while (!messages.isEmpty()) {
					String message = messages.remove();
					sendMessage(message);
				}
				// if (messages.isEmpty()) {
				// block();
				// }
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
				for (int i = 0; i < 10000000; i++) {
					
				}
				if (msg != null) {
					msg.getContent();
					msg.getSender();
					inToReplyTo = msg.getReplyWith();
					log.info(inToReplyTo);
					if (msg.getPerformative() == ACLMessage.INFORM) {
						Gson gson = new Gson();
						Message m = gson.fromJson(msg.getContent(), Message.class);
						currentLocation = map.addNewField(m.cell, currentLocation);
						MyAgent.log.info("ausgabe" + msg.getContent());
						logic(m);
					} else if (msg.getPerformative() == ACLMessage.REFUSE) {
						map.addNewField(new Cell(0, 0, 0, 0, 0, true, false, "FREE"), currentLocation);
						currentLocation = lastCord;
						Gson gson = new Gson();
						Message m = gson.fromJson(msg.getContent(), Message.class);
						logic(m);
					}
				} else {
					block();
				}

			}
		});

	}

	@Override
	protected void logic(Message msg) {
		Gson gson = new Gson();
		if (msg == null) {
			if (!login) {
				log.debug("Test");
				log.error("Test");
				log.info("Test");
				log.warn("test");
				messages.add(gson.toJson(new InformMessage(AntWorldConsts.ANT_ACTION_LOGIN)));
				login = true;
			}
		} else {
			if(msg.state.equals("DEAD")){
				doSuspend(); //Tötet der sich dann selber ? 
				//FIXME: BBYL
			}
			if (msg.cell.getStench() == 0) {
				List<Cord> possibleNeighbours = new ArrayList<>();
				List<Cord> neighbours = map.getNeighbours(currentLocation);
				
				Cord toGoCord = lastCord;
				for (Cord cord : neighbours) {
					if (cord != null) {
						if (map.getCurrentField(cord) == null) {
							possibleNeighbours.add(cord);
						}
					}
				}
				toGoCord = getNextField(possibleNeighbours, toGoCord);
				String action = nextStep(toGoCord);
				lastCord = currentLocation;
				currentLocation = toGoCord;
				messages.add(gson.toJson(new InformMessage(action)));
			}else{
				Cord toGoCord = lastCord;
				String action = nextStep(toGoCord);
				currentLocation = lastCord;
				messages.add(gson.toJson(new InformMessage(action)));
			}
		}

	}

	private Cord getNextField(List<Cord> possibleNeighbours, Cord toGoCord) {
		int currentHighestIndex = 0;
		for (Cord cord : possibleNeighbours) {
			int fieldIndex;
			try {
				fieldIndex = map.getFieldIndex(cord);
			} catch (Exception e) {
				log.error("getIndex did something validate the result");
				return getNextField(possibleNeighbours, toGoCord);
			}
			if (fieldIndex >= currentHighestIndex) {
				currentHighestIndex = fieldIndex;
				toGoCord = cord;
			}
		}
		return toGoCord;
	}

	private String nextStep(Cord toGoCord) {
		String action = AntWorldConsts.ANT_ACTION_UP;
		if (currentLocation.getX() < toGoCord.getX()) {
			action = AntWorldConsts.ANT_ACTION_RIGHT;
		} else if (currentLocation.getX() > toGoCord.getX()) {
			action = AntWorldConsts.ANT_ACTION_LEFT;
		} else if (currentLocation.getY() < toGoCord.getY()) {
			action = AntWorldConsts.ANT_ACTION_DOWN;
		} else if (currentLocation.getY() > toGoCord.getY()) {
			action = AntWorldConsts.ANT_ACTION_UP;
		}
		return action;
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
