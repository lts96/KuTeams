package UI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import clientSide.Sender;

public class ChatScreen 
{
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
		this.send = s;
		frame = new JFrame("채팅 화면");
		frame.setBounds(900, 300, 400, 500);
		panel = new JPanel();
		frame.add(panel);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// close thread
				System.exit(0);
			}
		});
		panel.setLayout(null);
		
		
		chat = new JTextField(15);
		chat.setBounds(30, 400, 270, 40);
		panel.add(chat);
		chat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendChat();
			}
		});
		
		submit= new JButton("전송");
		submit.setBounds(300,400,70,40);
		panel.add(submit);
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
		String userChat = "[cp]:"+chat.getText()+":";
		send.sendString(userChat);
		chat.setText("");
		addChat(str , true);
	}
	public void addChat(String s , boolean flag)
	{
		if(flag)
			chatlog.append("[보낸 메세지]: "+s+"\n");
		else
			chatlog.append("[받은 메세지]: "+s+"\n");
		chatlog.setCaretPosition(chatlog.getDocument().getLength());
	}
	public void screenOn(boolean flag)
	{
		this.frame.setVisible(flag);
	}
}
