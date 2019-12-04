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
	private ArrayList<RoomInfo> roomInfoList = new ArrayList<RoomInfo>();
	private JFrame frame;
	private JButton b1,b2,b3 , refresh;
	private JPanel panel;
	private JLabel infor, enter;
	private JTextField roomName;
	private int x = 80;
	private CreateRoomScreen crs;
	private RoomScreen rs;
	private PrintInfoScreen pis;
	private JTextArea roomlist;
	private JScrollPane scroll;
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
				// 현재  연결 하나 끊으면 서버가 닫혀버리는 버그 있음? 
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
		refresh = new JButton("새로고침");
		refresh.setBounds(x+300, 140, 100, 40);
		
		roomlist = new JTextArea();
		roomlist.setBounds(x,180,400, 350);
		panel.add(roomlist);
		
		scroll = new JScrollPane(roomlist);
		scroll.setBounds(x,180,400, 350);
		scroll.getViewport().setBackground(Color.white);
		panel.add(scroll);

		enter = new JLabel("들어갈 방 이름 입력");
		enter.setBounds(500,130,150,40);
		panel.add(enter);
		
		roomName = new JTextField();
		roomName.setBounds(500,180,200,45);
		panel.add(roomName);
		
		panel.add(infor);
		panel.add(b1);
		panel.add(b2);
		panel.add(b3);
		panel.add(refresh);
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
		
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				requestRoomList();
			}
		});
		frame.setVisible(flag);
		panel.setVisible(flag);
	}
	public void requestRoomList() 
	{
		roomlist.setText("");
		s.sendString("[rl]");
	}
	public void enterRoom()
	{
		String enterRequest = roomName.getText();
		s.sendString("[ra]:"+enterRequest+":");
	}
	public void failRoomAccess(String str)
	{
		String sub = str.split(":")[0];
		JOptionPane.showMessageDialog(null, sub);
	}
	public void printMyInfo()
	{
		// 서버한테서 내 정보 받아옴 -> 생성자 통해서 내 정보 출력해주는 화면 생성 
		// pis = new PrintInfoScreen(String name , String id , String pw , String addr , int time);
		
	}
	public void updateScreenInfo(String input , boolean flag )   // 더하는 것 뿐만아니라 지우는 것도 가능해야함 , 그리고 실시간으로 방 입장할때마다 인원 변화 시켜야됨 
	{
		if(flag)   //room list 추가시 
		{
			System.out.println("room : "+ input);
			String token = input.split(":")[0];
			int num = Integer.parseInt(input.split(":")[1]);
			for(int i=0;i<num;i++)
			{
				String rname = input.split(":")[i*3+2];
				String count = input.split(":")[i*3+3];
				String limit = input.split(":")[i*3+4];
				//RoomInfo r = new RoomInfo(rname,count,limit);
				//roomInfoList.add(r);
				roomlist.append("[방 이름 : "+ rname +"    현재 인원 :  "+count+"    인원 제한 :  "+limit +"]\n");
				roomlist.setCaretPosition(roomlist.getDocument().getLength());
			}
		}
	}
	public void addScreenInfo(String input, boolean flag)
	{
		if(flag)
		{
			System.out.println("room : "+ input);
			String token = input.split(":")[0];
			String rname = input.split(":")[1];
			String count = input.split(":")[2];
			String limit = input.split(":")[3];
				//RoomInfo r = new RoomInfo(rname,count,limit);
				//roomInfoList.add(r);
			roomlist.append("[방 이름 : "+ rname +"    현재 인원 :  "+count+"    인원 제한 :  "+limit +"]\n");
			roomlist.setCaretPosition(roomlist.getDocument().getLength());
		}
	}
	public void screenOn(boolean flag)
	{
		this.frame.setVisible(flag);
		this.panel.setVisible(flag);
	}
}
