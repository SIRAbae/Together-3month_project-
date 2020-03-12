package Server;

import java.io.IOException;
//import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
//import java.util.ArrayList;
import java.util.HashMap;
//import java.util.List;
import java.util.Map;

//import Test.ServerProceedThread;

public class Main {
	
	public static final int PORT = 9000; //포트값 상수로 선언

	public static void main(String[] args) {
		ServerSocket serverSocket = null;	//서버소켓
		Server_DBcon s_db = null;
		ReceiveThread RT = null;
		HashMap<Socket,String> clientmap = new HashMap<Socket,String>(); //접속자들을 담을 리스트
		ArrayList<User_info> u_info = new ArrayList<>();
		
		
		try {
			Collections.synchronizedMap(clientmap); //교통정리를 해준다.( clientMap을 네트워크 처리해주는것 )
			
			//1.서버 소켓 생성
			serverSocket = new ServerSocket();
			consoleLog("서버시작");
			
			//1.5 사용자 정보 불러옴
			s_db = new Server_DBcon(u_info);
			
			//2.바인딩
			String hostAddress = InetAddress.getLocalHost().getHostAddress(); //IP주소 가져오기
			serverSocket.bind(new InetSocketAddress(hostAddress, PORT));
			consoleLog("연결기다림 -" + hostAddress + ":" + PORT); //자기 주소랑 포트 를 띄움
			
			GetDistanceByPos gd = new GetDistanceByPos();
			double result = gd.Distance(36.3378637, 127.4438492, 36.3357655,127.4508804, "Kilometer");
			System.out.println(result);
			
			//3.요청대기
			while(true) {
				Socket socket = serverSocket.accept();
				socket.setSoTimeout(600000);
				RT = new ReceiveThread(socket, clientmap,u_info);
				RT.start();
				String Msg = " -- 클라이언트 [" + socket.getInetAddress().toString() + "]로부터 연결 성공";
				consoleLog(Msg);
				//위도경도 
				
				
			}
		}
		catch (SocketTimeoutException e) {
			System.out.println("Main 에서 오류잡음 클라이언트로부터 연결끊김");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			
			try {
				if(serverSocket != null && !serverSocket.isClosed()) {
					serverSocket.close();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private static void consoleLog(String log) {
		SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");		
		String format_time1 = format1.format (System.currentTimeMillis());
		
        System.out.println(format_time1 + "-" + "[투게더 서버 " + Thread.currentThread().getId() + "] " + log);
    }	
	
}
