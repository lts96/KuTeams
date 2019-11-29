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
	Map<SocketChannel, List<byte[]>> clientChannel = new HashMap<>();   // 채널마다 byte 배열 저장 필요 -> 메세지 뭐 왔나 저장용
	List<Client> clientlist = new ArrayList<>();
	List<Room> roomList = new ArrayList<>();
	ServerSocketChannel msc = null;
	Selector selector = null;
	public void start() throws IOException 
	{
		final int default_port = 8888;     // 일단은 임의로 설정     // 기본적인 메세지는 이걸로 받음 , 대신 화면 전송은 다른 프로세스 or 다른 쓰레드 사용 
		
		// 클라이언트 더미데이터 
		Client c1 = new Client("이태선","client","1234");
		Client c2 = new Client("김철수", "admin", "2323");
		Client c3 = new Client("존", "john","3333");
		clientlist.add(c1);
		clientlist.add(c2);
		clientlist.add(c3);
		
		// 룸 더미데이터 
		Room r1 = new Room(-1,"test 룸","김철수" ,3);
		
		
		System.out.println("[main server start!!]");
		//System.out.println("서버 포트 번호 입력 : ");
		try
		{
			InetAddress local = InetAddress.getLocalHost();
			System.out.println("main server IP : " + local.getHostAddress());  // 확인용 
			msc = ServerSocketChannel.open();
			selector = Selector.open();
			// msc는 메인 소켓 채널의 약자
			if(!msc.isOpen())
			{
				System.out.println("main server open fail");
				return;
			}
			
			msc.configureBlocking(false);
			// 옵션값
			msc.setOption(StandardSocketOptions.SO_RCVBUF, 256*1024);
			msc.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			
			msc.bind(new InetSocketAddress(local , default_port));
        	msc.register(selector, SelectionKey.OP_ACCEPT);
        	System.out.println("클라이언트 연결 기다리는중... "); 
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
            			System.out.println("서버 or selector 가 열리지 않았음!");
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

	private void acceptKey(SelectionKey key, Selector selector) throws IOException {  // accept 가능할때 
		// TODO Auto-generated method stub
		ServerSocketChannel serverC = (ServerSocketChannel) key.channel();
		SocketChannel socketC = serverC.accept();
		socketC.configureBlocking(false);
		System.out.println("클라이언트 주소 : " + socketC.getRemoteAddress());
		//socketC.write(ByteBuffer.wrap("서버에 접속되셨습니다.".getBytes("UTF-8")));
		socketC.register(selector, SelectionKey.OP_READ);
		
		clientChannel.put(socketC, new ArrayList<byte[]>());
	}
	private void readKey(SelectionKey key) throws IOException  // read 가능할때 
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
				//System.out.println("읽은 값 : "+ input + " by "+ socketC.getRemoteAddress());
				sub = input.substring(0, 4);
				//System.out.println(sub);
				if(sub.equals("[lp]"))  // 패킷 검사하기 (로그인인지 아닌지)
				{
					checkLogin(input,key);
				}
				else if(sub.equals("[sp]"))  // 회원가입인지 아닌지    signin packet
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
				
				else if(sub.equals("[ra]")) // 방 참가 요청 코드   room access
				{
					
				}
				else if(sub.equals("[cp]"))
				{
					// 다른 클라이언트에게 받은 메세지 보내기 broadcast or multicast
					System.out.println("읽은 값 : "+ input + " by "+ socketC.getRemoteAddress());
					sendMessage(key, input);  // -> 널포인트 오류 뜸
				}
				buffer.compact();    // 버퍼 오버플로우 조심?
				buffer.clear();
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.err.println(e);
			System.exit(1);    // -> 이거 지우면  클라이언트에서 창 닫을때 계속 에러메세지 출력됨 
		}
	}
	private void writeKey(SelectionKey key) throws IOException   // write 가 가능할때
	{
		SocketChannel socketC = (SocketChannel) key.channel();
		List<byte[]> clientData = clientChannel.get(socketC);
		Iterator<byte[]> it = clientData.iterator();
		while(it.hasNext()==true)
		{
			byte[] temp = it.next();
			System.out.println("send to "+ socketC.getRemoteAddress() +" "+ new String(temp));  // 뭐 보내는지 체크용 
			socketC.write(ByteBuffer.wrap(temp));
			it.remove();
		}
		key.interestOps(SelectionKey.OP_READ);
	}
	public void sendMessage(SelectionKey key , String input) throws IOException       // 받은 데이터 다른 클라이언트들에게 전송   미완성 
	{
		SocketChannel socketC = (SocketChannel) key.channel();
		//String dummy = input.split(":")[0];
		String chatText = input;
		//String dummy2 = input.split(":")[2];
		Client sender = null;
		
		
		System.out.println("받은 메세지 : "+ chatText + " 길이 : "+chatText.length());
		for(int i=0;i<clientlist.size();i++)   // 될지 안될지 모름 
		{
			if(socketC.equals(clientlist.get(i).getSocketChannel()))
			{
				sender = clientlist.get(i);
				break;
			}
		}
		for(int i=0;i<clientlist.size();i++)
		{
			// 자기 자신에게 재전송하는거 방지
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
		System.out.println("보낼 room name list : "+ list);
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
		System.out.println("로그인 요청 -> 입력받은 ID : "+ id + " 입력받은 PW : "+ pw);	
		
		// 클라이언트 있는지 찾아보기  있으면 id , pw 비교 
		for(int i=0;i<clientlist.size();i++)
		{
			if(clientlist.get(i).getId().equals(id)&&clientlist.get(i).getPw().equals(pw))           
			{
				// 클라이언트에게 로그인 답장 주는 코드 
				Client c = clientlist.get(i);
				c.setSocketChannel(socketC);     // 클라이언트 address 설정 
				c.setOnline(true);
				List<byte[]> updateClientData = clientChannel.get(socketC);
				updateClientData.add(loginAcceptCode.getBytes());
				key.interestOps(SelectionKey.OP_WRITE);
				flag = true;
				break;
			}
		}
		if(flag==false)  // 로그인 실패시 
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
		System.out.println("방 생성 요청 : "+ rname + " : "+ tname +" : "+ num);
		r = new Room(roomCount+1 , rname , tname , num);
		roomCount++;
		return r; 
	}
}
