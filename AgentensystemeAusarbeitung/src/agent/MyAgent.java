package agent;

import behaviour.MessageBehaviour;
import behaviour.SearchBehaviour;
import jade.core.Agent;

public class MyAgent extends Agent implements IAgent{

	@Override
	protected void setup() {
		System.out.println("hello world! my name is " + getLocalName());
		addBehaviour(new MessageBehaviour());
		addBehaviour(new SearchBehaviour());
	}
	
	public MyAgent(){
		
	}
	
	public void runAgent(){
	
	}
	
	
}
