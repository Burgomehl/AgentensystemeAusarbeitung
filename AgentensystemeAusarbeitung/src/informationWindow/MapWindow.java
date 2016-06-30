package informationWindow;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import agent.MyAgent;

import data.Cell;
import data.Cord;
import data.Map;

public class MapWindow extends JFrame {

	private final String title = "Map of AntWorld";

	private static MapWindow mapWindow = null;

	private JScrollPane scrollPane;
	private Background bGround = new Background();
	private Foreground fGround = new Foreground();
	private Toolbar toolbar = new Toolbar();

	private AgentWindow agentWindow = AgentWindow.getInstance();
	private List<MyAgent> agentList = new ArrayList<MyAgent>();
	private List<Cord> locations = new ArrayList<Cord>();

	private Map map;

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
				JLayeredPane layer = new JLayeredPane();
				layer.setLayout(null);
				scrollPane = new JScrollPane(layer);
				scrollPane.getVerticalScrollBar().setUnitIncrement(16);
				scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
				layer.add(bGround, new Integer(0));
				layer.add(fGround, new Integer(1));
				setSize(800, 600);
				setJMenuBar(toolbar);
				add(scrollPane);

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
		bGround.setMapWindow(this);
		fGround.setMapWindow(this);
	}

	/**
	 * 
	 * @param field,
	 *            two-dimensional array to call method "receiveMap" in
	 *            MapWindow.Screen with the same value
	 */
	public void receiveMap(Cell[][] field, Cord currentLocation, String nameOfAgent) {
		if ((field.length + 2) * bGround.scaledHeight > bGround.getHeight()
				|| (field[0].length + 2) * bGround.scaledWidth > bGround.getWidth()) {
			resizeScreen((field[0].length + 2) * bGround.scaledWidth, (field.length + 2) * bGround.scaledHeight);
		}
		for (int i = 0; i < agentList.size(); ++i) {
			if (agentList.get(i).getLocalName().equalsIgnoreCase(nameOfAgent)) {
				locations.add(i, currentLocation);
			}
		}
		System.out.println("field: " + field.length + "\t" + field[0].length);
		bGround.receiveMap(field);
		fGround.receiveMap(field.length + 2, field[0].length + 2, agentList, locations);
	}

	private void resizeScreen(int width, int height) {
		Runnable next = new Runnable() {
			@Override
			public void run() {
				bGround.setSize(new Dimension(width, height));
				fGround.setSize(new Dimension(width, height));
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
	public void addAgent(MyAgent newAgent, Cord location) {
		agentList.add(newAgent);
		locations.add(location);
		Runnable next = new Runnable() {
			@Override
			public void run() {
				// toolbar.refreshAgentMenu(newAgent);
				agentWindow.setAgentList(agentList, locations);
			}
		};
		SwingUtilities.invokeLater(next);
	}

	private void refreshLocationOfAgent(Cord newLocation) {

	}

	/**
	 * counterpart to MapWindow.addAgent(...) to de-register an agent
	 * 
	 * @param agent2Delete
	 *            is the agent which will be removed
	 * @return if the removing of agent was successful
	 */
	public boolean removeAgent(MyAgent agent2Delete) {
		// return agentWindow.removeAgent(agent2Delete);
		for (int i = 0; i < agentList.size(); ++i) {
			if (agentList.get(i).equals(agent2Delete)) {
				agentList.remove(agent2Delete);
				locations.remove(i);
			}
		}
		// agentList.remove(agent2Delete);
		agentWindow.setAgentList(agentList, locations);
		return true;
	}

	class Foreground extends JComponent {

		private MapWindow mWindow;

		private Dimension size = bGround.size;
		private int scaledWidth = bGround.scaledWidth;
		private int scaledHeight = bGround.scaledHeight;

		private String pathToResources = bGround.pathToResources;
		private Image runner = Toolkit.getDefaultToolkit().createImage(pathToResources + "runner.png");
		private Image lkw = Toolkit.getDefaultToolkit().createImage(pathToResources + "lkw.png");

		private Image[][] ants;

		private void initializeMap() {
			if (bGround.field != null) {
				ants = new Image[bGround.field.length][bGround.field[0].length];
				for (int i = 0; i < bGround.field.length; ++i) {
					for (int j = 0; j < bGround.field[i].length; ++j) {
						if ("start".equalsIgnoreCase(bGround.field[i][j].getType()))
							ants[i + 1][j + 1] = runner;
					}
				}
				MyAgent.log.info("ants: " + ants.length + "\t" + ants[0].length);
				Runnable next = new Runnable() {
					@Override
					public void run() {
						repaint();
					}
				};
				SwingUtilities.invokeLater(next);
			}
		}

		public void setMapWindow(MapWindow mapWindow) {
			mWindow = mapWindow;
		}

		public void receiveMap(int height, int width, List<MyAgent> agentList, List<Cord> locations) {
			// System.out.println("receive: " + height + "\t" + width);
			Image[][] temp = new Image[width][height];
			for (int i = 0; i < agentList.size(); ++i) {

				temp[locations.get(i).getX() + 1][locations.get(i).getY() + 1] = agentList.get(i).hasFood() ? lkw
						: runner;
			}

			ants = temp;
		}

		@Override
		public Dimension getPreferredSize() {
			return size;
		}

		@Override
		public void setSize(Dimension size) {
			super.setSize(size);
			getParent().setSize(size);
			getParent().setPreferredSize(size);
			this.size = size;
		}

		@Override
		public void paintComponent(Graphics g) {

			if (ants != null) {
				// System.out.println("ants2: " + ants.length + "\t" +
				// ants[0].length);
				for (int i = 0; i < ants.length; ++i) {
					for (int j = 0; j < ants[i].length; ++j) {
						g.drawImage(ants[i][j], i * scaledWidth, j * scaledHeight, this);
					}
				}
			} else
				initializeMap();
		}
	}

	class Background extends JComponent {

		// private MediaTracker m = new MediaTracker(this);

		private final String pathToResources = "res/best/";
		private final int scaledHeight = 75;
		private final int scaledWidth = 75;
		private Image stone = Toolkit.getDefaultToolkit().createImage(pathToResources + "bestStone.png");
		private Image grass = Toolkit.getDefaultToolkit().createImage(pathToResources + "grass.png");
		private Image best_food = Toolkit.getDefaultToolkit().createImage(pathToResources + "coin.png");
		private Image trap = Toolkit.getDefaultToolkit().createImage(pathToResources + "police.png");
		private Image fogOfWar = Toolkit.getDefaultToolkit().createImage("res/NebelTile.png");
		private Image startField = Toolkit.getDefaultToolkit().createImage(pathToResources + "hideout.png");

		private MapWindow mWindow;
		private Cell[][] field;
		private Image[][] mapAsImage;

		private Dimension size = new Dimension(800, 600);

		Background() {
			//
		}

		Background(MapWindow mWindow) {
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
			if (field != null) {
				// System.out.println("field: " + field.length + field[0].length
				// + "\t" + field[0][0].getType());
				this.mapAsImage = new Image[field.length + 2][field[0].length + 2];

				for (int i = 0; i < field.length; ++i) {
					for (int j = 0; j < field[i].length; ++j) {
						mapAsImage[i][0] = fogOfWar;
						mapAsImage[0][j] = fogOfWar;
						mapAsImage[mapAsImage.length][j] = fogOfWar;
						mapAsImage[i][mapAsImage[i].length] = fogOfWar;
						if (field[i][j] != null)
							mapAsImage[i + 1][j + 1] = //
									field[i][j].getFood() > 0 ? getCoinsOnGrass(field[i][j].getFood()) //
											: field[i][j].isRock() ? stone //
													: field[i][j].isTrap() ? trap : grass;
						else {
							mapAsImage[i + 1][j + 1] = fogOfWar;
						}
					}
				}
				for (int i = 0; i < field.length; ++i) {
					for (int j = 0; j < field[i].length; ++j) {
						if (field[i][j].getType().equalsIgnoreCase("start"))
							mapAsImage[i][j] = startField;
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

		/**
		 * method to re-build and draw the map
		 * 
		 * @param field,
		 *            the two-dimensional array with cells which have
		 *            information about the cells the agent has explored
		 */
		private void receiveMap(Cell[][] field) {
			this.field = field;
			Image[][] temp = new Image[field.length + 2][field[0].length + 2];

			for (int i = 0; i < field.length; ++i) {
				for (int j = 0; j < field[i].length; ++j) {
					temp[i][0] = fogOfWar;
					temp[0][j] = fogOfWar;
					temp[temp.length - 1][j] = fogOfWar;
					temp[i][temp[i].length - 1] = fogOfWar;
					if (field[i][j] != null)
						if ("start".equalsIgnoreCase(field[i][j].getType()))
							temp[i + 1][j + 1] = startField;
						else {
							temp[i + 1][j + 1] = //
									field[i][j].getFood() > 0 ? getCoinsOnGrass(field[i][j].getFood()) //
											: field[i][j].isRock() ? stone //
													: field[i][j].isTrap() ? trap : grass;
						}
					else {
						temp[i + 1][j + 1] = fogOfWar;
					}
				}
			}

			mapAsImage = temp;

			repaint();
		}

		private Image getCoinsOnGrass(int amountOfFood) {
			int size = scaledWidth * scaledHeight;
			int[] pixelsCoin = new int[75 * 75];
			int[] pixelsGrass = new int[size];
			int[] result = new int[size];
			// PixelGrabber grabber = new PixelGrabber(best_food, 0, 0,
			// scaledWidth, scaledHeight, pixelsCoin, 0,
			// scaledWidth);
			PixelGrabber grabGrass = new PixelGrabber(grass, 0, 0, scaledWidth, scaledHeight, pixelsGrass, 0,
					scaledWidth);
			MemoryImageSource source = new MemoryImageSource(scaledWidth, scaledHeight, result, 0, scaledWidth);
			source.setAnimated(true);
			try {
				// grabber.grabPixels();
				grabGrass.grabPixels();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			BufferedImage out = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
			out.createGraphics().drawImage(grass, 0, 0, scaledWidth, scaledHeight, null);
			// out.createGraphics().setColor(Color.WHITE);
			// out.createGraphics().setBackground(Color.WHITE);
			// out.createGraphics().drawRect(0, 0, scaledWidth, scaledHeight);
			if (amountOfFood < 6) {
				for (int i = 0; i < amountOfFood; ++i) {
					out.createGraphics().drawImage(best_food, 75 / 2 - 10, 75 - i * 10 - 10, 20, 10, null);
				}
				// int cell = 0;
				// for (int i = 0; i < amountOfFood; ++i) {
				// for (int j = 0; j < scaledHeight; ++j) {
				// for (int k = 0; k < scaledWidth; ++k) {
				// if (j * scaledHeight + k >= scaledHeight * (scaledHeight - 1)
				// - 10
				// - best_food.getHeight(this) * i - (scaledWidth / 2 -
				// best_food.getWidth(this) / 2)
				// || j * scaledHeight + k < scaledHeight * (scaledHeight - 1) -
				// 10
				// - (scaledWidth + best_food.getWidth(this) / 2)) {
				// if (!(cell < 200))
				// cell = 0;
				// result[j * scaledHeight + k] = pixelsCoin[cell];
				// // else{
				// // cell = 0;
				// // }
				// }
				// }
				// }
				// }

			} else if (amountOfFood < 11) {
				// int cell = 0;
				// for (int i = 0; i < amountOfFood; ++i) {
				// for (int j = 0; j < scaledHeight; ++j) {
				// for (int k = 0; k < scaledWidth; ++k) {
				// if (j * scaledHeight + k >= scaledHeight * (scaledHeight - 1)
				// - 10
				// - best_food.getHeight(this) * i - (scaledWidth / 2 -
				// best_food.getWidth(this))
				// || j * scaledHeight + k < scaledHeight * (scaledHeight - 1) -
				// 10
				// - (scaledWidth + best_food.getWidth(this))) {
				// if (!(cell < 200))
				// cell = 0;
				// result[j * scaledHeight + k] = pixelsCoin[cell];
				// }
				// }
				// }
				// }
				for (int i = 0; i < amountOfFood; ++i) {
					for (int j = 0; j < 5; ++j, --amountOfFood) {
						out.createGraphics().drawImage(best_food, 75 / 2 - 10 * j, 75 - i * 10 - 10, 20, 10, null);
					}
				}
			} else {
				// int cell = 0;
				// for (int i = 0; i < amountOfFood; ++i) {
				// for (int j = 0; j < scaledHeight; ++j) {
				// for (int k = 0; k < scaledWidth; ++k) {
				// if (j * scaledHeight + k >= scaledHeight * (scaledHeight - 1)
				// - 10
				// - best_food.getHeight(this) * i - (scaledWidth / 2 -
				// best_food.getWidth(this) * 1.5)
				// || j * scaledHeight + k < scaledHeight * (scaledHeight - 1) -
				// 10
				// - (scaledWidth + best_food.getWidth(this) * 1.5)) {
				// if (!(cell < 200))
				// cell = 0;
				// result[j * scaledHeight + k] = pixelsCoin[cell];
				// }
				// }
				// }
				// }
				// for (int i = 0; i < amountOfFood; ++i) {
				for (int j = 0; j < amountOfFood; ++j) {
					for (int k = 0; k < 5; ++k, --amountOfFood) {
						out.createGraphics().drawImage(best_food, 30 + j * 10, 75 - k * 10 - 10, 20, 10, null);
					}
				}
				// }
			}

			PixelGrabber grabber = new PixelGrabber(out, 0, 0, scaledWidth, scaledHeight, pixelsCoin, 0, scaledWidth);

			try {
				grabber.grabPixels();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < pixelsCoin.length; ++i) {

				if (pixelsCoin[i] != 0) {
					result[i] = pixelsCoin[i];
				} else {
					result[i] = pixelsGrass[i];
				}
			}

			source.newPixels();
			return Toolkit.getDefaultToolkit().createImage(source);
		}

		public Image getBestFood() {
			return best_food;
		}

		@Override
		public Dimension getPreferredSize() {
			return size;
		}

		@Override
		public void setSize(Dimension size) {
			super.setSize(size);
			getParent().setSize(size);
			getParent().setPreferredSize(size);
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
					agentWindow.setVisible(true);
				}
			});

			menu.add(show);
			menu.addSeparator();

			return menu;
		}

		// /**
		// * will add a new entry to menubar with the new agent
		// *
		// * @param agent
		// * which is added to the antWorld
		// */
		// public void refreshAgentMenu(MyAgent agent) {
		// JMenuItem item = new JMenuItem("Agent " + agent.getLocalName());
		//
		// agentMenu.add(item);
		// }

	}
}
