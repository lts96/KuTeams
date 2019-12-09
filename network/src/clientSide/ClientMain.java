package clientSide;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import UI.LoginScreen;
import UI.PrintInfoScreen;
import UI.Monitor;
import UI.Camera;
import UI.ChatScreen;
import UI.CreateRoomScreen;
import UI.LobbyScreen;
public class ClientMain {
	private static ByteBuffer buffer = ByteBuffer.allocate(100000);
	private static ChatScreen chat;
	private static LoginScreen login;
	private static LobbyScreen lobby;
	private static CreateRoomScreen crs;
	private static PrintInfoScreen pis;
	private static Camera cam;
	private static Monitor monitor;
	public static int roomCode = -1;
	private static String ip = "";
	public static void main(String[] args) throws IOException {
		InetAddress local = InetAddress.getLocalHost();
		ip = local.getHostAddress();   // ��Ʈ�� ���� ip 
		final int default_port = 8888;
		// �ϴ� ���Ƿ� ���� ȣ��Ʈ�� ��Ʈ ��ȣ ���� , ���߿��� ������ �ƴ϶� �ܺ� ip 
		// ��Ʈ ��ȣ�� ���� �Է� �޴��� �ؾߵ�.
		Selector selector = Selector.open();
		if(!selector.isOpen())
		{
			System.out.println("client selector open fail!");
			return;
		}
		SocketAddress sa = new InetSocketAddress(ip, default_port);
		SocketChannel sc = SocketChannel.open(sa);
		//DatagramSocket udpSocket = new DatagramSocket();
		//System.out.println("Client UDP socket port : "+udpSocket.getLocalPort());
		if(!sc.isOpen())
		{
			System.out.println("client socketChannel open fail!");
			return;
		}
		try {                                             // ���ŷ���� ���� -> ���� �߸� ���� ū�ϳ��� -> ��� ���ѷ��� ������ 
			sc.configureBlocking(true);
			sc.setOption(StandardSocketOptions.SO_RCVBUF, 64*1024*1024);
			sc.setOption(StandardSocketOptions.SO_SNDBUF, 64*1024*1024);
			//sc.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
			//sc.register(selector, SelectionKey.OP_CONNECT);
			
			//sc.connect(new java.net.InetSocketAddress(ip, default_port));
			if(!sc.isConnected())
				System.out.println("������ ä�� ���� ����!");
			Thread thread = new Thread() {
				public void run()
				{
					Sender send = new Sender(sc);
					cam = new Camera(send);
					monitor = new Monitor(false);
					chat = new ChatScreen(false, send , cam , monitor);
					crs = new CreateRoomScreen(false , send);
					lobby = new LobbyScreen(false, send , crs);
					login = new LoginScreen(true , send , lobby);
				}

			};
			thread.start();
			// receive �ϴ� �κ� 
			while(true)
			{
				String input,sub;
				String parse;
				int readNum = -1;
				try {
					readNum = sc.read(buffer);
					if(readNum == -1)
					{
						return;
					}
					else if(readNum > 0 && readNum < 1024)   // && readNum <=1024
					{
						input = new String(buffer.array(),"UTF-8");
						//System.out.println("���� : "+input.length()+" ���� �� : "+ input);
						//System.out.println("���� : "+sub.length()+"sub : "+ sub);
						sub = input.substring(0, 4);
						if(sub.equals("[[lo"))  // ��Ŷ �˻��ϱ� (�α������� �ƴ���)
						{
							sub = input.substring(0,17);
							login.recvLogin(input);
						}
						else if(sub.equals("[[si"))   // ȸ������ �˻� 
						{
							
						}
						else if (sub.equals("[[rc")) // �� ���� ��� 
						{
							crs.recvMsg(input);
						}
						else if(sub.equals("[ri]"))
						{
							lobby.addScreenInfo(input, true);
						}
						else if(sub.equals("[ru]")) // lobbyScreen update ���    �� �߰� �Ҷ� true  �� ���� �ɶ� false
						{
							lobby.updateScreenInfo(input, true);
						}
						else if(sub.equals("[[ra")) // �� ���� ��û ���
						{
							if(input.contains("success"))     // ä�ð� ȭ�� ���� ���� 
							{
								String token = input.split(":")[0];
								roomCode= Integer.parseInt(input.split(":")[1]);
								System.out.println("������ �� �ڵ� : "+ roomCode);
								chat.screenOn(true);
								//monitor.screenOn(false);
								//cam.screenOn(true);
								//cam.camera_work();
							}
							else
							{
								lobby.failRoomAccess(input);
							}
						}
						else if(sub.equals("[cp]"))
						{
							System.out.println("���� �� : "+ input + " by "+ sc.getRemoteAddress());
							if(chat.isAct())
							{
								chat.recvChat(input);
							}
						}
						buffer.compact();    // ���� �ʱ�ȭ �ؾߵ�  �� ��� ���� 
						buffer.clear();
					}
					else if (readNum >= 1024)   // �̹��� �б� 
					{
						if(monitor.isAct())
						{
							buffer.flip();
							System.out.println("Ŭ���̾�Ʈ�� ���� �̹��� ũ�� : "+ readNum);
							byte [] imageInByte= new byte[readNum];
							buffer.get(imageInByte,buffer.position(), buffer.limit());
							//System.out.println("buffer position : "+ buffer.position() +" buffer limit :" + buffer.limit());
							//System.out.println("image byte size : "+ imageInByte.length);
							BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageInByte));
							monitor.recvScreen(image);
						}			
				        //ImageIO.write(imag, "png", new File("C:\\Users\\s_dlxotjs\\Desktop\\��Ʈ��ũ ���α׷���", "img-client2.png"));
						buffer.compact();
						buffer.clear();
					}
				}
				catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					System.err.println(e);
					break;
				}
			}
			
		} 
		catch (IOException e) {
			e.printStackTrace();
			//System.exit(1);
		}
	}
	public String getIp()
	{
		return ip;
	}
	public void readImage()
	{
		
	}
}
