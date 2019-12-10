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
		frame = new JFrame("ä�� ȭ��");
		frame.setBounds(900, 300, 800, 500);
		panel = new JPanel();
		frame.add(panel);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// ä�ù� �����ٴ� �޼��� ��� 
				String userChat = "[ex]:���� ä���� �����߽��ϴ�."+":"+ClientMain.roomCode+":";
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
		
		submit= new JButton("����");
		submit.setBounds(300,400,70,40);
		panel.add(submit);
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(act)
				sendChat();
			}
		});
		
		cameraOn= new JButton("ī�޶� �ѱ�");
		cameraOn.setBounds(450,330,120,50);
		panel.add(cameraOn);
		cameraOn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(act)
				{
					send.sendString("[ch]:"+"���� ����� �����߽��ϴ�!"+":"+ClientMain.roomCode+":");
					cam.screenOn(true);
					cam.run();
				}
			}
		});
		
		cameraOff= new JButton("ī�޶� ����");
		cameraOff.setBounds(590,330,120,50);
		panel.add(cameraOff);
		cameraOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(act)
				{
					send.sendString("[ch]:"+"���� ����� �����߽��ϴ�."+":"+ClientMain.roomCode+":");
					cam.screenOn(false);
				}
			}
		});
		
		monitorOn= new JButton("����� �ѱ�");
		monitorOn.setBounds(450,390,120,50);
		panel.add(monitorOn);
		monitorOn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(act)
				{
					chatlog.append("[�ý���] : ����͸� �׽��ϴ�.\n");
					monitor.screenOn(true);
					if(monitor.isAct())
						monitor.printScreen();
				}
			}
		});
		
		monitorOff= new JButton("����� ����");
		monitorOff.setBounds(590,390,120,50);
		panel.add(monitorOff);
		monitorOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(act)
				{
					chatlog.append("[�ý���] : ����͸� �����ϴ�.\n");
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
		
		clientLabel = new JLabel("���� ���� ���");
		clientLabel.setBounds(450, 30, 150, 40);
		panel.add(clientLabel);
		
		chatLabel = new JLabel("ä�� ���");
		chatLabel.setBounds(30, 30 , 120,40);
		panel.add(chatLabel);
		
		frame.setVisible(flag);
		panel.setVisible(true);
	}
	public void recvChat(String input)   // ä�� �޼��� �ޱ⸸ �ϴ� ���� 
	{
		if(input.length() > 0)
			addChat(input, false);
	}
	public void sendChat()    // ä�� �޼��� ������ ��� 
	{
		String str = chat.getText();
		String userChat = "[ch]:"+chat.getText()+":"+ClientMain.roomCode+":";
		send.sendString(userChat);
		chat.setText("");
		addChat(str , true);
	}
	public void sendWhisper()   // ����    [cw]:�̸�:����:roomCode     �ӼӸ� ��� , ���� �̿ϼ� 
	{
		String str = chat.getText();
		String userChat = "[cw]:"+chat.getText()+":"+ClientMain.roomCode+":";
		send.sendString(userChat);
		chat.setText("");
		addChat(str , true);
	}
	public void addChat(String s , boolean flag)    // ä�� ���� ȭ�鿡 ����ϴ� �Լ� 
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
	public void printClientList(String list)                  // ���� �ش� �濡 �������� client ��� 
	{
		String token = list.split(":")[0];
		int num = Integer.parseInt(list.split(":")[1]);
		for(int i=2 ;i< num+2;i++)
		{
			String name = list.split(":")[i];
			clientList.append("[�̸� : "+ name+"]" +"\n");
		}
	}
	public void screenOn(boolean flag)     // ȭ�� ����Ű�� ��� (��ư �� �ٸ� ��� ��Ȱ��ȭ ���� )
	{
		this.act = flag;
		this.frame.setVisible(flag);
		chatlog.setText("");
		chat.setText("");
		String userChat = "[ch]:���� ä���� �����߽��ϴ�."+":"+ClientMain.roomCode+":";
		if(flag)
			send.sendString(userChat);
	}
	public boolean isAct()     // ���� ���� �������� üũ��
	{
		return this.act;
	}
	public void setTitle()     // ������ ���� ���ſ�
	{
		frame.setTitle(ClientMain.clientName+"�� ä��ȭ��");
	}
}
