package informationWindow;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import agent.AbstractAgent;

import data.Cell;
import data.Cord;
import data.Map;

public class MapWindow extends JFrame {

	private final String title = "Map of AntWorld";

	private static MapWindow mapWindow = null;

	private JScrollPane scrollPane;
	private Screen screen = new Screen();
	private Toolbar toolbar = new Toolbar();

	private AgentWindow agentWindow = AgentWindow.getInstance();

	// private IMap map;
	private Map map;
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

	protected void initComponent() {
		Runnable next = new Runnable() {
			@Override
			public void run() {
				setTitle(title);
				scrollPane = new JScrollPane(screen, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
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
			}
		};
		SwingUtilities.invokeLater(next);
		// start();
	}

	/**
	 * let show the window with map and agent information
	 */
	public void start() {
		Runnable next = new Runnable() {
			@Override
			public void run() {

				setLocationRelativeTo(null);
				setVisible(true);
				agentWindow.start();

			}
		};
		SwingUtilities.invokeLater(next);
	}

	public void setMap(Map map) {
		this.map = map;
		screen.setMapWindow(this);
	}

	/**
	 * 
	 * @param field,
	 *            two-dimensional array to call method "receiveMap" in
	 *            MapWindow.Screen with the same value
	 */
	public void receiveMap(Cell[][] field, Cord currentLocation) {
		if ((field.length + 2) * 32 > screen.getHeight() || (field[0].length + 2) * 32 > screen.getWidth()) {
			System.out.println("größer als Screen");
			resizeScreen((field[0].length + 2) * 32, (field.length + 2) * 32);
		}
		screen.receiveMap(field, currentLocation);
	}

	private void resizeScreen(int width, int height) {
		Runnable next = new Runnable() {
			@Override
			public void run() {
				screen.setSize(new Dimension(width, height));
			}
		};
		SwingUtilities.invokeLater(next);
	}

	/**
	 * method to register a new agent to get information from it and draw map,
	 * show information in AgentWindow...
	 * 
	 * @param newAgent
	 *            is the agent which will be added
	 */
	public void addAgent(AbstractAgent newAgent) {
		Runnable next = new Runnable() {
			@Override
			public void run() {
				toolbar.refreshAgentMenu(newAgent);
				agentWindow.addAgent(newAgent);
			}
		};
		SwingUtilities.invokeLater(next);
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

		// private MediaTracker m = new MediaTracker(this);

		private final String pathToResources = "res/";
		private final int scaledHeight = 32;
		private final int scaledWidth = 32;
		private Image stone = Toolkit.getDefaultToolkit().createImage(pathToResources + "obstacle.gif");
		private Image grass = Toolkit.getDefaultToolkit().createImage(pathToResources + "ground.gif");
		private Image best_food = Toolkit.getDefaultToolkit().createImage(pathToResources + "food.gif");
		private Image trap = Toolkit.getDefaultToolkit().createImage(pathToResources + "pit.gif");
		private Image fogOfWar = Toolkit.getDefaultToolkit().createImage("res/NebelTile.png");
		private Image startField = Toolkit.getDefaultToolkit().createImage(pathToResources + "startField.png");

		private Image antRed = Toolkit.getDefaultToolkit().createImage(pathToResources + "antred.png");
		private Image antGreen = Toolkit.getDefaultToolkit().createImage(pathToResources + "antgreen.png");
		private Image antBlue = Toolkit.getDefaultToolkit().createImage(pathToResources + "antblue.png");
		private Image antYellow = Toolkit.getDefaultToolkit().createImage(pathToResources + "antyellow.png");
		private Image bestShooter = Toolkit.getDefaultToolkit().createImage(pathToResources + "bestShooter.png");
		private Image bestBoy = Toolkit.getDefaultToolkit().createImage(pathToResources + "bestBoy.png");

		// Images for draw own ants
		private Image grassWithBoy;
		private Image grassWithFoodAndBoy;
		private Image grassWithBoyOnStart;

		private Image grassWithShooter;
		private Image grassWithFoodAndShooter;
		private Image grassWithShooterOnStart;

		// private int[][] currentLocation = new int[][] { { 0 }, { 0 } };

		private MapWindow mWindow;
		private Cell[][] field;
		private Image[][] mapAsImage;
		// private int[][] positionArray;

		private Dimension size = new Dimension(800, 600);

		Screen() {
			//
		}

		Screen(MapWindow mWindow) {
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
		private void initializeMap() {
			initAnts();
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
							mapAsImage[i + 1][j + 1] = //
									field[i][j].getFood() > 0 ? best_food //
											: field[i][j].isRock() ? stone //
													: field[i][j].isTrap() ? trap : grass;
						else {
							mapAsImage[i + 1][j + 1] = fogOfWar;
						}
					}
				}
				Runnable next = new Runnable() {
					@Override
					public void run() {
						repaint();
					}
				};
				SwingUtilities.invokeLater(next);
			} else {
				return;
			}
		}

		private void initAnts() {
			int[] pixelsBack = new int[1024];
			int[] pixelsFood = new int[1024];
			int[] pixelsShooter = new int[1024];
			int[] pixelsBoy = new int[1024];

			int[] result = new int[1024];
			MemoryImageSource source = new MemoryImageSource(scaledWidth, scaledHeight, result, 0, scaledWidth);
			source.setAnimated(true);
			PixelGrabber grabber = new PixelGrabber(grass, 0, 0, scaledWidth, scaledHeight, pixelsBack, 0, scaledWidth);
			PixelGrabber grabberFood = new PixelGrabber(best_food, 0, 0, scaledWidth, scaledHeight, pixelsFood, 0,
					scaledWidth);
			PixelGrabber grabberShooter = new PixelGrabber(bestShooter, 0, 0, scaledWidth, scaledHeight, pixelsShooter,
					0, scaledWidth);
			PixelGrabber grabberBoy = new PixelGrabber(bestBoy, 0, 0, scaledWidth, scaledHeight, pixelsBoy, 0,
					scaledWidth);
			try {
				grabber.grabPixels();
				grabberFood.grabPixels();
				grabberShooter.grabPixels();
				grabberBoy.grabPixels();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < pixelsBack.length; ++i) {
				if (pixelsShooter[i] != 0) {
					result[i] = pixelsShooter[i];
				} else {
					result[i] = pixelsBack[i];
				}
			}
			this.grassWithShooter = Toolkit.getDefaultToolkit().createImage(source);
			source.newPixels();
			for (int i = 0; i < pixelsBack.length; ++i) {
				if (pixelsBoy[i] != 0) {
					result[i] = pixelsBoy[i];
				} else {
					result[i] = pixelsBack[i];
				}
			}
			this.grassWithBoy = Toolkit.getDefaultToolkit().createImage(source);
			source.newPixels();
		}

		/**
		 * method to re-build and draw the map
		 * 
		 * @param field,
		 *            the two-dimensional array with cells which have
		 *            information about the cells the agent has explored
		 */
		private void receiveMap(Cell[][] field, Cord currentLocation) {
			this.field = field;
			Image[][] temp = new Image[field.length + 2][field[0].length + 2];

			for (int i = 0; i < field.length; ++i) {
				for (int j = 0; j < field[i].length; ++j) {
					temp[i][0] = fogOfWar;
					temp[0][j] = fogOfWar;
					temp[temp.length - 1][j] = fogOfWar;
					temp[i][temp[i].length - 1] = fogOfWar;
					if (field[i][j] != null)
						temp[i + 1][j + 1] = //
								field[i][j].getFood() > 0 ? best_food //
										: field[i][j].isRock() ? stone //
												: field[i][j].isTrap() ? trap : grass;
				}
			}

			temp[currentLocation.getX()][currentLocation.getY()] = grassWithBoy;

			mapAsImage = temp;

			Runnable next = new Runnable() {
				@Override
				public void run() {
					repaint();
				}
			};
			SwingUtilities.invokeLater(next);
		}

		@Override
		public Dimension getPreferredSize() {
			return size;
		}

		@Override
		public void setSize(Dimension size) {
			super.setSize(size);
			this.size = size;
		}

		@Override
		public void paintComponent(Graphics g) {
			// System.out.println(getSize());
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

		// @Override
		// public void repaint() {
		// super.repaint();
		// }
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
