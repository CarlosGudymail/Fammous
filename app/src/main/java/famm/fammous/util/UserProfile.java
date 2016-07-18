package famm.fammous.util;

import famm.fammous.connection.JResponseTokken;

public class UserProfile {

	private static JResponseTokken jResponseTokken;

	
	public UserProfile(){		
	}

	public static JResponseTokken getjResponseTokken() {
		return jResponseTokken;
	}

	public static void setjResponseTokken(JResponseTokken jResponseTokken) {
		UserProfile.jResponseTokken = jResponseTokken;
	}
}
