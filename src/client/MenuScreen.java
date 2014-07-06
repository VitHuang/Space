package client;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

public class MenuScreen extends JPanel implements ActionListener {

	private static final long serialVersionUID = 42L;

	private int selectedShip = 0;
	private static final int NUM_SHIPS = 2;
	private ImageIcon[] imageIcons;

	private JTextField nameField;
	private JButton changeShipButton;
	private JTextField hostField;
	private JSpinner portSpinner;
	private JButton startGameButton;
	private JFrame frame;

	public static void main(String[] args) {
		new MenuScreen();
	}

	public MenuScreen() {
		imageIcons = new ImageIcon[2];
		for (int i = 0; i < NUM_SHIPS; i++) {
			imageIcons[i] = new ImageIcon(
					ImageManager.getImage("resources/ship" + i + ".png"));
		}
		nameField = new JTextField(16);
		changeShipButton = new JButton();
		changeShipButton.setIcon(imageIcons[selectedShip]);
		changeShipButton.setActionCommand("change ship");
		changeShipButton.addActionListener(this);
		hostField = new JTextField(15);
		portSpinner = new JSpinner(new SpinnerNumberModel(20000, 0, 65535, 1));
		startGameButton = new JButton("Join game!");
		startGameButton.setActionCommand("start game");
		startGameButton.addActionListener(this);
		GridLayout layout = new GridLayout(0, 2);
		this.setLayout(layout);
		this.add(new JLabel("Nickname:"));
		this.add(nameField);
		this.add(new JLabel("Ship type:"));
		this.add(changeShipButton);
		this.add(new JLabel("Host:"));
		this.add(hostField);
		this.add(new JLabel("Server port:"));
		this.add(portSpinner);
		this.add(startGameButton);
		frame = new JFrame("En-masse-teroids");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("change ship".equals(e.getActionCommand())) {
			selectedShip++;
			if (selectedShip >= NUM_SHIPS) {
				selectedShip = 0;
			}
			changeShipButton.setIcon(imageIcons[selectedShip]);
		} else if ("start game".equals(e.getActionCommand())) {
			try {
				new SpaceGame(nameField.getText(), selectedShip,
						hostField.getText(),
						((SpinnerNumberModel) portSpinner.getModel())
								.getNumber().intValue());
				frame.setVisible(false);
			} catch (UnknownHostException e1) {
				System.out.println("Couldn't find host " + hostField.getText());
			} catch (IOException e1) {
				System.out.println("Failed to connect to server");
			}
		}
	}
}
