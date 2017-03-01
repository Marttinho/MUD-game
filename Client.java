
package mud.cs3524.solutions.mud;

import java.util.List;
import java.util.ArrayList;

public class Client implements ClientInterface {
	private String name;
  private String location;
  private String world;
  private List<String> things = new ArrayList<String>();
	
  public Client(String pname, String startingLocation) {
    	name = pname;
    	location = startingLocation;
  }


  public String getName() {
    	return name;
  }



	public String getLocation() {
    	return location;
  	}


  public String setLocation(String newLocation) {
    	return location = newLocation;
  }

  public void print(String output){
    
    System.out.print(output);
    
  }
  public String getWorld() {
    return world;
  }

  public String setWorld(String newWorld) {
    return world = newWorld;
  }

  public List<String> getThings() {
    return things;
  }

  public void carry(String thing) {
    things.add(thing);
  }

  public Boolean drop(String thing) {
    if (things.contains(thing)) {
      things.remove(thing);
      return true;
    } else { return false; }
  }
}