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
}
