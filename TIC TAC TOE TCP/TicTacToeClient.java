import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class TicTacToeClient {

	int id, portNumber, myPort;
	String hostName = "localhost", token, myText;
	boolean clientListenerRunning = false;
	Socket currentPlayer;
	PrintWriter currentPlayerOut;
	BufferedReader currentPlayerIn;
	String inputLine, outputLine, input[];

	JLabel playerName;
	JLabel mark;
	JLabel message;
	JButton fieldButtons[]= new JButton[9];

	class Board{

		/**
		 * @param	pane		the JPanel component which will hold all of 
		 * 						the internal components
		 * 
		 * @param	playerName	the label which shows the id of the player
		 * 
		 * @param	mark		the label which shows the mark that the player 
		 * 						can put in the grids
		 * 
		 * @param	fieldButtons	the buttons which the player can click
		 * 							to register its move on a particular block
		 * 
		 * @param	currentPlayer	the socket connection on which the client
		 * 							will send the commands to server
		 * 
		 * @param	currentPlayerOut	the output stream on which commands or			
		 * 								data can be sent
		 * 
		 * @param	currentPlayerIn		the input stream on which commands or
		 * 								data can be sent
		 * 
		 * @param	message		the label which will display notification 
		 * 						messages to the player
		 * 
		 */
		public Component createComponents() {

			/*
			 * create a pane in which all the components will be added, which 
			 * will
			 * further have components in it
			 */
			JPanel pane = new JPanel();

			// set the size of the layout of components (width, height)
			pane.setPreferredSize(new Dimension(550, 550));
			pane.setBorder(BorderFactory.createEmptyBorder(
					50, //top
					50, //left
					50, //bottom
					50) //right
					);


			// create a grid layout with 5 rows and 1 column
			pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

			playerName = new JLabel();
			playerName.setPreferredSize(new Dimension(20, 20));
			playerName.setBorder(BorderFactory.createEmptyBorder(
					0, //top
					0, //left
					0, //bottom
					0) //right
					);
			playerName.setText("You are Player " + id);
			playerName.setHorizontalAlignment(JLabel.CENTER);

			mark = new JLabel();
			mark.setPreferredSize(new Dimension(20, 20));
			mark.setBorder(BorderFactory.createEmptyBorder(
					0, //top
					0, //left
					0, //bottom
					0) //right
					);
			mark.setText("You have got: " + myText);
			mark.setHorizontalAlignment(JLabel.CENTER);
			
			JPanel grid = new JPanel();
			grid.setLayout(new GridLayout(3, 3));

			
			for(int i = 0; i < 9; i++){
				fieldButtons[i] = new JButton();
			}
			for(int i = 0; i < 9; i++){

				// set the text in the button to "Convert"
				fieldButtons[i].setText("");
				fieldButtons[i].setName(Integer.toString(i + 1));
				//buttons[i].setName(myText);
				fieldButtons[i].setHorizontalAlignment(JLabel.CENTER);
				final int index = i;
				// add an action listener to the button to call the convert() 
				// method
				fieldButtons[i].addActionListener(
						new ActionListener() {
							public void actionPerformed(ActionEvent e) {

								try{
									/*
									 * get the input as well as output streams
									 */
									currentPlayer = new Socket(hostName, 
											portNumber);
									currentPlayerOut = new PrintWriter
											(currentPlayer.getOutputStream(), 
													true);
									currentPlayerIn = new BufferedReader(
											new InputStreamReader(currentPlayer
													.getInputStream()));
									
									/*
									 * send the command which indicates
									 * the button pressed and the
									 * character entered by the player
									 */
									currentPlayerOut.println(token + " " 
									+ fieldButtons[index].getName() 
									+ " " + myText);

									/*
									 * read the input received from the server
									 */
									while ((inputLine = currentPlayerIn.
											readLine()) != null) {

										/*
										 * wait if the server asks
										 * to wait
										 */
										if(inputLine.equals("Wait for "
												+ "your turn")){

											message.setText(inputLine);

										}else if(inputLine.
												equals("doesNotHaveToken")){

											token = "doesNotHaveToken";
											message.setText("Now its the other "
													+ "players turn to play");
											
											/*
											 * update the button fields
											 * 
											 */
											fieldButtons[index].setName(myText);
											fieldButtons[index].setText(myText);
											fieldButtons[index].
											setEnabled(false);
										}
										
										if(inputLine.equals("You Win")){
											message.setText("You Win");
											
											for(int i = 0; i < 9; i++){
												fieldButtons[i].
												setEnabled(false);
											}
											
											/*
											 * if the player has won,
											 * disable all the buttons so
											 * that the player cannot further
											 * play, and release
											 * resources
											 */
											currentPlayer.close();
											currentPlayerOut.close();
											currentPlayerIn.close();
										}
										if (inputLine.equals("done")){
											break;
										}

									}
									
								}catch(Exception e1){

								}
							}
						}
						);
				
				grid.add(fieldButtons[i]);
			}

			message = new JLabel();
			message.setPreferredSize(new Dimension(20, 20));
			message.setBorder(BorderFactory.createEmptyBorder(
					0, //top
					0, //left
					0, //bottom
					0) //right
					);
			
			message.setHorizontalAlignment(JLabel.CENTER);

			// add all the components to the pane
			pane.add(playerName);
			pane.add(mark);
			pane.add(grid);
			pane.add(message);

			return pane;
		}
	}

	class ClientHandler extends Thread{

		Socket clientSocket;
		Socket currentPlayer;
		PrintWriter currentPlayerOut;
		BufferedReader currentPlayerIn;
		String inputLine, outputLine;

		public ClientHandler(Socket clientSocket){
			this.clientSocket = clientSocket;
		}

		/**
		 * @param	clientSocket		the socket on which the client is
		 * 								listening to connections
		 * 
		 * @param	currentPlayerOut	the output stream on which the clients
		 * 								server can send data
		 * 
		 * @param	currentPlayerIn		the input stream on which the clients
		 * 								server can receive data
		 * 
		 * @param	inputLine			the string that is received from the
		 * 								client
		 * 
		 * @param	input				the input that was split based upon the
		 * 								spacing is stored in an array
		 */
		public void run(){

			try{

				System.out.println("Request received for TicTacToe "
						+ "Client Handler.");
				
				// get the input and output streams
				PrintWriter out =
						new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));

				// read the input
				inputLine = in.readLine();

				// split the input into the array based upon the spacing
				input = inputLine.split("\\s");

				if(input[0].equals("hasToken")){

					/*
					 * set the button text to the character assigned to the
					 * player
					 */
					fieldButtons[Integer.parseInt(input[1]) - 1].
					setText(input[2]);
					
					// disable this button which was clicked
					fieldButtons[Integer.parseInt(input[1]) - 1].
					setEnabled(false);
					message.setText("Its your turn to play");

					// update the token of the player
					token = input[0];

				}else if(input[0].equals("YouLose")){
					
					/*
					 * if the player loses, then update the label field and
					 * and the button text
					 */
					fieldButtons[Integer.parseInt(input[1]) - 1]
							.setText(input[2]);
					message.setText("You Lose");
					
					for(int i = 0; i < 9; i++){
						
						/*
						 *  disable all the buttons so that the player
						 *  cannot playe further as the game has ended
						 */
						
						fieldButtons[i].setEnabled(false);
					}
					
				}else if(input[0].equals("Start")){
					
					/*
					 * indicates that the server is running and the game
					 * can begin
					 */
					if(input[1].equals("hasToken")){
						message.setText("Its your turn to play");
						
					}else{
						message.setText("The game has started");						
					}
					
					// update the token of the player assigned to it
					token = input[1];
				}

			}catch(Exception e){
				System.out.println("Something went wrong.");

			}
		}
	}
	class ClientListener extends Thread{

		int myPort;
		ServerSocket serverSocket = null;
		Socket clientSocket = null;

		public ClientListener(int myPort){
			this.myPort = myPort;
		}

		/**
		 * @param	myPort			the port on which the player client will
		 * 							be listening to connections
		 * 
		 * @param	serverSocket	the socket on which the server can send
		 * 							commands
		 * 
		 */
		public void run(){

			try{
				
				/*
				 * start listening to the connections on the client port
				 */
				serverSocket = new ServerSocket(myPort);	
				System.out.println("TicTacToe Client listening on port: " 
				+ myPort);
				

			} catch (Exception e) {
				System.out.println("Could not listen on port " + myPort);
				System.exit(-1);
			}
			try{

				while(true){

					/*
					 * when a connection is received, transfer the request to
					 * the client handler thread
					 */
					ClientHandler handler = new ClientHandler(serverSocket
							.accept());
					handler.start();

				}

			}catch(Exception e){
				System.out.println("Something went wrong.");
			}

		}
	}

	public TicTacToeClient(int id, int portNumber, int myPort, String myText) {
		this.myText = myText;
		this.id = id;
		this.portNumber = portNumber;
		this.myPort = myPort;
	}

	public void drawBoard(){

		/*
		 * a JFrame object which will be used to display components
		 * into it
		 */
		JFrame frame = new JFrame("Converter");

		/*
		 * the object of the Converter class which has the
		 * methods which create components like buttons,
		 * text boxes, labels and drop down menus
		 */
		Board app = new Board();
		Component contents = app.createComponents();
		frame.getContentPane().add(contents);

		//Finish setting up the frame, and show it.
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.pack();
		frame.setVisible(true);
	}

	public void startClient(){

		try{

			// start the client listener thread
			ClientListener cListener = new ClientListener(myPort);
			cListener.start();
			//cListener.join();
			
			// draw the grid layout board on which the user can play
			drawBoard();

		}catch(Exception e){

		}


	}
}
