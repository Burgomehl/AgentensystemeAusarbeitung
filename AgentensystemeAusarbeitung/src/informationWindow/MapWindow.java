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
import javax.swing.JScrollPane;

import agent.AbstractAgent;

import data.Cell;
import data.MapAsArrayReloaded;

public class MapWindow extends JFrame {

	private final String title = "Map of AntWorld";

	private static MapWindow mapWindow = null;

	private JScrollPane scrollPane;
	private Screen screen = new Screen();
	private Toolbar toolbar = new Toolbar();

	private AgentWindow agentWindow = AgentWindow.getInstance();

	// private IMap map;
	private MapAsArrayReloaded map;
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
		scrollPane = new JScrollPane(screen);
		add(scrollPane);
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

	/**
	 * let show the window with map and agent information
	 */
	public void start() {
		setVisible(true);
		agentWindow.start();
	}

	public void setMap(MapAsArrayReloaded map) {
		this.map = map;
		screen.setMapWindow(this);
		// this.field = map.getMap();
		// this.mapWithImages = new Image[field.length][field[0].length];
	}

	/**
	 * 
	 * @param field,
	 *            two-dimensional array to call method "receiveMap" in
	 *            MapWindow.Screen with the same value
	 */
	public void receiveMap(Cell[][] field) {
		screen.receiveMap(field);
	}

	/**
	 * method to register a new agent to get information from it and draw map,
	 * show information in AgentWindow...
	 * 
	 * @param newAgent
	 *            is the agent which will be added
	 */
	public void addAgent(AbstractAgent newAgent) {
		toolbar.refreshAgentMenu(newAgent);
		agentWindow.addAgent(newAgent);
	}

	/**
	 * counterpart to MapWindow.addAgent(...) to de-register an agent
	 * 
	 * @param agent2Delete
	 *            is the agent which will be removed
	 * @return
	 */
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
		private Image bestBoy = Toolkit.getDefaultToolkit().createImage(pathToResources + "besterSchütze.png");

		// private int[][] currentLocation = new int[][] { { 0 }, { 0 } };

		private MapWindow mWindow;
		private Cell[][] field;
		private Image[][] mapAsImage;
		// private int[][] positionArray;

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

		/**
		 * first call - when the map was first time created
		 */
		private synchronized void initializeMap() {
			if (field != null) {
				System.out.println("field: " + field.length + field[0].length);
				this.mapAsImage = new Image[field.length + 2][field[0].length + 2];
				for (int i = 0; i < field.length; ++i) {
					for (int j = 0; j < field[i].length; ++j) {
						mapAsImage[i][0] = fogOfWar;
						mapAsImage[0][j] = fogOfWar;
						mapAsImage[mapAsImage.length][j] = fogOfWar;
						mapAsImage[i][mapAsImage[i].length] = fogOfWar;
						if (field[i][j] != null)
							// mapAsImage[i + 1][j + 1] = field[i][j].getFood()
							// > 0 ? best_food
							// : field[i][j].getStench() == 0 ? grass : trap;
							mapAsImage[i + 1][j + 1] = //
									field[i][j].getFood() > 0 ? best_food //
											: field[i][j].isRock() ? stone //
													: field[i][j].isTrap() ? trap : grass;
					}
				}
				// mapAsImage[1][1] = antRed;
				// try {
				// m.waitForAll();
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// positionArray = new int[3][3];
				repaint();
			} else {
				return;
			}
		}

		/**
		 * method to re-build and draw the map
		 * 
		 * @param field,
		 *            the two-dimensional array with cells which have
		 *            information about the cells the agent has explored
		 */
		private synchronized void receiveMap(Cell[][] field) {
			this.field = field;
			Image[][] temp = new Image[field.length + 2][field[0].length + 2];
			for (int i = 0; i < field.length; ++i) {
				for (int j = 0; j < field[i].length; ++j) {
					temp[i][0] = fogOfWar;
					temp[0][j] = fogOfWar;
					temp[temp.length - 1][j] = fogOfWar;
					temp[i][temp[i].length - 1] = fogOfWar;
					if (field[i][j] != null)
						// temp[i + 1][j + 1] = field[i][j].getFood() > 0 ?
						// best_food
						// : field[i][j].getStench() == 0 ? grass : trap;
						temp[i + 1][j + 1] = //
								field[i][j].getFood() > 0 ? best_food //
										: field[i][j].isRock() ? stone //
												: field[i][j].isTrap() ? trap : grass;
					// m.addImage(temp[i][j], i * (j + 1));
				}
			}

			// try {
			// m.waitForAll();
			// } catch (Exception e) {
			// e.printStackTrace();
			// }

			mapAsImage = temp;

			repaint();
		}

		// private void drawMapTile(String found, int x, int y) {
		// // currentLocation[0][0] = x;
		// // currentLocation[0][1] = y;
		// if (found.equalsIgnoreCase("grass"))
		// mapAsImage[getMid()[0] + x][getMid()[1] + y] = grass;
		// else if (found.equalsIgnoreCase("stone"))
		// mapAsImage[getMid()[0] + x][getMid()[1] + y] = stone;
		// else if (found.equalsIgnoreCase("stench"))
		// mapAsImage[getMid()[0] + x][getMid()[1] + y] = trap;
		// else if (found.equalsIgnoreCase("food"))
		// mapAsImage[getMid()[0] + x][getMid()[1] + y] = best_food;
		//
		// repaint();
		// // for (int i = 0; i < positionArray.length; ++i) {
		// // for (int j = 0; j < positionArray[i].length; ++i) {
		// // // if(positionArray[i][j])
		// // }
		// // }
		// }

		// private int[] getMid() {
		// return new int[] { (mapAsImage.length / 2), (mapAsImage[0].length) };
		// }

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(800, 600);
		}

		@Override
		public void paintComponent(Graphics g) {
			if (mapAsImage != null) {
				for (int i = 0; i < mapAsImage.length; ++i) {
					for (int j = 0; j < mapAsImage[i].length; ++j) {
						g.drawImage(mapAsImage[i][j], scaledWidth * i, scaledHeight * j, scaledWidth, scaledHeight,
								this);
					}
				}
			} else {
				initializeMap();
				repaint();
			}
		}

		@Override
		public void repaint() {
			super.repaint();
			// System.out.println("repaint()");
		}
	}

	/**
	 * 
	 * @author Gabriel Meyer
	 *
	 */
	class Toolbar extends JMenuBar {

		JMenu agentMenu = new JMenu("Agents");

		Toolbar() {
			add(getFileMenu());
			add(agentMenu);
		}

		/**
		 * 
		 * @return the menu "File" with the most important entries for agents
		 *         and map
		 */
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

		/**
		 * will add a new entry to menubar with the new agent
		 * 
		 * @param agent
		 *            which is added to the antWorld
		 */
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
