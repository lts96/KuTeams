package clientSide;
import java.io.BufferedWriter;

import java.io.IOException;
import java.net.*;
import java.nio.channels.SocketChannel;
public class ClientMain {

	public static void main(String[] args) throws IOException {
		final String ip = "192.168.123.105";   // 노트북 로컬 ip 
		final int default_port = 7777;
		SocketChannel sc = SocketChannel.open();
		if(!sc.isOpen())
		{
			System.out.println("client socketChannel open fail!");
			return;
		}
		BufferedWriter write;
		Sender sender = new Sender(sc);

		
		try {
			sc.connect(new java.net.InetSocketAddress(ip, default_port));
			// 일단 임의로 로컬 호스트랑 포트 번호 지정 , 나중에는 로컬이 아니라 외부 ip , 그리고 
			// 포트 번호도 따로 입력 받던가 해야됨.
			LoginScreen login = new LoginScreen(true, sender);
			
			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
			//System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			//System.exit(1);
		}
		
	}
}
