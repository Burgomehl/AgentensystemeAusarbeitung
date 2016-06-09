package agent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.gson.Gson;

import data.Cell;
import data.Cord;
import de.aim.antworld.agent.AntWorldConsts;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class MyAgent extends AbstractAgent {
	private Queue<String> messages = new LinkedList<>();
	private boolean login = false;

	@Override
	protected void addBehaviours() {
		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				while (!messages.isEmpty()) {
					String message = messages.remove();
					sendMessage(message);
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
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (msg.getPerformative() == ACLMessage.INFORM) {
						log.info("got informmessage");
						Gson gson = new Gson();
						Message m = gson.fromJson(msg.getContent(), Message.class);
						currentLocation = map.addNewField(m.cell, currentLocation);
						log.info("ausgabe" + msg.getContent());
						logic(m);
					} else if (msg.getPerformative() == ACLMessage.REFUSE) {
						log.info("got refuse message");
						map.addNewField(new Cell(0, 0, 0, 0, 0, true, false, "FREE"), currentLocation);
						currentLocation = getTotalPosition(lastCords.remove());
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
				log.debug("Login at antworld");
				messages.add(gson.toJson(new InformMessage(AntWorldConsts.ANT_ACTION_LOGIN, agentColor)));
				login = true;
			}
		} else {
			if (msg.state.equals("DEAD")) {
				log.info("agent is dead");
				doSuspend();
				// FIXME: BBYL
			}
			if (msg.cell.getStench() == 0) {
				log.info("no stench go on");
				List<Cord> possibleNeighbours = new ArrayList<>();
				List<Cord> neighbours = map.getNeighbours(currentLocation);
				Cord toGoCord = null;
				for (Cord cord : neighbours) {
					if (cord != null) {
						if (map.getCurrentField(cord) == null) {
							possibleNeighbours.add(cord);
						}
					}
				}
				if (!possibleNeighbours.isEmpty()) {
					log.info("neighbours found");
					toGoCord = getNextField(possibleNeighbours, toGoCord);
					lastCords.addFirst(getRelativePosition(currentLocation));
				} else {
					log.info("no neighbours found");
					toGoCord = getTotalPosition(lastCords.remove());
				}
				String action = nextStep(toGoCord);
				currentLocation = toGoCord;
				messages.add(gson.toJson(new InformMessage(action, agentColor)));
			} else {
				Cord toGoCord = getTotalPosition(lastCords.remove());
				String action = nextStep(toGoCord);
				log.info("stench found, will go back to last location: " + toGoCord + " from current location: "
						+ currentLocation);
				currentLocation = toGoCord;
				messages.add(gson.toJson(new InformMessage(action, agentColor)));
			}
		}
	}

	private Cord getRelativePosition(Cord cord) {
		Cord newCord = new Cord(cord.getX() - map.getMid().getX() , cord.getY() - map.getMid().getY());
		log.info("converting total : "+cord+" to cord "+newCord);
		return newCord;
	}

	private Cord getTotalPosition(Cord cord) {
		Cord newCord = new Cord(map.getMid().getX() + cord.getX(), map.getMid().getY() + cord.getY());
		log.info("converting relativ : "+cord+" to total "+newCord);
		return newCord;
	}

	private Cord getNextField(List<Cord> possibleNeighbours, Cord toGoCord) {
		int currentHighestIndex = 0;
		for (Cord cord : possibleNeighbours) {
			int fieldIndex = map.getFieldIndex(cord);
			if (fieldIndex >= currentHighestIndex) {
				currentHighestIndex = fieldIndex;
				toGoCord = cord;
			}
		}
		log.info("getNextField: " + toGoCord);
		return toGoCord;
	}

	private String nextStep(Cord toGoCord) {
		log.info("next step to : " + toGoCord);
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
	}

	@Override
	public void registerOnMap() {
	}

	@Override
	protected void loginAtToppic() {

	}

}
