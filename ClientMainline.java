/*
 @author: Martin, 51444972
 @version: 1.0.1

*/


package mud.cs3524.solutions.mud;

import java.rmi.Naming;
import java.lang.SecurityManager;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/* the usual way to connect to server, as we learned on the practicals, user will be asked
to enter name, and pick a game world, there are two predefined, Hell and Heaven, or the user 
can create his own on runtime, then there is a 'infinite' while loop, where the code waits
for input from user, that can be either pick a thing, drop a  thing, go 'somewhere', messageall, or message 'someuser'
or user can exit by typing quit or exit.
*/


public class ClientMainline
{
    public static void main(String args[])
    {
	if (args.length < 3) {
	    System.err.println( "Usage:\njava ClientMainline <registryhost> <registryport> <callbackport>" ) ;
	    return;
	}

	try {
	    String hostname = args[0];
	    int registryport = Integer.parseInt( args[1] ) ;
	    int callbackport = Integer.parseInt( args[2] ) ;
	
	    System.setProperty( "java.security.policy", "rmishout.policy" ) ;
	    System.setSecurityManager( new SecurityManager() ) ;
	    //using rmishout security policy from first practical, grants all priviledges
	    System.out.println("What is your name?");
		Scanner pn= new Scanner(System.in);
		String pname= pn.nextLine();
		//enter name
        Client client = new Client(pname,null);
        //creates client
	    ClientInterface clientstub = (ClientInterface)UnicastRemoteObject.exportObject( client, callbackport );
	    //creates stub
	    String regURL = "rmi://" + hostname + ":" + registryport + "/GameServerMainline";
	    GameServerInterface gamestub = (GameServerInterface)Naming.lookup( regURL );
		//local host
		List<String> worlds = gamestub.getWorlds();
		for(String world : worlds) {
			System.out.println(world);
		}//list of worlds on the server

		Boolean customWorldsAllowed = worlds.size() < 3;
		if (customWorldsAllowed == true) {
			System.out.println("Custom");
		} 	else {
			System.out.println("The world server is currently at maximum capacity");
		}//3 custom worlds are max allowed


		String world = null;
		while(world == null) {
			world = System.console().readLine("Choose World: ").trim();
			world = world.substring(0, 1).toUpperCase() + world.substring(1); 
			if (world.equals("Custom") && customWorldsAllowed == true) {
				world = gamestub.createWorld(buildWorld());
		}	else if (!worlds.contains(world)) {	
			world = null;
			System.out.println("That is not a valid world. Try again");
			}
		}//promt to choose a world
		System.out.println("Joining: " + world);
		
		if (gamestub.getPlayers() > 2){
			System.out.println("Maximum number of players reached");
			TimeUnit.SECONDS.sleep(1);
			System.exit(0);
		}//terminates if more than 3 players joined
		//System.out.println(gamestub.getPlayers());

		gamestub.spawn(clientstub, world); //connects client to a world of his choice

		String input = "";
		Boolean update = true;
		while(true){
			input = System.console().readLine();
			if ((input.contains("exit")) || (input.contains("quit"))){
				update = gamestub.removePlayer(clientstub);
				break;

			} //input from keyboard
			if (input.contains("pick")) {
				input = input.replace("pick ", "").trim(); //replaces pick with '' and trims the spaces => gets just the input ei. 'pen' insted of drop pen.
				update = gamestub.pick(clientstub, input);
			} else if (input.contains("drop")) {
				input = input.replace("drop ", "").trim();
				update = gamestub.drop(clientstub, input);
			} else if (input.contains("messageall")) {
				input = input.replace("messageall ", "").trim();
				update = gamestub.messaging(clientstub, input);
			} else if (input.contains("message")) {
				input = input.replace("message ", "").trim();
				String who = input.split(" ")[0].trim();
				String text = input.replace(who, "").trim();
				update = gamestub.messagingsomeone(clientstub, who, text);
			}else if (input.contains("go")) {
				input = input.replace("go ", "").trim();
				update = gamestub.move(clientstub, input);
			}else if (input.contains("?")) {
				System.out.println(" go <somewhere>\n pick <thing>\n drop <thing>\n messageall <textOFmessage>\n message <player> <text> ");
			}else {

				System.out.println(" wrong command\n for help try '?' ");
			}
		}
		System.out.println("exiting...");
		System.exit(0);
		} 
		catch (Exception e) { System.err.println(e.getMessage()); }
		
	}	
	
	
    //world creation functions
    private static List<String> buildWorld() {
    System.out.println("Welcome to World Creator!");
    System.out.println("Give your world a name like this:");
    System.out.println("name: <examplename>");
    System.out.println("Add links between places like this:");
    System.out.println("edge: A <something> B action description");
    System.out.println("Give places names like this:");
    System.out.println("message: A <name>");
    System.out.println("Place starting objects like this:");
    System.out.println("thing: <nameofthething>");
    System.out.println("Set a starting location like this:");
    System.out.println("start: <A,B,C,D>");
    System.out.println("When you're done type exit");
    System.out.println("Remember:You must complete all the attributes");

    String input = "";
    List<String> markup = new ArrayList<String>();
    while((!input.equals("exit"))
        || (markupContains(markup, "name: ").equals(false))
        || (markupContains(markup, "start: ").equals(false))
        || (markupContains(markup, "edge: ").equals(false))
        || (markupContains(markup, "message: ").equals(false))) {
      input = System.console().readLine(">").trim();
      markup.add(input);
    }
    System.out.println("done building");
    return markup;
  }

  public static Boolean markupContains(List<String> markup, String subString) {
    Boolean result = false;
    for(String statement : markup) {
      if (statement.contains(subString)) { result = true; }
    }
    return result;
  }
}