package clientSide;

import java.awt.image.*;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
	private ByteBuffer buffer = ByteBuffer.allocate(1024);  // ���ڿ� ���ۿ� 
	private ByteBuffer imageBuffer = ByteBuffer.allocate(100000);
	private BufferedOutputStream out;
	private SocketChannel socketC;
	private BufferedImage image;
	private Robot robot;
	public Sender(SocketChannel s1)
	{
		this.socketC = s1;
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
	public boolean sendImage(BufferedImage image , int code) throws IOException 
	{
		boolean flag = true;
		//System.out.println("�̹��� ���� : "+ image.toString());      // �켱 �̹��� -> ����Ʈ �迭�� ��ȯ (�����ϱ� ����)
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		ImageIO.write(image, "png", baos );
		byte[] imageInByte=baos.toByteArray();
		//System.out.println("byte size : "+ imageInByte.length + " byte data : "+ imageInByte.toString());  
		String token = "[im]:"+code+":"+imageInByte.length+":";
		
		// ��� ->  39648byte
		imageBuffer = ByteBuffer.wrap(imageInByte);
		int write = socketC.write(imageBuffer);
		System.out.println("write �� ���� : "+ write);
		//imageBuffer.flip();
		// �̹��� ����Ʈ �迭�� �̹����� ��ȯ�ؼ� �����غ��� -> ����     ���� �̰� �������� �غ��� �����ϸ� �� 
		//BufferedImage imag = ImageIO.read(new ByteArrayInputStream(imageInByte));
        //ImageIO.write(imag, "png", new File("C:\\Users\\s_dlxotjs\\Desktop\\��Ʈ��ũ ���α׷���", "img.png"));
		imageBuffer.compact();
		imageBuffer.clear();
		return flag;
	}
}
