package serverSide;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// ���� ���� ��Ȳ : Ŭ���̾�Ʈ ���� ���ӱ����� ���� 
		// create Room ���� ����.
		Server lobby = new Server();
		lobby.start();
		System.exit(0);
	}

}
