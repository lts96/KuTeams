package UI;
import javax.swing.*;
import java.awt.*;
public class PrintInfoScreen 
{
	private JFrame frame;
	public PrintInfoScreen()
	{
		
	}
	public PrintInfoScreen(boolean flag) // 미완 
	{
		frame = new JFrame("의 회원 정보");
		JPanel panel = new JPanel();
		panel.setLayout(null);
		frame.add(panel);
		frame.setBounds(300, 200, 400, 400);
		JLabel userName = new JLabel("이름 :   ");
		userName.setBounds(30, 30, 150, 40);
		JLabel userId = new JLabel("ID :  ");
		userId.setBounds(30, 90, 150, 40);
		JLabel userPw = new JLabel("PW :   ");
		userPw.setBounds(30, 150, 150, 40);
		JLabel userSocketAddr = new JLabel("Addr :  ");
		userSocketAddr.setBounds(30, 210, 250, 40);
		JLabel userCode = new JLabel("RoomCode :  ");
		userCode.setBounds(30, 270, 150, 40);
		
		panel.add(userCode);
		panel.add(userSocketAddr);
		panel.add(userPw);
		panel.add(userId);
		panel.add(userName);
		
		
		frame.setVisible(flag);
		panel.setVisible(true);
	}
	public void screenOn(boolean flag)
	{
		this.frame.setVisible(flag);
	}
}
