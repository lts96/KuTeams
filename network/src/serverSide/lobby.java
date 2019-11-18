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
		//System.out.println("서버 포트 번호 입력 : ");
		
		InetAddress local = InetAddress.getLocalHost();
		System.out.println("main server IP : " + local.getHostAddress());
		ServerSocketChannel msc = ServerSocketChannel.open();
		Selector selector = Selector.open();
		// msc는 메인 소켓 채널의 약자
		if(!msc.isOpen())
			System.out.println("main server open fail");
        msc.configureBlocking(false);
        msc.register(selector, SelectionKey.OP_ACCEPT,null);
        
        ServerSocket mainSocket = msc.socket();
        //mainSocket.bind(new InetSocketAddress(local , default_port));
        while(true)
        {
        	System.out.println("클라이언트 연결 기다리는중... "); 
        	Socket socket = mainSocket.accept();
        	System.out.println("클라이언트 주소 : " + socket.getRemoteSocketAddress());
        }
	}

}
