package UI;
import javax.swing.*;
import java.awt.*;
public class PrintInfoScreen 
{
	private JFrame frame;
	public PrintInfoScreen()
	{
		
	}
	public PrintInfoScreen(String name , String id , String pw , String socketAddr , int time)
	{
		frame = new JFrame(name+"의 회원 정보");
		JPanel panel = new JPanel();
		panel.setLayout(null);
		frame.add(panel);
		frame.setBounds(300, 200, 400, 400);
		JLabel userName = new JLabel("이름 :   "+ name);
		userName.setBounds(30, 30, 150, 40);
		JLabel userId = new JLabel("ID :  "+ id);
		userId.setBounds(30, 90, 150, 40);
		JLabel userPw = new JLabel("PW :   "+ pw);
		userPw.setBounds(30, 150, 150, 40);
		JLabel userSocketAddr = new JLabel("Addr :  "+ socketAddr);
		userSocketAddr.setBounds(30, 210, 250, 40);
		JLabel userTime = new JLabel("총 수강시간 :  "+ time);
		userTime.setBounds(30, 270, 150, 40);
		
		panel.add(userTime);
		panel.add(userSocketAddr);
		panel.add(userPw);
		panel.add(userId);
		panel.add(userName);
		
		
		frame.setVisible(true);
		panel.setVisible(true);
	}
	public void screenOn(boolean flag)
	{
		this.frame.setVisible(flag);
	}
}
