package UI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;

import clientSide.ClientMain;
import clientSide.Sender;
public class RoomScreen {
	private JFrame frame;
	private JPanel panel;
	private JButton on, off;
	private JLabel roomName , teacher , studentNum;
	private int width = 1280 , height = 720;
	private ChatScreen cs;
	Sender send;  
	private boolean act;
	private boolean shareSwitch;
	private Robot r;
	public RoomScreen()
	{
		
	}
	public RoomScreen(boolean flag,  Sender s) //
	{
		this.send = s;
		this.act = flag;
		this.shareSwitch = false;
		try {
			this.r = new Robot();
		} catch (AWTException e1) {
			System.out.println("create robot fail - RoomScreen line 35");
		}
		int x = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - width / 2; 
		int y = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - height / 2;
		frame = new JFrame();
		panel = new JPanel();
		frame.add(panel);
		frame.setBounds(x, y,width, height);
		frame.setLayout(null);
		//frame.getContentPane().setBackground(Color.white);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// 나중에 따로 close 함수 만들어서 바꿔야됨
				ClientMain.roomCode = -1;
				System.exit(0);
			}
		});
		on = new JButton("start");
		on.setBounds(400,530,80,60);
		panel.add(on);
		on.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(act)
				{
					shareSwitch = true;
					sendScreen();
				}
			}
		});
		off = new JButton("stop");
		off.setBounds(500,530,80,60);
		panel.add(off);
		off.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(act)
					shareSwitch = false;
			}
		});
		
		frame.setVisible(flag);
		panel.setVisible(true);
		//cs = new ChatScreen(s);
	}
	public void sendScreen()
	{
		BufferedImage image;
		while(shareSwitch)
		{
			image = r.createScreenCapture(new Rectangle(0, 0, width, height));
			send.sendImage(image);
		}
	}
	public void recvScreen()
	{
		
	}
	public void screenOn(boolean flag)
	{
		this.frame.setVisible(flag);
		this.act = flag;
	}
}
