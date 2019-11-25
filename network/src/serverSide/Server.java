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

import UI.LoginScreen;

public class Server {
	private ByteBuffer buffer = ByteBuffer.allocate(1024);
	Map<SocketChannel, List<byte[]>> clientChannel = new HashMap<>();   // ä�θ��� byte �迭 ���� �ʿ� -> �޼��� �� �Գ� �����
	Map<String, Client> clientInfo = new HashMap<>();
	public void start() throws IOException 
	{
		final int default_port = 9999;     // �ϴ��� ���Ƿ� ����     // �⺻���� �޼����� �̰ɷ� ���� , ��� ȭ�� ������ �ٸ� ���μ��� or �ٸ� ������ ��� 
		
		// Ŭ���̾�Ʈ ���̵����� 
		Client c1 = new Client("���¼�","client","1234");
		Client c2 = new Client("��ö��", "admin", "2323");
		clientInfo.put("client", c1);
		clientInfo.put("admin",c2);
		
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
			{
				System.out.println("main server open fail");
				return;
			}
			
			msc.configureBlocking(false);
			// �ɼǰ�
			msc.setOption(StandardSocketOptions.SO_RCVBUF, 128*1024);
			msc.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			
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
	public boolean close(ServerSocketChannel serverS , Selector select ) 
	{
		boolean flag = true;
		try {
			select.close();
			serverS.close();
		} catch (IOException e) {
			e.printStackTrace();
			return !flag;
		}
		
		return flag;
	}

	private void acceptKey(SelectionKey key, Selector selector) throws IOException {  // accept �����Ҷ� 
		// TODO Auto-generated method stub
		ServerSocketChannel serverC = (ServerSocketChannel) key.channel();
		SocketChannel socketC = serverC.accept();
		socketC.configureBlocking(false);
		System.out.println("Ŭ���̾�Ʈ �ּ� : " + socketC.getRemoteAddress());
		//socketC.write(ByteBuffer.wrap("������ ���ӵǼ̽��ϴ�.".getBytes("UTF-8")));
		socketC.register(selector, SelectionKey.OP_READ);
		
		clientChannel.put(socketC, new ArrayList<byte[]>());
	}
	private void readKey(SelectionKey key) throws IOException  // read �����Ҷ� 
	{
		String input="" ,sub;
		int readNum = -1;
		try {
			SocketChannel socketC = (SocketChannel)key.channel();
			buffer.clear();
			readNum = socketC.read(buffer);
			if(readNum == -1)
			{
				//System.out.println("read close");
				this.clientChannel.remove(socketC);
				
				socketC.close();
				key.cancel();
				return;
			}
			else 
			{
				input = new String(buffer.array(),"UTF-8");
				//System.out.println("���� : "+size+" ���� �� : "+ input + " by "+ socketC.getRemoteAddress());
				sub = input.substring(0, 4);
				//System.out.println(sub);
				if(sub.equals("[lp]"))  // ��Ŷ �˻��ϱ� (�α������� �ƴ���)
				{
					checkLogin(input,key);
				}
				else if(sub.equals("[sp]"))  // ȸ���������� �ƴ���    signin packet
				{
					signIn(input, key);
				}
				else if(sub.equals("[rp]")) // �� ���� ��û �ڵ� 
				{
					
				}
				else if(sub.equals("[cp]"))
				{
					// �ٸ� Ŭ���̾�Ʈ���� ���� �޼��� ������ broadcast or multicast
					System.out.println(" ���� �� : "+ input + " by "+ socketC.getRemoteAddress());
					sendMessage( key, buffer.array());
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.err.println(e);
			System.exit(1);    // -> �̰� �����  Ŭ���̾�Ʈ���� â ������ ��� �����޼��� ��µ� 
		}
	}
	private void writeKey(SelectionKey key) throws IOException   // write �� �����Ҷ�
	{
		SocketChannel socketC = (SocketChannel) key.channel();
		List<byte[]> clientData = clientChannel.get(socketC);
		Iterator<byte[]> it = clientData.iterator();
		while(it.hasNext()==true)
		{
			byte[] temp = it.next();
			System.out.println("send to client -> "+ new String(temp));  // �� �������� üũ�� 
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
	
	
	public void checkLogin(String input ,SelectionKey key )
	{
		SocketChannel socketC = (SocketChannel)key.channel();
		String loginAcceptCode = "[[login success!!]]";
		String loginRejectCode = "[[login fail!!]]";
		String dummy = input.split(":")[0];
		String id = input.split(":")[1];
		String pw = input.split(":")[2];
		System.out.println("�α��� ��û -> �Է¹��� ID : "+ id + " �Է¹��� PW : "+ pw);	
		
		// �ؽ� �ʿ��� Ŭ���̾�Ʈ �ִ��� ã�ƺ���. 
		if(clientInfo.containsKey(id)&&clientInfo.get(id).getPw().equals(pw))            // Ŭ���̾�Ʈ ������    �̰� �̻��� 
		{
			// ���� �ȵ��� ��   -> Ŭ���̾�Ʈ���� �α��� ���� �ִ� �ڵ� 
			List<byte[]> updateClientData = clientChannel.get(socketC);
			updateClientData.add(loginAcceptCode.getBytes());
			key.interestOps(SelectionKey.OP_WRITE);
		}
		else  // ������ 
		{
			List<byte[]> updateClientData = clientChannel.get(socketC);
			updateClientData.add(loginRejectCode.getBytes());
			key.interestOps(SelectionKey.OP_WRITE);
		}
	}
	public void signIn(String input , SelectionKey key)
	{
		
	}
	public void enterRoom()
	{
		
	}
}
