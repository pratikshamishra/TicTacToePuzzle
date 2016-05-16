/** 
 * The program implements a turn based player game, called TicTacToe,
 * in which there is a central server running, and clients on which the 
 * players play.
 * 
 * Version:   $Id: TicTacToeTCP.java, v 1.0 2014/12/04 16:00:00 $
 * 
 * @author Harshad Paradkar
 * @author Pratiksha Mishra
 * 
 * Revisions: 
 *      
 */

/**
 * @param portNumber		the local port on which the game server will be 
 * 							running and listening to connections.
 * 
 * @param IPAddress			the host name on which the server machine will
 * 							be running.
 * 
 * @param ar				the argument vector
 *
 */
public class TicTacToeTCP {

	public static void main(String ar[]) throws InterruptedException{

		int portNumber;
		String IPAddress = "localhost";
		if(ar.length != 2){

			System.err.println("Usage : java TicTacToeTCP port_number hostname");
			System.exit(1);
		}
		portNumber = Integer.parseInt(ar[0]);
		IPAddress = ar[1];
		
		/*
		 * call the server method to start running for listening on its
		 * assigned port
		 */
		TicTacToeServer serverObject = new TicTacToeServer(portNumber, IPAddress);
		serverObject.startServer();

	}
}
