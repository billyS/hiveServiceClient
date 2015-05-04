package uk.co.billy.hive.doorlockservice.impl;

import java.io.IOException;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import uk.co.billy.hive.doorlockservice.api.DoorLockServiceAPI;

public class ClientDoorlockGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JTextField enterField; 					
	private JTextArea displayArea; 						
	private JButton sendCommand;										
	private Socket connection; 							
	private Image lock;
	private Image unlock = null;
	private boolean isLocked = true;
	DoorLockServiceAPI service;
	// set up GUI
	public ClientDoorlockGUI(final DoorLockServiceAPI service) {
		super("TCP ClientDoorlockGUI");
		
		this.service = service;
		System.out.println("From Client "+service.getClass());
    	System.out.println(service.getClass().getName());
    	System.out.println(service.getClass().getComponentType());
		sendCommand = new JButton();		
		sendCommand.setText("Lock");
		sendCommand.setBounds(200, 200, 80, 40);
		
		sendCommand.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent event) {
				//send lock/unlock command to client
				//service.startService();
				//service.sendCommand("LOW");
				//displayMessage("\n" + "sent lock command to doorlock" );
				if(isLocked && service!=null) {
					service.sendCommand("LOW");
					//sendCommand.setIcon(new ImageIcon(unlock));
					isLocked = false;
					displayMessage("\n" + "sent lock command to doorlock" );
				}else if(!isLocked && service!=null){
					service.sendCommand("HI");
					//sendCommand.setIcon(new ImageIcon(lock));
					isLocked = true;
					displayMessage("\n" + "sent unlock command to doorlock" );
				}	
			}
		});
		
		add(sendCommand); 
		
		enterField = new JTextField(); 
		enterField.setEditable(false);
		enterField.addActionListener(new ActionListener() {

			// FIXME remove this testing only!!!!
			// send message to client
			public void actionPerformed(ActionEvent event) {
				//service.sendCommand(event.getActionCommand());
				enterField.setText("");
			}
		});

		add(enterField, BorderLayout.NORTH);

		displayArea = new JTextArea(); // create displayArea
		add(new JScrollPane(displayArea), BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 500); // set size of window
		setVisible(true); // show window
	}


	private void displayMessage(final String messageToDisplay) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() // updates displayArea
			{
				displayArea.append(messageToDisplay); // append message
			}
		});
	}

	private void setTextFieldEditable(final boolean editable) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() // sets enterField's editability
			{
				enterField.setEditable(editable);
			}
		});
	}
}
