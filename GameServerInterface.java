
package mud.cs3524.solutions.mud;

import java.rmi.Remote;
import java.util.List;
import java.rmi.RemoteException;

public interface GameServerInterface extends Remote
{
	public Boolean messaging(ClientInterface client, String text) throws java.rmi.RemoteException;
	public List<String> getWorlds() throws java.rmi.RemoteException;
	public Boolean pick(ClientInterface client, String thing) throws java.rmi.RemoteException;
	public String createWorld(List<String> markup) throws java.rmi.RemoteException;
	public void exportWorlds() throws java.rmi.RemoteException;
	public Boolean drop(ClientInterface client, String thing) throws java.rmi.RemoteException;
	public void status(ClientInterface client, String message) throws java.rmi.RemoteException;
	public Boolean spawn(ClientInterface client, String world) throws java.rmi.RemoteException;
	public Boolean move(ClientInterface client, String dir) throws java.rmi.RemoteException;
	public Boolean message(ClientInterface client, String scope, String message) throws java.rmi.RemoteException;
}
