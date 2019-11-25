package UI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import clientSide.Receiver;
import clientSide.Sender;
public class LoginScreen
{
	private boolean flag = false;
	private LobbyScreen screen;
	SignScreen signScreen;
	private JFrame frame;
	private JTextField id;
	private JPasswordField pw;
	private JButton submit , signIn;
	private JPanel panel;
	private JLabel ID, pass;
	Sender send;   Receiver recv;
	public LoginScreen(boolean flag ,Sender s , Receiver r)
	{
		this.send = s;
		this.recv = r;
		frame = new JFrame("로그인 화면");
		frame.setBounds(500, 500, 400, 300);
		panel = new JPanel();
		frame.add(panel);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		panel.setLayout(null);
		ID = new JLabel("ID");
		ID.setBounds(10,30 ,80 ,25);
		panel.add(ID);
		
		pass = new JLabel("PW");
		pass.setBounds(10,60 , 80 ,25);
		panel.add(pass);
		
		id = new JTextField(15);
		id.setBounds(60, 30, 200, 30);
		pw = new JPasswordField(15);
		pw.setBounds(60, 60, 200, 30);
		pw.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginCheck();
			}
		});
		panel.add(id);
		panel.add(pw);
		
		submit= new JButton("Login");
		submit.setBounds(280,30,80,60);
		panel.add(submit);
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginCheck();
			}
		});
		
		signIn = new JButton("Sign in");
		signIn.setBounds(280,150,80,60);
		panel.add(signIn);
		signIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				signScreen = new SignScreen();
				
			}
		});
		
		frame.setVisible(flag);
	}
	public void loginCheck()
	{
		String str;
		String userId = "[lp]:"+id.getText();
		String userPw = ":"+ new String(pw.getPassword())+":";
		send.sendString(userId);
		send.sendString(userPw);
		recv.receiveString();
		str = recv.receiveString();
		System.out.println( "receive msg line 85 : "+ str);
		
		if(str.contains("[[login success!!]]")){
			JOptionPane.showMessageDialog(null, "login success!");
			flag = true;
			frame.dispose();
			screen = new LobbyScreen(flag , send , recv);
		}
		else if(str.contains("[[login fail!!]]"))    // 여기까진 됨
		{
			JOptionPane.showMessageDialog(null, "login fail!");
		}
	}
	
	
	
	
}
