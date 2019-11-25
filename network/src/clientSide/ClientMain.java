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
		final String ip = local.getHostAddress();   // 노트북 로컬 ip 
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
			// 일단 임의로 로컬 호스트랑 포트 번호 지정 , 나중에는 로컬이 아니라 외부 ip , 그리고 
			// 포트 번호도 따로 입력 받던가 해야됨.
				
			//LoginScreen login = new LoginScreen(true, sender , receiver);
			//LobbyScreen ls = new LobbyScreen(true);
			//CreateRoomScreen crs= new CreateRoomScreen();
			//PrintInfoScreen pis = new PrintInfoScreen("이태선" , "client" , "1234" , "127.0.0.1", 999);
			RoomScreen rs = new RoomScreen("테스트용","아무개",4, sender ,receiver);
			// 옵션 설정
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
            			System.out.println("서버 or selector 가 열리지 않았음!");
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
				//System.out.println("길이 : "+size+" 읽은 값 : "+ input + " by "+ socketC.getRemoteAddress());
				sub = input.split(" ")[0];
				
			}
		}
		catch (Exception e) {
			System.err.println(e);
			System.exit(1);    // -> 이거 지우면  클라이언트에서 창 닫을때 계속 에러메세지 출력됨 
		}
		
	}
	
}
