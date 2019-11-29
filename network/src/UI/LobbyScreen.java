package UI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.*;
import clientSide.Sender;
public class LobbyScreen 
{
	private ArrayList<String> roomList = new ArrayList<String>();
	private JFrame frame;
	private JButton b1,b2,b3;
	private JPanel panel;
	private JLabel infor;
	private int x = 80;
	private CreateRoomScreen crs;
	private RoomScreen rs;
	private PrintInfoScreen pis;
	Sender s; 
	
	public LobbyScreen(boolean flag ,Sender s , CreateRoomScreen crs)  // �̿ϼ�.
	{
		this.crs = crs;
		this.s = s;
		frame = new JFrame("���� ȭ��");
		frame.setBounds(250, 150, 800, 600);
		//frame.setLayout(new FlowLayout());
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		panel = new JPanel();
		frame.add(panel);
		panel.setLayout(null);
		b1 = new JButton("���ǽ� ����");
		b1.setBounds(x, 10,150, 100);
		b2 = new JButton("���ǽ� ����"); 
		b2.setBounds(x+250, 10,150, 100);
		b3 = new JButton("�� ���� Ȯ��");
		b3.setBounds(x+500, 10,150, 100);
		infor = new JLabel("���� ������ ���� ���");
		infor.setBounds(x , 130 , 200 , 40);
		
		panel.add(infor);
		panel.add(b1);
		panel.add(b2);
		panel.add(b3);
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				crs.screenOn(true);
			}
		});
		
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enterRoom();
			}
		});
		
		b3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				printMyInfo();
			}
		});
		frame.setVisible(flag);
		panel.setVisible(flag);
	}
	public void enterRoom()
	{
		rs = new RoomScreen("�׽�Ʈ��","�ƹ���",4, s ); // ��� �׽�Ʈ ��
	}
	public void printMyInfo()
	{
		// �������׼� �� ���� �޾ƿ� -> ������ ���ؼ� �� ���� ������ִ� ȭ�� ���� 
		// pis = new PrintInfoScreen(String name , String id , String pw , String addr , int time);
		
	}
	public void screenOn(boolean flag)
	{
		this.frame.setVisible(flag);
		this.panel.setVisible(flag);
	}
}
