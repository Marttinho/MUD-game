
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
	public Boolean messagingsomeone(ClientInterface client, String who, String text) throws java.rmi.RemoteException;
	public Boolean removePlayer(ClientInterface player) throws java.rmi.RemoteException;
}
/* declaring all the functions needed on the Server side, the functions messaging and messaging someone are the ones that are additional for the A4-A1*/