import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;


public class TicTacToeServerUDP {

	int portNumber, clientOnePort, clienTwoPort, currentPlayerPort, current;
	String hostName = "localhost", tokenOne, tokenTwo, myCharOne, myCharTwo; 
	String[] boardMap;
	String IPAddress;
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

		boolean won = false;
		
		/*
		 * check at what location has the player clicked on the grid.
		 * 
		 * Them depending upon the location, scan the blocks besides it
		 * to check whether the player has won or not.
		 */
		
		if(current == 0){
			if( (boardMap[1].equals(character) && boardMap[2].equals(character)) 
					|| (boardMap[3].equals(character) && boardMap[6]
							.equals(character))
					|| (boardMap[4].equals(character) && boardMap[8]
							.equals(character))){

				won = true;
			}
		}else if(current == 1){
			if( (boardMap[0].equals(character) && boardMap[2].equals(character)) 
					|| (boardMap[4].equals(character) && boardMap[7]
							.equals(character))){

				won = true;
			}
		}else if(current == 2){
			if( (boardMap[0].equals(character) && boardMap[1].equals(character)) 
					|| (boardMap[5].equals(character) && boardMap[8]
							.equals(character))
					|| (boardMap[4].equals(character) && boardMap[6]
							.equals(character))){

				won = true;
			}
		}else if(current == 3){
			if( (boardMap[0].equals(character) && boardMap[6].equals(character)) 
					|| (boardMap[4].equals(character) && boardMap[5]
							.equals(character))){

				won = true;
			}
		}else if(current == 4){
			if( (boardMap[3].equals(character) && boardMap[5].equals(character)) 
					|| (boardMap[1].equals(character) && boardMap[7]
							.equals(character))
					|| (boardMap[0].equals(character) && boardMap[8]
							.equals(character))
					|| (boardMap[2].equals(character) && boardMap[6]
							.equals(character))){

				won = true;
			}
		}else if(current == 5){
			if( (boardMap[3].equals(character) && boardMap[4].equals(character)) 
					|| (boardMap[2].equals(character) && boardMap[8]
							.equals(character))){

				won = true;
			}
		}else if(current == 6){
			if( (boardMap[0].equals(character) && boardMap[3].equals(character)) 
					|| (boardMap[7].equals(character) && boardMap[8]
							.equals(character))
					|| (boardMap[2].equals(character) && boardMap[4]
							.equals(character))){

				won = true;
			}
		}else if(current == 7){
			if( (boardMap[6].equals(character) && boardMap[8].equals(character)) 
					|| (boardMap[1].equals(character) && boardMap[4]
							.equals(character))){

				won = true;
			}
		}else if(current == 8){
			if( (boardMap[6].equals(character) && boardMap[7].equals(character)) 
					|| (boardMap[2].equals(character) && boardMap[5]
							.equals(character))
					|| (boardMap[0].equals(character) && boardMap[4]
							.equals(character))){

				won = true;
			}
		}

		return won;
	}

	class ServerHandler extends Thread{

		int port;
		InetAddress IPAddress;
		DatagramSocket serverSocket;
		DatagramPacket receivePacket;
		DatagramPacket sendPacket ;
		String query;
		String inputLine, outputLine, input[];
		byte[] sendData  = new byte[1024];

		public ServerHandler(InetAddress IPAddress, int port
				, DatagramSocket serverSocket, DatagramPacket receivePacket
				, String query){

			this.port = port;
			this.IPAddress = IPAddress;
			this.serverSocket = serverSocket;
			this.receivePacket = receivePacket;
			this.query = query;
		}
		
		/**
		 * 
		 * @param	IPAddress			the host on which the server will be
		 * 								running
		 * 
		 * @param	serverSocket		the Datagram socket on which the
		 * 								server will send and receive packets
		 *  
		 * @param	receivePacket		the datagram packet, which will 
		 * 								contain the data packed into it
		 * 
		 * @param	query				the string which is unpacked from
		 * 								the packet received
		 *  
		 * @param	inputLine			the string data which holds the data
		 * 								that
		 * 								was received from the client
		 * 
		 * @param	input				the array which splits the input data 
		 * 								based
		 * 								upon the spacing between the strings
		 * 
		 */
		
		public void run(){

			try{

				// split the input String
				input = query.split("\\s");
				System.out.println(input[0]);
				if(input[0].equals("doesNotHaveToken")){

					// get the byte data of the string required
					sendData = ("Wait for your turn").getBytes(); 

					// create a packet with the data
					sendPacket = 
							new DatagramPacket(sendData, sendData.length
									, IPAddress, 
									port); 

					// send the packet
					serverSocket.send(sendPacket); 

					// get the byte data of the string required
					sendData = ("done").getBytes(); 

					// create a packet with the data
					sendPacket = 
							new DatagramPacket(sendData, sendData.length
									, IPAddress, 
									port); 

					// send the packet
					serverSocket.send(sendPacket); 

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
					sendData = ("doesNotHaveToken").getBytes(); 

					sendPacket = 
							new DatagramPacket(sendData, sendData.length, IPAddress, 
									port); 

					serverSocket.send(sendPacket); 

					if(won){

						/*
						 * if the player has won, then send it a similar
						 * message
						 */
						sendData = ("You Win").getBytes(); 

						sendPacket = 
								new DatagramPacket(sendData, sendData.length, IPAddress, 
										port); 

						serverSocket.send(sendPacket); 
					}
					// indicate end of commands
					sendData = ("done").getBytes(); 

					sendPacket = 
							new DatagramPacket(sendData, sendData.length, IPAddress, 
									port); 

					serverSocket.send(sendPacket);

					/*
					 * select the port of the other player to let it play
					 * the game
					 */
					if(currentPlayerPort == clientOnePort){
						currentPlayerPort = clienTwoPort;
					}else{
						currentPlayerPort = clientOnePort;
					}

					String serverHostname = new String (hostName);

					DatagramSocket send = new DatagramSocket(); 

					InetAddress IPAddress = InetAddress.getByName(serverHostname); 

					byte[] sendData = new byte[1024]; 
					byte[] receiveData = new byte[1024]; 

					if(won){

						/*
						 * send the received data to the other player, which
						 * now has become the current player
						 */
						sendData = ("YouLose" + " " + input[1] + " " + input[2]).getBytes();

						DatagramPacket sendPacket = 
								new DatagramPacket(sendData, sendData.length, IPAddress, currentPlayerPort); 

						send.send(sendPacket); 

					}else{
						/*
						 * send the received data to the other player, which
						 * now has become the current player
						 */
						sendData = ("hasToken" + " " + input[1] + " " + input[2]).getBytes();

						DatagramPacket sendPacket = 
								new DatagramPacket(sendData, sendData.length, IPAddress, currentPlayerPort); 

						send.send(sendPacket); 
					}
				}

			}catch(Exception e){

			}

		}
	}

	class Server extends Thread{

		int portNumber;
		DatagramSocket serverSocket;
		DatagramPacket receivePacket;

		public Server(int portNumber) {
			this.portNumber = portNumber;
		}
		
		/**
		 * @param	portNumber		the port on which the server will be
		 * 							listening to connections
		 * 
		 * @param	serverSocket	to open a socket on the server port
		 * 
		 * @param	receivePacket	to receive the packets from the client
		 * 
		 * 
		 */

		public void run() 
		{
			try{
				
				/*
				 * start listening to connection on the assigned port number
				 */
				serverSocket = new DatagramSocket(portNumber); 

				byte[] receiveData = new byte[1024]; 
				byte[] sendData  = new byte[1024]; 
				receivePacket = new DatagramPacket(receiveData
						, receiveData.length); 

				System.out.println("TicTacToe UDP Server listening on port: " 
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
					
					serverSocket.receive(receivePacket); 

					String query = new String(receivePacket.getData(), 0
							, receivePacket.getLength());

					InetAddress IPAddress = receivePacket.getAddress(); 
					int port = receivePacket.getPort();

					ServerHandler handler = new ServerHandler(IPAddress, port
							, serverSocket, receivePacket, query);
					handler.start();

				}

			}catch(Exception e){
				System.out.println("Something went wrong.");
			}			
		}

	}

	public TicTacToeServerUDP(int portNumber, String IPAddress){

		this.portNumber = portNumber;
		clientOnePort = portNumber + 1;
		clienTwoPort = portNumber - 1;
		
		this.IPAddress = IPAddress;
		
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

	public void startServer() throws InterruptedException
	, UnknownHostException {

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
		
		String serverHostname = new String ("localhost");
		InetAddress IPAddress = InetAddress.getByName(serverHostname); 
		// start the first player client
		TicTacToeClientUDP clientObjectOne = new TicTacToeClientUDP(1
				, portNumber, clientOnePort, myCharOne, IPAddress);
		clientObjectOne.startClient();
		// start the second player client
		TicTacToeClientUDP clientObjectTwo = new TicTacToeClientUDP(2
				, portNumber, clienTwoPort, myCharTwo, IPAddress);
		clientObjectTwo.startClient();

		try{
			// get the iput as well as output streams of the client
			DatagramSocket clientSocket = new DatagramSocket(); 
			byte[] sendData = new byte[1024]; 
			byte[] receiveData = new byte[1024]; 


			/*
			 * send a command with the string "Start" in it to let the
			 * clients know that the game can begin
			 */
			sendData = ("Start" + " " + tokenOne).getBytes();

			DatagramPacket sendPacket = 
					new DatagramPacket(sendData, sendData.length, IPAddress
							, clientOnePort); 

			clientSocket.send(sendPacket); 

			
			clientSocket = new DatagramSocket(); 
			sendData = new byte[1024]; 
			receiveData = new byte[1024]; 


			/*
			 * send a command with the string "Start" in it to let the
			 * clients know that the game can begin
			 */
			sendData = ("Start" + " " + tokenTwo).getBytes();

			sendPacket = 
					new DatagramPacket(sendData, sendData.length, IPAddress
							, clienTwoPort); 

			clientSocket.send(sendPacket);

		}catch(Exception e){

		}
	}
}
