package agent;

import javax.swing.SwingUtilities;

import com.google.gson.Gson;

import data.Cell;
import data.Cord;
import data.Message;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class GuiAgent extends AbstractAgent {

	// private final MapWindow mapWindow = MapWindow.getInstance();

	public GuiAgent() {
		mapWindow.start();
		// registerOnMap();
	}

	@Override
	protected void loginAtAntWorld() {
		// Shall not login on antWorld
	}

	// @Override
	// public void registerOnMap() {
	// super.registerOnMap();
	// }

	@Override
	public void doDelete() {
		super.doDelete();
		mapWindow.dispose();
		System.exit(0);
	}

	@Override
	protected void evaluateNextStep(Message msg) {
		// TODO Auto-generated method stub

	}

	// @Override
	// protected void loginAtToppic() {
	// // TODO Auto-generated method stub
	//
	// }

	@Override
	protected void addBehaviours() {
		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				MyAgent.log.info("Message Behaviour");
				ACLMessage msg = myAgent.receive();
				if (msg != null) {
					String content = msg.getContent();
					AID sender = msg.getSender();
					log.info("Sender of Message was: " + sender);
					if (msg.getPerformative() == ACLMessage.PROPAGATE) {
						log.info("topic send message to me");
						Gson gson = new Gson();
						Message m = gson.fromJson(content, Message.class);
						Cord cord = m.cord;
						Cell field = m.cell;
						Runnable next = new Runnable() {
							@Override
							public void run() {
								map.updateField(field, cord);

								if (m.agent != null)
									mapWindow.receiveMap(map.getMap(), map.getTotalPosition(cord), m.agent.agentName);
								else {
//									System.out.println("No agent has sent a message: " + m.toString());
									log.debug("No agent has sent a message: " + m.toString());
								}
							}
						};
						SwingUtilities.invokeLater(next);
					} else {
						log.info("Misterious Message received " + msg.getContent());
					}

				} else {
					block();
				}
			}

		});

	}
}
