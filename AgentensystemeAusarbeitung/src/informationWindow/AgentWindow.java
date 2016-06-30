package informationWindow;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import agent.ThiefAgent;

import data.Coordinate;

public class AgentWindow extends JFrame {

	private final String title = "AgentWindow to see information";

	/**
	 * maybe we don't need this list
	 */
	private List<ThiefAgent> listAgents = new ArrayList<ThiefAgent>();

	/**
	 * tree, model and root-node to display the agents
	 */
	private JTree tree;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode root;

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

	private void initComponents() {
		root = new DefaultMutableTreeNode("Agents");
		treeModel = new DefaultTreeModel(root);
		tree = new JTree(treeModel);

		this.add(new JScrollPane(tree));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// start();
	}

	public void start() {
		setVisible(true);
	}

	// /**
	// * Deprecated because it's only one line
	// *
	// * @return the DefaultMutableTreeNode root
	// */
	// @Deprecated
	// private DefaultMutableTreeNode getTree() {
	// DefaultMutableTreeNode root = new DefaultMutableTreeNode("Agents");
	//
	// // for (int i = 0; i < 10; ++i) {
	// // DefaultMutableTreeNode agent = new DefaultMutableTreeNode("Agent0" +
	// // i);
	// // root.add(agent);
	// // }
	// // for (int i = 0; i < listAgents.size(); ++i) {
	// // DefaultMutableTreeNode agent = new
	// // DefaultMutableTreeNode(listAgents.get(i).getLocalName());
	// //
	// // DefaultMutableTreeNode state = new
	// // DefaultMutableTreeNode(listAgents.get(i).getState());
	// // // DefaultMutableTreeNode color
	// // // DefaultMutableTreeNode currentFood
	// // // DefaultMutableTreeNode totalFood?
	// // // DefaultMutableTreeNode nextAction
	// // // DefaultMutableTreeNode cell
	// // agent.add(state);
	// // root.add(agent);
	// // }
	//
	// return root;
	// }

	/**
	 * 
	 * @param newAgent
	 */
	public void setAgentList(List<ThiefAgent> newAgent, List<Coordinate> locations) {
		this.listAgents = newAgent;

		for (int i = 0; i < newAgent.size(); ++i) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(newAgent.get(i).getLocalName());
			DefaultMutableTreeNode agent = new DefaultMutableTreeNode("Name: " + newAgent.get(i).getLocalName());
			@SuppressWarnings("static-access")
			DefaultMutableTreeNode color = new DefaultMutableTreeNode("Color: " + newAgent.get(i).agentColor);
			DefaultMutableTreeNode aState = new DefaultMutableTreeNode(
					"State: " + newAgent.get(i).getAgentState().getName());
			// DefaultMutableTreeNode image = new DefaultMutableTreeNode("Icon:
			// " + newAgent.get(i).getNameOfImage());
			DefaultMutableTreeNode location = new DefaultMutableTreeNode("Position: " + locations.get(i).toString());

			node.add(agent);
			node.add(color);
			node.add(aState);
			// node.add(image);
			node.add(location);

			treeModel.insertNodeInto(node, root, root.getChildCount());
			tree.treeDidChange();
		}
	}

	public void refreshLocationOfAgent(ThiefAgent agent, Coordinate newLocation) {
		for (int i = 0; i < root.getChildCount(); ++i) {
			// if(root.getChildAt(i).))){
			// }
		}
	}

	/**
	 * logic has to be updated
	 * 
	 * @param agent2Delete
	 *            the node which will be deleted
	 * @return if removing was success
	 */
	public boolean removeAgent(ThiefAgent agent2Delete) {
		return listAgents.remove(agent2Delete);
	}

	// public List<MyAgent> getAgentList() {
	// return this.listAgents;
	// }
}
