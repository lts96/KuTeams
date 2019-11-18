package clientSide;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
public class LobbyScreen 
{
	
	JFrame frame;
	JButton b1,b2,b3;
	JPanel panel;
	JLabel infor;
	int x = 80;
	public LobbyScreen(boolean flag)
	{
		frame = new JFrame("메인 화면");
		frame.setBounds(250, 150, 800, 600);
		//frame.setLayout(new FlowLayout());
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		panel = new JPanel();
		frame.add(panel);
		panel.setLayout(null);
		b1 = new JButton("강의실 생성");
		b1.setBounds(x, 10,150, 100);
		b2 = new JButton("강의실 입장"); 
		b2.setBounds(x+250, 10,150, 100);
		b3 = new JButton("내 정보 확인");
		b3.setBounds(x+500, 10,150, 100);
		infor = new JLabel("현재 개설된 강의 목록");
		infor.setBounds(x , 130 , 200 , 40);
		
		panel.add(infor);
		panel.add(b1);
		panel.add(b2);
		panel.add(b3);
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createRoom();
			}
		});
		
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enterRoom();
			}
		});
		
		b3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				printMyInfo();
			}
		});
		frame.setVisible(flag);
		panel.setVisible(flag);
	}
	public void createRoom()
	{
		JFrame frm = new JFrame("방 만들기");
	}
	public void enterRoom()
	{
		
		JFrame frm = new JFrame("방 이름");
		
	}
	public void printMyInfo()
	{
		
	}
}
