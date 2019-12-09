package serverSide;

import java.io.IOException;

public class Main {

	public static void main(String[] args)   // *** 여기서 run 해야 서버 켜짐 
	{
		// TODO Auto-generated method stub
		// 현재 진행 상황 : 클라이언트 다중 접속까지는 가능 
		// create Room 까지 진행.
		Server lobby = new Server();
		lobby.start();
		System.out.println("서버 종료됨");
		System.exit(0);
	}

}
