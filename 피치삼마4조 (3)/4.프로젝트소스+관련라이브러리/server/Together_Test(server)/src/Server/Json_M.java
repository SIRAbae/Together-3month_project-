package Server;

import com.google.gson.JsonObject;

public class Json_M {
	public String Userinfo(String Msg) {
		String error = "Error";
		try
		{
			String[] token = Msg.split("/");
			String user_no = token[0];
			String name = token[1];
			String birth = token[2];
			String gender = token[3];
			String phone = token[4];
			String id = token[5];
			String pw = token[6];
			String nick = token[7];
			String region = token[8];
			String walktime = token[9];
			String date = token[10];
			String intro = token[11];
			String member = token[12];
			String tok = token[13];

			JsonObject userinfo = new JsonObject();
			
			userinfo.addProperty("User_no", user_no);
			userinfo.addProperty("Name", name);
			userinfo.addProperty("Birth", birth);
			userinfo.addProperty("Gender", gender);
			userinfo.addProperty("Phone", phone);
			userinfo.addProperty("ID", id);
			userinfo.addProperty("PW", pw);
			userinfo.addProperty("Nick", nick);
			userinfo.addProperty("Region", region);
			userinfo.addProperty("Walktime", walktime);
			userinfo.addProperty("Date", date);
			userinfo.addProperty("Intro", intro);
			userinfo.addProperty("Member", member);
			userinfo.addProperty("Token", tok);
			
			String userinfo_res = userinfo.toString();
			
			System.out.println(userinfo_res);	
			
			return userinfo_res;
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return error;
	}
	
}
