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
			// �ϴ� ���Ƿ� ���� ȣ��Ʈ�� ��Ʈ ��ȣ ���� , ���߿��� ������ �ƴ϶� �ܺ� ip , �׸��� 
			// ��Ʈ ��ȣ�� ���� �Է� �޴��� �ؾߵ�.
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
