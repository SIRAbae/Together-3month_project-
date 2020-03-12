package Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Server_DBcon {
	String driver = "org.mariadb.jdbc.Driver";
	Connection con = null;
	Statement statement = null;
	ResultSet rs = null;
	int u_count;
	ArrayList<User_info> u_info = null;
	
	public Server_DBcon(ArrayList<User_info> user_info) {
		try {
			//��ü ȸ�������� ������̸���Ʈ;
			u_info = user_info;
			
			Class.forName(driver);
			
			con = DriverManager.getConnection(
					"jdbc:mariadb://127.0.0.1:3306/test1",
					"kyunghun96",
					"rlarud1125");
			
			consoleLog("DB���� ����");
			u_count = GetCount();
			getUserinfo();
			
		} catch(ClassNotFoundException e) {
			consoleLog("����̹� �ε� ����");
		} catch(SQLException e) {
			consoleLog("DB���� ����");
			e.printStackTrace();
		}
	}
	
	private int GetCount() {
		try {
			statement = con.createStatement();
			
			String sql = "select count(*) from user;";
			
			rs = statement.executeQuery(sql);
			
			rs.next();
			
			consoleLog("�� ȸ�� ��: " + rs.getInt(1));
		
			return rs.getInt(1);
		} catch (SQLException e) {
			System.out.println("����������");
			return 0;
		}
	}
	
	private void getUserinfo() {
		try {
			statement = con.createStatement();
			
			String sql = "select * from user;";
			
			rs = statement.executeQuery(sql);
			
			int r_user = 0;
			
			for(int i = 0; i < u_count; i++) {
				rs.next();
				if(rs.getInt(19)==2) {
					User_info info = new User_info(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),
														rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),rs.getString(12), 
															Double.parseDouble(rs.getString(13)), Double.parseDouble(rs.getString(14)), rs.getInt(19));
					
					u_info.add(info); //�����ͺ��̽��κ��� ������ ���� ����
					//System.out.println(u_info.get(i).U_ID + ":" + u_info.get(i).U_MEMBER);
					r_user++;
				}
			}
			consoleLog("���� ȸ�� ��: " + r_user);

		} catch(SQLException e) {
			System.out.println("����������");

		}
	}
	
	private static void consoleLog(String log) {
		SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");		
		String format_time1 = format1.format (System.currentTimeMillis());
		
        System.out.println(format_time1 + "-" + "[���Դ� ���� " + Thread.currentThread().getId() + "] " + log);
    }
}
