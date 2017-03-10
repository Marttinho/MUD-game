
package mud.cs3524.solutions.mud;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/*implementation of all the functions needed on the serverside such as status, which gives back
information about user location, and what is around him, the move function, the drop function, the spawn,
and both the messaging functions
*/

public class GameServerImplementation implements GameServerInterface {
  public Map<String, World> worlds;
  List<ClientInterface> clients;

  public GameServerImplementation() {
    clients = new ArrayList<ClientInterface>();
    worlds = new HashMap<String, World>();

    worlds.put("Hell", new World("hell.world"));
    worlds.put("Heaven", new World("heaven.world"));
  }

  public List<String> getWorlds() {
    return new ArrayList<String>(worlds.keySet());
  }

  public String createWorld(List<String> markup) {
    String name = "";
    if (worlds.size() < 3) {
      World world = new World(markup);
      worlds.put(world._name, world);
      name = world._name;
    }
    return name;
  }

  public void exportWorlds() {
    System.out.println("Exporting worlds...");
    for(World world : worlds.values()) {
      world.export();
    }
  }

	public void status(ClientInterface client, String message) {
    try {
     
		  String status = "World: " + client.getWorld() + "\n";
		  status += worlds.get(client.getWorld()).locationStatus(client.getLocation());
		  List<String> things = new ArrayList<String>(worlds.get(client.getWorld()).locationThings(client.getLocation()));
		  things.remove(client.getName());

		  String strThings = "";
		  String strPlayers = "";
		  String strBag = "";

		  for(String thing : things) {
		    if (isClient(thing).equals(true)) {
		      strPlayers += thing + " ";
		    } else {
		      strThings += thing + " ";
		    }
		  }

		  for(String thing : client.getThings()) {
		    strBag += thing + " ";
		  }

		  if (!strThings.equals("")) {
		    status += "Objects:" + " " + strThings + "\n";
		  }

		  if (!strPlayers.equals("")) {
		    status += "Players:" + " " + strPlayers + "\n";
		  }

		  if (!strBag.equals("")) {
		    status += "Bag:" + " " + strBag + "\n";
		  }

		  if (!message.equals("")) {
		    status += "Message:" + " " + message + "\n";
		  }
      
      client.print(status);
    } catch (Exception e) { System.out.println("status: " + e); }
  	}//the function for the information about player, gathers everything from the players, if there is some
      //one else at this location it lets you know, send the status string for print on the client side
 
	public Boolean spawn(ClientInterface client, String world) {
    try {
      clients.add(client);
      client.setWorld(world);
      client.setLocation(worlds.get(client.getWorld()).startLocation());
      worlds.get(client.getWorld()).addThing(worlds.get(client.getWorld()).startLocation(), client.getName());
      updatePlayers(worlds.get(client.getWorld()).startLocation(), client.getWorld(), "");
      System.out.println(client.getName() + " is in " + client.getWorld() + " world... ");
    } catch (Exception e) { System.out.println("spawn: " + e); return false; }
    return true;
  	}//function to create a player in the world and updates the number of players there

  public Boolean pick(ClientInterface client, String thing) {
    Boolean response  = true;
    try {
      if (isClient(thing).equals(false) && worlds.get(client.getWorld()).locationThings(client.getLocation()).contains(thing)) {
        worlds.get(client.getWorld()).delThing(client.getLocation(), thing);
        client.carry(thing);
        updatePlayers(client.getLocation(), client.getWorld(), "");
      } else {
        status(client, "Invalid Item Selection");
        response = false;
      }
    } catch (Exception e) { System.out.println("pick: " + e); return false; }
    return response;
  }//to pick up a thing at the location

  public Boolean drop(ClientInterface client, String thing) {
    Boolean response  = true;
    try {
      Boolean has = client.drop(thing);
      if (has.equals(true)) {
        worlds.get(client.getWorld()).addThing(client.getLocation(), thing);
        updatePlayers(client.getLocation(), client.getWorld(), "");
      } else {
        status(client, ("You do not have a " + thing + " to drop."));
        response = false;
      }
    } catch (Exception e) { System.out.println("drop: " + e); return false; }
    return response;
  }//to drop a thing

  public Boolean messaging (ClientInterface client, String text){
    try{
      for (ClientInterface dude : clients){
      dude.printmess("Global Chat:" + " " + client.getName() + " said: " + text);
      //System.out.println(text);
    }
    }//sends message to global chat

    catch (Exception e) { System.err.println(e.getMessage()); return false; }
    return true;
  }

  public Boolean messagingsomeone (ClientInterface player, String who, String text){
    try{
      for (ClientInterface client : clients)  {
          if (client.getName().equals(who)) {
            client.printmess("Private Chat:" + " " + player.getName() + " said: " + text);
            return true;
          }
      }
    }
    catch (Exception e) { System.err.println(e.getMessage()); return false; }
    return true;
  }//sends message to a single player

	public Boolean move(ClientInterface client, String dir) {
    try{
      String endpoint = worlds.get(client.getWorld()).moveThing(client.getLocation(), dir, client.getName());
      //updatePlayers(client.getLocation(), client.getWorld(), "");
      client.setLocation(endpoint);
      updatePlayers(endpoint, client.getWorld(), "");
    } catch (Exception e) { System.out.println("pick: " + e); return false; }
    return true;
  	}//moves player from position to position
  		
  private void updatePlayers(String location, String world, String message) {
    try {
      for(ClientInterface client : clients)  {
        if (client.getLocation().equals(location) && client.getWorld().equals(world)) {
          status(client, message);
         
        }
      }
    } catch (Exception e) { System.out.println("updatePlayers: " + e); }
  	}

  private Boolean isClient(String thing) {
    try {
      for(ClientInterface client : clients)  {
        if (client.getName().equals(thing)) {
          return true;
        }
      }
    } catch (Exception e) { System.out.println("isPlayer: " + e); }
    return false;
  	}//checks if an object is a player

  public Boolean removePlayer(ClientInterface player) {
    try {
      String pname = player.getName();
      String pWorld = player.getWorld();
      clients.remove(player);
      System.out.println(pname + " left " + pWorld);
      for (ClientInterface dude : clients){
      dude.printmess(pname + " has left the game");
      //System.out.println(text);
      }
      
    } 
    catch (Exception e) { System.out.println("removePlayer: " + e); return false; }
    
    return false;
  }



}