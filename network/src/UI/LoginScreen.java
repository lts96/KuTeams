package UI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;

import javax.swing.*;

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
	Sender send; 
	private boolean act;
	public LoginScreen(boolean flag ,Sender s , LobbyScreen screen)
	{
		this.screen = screen;
		this.send = s;
		act = flag;
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
				if(act)
				requestlogin();
			}
		});
		panel.add(id);
		panel.add(pw);
		
		submit= new JButton("Login");
		submit.setBounds(280,30,80,60);
		panel.add(submit);
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(act)
				requestlogin();
			}
		});
		
		signIn = new JButton("Sign in");
		signIn.setBounds(280,150,80,60);
		panel.add(signIn);
		signIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//if(act)
				//signScreen = new SignScreen();
				
			}
		});
		
		frame.setVisible(flag);
	}
	public void requestlogin()
	{
		String userId = "[lp]:"+id.getText();
		String userPw = ":"+ new String(pw.getPassword())+":";
		String port = Integer.toString(send.getUdpSocket().getLocalPort());
		send.sendString(userId + userPw + port + ":");
	}
	public void recvLogin(String str)
	{
		if(str.contains("[[login success]]")){
			JOptionPane.showMessageDialog(null, "login success!");
			flag = true;
			frame.dispose();
			screen.screenOn(true);
		}
		else    
		{
			String sub = str.split(":")[0];
			JOptionPane.showMessageDialog(null, sub);
		}
	}
	public void screenOn(boolean flag)
	{
		this.act = flag;
		this.frame.setVisible(flag);
	}
}
