package agent;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import behaviour.IBehaviour;
import data.IMap;
import data.MapAsArray;
import de.aim.antworld.agent.AntWorldConsts;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AbstractAgent extends Agent{
	protected List<IBehaviour> behaviours;
	protected String worldName = "";
	protected IMap handler = new MapAsArray();
	protected String inToReplyTo = "";
	protected int state;
	public static final Logger log = LoggerFactory.getLogger(MyAgent.class);
	
	@Override
	protected void setup() {
		state = 0;
		login();
		addBehaviours();
	}
	
	protected void login(){
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
	
	protected void loginOnToppic(){
		
	}
	
	protected void addBehaviours(){
		
	}

}
