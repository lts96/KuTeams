package serverSide;

import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class Client 
{
	private String name;
	private String id;
	private String pw;
	private int time;
	private SocketChannel socketC;   
	private int roomCode = -1;
	private boolean online = false;
	public Client()
	{
		
	}
	public Client(String name, String id , String pw )   
	{
		this.setName(name);
		this.setId(id);
		this.setPw(pw);
		this.setTime(0);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	
	public int getRoomCode() {
		return roomCode;
	}
	public void setRoomCode(int roomCode) {
		this.roomCode = roomCode;
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
	public SocketChannel getSocketChannel() {
		return socketC;
	}
	public void setSocketChannel(SocketChannel socketC) {
		this.socketC = socketC;
	}
	
	
}
