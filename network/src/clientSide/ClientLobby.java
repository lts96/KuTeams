package clientSide;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.*;
public class ClientLobby {

	public static void main(String[] args) {
		Socket socket;
		BufferedWriter write;
		try {
			socket = new Socket("192.168.123.105",7777);
			// 일단 임의로 로컬 호스트랑 포트 번호 지정 , 나중에는 로컬이 아니라 외부 ip , 그리고 
			// 포트 번호도 따로 입력 받던가 해야됨.
			LoginScreen login = new LoginScreen(true);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			//System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			//System.exit(1);
		}
		
	}

}
