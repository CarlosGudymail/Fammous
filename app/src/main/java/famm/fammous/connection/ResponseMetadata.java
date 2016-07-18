package famm.fammous.connection;

public class ResponseMetadata {

	private String status;
	private String tokken;
	private User_data user_data;
	private User_group user_group;
	private String autologin_url;
	private Images url;
	
	public String getStatus() {
		return status;
	}

	public String getTokken() {
		return tokken;
	}

	public User_data getUser_data() {
		return user_data;
	}

	public User_group getUser_group() {
		return user_group;
	}

	public String getAutologin_url() {
		return autologin_url;
	}

	public Images getUrl() {
		return url;
	}
}
