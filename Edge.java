/*
 @author: Martin, 51444972
 @version: 1.0.1

*/
package mud.cs3524.solutions.mud;

// Represents an path in the MUD (an edge in a graph).
class Edge
{
    public Vertex _dest;   // Your destination if you walk down this path
    public String _view;   // What you see if you look down this path
    
    public Edge( Vertex d, String v )
    {
        _dest = d;
		_view = v;
    }
}
/* file that was provided*/
