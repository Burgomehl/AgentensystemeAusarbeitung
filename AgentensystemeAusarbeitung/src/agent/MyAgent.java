package agent;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.gson.Gson;

import data.AgentInfo;
import data.Cell;
import data.Cord;
import data.InformMessage;
import data.Message;
import de.aim.antworld.agent.AntWorldConsts;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class MyAgent extends AbstractAgent {
	private Queue<String> messages = new LinkedList<>();
	private boolean login = false;
	private Deque<Cord> movementOrder;
	private final static Gson gson = new Gson();
	private Cord lastLocation;
	private boolean foundFood = false;

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
						Message m = gson.fromJson(content, Message.class);
						if (!(m.action.equals(AntWorldConsts.ANT_ACTION_DROP) || m.action.equals(AntWorldConsts.ANT_ACTION_COLLECT))) {
							Cell field = new Cell(0, 0, 0, 0, 0, true, false, "FREE");
							map.addNewField(field, currentLocation);
							Message newMessage = new Message();
							newMessage.cell = field;
							newMessage.cord = currentLocation;
							String con = gson.toJson(newMessage);
							sendMessage(con, ACLMessage.PROPAGATE, topicAID);
						}
						currentLocation = lastLocation;
						evaluateNextStep(m);
					}
				} else {
					block();
				}
			}

			private void sendMessageToTopic(Message m) {
				currentLocation = map.addNewField(m.cell, currentLocation);
				m.cord = currentLocation;
				AgentInfo agent = new AgentInfo();
				agent.agentName = myAgent.getLocalName();
				agent.currentPosition = currentLocation;
				m.agent = agent;
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
					if (msg.cell.getFood() > 0) {
						log.info("Searching for best route back home");
						searchNextFieldWithDecision = doIHaveToMoveHome(searchNextFieldWithDecision);
						movementOrder = SearchMethod.searchLikeAStar(map, currentLocation, searchNextFieldWithDecision,
								a -> (map.getMap())[a.getX()][a.getY()] != null);
						movementOrder.addFirst(currentLocation);
						messages.add(gson.toJson(new InformMessage(AntWorldConsts.ANT_ACTION_COLLECT, agentColor)));
						foundFood = true;
					} else if (msg.cell.getStench() == 0) {
						log.info("Searching best way to next empty field");
						searchNextFieldWithDecision = SearchMethod.searchNextFieldWithDecision(map, currentLocation,
								a -> a == null, a -> true);
						searchNextFieldWithDecision = doIHaveToMoveHome(searchNextFieldWithDecision);
						movementOrder = SearchMethod.searchLikeAStar(map, currentLocation, searchNextFieldWithDecision,
								a -> true);

					} else {
						{
							List<Cord> neighbours = map.getNeighbours(currentLocation, a -> true);
							for (Cord cord : neighbours) {
								// if(cord.equals(currentLocation)){
								// continue;
								// }
								Cell posField = map.getCurrentField(cord);
								if (posField != null && posField.isTrap()) {
									posField.setStench(posField.getStench() - 1);
								} else {
									int stenchIntens = 0;
									List<Cord> neighbours2 = map.getNeighbours(cord,
											a -> map.getMap()[a.getX()][a.getY()] != null);
									for (Cord cord2 : neighbours2) {
										Cell currentField = map.getCurrentField(cord2);
										if (currentField.getStench() > 0) {
											stenchIntens++;
										}
									}
									if (stenchIntens >= 3 && map.getCurrentField(cord) == null) {
										Cell newTrap = new Cell(0, 0, 0, 0, 0, false, true, null);
										newTrap.setTrap(true);
										map.addNewField(newTrap, cord);
										System.out.println(cord + " is the position of a trap");
										for (Cord cord2 : neighbours2) {
											Cell currentField2 = map.getCurrentField(cord2);
											currentField2.setStench(currentField2.getStench() - 1);
										}
									}
								}
							}
						}
						log.info("Searching for the next allready visited Field");
						searchNextFieldWithDecision = SearchMethod.searchNextFieldWithDecision(map, currentLocation,
								a -> a != null, a -> (map.getMap())[a.getX()][a.getY()] != null);
						searchNextFieldWithDecision = doIHaveToMoveHome(searchNextFieldWithDecision);
						movementOrder = SearchMethod.searchLikeAStar(map, currentLocation, searchNextFieldWithDecision,
								a -> (map.getMap())[a.getX()][a.getY()] != null);
					}
				}
				moveToNextField(movementOrder.removeFirst());
			}
		}
	}

	private Cord doIHaveToMoveHome(Cord searchNextFieldWithDecision) {
		if (searchNextFieldWithDecision == null) {
			searchNextFieldWithDecision = new Cord(0, 0);
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

	@Override
	public void registerOnMap() {
		super.registerOnMap();
	}

}
