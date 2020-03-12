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
			//0.시도 관련 체크용 아이피 받기
			this.socket = socket;
			//u_info = u;
			// 1. 드라이버 로딩
            // 드라이버 인터페이스를 구현한 클래스를 로딩
            // mysql, oracle 등 각 벤더사 마다 클래스 이름이 다르다.
            // mysql은 "com.mysql.jdbc.Driver"이며, 이는 외우는 것이 아니라 구글링하면 된다.
            // 참고로 이전에 연동했던 jar 파일을 보면 com.mysql.jdbc 패키지에 Driver 라는 클래스가 있다.
			Class.forName(driver);
			
			 // 2. 연결하기
            // 드라이버 매니저에게 Connection 객체를 달라고 요청한다.
            // Connection을 얻기 위해 필요한 url 역시, 벤더사마다 다르다.
            // mariaDB은 "mariaDB://localhost/사용할db이름" 이다.
			// @param  getConnection(url, userName, password);
            // @return Connection
			con = DriverManager.getConnection(
					"jdbc:mariadb://127.0.0.1:3306/test1",
					"kyunghun96",
					"rlarud1125");
			
			System.out.println("DB접속 성공");
			
		} catch(ClassNotFoundException e) {
			System.out.println("드라이버 로드 실패");
		} catch(SQLException e) {
			System.out.println("DB접속 실패");
			e.printStackTrace();
		}
	}
	
	//===========================================================관리자용==================================================================================
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

					u_list.add(info); //데이터베이스로부터 가져온 정보 저장
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
				System.out.println("오류류류류");
				return 0;
				
			}
		}

}
