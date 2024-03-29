/*
 @author: Martin, 51444972
 @version: 1.0.1

*/

package mud.cs3524.solutions.mud;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;

// Represents a location in the MUD (a vertex in the graph).
class Vertex
{
    public String _name;             // Vertex name
    public String _msg = "";         // Message about this location
    public Map<String,Edge> _routes; // Association between direction
				     // (e.g. "north") and a path
				     // (Edge)
    public List<String> _things;     // The things (e.g. players) at
				     // this location

    public Vertex( String nm )
    {
	_name = nm; 
	_routes = new HashMap<String,Edge>(); // Not synchronised
	_things = new Vector<String>();       // Synchronised
    }

}

/*another file that was provided*/