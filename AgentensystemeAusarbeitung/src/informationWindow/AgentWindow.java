package informationWindow;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import agent.MyAgent;

public class AgentWindow extends JFrame {

	private final String title = "AgentWindow to see information";
	private final List<MyAgent> listAgents = new ArrayList<MyAgent>();

	public AgentWindow() {
		setTitle(title);
	}

	private void initComponents() {
		TreeNode root = getTree();
		JTree tree = new JTree(root);

		this.add(new JScrollPane(tree));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		start();
	}

	public void start() {
		setVisible(true);
	}

	private TreeNode getTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Agents");

		for (int i = 0; i < listAgents.size(); ++i) {
			DefaultMutableTreeNode agent = new DefaultMutableTreeNode(listAgents.get(i).getLocalName());

			DefaultMutableTreeNode state = new DefaultMutableTreeNode(listAgents.get(i).getState());
			// DefaultMutableTreeNode color
			// DefaultMutableTreeNode currentFood
			// DefaultMutableTreeNode totalFood?
			// DefaultMutableTreeNode nextAction
			// DefaultMutableTreeNode cell
			agent.add(state);
			root.add(agent);
		}

		return root;
	}

	public void addAgent(MyAgent newAgent) {
		listAgents.add(newAgent);
	}

	public boolean removeAgent(MyAgent agent2Delete) {
		return listAgents.remove(agent2Delete);
	}
}
