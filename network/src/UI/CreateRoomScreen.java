package UI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import clientSide.Sender;
public class CreateRoomScreen 
{
	private JFrame frame;
	private JButton submit;
	private JTextField rName, tName , sNum;
	private JPanel panel;
	private JLabel roomName , teacher , studentNum;
	private Sender send;
	public CreateRoomScreen(boolean flag ,Sender s)   // �ߵ�.
	{
		this.send = s;
		frame = new JFrame("�� �����");
		frame.setBounds(300, 200, 400, 300);
		panel = new JPanel();
		panel.setLayout(null);
		frame.add(panel);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.setVisible(false);
			}
		});
		
		roomName = new JLabel("�� ����");
		roomName.setBounds(30,20, 60,25);
		teacher = new JLabel("���� �̸�");
		teacher.setBounds(30,60, 60,25);
		studentNum = new JLabel("�ο� ����");
		studentNum.setBounds(30,100 ,60,25);
		panel.add(roomName);
		panel.add(teacher);
		panel.add(studentNum);
		
		submit = new JButton("����");
		submit.setBounds(280, 40, 60, 60);
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				submitRoom();
			}
		});
		panel.add(submit);
		
		rName = new JTextField();
		rName.setBounds(100,20, 120, 25);
		tName = new JTextField();
		tName.setBounds(100,60, 120, 25);
		sNum = new JTextField();
		sNum.setBounds(100,100, 120, 25);
		
		panel.add(rName);
		panel.add(tName);
		panel.add(sNum);
		
		frame.setVisible(flag);
		panel.setVisible(true);
	}
	public void submitRoom()
	{
		String str = "[rc]:"+rName.getText()+":"+tName.getText()+":"+sNum.getText()+":";
		send.sendString(str);
	}
	public boolean recvMsg(String str)
	{
		if(str.contains("[[rc success]]"))
		{
			JOptionPane.showMessageDialog(null, "�� ���� ����!");
			frame.dispose();
			return true;
		}
		else 
		{
			JOptionPane.showMessageDialog(null, "�� ���� ����!");
			return false;
		}
	}
	public void screenOn(boolean flag)
	{
		this.frame.setVisible(flag);
	}
}
