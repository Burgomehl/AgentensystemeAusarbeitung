package agent;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import behaviour.IBehaviour;
import data.Cord;
import data.MapAsArray;
import de.aim.antworld.agent.AntWorldConsts;
import informationWindow.MapWindow;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public abstract class AbstractAgent extends Agent {
	protected List<IBehaviour> behaviours;
	protected String worldName = "";
	protected MapAsArray map = new MapAsArray();
	protected String inToReplyTo = "";
	protected int state;
	protected Cord currentLocation;
	protected boolean releaseLock = true;
	protected final MapWindow mapWindow = MapWindow.getInstance();

	public static final Logger log = Logger.getLogger(Agent.class);

	@Override
	protected void setup() {
		PropertyConfigurator.configure("log4j.properties");
		state = 0;
		currentLocation = map.getCurrentLocation();
		loginAtAntWorld();
		loginAtToppic();
		logic(null);
		addBehaviours();
		// registerOnMap();
	}

	protected void loginAtAntWorld() {
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
			}

		});
	}

	protected void registerOnMap() {
		mapWindow.addAgent(this);
	}

	protected abstract void logic(Message msg);

	protected abstract void loginAtToppic();

	protected abstract void addBehaviours();

}
