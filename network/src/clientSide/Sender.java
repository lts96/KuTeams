package clientSide;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import javax.imageio.ImageIO;

import java.awt.*;
public class Sender 
{
	private ByteBuffer buffer = ByteBuffer.allocate(1024);  // 문자열 전송용 
	private BufferedOutputStream out;
	private SocketChannel socketC;
	private Socket socket;
	private BufferedImage image;
	private Robot robot;
	public Sender(SocketChannel s)
	{
		this.socketC = s;
		this.socket = new Socket();
		try {
			socket.connect(socketC.getRemoteAddress(), 3000);
			out = new BufferedOutputStream(socket.getOutputStream());
		} catch (IOException e1) {
			System.out.println("screen socket connect fail");
		}
		try {
			robot = new Robot();
		} catch (AWTException e) {
			System.out.println("create robot fail");
		}
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
	public boolean sendScreen(BufferedImage image) 
	{
		boolean flag = true;
		try {
			ImageIO.write(image , "png", out);
			out.flush();
		} catch (IOException e) {
			System.out.println("image send fail");
		}
		return flag;
	}
}
