package Server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ParserBycode_Manager {
	
	private HashMap<Socket,String> C_map = null;		//현재 접속자담을 리스트
	private ArrayList<User_info> user_info = null; 		//정규회원 유저 리스트
	private Socket socket = null;						//User 주소
	
	private DBcon_Manager db = null;
	private intConverter ic = null;
	private PicConvert pc = null;
	private GetDistanceByPos dis = null;
	
	private Json_M js = null;
	//========================================
	final static String GetAllUser = "GETALLUSER";
	
	final static String APPROVED = "APPROVED";
	final static String DENIED = "DENIED";

	
	//=======================================
	
	
	public ParserBycode_Manager(Socket sock,HashMap<Socket,String> clientmap,ArrayList<User_info> uinfo) {
		C_map = clientmap;
		user_info = uinfo;
		socket = sock;
		
		js = new Json_M();
		db = new DBcon_Manager(socket);
		dis = new GetDistanceByPos();
	}
	
	public String getAllUser() {
		
		try {
			ArrayList<User_info_M> u_list = db.getAllUser();
			
			String getAll_res = "";
			for(int i = 0; i < u_list.size(); i++) {
				String Msg = "!" + u_list.get(i).user_no + "/" + u_list.get(i).U_NAME + "/" + u_list.get(i).U_BIRTH + "/" +
						u_list.get(i).U_GENDER + "/" + u_list.get(i).U_PHONE + "/" + u_list.get(i).U_ID + "/" +
						u_list.get(i).U_PW + "/" + u_list.get(i).U_NICK + "/" + u_list.get(i).U_REGION + "/" +
						u_list.get(i).U_WALKTIME + "/" + u_list.get(i).U_DATE + "/" + u_list.get(i).U_INTRO + "/" +
						u_list.get(i).U_LAT + "/" + u_list.get(i).U_LONG + "/" +
						u_list.get(i).U_MEMBER + "/" + u_list.get(i).U_TOKEN;
						
				getAll_res += Msg;
			}
			
			String res = GetAllUser + "@" + getAll_res;
			
			System.out.println(res);
			return res;
			
		} catch(Exception e) {
			System.out.println("실패");
			return null;
		}
	}
	
	public String Approve(String id, String token) {
		
		try {
			boolean IsSuccess = db.Approve(id);
			if(IsSuccess == true) {
				String res = APPROVED + "@" + "승인완료";
				
				return res;
			}else {
				String res = DENIED + "@" + "승인실패";

				return res;
			}
		}catch(Exception e){
			return null;
		}
		
	}

}
