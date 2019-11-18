package clientSide;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
public class LoginScreen
{
	private boolean flag = false;
	LobbyScreen screen;
	SignScreen signScreen;
	JFrame frame;
	JTextField id;
	JPasswordField pw;
	JButton submit , signIn;
	JPanel panel;
	JLabel ID, pass;
	Sender send;
	public LoginScreen(boolean flag ,Sender s)
	{
		this.send = s;
		frame = new JFrame("·Î±×ÀÎ È­¸é");
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
		boolean flag = true;   // ¶«»§¿ë 
		String userId = "!@#$:"+id.getText();
		String userPw = ":"+ new String(pw.getPassword())+":";
		send.sendString(userId);
		send.sendString(userPw);
		
		
		if(flag){
			JOptionPane.showMessageDialog(null, "login success!");
			flag = true;
			frame.dispose();
			screen = new LobbyScreen(flag);
		}
		else 
		{
			JOptionPane.showMessageDialog(null, "login fail!");
		}
	}
	
	
	
	
}
