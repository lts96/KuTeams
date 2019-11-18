package serverSide;
import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;



import clientSide.LoginScreen;

public class Lobby {
	
	private ByteBuffer buffer = ByteBuffer.allocate(1024);
	Map<SocketChannel, List<byte[]>> client = new HashMap<>();   // ä�θ��� byte �迭 ���� �ʿ� -> �޼��� �� �Գ� �����
	public void start() throws IOException 
	{
		int default_port = 7777;     // �ϴ��� ���Ƿ� ���� 
		System.out.println("[main server start!!]");
		//System.out.println("���� ��Ʈ ��ȣ �Է� : ");
		
		try
		{
			InetAddress local = InetAddress.getLocalHost();
			System.out.println("main server IP : " + local.getHostAddress());  // Ȯ�ο� 
			ServerSocketChannel msc = ServerSocketChannel.open();
			Selector selector = Selector.open();
			// msc�� ���� ���� ä���� ����
			if(!msc.isOpen())
				System.out.println("main server open fail");
			else 
				msc.configureBlocking(false);
			msc.bind(new InetSocketAddress(local , default_port));
        	msc.register(selector, SelectionKey.OP_ACCEPT);
        	System.out.println("Ŭ���̾�Ʈ ���� ��ٸ�����... "); 
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
            		if(key.isAcceptable()) 
            		{
            			this.acceptKey(key,selector);
            		}
            		else if(key.isReadable())
            		{
            			this.readKey(key);
            		}
            		else if(key.isWritable())
            		{
            			this.writeKey(key);
            		}
            		else 
            			System.out.println("���� or selector �� ������ �ʾ���!");
            		keys.remove();
        		}
        			
        	}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			//System.exit(1);
		}
	}

	private void acceptKey(SelectionKey key, Selector selector) throws IOException {  // accept �����Ҷ� 
		// TODO Auto-generated method stub
		ServerSocketChannel serverC = (ServerSocketChannel) key.channel();
		SocketChannel socketC = serverC.accept();
		socketC.configureBlocking(false);
		System.out.println("Ŭ���̾�Ʈ �ּ� : " + socketC.getRemoteAddress());
		socketC.write(ByteBuffer.wrap("������ ���ӵǼ̽��ϴ�.".getBytes("UTF-8")));
		socketC.register(selector, SelectionKey.OP_READ);
		
		client.put(socketC, new ArrayList<byte[]>());
	}
	private void readKey(SelectionKey key) throws IOException  // read �����Ҷ� 
	{
		String input;
		int readNum = -1;
		try {
			SocketChannel socketC = (SocketChannel)key.channel();
			buffer.clear();
			readNum = socketC.read(buffer);
			if(readNum == -1)
			{
				//System.out.println("read close");
				this.client.remove(socketC);
				
				socketC.close();
				key.cancel();
				return;
			}
			else 
			{
				input = new String(buffer.array(),"UTF-8");
				System.out.println("���� �� : "+ input + " by "+ socketC.getRemoteAddress());
				// �ٸ� Ŭ���̾�Ʈ���� ���� �޼��� ������ 
				sendMessage( key, buffer.array());
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.err.println(e);
			System.exit(1);     // -> �̰� �����  Ŭ���̾�Ʈ���� â ������ ��� �����޼��� ��µ� 
		}
	}
	private void writeKey(SelectionKey key) throws IOException   // write �� �����Ҷ�
	{
		SocketChannel socketC = (SocketChannel) key.channel();
		List<byte[]> clientData = client.get(socketC);
		Iterator<byte[]> it = clientData.iterator();
		while(it.hasNext()==true)
		{
			byte[] temp = it.next();
			socketC.write(ByteBuffer.wrap(temp));
			it.remove();
		}
		key.interestOps(SelectionKey.OP_READ);
	}
	private void sendMessage(SelectionKey key , byte[] data )       // ���� ������ �ٸ� Ŭ���̾�Ʈ�鿡�� ����   �̿ϼ� 
	{
		SocketChannel socketC = (SocketChannel) key.channel();
		
		key.interestOps(SelectionKey.OP_WRITE);
	}

}
