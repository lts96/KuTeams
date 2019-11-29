package UI;

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
	
	public SignScreen()
	{
		
	}
	public void screenOn(boolean flag)
	{
		this.frame.setVisible(flag);
	}
}
