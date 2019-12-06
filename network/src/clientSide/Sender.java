package clientSide;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import javax.imageio.ImageIO;

import java.awt.*;
public class Sender 
{
	private ByteBuffer buffer = ByteBuffer.allocate(1024);  // 문자열 전송용 
	private BufferedOutputStream out;
	private SocketChannel socketC;
	private BufferedImage image;
	private Robot robot;
	private DatagramPacket dp;
	private DatagramSocket udpSocket;
	public Sender(SocketChannel s1 , DatagramSocket s2)
	{
		this.socketC = s1;
		this.setUdpSocket(s2);
		
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
	public boolean sendImage(BufferedImage image) 
	{
		boolean flag = true;
		
		
		return flag;
	}
	public DatagramSocket getUdpSocket() {
		return udpSocket;
	}
	public void setUdpSocket(DatagramSocket udpSocket) {
		this.udpSocket = udpSocket;
	}
}
