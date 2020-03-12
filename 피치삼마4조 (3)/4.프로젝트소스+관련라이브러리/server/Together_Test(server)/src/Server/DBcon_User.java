package Server;

import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
//import java.sql.Statement;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/*
구문객체를 던지는 3가지 방법
1. execute -> 테이블 생성, 수정,삭제 등 데이터베이스 관리 명령어 사용    create..
2. excuteUpdate -> 레코드 삽입 수정 삭제등 데이터 조작 명령어 사용    insert, update, delete..
3. excuteQuery -> 레코드 조회, 테이블 조회 등 조회 명령어 사용    select..
*/

public class DBcon_User {
	String driver = "org.mariadb.jdbc.Driver";
	Connection con = null;
	Statement statement = null;
	ResultSet rs = null;
	Socket socket = null;
	
	int u_count = 0;
	ArrayList<User_info> u_info = null;
	
	private static final String login_Success = "001:아이디,비밀번호 일치";
	private static final String id_Fail = "002:아이디 불일치";
	private static final String pw_Fail = "003:비밀 번호 불일치";
	private static final String Query_Fail = "004: 데이터베이스 검색 실패";
	private static final String NormalLogin_Success = "007:일반로그인";
	private static final String Templogin_Success = "006:임시회원로그인";
	
	private static final String SignUp_Success = "101: 회원가입성공";
	private static final String Duplication_of_ID = "102:이미존재하는아이디";
	private static final String Available_ID = "103:사용가능한아이디";
	private static final String Duplication_of_Nick = "104:이미존재하는닉네임";
	private static final String Available_Nick = "105:사용가능한닉네임";
	private static final String SignUp_Failed = "106:회원가입 실패";

	private static final String UserPicSave_Success = "201:회원사진저장성공";
	private static final String UserPicSave_Failed = "202:회원사진저장실패";
	
	private static final String PetRegi_Success = "401:등록성공";
	private static final String PetRegi_Failed = "402:등록실패";
	
	private static final String LikeSend_Success = "501:관심친구성공";
	private static final String LikeSend_Failed = "502:관심친구 실패";
	private static final String LikeSend_Multiple = "504:중복관심친구";
	private static final String LikeCancel_Success = "505:관심친구취소";
	private static final String LikeDeny_Success = "506:관심친구거절";
	private static final String LikeAccept_Success = "509:관심친구수락";
	private static final String LikeAccept_Duple = "510:관심친구중복수락";


	
	



	
	private static final String LikeAceept_Success = "601:친구수락성공";
	private static final String LikeAceept_Failed = "602:친구수락실패";
	
		private static final int Stand_by = 0;
		private static final int ACCEPT = 1;
	
	public DBcon_User(Socket socket,ArrayList<User_info> u) {
		try {
			//0.시도 관련 체크용 아이피 받기
			this.socket = socket;
			u_info = u;
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
//===================================================================================================================================================
	public ArrayList<String> Login_db(String Msg) {
		int myUser_no = 0;
		ArrayList<String> Login_data = new ArrayList<String>();
		// 3. 쿼리 수행을 위한 Statement 객체 생성
				try {
					String[] tokens = Msg.split("/");
					
					statement = con.createStatement();
					// 4. SQL 쿼리 작성
		            // 주의사항
		            // 1) JDBC에서 쿼리를 작성할 때는 세미콜론(;)을 빼고 작성한다.
		            // 2) SELECT 할 때 * 으로 모든 칼럼을 가져오는 것보다
		            //   가져와야 할 칼럼을 직접 명시해주는 것이 좋다.
		            // 3) 원하는 결과는 쿼리로써 마무리 짓고, java 코드로 후작업 하는 것은 권하지 않음
		            // 4) 쿼리를 한 줄로 쓰기 어려운 경우 들여쓰기를 사용해도 되지만 띄어쓰기에 유의 !!
					
					String sql = "select * from user where U_ID=" + "\"" + tokens[0] + "\"" + ";";
					
					// 5. 쿼리 수행
		            // 레코드들은 ResultSet 객체에 추가된다.
		            rs = statement.executeQuery(sql);
		            
		         // 6. 실행결과 출력하기
		            //존재할시~
	            	if(rs.next() == true) {
			            if(rs.getString(6).equals(tokens[0])) {
			            	//1단계 아이디 일치 ->rs 값은 넘어오지만 쿼리값이 아예없기 때문에 catch로 넘어감 즉 비교자체를 할 수 가없다. 가 아니라 반환값 true false 로 판단가능
			            	if(rs.getString(7).equals(tokens[1])) {
			            		//2단계 비밀번호 일치
				            	//=====================================================================================================================
	
			            		if(rs.getInt(19) == 1) {		//일반회원	
			            			consoleLog(NormalLogin_Success);
			            			String update = "update user set U_LAT=" + 
			            										"\"" + tokens[2] + "\"" + "," + 
			            										"U_LONG=" +
			            										"\"" + tokens[3] + "\"" + "," +
			            										"U_TOKEN=" +
			            										"\"" + tokens[4] + "\"" + 
			            										" where U_ID=" + "\"" + tokens[0]+ "\""+ ";";
			            			   
				            		statement.executeUpdate(update); //로그인성공하면 토큰 값저장
				            		
				            		//String rt_value = login_Success + ":" + rs.getInt(3);
				            		Login_data.add("0" +"_"+ NormalLogin_Success);
				            		
				            		String msg =  "10" +
				            				"_" + rs.getString(2) +
				            				"/" + rs.getString(3) +
				            				"/" + rs.getString(4) +
				            				"/" + rs.getString(4) +
				            				"/" + rs.getString(6) +
				            				"/" + rs.getString(7) +
				            				"/" + rs.getString(8) +
				            				"/" + rs.getString(9) +
				            				"/" + rs.getString(10) +
				            				"/" + rs.getString(11) +
				            				"/" + rs.getString(12) +
				            				"/" + rs.getInt(19);										//10.user정보
				            		
				            		Login_data.add(msg);
		
				            		return Login_data;
			            			
				            	//=====================================================================================================================
			            		} else if(rs.getInt(19) == 2 ) {   //정규회원 로그인
			            			
			            			consoleLog(login_Success);
			            			String update = "update user set U_LAT=" + 
			            										"\"" + tokens[2] + "\"" + "," + 
			            										"U_LONG=" +
			            										"\"" + tokens[3] + "\"" + "," +
			            										"U_TOKEN=" +
			            										"\"" + tokens[4] + "\"" + 
			            										" where U_ID=" + "\"" + tokens[0]+ "\""+ ";";
			            			   
				            		statement.executeUpdate(update); //로그인성공하면 토큰 값저장
				            		
				            		//String rt_value = login_Success + ":" + rs.getInt(3);
				            		
				            		Login_data.add("0" +"_"+login_Success);													//0.성공값
				            		String user = "10" +
				            				"_" + rs.getString(2) +
				            				"/" + rs.getString(3) +
				            				"/" + rs.getString(4) +
				            				"/" + rs.getString(4) +
				            				"/" + rs.getString(6) +
				            				"/" + rs.getString(7) +
				            				"/" + rs.getString(8) +
				            				"/" + rs.getString(9) +
				            				"/" + rs.getString(10) +
				            				"/" + rs.getString(11) +
				            				"/" + rs.getString(12) +
				            				"/" + rs.getInt(19); 			
				            		
				            		Login_data.add(user);																	//10.내정보
				            		
				            		myUser_no = rs.getInt(1);
				            		
				            		int p_count = GetPetCount(rs.getInt(1));
				            		if(p_count != 0) {
				            			String select_dogs = "select * from pet where User_no=" + myUser_no + ";";
					            		
					            		rs = statement.executeQuery(select_dogs);
					            		
					            		for(int i = 0; i < p_count; i++ ) {
					            			
					            			rs.next();
					            			String dog = "20" + 
					            					"_" +  rs.getString(3) +
						            				"/" + rs.getString(4) +
						            				"/" + rs.getString(5) +
						            				"/" + rs.getInt(6) +
						            				"/" + rs.getString(7) +
						            				"/" + rs.getString(8) +
						            				"/" + rs.getString(9) +
						            				"/" + rs.getString(10) +
						            				"/" + rs.getString(11) +
						            				"/" + rs.getString(12);
					            													
					            			Login_data.add(dog);																//20. 내 강아지정보
					            			
					            		}
				            			
				            		}

				            		int sl_counts = 0;
				            		int sl_dog_counts = 0;
				            		String sl_sql = "select count(*) from likes where Send_ID=" + myUser_no + ";";
	
				            		rs = statement.executeQuery(sl_sql);
				            		
				            		if(rs.next() == true ) {/*1*/
				            			sl_counts = rs.getInt(1);
				            			
				            			String select_slikes = "select Recv_ID from likes where Send_ID=" + myUser_no + ";";
				            			rs = statement.executeQuery(select_slikes);
				            			
				            			if(rs.next() == true) {/*2*/
				            				ArrayList<Integer> num = new ArrayList<Integer>();
				            				for(int i = 0; i < sl_counts; i++) {
				            					num.add(rs.getInt(1));
				            					if(rs.next() == false) {
				            						System.out.println("No more Recv");
				            					}
				            				}
				            				
				            				for(int i = 0; i < sl_counts; i++ ) {
				            					String select1 = "select user_no,U_NAME,U_BIRTH,U_GENDER,U_NICK,U_REGION,U_WALKTIME,U_INTRO from user where user_no=" + num.get(i) + ";";		
						            			rs = statement.executeQuery(select1);
						            			
						            			if(rs.next() == true) {/*3*/
						            				
						            				String LikeSend_res = "30" +
						            						"_" + rs.getInt(1) +
	            											"/" + rs.getString(2) +
	            											"/" + rs.getString(3) +
	            											"/" + rs.getString(4) +
	            											"/" + rs.getString(5) +
	            											"/" + rs.getString(6) +
	            											"/" + rs.getString(7) +
	            											"/" + rs.getString(8);
						            				
						            				Login_data.add(LikeSend_res);
						            				
						            				String select2 = "select count(*) from pet where User_no= " + num.get(i) + ";";
						            				rs = statement.executeQuery(select2);
						            				
						            				if(rs.next() == true) {/*4*/
						            				
						            					sl_dog_counts = rs.getInt(1);
						            					
						            					String select3 = "select * from pet where User_no= " + num.get(i) + ";";
						            					rs = statement.executeQuery(select3);
						            					
						            					for(int j =0; j< sl_dog_counts; j++) {
						            						if(rs.next() == true) {/*5*/
							            						String dog = "31" 			+ "_" +
					            										rs.getInt(1) 		+ "/" +
					            										rs.getString(3) 	+ "/" +
					            										rs.getString(4) 	+ "/" +
					            										rs.getString(5) 	+ "/" +
					            										rs.getInt(6) 		+ "/" +
					            										rs.getString(7) 	+ "/" +
					            										rs.getString(8) 	+ "/" +
					            										rs.getString(9) 	+ "/" +
					            										rs.getString(10) 	+ "/" +
					            										rs.getString(11) 	+ "/" +
					            										rs.getString(12);
							            						
							            						Login_data.add(dog);
						            						} else {
						            							
						            						}
						            					}
						            				} else {
						            					
						            				}
	
						            			} else {
						            				
						            			}
				            				}
				            				
				            			} else {
				            				
				            			}
				            		} else {
																									//30.보낸신청사람정보
				            		}
				            		
				            		int rl_counts = 0;
				            		int rl_dog_counts = 0;
				            		String rl_sql = "select count(*) from likes where Recv_ID=" + myUser_no + ";";
	
				            		rs = statement.executeQuery(rl_sql);

				            		if(rs.next() == true ) {/*1*/
				            			rl_counts = rs.getInt(1);
				            			
				            			String select_rlikes = "select Send_ID from likes where Recv_ID=" + myUser_no + ";";
				            			rs = statement.executeQuery(select_rlikes);
				            			
				            			if(rs.next() == true) {/*2*/
				            				ArrayList<Integer> num = new ArrayList<Integer>();
				            				for(int i = 0; i < rl_counts; i++) {
				            					num.add(rs.getInt(1));
				            					if(rs.next() == false) {
				            						System.out.println("No more Recv");
				            					}	//김경훈 롤 개못행!!!!!!!!!!!
				            				}
				            				
				            				for(int i = 0; i < rl_counts; i++ ) {
				            					String select1 = "select user_no,U_NAME,U_BIRTH,U_GENDER,U_NICK,U_REGION,U_WALKTIME,U_INTRO from user where user_no=" + num.get(i) + ";";		
						            			rs = statement.executeQuery(select1);
						            			
						            			if(rs.next() == true) {/*3*/
						            				
						            				String LikeRecv_res = "40" +
						            						"_" + rs.getInt(1) +
	            											"/" + rs.getString(2) +
	            											"/" + rs.getString(3) +
	            											"/" + rs.getString(4) +
	            											"/" + rs.getString(5) +
	            											"/" + rs.getString(6) +
	            											"/" + rs.getString(7) +
	            											"/" + rs.getString(8);
						            				
						            				Login_data.add(LikeRecv_res);
						            				
						            				String select2 = "select count(*) from pet where User_no= " + num.get(i) + ";";
						            				rs = statement.executeQuery(select2);
						            				
						            				if(rs.next() == true) {/*4*/
						            				
						            					rl_dog_counts = rs.getInt(1);
						            					
						            					String select3 = "select * from pet where User_no= " + num.get(i) + ";";
						            					rs = statement.executeQuery(select3);
						            					
						            					for(int j =0; j< rl_dog_counts; j++) {
						            						if(rs.next() == true) {/*5*/
							            						String dog = "41" 			+ "_" +
					            										rs.getInt(1) 		+ "/" +
					            										rs.getString(3) 	+ "/" +
					            										rs.getString(4) 	+ "/" +
					            										rs.getString(5) 	+ "/" +
					            										rs.getInt(6) 		+ "/" +
					            										rs.getString(7) 	+ "/" +
					            										rs.getString(8) 	+ "/" +
					            										rs.getString(9) 	+ "/" +
					            										rs.getString(10) 	+ "/" +
					            										rs.getString(11) 	+ "/" +
					            										rs.getString(12);
							            						
							            						Login_data.add(dog);
						            						} else {
						            							
						            						}
						            					}
						            				} else {
						            					
						            				}
	
						            			} else {
						            				
						            			}
				            				}
				            				
				            			} else {
				            				
				            			}
				            		} else {
																									//40.받은신청사람정보
				            		}											
				            		//===
				            		String fr_sql = "select Fr_list from myfriends where My_userno= " + myUser_no + ";";
				            		rs = statement.executeQuery(fr_sql);
				            		if(rs.next() == true) {
				            			String fr_list = rs.getString(1);
				            			String[] Fr_list = fr_list.split("#");
				            			
				            			for(int i = 1; i < Fr_list.length; i++) {
				            				String select1 = "select user_no,U_NAME,U_BIRTH,U_GENDER,U_NICK,U_REGION,U_WALKTIME,U_INTRO from user where U_NICK=" + "\"" + Fr_list[i] +"\"" + ";";
				            				rs = statement.executeQuery(select1);
				            				if(rs.next() == true) {
				            					String friend = "50" +
					            						"_" + rs.getInt(1) +
            											"/" + rs.getString(2) +
            											"/" + rs.getString(3) +
            											"/" + rs.getString(4) +
            											"/" + rs.getString(5) +
            											"/" + rs.getString(6) +
            											"/" + rs.getString(7) +
            											"/" + rs.getString(8);
				            					Login_data.add(friend);
				            					
				            					int fr_num = rs.getInt(1);
				            					
				            					String select2 = "select count(*) from pet where User_no= " + fr_num + ";";
					            				rs = statement.executeQuery(select2);
					            				
					            				int fr_dogs = 0;
					            				
					            				if(rs.next() == true) {
					            					fr_dogs = rs.getInt(1);
					            					
					            					String select3 = "select * from pet where User_no= " + fr_num + ";";
					            					rs = statement.executeQuery(select3);
					            					
					            					for(int j = 0; j < fr_dogs; j++) {
					            						if(rs.next() == true) {
					            							String dog = "51" 			+ "_" +
				            										rs.getInt(1) 		+ "/" +
				            										rs.getString(3) 	+ "/" +
				            										rs.getString(4) 	+ "/" +
				            										rs.getString(5) 	+ "/" +
				            										rs.getInt(6) 		+ "/" +
				            										rs.getString(7) 	+ "/" +
				            										rs.getString(8) 	+ "/" +
				            										rs.getString(9) 	+ "/" +
				            										rs.getString(10) 	+ "/" +
				            										rs.getString(11) 	+ "/" +
				            										rs.getString(12);
						            						
						            						Login_data.add(dog);
					            						} else {
					            							
					            						}
					            					}
					            				} else {
					            					
					            				}
				            				}else {
				            					
				            				}
				            			}
				            			
				            		} else {
				            																									//친구목록정보
				            		}
				            		
				            		//getUserinfo();							//바뀐 정보로 정규회원수 갱신;
				            		return Login_data;
					            	//=====================================================================================================================
	
			            		} else if (rs.getInt(19) == 0 ) {		//임시회원 로그인
			            			String update = "update user set U_TOKEN=" + "\"" + tokens[4] + "\"" + " where U_ID=" + "\"" + tokens[0]+ "\""+ ";";
			            			   
				            		statement.executeUpdate(update); //로그인성공하면 토큰 값저장
				            		
			            			consoleLog(Templogin_Success);
			            			Login_data.add("0" +"_"+ Templogin_Success);
			            			return Login_data;
			            			
			            		} else {
			            			return null;
			            		}
				            	//=====================================================================================================================
	
			            	}
			            	else {
			            		consoleLog(pw_Fail);
			            		Login_data.add("0" +"_"+pw_Fail);
			            		return Login_data;
			            	}
			            }
			            else {
			            	consoleLog(id_Fail);
			            	Login_data.add("0" +"_"+id_Fail);
		            		return Login_data;
			            }
			            
	            	} else if(rs.next() == false) {
	            			Login_data.add("0" +"_"+id_Fail);
		            		return Login_data;
							
					} else {
						return null;
					}

				} 
				catch (SQLException e) {
					// TODO Auto-generated catch block
						return null;
				}
	}
//===================================================================================================================================================
	public String Id_Check_db(String id) {
		try {
			
			statement = con.createStatement();
			
			String sql = "select U_ID from user where U_ID=" + "\"" + id + "\"" + ";";
			
			rs = statement.executeQuery(sql);
			if(rs.next()== true) {
				
				if(rs.getString(1).equals(id)) {
					consoleLog(Duplication_of_ID);
					return Duplication_of_ID;
				} else {
					return null;
				}
			} else {
				consoleLog(Available_ID);
				return Available_ID;
			}
		} catch (SQLException e) {
			return null;
		}
	}
//===================================================================================================================================================
	public String NickName_Check_db(String nick) {
		try {
			
			statement = con.createStatement();
			
			String sql = "select U_NICK from user where U_NICK=" + "\"" + nick + "\"" + ";";
			
			rs = statement.executeQuery(sql);
			if(rs.next() == true) {
			
				if(rs.getString(1).equals(nick)) {
					consoleLog(Duplication_of_Nick);
					return Duplication_of_Nick;
				} else {
					return null;
				}
			}else {
				consoleLog(Available_Nick);
				return Available_Nick;
			}
			
		} catch (SQLException e) {
			return null;
		}
	}
//===================================================================================================================================================
	public String SignUp_db(String Msg) {
		try {
			String[] tokens = Msg.split("/");
			
			String name = tokens[0];	//이름
			String birth = tokens[1];   //생년월일 (+나이)
			String gender = tokens[2];	//성별
			String phone = tokens[3];	//전화번호
			String id = tokens[4];	    //아이디
			String pw = tokens[5];	    //비밀번호
			String nick = tokens[6];	//닉네임
			String region = tokens[7];	//region
			String walktime = tokens[8];//산책시간
			String date = tokens[9];    //가입날짜
			String intro = tokens[10];	//자기소개
			String token = tokens[11];	//토큰
			
			statement = con.createStatement();
		
			//String select = "select UID from user where UID=" + "\"" + id + "\";";
			String insert = "insert into user(U_NAME, U_BIRTH,U_GENDER,U_PHONE,U_ID,U_PW,U_NICK,U_REGION,U_WALKTIME,U_DATE,U_INTRO,U_TOKEN) "
					+ "values("+"\"" + name + "\"" + "," + "\"" + birth +"\"" + "," + "\"" + gender +"\"" + "," + "\"" + phone +"\"" + "," + 
					"\"" + id +"\"" + "," + "\"" + pw +"\"" + "," + "\"" + nick +"\"" + "," + "\"" + region +"\"" + "," + "\"" + walktime +"\"" + "," +
					"\"" + date +"\"" + "," + "\"" + intro +"\"" + "," + "\"" + token +"\""+ ");";
			
			statement.executeUpdate(insert);
			consoleLog(SignUp_Success);
			
			return SignUp_Success;

		}catch (SQLException e) {
			// TODO Auto-generated catch block
			consoleLog(SignUp_Failed);
			return SignUp_Failed;
		}
	}
//===================================================================================================================================================
	public ArrayList<String> PetRegi_db(String Msg) {
		ArrayList<String> p_data = new ArrayList<String>();
		try {
			
			String[] tokens = Msg.split("/");
			
			String name = tokens[0];	//이름
			String type = tokens[1];   //생년월일 (+나이)
			String size = tokens[2];	//성별
			int age = Integer.parseInt(tokens[3]);	//전화번호
			String birth = tokens[4];	    //아이디
			String gender = tokens[5];	    //비밀번호
			int neutral = Integer.parseInt(tokens[6]);	//닉네임
			String vaccine = tokens[7];	//region
			String character = tokens[8];//산책시간
			String intro = tokens[9];	//자기소개
			String id = tokens[10];

			statement = con.createStatement();
			
			String select = "select user_no from user where U_ID=" + "\"" + id + "\"" + ";";
			
			rs = statement.executeQuery(select);
		    rs.next();
			
			int User_no = rs.getInt(1);
		
			String insert = "insert into pet(User_no, P_NAME, P_TYPE, P_SIZE,P_AGE,P_BIRTH,P_GENDER,P_NEUTRAL,P_VACCINE,P_CHARACTER,P_INTRO) "
					+ "values(" + User_no + "," +"\"" + name + "\"" + "," + "\"" + type +"\"" + "," + "\"" + size +"\"" + "," + age + "," + 
					"\"" + birth +"\"" + "," + "\"" + gender +"\"" + ","  + neutral + "," + "\"" + vaccine + "\"" + "," + "\"" + character +"\"" + "," +
					"\"" + intro + "\""+ ");";
			
			statement.executeUpdate(insert);
			
			String select1 = "select U_MEMBER from user where U_ID=" + "\"" + id + "\"" + ";";
			
			rs = statement.executeQuery(select1);
			rs.next();
			
			int mem = rs.getInt(1);
			if(mem == 1) {
				String update = "update user set U_MEMBER = 2 where U_ID=" + "\"" + id + "\"" + ";";
				statement.executeUpdate(update);
				
				getUserinfo();
			}
			
			String pet = "40" + 
					"_" + name +
					"/" + type +
					"/" + size +
					"/" + age +
					"/" + birth +
					"/" + gender +
					"/" + neutral +
					"/" + vaccine +
					"/" + character +
					"/" + intro;
			p_data.add("0_"+PetRegi_Success);
			p_data.add(pet);
			
			consoleLog(PetRegi_Success);
			
			return p_data;

		}catch (SQLException e) {
			// TODO Auto-generated catch block
			p_data.removeAll(p_data);
			p_data.add("0_"+PetRegi_Failed);
			consoleLog(PetRegi_Failed);
			return p_data;
		}
	}
//===================================================================================================================================================
	public ArrayList<String> RandomMatch_db(String nick) {
		ArrayList<String> name_list = new ArrayList<String>();
		try {
			name_list.add("0_" + nick);
			statement = con.createStatement();
			String select = "select user_no from user where U_NICK=" + "\"" + nick  + "\"" + ";";

			rs = statement.executeQuery(select);
			rs.next();
			int myUser_no = rs.getInt(1);
			
			String select0 = "select Fr_list from myfriends where My_userno= " + myUser_no + ";";
			rs = statement.executeQuery(select0);
			if(rs.next() == true) {
				String fr_list = rs.getString(1);
				String[] Fr_list = fr_list.split("#");
				for(int i = 1; i < Fr_list.length; i++) {
					name_list.add("30_" + Fr_list[i]);
				}
			} else {
				System.out.println("친구없음");
			}
			
			
			String select1 = "select count(*) from user left join likes on user.user_no = likes.Recv_ID where likes.Send_ID=" + myUser_no + ";";
			
			rs = statement.executeQuery(select1);
			
			int send_count = 0;
		
			if(rs.next() == true) {
				send_count = rs.getInt(1);
			}
			String select2= "select user.U_NICK from user left join likes on user.user_no = likes.Recv_ID where likes.Send_ID=" + myUser_no +";";
			
			rs = statement.executeQuery(select2);
			if(rs.next() == true) {
				for(int i = 0; i < send_count; i++) {
					name_list.add("10_" + rs.getString(1));
					
					rs.next();
				}
				
			}else {
				System.out.println("SEND 없음");
			}
			
			String select3 = "select count(*) from user left join likes on user.user_no = likes.Send_ID where likes.Recv_ID=" + myUser_no + ";";
			
			rs = statement.executeQuery(select3);
			
			int recv_count = 0;
		
			if(rs.next() == true) {
				recv_count = rs.getInt(1);
			}
			
			String select4= "select user.U_NICK from user left join likes on user.user_no = likes.Send_ID where likes.Recv_ID=" + myUser_no +";";
			
			rs = statement.executeQuery(select4);
			if(rs.next() == true) {
				for(int i = 0; i < recv_count; i++) {
					name_list.add("20_" +rs.getString(1));
					
					rs.next();
				}
				
			}else {
				System.out.println("RECV 없음");
			}
			
			//getUserinfo();
			return name_list;
			
		}catch(SQLException e) {
			return null;
		}
	}
//===================================================================================================================================================
	public ArrayList<String> GetRandomDog_db(int user_no) {
		ArrayList<String> dogList = new ArrayList<String>();
		try {
			statement = con.createStatement();
			
			int dogCount = 0;
			
			String select0 = "select count(*) from pet where User_no= " + user_no + ";";
			rs = statement.executeQuery(select0);
			if(rs.next() == true) {
				dogCount = rs.getInt(1);
				
				String select1 = "select * from pet where User_no=" + user_no + ";";
				rs = statement.executeQuery(select1);
				for(int i =0; i<dogCount; i++) {
					if(rs.next() == true){
						String doginfo = 
								rs.getInt(1) + "/" +
								rs.getString(3) + "/" +
								rs.getString(4) + "/" +
								rs.getString(5) + "/" +
								rs.getInt(6) + "/" +
								rs.getString(7) + "/" +
								rs.getString(8) + "/" +
								rs.getString(9) + "/" +
								rs.getString(10) + "/" +
								rs.getString(11) + "/" +
								rs.getString(12);
						
						dogList.add(doginfo);
					} else {
						System.out.println("더이상 없음");
					}
				}
				
				
			} else {
				System.out.println("강아지 갯수 없음");
				return null;
			}
			return dogList;

		} catch(SQLException e ) {
			return null;	
		}
	}
//===================================================================================================================================================
	public ArrayList<String> LikeSend_db(String Msg) {
		
		ArrayList<String> send = new ArrayList<String>();
		try {
			String[] tokens = Msg.split("/");
			
			String mynick = tokens[0];
			String yournick = tokens[1];
			
			
			
			statement = con.createStatement();
			
			String select1 = "select user_no from user where U_NICK=" + "\"" + mynick + "\"" + ";";
			rs = statement.executeQuery(select1);
			rs.next();
			int myid_num = rs.getInt(1);
			
			String select2 = "select user_no from user where U_NICK=" + "\"" + yournick + "\"" + ";";
			
			rs = statement.executeQuery(select2);
			rs.next();
			int yourid_num = rs.getInt(1);

			
			
			String select0 = "select * from likes where Send_ID= " + myid_num + " and Recv_ID= " + yourid_num +";";		//중복 체크
			rs = statement.executeQuery(select0);
			
			if(rs.next() == true) {
				if(rs.getInt(1) == myid_num) {
					if(rs.getInt(2) == yourid_num) {
						if(rs.getInt(3) == Stand_by) {
							send.add("0" + "!" + LikeSend_Multiple);
							
						} 
					}
				}
				return send;
			} else if(rs.next() == false) {

				send.add("0" + "!" + LikeSend_Success);
				
				String insert  = "insert into likes values" + "(" + myid_num + "," + yourid_num + "," + Stand_by + ")"+" ;";
				statement.executeUpdate(insert);
				
				String select3 = "select user_no,U_NAME,U_BIRTH,U_GENDER,U_NICK,U_REGION,U_WALKTIME,U_INTRO,U_TOKEN from user where user_no=" + yourid_num + ";";		
				rs = statement.executeQuery(select3);
				if(rs.next() == true) {		//신청보낸사람이 받는 상대방 정보
					String LikeSend_res = 30 +
							"!" + rs.getInt(1) +
							"/" + rs.getString(2) +
							"/" + rs.getString(3) +
							"/" + rs.getString(4) +
							"/" + rs.getString(5) +
							"/" + rs.getString(6) +
							"/" + rs.getString(7) +
							"/" + rs.getString(8) +
							"/" + rs.getString(9);
					
					send.add(LikeSend_res);
					send.add("100" + "!" + rs.getString(9));

					//===============
            		
            		int p_count1 = GetPetCount(rs.getInt(1));
            		if(p_count1 != 0) {
            			String select_dogs = "select * from pet where User_no=" + yourid_num + ";";
	            		
	            		rs = statement.executeQuery(select_dogs);
	            		
	            		for(int i = 0; i < p_count1; i++ ) {
	            			
	            			rs.next();
	            			String dog = "31" + 
	            					"!" + rs.getInt(1) +
	            					"/" + rs.getString(3) +
		            				"/" + rs.getString(4) +
		            				"/" + rs.getString(5) +
		            				"/" + rs.getInt(6) +
		            				"/" + rs.getString(7) +
		            				"/" + rs.getString(8) +
		            				"/" + rs.getString(9) +
		            				"/" + rs.getString(10) +
		            				"/" + rs.getString(11) +
		            				"/" + rs.getString(12);
	            													
	            			send.add(dog);																//31. 내 강아지정보
	            			
	            		}
            			
            		} else {
            			System.out.println("상대방 개정보 없음");
            		}
					//=================
					
					String select4 = "select user_no,U_NAME,U_BIRTH,U_GENDER,U_NICK,U_REGION,U_WALKTIME,U_INTRO from user where user_no=" + myid_num + ";";		
					rs = statement.executeQuery(select4);
					if(rs.next() == true) {	  //신청받은 사람이 받는 상대방정보
						String LikeRecv_res = 40 +
								"!" + rs.getInt(1) +
								"/" + rs.getString(2) +
								"/" + rs.getString(3) +
								"/" + rs.getString(4) +
								"/" + rs.getString(5) +
								"/" + rs.getString(6) +
								"/" + rs.getString(7) +
								"/" + rs.getString(8);
						
						send.add(LikeRecv_res);
						
						int p_count2 = GetPetCount(rs.getInt(1));
	            		if(p_count2 != 0) {
	            			String select_dogs = "select * from pet where User_no=" + myid_num + ";";
		            		
		            		rs = statement.executeQuery(select_dogs);
		            		
		            		for(int i = 0; i < p_count2; i++ ) {
		            			
		            			rs.next();
		            			String dog = "41" + 
		            					"!" + rs.getInt(1) +
		            					"/" + rs.getString(3) +
			            				"/" + rs.getString(4) +
			            				"/" + rs.getString(5) +
			            				"/" + rs.getInt(6) +
			            				"/" + rs.getString(7) +
			            				"/" + rs.getString(8) +
			            				"/" + rs.getString(9) +
			            				"/" + rs.getString(10) +
			            				"/" + rs.getString(11) +
			            				"/" + rs.getString(12);
		            													
		            			send.add(dog);																//31. 내 강아지정보
		            			
		            		}
	            			
	            		} else {
	            			System.out.println("상대방 개정보 없음");
	            			return null;
	            		}
					}else {
						return null;
					}
				} else {
					return null;
				}
			}
			return send;
	
		} catch (SQLException e) {
			consoleLog(LikeSend_Failed);
			return null;
		}
	}
//===================================================================================================================================================
	public ArrayList<String> LikeCancel_db(String Msg) {
		ArrayList<String> youAndme = new ArrayList<String>();
		int myid_num;
		int yourid_num;
		try {
			statement = con.createStatement();
			
			String[] tokens = Msg.split("/");
			
			String mynick = tokens[0];
			String yournick = tokens[1];
			
			statement = con.createStatement();
			String select1 = "select user_no from user where U_NICK=" + "\"" + mynick + "\"" + ";";			//닉을 -> 유저넘버로
			rs = statement.executeQuery(select1);
			if(rs.next()==true) {
				myid_num = rs.getInt(1);
			} else {
				
				return null;
			}

			String select2 = "select user_no from user where U_NICK=" + "\"" + yournick + "\"" + ";";		//닉을 -> 유저넘버로
			rs = statement.executeQuery(select2);
			rs.next();
			yourid_num = rs.getInt(1);
			
			String select0 = "select * from likes where Send_ID= " + myid_num + " and Recv_ID= " + yourid_num +";";		//중복 체크
			rs = statement.executeQuery(select0);
			
			if(rs.next() == true) {
				if(rs.getInt(1) == myid_num) {
					if(rs.getInt(2) == yourid_num) {
						if(rs.getInt(3) == Stand_by) {
							String delete = "delete from likes where Send_ID= " + myid_num + " and Recv_ID= " + yourid_num + ";";
							statement.executeUpdate(delete);
							
						} 
					}
				}
				consoleLog(LikeCancel_Success);
				youAndme.add("0" + "_" + LikeCancel_Success);
				youAndme.add("30" + "_" + myid_num);
				youAndme.add("40" + "_" + yourid_num);
				
				return youAndme;
			}else {
				return null;
			}
			
			
		} catch(SQLException e) {
			return null;
		}
	}
//===================================================================================================================================================
	public ArrayList<String> LikeDeny_db(String Msg) {
		ArrayList<String> youAndme = new ArrayList<String>();
		int myid_num;	//누군가로부터 신청받은 나의 번호
		int yourid_num;	//나에게 신청보낸 너의 번호
		try {
			statement = con.createStatement();
			
			String[] tokens = Msg.split("/");
			
			String mynick = tokens[0];
			String yournick = tokens[1];
			
			statement = con.createStatement();
			String select1 = "select user_no from user where U_NICK=" + "\"" + mynick + "\"" + ";";			//닉을 -> 유저넘버로
			rs = statement.executeQuery(select1);
			if(rs.next()==true) {
				myid_num = rs.getInt(1);
			} else {
				
				return null;
			}

			String select2 = "select user_no from user where U_NICK=" + "\"" + yournick + "\"" + ";";		//닉을 -> 유저넘버로
			rs = statement.executeQuery(select2);
			rs.next();
			yourid_num = rs.getInt(1);
			
			String select0 = "select * from likes where Send_ID= " + yourid_num + " and Recv_ID= " + myid_num +";";		//중복 체크
			rs = statement.executeQuery(select0);
			
			if(rs.next() == true) {
				if(rs.getInt(1) == yourid_num) {
					if(rs.getInt(2) == myid_num) {
						if(rs.getInt(3) == Stand_by) {
							String delete = "delete from likes where Send_ID= " + yourid_num + " and Recv_ID= " + myid_num + ";";
							statement.executeUpdate(delete);
							
						} 
					}
				}
				consoleLog(LikeDeny_Success);
				youAndme.add("0" + "_" + LikeDeny_Success);
				youAndme.add("30" + "_" + myid_num);
				youAndme.add("40" + "_" + yourid_num);
				
				return youAndme;
			}else {
				return null;
			}
			
			
		} catch(SQLException e) {
			return null;
		}
	}
//===================================================================================================================================================
	public ArrayList<String> LikeAceept_db(String Msg) {
		ArrayList<String> fr_data = new ArrayList<String>();
		int myid_num = 0;
		int yourid_num = 0;
		try {
			
			String[] tokens = Msg.split("/");
			
			String mynick = tokens[0];
			String yournick = tokens[1];
			
			statement = con.createStatement();
			String select1 = "select user_no from user where U_NICK=" + "\"" + mynick + "\"" + ";";			//닉을 -> 유저넘버로
			rs = statement.executeQuery(select1);
			rs.next();
			myid_num = rs.getInt(1);

			String select2 = "select user_no from user where U_NICK=" + "\"" + yournick + "\"" + ";";		//닉을 -> 유저넘버로
			rs = statement.executeQuery(select2);
			rs.next();
			yourid_num = rs.getInt(1);

			//===========================================
			
			String select3 = "select Fr_list from myfriends where My_userno=" + myid_num + ";";
			rs = statement.executeQuery(select3);
			if(rs.next() == true) {	
				
					//친구리스트가있을떄==============================================================================================================
					String friends_list = rs.getString(1);
					String[] fr_list = friends_list.split("#");
					
					for(int i = 1; i < fr_list.length; i++) {
						
						if(fr_list[i].equals(yournick)) {
							
							fr_data.add("0" + "!" + LikeAccept_Duple);
							
							return fr_data;
						}
	
					}
					
					System.out.println("중복값없음");
					fr_data.add("0" + "!" + LikeAccept_Success);
					
					String update = "update myfriends set Fr_list = " + "\"" + friends_list + "#" + yournick +"\"" + "where My_userno = " + myid_num +";";
					statement.executeUpdate(update);		//친구를 DB에 등록
					
					
					
					String delete = "delete from likes where Send_ID= " + yourid_num + " and Recv_ID= " + myid_num + ";";
					statement.executeUpdate(delete);
					
					String select4 = "select user_no,U_NAME,U_BIRTH,U_GENDER,U_NICK,U_REGION,U_WALKTIME,U_INTRO,U_TOKEN from user where user_no=" + yourid_num + ";";		
					rs = statement.executeQuery(select4);
					if(rs.next() == true) {	  //수락한 사람이 받는 상대방정보 (사람 개)
						String fr_res = 30 +
								"!" + rs.getInt(1) +
								"/" + rs.getString(2) +
								"/" + rs.getString(3) +
								"/" + rs.getString(4) +
								"/" + rs.getString(5) +
								"/" + rs.getString(6) +
								"/" + rs.getString(7) +
								"/" + rs.getString(8) +
								"/" + rs.getString(9);
						
						fr_data.add(fr_res);
						fr_data.add("100" + "!" + rs.getString(9));
						
						int p_count = GetPetCount(rs.getInt(1));
	            		if(p_count != 0) {
	            			String select_dogs = "select * from pet where User_no=" + yourid_num + ";";
		            		
		            		rs = statement.executeQuery(select_dogs);
		            		
		            		for(int i = 0; i < p_count; i++ ) {
		            			
		            			rs.next();
		            			String dog = "31" + 
		            					"!" + rs.getInt(1) +
		            					"/" + rs.getString(3) +
			            				"/" + rs.getString(4) +
			            				"/" + rs.getString(5) +
			            				"/" + rs.getInt(6) +
			            				"/" + rs.getString(7) +
			            				"/" + rs.getString(8) +
			            				"/" + rs.getString(9) +
			            				"/" + rs.getString(10) +
			            				"/" + rs.getString(11) +
			            				"/" + rs.getString(12);
		            													
		            			fr_data.add(dog);																//31. 내 강아지정보
		            			
		            		}
	            			
	            		} else {
	            			System.out.println("상대방 개정보 없음");
	            			return null;
	            		}
					}else {
						return null;
					}
					
					String select5 = "select user_no,U_NAME,U_BIRTH,U_GENDER,U_NICK,U_REGION,U_WALKTIME,U_INTRO from user where user_no=" + myid_num + ";";		
					rs = statement.executeQuery(select5);
					if(rs.next() == true) {	  //신청보낸 사람이 받는 수락한상대방정보
						String fr_res = 40 +
								"!" + rs.getInt(1) +
								"/" + rs.getString(2) +
								"/" + rs.getString(3) +
								"/" + rs.getString(4) +
								"/" + rs.getString(5) +
								"/" + rs.getString(6) +
								"/" + rs.getString(7) +
								"/" + rs.getString(8);
						
						fr_data.add(fr_res);
						
						int p_count2 = GetPetCount(rs.getInt(1));
	            		if(p_count2 != 0) {
	            			String select_dogs = "select * from pet where User_no=" + myid_num + ";";
		            		
		            		rs = statement.executeQuery(select_dogs);
		            		
		            		for(int i = 0; i < p_count2; i++ ) {
		            			
		            			rs.next();
		            			String dog = "41" + 
		            					"!" + rs.getInt(1) +
		            					"/" + rs.getString(3) +
			            				"/" + rs.getString(4) +
			            				"/" + rs.getString(5) +
			            				"/" + rs.getInt(6) +
			            				"/" + rs.getString(7) +
			            				"/" + rs.getString(8) +
			            				"/" + rs.getString(9) +
			            				"/" + rs.getString(10) +
			            				"/" + rs.getString(11) +
			            				"/" + rs.getString(12);
		            													
		            			fr_data.add(dog);																//31. 내 강아지정보
		            			
		            		}
	            			
	            		} else {
	            			System.out.println("상대방 개정보 없음");
	            			return null;
	            		}
	            		
	            		
					}else {
						return null;
					}
				//return fr_data;
			} else {		
				
					//친구리스트가 없을떄==============================================================================================================
				
					System.out.println("없는친구");
					fr_data.add("0" + "!" + LikeAccept_Success);
				
					String insert = "insert into myfriends(My_userno, Fr_list) values(" + myid_num + "," + "\"" + "#" + yournick  + "\"" + ");";
					statement.executeUpdate(insert);
					
					String delete = "delete from likes where Send_ID= " + yourid_num + " and Recv_ID= " + myid_num + ";";
					statement.executeUpdate(delete);
					
					String select4 = "select user_no,U_NAME,U_BIRTH,U_GENDER,U_NICK,U_REGION,U_WALKTIME,U_INTRO,U_TOKEN from user where user_no=" + yourid_num + ";";		
					rs = statement.executeQuery(select4);
					if(rs.next() == true) {	  //신청받은 사람이 받는 상대방정보
						String fr_res = 30 +
								"!" + rs.getInt(1) +
								"/" + rs.getString(2) +
								"/" + rs.getString(3) +
								"/" + rs.getString(4) +
								"/" + rs.getString(5) +
								"/" + rs.getString(6) +
								"/" + rs.getString(7) +
								"/" + rs.getString(8) +
								"/" + rs.getString(9);
						
						fr_data.add(fr_res);
						fr_data.add("100" + "!" + rs.getString(9));
						
						int p_count = GetPetCount(rs.getInt(1));
	            		if(p_count != 0) {
	            			String select_dogs = "select * from pet where User_no=" + yourid_num + ";";
		            		
		            		rs = statement.executeQuery(select_dogs);
		            		
		            		for(int i = 0; i < p_count; i++ ) {
		            			
		            			rs.next();
		            			String dog = "31" + 
		            					"!" + rs.getInt(1) +
		            					"/" + rs.getString(3) +
			            				"/" + rs.getString(4) +
			            				"/" + rs.getString(5) +
			            				"/" + rs.getInt(6) +
			            				"/" + rs.getString(7) +
			            				"/" + rs.getString(8) +
			            				"/" + rs.getString(9) +
			            				"/" + rs.getString(10) +
			            				"/" + rs.getString(11) +
			            				"/" + rs.getString(12);
		            													
		            			fr_data.add(dog);																//31. 내 강아지정보
		            			
		            		}
	            			
	            		} else {
	            			System.out.println("상대방 개정보 없음");
	            			return null;
	            		}
					}else {
						return null;
					}
					
					String select5 = "select user_no,U_NAME,U_BIRTH,U_GENDER,U_NICK,U_REGION,U_WALKTIME,U_INTRO from user where user_no=" + myid_num + ";";		
					rs = statement.executeQuery(select5);
					if(rs.next() == true) {	  //신청보낸 사람이 받는 수락한상대방정보
						String fr_res = 40 +
								"!" + rs.getInt(1) +
								"/" + rs.getString(2) +
								"/" + rs.getString(3) +
								"/" + rs.getString(4) +
								"/" + rs.getString(5) +
								"/" + rs.getString(6) +
								"/" + rs.getString(7) +
								"/" + rs.getString(8);
						
						fr_data.add(fr_res);
						
						int p_count2 = GetPetCount(rs.getInt(1));
	            		if(p_count2 != 0) {
	            			String select_dogs = "select * from pet where User_no=" + myid_num + ";";
		            		
		            		rs = statement.executeQuery(select_dogs);
		            		
		            		for(int i = 0; i < p_count2; i++ ) {
		            			
		            			rs.next();
		            			String dog = "41" + 
		            					"!" + rs.getInt(1) +
		            					"/" + rs.getString(3) +
			            				"/" + rs.getString(4) +
			            				"/" + rs.getString(5) +
			            				"/" + rs.getInt(6) +
			            				"/" + rs.getString(7) +
			            				"/" + rs.getString(8) +
			            				"/" + rs.getString(9) +
			            				"/" + rs.getString(10) +
			            				"/" + rs.getString(11) +
			            				"/" + rs.getString(12);
		            													
		            			fr_data.add(dog);																//41. 내 강아지정보
		            			
		            		}
	            			
	            		} else {
	            			System.out.println("상대방 개정보 없음");
	            			return null;
	            		}
					}else {
						return null;
					}
				
				//return fr_data;
			}
			
			String select4 = "select Fr_list from myfriends where My_userno=" + yourid_num + ";";		//여기선 수락을 당한 사람의 친구리스트에 수락한 사람의 친구를 저장하는곳.
			rs = statement.executeQuery(select4);
			if(rs.next() == true) {
				//친구리스트가있을떄==============================================================================================================
				String friends_list = rs.getString(1);
				String[] fr_list = friends_list.split("#");
				
				for(int i = 1; i < fr_list.length; i++) {
					
					if(fr_list[i].equals(yournick)) {
						
						//fr_data.add("0" + "!" + LikeAccept_Duple);
						System.out.println("상대방 친구리스트 중복");
						break;
						//return fr_data;
					}

				}
				
				System.out.println("중복값없음");
				
				String update = "update myfriends set Fr_list = " + "\"" + friends_list + "#" + mynick +"\"" + "where My_userno = " + yourid_num +";";
				statement.executeUpdate(update);		//친구를 DB에 등록
				
				return fr_data;

			} else {
				//친구리스트가없을떄==============================================================================================================

				System.out.println("없는친구");
			
				String insert = "insert into myfriends(My_userno, Fr_list) values(" + yourid_num + "," + "\"" + "#" + mynick  + "\"" + ");";
				statement.executeUpdate(insert);
				
				return fr_data;

			}
			
			
		}catch(SQLException e) {
			return null;
		}
			
	}
	
	public String Chat_token(String Msg) {
		try {
			statement = con.createStatement();
			
			String select = "select U_TOKEN from user where U_NICK=" + "\"" + Msg + "\"" + ";";
			rs = statement.executeQuery(select);
			if(rs.next() == true) {
				String token = rs.getString(1);
				return token;
			}else {
				return null;
			}
			
		}catch(SQLException e) {
			return null;
		}
	}
	
//===================================================================================================================================================
	public String UserPic_db(String msg,String id,int i) {
		try {
			
			statement = con.createStatement();
			
			//String sql = "select U_Pic1,U_Pic2,U_Pic3,U_Pic4 from user where U_ID=" + "\"" + id + "\"" + ";";
			String update ="update user set U_Pic" + i + " = " + "\"" + msg + "\"" + "where U_ID=" + "\"" + id + "\"" + ";";
			statement.executeUpdate(update);
			
			consoleLog(UserPicSave_Success);
			return UserPicSave_Success;
			
			
		} catch (SQLException e) {
			consoleLog(UserPicSave_Failed);
			return UserPicSave_Failed;
		}
		
	}
	
	public String petPic_db(String id, String dogname,int i) {
		
		try {
			
			statement = con.createStatement();
			
			
			String select = "select user_no from user where U_ID=" + "\"" + id + "\"" + ";" ;
			
			rs = statement.executeQuery(select);
			rs.next();
			
			int user_no = rs.getInt(1);
			
			//String sql = "select U_Pic1,U_Pic2,U_Pic3,U_Pic4 from user where U_ID=" + "\"" + id + "\"" + ";";
			String update ="update pet set P_Pic" + i + " = " + "\"" + dogname + i + "\"" + "where P_NAME=" + "\"" + dogname + "\"" + ";";
			statement.executeUpdate(update);
			
			consoleLog(UserPicSave_Success);
			return UserPicSave_Success;
			
			
		} catch (SQLException e) {
			consoleLog(UserPicSave_Failed);
			return UserPicSave_Failed;
		}
		
		
	}
	//======================================회원가입시 회원수 갱신==========================================================================================

	private int GetPetCount(int userno) {
		try {
			statement = con.createStatement();
			
			String sql = "select count(*) from pet where User_no=" + userno + ";";
			
			rs = statement.executeQuery(sql);
			
			if(rs.next() == true ) {
			
				int p_count = rs.getInt(1);
				consoleLog("총 강아지 수: " + p_count);
			
				return p_count;
			}else {
				return 0;
			}
		} catch (SQLException e) {
			System.out.println("오류류류류");
			return 0;
			
		}
	}
	public void GetCount() {
		try {
			statement = con.createStatement();
			
			String sql = "select count(*) from user;";
			
			rs = statement.executeQuery(sql);
			
			rs.next();
			
			u_count = rs.getInt(1);
			consoleLog("총 회원 수: " + u_count);
		
			
		} catch (SQLException e) {
			System.out.println("오류류류류");
			
		}
	}
	
	public void getUserinfo() {
		try {
			u_info.removeAll(u_info);
			
			statement = con.createStatement();
			
			String sql0 = "select count(*) from user;";
			
			rs = statement.executeQuery(sql0);
			
			rs.next();
			int Alluser_count = rs.getInt(1);
			
			String sql1 = "select * from user;";
			
			rs = statement.executeQuery(sql1);
			
			int r_user = 0;
			
			for(int i = 0; i < Alluser_count; i++) {
				rs.next();
				if(rs.getInt(19)==2) {
					User_info info = new User_info(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),
														rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),rs.getString(12), 
															Double.parseDouble(rs.getString(13)), Double.parseDouble(rs.getString(14)), rs.getInt(19));
					
					u_info.add(info); //데이터베이스로부터 가져온 정보 저장
					//System.out.println(u_info.get(i).U_ID + ":" + u_info.get(i).U_MEMBER);
					r_user++;
				}
			}
			consoleLog("정규 회원 수: " + r_user);

		} catch(SQLException e) {
			System.out.println("오류류류류");

		}
	}

//===================================================================================================================================================
	private void consoleLog(String log) {
        System.out.println("[투게더 서버] " + socket.getInetAddress().toString() + " - " +log);
    }
}
