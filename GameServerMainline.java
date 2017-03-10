
package mud.cs3524.solutions.mud;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.net.InetAddress;

/*the server mainline, binds as a stub to the rmi adress so it can be called by client, hostaname is set to 
localhost, security is again rmishout that grants all priviledges
*/
public class GameServerMainline
{
    public static void main(String args[])
    {
	if (args.length < 2) {
	    System.err.println( "Usage:\njava GameServerMainline <registryport> <serverport>" ) ;
	    return;
	}

	try {
	    String hostname = (InetAddress.getLocalHost()).getCanonicalHostName() ;
	    int registryport = Integer.parseInt( args[0] ) ;
	    int serverport = Integer.parseInt( args[1] ) ;
	
	    System.setProperty( "java.security.policy", "rmishout.policy" ) ;
	    System.setSecurityManager( new RMISecurityManager() ) ;

        GameServerImplementation serv = new GameServerImplementation();

        GameServerInterface stub = (GameServerInterface)java.rmi.server.UnicastRemoteObject.exportObject( serv, serverport );
        Naming.rebind( "rmi://" + hostname + ":" + registryport + "/GameServerMainline", stub );
	
        System.out.println("Server is running");

	}
	catch(java.net.UnknownHostException e) {
	    System.err.println( "It seems that Java can't determine the local host!" );
	}
	catch (java.io.IOException e) {
            System.out.println( "Failed to register." );
        }
    }
}
//java mud.cs3524.solutions.mud.GameServerMainline 50011 50012
//java mud.cs3524.solutions.mud.ClientMainline localhost 50011 50013
