package serverSide;

import java.util.ArrayList;
import java.util.List;

public class Room 
{
	private int roomCode;
	private String roomName;
	private String host;
	private int limit;
	private List<Client> clientList = new ArrayList<Client>();
	
	public Room(int code, String name , String host , int limit)  
	{
		this.roomCode = code;
		this.roomName = name; 
		this.setHost(host);
		this.limit = limit;
	}
	public int getLimit() {
		return this.limit;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public List<Client> getClientList() {
		return clientList;
	}
	public void setClientList(List<Client> clientList) {
		this.clientList = clientList;
	}
	public int getRoomCode() {
		return roomCode;
	}
	public void setRoomCode(int roomCode) {
		this.roomCode = roomCode;
	}
	public void addClient(Client c)
	{
		if(clientList.size()< limit)
			clientList.add(c);
		else 
			System.out.println("room is full!!");
	}
	public void deleteClient(Client c)
	{
		if(!clientList.isEmpty())
			clientList.remove(c);
	}
	public Client findClientByName(String name)
	{
		Client c;
		if(isRoomEmpty())
			return null;
		else 
		{
			for(int i=0;i<clientList.size();i++)
			{
				c = clientList.get(i);
				if(c.getName().equals(name))
					return c;
			}
		}
		return null;
	}
	public Client findClientByLogin(String id , String pw)
	{
		Client c; 
		if(isRoomEmpty())
			return null;
		else 
		{
			for(int i=0;i<clientList.size();i++)
			{
				c = clientList.get(i);
				if(c.getId().equals(id)&&c.getPw().equals(pw))
					return c;
			}
		}
		return null;
	}
	public boolean isRoomEmpty()
	{
		return clientList.isEmpty();
	}
	public int clientNum()
	{
		return clientList.size();
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
}
