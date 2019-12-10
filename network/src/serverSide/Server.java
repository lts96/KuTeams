package serverSide;
import java.awt.image.BufferedImage;
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

import javax.imageio.ImageIO;

import UI.LoginScreen;

public class Server {
	private static int roomCount = 1;
	private ByteBuffer buffer = ByteBuffer.allocate(100000);
	private ByteBuffer imageBuffer = ByteBuffer.allocate(100000); 
	Map<SocketChannel, List<byte[]>> clientChannel = new HashMap<>();   // 채널마다 byte 배열 저장 필요 -> 메세지 뭐 왔나 저장용
	List<Client> clientlist = new ArrayList<>();
	List<Room> roomList = new ArrayList<>();
	ServerSocketChannel msc = null;
	Selector selector = null;
	DatagramSocket udpSocket;
	InetAddress local;
	public void start() 
	{
		final int default_port = 8888;     // 일단은 임의로 설정     // 기본적인 메세지는 이걸로 받음 , 대신 화면 전송은 다른 프로세스 or 다른 쓰레드 사용 
		// 클라이언트 더미데이터 
		Client c1 = new Client("이태선","client","1234");
		Client c2 = new Client("김철수", "admin", "2323");
		Client c3 = new Client("존", "3333","3333");
		Client c4 = new Client("네모","4444","4444");
		//Client c5 = new Client("박씨", "5555", "5555");
		//Client c6 = new Client("최씨", "6666","6666");
		//Client c7 = new Client("페리","7777","7777");
		//Client c8 = new Client("조안나", "8888", "8888");
		//Client c9 = new Client("제이슨", "9999","9999");
		
		clientlist.add(c1);
		clientlist.add(c2);
		clientlist.add(c3);
		clientlist.add(c4);
		//clientlist.add(c5);
		//clientlist.add(c6);
		//clientlist.add(c7);
		//clientlist.add(c8);
		//clientlist.add(c9);

		// 룸 더미데이터 
		Room r1 = new Room(1,"test","김철수" ,9);   // -> 버그   3인으로 설정해놨는데 들어감 
		roomList.add(r1);
		
		System.out.println("[main server start!!]");
		//System.out.println("서버 포트 번호 입력 : ");
		try
		{
			local = InetAddress.getLocalHost();
			System.out.println("main server IP : " + local.getHostAddress());  // 확인용 
			msc = ServerSocketChannel.open();
			selector = Selector.open();
			//udpSocket = new DatagramSocket(9999);  //udp는 연결 필요없이 다수의 클라이언트 수용 가능 
			// 문제는 어떻게 이미지 파일을 주고 받을 것인가 -> 이걸 제대로 출력할 수 있는가 (이건 클라이언트 담당)
			
			// msc는 메인 소켓 채널의 약자
			if(!msc.isOpen())
			{
				System.out.println("main server open fail");
				return;
			}
			
			msc.configureBlocking(false);
			// 옵션값
			msc.setOption(StandardSocketOptions.SO_RCVBUF, 128*1024*1024);    // 대용량을 위해 늘림 
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
			System.out.println("server.java 124 line catch");
			System.out.println(e);
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
			else if(readNum > 0 && readNum < 1024)
			{
				buffer.flip();
				byte [] messageByte= new byte[readNum];
				buffer.get(messageByte,buffer.position(), buffer.limit());
				input = new String(messageByte,"UTF-8");
				System.out.println("읽은 값 : "+ input + " by "+ socketC.getRemoteAddress());
				token = input.substring(0, 4);
				//buffer.flip();
				if(token.equals("[lp]"))  // 패킷 검사하기 (로그인인지 아닌지)
				{
					checkLogin(input,key);
				}
				else if(token.equals("[sp]"))  // 회원가입인지 아닌지    signin packet
				{
					signIn(input, key);
				}
				else if(token.equals("[ch]"))   // 채팅 메세지 
				{
					// 다른 클라이언트에게 받은 메세지 보내기
					System.out.println("메세지 : "+ input + " by "+ socketC.getRemoteAddress());
					sendMessage(key, input);
				}
				else if(token.equals("[cw]"))    // 귓속말 처리 
				{
					System.out.println("메세지 : "+ input + " by "+ socketC.getRemoteAddress());
					sendMessage(key, input);
				}
				else if (token.equals("[rc]"))  // 방 생성 요청 
				{
					Room r = createRoom(input , key);
					if(r != null)   // 방이 제대로 생성되었을때 
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
								System.out.println("클라이언트 "+ clientlist.get(i).getName()+"님이 "+r.getRoomName()+" 방에 입장");
								break;
							}
						}
						// 클라이언트 쪽  방 리스트 정보 업데이트 용 
					}
					else  // 생성에 실패했을때 
					{
						sendMsgToClient("[[rc fail]]", key);
					}
				}
				else if(token.equals("[ra]")) // 방 참가 요청 코드   room access
				{
					int code = enterRoom(key,input);
					if(code > 0)
						sendMsgToClient("[[ra success]]:"+code +":", key);    // 방 입장 성공
					else if (code == -1)       //
						sendMsgToClient("[[ra fail]] \n room is not exist:", key);   // 입장 실패 -> 방 못찾음
					else if (code == -2)
						sendMsgToClient("[[ra fail]] \n room is already full:", key); // 입장 실패 -> 인원 제한 걸림
				}
				else if(token.equals("[rl]"))  // 방 리스트 요청 
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
				else if(token.equals("[ex]"))   // 클라이언트가 채팅방에서 나갈때 
				{
					exitRoom(input , key);
				}
				else if(token.equals("[im]"))   
				{
					int rcode = Integer.parseInt(input.split(":")[1]);	
				}
				else if(token.equals("[cc]"))   // 클라이언트 연결이 끊어졌을때 -> 로그아웃    라인 597
				{
					logOut(findSender(key));
				}
				buffer.compact();    // 이건 불안정한 방법 -> 버퍼가 읽은 값이 512를 넘어가면 제대로 초기화가 안됨 
				buffer.clear();
			}
			else if(readNum >= 1024) // 만약 읽을 데이터가 1024 이상일 경우    -> 일단은 성공       
			{
				//readNum = socketC.read(buffer);
				//한번에 read 할수 있는 바이트 수 -> 131711 ?  이 이상을 읽지를 못함 
				buffer.flip();
				System.out.println("readNum : "+ readNum);
				byte [] imageInByte= new byte[readNum];
				buffer.get(imageInByte,buffer.position(), buffer.limit());
				System.out.println("buffer position : "+ buffer.position() +" buffer limit :" + buffer.limit());
				System.out.println("image byte size : "+ imageInByte.length);
				
				Client sender = findSender(key);
				if(sender!=null)
				{
					for(int i=0;i<clientlist.size();i++)
					{
						if(!clientlist.get(i).getId().equals(sender.getId())&& clientlist.get(i).getRoomCode() == sender.getRoomCode())
						{
							SocketChannel target = clientlist.get(i).getSocketChannel();
							if(target!=null)
							{
								System.out.println(sender.getName()+"님이 "+clientlist.get(i).getName() +"에게");
								SelectionKey targetKey = target.keyFor(selector);
								SocketChannel targetChannel = (SocketChannel)targetKey.channel();
								List<byte[]> updateClientData = clientChannel.get(targetChannel);
								updateClientData.add(imageInByte);
								targetKey.interestOps(SelectionKey.OP_WRITE);
							}
						}
					}
				}
				//바이트 -> 이미지 파일 복원 -> 성공함 
				//BufferedImage imag = ImageIO.read(new ByteArrayInputStream(imageInByte));
		        //ImageIO.write(imag, "png", new File("C:\\Users\\s_dlxotjs\\Desktop\\네트워크 프로그래밍", "img-server2.png"));
			
				buffer.compact();
				buffer.clear();
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
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
			if(temp.length <= 1024)
				System.out.println("send to "+ socketC.getRemoteAddress() +" "+ new String(temp));  // 뭐 보내는지 체크용 
			socketC.write(ByteBuffer.wrap(temp));
			it.remove();
		}
		key.interestOps(SelectionKey.OP_READ);
	}
	public void sendMessage(SelectionKey key , String input) throws IOException       // 받은 데이터 다른 클라이언트들에게 전송   미완성 
	{
		SocketChannel socketC = (SocketChannel) key.channel();
		Client sender = null;
		String tok = input.split(":")[0];
		String chatText = input.split(":")[1];
		String roomCode = input.split(":")[2];
		int code = Integer.parseInt(roomCode);
		//System.out.println("길이 : "+chatText.length()+" 받은 메세지 : "+ chatText );
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
				// 자기 자신에게 재전송하는거 방지
				if((clientlist.get(i).getRoomCode() == code) && !(clientlist.get(i).getId().equals(sender.getId())))
				{
					SocketChannel target = clientlist.get(i).getSocketChannel();
					if(target!=null)
					{
						System.out.println(sender.getName()+"님이 "+clientlist.get(i).getName() +"에게");
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
				// 자기 자신에게 재전송하는거 방지
				if((clientlist.get(i).getRoomCode() == code) && (clientlist.get(i).getName().equals(name)))
				{
					SocketChannel target = clientlist.get(i).getSocketChannel();
					if(target!=null)
					{
						System.out.println(sender.getName()+"님이 "+clientlist.get(i).getName() +"에게");
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
		int errorCode = 0;
		String token = input.split(":")[0];
		String id = input.split(":")[1];
		String pw = input.split(":")[2];
		System.out.println("로그인 요청 -> clientID : "+ id + " client PW : "+ pw);	
		
		// 클라이언트 있는지 찾아보기  있으면 id , pw 비교 
		for(int i=0;i<clientlist.size();i++)
		{
			if(clientlist.get(i).getId().equals(id)&&clientlist.get(i).getPw().equals(pw))           
			{
				// 클라이언트에게 로그인 답장 주는 코드 
				Client c = clientlist.get(i);
				if(!c.getOnline())         // 이미 접속했는지 아닌지 체크
					c.setOnline(true);
				else 
				{
					errorCode = 1;
					break;
				}
				// 로그인 성공했을때 
				loginAcceptCode += ":"+c.getName() + ":";
				c.setSocketChannel(socketC);     // 클라이언트 address 설정     
				List<byte[]> updateClientData = clientChannel.get(socketC);
				updateClientData.add(loginAcceptCode.getBytes("UTF-8"));
				key.interestOps(SelectionKey.OP_WRITE);
				flag = true;
				break;
			}
		}
		if(flag==false)  // 로그인 실패시 
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
				if(num< temp.getLimit())    // 방에 들어갈 수 있을때 
				{
					System.out.println("["+temp.getRoomName()+"]방 입장 요청 -> 승인됨");
					num++; 
					temp.setCurrentClientNum(num);
					if(sender!=null)
						sender.setRoomCode(temp.getRoomCode());
					return temp.getRoomCode();
				}
				else  // 방은 있는데 자리가 없어서 못들어갈때 
					return -2;
			}
		}
		return -1;  // 방을 못찾았을때 
	}
	public Room createRoom(String input ,SelectionKey key)
	{
		Room r;
		boolean flag = true;
		SocketChannel socketC = (SocketChannel)key.channel();
		String token = input.split(":")[0];
		String rname = input.split(":")[1];
		String host = input.split(":")[2];
		String limit = input.split(":")[3];
		int num = Integer.parseInt(limit); 
		System.out.println("방 생성 요청 ->"+ rname + ","+ host +","+ num);
		for(int i=0;i<roomList.size();i++)
		{
			if(roomList.get(i).getRoomName().equals(rname))
			{
				System.out.println("방 생성 실패-> 이름이 중복됨");
				flag = false;
				break;
			}
		}
		if(flag)
		{
			r = new Room(roomCount+1 , rname , host , num);
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
		//System.out.println("라인 510 : text -> "+text +"," +"rcode -> "+rcode);
		int num;
		int targetRoomCode = -2;
		for(int i=0;i<roomList.size();i++)
		{
			temp = roomList.get(i);
			num = temp.getCurrentClientNum();
			if(temp.getRoomCode()== rcode)
			{
				System.out.println(temp.getRoomName()+" 방에서 client가 나감");   // 방의 현재 인원 수 줄여주기 
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
				System.out.println("에러 발생 : 서버 라인 544");
			}
		}
	}
	public Client findSender(SelectionKey key)
	{
		Client sender = null;
		SocketChannel channel = (SocketChannel) key.channel();
		for(int i=0;i<clientlist.size();i++)
		{
			if(channel.equals(clientlist.get(i).getSocketChannel()))
			{
				sender = clientlist.get(i);
				break;
			}
		}
		return sender;
	}
	public void logOut(Client client)
	{
		if(client != null)
		{
			System.out.println("Client "+client.getName()+"님이 로그아웃 했습니다.");
			client.setOnline(false);
		}
	}
	public String currentClientList(int roomCode)    // 아직 미완성 -> 이걸 클라이언트 만들어지거나 나가는 곳마다 넣어줘야됨 
	{
		String list = "[cl]:";
		int num = 0;
		for(int  i = 0; i< roomList.size();i++)
		{
			if(roomList.get(i).getRoomCode() == roomCode)
			{
				Room r = roomList.get(i);
				for(int j =0;j< r.getClientList().size();i++)
				{
					list += r.getClientList().get(i).getName() + ":";
				}
				break;
			}
		}
		if(num == 0)
			list += num + ":";
		return list;
	}
}
