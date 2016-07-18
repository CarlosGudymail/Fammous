package famm.fammous.video;

import android.content.Context;

import java.util.ArrayList;

public class ComunicationService {

	private static String videoRoute;	
	private static Context context;
	private static ArrayList args;
	private static String function;
	private static ArrayList listParams;

	public static ArrayList getParams() {
		return listParams;
	}

	public static void setParams(ArrayList listParams) {
		ComunicationService.listParams = listParams;
	}

	public ComunicationService(){			
	}

	public static String getVideoRoute() {
		return videoRoute;
	}

	public static void setVideoRoute(String videoRoute) {
		ComunicationService.videoRoute = videoRoute;
	}

	public static Context getContext() {
		return context;
	}

	public static void setContext(Context context) {
		ComunicationService.context = context;
	}

	public static ArrayList getArgs() {
		return args;
	}

	public static void setArgs(ArrayList args) {
		ComunicationService.args = args;
	}

	public static String getFunction() {
		return function;
	}

	public static void setFunction(String function) {
		ComunicationService.function = function;
	}

	public void clear() {
		args = new ArrayList();			
		videoRoute = null;	
		function = "";
	}
}
