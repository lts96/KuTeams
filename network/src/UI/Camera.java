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
		frame = new JFrame("ī�޶�"); //â ����
		frame.setBounds(0, 0, 720, 480);//â ��ġ,ũ�� ����
		frame.setLayout(null);
		
		/*
		text = new JTextField(); //�ؽ�Ʈ���� ����
		text.setVisible(true); //����
		text.setBounds(25, 15, 100, 50);
		
		button = new JButton("����");
		button.setVisible(true);
		button.setBounds(125, 15, 50, 50);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				client_work();
			}

		});
		//frame.add(panel);
		frame.add(text);
		frame.add(button);
		*/
		this.send = s;
		frame.setVisible(false);
		//camera_work();
	}
	public void camera_work()
	{
		Socket socket = null;
		System.out.println("Ŭ���̾�Ʈ �غ�Ϸ�");
		try 
		{
			//socket = new Socket("127.0.0.1", 12345);
			System.out.println("���ӿϷ� - Ŭ���̾�Ʈ ");
			BufferedImage image;
			Robot r = new Robot(); 
			//BufferedOutputStream bout = new BufferedOutputStream(socket.getOutputStream());
			//frame.setUndecorated(true);
			//frame.setBackground(new Color(0,0,0,122));
			frame.setBounds(0, 0, 350, 1);
			//while(flag) 
			//{
				int frameX = frame.getX();
				int frameY = frame.getY();
				image = r.createScreenCapture(new Rectangle(frameX,frameY,720,480));
				send.sendImage(image, ClientMain.roomCode);
					//��ũ������ �� image�� ������
				//ImageIO.write(image, "bmp", bout);//�� �̹����� png���Ϸ� ���� �ƿ�ǲ��Ʈ������ ����
				//bout.flush();   //���ۿ� ���� �̹����� ������ ����
			//}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("���ӽ��� - Ŭ���̾�Ʈ");
		}
		
	}
	public void screenOn(boolean act)
	{
		frame.setVisible(act);
		this.flag = act;
	}
}
