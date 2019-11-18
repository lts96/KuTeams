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
	JFrame frame;
	JTextField id;
	JPasswordField pw;
	JButton b1;
	JPanel panel;
	JLabel ID, pass;
	public LoginScreen(boolean flag)
	{
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
		
		b1 = new JButton("Login");
		b1.setBounds(280,30,80,60);
		panel.add(b1);
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginCheck();
			}
		});
		
		frame.setVisible(flag);
	}
	public void loginCheck()
	{
		if(id.getText().equals("client")&& new String(pw.getPassword()).equals("1234")){
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
	public boolean getFlag()
	{
		return flag;
	}
}
