package informationWindow;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import agent.AbstractAgent;

public class AgentWindow extends JFrame {

	private final String title = "AgentWindow to see information";

	/**
	 * maybe we don't need this list
	 */
	private final List<AbstractAgent> listAgents = new ArrayList<AbstractAgent>();

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
		root = getTree();
		treeModel = new DefaultTreeModel(root);
		tree = new JTree(treeModel);

		this.add(new JScrollPane(tree));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// start();
	}

	public void start() {
		setVisible(true);
	}

	/**
	 * Deprecated because it's only one line
	 * 
	 * @return the DefaultMutableTreeNode root
	 */
	@Deprecated
	private DefaultMutableTreeNode getTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Agents");

		// for (int i = 0; i < 10; ++i) {
		// DefaultMutableTreeNode agent = new DefaultMutableTreeNode("Agent0" +
		// i);
		// root.add(agent);
		// }
		// for (int i = 0; i < listAgents.size(); ++i) {
		// DefaultMutableTreeNode agent = new
		// DefaultMutableTreeNode(listAgents.get(i).getLocalName());
		//
		// DefaultMutableTreeNode state = new
		// DefaultMutableTreeNode(listAgents.get(i).getState());
		// // DefaultMutableTreeNode color
		// // DefaultMutableTreeNode currentFood
		// // DefaultMutableTreeNode totalFood?
		// // DefaultMutableTreeNode nextAction
		// // DefaultMutableTreeNode cell
		// agent.add(state);
		// root.add(agent);
		// }

		return root;
	}

	public void addAgent(AbstractAgent newAgent) {
		listAgents.add(newAgent);

		DefaultMutableTreeNode node = new DefaultMutableTreeNode(newAgent.getLocalName());
		DefaultMutableTreeNode agent = new DefaultMutableTreeNode("Name: " + newAgent.getLocalName());
		DefaultMutableTreeNode state = new DefaultMutableTreeNode("State: " + newAgent.getState());
		DefaultMutableTreeNode aState = new DefaultMutableTreeNode("AgentState: " + newAgent.getAgentState().getName());
		DefaultMutableTreeNode locName = new DefaultMutableTreeNode(
				"PositionName: " + newAgent.getLocation().getName());
		DefaultMutableTreeNode location = new DefaultMutableTreeNode("Postion: " + newAgent.getLocation().getAddress());

		node.add(agent);
		node.add(state);
		node.add(aState);
		node.add(locName);
		node.add(location);

		treeModel.insertNodeInto(node, root, root.getChildCount());
		tree.treeDidChange();
	}

	/**
	 * logic has to be updated
	 * 
	 * @param agent2Delete
	 *            the node which will be deleted
	 * @return if removing was success
	 */
	public boolean removeAgent(AbstractAgent agent2Delete) {
		return listAgents.remove(agent2Delete);
	}

	public List<AbstractAgent> getAgentList() {
		return this.listAgents;
	}
}
