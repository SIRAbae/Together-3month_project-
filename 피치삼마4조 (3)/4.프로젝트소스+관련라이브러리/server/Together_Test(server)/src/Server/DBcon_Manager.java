package Server;

import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBcon_Manager {
	
	String driver = "org.mariadb.jdbc.Driver";
	Connection con = null;
	Statement statement = null;
	ResultSet rs = null;
	Socket socket = null;
	
	int u_count = 0;
	ArrayList<User_info> u_info = null;
	
	private final static int APPROVED = 1;
	
	public DBcon_Manager(Socket socket) {
		try {
			//0.�õ� ���� üũ�� ������ �ޱ�
			this.socket = socket;
			//u_info = u;
			// 1. ����̹� �ε�
            // ����̹� �������̽��� ������ Ŭ������ �ε�
            // mysql, oracle �� �� ������ ���� Ŭ���� �̸��� �ٸ���.
            // mysql�� "com.mysql.jdbc.Driver"�̸�, �̴� �ܿ�� ���� �ƴ϶� ���۸��ϸ� �ȴ�.
            // ����� ������ �����ߴ� jar ������ ���� com.mysql.jdbc ��Ű���� Driver ��� Ŭ������ �ִ�.
			Class.forName(driver);
			
			 // 2. �����ϱ�
            // ����̹� �Ŵ������� Connection ��ü�� �޶�� ��û�Ѵ�.
            // Connection�� ��� ���� �ʿ��� url ����, �����縶�� �ٸ���.
            // mariaDB�� "mariaDB://localhost/�����db�̸�" �̴�.
			// @param  getConnection(url, userName, password);
            // @return Connection
			con = DriverManager.getConnection(
					"jdbc:mariadb://127.0.0.1:3306/test1",
					"kyunghun96",
					"rlarud1125");
			
			System.out.println("DB���� ����");
			
		} catch(ClassNotFoundException e) {
			System.out.println("����̹� �ε� ����");
		} catch(SQLException e) {
			System.out.println("DB���� ����");
			e.printStackTrace();
		}
	}
	
	//===========================================================�����ڿ�==================================================================================
		public ArrayList<User_info_M> getAllUser() {
			try {
				
				ArrayList<User_info_M> u_list = new ArrayList<User_info_M>();
				
				int ulist_num = GetCount();
				statement = con.createStatement();
				
				String sql = "select * from user;";
				
				rs = statement.executeQuery(sql);
				
				for(int i = 0; i < ulist_num; i++) {
					rs.next();
					
					User_info_M info = new User_info_M(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),
							rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),rs.getString(12), 
								Double.parseDouble(rs.getString(13)), Double.parseDouble(rs.getString(14)), rs.getInt(19), rs.getString(20));

					u_list.add(info); //�����ͺ��̽��κ��� ������ ���� ����
				}
				
				return u_list;
				
			} catch(SQLException e) {
				return null;
			}
		}
//=============================================================================================================================================
		public boolean Approve(String id) {
			try {
				statement = con.createStatement();
				
				String sql = "update user set U_MEMBER = " + APPROVED + " where U_ID= " + "\"" + id + "\"" + ";";
				
				statement.executeUpdate(sql);
				
				return true;
				
			}catch(SQLException e) {
				return false;
			}
			
		}
//=============================================================================================================================================
		

		public int GetCount() {
			try {
				statement = con.createStatement();
				
				String sql = "select count(*) from user;";
				
				rs = statement.executeQuery(sql);
				
				rs.next();
				
				u_count = rs.getInt(1);

				return u_count;
			
				
			} catch (SQLException e) {
				System.out.println("����������");
				return 0;
				
			}
		}

}
