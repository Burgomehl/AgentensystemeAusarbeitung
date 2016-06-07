package agent;

import informationWindow.MapWindow;

public class GuiAgent extends AbstractAgent {

	private final MapWindow mapWindow = MapWindow.getInstance();

	public GuiAgent() {
		// registerOnMap();
	}

	@Override
	protected void loginAtAntWorld() {
		// Shall not login on antWorld
	}

	@Override
	public void registerOnMap() {
		mapWindow.addAgent(this);
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

	@Override
	protected void loginAtToppic() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void addBehaviours() {
		// TODO Auto-generated method stub
		
	}
}
