package serverSide;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// 현재 진행 상황 : 클라이언트 다중 접속까지는 가능 
		// create Room 까지 진행.
		Server lobby = new Server();
		lobby.start();
		System.exit(0);
	}

}
