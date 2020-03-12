package Server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ParserByCode_User {
	
	
	//==============================================================================
	private static final String Query_Fail = "004:데이터베이스 검색실패";
	private static final String LOGOUT_SUCCESS = "005:로그아웃 성공";
	
	private static final String MATCH_SUCCESS = "301:매칭 성공";
	private static final String MATCH_FAILED = "302:매칭 실패";
	
	private static final String LikeSend_Get = "503:받은 관심친구";
	private static final String LikeCancel_Get = "507:받은 관심친구취소";
	private static final String LikeDeny_Get = "508:받은 관심친구거절";
	private static final String LikeAccept_Get = "511:받은 관심친구수락";


	
	private static final String Chatting_Recv = "701:채팅수신";

	
	private static final String Kilometer = "Kilometer";
	private static final String meter = "Meter";
	//================================================================================
	
	private HashMap<Socket,String> C_map = null;		//현재 접속자담을 리스트
	private ArrayList<User_info> user_info = null; 		//정규회원 유저 리스트
	private Socket socket = null;						//User 주소
	
	public boolean Login = false;
	public boolean TempLogin= false;

	
	public String USER_ID = null;

	private DBcon_User db = null;
	private intConverter ic = null;
	private PicConvert pc = null;
	private GetDistanceByPos dis = null;
	
	private Json js = null;
	
	public ParserByCode_User(Socket sock,HashMap<Socket,String> clientmap,ArrayList<User_info> uinfo) {
		C_map = clientmap;
		user_info = uinfo;
		socket = sock;
		
		js = new Json();
		db = new DBcon_User(socket,user_info);
		dis = new GetDistanceByPos();
		
		
	}
	//========================================신호 별 처리리 함수
	public String Login(String msg) {
			ArrayList<String> login_res = new ArrayList<String>();
			try {
				JsonParser parser = new JsonParser();
				JsonElement element = parser.parse(msg);
				
				String id = element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("ID").getAsString();	//아이디
				String pw = element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("PW").getAsString();	//비밀번호
				String lat	= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Lat").getAsString();	//위도
				String lon	= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Long").getAsString();	//경도
				String token = element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("TOKEN").getAsString();	//비밀번호
				
				String Msg = id + "/" + pw + "/" + lat + "/" + lon + "/" + token; //토큰화해서 DB로 보냄
				
				login_res = db.Login_db(Msg); //분석 및 결과 
				
				if(login_res == null) {//실패				
					String id_Error = js.LoginResult(Query_Fail); //JSON 객체 반환
					return id_Error;
					
				} else {
					String code = "";		String user = "";			String pets = "";	
					String i_sends = "";	String i_sends_pets = "";
					String i_recvs = "";	String i_recvs_pets = "";
					String i_friend = "";	String i_friend_pets = "";

					//성공
					for(int i = 0; i < login_res.size(); i++) {
						
						String[] getRes = login_res.get(i).split("_");
						
						if(getRes[0].equals("0")) {
							code = js.LoginResult(getRes[1]); //JSON 객체 반환
							
						} else if(getRes[0].equals("10")) {
							user = "/" +js.Userinfo(getRes[0],getRes[1]);
							
						} else if(getRes[0].equals("20")) {
							pets += "/" + js.Petinfo(getRes[0],getRes[1]);
							
						} else if(getRes[0].equals("30")) {
							i_sends += "/" + js.YourUserInfo(getRes[0],getRes[1]);
							
						} else if(getRes[0].equals("31")) {
							i_sends_pets += "/" + js.YourPetInfo(getRes[0],getRes[1]);
							
						} else if(getRes[0].equals("40")) {
							i_recvs += "/" + js.YourUserInfo(getRes[0],getRes[1]);
							
						} else if(getRes[0].equals("41")) {
							i_recvs_pets += "/" + js.YourPetInfo(getRes[0],getRes[1]);
							
						}else if(getRes[0].equals("50")) {
							i_friend += "/" + js.YourUserInfo(getRes[0],getRes[1]);
							
						}else if(getRes[0].equals("51")) {
							i_friend_pets += "/" + js.YourPetInfo(getRes[0],getRes[1]);
							
						}
						
					}
					String login_result = code + user + pets + i_sends + i_sends_pets + i_recvs + i_recvs_pets + i_friend + i_friend_pets;
					
					if(code.contains("001")) {
						Login = true;
						USER_ID = id;
					} else if (code.contains("006")) {
						TempLogin = true;
					}
					
					return login_result;
				}
			
			} catch(Exception e) {   
				e.printStackTrace();
				//Send("JSON 오류");
				return null;
			}
			
		}
	
	public String Id_Check(String msg) {
		try {
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(msg);
			
			String id = element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("ID").getAsString();	//아이디
			
			String Msg = id; //토큰화해서 DB로 보냄
			
			String rs = db.Id_Check_db(Msg); //분석 및 결과 
			
			if(rs == null) {
				String IdCheck_result = js.IdCheck_result(Query_Fail); //JSON 객체 반환
				
				return IdCheck_result;
				//Send(IdCheck_result);
			} else {
				String IdCheck_result = js.IdCheck_result(rs); //JSON 객체 반환
				
				return IdCheck_result;
				//Send(IdCheck_result); //결과 전송 
			}
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String NickName_Check(String msg) {
		try {
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(msg);
			
			String nick = element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Nickname").getAsString();
			
			String Msg = nick;
			
			String rs = db.NickName_Check_db(Msg);
			if(rs == null) {
				
				String NickCheck_result = js.NickCheck_result(Query_Fail); //JSON 객체 반환
				return NickCheck_result;
				//Send(NickCheck_result);
			} else {
			
				String NickCheck_result = js.NickCheck_result(rs); //JSON 객체 반환
				//Send(NickCheck_result); //결과 전송 
				return NickCheck_result;
			}
		
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String Sign_Up(String msg) {
		try {
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(msg);
			
			
			String name 	= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Name").getAsString();		//이름
			String birth 	= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Birth").getAsString();		//생년월일 (+나이)
			String gender 	= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Gender").getAsString();		//성별
			String phone 	= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Phone").getAsString();		//전화번호
			String id 		= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("ID").getAsString();			//아이디
			String pw 		= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("PW").getAsString();			//비밀번호
			String nick 	= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Nick").getAsString();		//닉네임
			String region 	= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Region").getAsString();		//region
			String walktime = element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Walktime").getAsString();	//산책시간
			String date 	= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Date").getAsString();		//가입날짜
			String intro 	= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Intro").getAsString();		//자기소개	
			String token 	= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Token").getAsString();		//토큰

			
			String Msg = name + "/" + birth + "/" + gender + "/" + phone + "/" + id + "/" + pw + "/" + 
							nick + "/" + region + "/" + walktime + "/" + date + "/" + intro + "/" + token; //토큰화해서 DB로 보냄
			
			String rs = db.SignUp_db(Msg); //분석 및 결과 
			
			if(rs == null) {
				
				String SignUp_result = js.SignUp_result(Query_Fail); //JSON 객체 반환
				
				return SignUp_result;
				
			} else {
			
				String SignUp_result = js.SignUp_result(rs); //JSON 객체 반환
				return SignUp_result;
			}
		
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	public String Ran_Match(String msg) {
		ArrayList<User_info> excludedUser = new ArrayList<User_info>();
		
		for(int i = 0; i< user_info.size(); i++) {
			excludedUser.add(user_info.get(i));			//배열 복사
		}
		ArrayList<String> randb_res = new ArrayList<String>();
		ArrayList<String> name_list = new ArrayList<String>();
		ArrayList<String> getPet_list = new ArrayList<String>();
		ArrayList<String> Pet_list = new ArrayList<String>();

		try {			
			ArrayList<String> ran_list = new ArrayList<>();;
			
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(msg);
			
			String nick = element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Nick").getAsString();
			double lat = element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Lat").getAsDouble();
			double lon = element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Long").getAsDouble();
			double distance = element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Dis").getAsDouble();

			randb_res = db.RandomMatch_db(nick);
			
			for(int i = 0; i<randb_res.size(); i++) {
				String[] getRes = randb_res.get(i).split("_");

				if(getRes[0].equals("0")) {
					name_list.add(getRes[1]);
				}else if (getRes[0].equals("10")) {
					name_list.add(getRes[1]);
				} else if (getRes[0].equals("20")) {
					name_list.add(getRes[1]);
				} else if (getRes[0].equals("30")) {
					for(int j = 0; j < excludedUser.size(); j++) {
						if(excludedUser.get(j).U_NICK.equals(getRes[1])) {
							name_list.add(getRes[1]);
						} else {
							System.out.println("친구리시트 닉네임과 전체유저리스트중 닉 없음");
						}
					}
					
				} 
			}
			
			for(int i = 0; i < name_list.size(); i++) {
				for(int j = 0; j < excludedUser.size(); j++) {
					if(excludedUser.get(j).U_NICK.equals(name_list.get(i))) {
						excludedUser.remove(j);
						j--;
					}
				}
			}
			
			if(excludedUser.size() > 0) {
				for(int i =0; i < excludedUser.size(); i++) {
					
					double dis_res = dis.Distance(lat, lon, excludedUser.get(i).U_LAT, excludedUser.get(i).U_LONG, Kilometer);				
					//System.out.println(dis_res); //사잇값 구하기	
					if(dis_res < distance) {		//만약 1.5키로 내외면
						System.out.println(excludedUser.get(i).U_NICK);
						
						String Msg = excludedUser.get(i).user_no + "/" + excludedUser.get(i).U_NAME + "/" + excludedUser.get(i).U_BIRTH + "/" + excludedUser.get(i).U_GENDER + "/" 
									+ excludedUser.get(i).U_NICK + "/" + excludedUser.get(i).U_REGION + "/" + excludedUser.get(i).U_WALKTIME + "/" 
									+ excludedUser.get(i).U_INTRO + "/" + dis_res; 
							
						String ran_user = js.RandomUserInfo("70",Msg);
						
						ran_list.add(ran_user);
					} else {
						System.out.println("범위내 대상 존재하지 않음");
					}	
					
					getPet_list = db.GetRandomDog_db(excludedUser.get(i).user_no);
					
					for(int j =0; j < getPet_list.size(); j++) {
						String ran_pet = js.RandomPetInfo("71", getPet_list.get(j));
						Pet_list.add(ran_pet);
					}
				}
			}
			
			if(ran_list.size() > 0) {
				Set<Integer> ran_num = new HashSet<Integer>();				//중복이 안들어가는 Set 클래스
				Random ran = new Random();	
				
				while(ran_num.size() != ran_list.size()) {				//최대숫자가 정규회원수만큼인 중복안되는 난수 생성해서 난수 저장
					ran_num.add(ran.nextInt(ran_list.size()));
				}				
				
				List<Integer> ran_numlist = new ArrayList<Integer>(ran_num);		//저장된 Set 난수를 List에 저장 ex( 3,1 ,4, 5, 7,)

				Collections.shuffle(ran_numlist);
				System.out.println(ran_numlist.toString());
				
				String match_success = js.RanMatch_result(MATCH_SUCCESS);
				for(int i = 0; i < ran_list.size(); i ++) {
					
					match_success += "/" + ran_list.get(ran_numlist.get(i));		//for문 돌려서 랜던숫자가 들어가있는 List에서 뽑아온 그숫자를 랜덤매칭상대 배열 번호 로 지정. 총 3명 
					
					JsonParser Userparser = new JsonParser();
					JsonElement Userelement = Userparser.parse(ran_list.get(ran_numlist.get(i)));
					int user_no = Userelement.getAsJsonObject().get("UserNum").getAsInt();
					
					for(int j =0; j<Pet_list.size(); j++) {
						JsonParser Petparser = new JsonParser();
						JsonElement Petelement = Petparser.parse(Pet_list.get(j));
						int User_no = Petelement.getAsJsonObject().get("UserNum").getAsInt();
						
						if(User_no == user_no) {
							match_success += "/" + Pet_list.get(j);
						}else {
							System.out.println("일치 하지 않음");
						}
					}
				}				
				return match_success;	
			}
			else {
				String match_failed = js.RanMatch_result(MATCH_FAILED);
				return match_failed;
			}
			
		
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String Pet_Regi(String msg) {
		ArrayList<String> P_data = new ArrayList<String>();
		try {
			
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(msg);
			
			
			String id 			= element.getAsJsonObject().get("ID").getAsString();		//이름
			String name 		= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Name").getAsString();		//강아지 이름
			String type 		= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Type").getAsString();		//견종
			String size 		= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Size").getAsString();		//사이즈
			String age 			= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Age").getAsString();			//나이
			String birth 		= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Birth").getAsString();			//생월일
			String gender 		= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Gender").getAsString();		//성별
			String neutral 		= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Neutral").getAsString();		//중성화유뮤
			String vaccine 		= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Vaccine").getAsString();	//백신유무
			String character 	= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Character").getAsString();		//성격
			String intro	 	= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Intro").getAsString();		//강아지 소개

			
			String Msg = name + "/" + type + "/" + size + "/" + age + "/" + birth + "/" + gender + "/" + 
					neutral + "/" + vaccine + "/" + character + "/" + intro + "/" + id; //토큰화해서 DB로 보냄
			
			P_data = db.PetRegi_db(Msg); //분석 및 결과 
			
			if(P_data == null) {
				
				String PetRegi_result = js.PetRegi_result(Query_Fail); //JSON 객체 반환
				
				return PetRegi_result;
				
			} else {
			
				String code = "";	String pet = "";
				//성공
				for(int i = 0; i < P_data.size(); i++) {
					
					String[] getRes = P_data.get(i).split("_");
					
					if(getRes[0].equals("0")) {
						code = js.PetRegi_result(getRes[1]); //JSON 객체 반환
						
					} else if(getRes[0].equals("40")) {
						pet = "/" +js.Petinfo(getRes[0],getRes[1]);
						
					} 
				}
				String PetRegi_result = code + pet;
			
				return PetRegi_result;
			}
			
		} catch(Exception e) {
			return null;
		}
	}
	
	public ArrayList<String> LikeSend(String msg) {
		ArrayList<String> send_rs = new ArrayList<String>();
		ArrayList<String> LikeSend_data = new ArrayList<String>();

		try {
			
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(msg);
			
			String myNick 			= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("My").getAsString();		//이름
			String yourNick 		= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("You").getAsString();		//이름
			
			String Msg = myNick + "/" + yourNick;
			
			send_rs = db.LikeSend_db(Msg);
			
			if(send_rs == null) {
				String LikeSend_result = js.LikeSend_result(Query_Fail); //JSON 객체 반환
				
				LikeSend_data.add(LikeSend_result);
				return LikeSend_data;
				
			} else {
				String code1 = ""; String yourinfotoMe = ""; String yourdoginfos = "";
				String code2 = ""; 
				String myinfotoYou = ""; String mydoginfos = "";  String token = "";

				for(int i = 0; i < send_rs.size(); i++) {
					
					String[] getRes = send_rs.get(i).split("!");
					
					if(getRes[0].equals("0")) {
						if(getRes[1].contains("504")) {
							String code3 = js.LikeSend_result(getRes[1]);
							LikeSend_data.add(code3);
							return LikeSend_data;

						} else {
							code1 = js.LikeSend_result(getRes[1]); //JSON 객체 반환
							code2 = js.LikeSend_result(LikeSend_Get);
						}
						
					} else if(getRes[0].equals("30")) {
						yourinfotoMe = "/" + js.YourUserInfo(getRes[0],getRes[1]);		//내가받는 너의정보
						
					}else if(getRes[0].equals("31")) {
						yourdoginfos += "/" + js.YourPetInfo(getRes[0],getRes[1]);		//내가받는 너의개정보
						
					} else if(getRes[0].equals("40")) {
						myinfotoYou = "/" + js.YourUserInfo(getRes[0],getRes[1]);		//너가받는 나의정보
						
					} else if(getRes[0].equals("41")) {
						mydoginfos += "/" + js.YourPetInfo(getRes[0],getRes[1]);		//너가받는 나의정보		
					} else if(getRes[0].equals("100")) {
						token = getRes[1];											//너의 토큰		
					} 
				}
				String LikeSend_res1 = code1 + yourinfotoMe + yourdoginfos;
				String LikeSend_res2 = code2 + myinfotoYou + mydoginfos;
				String LikeSend_res3 = yourNick;
				String LikeSend_res4 = token;
				
				LikeSend_data.add(LikeSend_res1);
				LikeSend_data.add(LikeSend_res2);
				LikeSend_data.add(LikeSend_res3);
				LikeSend_data.add(LikeSend_res4);

				return LikeSend_data;
			}
					
		} catch (Exception e) {
			
			return null;
		}
	}
	
	public  ArrayList<String> LikeCancel(String msg) {
		ArrayList<String> likeCancel_res = new ArrayList<String>();
		ArrayList<String> LikeCancel = new ArrayList<String>();

		try {
			
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(msg);
			
			String myNick 		= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("My").getAsString();			//나의 닉에임
			String yourNick 	= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("You").getAsString();		//너의 닉에임
			
			String Msg = myNick + "/" + yourNick;
			
			likeCancel_res = db.LikeCancel_db(Msg);
			
			if(likeCancel_res == null) {
				
				String LikeCancel_result = js.NickCheck_result(Query_Fail); //JSON 객체 반환	//004에대한 처리하는 함수만들기
				
				LikeCancel.add(LikeCancel_result);
				return LikeCancel;
				
			} else {
			
				String code = "";	String code1 ="";	String MyNumToYou = "";	String YourNumToMe = "";
				//성공
				for(int i = 0; i < likeCancel_res.size(); i++) {
					
					String[] getRes = likeCancel_res.get(i).split("_");
					
					if(getRes[0].equals("0")) {
						code = js.LikeSend_result(getRes[1]);
						code1 = js.LikeSend_result(LikeCancel_Get);
						
					}else if(getRes[0].equals("30")) {
						MyNumToYou = "/" +js.YourUserNum(getRes[1]);
						
					} 
					else if(getRes[0].equals("40")) {
						YourNumToMe = "/" +js.YourUserNum(getRes[1]);
						
					} 
				}
				LikeCancel.add(code);
				LikeCancel.add(YourNumToMe);
				LikeCancel.add(code1);
				LikeCancel.add(MyNumToYou);
				LikeCancel.add(yourNick);
			
				
				return LikeCancel;
			}
			
		}catch(Exception e) {
			return null;
		}
	}
	
	public  ArrayList<String> LikeDeny(String msg) {
		ArrayList<String> likeDeny_res = new ArrayList<String>();
		ArrayList<String> LikeDeny = new ArrayList<String>();

		try {
			
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(msg);
			
			String myNick 		= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("My").getAsString();			//나의 닉에임
			String yourNick 	= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("You").getAsString();		//너의 닉에임
			
			String Msg = myNick + "/" + yourNick;
			
			likeDeny_res = db.LikeDeny_db(Msg);
			
			if(likeDeny_res == null) {
				
				String LikeDeny_result = js.NickCheck_result(Query_Fail); //JSON 객체 반환	//004에대한 처리하는 함수만들기
				
				LikeDeny.add(LikeDeny_result);
				return LikeDeny;
				
			} else {
			
				String code = "";	String code1 ="";	String MyNumToYou = "";	String YourNumToMe = "";
				//성공
				for(int i = 0; i < likeDeny_res.size(); i++) {
					
					String[] getRes = likeDeny_res.get(i).split("_");
					
					if(getRes[0].equals("0")) {
						code = js.LikeSend_result(getRes[1]);
						code1 = js.LikeSend_result(LikeDeny_Get);
						
					}else if(getRes[0].equals("30")) {
						MyNumToYou = "/" +js.YourUserNum(getRes[1]);
						
					} 
					else if(getRes[0].equals("40")) {
						YourNumToMe = "/" +js.YourUserNum(getRes[1]);
						
					} 
				}
				LikeDeny.add(code);
				LikeDeny.add(YourNumToMe);
				LikeDeny.add(code1);
				LikeDeny.add(MyNumToYou);
				LikeDeny.add(yourNick);
			
				
				return LikeDeny;
			}
			
		}catch(Exception e) {
			return null;
		}
	}
	
	public ArrayList<String> LikeAccept(String msg) {
		ArrayList<String> like_Accept_res = new ArrayList<String>();
		ArrayList<String> LikeAccept_data = new ArrayList<String>();

		try {
			
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(msg);
			
			String myNick 		= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("My").getAsString();			//나의 닉에임
			String yourNick 	= element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("You").getAsString();		//너의 닉에임
			
			String Msg = myNick + "/" + yourNick;
			
			like_Accept_res = db.LikeAceept_db(Msg);
			
			if(like_Accept_res == null) {
				
			} else {
				String code = ""; String code1 = "";
				String MyInfo = ""; String Mydog = ""; String token = "";
				String yourInfo = ""; String Yourdog = "";
				
				for(int i = 0; i < like_Accept_res.size(); i++) {
					
					String[] getRes = like_Accept_res.get(i).split("!");
					
					if(getRes[0].equals("0")) {
						code = js.LikeAccept_result(getRes[1]); //JSON 객체 반환
						code1 = js.LikeAccept_result(LikeAccept_Get);
						
					}  else if(getRes[0].equals("30")) {
						yourInfo = "/" + js.YourUserInfo(getRes[0],getRes[1]);
						
					} else if(getRes[0].equals("31")) {
						Yourdog += "/" + js.YourPetInfo(getRes[0],getRes[1]);
						
					} else if(getRes[0].equals("40")) {
						MyInfo = "/" + js.YourUserInfo(getRes[0],getRes[1]);
						
					} else if(getRes[0].equals("41")) {
						Mydog += "/" + js.YourPetInfo(getRes[0],getRes[1]);
						
					} else if(getRes[0].equals("100")) {
						token = getRes[1];											//너의 토큰		
					} 
					
				}
				LikeAccept_data.add(code);
				LikeAccept_data.add(yourInfo);
				LikeAccept_data.add(Yourdog);
				LikeAccept_data.add(code1);
				LikeAccept_data.add(MyInfo);
				LikeAccept_data.add(Mydog);
				LikeAccept_data.add(token);
				LikeAccept_data.add(yourNick);

				
				return LikeAccept_data;
				
			}
			
			return null;
		}catch(Exception e) {
			return null;
		}
		
	}
	
	public String Chatting(String msg) {
		try {
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(msg);
			
			String myNick = element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("MyNick").getAsString();			//나의 닉에임
			String yourNick = element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("YourNick").getAsString();			//너의 닉에임
			String content = element.getAsJsonObject().get("msg").getAsJsonObject().get("data").getAsJsonObject().get("Chat").getAsString();			//내용
			
			String chatting_res = js.Chatting_result(Chatting_Recv);
			String chatting_content = js.Chatting_Send(myNick, yourNick, content);
			String token = db.Chat_token(yourNick);
			
			if(chatting_res == null || chatting_content == null) {
				String chatting_send_fail = js.NickCheck_result(Query_Fail);
				return chatting_send_fail;
			} else {
				String chatting_result = chatting_res + "/" + chatting_content + "/" + yourNick + "/" + token;
				return chatting_result;
			}
			
			
		}catch(Exception e) {
			return null;
		}
	}
	
	public String Logout(String msg) {
		try {
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(msg);
			
			String logout = element.getAsJsonObject().get("CODE").getAsString();	//로그아웃 신호
			
			if(logout.equals("Logout")) {
				String Logout_result = js.Logout_result(LOGOUT_SUCCESS);
				
				Login = false;
				
				return Logout_result;				
			} else {
				return null;
			}

		} catch(Exception e) {   
			e.printStackTrace();
			return null;
		}
	}
	
	public void GetCount() {
		db.GetCount();
	}
	
	public void GetAll_Regular_User() {
		db.getUserinfo();
	}
	
	public String UserPic_db(String fname, String id,int i) {
		String res = db.UserPic_db(fname, id, i);
		
		return res;
	}
	
	public String petPic_db(String id,String dogname,int i) {
		String res = db.petPic_db(id,dogname,i);
		
		return res;
	}

}
