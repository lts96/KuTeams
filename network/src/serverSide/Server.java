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
	private static int roomCount = 1;
	private ByteBuffer buffer = ByteBuffer.allocate(1024);
	Map<SocketChannel, List<byte[]>> clientChannel = new HashMap<>();   // ä�θ��� byte �迭 ���� �ʿ� -> �޼��� �� �Գ� �����
	List<Client> clientlist = new ArrayList<>();
	List<Room> roomList = new ArrayList<>();
	ServerSocketChannel msc = null;
	Selector selector = null;
	DatagramSocket udpSocket;
	InetAddress local;
	public void start() 
	{
		final int default_port = 8888;     // �ϴ��� ���Ƿ� ����     // �⺻���� �޼����� �̰ɷ� ���� , ��� ȭ�� ������ �ٸ� ���μ��� or �ٸ� ������ ��� 
		// Ŭ���̾�Ʈ ���̵����� 
		Client c1 = new Client("���¼�","client","1234");
		Client c2 = new Client("��ö��", "admin", "2323");
		Client c3 = new Client("��", "3333","3333");
		//Client c4 = new Client("�׸�","4444","4444");
		//Client c5 = new Client("�ھ�", "5555", "5555");
		//Client c6 = new Client("�־�", "6666","6666");
		//Client c7 = new Client("�丮","7777","7777");
		//Client c8 = new Client("���ȳ�", "8888", "8888");
		//Client c9 = new Client("���̽�", "9999","9999");
		
		clientlist.add(c1);
		clientlist.add(c2);
		clientlist.add(c3);
		//clientlist.add(c4);
		//clientlist.add(c5);
		//clientlist.add(c6);
		//clientlist.add(c7);
		//clientlist.add(c8);
		//clientlist.add(c9);
		

		// �� ���̵����� 
		Room r1 = new Room(1,"test","��ö��" ,9);   // -> ����   3������ �����س��µ� �� 
		roomList.add(r1);
		
		System.out.println("[main server start!!]");
		//System.out.println("���� ��Ʈ ��ȣ �Է� : ");
		try
		{
			local = InetAddress.getLocalHost();
			System.out.println("main server IP : " + local.getHostAddress());  // Ȯ�ο� 
			msc = ServerSocketChannel.open();
			selector = Selector.open();
			
			udpSocket = new DatagramSocket(9999);  //udp�� ���� �ʿ���� �ټ��� Ŭ���̾�Ʈ ���� ���� 
			// ������ ��� �̹��� ������ �ְ� ���� ���ΰ� -> �̰� ����� ����� �� �ִ°� (�̰� Ŭ���̾�Ʈ ���)
			Thread image = new Thread() {
				public void run()
				{
					while(true)
					{
						receiveImage();
						sendImage();
						try {
							this.sleep(2000);
						} catch (InterruptedException e) {
							System.out.println("���� �߻�   ��ġ : ���� line 82");
						}
					}
				}
			};
			
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
        	
        	image.start();
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
			System.out.println("server.java 100 line catch");
			if(msc.isOpen())
			{
				try {
					closeServer();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("server.java 107 line catch");
				}
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
		String input="" ,token;
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
				token = input.substring(0, 4);
				//System.out.println(sub);
				//buffer.flip();
				if(token.equals("[lp]"))  // ��Ŷ �˻��ϱ� (�α������� �ƴ���)
				{
					checkLogin(input,key);
				}
				else if(token.equals("[sp]"))  // ȸ���������� �ƴ���    signin packet
				{
					signIn(input, key);
				}
				else if(token.equals("[ch]"))   // ä�� �޼��� 
				{
					// �ٸ� Ŭ���̾�Ʈ���� ���� �޼��� ������
					System.out.println("�޼��� : "+ input + " by "+ socketC.getRemoteAddress());
					sendMessage(key, input);
				}
				else if(token.equals("[cw]"))    // �ӼӸ� ó�� 
				{
					System.out.println("�޼��� : "+ input + " by "+ socketC.getRemoteAddress());
					sendMessage(key, input);
				}
				else if (token.equals("[rc]"))  // �� ���� ��û 
				{
					Room r = createRoom(input , key);
					if(r != null)   // ���� ����� �����Ǿ����� 
					{
						roomList.add(r);
						sendMsgToClient("[[rc success]]:"+r.getRoomCode()+":", key);
						sendMsgToClient("[ri]:"+r.getRoomName()+":"+r.getClientList().size()+":"+r.getLimit()+":",key);
						for(int i=0;i<clientlist.size();i++)
						{
							if(clientlist.get(i).getSocketChannel()!=null&& clientlist.get(i).getSocketChannel().equals(socketC))
							{
								clientlist.get(i).setRoomCode(r.getRoomCode());
								r.addClient(clientlist.get(i));
								System.out.println("Ŭ���̾�Ʈ "+ clientlist.get(i).getName()+"���� "+r.getRoomName()+" �濡 ����");
								break;
							}
						}
						// Ŭ���̾�Ʈ ��  �� ����Ʈ ���� ������Ʈ �� 
					}
					else  // ������ ���������� 
					{
						sendMsgToClient("[[rc fail]]", key);
					}
				}
				else if(token.equals("[ra]")) // �� ���� ��û �ڵ�   room access
				{
					int code = enterRoom(key,input);
					if(code > 0)
						sendMsgToClient("[[ra success]]:"+code +":", key);    // �� ���� ����
					else if (code == -1)       //
						sendMsgToClient("[[ra fail]] \n room is not exist:", key);   // ���� ���� -> �� ��ã��
					else if (code == -2)
						sendMsgToClient("[[ra fail]] \n room is already full:", key); // ���� ���� -> �ο� ���� �ɸ�
				}
				else if(token.equals("[rl]"))  // �� ����Ʈ ��û 
				{
					String str = "[ru]:"+roomList.size()+":";
					Room r;
					for(int i=0;i<roomList.size();i++)
					{
						r= roomList.get(i);
						str += r.getRoomName()+":"+r.getClientList().size()+":"+r.getLimit()+":";
					}
					sendMsgToClient(str,key);
				}
				else if(token.equals("[ex]"))   // Ŭ���̾�Ʈ�� ä�ù濡�� ������ 
				{
					exitRoom(input , key);
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
		Client sender = null;
		String tok = input.split(":")[0];
		String chatText = input.split(":")[1];
		String roomCode = input.split(":")[2];
		int code = Integer.parseInt(roomCode);
		//System.out.println("���� : "+chatText.length()+" ���� �޼��� : "+ chatText );
		for(int i=0;i<clientlist.size();i++)   // ���� �ȵ��� �� 
		{
			if(socketC.equals(clientlist.get(i).getSocketChannel()))
			{
				sender = clientlist.get(i);
				break;
			}
		}
		if(sender != null)
		{
			chatText = "[cp]:"+chatText +":" +sender.getName()+":";
			//System.out.println("chat : "+ chatText);
			for(int i=0;i<clientlist.size();i++)
			{
				// �ڱ� �ڽſ��� �������ϴ°� ����
				if((clientlist.get(i).getRoomCode() == code) && !(clientlist.get(i).getId().equals(sender.getId())))
				{
					SocketChannel target = clientlist.get(i).getSocketChannel();
					if(target!=null)
					{
						System.out.println(sender.getName()+"���� "+clientlist.get(i).getName() +"����");
						SelectionKey targetKey = target.keyFor(selector);
						sendMsgToClient(chatText,targetKey);
					}
				}
			}
		}
	}
	public void sendWhisper(SelectionKey key ,String input) throws UnsupportedEncodingException 
	{
		SocketChannel socketC = (SocketChannel) key.channel();
		Client sender = null;
		String tok = input.split(":")[0];
		String name = input.split(":")[1];
		String chatText = input.split(":")[2];
		String roomCode = input.split(":")[3];
		int code = Integer.parseInt(roomCode);
		for(int i=0;i<clientlist.size();i++)   
		{
			if(socketC.equals(clientlist.get(i).getSocketChannel()))
			{
				sender = clientlist.get(i);
				break;
			}
		}
		if(sender != null)
		{
			chatText = "[cp]:"+chatText +":" +sender.getName()+":";
			//System.out.println("chat : "+ chatText);
			for(int i=0;i<clientlist.size();i++)
			{
				// �ڱ� �ڽſ��� �������ϴ°� ����
				if((clientlist.get(i).getRoomCode() == code) && (clientlist.get(i).getName().equals(name)))
				{
					SocketChannel target = clientlist.get(i).getSocketChannel();
					if(target!=null)
					{
						System.out.println(sender.getName()+"���� "+clientlist.get(i).getName() +"����");
						SelectionKey targetKey = target.keyFor(selector);
						sendMsgToClient(chatText,targetKey);
					}
				}
			}
		}
	}
	public void sendMsgToClient(String str , SelectionKey key) throws UnsupportedEncodingException       // ok
	{
		SocketChannel socketC = (SocketChannel)key.channel();
		List<byte[]> updateClientData = clientChannel.get(socketC);
		updateClientData.add(str.getBytes("UTF-8"));
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
		int errorCode = 0;
		String token = input.split(":")[0];
		String id = input.split(":")[1];
		String pw = input.split(":")[2];
		int port = Integer.parseInt(input.split(":")[3]);
		System.out.println("�α��� ��û -> clientID : "+ id + " client PW : "+ pw+ " client UDP port : "+ port);	
		
		// Ŭ���̾�Ʈ �ִ��� ã�ƺ���  ������ id , pw �� 
		for(int i=0;i<clientlist.size();i++)
		{
			if(clientlist.get(i).getId().equals(id)&&clientlist.get(i).getPw().equals(pw))           
			{
				// Ŭ���̾�Ʈ���� �α��� ���� �ִ� �ڵ� 
				Client c = clientlist.get(i);
				if(!c.getOnline())         // �̹� �����ߴ��� �ƴ��� üũ
					c.setOnline(true);
				else 
				{
					errorCode = 1;
					break;
				}
				// �α��� ���������� 
				c.setSocketChannel(socketC);     // Ŭ���̾�Ʈ address ����     
				c.setUDPport(port);
				List<byte[]> updateClientData = clientChannel.get(socketC);
				updateClientData.add(loginAcceptCode.getBytes());
				key.interestOps(SelectionKey.OP_WRITE);
				flag = true;
				break;
			}
		}
		if(flag==false)  // �α��� ���н� 
		{
			String errorMsg="[[login fail]]";
			if(errorCode == 0)
				errorMsg += "\nid or pw wrong:";
			else
				errorMsg += "\nalready connect:";
			List<byte[]> updateClientData = clientChannel.get(socketC);
			updateClientData.add(errorMsg.getBytes());
			key.interestOps(SelectionKey.OP_WRITE);
		}
	}
	public void signIn(String input , SelectionKey key)
	{
		
	}
	public int enterRoom(SelectionKey key, String input)
	{
		SocketChannel socketC = (SocketChannel) key.channel();
		Client sender = null;
		for(int i=0;i<clientlist.size();i++)   
		{
			if(socketC.equals(clientlist.get(i).getSocketChannel()))
			{
				sender = clientlist.get(i);
				break;
			}
		}
		String token = input.split(":")[0];
		String rname = input.split(":")[1];
		Room temp;
		int num;
		for(int i=0;i<roomList.size();i++)
		{
			temp = roomList.get(i);
			num = temp.getCurrentClientNum();
			if(temp.getRoomName().equals(rname))
			{
				if(num< temp.getLimit())    // �濡 �� �� ������ 
				{
					System.out.println("["+temp.getRoomName()+"]�� ���� ��û -> ���ε�");
					num++; 
					temp.setCurrentClientNum(num);
					if(sender!=null)
						sender.setRoomCode(temp.getRoomCode());
					return temp.getRoomCode();
				}
				else  // ���� �ִµ� �ڸ��� ��� ������ 
					return -2;
			}
		}
		return -1;  // ���� ��ã������ 
	}
	public Room createRoom(String input ,SelectionKey key)
	{
		Room r;
		boolean flag = true;
		SocketChannel socketC = (SocketChannel)key.channel();
		String token = input.split(":")[0];
		String rname = input.split(":")[1];
		String tname = input.split(":")[2];
		String limit = input.split(":")[3];
		int num = Integer.parseInt(limit); 
		System.out.println("�� ���� ��û ->"+ rname + ","+ tname +","+ num);
		for(int i=0;i<roomList.size();i++)
		{
			if(roomList.get(i).getRoomName().equals(rname))
			{
				System.out.println("�� ���� ����-> �̸��� �ߺ���");
				flag = false;
				break;
			}
		}
		if(flag)
		{
			r = new Room(roomCount+1 , rname , tname , num);
			roomCount++;
			return r; 
		}
		else 
			return null;
	}
	public void exitRoom(String input , SelectionKey key)       
	{
		Room temp;
		String token = input.split(":")[0];
		String text = input.split(":")[1];
		int rcode = Integer.parseInt(input.split(":")[2]);
		//System.out.println("���� 510 : text -> "+text +"," +"rcode -> "+rcode);
		int num;
		int targetRoomCode = -2;
		for(int i=0;i<roomList.size();i++)
		{
			temp = roomList.get(i);
			num = temp.getCurrentClientNum();
			if(temp.getRoomCode()== rcode)
			{
				System.out.println(temp.getRoomName()+" �濡�� client�� ����");   // ���� ���� �ο� �� �ٿ��ֱ� 
				num--;
				temp.setCurrentClientNum(num);
				targetRoomCode = temp.getRoomCode();
				break;
			}
		}
		if(targetRoomCode >=0)
		{
			text = "[cp]:"+text+":"+ Integer.toString(targetRoomCode)+":";
			try {
				sendMessage(key, text);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("���� �߻� : ���� ���� 544");
			}
		}
	}
	public void receiveImage()
	{
		
	}
	public void sendImage()
	{
		DatagramPacket dp;
		String testMsg = "send testMsg by udp";
		int port;
		for(int i=0;i<clientlist.size();i++)
		{
			port = clientlist.get(i).getUDPport();
			dp = new DatagramPacket(testMsg.getBytes(), testMsg.getBytes().length,local,port);
			try {
				udpSocket.send(dp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("send by udp fail! -> server.java line 488");
			}
		}
	}
}
