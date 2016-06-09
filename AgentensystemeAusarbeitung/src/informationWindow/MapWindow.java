package informationWindow;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
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

	private Screen screen = new Screen();
	private Toolbar toolbar = new Toolbar();

	private AgentWindow agentWindow = AgentWindow.getInstance();

	// private IMap map;
	private MapAsArray map;
	// private Cell[][] field;
	// private Image[][] mapWithImages;

	private MapWindow() {
		initComponent();
	}

	public static MapWindow getInstance() {
		if (mapWindow == null) {
			mapWindow = new MapWindow();
		}

		return mapWindow;
	}

	// public MapWindow(MapAsArray map) {
	// setMap(map);
	// initComponent();
	// }

	protected void initComponent() {
		setTitle(title);
		add(screen);
		setJMenuBar(toolbar);
		pack();

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setState(ICONIFIED);
			}
		});
		// start();
	}

	public void start() {
		setVisible(true);
		agentWindow.start();
	}

	public void setMap(MapAsArray map) {
		this.map = map;
		screen.setMapWindow(this);
		// this.field = map.getMap();
		// this.mapWithImages = new Image[field.length][field[0].length];
	}

	public void addAgent(AbstractAgent newAgent) {
		toolbar.refreshAgentMenu(newAgent);
		agentWindow.addAgent(newAgent);
	}

	public boolean removeAgent(AbstractAgent agent2Delete) {
		return agentWindow.removeAgent(agent2Delete);
	}

	class Screen extends JComponent {

		private MediaTracker m = new MediaTracker(this);

		private final String pathToResources = "res/";
		private final int scaledHeight = 32;
		private final int scaledWidth = 32;
		private Image stone = Toolkit.getDefaultToolkit().createImage(pathToResources + "obstacle.gif");
		private Image grass = Toolkit.getDefaultToolkit().createImage(pathToResources + "ground.gif");
		private Image best_food = Toolkit.getDefaultToolkit().createImage(pathToResources + "food.gif");
		private Image trap = Toolkit.getDefaultToolkit().createImage(pathToResources + "pit.gif");
		private Image fogOfWar = Toolkit.getDefaultToolkit().createImage("res/NebelTile.png");

		private Image antRed = Toolkit.getDefaultToolkit().createImage(pathToResources + "antred.png");
		private Image antGreen = Toolkit.getDefaultToolkit().createImage(pathToResources + "antgreen.png");
		private Image antBlue = Toolkit.getDefaultToolkit().createImage(pathToResources + "antblue.png");
		private Image antYellow = Toolkit.getDefaultToolkit().createImage(pathToResources + "antyellow.png");

		private int[][] currentLocation = new int[][] { { 0 }, { 0 } };

		private MapWindow mWindow;
		private Cell[][] field;
		private Image[][] mapAsImage;

		Screen() {
			//
		}

		Screen(MapWindow mWindow) {
			// this.mWindow = mWindow;
			// this.field = mWindow.map.getMap();
			setMapWindow(mWindow);
		}

		public void setMapWindow(MapWindow mWindow) {
			this.mWindow = mWindow;
			this.field = this.mWindow.map.getMap();
			initializeMap();
			repaint();
		}

		private void initializeMap() {
			this.mapAsImage = new Image[3][3];
			for (int i = 0; i < mapAsImage.length; ++i) {
				for (int j = 0; j < mapAsImage[i].length; ++j) {
					mapAsImage[i][j] = fogOfWar;
					m.addImage(mapAsImage[i][j], i * (j + 1));
				}
			}
			try {
				m.waitForAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
			repaint();
		}

		private void drawMapTile(int x, int y) {
			currentLocation[0][0] = x;
			currentLocation[0][1] = y;

		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(800, 600);
		}

		@Override
		public void paintComponent(Graphics g) {
			if (mapAsImage != null) {
				for (int i = 0; i < mapAsImage.length; ++i) {
					for (int j = 0; j < mapAsImage[i].length; ++j) {
						g.drawImage(fogOfWar, scaledWidth * i, scaledHeight * j, scaledWidth, scaledHeight, this);
					}
				}
			} else {
				initializeMap();
				repaint();
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
