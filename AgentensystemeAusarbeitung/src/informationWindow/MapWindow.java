package informationWindow;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import data.Cell;
import data.MapAsArray;

public class MapWindow extends JFrame {

	private final String title = "Map of AntWorld";

	private MapAsArray map;
	// private Field[][] field;

	public MapWindow() {
		initComponent();
	}

	public MapWindow(MapAsArray map) {
		setMap(map);
		initComponent();
	}

	protected void initComponent() {
		setTitle(title);
		add(new Screen());
		setJMenuBar(new Toolbar());
		pack();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
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

		Toolbar() {
			add(getFileMenu());
			add(getAgentMenu());
		}

		private JMenu getFileMenu() {
			JMenu menu = new JMenu("File");
			JMenuItem show = new JMenuItem("Show agentwindow");

			menu.add(show);
			menu.addSeparator();

			return menu;
		}

		private JMenu getAgentMenu() {
			JMenu menu = new JMenu("Agent");
			JMenuItem itemOne = new JMenuItem("Show only agent one");

			menu.add(itemOne);
			return menu;
		}
	}
}
