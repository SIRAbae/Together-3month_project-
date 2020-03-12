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
	
	public static final int PORT = 9000; //��Ʈ�� ����� ����

	public static void main(String[] args) {
		ServerSocket serverSocket = null;	//��������
		Server_DBcon s_db = null;
		ReceiveThread RT = null;
		HashMap<Socket,String> clientmap = new HashMap<Socket,String>(); //�����ڵ��� ���� ����Ʈ
		ArrayList<User_info> u_info = new ArrayList<>();
		
		
		try {
			Collections.synchronizedMap(clientmap); //���������� ���ش�.( clientMap�� ��Ʈ��ũ ó�����ִ°� )
			
			//1.���� ���� ����
			serverSocket = new ServerSocket();
			consoleLog("��������");
			
			//1.5 ����� ���� �ҷ���
			s_db = new Server_DBcon(u_info);
			
			//2.���ε�
			String hostAddress = InetAddress.getLocalHost().getHostAddress(); //IP�ּ� ��������
			serverSocket.bind(new InetSocketAddress(hostAddress, PORT));
			consoleLog("�����ٸ� -" + hostAddress + ":" + PORT); //�ڱ� �ּҶ� ��Ʈ �� ���
			
			GetDistanceByPos gd = new GetDistanceByPos();
			double result = gd.Distance(36.3378637, 127.4438492, 36.3357655,127.4508804, "Kilometer");
			System.out.println(result);
			
			//3.��û���
			while(true) {
				Socket socket = serverSocket.accept();
				socket.setSoTimeout(600000);
				RT = new ReceiveThread(socket, clientmap,u_info);
				RT.start();
				String Msg = " -- Ŭ���̾�Ʈ [" + socket.getInetAddress().toString() + "]�κ��� ���� ����";
				consoleLog(Msg);
				//�����浵 
				
				
			}
		}
		catch (SocketTimeoutException e) {
			System.out.println("Main ���� �������� Ŭ���̾�Ʈ�κ��� �������");
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
		
        System.out.println(format_time1 + "-" + "[���Դ� ���� " + Thread.currentThread().getId() + "] " + log);
    }	
	
}
