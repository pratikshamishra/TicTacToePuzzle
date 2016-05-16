import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;


public class TicTacToeServer {

	int portNumber, clientOnePort, clienTwoPort, currentPlayerPort, current;
	String hostName = "localhost", tokenOne, tokenTwo, myCharOne, myCharTwo; 
	String[] boardMap;
	boolean won = false;

	/**
	 * 
	 * @param boardMap		the array representing the values, which has the 
	 * 						characters - X and O stored in it
	 * 
	 * @param current		the current location at which the character which 
	 * 						the current player has entered
	 * 
	 * @param character		the character which the current player has entered
	 * 
	 * @return won			the boolean value which represents whether the 
	 * 						player has won or not
	 */
	public boolean checkIfWon(String[] boardMap, int current, String character){

		/*
		 * check at what location has the player clicked on the grid.
		 * 
		 * Them depending upon the location, scan the blocks besides it
		 * to check whether the player has won or not.
		 */
		boolean won = false;
		if(current == 0){
			if( (boardMap[1].equals(character) && boardMap[2].equals(character)) 
					|| (boardMap[3].equals(character) && boardMap[6].
							equals(character))
					|| (boardMap[4].equals(character) && boardMap[8].
							equals(character))){

				won = true;
			}
		}else if(current == 1){
			if( (boardMap[0].equals(character) && boardMap[2].equals(character)) 
					|| (boardMap[4].equals(character) && boardMap[7].
							equals(character))){

				won = true;
			}
		}else if(current == 2){
			if( (boardMap[0].equals(character) && boardMap[1].equals(character)) 
					|| (boardMap[5].equals(character) && boardMap[8].
							equals(character))
					|| (boardMap[4].equals(character) && boardMap[6].
							equals(character))){

				won = true;
			}
		}else if(current == 3){
			if( (boardMap[0].equals(character) && boardMap[6].equals(character)) 
					|| (boardMap[4].equals(character) && boardMap[5].
							equals(character))){

				won = true;
			}
		}else if(current == 4){
			if( (boardMap[3].equals(character) && boardMap[5].equals(character)) 
					|| (boardMap[1].equals(character) && boardMap[7].
							equals(character))
					|| (boardMap[0].equals(character) && boardMap[8].
							equals(character))
					|| (boardMap[2].equals(character) && boardMap[6].
							equals(character))){

				won = true;
			}
		}else if(current == 5){
			if( (boardMap[3].equals(character) && boardMap[4].equals(character)) 
					|| (boardMap[2].equals(character) && boardMap[8].
							equals(character))){

				won = true;
			}
		}else if(current == 6){
			if( (boardMap[0].equals(character) && boardMap[3].equals(character)) 
					|| (boardMap[7].equals(character) && boardMap[8].
							equals(character))
					|| (boardMap[2].equals(character) && boardMap[4].
							equals(character))){

				won = true;
			}
		}else if(current == 7){
			if( (boardMap[6].equals(character) && boardMap[8].equals(character)) 
					|| (boardMap[1].equals(character) && boardMap[4].
							equals(character))){

				won = true;
			}
		}else if(current == 8){
			if( (boardMap[6].equals(character) && boardMap[7].equals(character)) 
					|| (boardMap[2].equals(character) && boardMap[5].
							equals(character))
					|| (boardMap[0].equals(character) && boardMap[4].
							equals(character))){

				won = true;
			}
		}

		return won;
	}

	class ServerHandler extends Thread{
		Socket clientSocket;
		Socket currentPlayer;
		PrintWriter currentPlayerOut;
		BufferedReader currentPlayerIn;
		String inputLine, outputLine, input[];


		public ServerHandler(Socket clientSocket){
			this.clientSocket = clientSocket;

		}

		/**
		 * 
		 * @param	out			the output stream of the server, on which the
		 * 						server can send data.
		 * 
		 * @param	in			the input stream on which the server can
		 *  					read the data sent from the client
		 *  
		 * @param	currentPlayerOut	the output stream of the server,on which
		 * 								the	server can send data.
		 * 
		 * @param	currentPlayerIn		the input stream on which the server can
		 *  							read the data sent from the client
		 *  
		 * @param	inputLine	the string data which holds the data that
		 * 						was received from the client
		 * 
		 * @param	input		the array which splits the input data based
		 * 						upon the spacing between the strings
		 * 
		 */
		public void run(){

			try{

				System.out.println("Request received from TicTacToe Client.");
				/*
				 * get the output as well as the input streams on which
				 * the server can send data over to client and receive from
				 */
				PrintWriter out =
						new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));

				// read the input into a string
				inputLine = in.readLine();

				// split the input data based upon the space characters
				input = inputLine.split("\\s");
				
				
				if(input[0].equals("doesNotHaveToken")){

					/*
					 * if the first field shows that the client does not have
					 * a token to play the game currently, then tell the client
					 * player that its not your turn to play and wait
					 */
					out.println("Wait for your turn");
					out.println("done");

				}else{

					/*
					 * if the player has the token to play the game, it means
					 * it clicked on one the blocks on the grid of the game
					 */
					
					// get the index of the grid
					current = (Integer.parseInt(input[1]) - 1);
					
					// get the character which was clicked
					String value = input[2];

					/*
					 *  store into the map to check later if player won the game
					 *  or not
					 */
					
					boardMap[current] = value;

					// check whether the current player won or not
					won = checkIfWon(boardMap, current, value);

					/*
					 * tell the current player that now it does not have the 
					 * token to play the game and has to therefore wait
					 */
					out.println("doesNotHaveToken");
					if(won){
						
						/*
						 * if the player has won, then send it a similar
						 * message
						 */
						out.println("You Win");
					}
					// indicate end of commands
					out.println("done");

					/*
					 * select the port of the other player to let it play
					 * the game
					 */
					if(currentPlayerPort == clientOnePort){
						currentPlayerPort = clienTwoPort;
					}else{
						currentPlayerPort = clientOnePort;
					}

					currentPlayer = new Socket(hostName, currentPlayerPort);
					currentPlayerOut = new PrintWriter(currentPlayer.
							getOutputStream(), true);
					currentPlayerIn = new BufferedReader(
							new InputStreamReader(currentPlayer.
									getInputStream()));

					/*
					 * send the received data to the other player, which
					 * now has become the current player
					 */
					if(won){
						currentPlayerOut.println("YouLose" + " " + input[1] 
								+ " " + input[2]);
					}else{
						currentPlayerOut.println("hasToken" + " " + input[1] 
								+ " " + input[2]);
					}
				}

			}catch(Exception e){
				System.out.println("Something went wrong.");
			}

		}
	}

	class Server extends Thread{

		int portNumber;
		ServerSocket serverSocket = null;
		Socket clientSocket = null;

		public Server(int portNumber) {
			this.portNumber = portNumber;
		}

		/**
		 * @param	portNumber		the port on which the server will be
		 * 							listening to connections
		 * 
		 * @param	serverSocket	to open a socket on the server port
		 * 
		 * @param	clientSocket	to bind onto the client port connection
		 * 
		 * 
		 */
		public void run() 
		{
			try{
				
				/*
				 * start listening to connection on the assigned port number
				 */
				serverSocket = new ServerSocket(portNumber);		
				System.out.println("TicTacToe Server listening on port: " 
				+ portNumber);
			} catch (Exception e) {
				System.out.println("Could not listen on port " + portNumber);
				System.exit(-1);
			}
			try{

				while(true){
					
					/*
					 * when a connection is received, accept it and then
					 * transfer it to the server handler to handle the request
					 */
					ServerHandler handler = new ServerHandler(serverSocket.
							accept());
					handler.start();

				}

			}catch(Exception e){
				System.out.println("Something went wrong.");
			}			
		}

	}

	
	public TicTacToeServer(int portNumber, String hostName){

		this.portNumber = portNumber;
		clientOnePort = portNumber + 1;
		clienTwoPort = portNumber - 1;
		this.hostName = hostName;
		boardMap = new String[9];

		for(int i = 0; i < 9; i++){
			boardMap[i] = "";
		}
	}
	
	/**
	 * @param	sThread		the object of the Server class to start the
	 * 						server thread
	 * 
	 * @param	randomizer	to randomly select the first player
	 * 
	 * @param	player		the player number indicating first or second player
	 * 
	 * @param	portNumber	the port on which the server will listening to
	 * 						connections
	 * 
	 * @param	clientOnePort	the port number of the first player
	 * 
	 * @param	clienTwoPort	the port number of the second player
	 * 
	 * @param	hostName	the host on which the server will be running
	 * 
	 * @param	boardMap	the map in which we will update the characters
	 * 						entered onto the grid
	 * 
	 * @throws InterruptedException to throw interrupted Exception when
	 * 								interrupted
	 */

	public void startServer() throws InterruptedException {

		Server sThread = new Server(portNumber);

		// start the server thread
		sThread.start();

		Thread.sleep(1000);

		// randomly select the first player amongst the two
		Random randomizer = new Random();
		int player = randomizer.nextInt(2);

		if(player == 0){

			myCharOne = "X";
			myCharTwo = "O";
			currentPlayerPort = clientOnePort;
			tokenOne = "hasToken";
			tokenTwo = "doesNotHaveToken";


		}else{
			myCharOne = "O";
			myCharTwo = "X";
			currentPlayerPort = clienTwoPort;
			tokenTwo = "hasToken";
			tokenOne = "doesNotHaveToken";

		}

		// start the first player client
		TicTacToeClient clientObjectOne = new TicTacToeClient(1, portNumber, 
				clientOnePort, myCharOne);
		clientObjectOne.startClient();

		// start the second player client
		TicTacToeClient clientObjectTwo = new TicTacToeClient(2, portNumber, 
				clienTwoPort, myCharTwo);
		clientObjectTwo.startClient();

		try{

			// get the iput as well as output streams of the client
			Socket tTTClientOne = new Socket(hostName, clientOnePort);
			PrintWriter outOne = new PrintWriter(tTTClientOne.
					getOutputStream(), true);
			BufferedReader inOne = new BufferedReader(
					new InputStreamReader(tTTClientOne.getInputStream()));

			/*
			 * send a command with the string "Start" in it to let the
			 * clients know that the game can begin
			 */
			outOne.println("Start" + " " + tokenOne);

			// get the iput as well as output streams of the client
			Socket tTTClientTwo = new Socket(hostName, clienTwoPort);
			PrintWriter outTwo = new PrintWriter(tTTClientTwo.
					getOutputStream(), true);
			BufferedReader inTwo = new BufferedReader(
					new InputStreamReader(tTTClientTwo.getInputStream()));

			/*
			 * send a command with the string "Start" in it to let the
			 * clients know that the game can begin
			 */
			
			outTwo.println("Start" + " " + tokenTwo);

		}catch(Exception e){

		}
	}
}
