package informationWindow;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import agent.AbstractAgent;

import data.Cell;
import data.MapAsArray;

public class MapWindow extends JFrame {

	private final String title = "Map of AntWorld";

	private static MapWindow mapWindow = null;

	private Toolbar toolbar = new Toolbar();

	private AgentWindow agentWindow = AgentWindow.getInstance();

	// private IMap map;
	private MapAsArray map;
	// private Field[][] field;

	private MapWindow() {
		initComponent();
	}

	public static MapWindow getInstance() {
		if (mapWindow == null) {
			mapWindow = new MapWindow();
		}

		return mapWindow;
	}

	public MapWindow(MapAsArray map) {
		setMap(map);
		initComponent();
	}

	protected void initComponent() {
		setTitle(title);
		add(new Screen());
		setJMenuBar(toolbar);
		pack();

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setState(ICONIFIED);
			}
		});
		start();
	}

	public void start() {
		setVisible(true);
	}

	public void setMap(MapAsArray map) {
		this.map = map;
		// this.field = map.getMap();
	}

	public void addAgent(AbstractAgent newAgent) {
		toolbar.refreshAgentMenu(newAgent);
		agentWindow.addAgent(newAgent);
	}

	public boolean removeAgent(AbstractAgent agent2Delete) {
		return agentWindow.removeAgent(agent2Delete);
	}

	class Screen extends JComponent {

		private MapWindow mWindow;
		private Cell[][] field;

		Screen() {

		}

		Screen(MapWindow mWindow) {
			// this.mWindow = mWindow;
			// this.field = mWindow.map.getMap();
			setMapWindow(mWindow);
		}

		public void setMapWindow(MapWindow mWindow) {
			this.mWindow = mWindow;
			this.field = this.mWindow.map.getMap();
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(800, 600);
		}

		@Override
		public void paintComponent(Graphics g) {
			if (field != null) {
				for (int i = 0; i < field.length; ++i) {
					for (int j = 0; j < field[i].length; ++j) {
						//
					}
				}
			}
		}
	}

	class Toolbar extends JMenuBar {

		JMenu agentMenu = new JMenu("Agents");

		Toolbar() {
			add(getFileMenu());
			add(agentMenu);
		}

		private JMenu getFileMenu() {
			JMenu menu = new JMenu("File");
			JMenuItem show = new JMenuItem("Show agentwindow");
			show.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// agentWindow = AgentWindow.getInstance();
					agentWindow.setVisible(true);
				}
			});

			menu.add(show);
			menu.addSeparator();

			return menu;
		}

		public void refreshAgentMenu(AbstractAgent agent) {
			JMenuItem item = new JMenuItem("Agent " + agent.getLocalName());

			agentMenu.add(item);
		}

		/**
		 * Deprecated because it's only one line
		 * 
		 * @return the JMenu for agents
		 */
		@SuppressWarnings("unused")
		@Deprecated
		private JMenu getAgentMenu() {
			JMenu menu = new JMenu("Agent");

			// for (int i = 0; i < agentWindow.getAgentList().size(); ++i) {
			// JMenuItem item = new JMenuItem("Show only agent " + i);
			// menu.add(item);
			// }

			// JMenuItem itemOne = new JMenuItem("Show only agent one");
			//
			// menu.add(itemOne);
			return menu;
		}
	}
}
