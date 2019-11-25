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
		SocketChannel sc = SocketChannel.open();
		Selector selector = Selector.open();
		if(!sc.isOpen())
		{
			System.out.println("client socketChannel open fail!");
			return;
		}
		Sender sender = new Sender(sc);
		Receiver receiver = new Receiver(sc);
		
		try {
			sc.connect(new java.net.InetSocketAddress(ip, default_port));
			// �ϴ� ���Ƿ� ���� ȣ��Ʈ�� ��Ʈ ��ȣ ���� , ���߿��� ������ �ƴ϶� �ܺ� ip , �׸��� 
			// ��Ʈ ��ȣ�� ���� �Է� �޴��� �ؾߵ�.
				
			//LoginScreen login = new LoginScreen(true, sender , receiver);
			//LobbyScreen ls = new LobbyScreen(true);
			//CreateRoomScreen crs= new CreateRoomScreen();
			//PrintInfoScreen pis = new PrintInfoScreen("���¼�" , "client" , "1234" , "127.0.0.1", 999);
			RoomScreen rs = new RoomScreen("�׽�Ʈ��","�ƹ���",4, sender ,receiver);
			// �ɼ� ����
			sc.configureBlocking(false);
			sc.setOption(StandardSocketOptions.SO_RCVBUF, 128*1024);
			
        	while(true)
        	{
        		selector.select();
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
            			//writeKey(key);
            		}
            		else 
            			System.out.println("���� or selector �� ������ �ʾ���!");
            		keys.remove();
        		}
        			
        	}
			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
			//System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			//System.exit(1);
		}
		
	}
    
	private static void readKey(SelectionKey key) {
		String input, sub;
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
				//System.out.println("���� : "+size+" ���� �� : "+ input + " by "+ socketC.getRemoteAddress());
				sub = input.split(" ")[0];
				
			}
		}
		catch (Exception e) {
			System.err.println(e);
			System.exit(1);    // -> �̰� �����  Ŭ���̾�Ʈ���� â ������ ��� �����޼��� ��µ� 
		}
		
	}
	
}
