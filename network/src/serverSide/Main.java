package serverSide;

import java.io.IOException;

public class Main {

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		// ���� ���� ��Ȳ : Ŭ���̾�Ʈ ���� ���ӱ����� ���� 
		// create Room ���� ����.
		Server lobby = new Server();
		lobby.start();
		System.out.println("���� �����");
		System.exit(0);
	}

}
