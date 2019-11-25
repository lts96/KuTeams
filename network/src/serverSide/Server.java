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
	Map<SocketChannel, List<byte[]>> clientChannel = new HashMap<>();   // 채널마다 byte 배열 저장 필요 -> 메세지 뭐 왔나 저장용
	Map<String, Client> clientInfo = new HashMap<>();
	public void start() throws IOException 
	{
		final int default_port = 9999;     // 일단은 임의로 설정     // 기본적인 메세지는 이걸로 받음 , 대신 화면 전송은 다른 프로세스 or 다른 쓰레드 사용 
		
		// 클라이언트 더미데이터 
		Client c1 = new Client("이태선","client","1234");
		Client c2 = new Client("김철수", "admin", "2323");
		clientInfo.put("client", c1);
		clientInfo.put("admin",c2);
		
		System.out.println("[main server start!!]");
		//System.out.println("서버 포트 번호 입력 : ");
		try
		{
			InetAddress local = InetAddress.getLocalHost();
			System.out.println("main server IP : " + local.getHostAddress());  // 확인용 
			ServerSocketChannel msc = ServerSocketChannel.open();
			Selector selector = Selector.open();
			// msc는 메인 소켓 채널의 약자
			if(!msc.isOpen())
			{
				System.out.println("main server open fail");
				return;
			}
			
			msc.configureBlocking(false);
			// 옵션값
			msc.setOption(StandardSocketOptions.SO_RCVBUF, 128*1024);
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
				//System.out.println("길이 : "+size+" 읽은 값 : "+ input + " by "+ socketC.getRemoteAddress());
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
				else if(sub.equals("[rp]")) // 방 참가 요청 코드 
				{
					
				}
				else if(sub.equals("[cp]"))
				{
					// 다른 클라이언트에게 받은 메세지 보내기 broadcast or multicast
					System.out.println(" 읽은 값 : "+ input + " by "+ socketC.getRemoteAddress());
					sendMessage( key, buffer.array());
				}
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
			System.out.println("send to client -> "+ new String(temp));  // 뭐 보내는지 체크용 
			socketC.write(ByteBuffer.wrap(temp));
			it.remove();
		}
		key.interestOps(SelectionKey.OP_READ);
	}
	private void sendMessage(SelectionKey key , byte[] data )       // 받은 데이터 다른 클라이언트들에게 전송   미완성 
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
		System.out.println("로그인 요청 -> 입력받은 ID : "+ id + " 입력받은 PW : "+ pw);	
		
		// 해시 맵에서 클라이언트 있는지 찾아보기. 
		if(clientInfo.containsKey(id)&&clientInfo.get(id).getPw().equals(pw))            // 클라이언트 있을때    이거 이상함 
		{
			// 될지 안될지 모름   -> 클라이언트에게 로그인 답장 주는 코드 
			List<byte[]> updateClientData = clientChannel.get(socketC);
			updateClientData.add(loginAcceptCode.getBytes());
			key.interestOps(SelectionKey.OP_WRITE);
		}
		else  // 없을때 
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
