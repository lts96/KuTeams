package UI;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import clientSide.ClientMain;
import clientSide.Sender;
public class Camera 
{
	private final int w = Toolkit.getDefaultToolkit().getScreenSize().width, h = Toolkit.getDefaultToolkit().getScreenSize().height;
	private final int x = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - w / 2, y = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - h / 2;
	private JFrame frame;
	private JTextField text;
	private JPanel panel;
	private JButton button;
	Sender send;
	private boolean flag;
	public Camera(Sender s)
	{
		frame = new JFrame("카메라"); //창 생성
		frame.setBounds(300, 300,720,1);//창 위치,크기 조절
		frame.setLayout(null);
		this.send = s;
		frame.setVisible(false);
	}
	public void run()
	{
		Thread t1 = new Thread() {
			public void run()
			{
				try 
				{
					BufferedImage image;
					Robot r = new Robot(); 
					while(flag) 
					{
						int frameX = frame.getX();
						int frameY = frame.getY();
						image = r.createScreenCapture(new Rectangle(frameX,frameY,720,480));
						send.sendImage(image, ClientMain.roomCode);
						//ImageIO.write(image, "bmp", bout);//그 이미지를 png파일로 소켓 아웃풋스트림으로 쏴줌
						//bout.flush();   //버퍼에 쓰인 이미지를 서버로 보냄
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					System.out.println("camera error");
				}
			}
		};
		t1.start();
	}
	public void screenOn(boolean act)
	{
		frame.setVisible(act);
		this.flag = act;
	}
}
