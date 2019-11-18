package clientSide;

public class Client 
{
	private String name;
	private String id;
	private String pw;
	private int time;
	public Client()
	{
		
	}
	public Client(String name, String id , String pw)
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
	
	
}
