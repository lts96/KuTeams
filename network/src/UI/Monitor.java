package UI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import clientSide.ClientMain;
import clientSide.Sender;
public class Monitor {
	private JFrame frame;
	private JPanel panel;
	private boolean act;
	private Thread print;
	final int w = 720, h = 480;
	private BufferedImage image;
	public Monitor()
	{
		
	}
	public Monitor(boolean flag) //
	{
		int x = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - w / 2;
		int y = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - h / 2;
		this.act = flag;
		frame = new JFrame("모니터");
		frame.setBounds(x, y,w, h);
		frame.setLayout(null);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				screenOn(false);
			}
		});
		frame.setVisible(flag);
	}
	
	public void recvScreen(BufferedImage image)
	{
		this.image = image;
	}
	public void printScreen()
	{
		print = new Thread() {
			public void run()
			{
				while(act) 
				{
					if(image != null)
					frame.getGraphics().drawImage(image, 0,0,w,h, frame);
				}
			}
		};
		print.start();
	}
	public void screenOn(boolean flag)
	{
		this.act = flag;
		this.frame.setVisible(flag);
	}
	public boolean isAct()
	{
		return this.act;
	}
	public void setTitle()
	{
		frame.setTitle(ClientMain.clientName+"의 모니터");
	}
}
