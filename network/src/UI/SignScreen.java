package UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class SignScreen 
{
	JFrame frame;
	JTextField name , id;
	JPasswordField pw;
	JButton submit;
	JPanel panel;
	JLabel ID, pass, Name;
	
	public SignScreen(boolean flag)   // 미완성 
	{
		//this.screen = screen;
		//this.send = s;
		//act = flag;
		frame = new JFrame("회원 가입");
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
				//if(act)
				//requestlogin();
			}
		});
		panel.add(id);
		panel.add(pw);
		
		submit= new JButton("Login");
		submit.setBounds(280,30,80,60);
		panel.add(submit);
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//if(act)
				//requestlogin();
			}
		});
		
	
		frame.setVisible(flag);
	}
	
	public void screenOn(boolean flag)
	{
		this.frame.setVisible(flag);
	}
}
