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
		System.out.println("registerOnMap");
		mapWindow.addAgent(this);
	}

	public void doKill() {
		mapWindow.dispose();
	}
}
