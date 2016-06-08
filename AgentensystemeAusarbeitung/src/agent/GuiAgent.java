package agent;

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
		// mapWindow.addAgent(this);
		super.registerOnMap();
	}

	@Override
	public void doDelete() {
		super.doDelete();
		mapWindow.dispose();
		System.exit(0);
	}
}
