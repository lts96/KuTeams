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

import UI.LoginScreen;
import UI.PrintInfoScreen;
import UI.RoomScreen;
import UI.CreateRoomScreen;
import UI.LobbyScreen;
public class ClientMain {
	private static ByteBuffer buffer = ByteBuffer.allocate(1024);
	public static void main(String[] args) throws IOException {
		InetAddress local = InetAddress.getLocalHost();
		final String ip = local.getHostAddress();   // ��Ʈ�� ���� ip 
		final int default_port = 9999;
		// �ϴ� ���Ƿ� ���� ȣ��Ʈ�� ��Ʈ ��ȣ ���� , ���߿��� ������ �ƴ϶� �ܺ� ip , �׸��� 
		// ��Ʈ ��ȣ�� ���� �Է� �޴��� �ؾߵ�.
		SocketChannel sc = SocketChannel.open();
		Selector selector = Selector.open();
		LoginScreen login;
		LobbyScreen lobby;
		CreateRoomScreen crs;
		PrintInfoScreen pis;
		RoomScreen rs;
		if(!sc.isOpen())
		{
			System.out.println("client socketChannel open fail!");
			return;
		}
		Sender sender = new Sender(sc);
		Receiver recv = new Receiver(sc);
		
		try {
			sc.connect(new java.net.InetSocketAddress(ip, default_port));
			sc.configureBlocking(false);
			sc.setOption(StandardSocketOptions.SO_RCVBUF, 128*1024);

			Thread thread = new Thread() 
			{
				public void run()
				{
					while(true)           // ui �����忡 ������ selector�� �ȵ��� �ִ°� ���� -> ���� ������ ���� �����ߵ� 
					{
						try {
							int count = selector.select();
							if(count == 0)
								continue;
							Iterator keys = selector.selectedKeys().iterator();
							while(keys.hasNext()==true)
							{
								SelectionKey key = (SelectionKey) keys.next();
								if(key.isValid()==false)
								{
									continue;
								}
								else if(key.isReadable())
								{
									readKey(key);
								}
								else if(key.isWritable())
								{
									writeKey(key);
								}
								else 
									System.out.println("���� or selector �� ������ �ʾ���!");
								keys.remove();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			};
			thread.start();	
			login = new LoginScreen(true, sender , recv);
			//lobby = new LobbyScreen(false, sender ,recv);
			//crs= new CreateRoomScreen();
			//pis = new PrintInfoScreen("���¼�" , "client" , "1234" , "127.0.0.1", 999);
			//rs = new RoomScreen("�׽�Ʈ��","�ƹ���",4, sender ,recv);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
			//System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			//System.exit(1);
		}
		
	}
    
	private static void writeKey(SelectionKey key) {
		
	}

	private static void readKey(SelectionKey key) {
		String input, parse , sub;
		int readNum = -1;
		try {
			SocketChannel socketC = (SocketChannel)key.channel();
			buffer.clear();
			readNum = socketC.read(buffer);
			if(readNum == -1)
			{
				//System.out.println("read close");
				socketC.close();
				key.cancel();
				return;
			}
			else 
			{
				input = new String(buffer.array(),"UTF-8");
				System.out.println("���� : "+input.length()+" ���� �� : "+ input + " by "+ socketC.getRemoteAddress());
				sub = input.substring(0, 4);
				parse = input.split(" ")[0];
				if(sub.equals("[cp]"))
				{
					
				}
				else if(sub.equals("[[lo"))
				{
					
				}
			}
		}
		catch (Exception e) {
			System.err.println(e);
			System.exit(1);    // -> �̰� �����  Ŭ���̾�Ʈ���� â ������ ��� �����޼��� ��µ� 
		}
		
	}
	
}
