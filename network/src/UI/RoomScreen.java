package UI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import clientSide.Sender;
public class RoomScreen {
	private JFrame frame;
	private JPanel panel;
	private JLabel roomName , teacher , studentNum;
	private int width = 1280 , height = 720;
	private ChatScreen cs;
	Sender send;  
	public RoomScreen()
	{
		
	}
	public RoomScreen(String roomName , String teacher , int snum , Sender s)
	{
		this.send = s;
		int x = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - width / 2; 
		int y = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - height / 2;
		frame = new JFrame(roomName);
		panel = new JPanel();
		frame.setBounds(x, y,width, height);
		frame.setLayout(null);
		//frame.getContentPane().setBackground(Color.white);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// 나중에 따로 close 함수 만들어서 바꿔야됨
				System.exit(0);
			}
		});
		frame.setVisible(true);
		//cs = new ChatScreen(s);
		screenShare();
	}
	public void screenShare()
	{
		
	}
	public void screenOn(boolean flag)
	{
		this.frame.setVisible(flag);
	}
}
