package Server;

//서버 오픈시 사용자 정보를 전부 객체화 함.

public class User_info {
	int user_no;
	String U_NAME;
	String U_BIRTH;
	String U_GENDER;
	String U_PHONE;
	String U_ID;
	String U_PW;
	String U_NICK;
	String U_REGION;
	String U_WALKTIME;
	String U_DATE;
	String U_INTRO;
	double U_LAT;
	double U_LONG;
	int    U_MEMBER;
	
	public User_info(int u_no,String name,String birth,String gender, String phone, String id, String pw, String nick,
			String region, String walktime, String date, String intro, double lat, double lon,int member) {
		
		user_no = u_no;
		U_NAME = name;
		U_BIRTH = birth;
		U_GENDER = gender;
		U_PHONE = phone;
		U_ID = id;
		U_PW = pw;
		U_NICK = nick;
		U_REGION = region;
		U_WALKTIME = walktime;
		U_DATE = date;
		U_INTRO = intro;
		U_LAT = lat;
		U_LONG = lon;
		U_MEMBER = member;
		
	}

}
