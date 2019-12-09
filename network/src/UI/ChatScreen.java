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
	private JLabel teacher, client;
	private JTextArea chatlog;
	private JScrollPane scroll;
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
				cam.screenOn(false);
			}
		});
		
		monitorOn= new JButton("모니터 켜기");
		monitorOn.setBounds(450,390,120,50);
		panel.add(monitorOn);
		monitorOn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(act)
				{
					monitor.screenOn(true);
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
					monitor.screenOn(false);
			}
		});
		
	
		chatlog = new JTextArea();
		chatlog.setBounds(30, 30,340, 350);
		panel.add(chatlog);
		
		scroll = new JScrollPane(chatlog);
		scroll.setBounds(30, 30,340, 350);
		scroll.getViewport().setBackground(Color.white);
		panel.add(scroll);
		frame.setVisible(flag);
		panel.setVisible(true);
	}
	public void recvChat(String input) 
	{
		if(input.length() > 0)
			addChat(input, false);
	}
	public void sendChat()
	{
		String str = chat.getText();
		String userChat = "[ch]:"+chat.getText()+":"+ClientMain.roomCode+":";
		send.sendString(userChat);
		chat.setText("");
		addChat(str , true);
	}
	public void sendWhisper()   // 형식    [cw]:이름:내용:roomCode 
	{
		String str = chat.getText();
		String userChat = "[cw]:"+chat.getText()+":"+ClientMain.roomCode+":";
		send.sendString(userChat);
		chat.setText("");
		addChat(str , true);
	}
	public void addChat(String s , boolean flag)
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
	public void screenOn(boolean flag)
	{
		this.act = flag;
		this.frame.setVisible(flag);
		chatlog.setText("");
		chat.setText("");
		String userChat = "[ch]:님이 채팅을 시작했습니다."+":"+ClientMain.roomCode+":";
		if(flag)
			send.sendString(userChat);
	}
	public boolean isAct()
	{
		return this.act;
	}
}
