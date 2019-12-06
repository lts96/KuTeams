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
	private JButton submit;
	private JPanel panel;
	private JLabel teacher, client;
	private JTextArea chatlog;
	private JScrollPane scroll;
	Sender send;
	public ChatScreen(boolean flag , Sender s)
	{
		this.act = flag;
		this.send = s;
		frame = new JFrame("ä�� ȭ��");
		frame.setBounds(900, 300, 400, 500);
		panel = new JPanel();
		frame.add(panel);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// ä�ù� �����ٴ� �޼��� ��� 
				String userChat = "[ex]:���� ä���� �����߽��ϴ�."+":"+ClientMain.roomCode+":";
				send.sendString(userChat);
				ClientMain.roomCode = -1;
				screenOn(false);
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
		
		submit= new JButton("����");
		submit.setBounds(300,400,70,40);
		panel.add(submit);
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(act)
				sendChat();
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
	public void sendWhisper()   // ����    [cw]:�̸�:����:roomCode 
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
			chatlog.append("[���� �޼���]: "+s+"\n");
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
		String userChat = "[ch]:���� ä���� �����߽��ϴ�."+":"+ClientMain.roomCode+":";
		if(flag)
			send.sendString(userChat);
	}
	public boolean isAct()
	{
		return this.act;
	}
}
