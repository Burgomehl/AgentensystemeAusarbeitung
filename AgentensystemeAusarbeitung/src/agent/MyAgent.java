package agent;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.google.gson.Gson;

import data.AgentInfo;
import data.Cell;
import data.Cord;
import data.InformMessage;
import data.Message;
import de.aim.antworld.agent.AntWorldConsts;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class MyAgent extends AbstractAgent {
	private Queue<String> messages = new LinkedList<>();
	private boolean login = false;
	private Deque<Cord> movementOrder;
	private final static Gson gson = new Gson();
	private Cord lastLocation;
	private boolean foundFood = false;
	private boolean food = false;
	private Set<Cord> foundStenches = new HashSet<>();
	private List<Cord> stenchCoordinatesToRemove = new LinkedList<>();
	private int trys = 0;

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
					if (currentLocation.equals(new Cord(2, -3)) || currentLocation.equals(new Cord(1, -2))
							|| currentLocation.equals(new Cord(2, -1))) {
						System.out.println("Stop");
					}
					if (performative == ACLMessage.PROPAGATE && !msg.getSender().equals(getAID())) {
						log.info("Got Propagate");
						addMapByTopicMsg(content);
					} else if (performative == ACLMessage.INFORM) {
						inToReplyTo = msg.getReplyWith();
						Message m = gson.fromJson(content, Message.class);
						if (m.action.equals(AntWorldConsts.ANT_ACTION_COLLECT)) {
							currentLocation = map.updateField(m.cell, currentLocation);
						} else {
							currentLocation = map.addNewField(m.cell, currentLocation);
						}
						AgentInfo agent = new AgentInfo();
						agent.agentName = myAgent.getLocalName();
						agent.currentPosition = currentLocation;
						m.agent = agent;
						sendMessageToTopic(m);
						if (movementOrder == null || movementOrder.isEmpty()) {
							log.info("need new movement order");
							if (currentLocation.equals(new Cord(0, 0)) && m.currentFood > 0) {
								messages.add(
										gson.toJson(new InformMessage(AntWorldConsts.ANT_ACTION_DROP, agentColor)));
								food = false;
							} else {
								evaluateNextStep(m);
							}
						} else {
							log.info("Will use allready found way");
							moveToNextField(movementOrder.removeFirst());
						}
					} else if (performative == ACLMessage.REFUSE) {
						inToReplyTo = msg.getReplyWith();
						log.info("movement was refused");
						Message m = gson.fromJson(content, Message.class);
						if (!(m.action.equals(AntWorldConsts.ANT_ACTION_DROP)
								|| m.action.equals(AntWorldConsts.ANT_ACTION_COLLECT))) {
							Cell field = new Cell(0, 0, 0, 0, 0, true, false, "FREE");
							map.addNewField(field, currentLocation);
							Message newMessage = new Message();
							newMessage.cell = field;
							sendMessageToTopic(newMessage);
						}
						currentLocation = lastLocation;
						evaluateNextStep(m);
					}
				} else {
					block();
				}
			}

			private void sendMessageToTopic(Message m) {
				m.cord = currentLocation;
				String con = gson.toJson(m);
				sendMessage(con, ACLMessage.PROPAGATE, topicAID);
			}

			private void addMapByTopicMsg(String content) {
				log.info("topic send message to me");
				Message m = gson.fromJson(content, Message.class);
				Cord cord = m.cord;
				Cell field = m.cell;
				map.updateField(field, cord);
			}
		});

	}

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
				if (foundFood) {
					Cord searchNextFieldWithDecision = SearchMethod.searchNextFieldWithDecision(map, currentLocation,
							a -> a != null && a.getFood() > 0, a -> (map.getMap())[a.getX()][a.getY()] != null);
					if (searchNextFieldWithDecision != null) {
						movementOrder = SearchMethod.searchLikeAStar(map, currentLocation, searchNextFieldWithDecision,
								a -> (map.getMap())[a.getX()][a.getY()] != null);
					}
					foundFood = false;
				}
				if (movementOrder == null || movementOrder.isEmpty()) {
					Cord searchNextFieldWithDecision = null;
					if (msg.currentFood > 0) {
						foundFoodGoHome(searchNextFieldWithDecision);
					} else if (msg.cell.getFood() > 0) {
						log.info("Searching for best route back home");
						foundFoodGoHome(searchNextFieldWithDecision);
						foundFood = true;
					} else if (msg.cell.getStench() == 0) {
						log.info("Searching best way to next empty field");
						searchNextFieldWithDecision = SearchMethod.searchNextFieldWithDecision(map, currentLocation,
								a -> a == null, a -> true);
						searchNextFieldWithDecision = doIHaveToMoveHome(searchNextFieldWithDecision);
						movementOrder = SearchMethod.searchLikeAStar(map, currentLocation, searchNextFieldWithDecision,
								a -> true);

					} else {
						foundStenches.add(currentLocation);
						findTraps(currentLocation);
						foundStenches.remove(stenchCoordinatesToRemove);
						log.info("Searching for the next allready visited Field");
						searchNextFieldWithDecision = SearchMethod.searchNextFieldWithDecision(map, currentLocation,
								a -> a != null, a -> (map.getMap())[a.getX()][a.getY()] != null);
						searchNextFieldWithDecision = doIHaveToMoveHome(searchNextFieldWithDecision);
						movementOrder = SearchMethod.searchLikeAStar(map, currentLocation, searchNextFieldWithDecision,
								a -> (map.getMap())[a.getX()][a.getY()] != null);
					}
				}
				if (movementOrder.isEmpty() && trys < 1) {
					for (Cord cord : foundStenches) {
						findTraps(cord);
					}
					foundStenches.remove(stenchCoordinatesToRemove);
					++trys;
					evaluateNextStep(msg);
					return;
				} else {
					moveToNextField(movementOrder.removeFirst());
					trys = 0;
				}
			}
		}
	}

	private void findTraps(Cord posToSearch) {
		List<Cord> neighbours = map.getNeighbours(posToSearch, a -> map.getMap()[a.getX()][a.getY()] == null);
		if (neighbours.isEmpty()) {
			degreaseStench(posToSearch, null);
		}
		for (Cord cord : neighbours) {
			if (neighbours.size() == 1) {
				List<Cord> neighbours2 = map.getNeighbours(cord, a -> map.getMap()[a.getX()][a.getY()] != null);
				setFieldAsTrap(cord, neighbours2);
				break;
			}
			int stenchIntens = 0;
			List<Cord> neighbours2 = map.getNeighbours(cord, a -> map.getMap()[a.getX()][a.getY()] != null);
			for (Cord cord2 : neighbours2) {
				Cell currentField = map.getCurrentField(cord2);
				if (currentField.getStench() > 0) {
					stenchIntens++;
				}
			}
			if (stenchIntens >= 3 && map.getCurrentField(cord) == null) {
				setFieldAsTrap(cord, neighbours2);
			}
		}
	}

	private void degreaseStench(Cord cord, Cell cell) {
		if (cell == null) {
			cell = map.getCurrentField(cord);
		}
		cell.setStench(cell.getStench() - 1);
		if (cell.getStench() <= 0) {
			stenchCoordinatesToRemove.add(cord);
		}
		map.updateField(cell, cord);
		Message newMessage = new Message();
		newMessage.cell = cell;
		newMessage.cord = cord;
		String con = gson.toJson(newMessage);
		sendMessage(con, ACLMessage.PROPAGATE, topicAID);
	}

	private void setFieldAsTrap(Cord cord, List<Cord> neighbours2) {
		Cell newTrap = new Cell(0, 0, 0, 0, 0, false, true, null);
		newTrap.setTrap(true);
		map.updateField(newTrap, cord);
		Message newMessage = new Message();
		newMessage.cell = newTrap;
		newMessage.cord = cord;
		String con = gson.toJson(newMessage);
		sendMessage(con, ACLMessage.PROPAGATE, topicAID);
		// degreaseStench(cord, newTrap);
		for (Cord cord2 : neighbours2) {
			degreaseStench(cord2, null);
		}
	}

	private void foundFoodGoHome(Cord searchNextFieldWithDecision) {
		searchNextFieldWithDecision = doIHaveToMoveHome(searchNextFieldWithDecision);
		movementOrder = SearchMethod.searchLikeAStar(map, currentLocation, searchNextFieldWithDecision,
				a -> (map.getMap())[a.getX()][a.getY()] != null);
		movementOrder.addFirst(currentLocation);
		messages.add(gson.toJson(new InformMessage(AntWorldConsts.ANT_ACTION_COLLECT, agentColor)));
		food = true;
	}

	private Cord doIHaveToMoveHome(Cord searchNextFieldWithDecision) {
		if (searchNextFieldWithDecision == null) {
			return new Cord(0, 0);
		}
		return searchNextFieldWithDecision;
	}

	private void moveToNextField(Cord toGoCord) {
		String action = nextStep(toGoCord);
		lastLocation = currentLocation;
		currentLocation = toGoCord;
		log.info("Next movement to " + currentLocation);
		messages.add(gson.toJson(new InformMessage(action, agentColor)));
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

	public void registerOnMap() {

	}

	public boolean hasFood() {
		return food;
	}

}
