package clientSide;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import UI.LoginScreen;
import UI.PrintInfoScreen;
import UI.RoomScreen;
import UI.ChatScreen;
import UI.CreateRoomScreen;
import UI.LobbyScreen;
public class ClientMain {
	private static ByteBuffer buffer = ByteBuffer.allocate(1024);
	private static ChatScreen chat;
	private static LoginScreen login;
	private static LobbyScreen lobby;
	private static CreateRoomScreen crs;
	private static PrintInfoScreen pis;
	private static RoomScreen rs;
	public static int roomCode = -1;
	private static String ip = "";
	public static void main(String[] args) throws IOException {
		InetAddress local = InetAddress.getLocalHost();
		ip = local.getHostAddress();   // ��Ʈ�� ���� ip 
		final int default_port = 8888;
		// �ϴ� ���Ƿ� ���� ȣ��Ʈ�� ��Ʈ ��ȣ ���� , ���߿��� ������ �ƴ϶� �ܺ� ip , �׸��� 
		// ��Ʈ ��ȣ�� ���� �Է� �޴��� �ؾߵ�.
		Selector selector = Selector.open();
		if(!selector.isOpen())
		{
			System.out.println("client selector open fail!");
			return;
		}
		SocketAddress sa = new InetSocketAddress(ip, default_port);
		SocketChannel sc = SocketChannel.open(sa);
		DatagramSocket udpSocket = new DatagramSocket();
		System.out.println("Client UDP socket port : "+udpSocket.getLocalPort());
		if(!sc.isOpen())
		{
			System.out.println("client socketChannel open fail!");
			return;
		}
		try {
			sc.configureBlocking(false);
			sc.setOption(StandardSocketOptions.SO_RCVBUF, 128*1024);
			sc.setOption(StandardSocketOptions.SO_SNDBUF, 128*1024);
			sc.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
			sc.register(selector, SelectionKey.OP_CONNECT);
			//sc.connect(new java.net.InetSocketAddress(ip, default_port));
			if(!sc.isConnected())
				System.out.println("������ ä�� ���� ����!");
			Thread thread = new Thread() {
				public void run()
				{
					Sender send = new Sender(sc , udpSocket);
					rs = new RoomScreen(false, send);
					chat = new ChatScreen(false, send);
					crs = new CreateRoomScreen(false , send);
					lobby = new LobbyScreen(false, send , crs);
					login = new LoginScreen(true , send , lobby);
				}

			};
			thread.start();
			// receive �ϴ� �κ� 
			SocketChannel socketC = sc;
			while(true)
			{
				String input,sub;
				String parse;
				int readNum = -1;
				try {
					readNum = socketC.read(buffer);
					if(readNum == -1)
					{
						return;
					}
					else
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
								rs.screenOn(true);
							}
							else
							{
								lobby.failRoomAccess(input);
							}
						}
						else if(sub.equals("[cp]"))
						{
							System.out.println("���� �� : "+ input + " by "+ socketC.getRemoteAddress());
							if(chat.isAct())
							{
								chat.recvChat(input);
							}
						}
						buffer.compact();    // ���� �ʱ�ȭ �ؾߵ�  �� ��� ���� 
						buffer.clear();
					}
				}
				catch (Exception e) {
					// TODO: handle exception
					System.err.println(e);
					break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			//System.exit(1);
		}
	}
	public String getIp()
	{
		return ip;
	}
}
