package UI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.*;
import clientSide.Sender;
public class LobbyScreen 
{
	private ArrayList<String> roomList = new ArrayList<String>();
	private JFrame frame;
	private JButton b1,b2,b3;
	private JPanel panel;
	private JLabel infor;
	private int x = 80;
	private CreateRoomScreen crs;
	private RoomScreen rs;
	private PrintInfoScreen pis;
	Sender s; 
	
	public LobbyScreen(boolean flag ,Sender s , CreateRoomScreen crs)  // 미완성.
	{
		this.crs = crs;
		this.s = s;
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
				crs.screenOn(true);
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
	public void enterRoom()
	{
		rs = new RoomScreen("테스트용","아무개",4, s ); // 출력 테스트 용
	}
	public void printMyInfo()
	{
		// 서버한테서 내 정보 받아옴 -> 생성자 통해서 내 정보 출력해주는 화면 생성 
		// pis = new PrintInfoScreen(String name , String id , String pw , String addr , int time);
		
	}
	public void screenOn(boolean flag)
	{
		this.frame.setVisible(flag);
		this.panel.setVisible(flag);
	}
}
