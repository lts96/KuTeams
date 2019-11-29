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
import UI.CreateRoomScreen;
import UI.LobbyScreen;
public class ClientMain {
	private static ByteBuffer buffer = ByteBuffer.allocate(1024);
	private static LoginScreen login;
	private static LobbyScreen lobby;
	private static CreateRoomScreen crs;
	private static PrintInfoScreen pis;
	private static RoomScreen rs;
	public static void main(String[] args) throws IOException {
		InetAddress local = InetAddress.getLocalHost();
		final String ip = local.getHostAddress();   // ��Ʈ�� ���� ip 
	
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
				System.out.println("������ ���� ����!");
			Thread thread = new Thread() {
				public void run()
				{
					
					Sender send = new Sender(sc);
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
					SocketChannel socketC = sc;
					readNum = socketC.read(buffer);
					if(readNum == -1)
					{
						return;
					}
					else
					{
						input = new String(buffer.array(),"UTF-8");
						//System.out.println("���� : "+input.length()+" ���� �� : "+ input);
						sub = input.substring(0, 4);
						if(sub.equals("[[lo"))  // ��Ŷ �˻��ϱ� (�α������� �ƴ���)
						{
							sub = input.substring(0,17);
							//System.out.println("���� : "+sub.length()+"sub : "+ sub);
							login.recvLogin(input);
						}
						else if(sub.equals("[[si"))   // ȸ������ �˻� 
						{
							
						}
						else if (sub.equals("[[rc")) // �� ���� �˷��ִ� �ڵ� 
						{
							crs.recvMsg(input);
						}
						else if(sub.equals("[rp]")) // �� ���� ��û �ڵ� 
						{
							
						}
						else if(sub.equals("[cp]"))
						{
							System.out.println("���� �� : "+ input + " by "+ socketC.getRemoteAddress());
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
}
