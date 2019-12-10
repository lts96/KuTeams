package UI;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

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
	private LineBorder border;
	private Thread video;
	Sender send;
	private boolean flag;
	public Camera(Sender s)
	{
		panel = new JPanel();
		frame = new JFrame("ī�޶�"); //â ����
		frame.setBounds(300, 300, 720, 1);//â ��ġ,ũ�� ����
		panel.setBounds(0,0,710,433);
		frame.setLayout(null);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				send.sendString("[ch]:"+"���� ����� �����߽��ϴ�."+":"+ClientMain.roomCode+":");
				screenOn(false);
			}
		});
		this.send = s;
		frame.setVisible(false);
		//frame.add(panel);
		//panel.setVisible(false);
		//border = new LineBorder(Color.RED , 5 , true);
		//panel.setBorder(border);
		//panel.setOpaque(false);
	}
	public void run()
	{
		video = new Thread() {
			public void run()
			{
				try 
				{
					BufferedImage image;
					Robot r = new Robot(); 
					while(flag) 
					{
						this.sleep(30);
						int frameX = frame.getX();
						int frameY = frame.getY();
						image = r.createScreenCapture(new Rectangle(frameX,frameY,720,480));
						send.sendImage(image, ClientMain.roomCode);
						//ImageIO.write(image, "bmp", bout);//�� �̹����� png���Ϸ� ���� �ƿ�ǲ��Ʈ������ ����
						//bout.flush();   //���ۿ� ���� �̹����� ������ ����
					}
					System.out.println("ȭ�� ���� ����!");
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					System.out.println("camera error");
				}
			}
		};
		video.start();
	}
	public void screenOn(boolean act)
	{
		this.flag = act;
		frame.setVisible(act);
		panel.setVisible(act);
	}
	public void setTitle()
	{
		frame.setTitle(ClientMain.clientName+"�� ī�޶�");
	}
}
