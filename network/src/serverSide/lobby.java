package serverSide;
import java.io.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.net.*;
import java.util.Scanner;

import clientSide.LoginScreen;

public class lobby {
	public static void main(String[] args) throws IOException 
	{
		int default_port = 7777;
		System.out.println("[main server start!!]");
		//System.out.println("���� ��Ʈ ��ȣ �Է� : ");
		
		InetAddress local = InetAddress.getLocalHost();
		System.out.println("main server IP : " + local.getHostAddress());
		ServerSocketChannel msc = ServerSocketChannel.open();
		Selector selector = Selector.open();
		// msc�� ���� ���� ä���� ����
		if(!msc.isOpen())
			System.out.println("main server open fail");
        msc.configureBlocking(false);
        msc.register(selector, SelectionKey.OP_ACCEPT,null);
        
        ServerSocket mainSocket = msc.socket();
        //mainSocket.bind(new InetSocketAddress(local , default_port));
        while(true)
        {
        	System.out.println("Ŭ���̾�Ʈ ���� ��ٸ�����... "); 
        	Socket socket = mainSocket.accept();
        	System.out.println("Ŭ���̾�Ʈ �ּ� : " + socket.getRemoteSocketAddress());
        }
	}

}
