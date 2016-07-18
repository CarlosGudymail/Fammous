package famm.fammous.start;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import famm.fammous.MainActivity;
import famm.fammous.R;
import famm.fammous.bbdd.OperacionesBBDD;
import famm.fammous.bbdd.StartBBDD;
import famm.fammous.bbdd.UserBBDD;
import famm.fammous.connection.ApiConnection;
import famm.fammous.connection.Callback;
import famm.fammous.connection.JResponseTokken;
import famm.fammous.util.ConnectionDetector;
import famm.fammous.util.ImageLoadTask;
import famm.fammous.util.UserProfile;

//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.Tracker;

public class SplashScreen extends Activity {
	
	private boolean connectionFlag;
	private StartBBDD startBBDD;
	private SQLiteDatabase db;
	private OperacionesBBDD opBBDD;
	private UserBBDD userBBDD;
	private ArrayList <String> listBBDD;
	private UserProfile userProfile;
	private ImageView imageViewSplash;
	private Animation animation;
	private ProgressBar progressBar;
	private String email, password, tokken;
	private TextView textViewPercent;
	private ConnectionDetector connectionDetector;
	private ArrayList listResponse;
	private Toast toast;
	private ApiConnection api;
	private List<String> args;
	private final String TAG = "SplashScreen";
	private Context context;
	private JResponseTokken jResponseTokken;
	private ImageLoadTask imageLoadTask;
	//public static GoogleAnalytics analytics;
	//public static Tracker tracker;
	//private Operations operations;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);

		/*analytics = GoogleAnalytics.getInstance(this);
		analytics.setLocalDispatchPeriod(1800);

		tracker = analytics.newTracker("UA-000-1"); // Replace with actual tracker id
		tracker.enableExceptionReporting(true);
		tracker.enableAdvertisingIdCollection(true);
		tracker.enableAutoActivityTracking(true);*/

		context = this;
		
		//Inicia la base de datos y comprueba si el usuario tiene conexión a internet
        //startBBDD = new StartBBDD(this);
        connectionDetector = new ConnectionDetector(this);
        connectionFlag = connectionDetector.isNetworkConnected();
        jResponseTokken = new JResponseTokken();
		imageLoadTask = new ImageLoadTask();
        
        /*try {
    		startBBDD.createDataBase(this);
    	} catch (IOException ioe) {
    		throw new Error("Unable to create database");
    	}
      
		//Base de datos en modo escritura
    	db = startBBDD.getWritableDatabase(); 			
    	//startBBDD.borrarTablas(db);
    	
		if(db != null){		
			userBBDD = new UserBBDD(db);		    	
			startBBDD.crearTablas(db); //Crea las tablas en la base de datos si no han sido creadas			
        }	*/

		opBBDD = new OperacionesBBDD();
		opBBDD.init(this);
		userBBDD = new UserBBDD();
		
		userProfile = new UserProfile();	
		api = new ApiConnection();
		
		//Crea la carpeta para almacenar los v�deos
		//File folder = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Fammous");
		String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Fammous/";
		try
		{
		    File dir = new File(fullPath);
		    if (!dir.exists()) {
		      dir.mkdirs();
		    }
		}
		 catch (Exception e) {
		   Log.e(TAG, "Exception al crear la carpeta" + e.getMessage());
		}		
		
		//Obtiene el usuario y la contrase�a de la base de datos
		if(userBBDD.isEmpty() == false){
			if(connectionFlag == true){				
				listBBDD = userBBDD.get();
				email = (String) listBBDD.get(0);
				tokken = (String) listBBDD.get(1);
				//String pass = BCrypt.hashpw(password, BCrypt.gensalt(5));
				args = new ArrayList<String>();
				args.add(tokken);	
				
				//Obtiene el tokken
				api.connectionGet(this, "checkTokken", args, new Callback<Integer>(){
					@Override
					public void onResponse(Integer t) {
						listResponse = api.getResult();
						
				    	//Si la respuesta del servidor ha sido OK code=100
						if(listResponse.size() == 2 ){
							if((Boolean)listResponse.get(1) == true){
								args.clear();															
								args.add(tokken);		
								jResponseTokken = (JResponseTokken) listResponse.get(0);
								String url = jResponseTokken.getMetadata().getAutologin_url();
								downloadImages();

								if(url.isEmpty() == false){
									//String url = url.replaceAll("\", "");
									Intent i = new Intent(SplashScreen.this, MainActivity.class);
									i.putExtra("urlHome", url);
									startActivity(i);
									finish();	
								}
								else{
									Intent i = new Intent(SplashScreen.this, MainActivity.class);
									startActivity(i);
									finish();
								}
							}
							else{
								Intent i = new Intent(SplashScreen.this, MainActivity.class);
								startActivity(i);
								finish();
							}
						}
						else{
							Intent i = new Intent(SplashScreen.this, MainActivity.class);
							startActivity(i);
							finish();
						}						
					}					
				});				
			}
			else{
				onDestroy();
			}			
		}	
		//No tiene datos del registro en la base de datos
		else{
			if(connectionFlag == true){				
				Intent i = new Intent(SplashScreen.this, StartScreen.class);
				startActivity(i);
				finish();
			}
			else{			
				onDestroy();
			}
		}

		/*Intent i = new Intent(SplashScreen.this, MainActivity.class);
		i.putExtra("urlHome", "https://famm.fammous.com/es/fammous/tokkenlogin/a129159df4937d268967cbfb30b5893b");
		startActivity(i);*/
	}

	private void downloadImages() {

		try{
			String urlImage = jResponseTokken.getMetadata().getUrl().getProfile_pic();
			String routeUrlImage = Environment.getExternalStorageDirectory().getPath() + "/Fammous/ProfilePic.jpg";
			File f = new File(routeUrlImage);
			/*if(f.exists()){
				f.delete();
			}*/
			//imageLoadTask.getBitmapFromUrl(urlImage, routeUrlImage);
			new ImageLoadTask(urlImage, routeUrlImage).execute();

		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			String urlImage2 = jResponseTokken.getMetadata().getUrl().getCover_pic();
			String routeUrlImage2 = Environment.getExternalStorageDirectory().getPath() + "/Fammous/CoverPic.jpg";
			File f = new File(routeUrlImage2);
			/*if(f.exists()){
				f.delete();
			}*/
			new ImageLoadTask(urlImage2, routeUrlImage2).execute();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onStop() {
	    super.onStop();	
	    finish();
	}
	  
	@Override
	public void onDestroy(){
		super.onDestroy();
		
	}
	
	
}
