package clientSide;
import java.io.BufferedWriter;

import java.io.IOException;
import java.net.*;
import java.nio.channels.SocketChannel;
public class ClientMain {

	public static void main(String[] args) throws IOException {
		final String ip = "192.168.123.105";   // ��Ʈ�� ���� ip 
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
			// �ϴ� ���Ƿ� ���� ȣ��Ʈ�� ��Ʈ ��ȣ ���� , ���߿��� ������ �ƴ϶� �ܺ� ip , �׸��� 
			// ��Ʈ ��ȣ�� ���� �Է� �޴��� �ؾߵ�.
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
