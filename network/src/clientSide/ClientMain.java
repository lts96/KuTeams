package clientSide;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import UI.LoginScreen;
import UI.PrintInfoScreen;
import UI.RoomScreen;
import UI.ChatScreen;
import UI.CreateRoomScreen;
import UI.LobbyScreen;
public class ClientMain {
	private static ByteBuffer buffer = ByteBuffer.allocate(1024);
	private static ChatScreen chat;
	private static LoginScreen login;
	private static LobbyScreen lobby;
	private static CreateRoomScreen crs;
	private static PrintInfoScreen pis;
	private static RoomScreen rs;
	public static int roomCode = -1;
	private static String ip = "";
	public static void main(String[] args) throws IOException {
		InetAddress local = InetAddress.getLocalHost();
		ip = local.getHostAddress();   // 노트북 로컬 ip 
		final int default_port = 8888;
		// 일단 임의로 로컬 호스트랑 포트 번호 지정 , 나중에는 로컬이 아니라 외부 ip , 그리고 
		// 포트 번호도 따로 입력 받던가 해야됨.
		Selector selector = Selector.open();
		if(!selector.isOpen())
		{
			System.out.println("client selector open fail!");
			return;
		}
		SocketAddress sa = new InetSocketAddress(ip, default_port);
		SocketChannel sc = SocketChannel.open(sa);
		DatagramSocket udpSocket = new DatagramSocket();
		System.out.println("Client UDP socket port : "+udpSocket.getLocalPort());
		if(!sc.isOpen())
		{
			System.out.println("client socketChannel open fail!");
			return;
		}
		try {
			sc.configureBlocking(false);
			sc.setOption(StandardSocketOptions.SO_RCVBUF, 128*1024);
			sc.setOption(StandardSocketOptions.SO_SNDBUF, 128*1024);
			sc.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
			sc.register(selector, SelectionKey.OP_CONNECT);
			//sc.connect(new java.net.InetSocketAddress(ip, default_port));
			if(!sc.isConnected())
				System.out.println("서버와 채널 연결 실패!");
			Thread thread = new Thread() {
				public void run()
				{
					Sender send = new Sender(sc , udpSocket);
					rs = new RoomScreen(false, send);
					chat = new ChatScreen(false, send);
					crs = new CreateRoomScreen(false , send);
					lobby = new LobbyScreen(false, send , crs);
					login = new LoginScreen(true , send , lobby);
				}

			};
			thread.start();
			// receive 하는 부분 
			SocketChannel socketC = sc;
			while(true)
			{
				String input,sub;
				String parse;
				int readNum = -1;
				try {
					readNum = socketC.read(buffer);
					if(readNum == -1)
					{
						return;
					}
					else
					{
						input = new String(buffer.array(),"UTF-8");
						//System.out.println("길이 : "+input.length()+" 읽은 값 : "+ input);
						//System.out.println("길이 : "+sub.length()+"sub : "+ sub);
						sub = input.substring(0, 4);
						if(sub.equals("[[lo"))  // 패킷 검사하기 (로그인인지 아닌지)
						{
							sub = input.substring(0,17);
							login.recvLogin(input);
						}
						else if(sub.equals("[[si"))   // 회원가입 검사 
						{
							
						}
						else if (sub.equals("[[rc")) // 방 생성 결과 
						{
							crs.recvMsg(input);
						}
						else if(sub.equals("[ri]"))
						{
							lobby.addScreenInfo(input, true);
						}
						else if(sub.equals("[ru]")) // lobbyScreen update 명령    방 추가 할땐 true  방 삭제 될땐 false
						{
							lobby.updateScreenInfo(input, true);
						}
						else if(sub.equals("[[ra")) // 방 참가 요청 결과
						{
							if(input.contains("success"))     // 채팅과 화면 전송 시작 
							{
								String token = input.split(":")[0];
								roomCode= Integer.parseInt(input.split(":")[1]);
								System.out.println("접속한 룸 코드 : "+ roomCode);
								chat.screenOn(true);
								rs.screenOn(true);
							}
							else
							{
								lobby.failRoomAccess(input);
							}
						}
						else if(sub.equals("[cp]"))
						{
							System.out.println("읽은 값 : "+ input + " by "+ socketC.getRemoteAddress());
							if(chat.isAct())
							{
								chat.recvChat(input);
							}
						}
						buffer.compact();    // 버퍼 초기화 해야됨  이 방법 말고 
						buffer.clear();
					}
				}
				catch (Exception e) {
					// TODO: handle exception
					System.err.println(e);
					break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			//System.exit(1);
		}
	}
	public String getIp()
	{
		return ip;
	}
}
