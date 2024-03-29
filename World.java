/*
 @author: Martin, 51444972
 @version: 1.0.1

*/



package mud.cs3524.solutions.mud;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Scanner;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.HashMap;
import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * A class that can be used to represent a MUD; essenially, this is a
 * graph.
 */

public class World
{
    /**
     * Private stuff
     */
    public String _name = "Generic World";
    // A record of all the vertices in the MUD graph. HashMaps are not
    // synchronized, but we don't really need this to be synchronised.
    private Map<String,Vertex> vertexMap = new HashMap<String,Vertex>();

    private String _startLocation = "";

    /**
     * Add a new edge to the graph.
     */
    private void addEdge( String sourceName, 
			  String destName, 
			  String direction, 
			  String view )
    {
        Vertex v = getOrCreateVertex( sourceName );
        Vertex w = getOrCreateVertex( destName );
        v._routes.put( direction, new Edge( w, view ) );
    }

    /**
     * Create a new thing at a location.
     */
    private void createThing( String loc, 
			      String thing )
    {
	Vertex v = getOrCreateVertex( loc );
	v._things.add( thing );
    }

    /**
     * Change the message associated with a location.
     */
    private void changeMessage( String loc, String msg )
    {
	Vertex v = getOrCreateVertex( loc );
	v._msg = msg;
    }

    /**
     * If vertexName is not present, add it to vertexMap.  In either
     * case, return the Vertex. Used only for creating the MUD.
     */
    private Vertex getOrCreateVertex( String vertexName )
    {
        Vertex v = vertexMap.get( vertexName );
        if (v == null) {
            v = new Vertex( vertexName );
            vertexMap.put( vertexName, v );
        }
        return v;
    }

    public String startLocation(){
        return _startLocation;
    }
    /**
     *
     */
    private Vertex getVertex( String vertexName )
    {
	return vertexMap.get( vertexName );
    }
    private void saveEdge(String[] attributes) {
        String message = "";
        for(String n: Arrays.copyOfRange(attributes, 4, attributes.length)) message += n + " ";
        addEdge(attributes[1], attributes[3], attributes[2], message.trim());
    }

    private void saveMessage(String[] attributes) {
        String message = "";
        for(String n: Arrays.copyOfRange(attributes, 2, attributes.length)) message += n + " ";
        changeMessage(attributes[1], message.trim());
    }

    private void saveThing(String[] attributes) {
        String message = "";
        for(String n: Arrays.copyOfRange(attributes, 2, attributes.length)) message += n + " ";
        addThing(attributes[1], message.trim());
    }
    private void loadRecord(String record) {
        String[] attributes = record.split(" ");
        if (attributes[0].equals("name:"))
            _name = attributes[1];
        if (attributes[0].equals("edge:"))
            saveEdge(attributes);
        if (attributes[0].equals("message:"))
            saveMessage(attributes);
        if (attributes[0].equals("thing:"))
            saveThing(attributes);
        if (attributes[0].equals("start:"))
            _startLocation = attributes[1];
    }
    /**
     * Creates the edges of the graph on the basis of a file with the
     * following fromat:
     * source direction destination message
     */
    private void createEdges( String edgesfile )
    {
	try {
	    FileReader fin = new FileReader( edgesfile );
            BufferedReader edges = new BufferedReader( fin );
            String line;
            while((line = edges.readLine()) != null) {
                StringTokenizer st = new StringTokenizer( line );
		if( st.countTokens( ) < 3 ) {
		    System.err.println( "Skipping ill-formatted line " + line );
		    continue;
		}
		String source = st.nextToken();
		String dir    = st.nextToken();
		String dest   = st.nextToken();
		String msg = "";
		while (st.hasMoreTokens()) {
		    msg = msg + st.nextToken() + " "; 
                }
		addEdge( source, dest, dir, msg );
	    }
	}
	catch( IOException e ) {
	    System.err.println( "Graph.createEdges( String " + 
				edgesfile + ")\n" + e.getMessage() );
	}
    }

    /**
     * Records the messages assocated with vertices in the graph on
     * the basis of a file with the following format:
     * location message
     * The first location is assumed to be the starting point for
     * users joining the MUD.
     */
    private void recordMessages( String messagesfile )
    {
	try {
	    FileReader fin = new FileReader( messagesfile );
            BufferedReader messages = new BufferedReader( fin );
            String line;
	    boolean first = true; // For recording the start location.
            while((line = messages.readLine()) != null) {
                StringTokenizer st = new StringTokenizer( line );
		if( st.countTokens( ) < 2 ) {
		    System.err.println( "Skipping ill-formatted line " + line );
		    continue;
		}
		String loc = st.nextToken();
		String msg = "";
		while (st.hasMoreTokens()) {
		    msg = msg + st.nextToken() + " "; 
                }
		changeMessage( loc, msg );
		if (first) {      // Record the start location.
		    _startLocation = loc;
		    first = false;
		}
	    }
	}
	catch( IOException e ) {
	    System.err.println( "Graph.recordMessages( String " + 
				messagesfile + ")\n" + e.getMessage() );
	}
    }

    /**
     * Records the things assocated with vertices in the graph on
     * the basis of a file with the following format:
     * location thing1 thing2 ...
     */
    private void recordThings( String thingsfile )
    {
	try {
	    FileReader fin = new FileReader( thingsfile );
            BufferedReader things = new BufferedReader( fin );
            String line;
            while((line = things.readLine()) != null) {
                StringTokenizer st = new StringTokenizer( line );
		if( st.countTokens( ) < 2 ) {
		    System.err.println( "Skipping ill-formatted line " + line );
		    continue;
		}
		String loc = st.nextToken();
		while (st.hasMoreTokens()) {
		    addThing( loc, st.nextToken()); 
                }
	    }
	}
	catch( IOException e ) {
	    System.err.println( "Graph.recordThings( String " + 
				thingsfile + ")\n" + e.getMessage() );
	}
    }

    /**
     * All the public stuff. These methods are designed to hide the
     * internal structure of the MUD. Could declare these on an
     * interface and have external objects interact with the MUD via
     * the interface.
     */

    /**
     * A constructor that creates the MUD.
     */
    public World( String worldfile )
    {
	try {
        Scanner sc = new Scanner(new File(worldfile));
        while (sc.hasNextLine()) {
            loadRecord(sc.nextLine());
        }
      }
      catch(java.io.FileNotFoundException e) { System.out.println(e.getMessage()); }

      System.out.println("Imported: " + _name + " (" + vertexMap.size() + " locations)");
    }

    public World(List<String> markup) {
        for(String statement : markup) {
        loadRecord(statement);
        }
    }

    // This method enables us to display the entire MUD (mostly used
    // for testing purposes so that we can check that the structure
    // defined has been successfully parsed.
    public String toString()
    {
	String summary = "";
	Iterator iter = vertexMap.keySet().iterator();
	String loc;
	while (iter.hasNext()) {
	    loc = (String)iter.next();
	    summary = summary + "Node: " + loc;
	    summary += ((Vertex)vertexMap.get( loc )).toString();
	}
	summary += "Start location = " + _startLocation;
	return summary;
    }

    /**
     * A method to provide a string describing a particular location.
     */
    public String locationInfo( String loc )
    {
	return getVertex( loc ).toString();
    }
    public String locationStatus(String loc) {
        String summary = getVertex(loc)._msg + "\n";
        for(Map.Entry<String, Edge> vertex : getVertex(loc)._routes.entrySet()) {
            summary += "To the " + vertex.getKey() + " there is " + vertex.getValue()._view + "\n";
        }
        return summary;
    }
    /**
     * Get the start location for new MUD users.
     */
    
    public List<String> locationThings(String loc) {
        return getVertex(loc)._things;
    }
    /**
     * Add a thing to a location; used to enable us to add new users.
     */
    public void addThing( String loc,
			  String thing )
    {
	Vertex v = getVertex( loc );
	v._things.add( thing );
    }

    /**
     * Remove a thing from a location.
     */
    public void delThing( String loc, 
			  String thing )
    {
	Vertex v = getVertex( loc );
	v._things.remove( thing );
    }

    /**
     * A method to enable a player to move through the MUD (a player
     * is a thing). Checks that there is a route to travel on. Returns
     * the location moved to.
     */
    public String moveThing( String loc, String dir, String thing )
    {
	Vertex v = getVertex( loc );
	Edge e = v._routes.get( dir );
	if (e == null)   // if there is no route in that direction
	    return loc;  // no move is made; return current location.
	v._things.remove( thing );
	e._dest._things.add( thing );
	return e._dest._name;
    }

    public void export() {
    System.out.println("Saving: " + _name);
    try {
      PrintWriter writer = new PrintWriter(_name.toLowerCase() + ".world", "UTF-8");
      // name
      writer.println("name: " + _name + "\n");
      // edges
      for(Map.Entry<String, Vertex> vertex : vertexMap.entrySet()) {
        for(Map.Entry<String, Edge> route : getVertex(vertex.getKey())._routes.entrySet()) {
          writer.println("edge: " + vertex.getKey() + " " + route.getKey() + " " + route.getValue()._dest._name + " " + route.getValue()._view);
        }
      }
      writer.println("");
      // messages
      for(Map.Entry<String, Vertex> vertex : vertexMap.entrySet()) {
        writer.println("message: " + vertex.getKey() + " " + vertex.getValue()._msg);
      }
      writer.println("");
      // things
      for(Map.Entry<String, Vertex> vertex : vertexMap.entrySet()) {
        for(String thing : vertex.getValue()._things) {
          writer.println("thing: " + vertex.getKey() + " " + thing);
        }
      }
      // start
      writer.println("\nstart: " + _startLocation);
      writer.close();
    }
    catch(java.io.FileNotFoundException e) { System.out.println(e.getMessage()); }
    catch(java.io.UnsupportedEncodingException e) { System.out.println(e.getMessage()); }
  }
    /**
     * A main method that can be used to testing purposes to ensure
     * that the MUD is specified correctly.
     */
}
