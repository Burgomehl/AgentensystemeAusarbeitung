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
import data.Coordinate;
import data.InformMessage;
import data.Message;
import de.aim.antworld.agent.AntWorldConsts;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ThiefAgent extends AbstractAgent {
	private Queue<String> messages = new LinkedList<>();
	private boolean login = false;
	private Deque<Coordinate> movementOrder;
	private final static Gson gson = new Gson();
	private Coordinate lastLocation;
	private boolean foundFood = false;
	private Set<Coordinate> foundStenches = new HashSet<>();
	private List<Coordinate> stenchCoordinatesToRemove = new LinkedList<>();
	private Queue<Coordinate> foodCoordinates = new LinkedList<>();
	private boolean food = false;
	private int trys = 0;
	private int trapTestSize = 3;

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

		/**
		 * receives messages and decide on msg.performative which steps ahave to be done.
		 * PROPAGATE -> handle messages from the other agents in the topic
		 * INFORM -> mainlogic search for already set movement orders, if the movement orders are empty let the logic find new movement orders
		 * REFUSE -> test if the Refuse could be cause of a rock
		 */
		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				ACLMessage msg = myAgent.receive();
				if (msg != null) {
					String content = msg.getContent();
					int performative = msg.getPerformative();
					if (performative == ACLMessage.PROPAGATE && !msg.getSender().equals(getAID())) {
						log.info("Got Propagate");
						addMapByTopicMsg(content);
					} else if (performative == ACLMessage.INFORM) {
						inToReplyTo = msg.getReplyWith();
						Message m = gson.fromJson(content, Message.class);
						if (m.action.equals(AntWorldConsts.ANT_ACTION_COLLECT)) {
							Cell currentField = map.getCurrentField(currentLocation);
							if (currentField != null) {
								m.cell.setStench(currentField.getStench());
							}
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
							if (currentLocation.equals(new Coordinate(0, 0)) && m.currentFood > 0) {
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
				Coordinate cord = m.cord;
				Cell field = m.cell;
				map.updateField(field, cord);
			}
		});

	}

	/**
	 * the logic of the agent. Starts with a login case. Test if the agent is dead and when these condition are not fullified the agent trys to go after already found food, if this is not possible, 
	 * new movement orders will be created (if the agent is caring food he have to go back to base, if food has been found the agent will collect it and go back home, 
	 * if there is no stench the agent will search for an unknown field, if there is stench it will be analyzed and possible Traps will be set)
	 * at the end if there is no movement order the agent will try to find any food he didn't found until jet and reanalyze all stenches he found 
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
				ThiefAgent.this.doDelete();
			} else {
				if (foundFood) {
					Coordinate searchNextFieldWithDecision = foodCoordinates.poll();
					if (searchNextFieldWithDecision != null) {
						movementOrder = SearchMethod.searchLikeAStar(map, currentLocation, searchNextFieldWithDecision,
								a -> (map.getMap())[a.getX()][a.getY()] != null);
					}
					foundFood = false;
				}
				if (movementOrder == null || movementOrder.isEmpty()) {
					Coordinate searchNextFieldWithDecision = null;
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
					foundFood = true;
					--trapTestSize; 
					for (Coordinate cord : foundStenches) {
						findTraps(cord);
					}
					foundStenches.remove(stenchCoordinatesToRemove);
					++trys;
					evaluateNextStep(msg);
					return;
				} else {
					try {
						moveToNextField(movementOrder.removeFirst());
					} catch (Exception e) {
						ThiefAgent.this.doDelete();
						log.info("Stopped Agent");
					}
					trys = 0;
					trapTestSize = 3;
				}
			}
		}

	}

	private void findTraps(Coordinate posToSearch) {
		List<Coordinate> neighbours = map.getNeighbours(posToSearch, a -> map.getMap()[a.getX()][a.getY()] == null);
		Queue<Coordinate> possibleTraps = new LinkedList<>();
		if (neighbours.isEmpty()) {
			decreaseStench(posToSearch, null);
		}
		for (Coordinate cord : neighbours) {
			if (neighbours.size() == 1) {
				setFieldAsTrap(cord);
				break;
			}
			int stenchIntens = 0;
			List<Coordinate> neighbours2 = map.getNeighbours(cord, a -> map.getMap()[a.getX()][a.getY()] != null);
			for (Coordinate cord2 : neighbours2) {
				Cell currentField = map.getCurrentField(cord2);
				if (currentField.getStench() > 0) {
					stenchIntens++;
				}
			}
			if (stenchIntens >= trapTestSize && map.getCurrentField(cord) == null) {
				possibleTraps.add(cord);
			}
		}
		if(possibleTraps.size()==1){
			setFieldAsTrap(possibleTraps.remove());
		}
	}

	private void decreaseStench(Coordinate cord, Cell cell) {
		if (cell == null) {
			cell = map.getCurrentField(cord);
		}
		cell.setStench(cell.getStench() - 1);
		if (cell.getStench() <= 0) {
			stenchCoordinatesToRemove.add(cord);
			if(cell.getFood()>0){
				if(!foodCoordinates.contains(cord)){
					foodCoordinates.add(cord);
					foundFood = true;
				}
			}
		}
		map.updateField(cell, cord);
		Message newMessage = new Message();
		newMessage.cell = cell;
		newMessage.cord = cord;
		String con = gson.toJson(newMessage);
		sendMessage(con, ACLMessage.PROPAGATE, topicAID);
	}

	private void setFieldAsTrap(Coordinate cord) {
		List<Coordinate> neighbours2 = map.getNeighbours(cord, a -> map.getMap()[a.getX()][a.getY()] != null);
		Cell newTrap = new Cell(0, 0, 0, 0, 0, false, true, null);
		newTrap.setTrap(true);
		map.updateField(newTrap, cord);
		Message newMessage = new Message();
		newMessage.cell = newTrap;
		newMessage.cord = cord;
		String con = gson.toJson(newMessage);
		sendMessage(con, ACLMessage.PROPAGATE, topicAID);
		for (Coordinate cord2 : neighbours2) {
			decreaseStench(cord2, null);
		}
	}

	private void foundFoodGoHome(Coordinate searchNextFieldWithDecision) {
		searchNextFieldWithDecision = doIHaveToMoveHome(searchNextFieldWithDecision);
		movementOrder = SearchMethod.searchLikeAStar(map, currentLocation, searchNextFieldWithDecision,
				a -> (map.getMap())[a.getX()][a.getY()] != null);
		movementOrder.addFirst(currentLocation);
		messages.add(gson.toJson(new InformMessage(AntWorldConsts.ANT_ACTION_COLLECT, agentColor)));
		foodCoordinates.add(currentLocation);
		food = true;
	}

	private Coordinate doIHaveToMoveHome(Coordinate searchNextFieldWithDecision) {
		if (searchNextFieldWithDecision == null) {
			return new Coordinate(0, 0);
		}
		return searchNextFieldWithDecision;
	}

	private void moveToNextField(Coordinate toGoCord) {
		String action = nextStep(toGoCord);
		lastLocation = currentLocation;
		currentLocation = toGoCord;
		log.info("Next movement to " + currentLocation);
		messages.add(gson.toJson(new InformMessage(action, agentColor)));
	}

	private String nextStep(Coordinate toGoCord) {
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

	public ThiefAgent() {
	}

	@Override
	public void setup() {
		super.setup();
		registerOnMap();
	}

	public void registerOnMap() {
		mapWindow.addAgent(this, currentLocation);
	}

	public boolean hasFood() {
		return food;
	}

}
