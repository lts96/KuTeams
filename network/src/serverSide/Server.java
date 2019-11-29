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
import java.util.Map.Entry;
import java.util.Scanner;

import UI.LoginScreen;

public class Server {
	private static int roomCount = 0;
	private ByteBuffer buffer = ByteBuffer.allocate(1024);
	Map<SocketChannel, List<byte[]>> clientChannel = new HashMap<>();   // ä�θ��� byte �迭 ���� �ʿ� -> �޼��� �� �Գ� �����
	List<Client> clientlist = new ArrayList<>();
	List<Room> roomList = new ArrayList<>();
	ServerSocketChannel msc = null;
	Selector selector = null;
	public void start() throws IOException 
	{
		final int default_port = 8888;     // �ϴ��� ���Ƿ� ����     // �⺻���� �޼����� �̰ɷ� ���� , ��� ȭ�� ������ �ٸ� ���μ��� or �ٸ� ������ ��� 
		
		// Ŭ���̾�Ʈ ���̵����� 
		Client c1 = new Client("���¼�","client","1234");
		Client c2 = new Client("��ö��", "admin", "2323");
		Client c3 = new Client("��", "john","3333");
		clientlist.add(c1);
		clientlist.add(c2);
		clientlist.add(c3);
		
		// �� ���̵����� 
		Room r1 = new Room(-1,"test ��","��ö��" ,3);
		
		
		System.out.println("[main server start!!]");
		//System.out.println("���� ��Ʈ ��ȣ �Է� : ");
		try
		{
			InetAddress local = InetAddress.getLocalHost();
			System.out.println("main server IP : " + local.getHostAddress());  // Ȯ�ο� 
			msc = ServerSocketChannel.open();
			selector = Selector.open();
			// msc�� ���� ���� ä���� ����
			if(!msc.isOpen())
			{
				System.out.println("main server open fail");
				return;
			}
			
			msc.configureBlocking(false);
			// �ɼǰ�
			msc.setOption(StandardSocketOptions.SO_RCVBUF, 256*1024);
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
			if(msc.isOpen())
			{
				closeServer();
			}
		}
	}
	public void closeServer() throws IOException {
		Iterator<Entry<SocketChannel, List<byte[]>>> entries = clientChannel.entrySet().iterator();
		while (entries.hasNext()) 
		{
			Entry<SocketChannel, List<byte[]>> entry = entries.next();
		    SocketChannel key = (SocketChannel) entry.getKey();
		    key.close();
		}
		if(msc != null && msc.isOpen())
			msc.close();
		if(selector != null && selector.isOpen())
			selector.close();
		System.out.println("Server down!!");
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
				//System.out.println("���� �� : "+ input + " by "+ socketC.getRemoteAddress());
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
				else if (sub.equals("[rc]"))  // room create ok
				{
					Room r = createRoom(input , key);
					if(r != null)
					{
						roomList.add(r);
						sendMsgToClient("[[rc success]]", key);
					}
					else 
					{
						sendMsgToClient("[[rc fail]]", key);
					}
				}
				
				else if(sub.equals("[ra]")) // �� ���� ��û �ڵ�   room access
				{
					
				}
				else if(sub.equals("[cp]"))
				{
					// �ٸ� Ŭ���̾�Ʈ���� ���� �޼��� ������ broadcast or multicast
					System.out.println("���� �� : "+ input + " by "+ socketC.getRemoteAddress());
					sendMessage(key, input);  // -> ������Ʈ ���� ��
				}
				buffer.compact();    // ���� �����÷ο� ����?
				buffer.clear();
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
			System.out.println("send to "+ socketC.getRemoteAddress() +" "+ new String(temp));  // �� �������� üũ�� 
			socketC.write(ByteBuffer.wrap(temp));
			it.remove();
		}
		key.interestOps(SelectionKey.OP_READ);
	}
	public void sendMessage(SelectionKey key , String input) throws IOException       // ���� ������ �ٸ� Ŭ���̾�Ʈ�鿡�� ����   �̿ϼ� 
	{
		SocketChannel socketC = (SocketChannel) key.channel();
		//String dummy = input.split(":")[0];
		String chatText = input;
		//String dummy2 = input.split(":")[2];
		Client sender = null;
		
		
		System.out.println("���� �޼��� : "+ chatText + " ���� : "+chatText.length());
		for(int i=0;i<clientlist.size();i++)   // ���� �ȵ��� �� 
		{
			if(socketC.equals(clientlist.get(i).getSocketChannel()))
			{
				sender = clientlist.get(i);
				break;
			}
		}
		for(int i=0;i<clientlist.size();i++)
		{
			// �ڱ� �ڽſ��� �������ϴ°� ����
			if((clientlist.get(i).getRoomCode() == sender.getRoomCode()) && (clientlist.get(i).isOnline()&& !(clientlist.get(i).getId().equals(sender.getId()))))
			{
				List<byte[]> updateClientData = clientChannel.get(clientlist.get(i).getSocketChannel());
				updateClientData.add(chatText.getBytes());
				key.interestOps(SelectionKey.OP_WRITE);
				selector.wakeup();
			}
		}
	}
	public void sendMsgToClient(String str , SelectionKey key)
	{
		SocketChannel socketC = (SocketChannel)key.channel();
		List<byte[]> updateClientData = clientChannel.get(socketC);
		updateClientData.add(str.getBytes());
		key.interestOps(SelectionKey.OP_WRITE);
	}
	public void sendRoomList(SelectionKey key)
	{
		String list="";
		for(int i=0;i<roomList.size();i++)
		{
			list= list + roomList.get(i).getRoomName()+ ":";
		}
		System.out.println("���� room name list : "+ list);
		SocketChannel socketC = (SocketChannel) key.channel();
		List<byte[]> updateClientData = clientChannel.get(socketC);
		updateClientData.add(list.getBytes());
		key.interestOps(SelectionKey.OP_WRITE);
	}
	public void checkLogin(String input ,SelectionKey key ) throws IOException
	{
		boolean flag = false;
		SocketChannel socketC = (SocketChannel)key.channel();
		String loginAcceptCode = "[[login success]]";
		String loginRejectCode = "[[login fail!!!]]";
		String dummy = input.split(":")[0];
		String id = input.split(":")[1];
		String pw = input.split(":")[2];
		System.out.println("�α��� ��û -> �Է¹��� ID : "+ id + " �Է¹��� PW : "+ pw);	
		
		// Ŭ���̾�Ʈ �ִ��� ã�ƺ���  ������ id , pw �� 
		for(int i=0;i<clientlist.size();i++)
		{
			if(clientlist.get(i).getId().equals(id)&&clientlist.get(i).getPw().equals(pw))           
			{
				// Ŭ���̾�Ʈ���� �α��� ���� �ִ� �ڵ� 
				Client c = clientlist.get(i);
				c.setSocketChannel(socketC);     // Ŭ���̾�Ʈ address ���� 
				c.setOnline(true);
				List<byte[]> updateClientData = clientChannel.get(socketC);
				updateClientData.add(loginAcceptCode.getBytes());
				key.interestOps(SelectionKey.OP_WRITE);
				flag = true;
				break;
			}
		}
		if(flag==false)  // �α��� ���н� 
		{
			List<byte[]> updateClientData = clientChannel.get(socketC);
			updateClientData.add(loginRejectCode.getBytes());
			key.interestOps(SelectionKey.OP_WRITE);
		}
	}
	public void signIn(String input , SelectionKey key)
	{
		
	}
	public Room createRoom(String input ,SelectionKey key)
	{
		Room r;
		SocketChannel socketC = (SocketChannel)key.channel();
		String dummy = input.split(":")[0];
		String rname = input.split(":")[1];
		String tname = input.split(":")[2];
		String limit = input.split(":")[3];
		int num = Integer.parseInt(limit); 
		System.out.println("�� ���� ��û : "+ rname + " : "+ tname +" : "+ num);
		r = new Room(roomCount+1 , rname , tname , num);
		roomCount++;
		return r; 
	}
}
