package clientSide;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.*;
public class ClientLobby {

	public static void main(String[] args) {
		Socket socket;
		BufferedWriter write;
		try {
			socket = new Socket("127.0.0.1",7777);
			// �ϴ� ���Ƿ� ���� ȣ��Ʈ�� ��Ʈ ��ȣ ���� , ���߿��� ������ �ƴ϶� �ܺ� ip , �׸��� 
			// ��Ʈ ��ȣ�� ���� �Է� �޴��� �ؾߵ�.
			LoginScreen login = new LoginScreen(true);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
		
	}

}
