package informationWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import agent.MyAgent;

import data.Cord;

public class AgentWindow extends JFrame {

	private final String title = "AgentWindow to see information";

	private List<MyAgent> listAgents = new ArrayList<MyAgent>();
	private Vector<Vector<String>> rowData = new Vector<Vector<String>>();
	private Vector<String> columns = new Vector<String>();
	private DefaultTableModel model;

	private static AgentWindow window = null;

	private AgentWindow() {
		setTitle(title);
		initComponents();
		setSize(300, 600);
	}

	public static AgentWindow getInstance() {
		if (window == null) {
			window = new AgentWindow();
		}

		return window;
	}

	/**
	 * simple method to initialize the names of columns
	 */
	private void initColumns() {
		columns.add("Name");
		columns.add("State");
		columns.add("currentFood");
		columns.add("total collected");
		columns.add("Position");
	}

	/**
	 * simple method to initialize the most important components for this window
	 */
	private void initComponents() {
		initColumns();
		model = new DefaultTableModel(rowData, columns);
		JTable table = new JTable(model);
		this.add(new JScrollPane(table));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	/**
	 * simple method to show the window not automatically
	 */
	public void start() {
		setVisible(true);
	}

	/**
	 * method to add an agent and show it in the agentWindow
	 * 
	 * @param agent
	 *            the new agent
	 * @param currentLocation
	 *            its current location
	 */
	public void addAgent(MyAgent agent, Cord currentLocation) {
		this.listAgents.add(agent);
		Vector<String> row = new Vector<String>();
		row.add(agent.getLocalName());
		row.add(agent.getAgentState().toString());
		row.add(agent.hasFood() ? "1" : "0");
		row.add("-");
		row.add(currentLocation.toString());
		// rowData.add(row);

		model.addRow(row);
	}

	/**
	 * method to update the current location of each agent
	 * 
	 * @param agent
	 *            the agent which location will be updated
	 * @param newLocation
	 *            the new location of the agent
	 */
	public void refreshLocationOfAgent(MyAgent agent, Cord newLocation) {
		for (int i = 0; i < listAgents.size(); ++i) {
			if (listAgents.get(i).equals(agent)) {
				model.setValueAt(newLocation.toString(), i, 4);
			}
		}
	}

	/**
	 * method to remove one known agent from the map
	 * 
	 * @param agent2Delete
	 *            the agent which will be deleted
	 * @return true if removing was success
	 */
	public boolean removeAgent(MyAgent agent2Delete) {
		return listAgents.remove(agent2Delete);
	}

}
