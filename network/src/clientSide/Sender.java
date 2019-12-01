package clientSide;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Sender 
{
	private ByteBuffer buffer = ByteBuffer.allocate(1024);  // 문자열 전송용 
	private SocketChannel socketC;
	
	public Sender(SocketChannel s)
	{
		this.socketC = s;
	}
	public boolean sendString(String str)  
	{
		boolean flag = true;
		try {
			buffer.clear();
			buffer = ByteBuffer.wrap(str.getBytes("UTF-8"));
			System.out.println("[client send]-> " +new String(buffer.array()));
			try {
				socketC.write(buffer);
			} catch (IOException e) {
				System.out.println("sender line 27 catch");
				return !flag;
			}
			
		} catch (UnsupportedEncodingException e) {
			System.out.println("sender line 32 catch");
			return !flag;
		}
		return flag;
	}
	public boolean sendScreen()
	{
		boolean flag = true;
		return flag;
	}
}
