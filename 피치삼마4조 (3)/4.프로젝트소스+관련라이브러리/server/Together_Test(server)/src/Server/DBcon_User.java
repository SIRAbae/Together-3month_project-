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
������ü�� ������ 3���� ���
1. execute -> ���̺� ����, ����,���� �� �����ͺ��̽� ���� ��ɾ� ���    create..
2. excuteUpdate -> ���ڵ� ���� ���� ������ ������ ���� ��ɾ� ���    insert, update, delete..
3. excuteQuery -> ���ڵ� ��ȸ, ���̺� ��ȸ �� ��ȸ ��ɾ� ���    select..
*/

public class DBcon_User {
	String driver = "org.mariadb.jdbc.Driver";
	Connection con = null;
	Statement statement = null;
	ResultSet rs = null;
	Socket socket = null;
	
	int u_count = 0;
	ArrayList<User_info> u_info = null;
	
	private static final String login_Success = "001:���̵�,��й�ȣ ��ġ";
	private static final String id_Fail = "002:���̵� ����ġ";
	private static final String pw_Fail = "003:��� ��ȣ ����ġ";
	private static final String Query_Fail = "004: �����ͺ��̽� �˻� ����";
	private static final String NormalLogin_Success = "007:�Ϲݷα���";
	private static final String Templogin_Success = "006:�ӽ�ȸ���α���";
	
	private static final String SignUp_Success = "101: ȸ�����Լ���";
	private static final String Duplication_of_ID = "102:�̹������ϴ¾��̵�";
	private static final String Available_ID = "103:��밡���Ѿ��̵�";
	private static final String Duplication_of_Nick = "104:�̹������ϴ´г���";
	private static final String Available_Nick = "105:��밡���Ѵг���";
	private static final String SignUp_Failed = "106:ȸ������ ����";

	private static final String UserPicSave_Success = "201:ȸ���������强��";
	private static final String UserPicSave_Failed = "202:ȸ�������������";
	
	private static final String PetRegi_Success = "401:��ϼ���";
	private static final String PetRegi_Failed = "402:��Ͻ���";
	
	private static final String LikeSend_Success = "501:����ģ������";
	private static final String LikeSend_Failed = "502:����ģ�� ����";
	private static final String LikeSend_Multiple = "504:�ߺ�����ģ��";
	private static final String LikeCancel_Success = "505:����ģ�����";
	private static final String LikeDeny_Success = "506:����ģ������";
	private static final String LikeAccept_Success = "509:����ģ������";
	private static final String LikeAccept_Duple = "510:����ģ���ߺ�����";


	
	



	
	private static final String LikeAceept_Success = "601:ģ����������";
	private static final String LikeAceept_Failed = "602:ģ����������";
	
		private static final int Stand_by = 0;
		private static final int ACCEPT = 1;
	
	public DBcon_User(Socket socket,ArrayList<User_info> u) {
		try {
			//0.�õ� ���� üũ�� ������ �ޱ�
			this.socket = socket;
			u_info = u;
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
//===================================================================================================================================================
	public ArrayList<String> Login_db(String Msg) {
		int myUser_no = 0;
		ArrayList<String> Login_data = new ArrayList<String>();
		// 3. ���� ������ ���� Statement ��ü ����
				try {
					String[] tokens = Msg.split("/");
					
					statement = con.createStatement();
					// 4. SQL ���� �ۼ�
		            // ���ǻ���
		            // 1) JDBC���� ������ �ۼ��� ���� �����ݷ�(;)�� ���� �ۼ��Ѵ�.
		            // 2) SELECT �� �� * ���� ��� Į���� �������� �ͺ���
		            //   �����;� �� Į���� ���� ������ִ� ���� ����.
		            // 3) ���ϴ� ����� �����ν� ������ ����, java �ڵ�� ���۾� �ϴ� ���� ������ ����
		            // 4) ������ �� �ٷ� ���� ����� ��� �鿩���⸦ ����ص� ������ ���⿡ ���� !!
					
					String sql = "select * from user where U_ID=" + "\"" + tokens[0] + "\"" + ";";
					
					// 5. ���� ����
		            // ���ڵ���� ResultSet ��ü�� �߰��ȴ�.
		            rs = statement.executeQuery(sql);
		            
		         // 6. ������ ����ϱ�
		            //�����ҽ�~
	            	if(rs.next() == true) {
			            if(rs.getString(6).equals(tokens[0])) {
			            	//1�ܰ� ���̵� ��ġ ->rs ���� �Ѿ������ �������� �ƿ����� ������ catch�� �Ѿ �� ����ü�� �� �� ������. �� �ƴ϶� ��ȯ�� true false �� �Ǵܰ���
			            	if(rs.getString(7).equals(tokens[1])) {
			            		//2�ܰ� ��й�ȣ ��ġ
				            	//=====================================================================================================================
	
			            		if(rs.getInt(19) == 1) {		//�Ϲ�ȸ��	
			            			consoleLog(NormalLogin_Success);
			            			String update = "update user set U_LAT=" + 
			            										"\"" + tokens[2] + "\"" + "," + 
			            										"U_LONG=" +
			            										"\"" + tokens[3] + "\"" + "," +
			            										"U_TOKEN=" +
			            										"\"" + tokens[4] + "\"" + 
			            										" where U_ID=" + "\"" + tokens[0]+ "\""+ ";";
			            			   
				            		statement.executeUpdate(update); //�α��μ����ϸ� ��ū ������
				            		
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
				            				"/" + rs.getInt(19);										//10.user����
				            		
				            		Login_data.add(msg);
		
				            		return Login_data;
			            			
				            	//=====================================================================================================================
			            		} else if(rs.getInt(19) == 2 ) {   //����ȸ�� �α���
			            			
			            			consoleLog(login_Success);
			            			String update = "update user set U_LAT=" + 
			            										"\"" + tokens[2] + "\"" + "," + 
			            										"U_LONG=" +
			            										"\"" + tokens[3] + "\"" + "," +
			            										"U_TOKEN=" +
			            										"\"" + tokens[4] + "\"" + 
			            										" where U_ID=" + "\"" + tokens[0]+ "\""+ ";";
			            			   
				            		statement.executeUpdate(update); //�α��μ����ϸ� ��ū ������
				            		
				            		//String rt_value = login_Success + ":" + rs.getInt(3);
				            		
				            		Login_data.add("0" +"_"+login_Success);													//0.������
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
				            		
				            		Login_data.add(user);																	//10.������
				            		
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
					            													
					            			Login_data.add(dog);																//20. �� ����������
					            			
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
																									//30.������û�������
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
				            					}	//����� �� ������!!!!!!!!!!!
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
																									//40.������û�������
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
				            																									//ģ���������
				            		}
				            		
				            		//getUserinfo();							//�ٲ� ������ ����ȸ���� ����;
				            		return Login_data;
					            	//=====================================================================================================================
	
			            		} else if (rs.getInt(19) == 0 ) {		//�ӽ�ȸ�� �α���
			            			String update = "update user set U_TOKEN=" + "\"" + tokens[4] + "\"" + " where U_ID=" + "\"" + tokens[0]+ "\""+ ";";
			            			   
				            		statement.executeUpdate(update); //�α��μ����ϸ� ��ū ������
				            		
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
			
			String name = tokens[0];	//�̸�
			String birth = tokens[1];   //������� (+����)
			String gender = tokens[2];	//����
			String phone = tokens[3];	//��ȭ��ȣ
			String id = tokens[4];	    //���̵�
			String pw = tokens[5];	    //��й�ȣ
			String nick = tokens[6];	//�г���
			String region = tokens[7];	//region
			String walktime = tokens[8];//��å�ð�
			String date = tokens[9];    //���Գ�¥
			String intro = tokens[10];	//�ڱ�Ұ�
			String token = tokens[11];	//��ū
			
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
			
			String name = tokens[0];	//�̸�
			String type = tokens[1];   //������� (+����)
			String size = tokens[2];	//����
			int age = Integer.parseInt(tokens[3]);	//��ȭ��ȣ
			String birth = tokens[4];	    //���̵�
			String gender = tokens[5];	    //��й�ȣ
			int neutral = Integer.parseInt(tokens[6]);	//�г���
			String vaccine = tokens[7];	//region
			String character = tokens[8];//��å�ð�
			String intro = tokens[9];	//�ڱ�Ұ�
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
				System.out.println("ģ������");
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
				System.out.println("SEND ����");
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
				System.out.println("RECV ����");
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
						System.out.println("���̻� ����");
					}
				}
				
				
			} else {
				System.out.println("������ ���� ����");
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

			
			
			String select0 = "select * from likes where Send_ID= " + myid_num + " and Recv_ID= " + yourid_num +";";		//�ߺ� üũ
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
				if(rs.next() == true) {		//��û��������� �޴� ���� ����
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
	            													
	            			send.add(dog);																//31. �� ����������
	            			
	            		}
            			
            		} else {
            			System.out.println("���� ������ ����");
            		}
					//=================
					
					String select4 = "select user_no,U_NAME,U_BIRTH,U_GENDER,U_NICK,U_REGION,U_WALKTIME,U_INTRO from user where user_no=" + myid_num + ";";		
					rs = statement.executeQuery(select4);
					if(rs.next() == true) {	  //��û���� ����� �޴� ��������
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
		            													
		            			send.add(dog);																//31. �� ����������
		            			
		            		}
	            			
	            		} else {
	            			System.out.println("���� ������ ����");
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
			String select1 = "select user_no from user where U_NICK=" + "\"" + mynick + "\"" + ";";			//���� -> �����ѹ���
			rs = statement.executeQuery(select1);
			if(rs.next()==true) {
				myid_num = rs.getInt(1);
			} else {
				
				return null;
			}

			String select2 = "select user_no from user where U_NICK=" + "\"" + yournick + "\"" + ";";		//���� -> �����ѹ���
			rs = statement.executeQuery(select2);
			rs.next();
			yourid_num = rs.getInt(1);
			
			String select0 = "select * from likes where Send_ID= " + myid_num + " and Recv_ID= " + yourid_num +";";		//�ߺ� üũ
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
		int myid_num;	//�������κ��� ��û���� ���� ��ȣ
		int yourid_num;	//������ ��û���� ���� ��ȣ
		try {
			statement = con.createStatement();
			
			String[] tokens = Msg.split("/");
			
			String mynick = tokens[0];
			String yournick = tokens[1];
			
			statement = con.createStatement();
			String select1 = "select user_no from user where U_NICK=" + "\"" + mynick + "\"" + ";";			//���� -> �����ѹ���
			rs = statement.executeQuery(select1);
			if(rs.next()==true) {
				myid_num = rs.getInt(1);
			} else {
				
				return null;
			}

			String select2 = "select user_no from user where U_NICK=" + "\"" + yournick + "\"" + ";";		//���� -> �����ѹ���
			rs = statement.executeQuery(select2);
			rs.next();
			yourid_num = rs.getInt(1);
			
			String select0 = "select * from likes where Send_ID= " + yourid_num + " and Recv_ID= " + myid_num +";";		//�ߺ� üũ
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
			String select1 = "select user_no from user where U_NICK=" + "\"" + mynick + "\"" + ";";			//���� -> �����ѹ���
			rs = statement.executeQuery(select1);
			rs.next();
			myid_num = rs.getInt(1);

			String select2 = "select user_no from user where U_NICK=" + "\"" + yournick + "\"" + ";";		//���� -> �����ѹ���
			rs = statement.executeQuery(select2);
			rs.next();
			yourid_num = rs.getInt(1);

			//===========================================
			
			String select3 = "select Fr_list from myfriends where My_userno=" + myid_num + ";";
			rs = statement.executeQuery(select3);
			if(rs.next() == true) {	
				
					//ģ������Ʈ��������==============================================================================================================
					String friends_list = rs.getString(1);
					String[] fr_list = friends_list.split("#");
					
					for(int i = 1; i < fr_list.length; i++) {
						
						if(fr_list[i].equals(yournick)) {
							
							fr_data.add("0" + "!" + LikeAccept_Duple);
							
							return fr_data;
						}
	
					}
					
					System.out.println("�ߺ�������");
					fr_data.add("0" + "!" + LikeAccept_Success);
					
					String update = "update myfriends set Fr_list = " + "\"" + friends_list + "#" + yournick +"\"" + "where My_userno = " + myid_num +";";
					statement.executeUpdate(update);		//ģ���� DB�� ���
					
					
					
					String delete = "delete from likes where Send_ID= " + yourid_num + " and Recv_ID= " + myid_num + ";";
					statement.executeUpdate(delete);
					
					String select4 = "select user_no,U_NAME,U_BIRTH,U_GENDER,U_NICK,U_REGION,U_WALKTIME,U_INTRO,U_TOKEN from user where user_no=" + yourid_num + ";";		
					rs = statement.executeQuery(select4);
					if(rs.next() == true) {	  //������ ����� �޴� �������� (��� ��)
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
		            													
		            			fr_data.add(dog);																//31. �� ����������
		            			
		            		}
	            			
	            		} else {
	            			System.out.println("���� ������ ����");
	            			return null;
	            		}
					}else {
						return null;
					}
					
					String select5 = "select user_no,U_NAME,U_BIRTH,U_GENDER,U_NICK,U_REGION,U_WALKTIME,U_INTRO from user where user_no=" + myid_num + ";";		
					rs = statement.executeQuery(select5);
					if(rs.next() == true) {	  //��û���� ����� �޴� �����ѻ�������
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
		            													
		            			fr_data.add(dog);																//31. �� ����������
		            			
		            		}
	            			
	            		} else {
	            			System.out.println("���� ������ ����");
	            			return null;
	            		}
	            		
	            		
					}else {
						return null;
					}
				//return fr_data;
			} else {		
				
					//ģ������Ʈ�� ������==============================================================================================================
				
					System.out.println("����ģ��");
					fr_data.add("0" + "!" + LikeAccept_Success);
				
					String insert = "insert into myfriends(My_userno, Fr_list) values(" + myid_num + "," + "\"" + "#" + yournick  + "\"" + ");";
					statement.executeUpdate(insert);
					
					String delete = "delete from likes where Send_ID= " + yourid_num + " and Recv_ID= " + myid_num + ";";
					statement.executeUpdate(delete);
					
					String select4 = "select user_no,U_NAME,U_BIRTH,U_GENDER,U_NICK,U_REGION,U_WALKTIME,U_INTRO,U_TOKEN from user where user_no=" + yourid_num + ";";		
					rs = statement.executeQuery(select4);
					if(rs.next() == true) {	  //��û���� ����� �޴� ��������
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
		            													
		            			fr_data.add(dog);																//31. �� ����������
		            			
		            		}
	            			
	            		} else {
	            			System.out.println("���� ������ ����");
	            			return null;
	            		}
					}else {
						return null;
					}
					
					String select5 = "select user_no,U_NAME,U_BIRTH,U_GENDER,U_NICK,U_REGION,U_WALKTIME,U_INTRO from user where user_no=" + myid_num + ";";		
					rs = statement.executeQuery(select5);
					if(rs.next() == true) {	  //��û���� ����� �޴� �����ѻ�������
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
		            													
		            			fr_data.add(dog);																//41. �� ����������
		            			
		            		}
	            			
	            		} else {
	            			System.out.println("���� ������ ����");
	            			return null;
	            		}
					}else {
						return null;
					}
				
				//return fr_data;
			}
			
			String select4 = "select Fr_list from myfriends where My_userno=" + yourid_num + ";";		//���⼱ ������ ���� ����� ģ������Ʈ�� ������ ����� ģ���� �����ϴ°�.
			rs = statement.executeQuery(select4);
			if(rs.next() == true) {
				//ģ������Ʈ��������==============================================================================================================
				String friends_list = rs.getString(1);
				String[] fr_list = friends_list.split("#");
				
				for(int i = 1; i < fr_list.length; i++) {
					
					if(fr_list[i].equals(yournick)) {
						
						//fr_data.add("0" + "!" + LikeAccept_Duple);
						System.out.println("���� ģ������Ʈ �ߺ�");
						break;
						//return fr_data;
					}

				}
				
				System.out.println("�ߺ�������");
				
				String update = "update myfriends set Fr_list = " + "\"" + friends_list + "#" + mynick +"\"" + "where My_userno = " + yourid_num +";";
				statement.executeUpdate(update);		//ģ���� DB�� ���
				
				return fr_data;

			} else {
				//ģ������Ʈ��������==============================================================================================================

				System.out.println("����ģ��");
			
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
	//======================================ȸ�����Խ� ȸ���� ����==========================================================================================

	private int GetPetCount(int userno) {
		try {
			statement = con.createStatement();
			
			String sql = "select count(*) from pet where User_no=" + userno + ";";
			
			rs = statement.executeQuery(sql);
			
			if(rs.next() == true ) {
			
				int p_count = rs.getInt(1);
				consoleLog("�� ������ ��: " + p_count);
			
				return p_count;
			}else {
				return 0;
			}
		} catch (SQLException e) {
			System.out.println("����������");
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
			consoleLog("�� ȸ�� ��: " + u_count);
		
			
		} catch (SQLException e) {
			System.out.println("����������");
			
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

//===================================================================================================================================================
	private void consoleLog(String log) {
        System.out.println("[���Դ� ����] " + socket.getInetAddress().toString() + " - " +log);
    }
}
