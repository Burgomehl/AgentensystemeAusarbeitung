package agent;

import com.google.gson.Gson;

import data.Cell;
import data.Cord;
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

	@Override
	public void registerOnMap() {
		super.registerOnMap();
	}

	@Override
	public void doDelete() {
		super.doDelete();
		mapWindow.dispose();
		System.exit(0);
	}

	@Override
	protected void logic(Message msg) {
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
						map.addNewField(field, cord);

						// Cell[][] c = map.getMap();
						// for (int i = 0; i < c.length; ++i) {
						// for (Cell cell : c[i]) {
						// System.out.println(cell == null ? "cell null" : "cell
						// not null");
						// }
						// }
						// System.out.println(map.getMap() == null ? "null" :
						// "not null");
						mapWindow.receiveMap(map.getMap());
					} else {
						log.info("Misterious Message received " + msg.getContent());
					}
				} else {
					block();
				}
			}

		});

	}
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
