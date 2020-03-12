package Server;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class ReceiveThread extends Thread{

	private boolean check = false;
	
	private DataInputStream in;
	private DataOutputStream out;
	
	private HashMap<Socket,String> C_map = null;		//접속자담을 리스트
	private ArrayList<User_info> user_info = null; // 전체 유저 저장소
	
	private Socket socket;
	private DBcon_User db = null;
	private intConverter ic = null;
	private PicConvert pc = null;

	private ParserByCode_User pbc_user = null;
	private ParserBycode_Manager pbc_manager = null;
	
	private FCM_Push fcm = null;
	//=====================================================================
	private static final SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");		
	private static final String format_time1 = format1.format (System.currentTimeMillis());
	
	private static final String Kilometer = "Kilometer";
	private static final String meter = "Meter";

	//====================================클라이언트
	private static final String IMAGE = "Image";
	private static final String JSON = "Json";
	
	private static final String Query_Fail = "004:데이터베이스 검색실패";
	private static final String LOGOUT_SUCCESS = "005:로그아웃 성공";
	
	private static final String MATCH_SUCCESS = "301:매칭 성공";
	private static final String MATCH_FAILED = "302:매칭 실패";

	//=======================구분값
	private static final int HeaderSize = 4;
	private static final int CLIENT = 2;
	private static final int MANAGER = 4;
	private static final int DATA = 3;

	//========================관리자=============
	final static String APPROVED = "APPROVED";
	final static String DENIED = "DENIED";

	//=====================================================================

	public ReceiveThread(Socket socket, HashMap<Socket,String> clientmap, ArrayList<User_info> uinfo) {
		try {

			this.socket = socket;
			ic = new intConverter();
			user_info = uinfo;
			C_map = clientmap;
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			
			pc = new PicConvert();
			fcm = new FCM_Push();
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			while(in != null) {
				
				byte[] sig = new byte[4];
				int size = in.read(sig,0,4);
				int signal = ic.byteArrayToInt(sig);
				
					if(size == -1 ) {																				//강제종료거나 , 정상종료시 수신값이 없어서 -1 반환 
						String Msg = format_time1 + "--" + socket.getInetAddress().toString() + " : 연결끊김";
						System.out.println(Msg);
						RemoveClient();
						socket.close();
						break;
					}
				
												//1= 관리자 ,2 = 유저 , 3 =유저 데이터 , 
				
					if(signal == 1) {
						isManager();
						
					} else if (signal == 2 || signal == 3) {
						
						isClient();
						
					} 
			}
		}
		catch (Exception e1) {
			String Msg = format_time1 + " -- 클라이언트 [" + socket.getInetAddress().toString() + "]로부터 연결 끊김";
			System.out.println(Msg);
			System.out.println(e1.getMessage());

			try {
				RemoveClient();
			} catch (IOException e) {
				System.out.println("오류");
				e.printStackTrace();
			}
		}
		
	}
	
//==============USER MANAGER 구분 함수==============================================
	public void isManager() {
		try {
			pbc_manager = new ParserBycode_Manager(socket,C_map,user_info);
			byte[] data_size = new byte[4]; 
			int size = (byte)in.read(data_size);
			
			int _size = ic.byteArrayToInt(data_size);	//=16
			
			int sum = 0;//받을데이터 총크기
			int len;	//받을데이터 버퍼당크기
			byte[] data = new byte[_size];//데이터담을 실제 그릇
			
			while((len = in.read(data)) != -1) {
				sum += len;
				//System.out.println("받고있는중 " + sum +"/" + filesize);
				if(sum == _size) {
					break;
				}
			}
			
			String res = new String(data);
			
			ConsoleLog("[관리자] 로그인 : " + res);
			
			ParserBycode_Manager(res);
			
		}catch(IOException e) {
			
		}
	}
	
	public void isClient() {
		try {
			pbc_user = new ParserByCode_User(socket,C_map,user_info);
			
			String Data = in.readUTF();
			 
			String[] tokens = Data.split("/"); //1.분류코드 
			 
			if(tokens[0].equals(IMAGE)) { 
				ConsoleLog(tokens[1]);				 
				ParserBypicode_User(tokens[1]);
			 
			} else if(tokens[0].equals(JSON)) { 
				ConsoleLog(tokens[1]);				 
				ParserBycode_User(tokens[1]); 
			}

		}catch(IOException e) {
			
		}
	}
	
	private void ParserBypicode_User(String msg) {
		String code = msg;
		
		System.out.println("[수신 신호:" + code + "]");
		
		switch(code) {
			case "SignUp": signUp_pic1(); 				break;
			case "PetRegi":	 pet_Pic1();					break;		
		}
		
	}
//================================사진데이터 받는함수(사람 강아지)===================================================================================================================
	private void signUp_pic() {
		try{
			int fileCounts = in.readInt(); 	
			ConsoleLog("사진 총 갯수 : " + String.valueOf(fileCounts));  //2.파일총갯수 수신
		
			for(int i = 0; i < fileCounts; i++ ) {
				String fName = in.readUTF();
				System.out.println("파일명" + " - " + fName);  //3.파일이름 수신
				
				String[] fName_s = fName.split("-");
				
				File pic_folder = new File("D:\\AndroidStudio-Study-Github\\Together\\Together_Test\\Picture\\" + fName_s[1]);
				
							if (pic_folder.mkdir()){
							      System.out.println(fName_s[1] + "회원 사진 디렉토리 생성 성공");
							}else{
							      System.out.println(fName_s[1] + "이미생성 되었거나 디렉토리 생성 실패");
							}
				
				String filePath = "D:\\AndroidStudio-Study-Github\\Together\\Together_Test\\Picture\\"+ fName_s[1]+ "\\" + fName +".bmp"; //3.5 파일저장위치 선정
				
				BufferedOutputStream bos = 
						new BufferedOutputStream(new FileOutputStream(filePath));
				System.out.println(socket.getInetAddress().toString() + " 파일생성");
				
				long filesize = in.readLong();											//4.파일크기 수신
				
				System.out.println("전송받을크기 :" + filesize);
				
				ArrayList<Byte> b = new ArrayList<>();
				int sum = 0;//받을데이터 총크기
				int len;	//받을데이터 버퍼당크기
				int size = 8192;//한번에받을버퍼크기
				byte[] data = new byte[size];//데이터담을 실제 그릇
				
				while((len = in.read(data)) != -1) {
					bos.write(data,0,len);
					sum += len;
					System.out.println("받고있는중 " + sum +"/" + filesize);
					if(sum == filesize) {
						break;
					}
				}
				
				System.out.println(data + "--" + len + "--" + sum);
				
				bos.flush();
				bos.close();
				
				System.out.println("파일수신완료");
				System.out.println("받은파일사이즈 : " + sum + "/" + filesize);
				
				String[] id = fName.split("-");										//파일이름에있는 Id넘김
				String res = pbc_user.UserPic_db(fName,id[1],i+1);					//데이터베이스에 파일명 저장
				
				ConsoleLog(res);
			}
		} catch (IOException e) {
			System.out.println("받는함수에서 오류");
		}
		
	}
	
	private void signUp_pic1() {
		try{
			int fileCounts = in.readInt(); 	
			ConsoleLog("사진 총 갯수 : " + String.valueOf(fileCounts));  //2.파일총갯수 수신
		
			for(int i = 0; i < fileCounts; i++ ) {
				String fName = in.readUTF();
				System.out.println("파일명" + " - " + fName);  //3.파일이름 수신
				
				String[] fName_s = fName.split("-");
				
				File pic_folder = new File("D:\\AndroidStudio-Study-Github\\Together\\Together_Test\\Picture\\" + fName_s[1]);
				
							if (pic_folder.mkdir()){
							      System.out.println(fName_s[1] + "회원 사진 디렉토리 생성 성공");
							}else{
							      System.out.println(fName_s[1] + "이미생성 되었거나 디렉토리 생성 실패");
							}
				
				String filePath = "D:\\AndroidStudio-Study-Github\\Together\\Together_Test\\Picture\\"+ fName_s[1]+ "\\" + fName +".bmp"; //3.5 파일저장위치 선정
				
				BufferedOutputStream bos = 
						new BufferedOutputStream(new FileOutputStream(filePath));
				System.out.println(socket.getInetAddress().toString() + " 파일생성");
				
				long filesize = in.readLong();											//4.파일크기 수신
				
				System.out.println("전송받을크기 :" + filesize);
				
				ArrayList<Byte> b = new ArrayList<>();
				int sum = 0;//받을데이터 총크기
				int left_data = 0;
                int recv_data = 0;
				
				left_data = (int)filesize;
				byte[] data = new byte[(int)filesize];//데이터담을 실제 그릇
				
				while(sum <= (int)filesize) {
					recv_data = in.read(data,sum,left_data);
					System.out.println("받고있는중 " + sum +"/" + filesize + "남은 데이터" + left_data);
					if(recv_data == 0) {
						bos.write(data,0,data.length);
						break;
					}
					sum += recv_data;
					left_data -= recv_data;
				}
				
				System.out.println(data + "--" + sum);
				
				bos.flush();
				bos.close();
				
				System.out.println("파일수신완료");
				System.out.println("받은파일사이즈 : " + sum + "/" + filesize);
				
				String[] id = fName.split("-");										//파일이름에있는 Id넘김
				String res = pbc_user.UserPic_db(fName,id[1],i+1);					//데이터베이스에 파일명 저장
				
				ConsoleLog(res);
			}
		} catch (IOException e) {
			System.out.println("받는함수에서 오류");
		}
		
	}

	//출처: https://dev.re.kr/24 [Dev.re.kr]
	
	private void pet_Pic() {
		
		try{
			int fileCounts = in.readInt(); 	
			ConsoleLog("사진 총 갯수 : " + String.valueOf(fileCounts));  //2.파일총갯수 수신
		
			for(int i = 0; i < fileCounts; i++ ) {
				String fName = in.readUTF();
				System.out.println("파일명" + " - " + fName);  //3.파일이름 수신
				
				String[] fName_s = fName.split("-");
				
				File pic_folder = new File("D:\\AndroidStudio-Study-Github\\Together\\Together_Test\\Picture\\" + fName_s[0] + "\\" + fName_s[1] +"_pet");
				
							if (pic_folder.mkdir()){
							      System.out.println(fName_s[1] + "반려견 사진 디렉토리 생성 성공");
							}else{
							      System.out.println(fName_s[1] + "이미생성 되었거나 디렉토리 생성 실패");
							}
				
				String filePath = "D:\\AndroidStudio-Study-Github\\Together\\Together_Test\\Picture\\"+ fName_s[0]+ "\\" + fName_s[1] +"_pet" + "\\" + fName + ".jpg"; //3.5 파일저장위치 선정
				
				BufferedOutputStream bos1 = 
						new BufferedOutputStream(new FileOutputStream(filePath));
				
				long filesize = in.readLong();											//4.파일크기 수신
				
				System.out.println("전송받을크기 :" + filesize);
				
				ArrayList<Byte> b = new ArrayList<>();
				int sum = 0;//받을데이터 총크기
				int len;	//받을데이터 버퍼당크기
				int size = 8192;//한번에받을버퍼크기
				byte[] data = new byte[size];//데이터담을 실제 그릇
				
				while((len = in.read(data,0,size)) != -1) {
					bos1.write(data,0,len);
					sum += len;
					System.out.println("받고있는중 " + sum + "/" + filesize);
					if(sum == filesize) {
						break;
					}
				}
				
				
				System.out.println(data + "--" + len + "--" + sum);
				
				bos1.flush();
				bos1.close();

				System.out.println("파일수신완료");
				System.out.println("받은파일사이즈 : " + sum);
				
				String[] id = fName.split("-");						//파일이름에있는 Id넘김
				String res = pbc_user.petPic_db(id[0],id[1],i+1);					//데이터베이스에 파일명 저장
				
				ConsoleLog(res);
			}
		} catch (IOException e) {
			System.out.println("받는함수에서 오류");
		}
	}
	
	private void pet_Pic1() {
		
		try{
			int fileCounts = in.readInt(); 	
			ConsoleLog("사진 총 갯수 : " + String.valueOf(fileCounts));  //2.파일총갯수 수신
		
			for(int i = 0; i < fileCounts; i++ ) {
				String fName = in.readUTF();
				System.out.println("파일명" + " - " + fName);  //3.파일이름 수신
				
				String[] fName_s = fName.split("-");
				
				File pic_folder = new File("D:\\AndroidStudio-Study-Github\\Together\\Together_Test\\Picture\\" + fName_s[0] + "\\" + fName_s[1] +"_pet");
				
							if (pic_folder.mkdir()){
							      System.out.println(fName_s[1] + "반려견 사진 디렉토리 생성 성공");
							}else{
							      System.out.println(fName_s[1] + "이미생성 되었거나 디렉토리 생성 실패");
							}
				
				String filePath = "D:\\AndroidStudio-Study-Github\\Together\\Together_Test\\Picture\\"+ fName_s[0]+ "\\" + fName_s[1] +"_pet" + "\\" + fName + ".jpg"; //3.5 파일저장위치 선정
				
				BufferedOutputStream bos1 = 
						new BufferedOutputStream(new FileOutputStream(filePath));
				
				long filesize = in.readLong();											//4.파일크기 수신
				
				System.out.println("전송받을크기 :" + filesize);
				
				ArrayList<Byte> b = new ArrayList<>();
				int sum = 0;//받을데이터 총크기
				int left_data = 0;
                int recv_data = 0;
				
				left_data = (int)filesize;
				byte[] data = new byte[(int)filesize];//데이터담을 실제 그릇
				
				while(sum <= (int)filesize) {
					recv_data = in.read(data,sum,left_data);
					System.out.println("받고있는중 " + sum +"/" + filesize + "남은 데이터" + left_data);
					if(recv_data == 0) {
						bos1.write(data,0,data.length);
						break;
					}
					sum += recv_data;
					left_data -= recv_data;
				}
				
				
				System.out.println(data + "--" + sum);
				
				bos1.flush();
				bos1.close();

				System.out.println("파일수신완료");
				System.out.println("받은파일사이즈 : " + sum);
				
				String[] id = fName.split("-");						//파일이름에있는 Id넘김
				String res = pbc_user.petPic_db(id[0],id[1],i+1);					//데이터베이스에 파일명 저장
				
				ConsoleLog(res);
			}
		} catch (IOException e) {
			System.out.println("받는함수에서 오류");
		}
	}
//================================사진데이터 받는함수===================================================================================================================
	
//=================================신호 파싱 함수===================================================================================================================	
//								~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
																				//Manager 전용
	private void ParserBycode_Manager(String msg) {
		JsonReader reader = new JsonReader(new StringReader(msg));
		reader.setLenient(true);	//여러가지 특수문자등이 있을때 .. 이걸 설정한다.
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(reader);
		
		String code = element.getAsJsonObject().get("CODE").getAsString();
		
		System.out.println("[수신 신호:" + code + "]");
		
		switch(code) {
			case "GetAll": getAllUser(); 		break;
			case "Approve": Approve(msg);			break;
			
		}
	}
	
	private void getAllUser() {
		try {
			String getAllUser_res = pbc_manager.getAllUser();
			if(getAllUser_res == null) {
				Send_M("UNKNOWN");
			}
			
			Send_M(getAllUser_res);
			
			
		} catch(Exception e) {
			
		}
	}
	
	private void Approve(String msg) {
		try {
			
			JsonReader reader = new JsonReader(new StringReader(msg));
			reader.setLenient(true);	//여러가지 특수문자등이 있을때 .. 이걸 설정한다.
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(reader);
			
			String id = element.getAsJsonObject().get("Content").getAsJsonObject().get("ID").getAsString();
			String token = element.getAsJsonObject().get("Content").getAsJsonObject().get("Token").getAsString();
			
			String send_res = pbc_manager.Approve(id, token);
			
			if(send_res == null) {
				Send_M("UNKNOWN");
			}else if(send_res.contains(APPROVED)) {

				Send_M(send_res);
				fcm.Send(token);
			}else {
				Send_M(send_res);
			}

		}catch(Exception e) {
			
		}
	}
	
	
//								~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private void ParserBycode_User (String msg) {									//Client 전용
		
		
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(msg);
		
		String code = element.getAsJsonObject().get("CODE").getAsString();
		
		System.out.println("[수신 신호:" + code + "]");
		
		switch(code) {
			case "Login": Login(msg); 				break;
			case "Logout":	 Logout(msg);			break;		
			case "IdCheck" : ID_Check(msg); 		break;
			case "NickCheck" : NICK_Check(msg); 	break;
			case "SignUp" : Sign_Up(msg); 			break;
			case "RandomMatch" : Ran_Match(msg); 	break;
			case "PetRegi" : Pet_Regi(msg); 		break;
			case "LikeSend": LikeSend(msg); 		break;
			case "LikeCancel": Like_Cancel(msg);	break;
			case "LikeDeny": Like_Deny(msg); 		break;
			case "LikeAccept": Like_Accept(msg); 		break;

			case "Chat": Chatting(msg); 			break;



		}
	}
	
	private void Login(String msg) {
		try {
			String login_res = pbc_user.Login(msg);
			if(login_res == null) {
				Send("JSON 오류");
			}
			
			if(pbc_user.Login == true) {
				AddClient(pbc_user.USER_ID);
				pbc_user.GetAll_Regular_User();
				Send(login_res);
				
			} else if(pbc_user.TempLogin == true) {
				Send(login_res);
			} else {
				Send(login_res);
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println("Login 오류");
		}
	}
	
	private void ID_Check(String msg) {
		
		try {
			String idCheck_res = pbc_user.Id_Check(msg);
			if(idCheck_res == null) {
				Send("JSON 오류");
			}
			
			Send(idCheck_res);
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println("ID_CHECK 오류");
		}
	}
	
	private void NICK_Check(String msg) {
		try {
			String nickCheck_res = pbc_user.NickName_Check(msg);
			if(nickCheck_res == null) {
				Send("JSON 오류");
			}
			
			Send(nickCheck_res);
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println("ID_CHECK 오류");
		}
	}
	
	private void Logout(String msg) {
		try {
			String Logout_res = pbc_user.Logout(msg);
			if(Logout_res == null) {
				Send("JSON 오류");
			}
			
			Send(Logout_res);
			
			RemoveClient();
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println("Logout 오류");
		}
	}
	
	private void Sign_Up(String msg) {
		
		try {
			String SignUp_res = pbc_user.Sign_Up(msg);
			if(SignUp_res == null) {
				Send("JSON 오류");
			}
			
			pbc_user.GetCount();		//전체회원수 갱신
			Send(SignUp_res);

		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println("SIGNUP 오류");
		}
	}
	
	private void Ran_Match(String msg) {
		try {
			String RanMatch_res = pbc_user.Ran_Match(msg);
			if(RanMatch_res == null) {
				Send("JSON 오류");
			}
			
			pbc_user.GetAll_Regular_User();
			Send(RanMatch_res);
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println("RANDOM 오류");
		}
		
	}
	
	private void Pet_Regi(String msg) {
		try {
			String Pet_Regi_res = pbc_user.Pet_Regi(msg);
			if(Pet_Regi_res == null) {
				Send("JSON 오류");
			}
			
			Send(Pet_Regi_res);
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println("PET REGI 오류");
		}
	}
	
	private void LikeSend(String msg) {
		ArrayList<String> LikeSend_result = new ArrayList<String>();
		try {
			LikeSend_result = pbc_user.LikeSend(msg);
			if(LikeSend_result == null) {
				Send("JSON 오류");
			} else {
			//여기 엘스이프로 만들기
				
				if(LikeSend_result.size() > 0) {
					if(LikeSend_result.get(0).contains("504") == true) {
						
						String likeSend_duple = LikeSend_result.get(0);
						Send(likeSend_duple);
						
					}else {
						
						String tome = LikeSend_result.get(0);
						String toyou = LikeSend_result.get(1);
						String yourNick = LikeSend_result.get(2);
						String token = LikeSend_result.get(3);;		
		
						Send(tome);		//나한테보내는 너의정보
		
						//id값으로 해쉬값 비교하고 그중에 있으면 직접보냄  없을시 푸시알람만.
						try {
							Socket sock = getKey(yourNick);
							if(sock != null) {
								
								ToSend(toyou,sock);
								fcm.LikeSend(token);
								
							} else {
								fcm.LikeSend(token);
							}
							
						}catch(Exception e) {
							System.out.println("상대방에게 전송 실패");
						}
					}
				}
			}
					
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println("LIKE SEND 오류");
		}
	}
	private void Like_Cancel(String msg) {
		ArrayList<String> LikeCancel_result = new ArrayList<String>();
		try {
			LikeCancel_result = pbc_user.LikeCancel(msg);
			if(LikeCancel_result == null) {
				Send("JSON 오류");
				
			} else {

				String code = LikeCancel_result.get(0);
				String yourNumtoMe = LikeCancel_result.get(1);
				String code1 = LikeCancel_result.get(2);
				String MynumToYou = LikeCancel_result.get(3);
				String yourNick = LikeCancel_result.get(4);;		

				Send(code + yourNumtoMe);		//나한테보내는 너의정보

				//id값으로 해쉬값 비교하고 그중에 있으면 직접보냄  없을시 푸시알람만.
				try {
					Socket sock = getKey(yourNick);
					if(sock != null) {
						
						ToSend(code1 + MynumToYou,sock);

					} else {

					}
					
				}catch(Exception e) {
					System.out.println("상대방에게 전송 실패");
				}

			}
					
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println("LIKECANCEL 오류");
		}
	}
	
	private void Like_Deny(String msg) {
		ArrayList<String> LikeDeny_result = new ArrayList<String>();
		try {
			LikeDeny_result = pbc_user.LikeDeny(msg);
			if(LikeDeny_result == null) {
				Send("JSON 오류");
				
			} else {

				String code = LikeDeny_result.get(0);
				String yourNumtoMe = LikeDeny_result.get(1);
				String code1 = LikeDeny_result.get(2);
				String MynumToYou = LikeDeny_result.get(3);
				String yourNick = LikeDeny_result.get(4);;		

				Send(code + yourNumtoMe);		//나한테보내는 너의정보

				//id값으로 해쉬값 비교하고 그중에 있으면 직접보냄  없을시 푸시알람만.
				try {
					Socket sock = getKey(yourNick);
					if(sock != null) {
						
						ToSend(code1 + MynumToYou,sock);

					} else {

					}
					
				}catch(Exception e) {
					System.out.println("상대방에게 전송 실패");
				}
			}
					
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println("LIKEDENY 오류");
		}
	}
	
	private void Like_Accept(String msg) {		
		ArrayList<String> LikeAccept_res = new ArrayList<String>();

		try {
			LikeAccept_res = pbc_user.LikeAccept(msg);
			if(LikeAccept_res == null) {
				Send("JSON 오류");
			} else {
			//여기 엘스이프로 만들기
				
				if(LikeAccept_res.size() > 0) {
					if(LikeAccept_res.get(0).contains("504") == true) {
						
						String likeSend_duple = LikeAccept_res.get(0);
						Send(likeSend_duple);
						
					}else {
						
						String code = LikeAccept_res.get(0);
						String yourInfo = LikeAccept_res.get(1);
						String yourdogs = LikeAccept_res.get(2);
						String code1 = LikeAccept_res.get(3);
						String MyInfo = LikeAccept_res.get(4);
						String Mydogs = LikeAccept_res.get(5);
						String token = LikeAccept_res.get(6);
						String yourNick = LikeAccept_res.get(7);
		
						Send(code + yourInfo + yourdogs);		//나한테보내는 너의정보
		
						//id값으로 해쉬값 비교하고 그중에 있으면 직접보냄  없을시 푸시알람만.
						try {
							Socket sock = getKey(yourNick);
							if(sock != null) {
								
								ToSend(code1 + MyInfo + Mydogs ,sock);
								fcm.LikeSend(token);
								
							} else {
								fcm.LikeSend(token);
							}
							
						}catch(Exception e) {
							System.out.println("상대방에게 전송 실패");
						}
					}
				}
			}
			
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("LIKE ACCEPT 오류");
		}
	}
	
	private void Chatting (String msg) {		
		try {
			String chat_res = pbc_user.Chatting(msg);
			if(chat_res == null) {
				Send("JSON 오류");
			} else if(chat_res.contains("004")) {
				Send(chat_res);
			} else {
				String[] chat_send = chat_res.split("/");
				
				String code = chat_send[0];
				String content = chat_send[1];
				String yourNick = chat_send[2];
				String token = chat_send[3];
				
				Socket sock = getKey(yourNick);
				if(sock != null) {
					
					ToSend(code + "/" + content,sock);

				} else {
					//fcm.ChatSend(token);
				}
			}
	
		}catch(Exception e) {
			
		}
	}
	
//===========================콘솔로그 , 접속자리스트,접속자 추가 및 제거, 데이터 전송=================================================================================================	=================================
	private void ConsoleLog(String msg) {
		System.out.println("\r\n#############################################################################################");
		System.out.println(format_time1 + " : 클라이언트[" + socket.getInetAddress().toString() + "]로부터 수신 : " + msg);

	}
	
	private void ToSend(String msg,Socket sock) {

		try {
			DataOutputStream DO = new DataOutputStream(sock.getOutputStream());
			
			byte[] Client = new byte[HeaderSize];
	        Client = ic.intToByteArray(CLIENT);
	        DO.write(Client);                               
	        DO.flush();
	        
	        DO.writeUTF(JSON + "/" + msg);
	        
	        System.out.println(format_time1 + " : 현재클라이언트가  클라이언트[" + sock.getInetAddress().toString() + "]로 발신 : " + msg);
			System.out.println("#############################################################################################\r\n");
			
		} catch(IOException e) {
			System.out.println("상대방에게 전송 실패");
		}

	}
	
	private void AddClient(String id) {
		C_map.put(socket, id);
		Show_ListOfConnector();
		
	}
	
	private void RemoveClient() throws IOException {
		
		if(C_map.size() > 0) {
			
			Set<Socket> set = C_map.keySet();
			
			Iterator<Socket> it = set.iterator();
			
			while(it.hasNext()) {
				Socket key = (Socket)it.next();
				if(key.equals(socket)) {
					C_map.remove(socket);
					Show_ListOfConnector();
					System.out.println("있음");

					break;
					
				}
			}		
		} else {
			System.out.println("업음");
			Show_ListOfConnector();
		}

	}
	
	private void Show_ListOfConnector() {
		
		System.out.println("				************************************접속자 리스트***********************************************");
		for(Map.Entry<Socket,String> elem : C_map.entrySet()){

			Socket key = elem.getKey();
			String value = elem.getValue();

			System.out.println("								"+key.getInetAddress()+" : "+value);
		}
		System.out.println("				*******************************************************************************************");
	}
	
	private Socket getKey(String value) { 
		try {
			String id = null;
			Socket sock = null;
			for(int i = 0; i < user_info.size(); i ++) {
				if(user_info.get(i).U_NICK.equals(value)) {
					id = user_info.get(i).U_ID;
					
					Iterator<Socket> it = C_map.keySet().iterator();
					while(it.hasNext()) {
						sock = it.next();
						if(C_map.get(sock).equals(id)) {
							return sock;
						}
					}
					
				} 
			}
			return null;
		}catch(Exception e) {

			return null; 
		}
	}


	
	private void Send(String Msg) {
		try {

            byte[] Client = new byte[HeaderSize];
            Client = ic.intToByteArray(CLIENT);
            out.write(Client);                               //0.User인지를 보냄
            out.flush();
			
            out.writeUTF(JSON + "/" + Msg);
			
			System.out.println(format_time1 + " : 클라이언트[" + socket.getInetAddress().toString() + "]로 발신 : " + Msg);
			System.out.println("#############################################################################################\r\n");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
	
	private void Send_M(String Msg) {
		try {
		

            byte[] manager = new byte[HeaderSize];
            manager = ic.intToByteArray(MANAGER);
            out.write(manager);                               //0.User인지를 보냄
            out.flush();
			
            
            String str = "JSON" + "\"";
            byte[] string = Msg.getBytes();
            int stringSize = string.length;
            
            
            
            byte[] strSize = new byte[4];
            strSize = ic.intToByteArray(stringSize);
            
            out.write(strSize,0,4);
            out.flush();
            
            //out.write(string,0,stringSize);
            out.write(string,0,stringSize);
            out.flush();
			
			System.out.println(format_time1 + " : 클라이언트[" + socket.getInetAddress().toString() + "]로 발신 : " + Msg);
			System.out.println("#############################################################################################\r\n");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
	
	private void SendTextPics(String Msg) {
		try {

            byte[] Client = new byte[HeaderSize];
            Client = ic.intToByteArray(CLIENT);
            out.write(Client);                               //0.User인지를 보냄
            out.flush();

            out.writeUTF(JSON + "/" + Msg);
            System.out.println(format_time1 + " : 클라이언트[" + socket.getInetAddress().toString() + "]로 발신 : " + Msg);
            //===========================================================================================================
            byte[] pic = new byte[HeaderSize];
            pic = ic.intToByteArray(DATA);
            out.write(pic);                               //(사진정보임을표현함 여기선)
            out.flush();
            
			out.writeUTF(IMAGE +"/" + Msg);
			out.writeInt(1);
			
			for(int i=0; i < 1;i++) {
				String filePath = "D:\\AndroidStudio-Study-Github\\Together\\Together_Test\\Picture\\d\\SignUp-d-0.bmp";
				
				/*
				 * //File file = new File(filePath); String encodedString =
				 * pc.BitmapToString(filePath); byte[] encodedByte = encodedString.getBytes();
				 * out.writeLong(encodedByte.length); out.write(encodedByte); out.flush();
				 */

				File file = new File(filePath); 
				FileInputStream fis = new FileInputStream(file);
				 
				System.out.println("보낼파일크기: " +file.length());
				 
				int len; 
				int size = 4096; 
				byte[] data = new byte[size]; //데이터 전송 최소단위
				 
				out.writeLong(file.length());
				 
				while((len = fis.read(data)) != -1) { 
					out.write(data,0,len); 
				}
				  
				out.flush(); 
				fis.close();
				 
	            System.out.println("데이터전송성공: " +file.length());
				System.out.println(format_time1 + " : 클라이언트[" + socket.getInetAddress().toString() + "]로 데이터 전송 : " + file.length());
			}
			System.out.println("#############################################################################################\r\n");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
