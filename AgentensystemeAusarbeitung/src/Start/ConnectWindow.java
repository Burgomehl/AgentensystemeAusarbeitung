package Start;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ConnectWindow extends JFrame {

	public ConnectWindow() {
		initComponents();
		setVisible(true);
	}

	private void initComponents() {
		setLayout(new GridLayout(0, 1));
		JPanel panelO = new JPanel(new GridLayout(0, 1));
		JPanel panelU = new JPanel(new GridLayout(0, 2));
		JLabel text = new JLabel("Please enter the ip-address of the world to connect");
		JTextField inputIP = new JTextField();
		JButton confirm = new JButton("OK");
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		panelO.add(text);
		panelO.add(inputIP);
		panelU.add(confirm);
		panelU.add(cancel);
		add(panelO);
		add(panelU);
		pack();
	}

	public static void main(String[] args) {
		new ConnectWindow();
	}

}
