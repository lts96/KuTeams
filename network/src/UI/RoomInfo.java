package UI;


// 로비 스크린 전용   -> 방 목록 출력 용
public class RoomInfo 
{
	private String name;
	private String limit;
	private String count;
	public RoomInfo(String n , String c , String l)
	{
		this.name = n;
		this.count = c;
		this.limit = l;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLimit() {
		return limit;
	}
	public void setLimit(String limit) {
		this.limit = limit;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	
}
