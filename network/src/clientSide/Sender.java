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
	private ByteBuffer buffer = ByteBuffer.allocate(1024);  // 문자열 전송용 
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
		//System.out.println("이미지 정보 : "+ image.toString());      // 우선 이미지 -> 바이트 배열로 변환 (전송하기 위해)
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		ImageIO.write(image, "png", baos );
		byte[] imageInByte=baos.toByteArray();
		//System.out.println("byte size : "+ imageInByte.length + " byte data : "+ imageInByte.toString());  
		String token = "[im]:"+code+":"+imageInByte.length+":";
		
		// 결과 ->  39648byte
		imageBuffer = ByteBuffer.wrap(imageInByte);
		int write = socketC.write(imageBuffer);
		System.out.println("write 한 길이 : "+ write);
		//imageBuffer.flip();
		// 이번엔 바이트 배열을 이미지로 변환해서 저장해보기 -> 성공     이제 이걸 서버에서 해보고 성공하면 끝 
		//BufferedImage imag = ImageIO.read(new ByteArrayInputStream(imageInByte));
        //ImageIO.write(imag, "png", new File("C:\\Users\\s_dlxotjs\\Desktop\\네트워크 프로그래밍", "img.png"));
		imageBuffer.compact();
		imageBuffer.clear();
		return flag;
	}
}
