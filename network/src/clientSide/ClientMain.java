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
		// 일단 임의로 로컬 호스트랑 포트 번호 지정 , 나중에는 로컬이 아니라 외부 ip , 그리고 
		// 포트 번호도 따로 입력 받던가 해야됨.
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
					while(true)           // ui 쓰레드에 막혀서 selector가 안돌고 있는거 같음 -> 따로 쓰레드 만들어서 돌려야됨 
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
									System.out.println("서버 or selector 가 열리지 않았음!");
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
			//pis = new PrintInfoScreen("이태선" , "client" , "1234" , "127.0.0.1", 999);
			//rs = new RoomScreen("테스트용","아무개",4, sender ,recv);
			
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
				System.out.println("길이 : "+input.length()+" 읽은 값 : "+ input + " by "+ socketC.getRemoteAddress());
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
			System.exit(1);    // -> 이거 지우면  클라이언트에서 창 닫을때 계속 에러메세지 출력됨 
		}
		
	}
	
}
