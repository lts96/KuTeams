package UI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import clientSide.ClientMain;
import clientSide.Sender;

public class ChatScreen 
{
	private boolean act;
	private JFrame frame;
	private JTextField chat;
	private JButton submit , cameraOn , cameraOff , monitorOn , monitorOff;
	private JPanel panel;
	private JLabel chatLabel, clientLabel;
	private JTextArea chatlog , clientList;
	private JScrollPane scroll , scroll2;
	private Camera cam;
	private Monitor monitor;
	Sender send;
	public ChatScreen(boolean flag , Sender s , Camera cam , Monitor monitor)
	{
		this.act = flag;
		this.cam = cam;
		this.send = s;
		this.monitor = monitor;
		frame = new JFrame("채팅 화면");
		frame.setBounds(900, 300, 800, 500);
		panel = new JPanel();
		frame.add(panel);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// 채팅방 나갔다는 메세지 출력 
				String userChat = "[ex]:님이 채팅을 종료했습니다."+":"+ClientMain.roomCode+":";
				send.sendString(userChat);
				ClientMain.roomCode = -1;
				screenOn(false);
				cam.screenOn(false);
				//System.exit(0);
			}
		});
		panel.setLayout(null);
		
		
		chat = new JTextField(15);
		chat.setBounds(30, 400, 270, 40);
		panel.add(chat);
		chat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(act)
				sendChat();
			}
		});
		
		submit= new JButton("전송");
		submit.setBounds(300,400,70,40);
		panel.add(submit);
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(act)
				sendChat();
			}
		});
		
		cameraOn= new JButton("카메라 켜기");
		cameraOn.setBounds(450,330,120,50);
		panel.add(cameraOn);
		cameraOn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(act)
				{
					send.sendString("[ch]:"+"님이 방송을 시작했습니다!"+":"+ClientMain.roomCode+":");
					cam.screenOn(true);
					cam.run();
				}
			}
		});
		
		cameraOff= new JButton("카메라 끄기");
		cameraOff.setBounds(590,330,120,50);
		panel.add(cameraOff);
		cameraOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(act)
				{
					send.sendString("[ch]:"+"님이 방송을 종료했습니다."+":"+ClientMain.roomCode+":");
					cam.screenOn(false);
				}
			}
		});
		
		monitorOn= new JButton("모니터 켜기");
		monitorOn.setBounds(450,390,120,50);
		panel.add(monitorOn);
		monitorOn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(act)
				{
					chatlog.append("[시스템] : 모니터를 켰습니다.\n");
					monitor.screenOn(true);
					if(monitor.isAct())
						monitor.printScreen();
				}
			}
		});
		
		monitorOff= new JButton("모니터 끄기");
		monitorOff.setBounds(590,390,120,50);
		panel.add(monitorOff);
		monitorOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(act)
				{
					chatlog.append("[시스템] : 모니터를 껐습니다.\n");
					monitor.screenOn(false);
				}
			}
		});
		
	
		chatlog = new JTextArea();
		chatlog.setBounds(30,70,340, 330);
		panel.add(chatlog);
		
		scroll = new JScrollPane(chatlog);
		scroll.setBounds(30, 70,340, 330);
		scroll.getViewport().setBackground(Color.white);
		panel.add(scroll);
		
		clientList = new JTextArea();
		clientList.setBounds(450, 70, 260, 230);
		panel.add(clientList);
		
		scroll2 = new JScrollPane(clientList);
		scroll2.setBounds(450, 70,260, 230);
		scroll2.getViewport().setBackground(Color.white);
		panel.add(scroll2);
		
		clientLabel = new JLabel("현재 접속 명단");
		clientLabel.setBounds(450, 30, 150, 40);
		panel.add(clientLabel);
		
		chatLabel = new JLabel("채팅 기록");
		chatLabel.setBounds(30, 30 , 120,40);
		panel.add(chatLabel);
		
		frame.setVisible(flag);
		panel.setVisible(true);
	}
	public void recvChat(String input)   // 채팅 메세지 받기만 하는 역할 
	{
		if(input.length() > 0)
			addChat(input, false);
	}
	public void sendChat()    // 채팅 메세지 보내는 기능 
	{
		String str = chat.getText();
		String userChat = "[ch]:"+chat.getText()+":"+ClientMain.roomCode+":";
		send.sendString(userChat);
		chat.setText("");
		addChat(str , true);
	}
	public void sendWhisper()   // 형식    [cw]:이름:내용:roomCode     귓속말 기능 , 아직 미완성 
	{
		String str = chat.getText();
		String userChat = "[cw]:"+chat.getText()+":"+ClientMain.roomCode+":";
		send.sendString(userChat);
		chat.setText("");
		addChat(str , true);
	}
	public void addChat(String s , boolean flag)    // 채팅 내용 화면에 출력하는 함수 
	{
		if(flag)
			chatlog.append("[보낸 메세지]: "+s+"\n");
		else
		{
			String tok = s.split(":")[0];
			String chatText = s.split(":")[1];
			String name = s.split(":")[2];
			chatlog.append("["+name+"]:"+chatText+"\n");
		}
		chatlog.setCaretPosition(chatlog.getDocument().getLength());
	}
	public void printClientList(String list)                  // 현재 해당 방에 접속중인 client 출력 
	{
		String token = list.split(":")[0];
		int num = Integer.parseInt(list.split(":")[1]);
		for(int i=2 ;i< num+2;i++)
		{
			String name = list.split(":")[i];
			clientList.append("[이름 : "+ name+"]" +"\n");
		}
	}
	public void screenOn(boolean flag)     // 화면 껐다키는 기능 (버튼 등 다른 기능 비활성화 포함 )
	{
		this.act = flag;
		this.frame.setVisible(flag);
		chatlog.setText("");
		chat.setText("");
		String userChat = "[ch]:님이 채팅을 시작했습니다."+":"+ClientMain.roomCode+":";
		if(flag)
			send.sendString(userChat);
	}
	public boolean isAct()     // 지금 켜진 상태인지 체크용
	{
		return this.act;
	}
	public void setTitle()     // 프레임 제목 갱신용
	{
		frame.setTitle(ClientMain.clientName+"의 채팅화면");
	}
}
