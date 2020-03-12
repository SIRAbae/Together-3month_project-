package Server;

import com.google.gson.JsonObject;

public class Json {
	
	public String LoginResult(String Msg) {
		String error = "Error";
		try {
			String[] token = Msg.split(":");
			
			if(token[0].equals("001")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String Login_res = res.toString();
				
				System.out.println(Login_res);	
				
				return Login_res;
			}
			else if(token[0].equals("002")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String Login_res = res.toString();
				
				System.out.println(Login_res);	
				
				return Login_res;
			}
			else if(token[0].equals("003")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String Login_res = res.toString();
				
				System.out.println(Login_res);	
				
				return Login_res;
			} else if(token[0].equals("004")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String SignUp_res = res.toString();
				
				System.out.println(SignUp_res);	
				
				return SignUp_res;
			} else if(token[0].equals("006")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String SignUp_res = res.toString();
				
				System.out.println(SignUp_res);	
				
				return SignUp_res;
			} else if(token[0].equals("007")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String SignUp_res = res.toString();
				
				System.out.println(SignUp_res);	
				
				return SignUp_res;
			}
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return error;
	}
	public String Userinfo(String sig,String Msg) {
		String error = "Error";
		try
		{
			String[] token = Msg.split("/");
			String name = token[0];
			String birth = token[1];
			String gender = token[2];
			String phone = token[3];
			String id = token[4];
			String pw = token[5];
			String nick = token[6];
			String region = token[7];
			String walktime = token[8];
			String date = token[9];
			String intro = token[10];
			String member = token[11];

			JsonObject userinfo = new JsonObject();
			
			userinfo.addProperty("Signal", sig);
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
			
			String userinfo_res = userinfo.toString();
			
			System.out.println(userinfo_res);	
			
			return userinfo_res;
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return error;
	}
	
	public String Petinfo(String sig,String Msg) {
		String error = "Error";
		try
		{
			String[] token = Msg.split("/");
			String name = token[0];
			String type = token[1];
			String size = token[2];
			int age = Integer.parseInt(token[3]);
			String birth = token[4];
			String gender = token[5];
			String neutral = token[6];
			String vaccine = token[7];
			String character = token[8];
			String intro = token[9];

			JsonObject petinfo = new JsonObject();
			
			petinfo.addProperty("Signal", sig);
			petinfo.addProperty("Name", name);
			petinfo.addProperty("Type", type);
			petinfo.addProperty("Size", size);
			petinfo.addProperty("Age", age);
			petinfo.addProperty("Birth", birth);
			petinfo.addProperty("Gender", gender);
			petinfo.addProperty("Neutral", neutral);
			petinfo.addProperty("Vaccine", vaccine);
			petinfo.addProperty("Character", character);
			petinfo.addProperty("Intro", intro);

			
			String petinfo_res = petinfo.toString();
			
			System.out.println(petinfo_res);	
			
			return petinfo_res;			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return error;
	}
	
	public String RandomUserInfo(String sig,String Msg) {
		String error = "Error";
		try
		{
			String[] token = Msg.split("/");
			String userno = token[0];
			String name = token[1];
			String birth = token[2];
			String gender = token[3];
			String nick = token[4];
			String region = token[5];
			String walktime = token[6];
			String intro = token[7];
			double distance = Double.parseDouble(token[8]);



			JsonObject userinfo = new JsonObject();
			
			userinfo.addProperty("Signal", Integer.parseInt(sig));
			userinfo.addProperty("UserNum", userno);
			userinfo.addProperty("Name", name);
			userinfo.addProperty("Birth", birth);
			userinfo.addProperty("Gender", gender);
			userinfo.addProperty("Nick", nick);
			userinfo.addProperty("Region", region);
			userinfo.addProperty("Walktime", walktime);
			userinfo.addProperty("Intro", intro);
			userinfo.addProperty("Distance", distance);
			
			String userinfo_res = userinfo.toString();
			
			System.out.println(userinfo_res);	
			
			return userinfo_res;
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return error;
		
	}
	
	public String RandomPetInfo(String sig,String Msg) {
		String error = "Error";
		try
		{
			String[] token = Msg.split("/");
			String userno = token[0];
			String name = token[1];
			String type = token[2];
			String size = token[3];
			int age = Integer.parseInt(token[4]);
			String birth = token[5];
			String gender = token[6];
			String neutral = token[7];
			String vaccine = token[8];
			String character = token[9];
			String intro = token[10];

			JsonObject petinfo = new JsonObject();
			
			petinfo.addProperty("Signal", Integer.parseInt(sig));
			petinfo.addProperty("UserNum", userno);
			petinfo.addProperty("Name", name);
			petinfo.addProperty("Type", type);
			petinfo.addProperty("Size", size);
			petinfo.addProperty("Age", age);
			petinfo.addProperty("Birth", birth);
			petinfo.addProperty("Gender", gender);
			petinfo.addProperty("Neutral", neutral);
			petinfo.addProperty("Vaccine", vaccine);
			petinfo.addProperty("Character", character);
			petinfo.addProperty("Intro", intro);

			
			String petinfo_res = petinfo.toString();
			
			System.out.println(petinfo_res);	
			
			return petinfo_res;			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return error;
	}
	
	public String YourUserInfo(String sig,String Msg) {
		String error = "Error";
		try
		{
			String[] token = Msg.split("/");
			String user_no = token[0];
			String name = token[1];
			String birth = token[2];
			String gender = token[3];
			String nick = token[4];
			String region = token[5];
			String walktime = token[6];
			String intro = token[7];


			JsonObject userinfo = new JsonObject();
			
			userinfo.addProperty("Signal", sig);
			userinfo.addProperty("UserNum", user_no);
			userinfo.addProperty("Name", name);
			userinfo.addProperty("Birth", birth);
			userinfo.addProperty("Gender", gender);
			userinfo.addProperty("Nick", nick);
			userinfo.addProperty("Region", region);
			userinfo.addProperty("Walktime", walktime);
			userinfo.addProperty("Intro", intro);

			
			String userinfo_res = userinfo.toString();
			
			System.out.println(userinfo_res);	
			
			return userinfo_res;
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return error;
		
	}
	
	public String YourPetInfo(String sig,String Msg) {
		String error = "Error";
		try
		{
			String[] token = Msg.split("/");
			String userno = token[0];
			String name = token[1];
			String type = token[2];
			String size = token[3];
			int age = Integer.parseInt(token[4]);
			String birth = token[5];
			String gender = token[6];
			String neutral = token[7];
			String vaccine = token[8];
			String character = token[9];
			String intro = token[10];

			JsonObject petinfo = new JsonObject();
			
			petinfo.addProperty("Signal", sig);
			petinfo.addProperty("UserNum", userno);
			petinfo.addProperty("Name", name);
			petinfo.addProperty("Type", type);
			petinfo.addProperty("Size", size);
			petinfo.addProperty("Age", age);
			petinfo.addProperty("Birth", birth);
			petinfo.addProperty("Gender", gender);
			petinfo.addProperty("Neutral", neutral);
			petinfo.addProperty("Vaccine", vaccine);
			petinfo.addProperty("Character", character);
			petinfo.addProperty("Intro", intro);

			
			String petinfo_res = petinfo.toString();
			
			System.out.println(petinfo_res);	
			
			return petinfo_res;			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return error;
	}
	
	public String PetRegi_result(String Msg) {
		String error = "Error";
		try {
			String[] token = Msg.split(":");
			if(token[0].equals("401")) {
				
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String Logout_res = res.toString();
				
				System.out.println(Logout_res);	
				
				return Logout_res;
			} else if(token[0].equals("402")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String SignUp_res = res.toString();
				
				System.out.println(SignUp_res);	
				
				return SignUp_res;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return error;
	}
	
	public String LikeSend_result(String Msg) {
		String error = "Error";
		try {
			String[] token = Msg.split(":");
			if(token[0].equals("501")) {
				
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String LikeSend_res = res.toString();
				
				System.out.println(LikeSend_res);	
				
				return LikeSend_res;
			} else if(token[0].equals("502")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String LikeSend_res = res.toString();
				
				System.out.println(LikeSend_res);	
				
				return LikeSend_res;
			} else if(token[0].equals("503")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String LikeSend_res = res.toString();
				
				System.out.println(LikeSend_res);	
				
				return LikeSend_res;
				
			} else if(token[0].equals("504")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String LikeSend_res = res.toString();
				
				System.out.println(LikeSend_res);	
				
				return LikeSend_res;
				
			} else if(token[0].equals("505")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String LikeSend_res = res.toString();
				
				System.out.println(LikeSend_res);	
				
				return LikeSend_res;
			} else if(token[0].equals("506")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String LikeSend_res = res.toString();
				
				System.out.println(LikeSend_res);	
				
				return LikeSend_res;
			} else if(token[0].equals("507")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String LikeSend_res = res.toString();
				
				System.out.println(LikeSend_res);	
				
				return LikeSend_res;
			} else if(token[0].equals("508")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String LikeSend_res = res.toString();
				
				System.out.println(LikeSend_res);	
				
				return LikeSend_res;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return error;
	}
	
	public String LikeAccept_result(String Msg) {
		String error = "Error";
		try {
			String[] token = Msg.split(":");
			if(token[0].equals("509")) {
				
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String LikeAccept_res = res.toString();
				
				System.out.println(LikeAccept_res);	
				
				return LikeAccept_res;
			} else if(token[0].equals("510")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String LikeAccept_res = res.toString();
				
				System.out.println(LikeAccept_res);	
				
				return LikeAccept_res;
			} else if(token[0].equals("511")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String LikeAccept_res = res.toString();
				
				System.out.println(LikeAccept_res);	
				
				return LikeAccept_res;
				
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return error;
	}
	
	public String LikeCancel_info(String Msg) {
		String error = "Error";
		try
		{
			String[] token = Msg.split("/");
			String MyNum = token[0];
			String YourNum = token[1];

			JsonObject LikeCancelinfo = new JsonObject();
			
			LikeCancelinfo.addProperty("MyUserNum", MyNum);
			LikeCancelinfo.addProperty("YourUserNum", YourNum);
			
			String LikeCancel_res = LikeCancelinfo.toString();
			
			System.out.println(LikeCancel_res);	
			
			return LikeCancel_res;			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return error;
	}
	
	public String LikeDeny_info(String Msg) {
		String error = "Error";
		try
		{
			String[] token = Msg.split("/");
			
			String YourNum = token[0];
			String MyNum = token[1];
			
			JsonObject LikeDenyinfo = new JsonObject();
			
			LikeDenyinfo.addProperty("YourUserNum", YourNum);
			LikeDenyinfo.addProperty("MyUserNum", MyNum);
			
			String LikeDeny_res = LikeDenyinfo.toString();
			
			System.out.println(LikeDeny_res);	
			
			return LikeDeny_res;			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return error;
	}
	
	public String YourUserNum(String Msg) {
		String error = "Error";
		try {
			String YourNum = Msg;
			
			JsonObject YourUserNum = new JsonObject();
			
			YourUserNum.addProperty("YourUserNum", YourNum);
			
			String YourUserNum_res = YourUserNum.toString();
			
			System.out.println(YourUserNum_res);	
			
			return YourUserNum_res;		
			
		}catch(Exception e) {
			e.printStackTrace();

		}
		return error;
	}
	
	public String RanMatch_result(String Msg) {
		String error = "Error";
		try {
			String[] token = Msg.split(":");
			if(token[0].equals("301")) {
				
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String Logout_res = res.toString();
				
				System.out.println(Logout_res);	
				
				return Logout_res;
			} else if(token[0].equals("302")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String SignUp_res = res.toString();
				
				System.out.println(SignUp_res);	
				
				return SignUp_res;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return error;
	}
	
	public String Chatting_result(String Msg) {
		String error = "Error";
		try {
			String[] token = Msg.split(":");
			if(token[0].equals("701")) {
				
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String Logout_res = res.toString();
				
				System.out.println(Logout_res);	
				
				return Logout_res;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return error;
	}
	
	public String Chatting_Send(String my, String your, String content) {
		String error = "Error";
		try
		{
			
			JsonObject chatinfo = new JsonObject();
			
			chatinfo.addProperty("MyNickname", my);
			chatinfo.addProperty("YourNickname", your);
			chatinfo.addProperty("Content", content);

			String chat_res = chatinfo.toString();
			
			System.out.println(chat_res);	
			
			return chat_res;			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return error;
	}
	
	public String Logout_result(String Msg) {
		String error = "Error";
		try {
			String[] token = Msg.split(":");
			if(token[0].equals("005")) {
				
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String Logout_res = res.toString();
				
				System.out.println(Logout_res);	
				
				return Logout_res;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return error;
	}
	
	public String IdCheck_result(String Msg) {
		String err = "Error";
		try {
			String[] token = Msg.split(":");
			if(token[0].equals("102")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String SignUp_res = res.toString();
				
				System.out.println(SignUp_res);	
				
				return SignUp_res;
				
			} else if(token[0].equals("103")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String SignUp_res = res.toString();
				
				System.out.println(SignUp_res);	
				
				return SignUp_res;
				
			} else if(token[0].equals("004")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String SignUp_res = res.toString();
				
				System.out.println(SignUp_res);	
				
				return SignUp_res;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return err;
	}
	
	public String NickCheck_result(String Msg) {
		String err = "Error";
		try {
			String[] token = Msg.split(":");
			if(token[0].equals("104")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String SignUp_res = res.toString();
				
				System.out.println(SignUp_res);	
				
				return SignUp_res;
				
			} else if(token[0].equals("105")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String SignUp_res = res.toString();
				
				System.out.println(SignUp_res);	
				
				return SignUp_res;
				
			} else if(token[0].equals("004")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String SignUp_res = res.toString();
				
				System.out.println(SignUp_res);	
				
				return SignUp_res;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return err;
	}
	
	public String SignUp_result(String Msg) {
		String err = "Error";
		try {
			String[] token = Msg.split(":");
			if(token[0].equals("101")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String SignUp_res = res.toString();
				
				System.out.println(SignUp_res);	
				
				return SignUp_res;
				
			} else if(token[0].equals("106")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String SignUp_res = res.toString();
				
				System.out.println(SignUp_res);	
				
				return SignUp_res;
				
			} else if(token[0].equals("004")) {
				String code_Num = token[0];
				String code_Content = token[1];
				
				JsonObject res = new JsonObject();
				
				res.addProperty("CODE", code_Num);
				res.addProperty("CONTENT", code_Content);
				
				String SignUp_res = res.toString();
				
				System.out.println(SignUp_res);	
				
				return SignUp_res;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return err;
		
	}
}
