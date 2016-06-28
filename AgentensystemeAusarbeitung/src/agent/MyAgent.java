package agent;

import java.util.ArrayList;
import java.util.Deque;
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
	private boolean waitForResponse = false;
	private Deque<Cord> movementOrder;
	private Deque<Cord> movementOrderBack = new LinkedList<>();
	private final static Gson gson = new Gson();
	private Cord lastLocation;

	@Override
	protected void addBehaviours() {
		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				while (!messages.isEmpty()) {
					String message = messages.remove();
					sendMessage(message, ACLMessage.REQUEST, new AID(worldName, AID.ISLOCALNAME));
				}
			}
		});

		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				ACLMessage msg = myAgent.receive();
				if (msg != null) {
					String content = msg.getContent();
					int performative = msg.getPerformative();
					inToReplyTo = msg.getReplyWith();
					if (performative == ACLMessage.PROPAGATE && !msg.getSender().equals(getAID())) {
						log.info("Got Propagate");
						addMapByTopicMsg(content);
					} else if (performative == ACLMessage.INFORM) {
						Message m = gson.fromJson(content, Message.class);
						sendMessageToTopic(m);
						if (movementOrder == null || movementOrder.isEmpty()) {
							log.info("need new movement order");
							if (currentLocation.equals(new Cord(0, 0)) && m.currentFood > 0) {
								messages.add(
										gson.toJson(new InformMessage(AntWorldConsts.ANT_ACTION_DROP, agentColor)));
							} else {
								evaluateNextStep(m);
							}
						} else {
							log.info("Will use allready found way");
							moveToNextField(movementOrder.removeFirst());
						}
					} else if (performative == ACLMessage.REFUSE) {
						log.info("movement was refused");
						Cell field = new Cell(0, 0, 0, 0, 0, true, false, "FREE");
						map.addNewField(field, currentLocation);
						Message newMessage = new Message();
						newMessage.cell = field;
						newMessage.cord = currentLocation;
						String con = gson.toJson(newMessage);
						sendMessage(con, ACLMessage.PROPAGATE, topicAID);
						currentLocation = lastLocation;
						Message m = gson.fromJson(content, Message.class);
						evaluateNextStep(m);
					}
				} else {
					block();
				}
			}

			private void sendMessageToTopic(Message m) {
				currentLocation = map.addNewField(m.cell, currentLocation);
				m.cord = currentLocation;
				String con = gson.toJson(m);
				sendMessage(con, ACLMessage.PROPAGATE, topicAID);
			}

			private void addMapByTopicMsg(String content) {
				log.info("topic send message to me");
				Message m = gson.fromJson(content, Message.class);
				Cord cord = m.cord;
				Cell field = m.cell;
				map.addNewField(field, cord);
			}
		});

	}

	/**
	 * Beim Setup vllt eine Methode aufrufen, welche über die args entscheidet,
	 * welche Logic angeworfen wird Logic als eigene Klasse und Interface und
	 * das dann die Methode da eine Logic einsetzt -> Lambda?!?
	 */

	@Override
	protected void evaluateNextStep(Message msg) {
		if (msg == null) {
			if (!login) {
				log.debug("Login at antworld");
				messages.add(gson.toJson(new InformMessage(AntWorldConsts.ANT_ACTION_LOGIN, agentColor)));
				login = true;
			}
		} else {
			if (msg.state.equals("DEAD")) {
				log.info("agent is dead");
				MyAgent.this.doDelete();
			} else {
				Cord searchNextFieldWithDecision = SearchMethod.searchNextFieldWithDecision(map, currentLocation,
						a -> a != null && a.getFood() > 0, a -> (map.getMap())[a.getX()][a.getY()] != null);
				if (searchNextFieldWithDecision != null) {
					movementOrder = SearchMethod.searchLikeAStar(map, currentLocation, searchNextFieldWithDecision,
							a -> (map.getMap())[a.getX()][a.getY()] != null);
				}
				if (movementOrder != null && !movementOrder.isEmpty()) {
					waitForResponse = true;
				} else {
					if (msg.cell.getFood() > 0) {
						log.info("Searching for best route back home");
						movementOrder = SearchMethod.searchLikeAStar(map, currentLocation, new Cord(0, 0),
								a -> (map.getMap())[a.getX()][a.getY()] != null);
						movementOrder.addFirst(currentLocation);
						messages.add(gson.toJson(new InformMessage(AntWorldConsts.ANT_ACTION_COLLECT, agentColor)));
						waitForResponse = true;
					} else if (msg.cell.getStench() == 0) {
						log.info("Searching best way to next empty field");
						movementOrder = SearchMethod.searchLikeAStar(map, currentLocation, SearchMethod
								.searchNextFieldWithDecision(map, currentLocation, a -> a == null, a -> true),
								a -> true);
						waitForResponse = true;
					} else {
						log.info("Searching for the next allready visited Field");
						movementOrder = SearchMethod.searchLikeAStar(map, currentLocation,
								SearchMethod.searchNextFieldWithDecision(map, currentLocation, a -> a != null,
										a -> (map.getMap())[a.getX()][a.getY()] != null),
								a -> (map.getMap())[a.getX()][a.getY()] != null);
						waitForResponse = true;
					}
				}
				moveToNextField(movementOrder.removeFirst());
			}
		}
	}

	private void moveToNextField(Cord toGoCord) {
		String action = nextStep(toGoCord);
		lastLocation = currentLocation;
		currentLocation = toGoCord;
		log.info("Next movement to " + currentLocation);
		messages.add(gson.toJson(new InformMessage(action, agentColor)));
	}

	/**
	 * method to go to the last location - can improve further
	 * 
	 * @param gson
	 *            to build Json-message
	 */
	private void goLast() {
		Cord lastCord = lastCords.getFirst();
		System.out.println(lastCords.getFirst().toString());
		moveToNextField(lastCord);
	}

	/**
	 * Method to calculate the best possible next field - can improve further
	 * 
	 * @return the next coordinate where the agent will be go
	 */
	private Cord getPossibleNextField() {
		List<Cord> possibleUnknownNeighbours = new ArrayList<Cord>();
		List<Cord> possibleKnownNeighbours = new ArrayList<Cord>();
		List<Cord> neighbours = map.getNeighbours(currentLocation, a -> (map.getMap())[a.getX()][a.getY()] != null);

		for (Cord cord : neighbours) {
			if (cord != null) {
				if (map.getCurrentField(cord) == null) {
					possibleUnknownNeighbours.add(cord);
				} else if (map.getCurrentField(cord) != null) {
					possibleKnownNeighbours.add(cord);
				}
			}
		}
		Cord possibleCord = null;
		if (!possibleKnownNeighbours.isEmpty())
			for (Cord cord : possibleKnownNeighbours) {
				if (map.getCurrentField(cord).getSmell() > 0) {
					possibleCord = cord;
					lastCords.addFirst(possibleCord);
				}
			}
		else if (!possibleUnknownNeighbours.isEmpty()) {
			possibleCord = getNextField(possibleUnknownNeighbours, possibleCord);
			lastCords.addFirst(possibleCord);
		} else
			possibleCord = lastCords.remove();
		return possibleCord;
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
	private void sendMessage(String Message, int messageType, AID aidToSendTo) {
		ACLMessage msg = new ACLMessage(messageType);
		msg.setSender(getAID());
		msg.addReceiver(aidToSendTo);
		msg.setInReplyTo(inToReplyTo);
		msg.setContent(Message);
		msg.setLanguage("JSON");
		log.info("schicke an " + aidToSendTo + " : " + Message);
		send(msg);
	}

	public MyAgent() {
	}

	@Override
	public void registerOnMap() {
		super.registerOnMap();
	}

	// @Override
	// protected void loginAtToppic() {
	// // TODO Auto-generated method stub
	// }
	//
	// @Override
	// protected void receiving() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// protected void sending() {
	// // TODO Auto-generated method stub
	//
	// }

}
