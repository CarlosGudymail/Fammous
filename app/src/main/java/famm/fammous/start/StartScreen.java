package famm.fammous.start;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import famm.fammous.MainActivity;
import famm.fammous.R;
import famm.fammous.bbdd.UserBBDD;
import famm.fammous.connection.ApiConnection;
import famm.fammous.connection.Callback;
import famm.fammous.connection.JResponseTokken;
import famm.fammous.util.DebugAct;
import famm.fammous.video.SendVideo;

public class StartScreen extends Activity {	
	
	private ApiConnection api;
	private List <String> args;
	private Button buttonLogin;
	private EditText editTextEmail, editTextPassword;
	private LinearLayout layoutLoad;
	private String email, password;	
	private ArrayList listResponse;
	private JResponseTokken jResponseTokken;
	private String tokken;
	private DebugAct debug;
	private Bundle extras;
	private String TAG = "StartScreen";
	private Boolean communication = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_screen);
		Log.d(TAG, "onCreate");
		
		buttonLogin = (Button) findViewById(R.id.buttonLoginStart);
		editTextEmail = (EditText) findViewById(R.id.editTextEmailStart);
		editTextPassword = (EditText) findViewById(R.id.editTextPasswordStart);
		layoutLoad = (LinearLayout) findViewById(R.id.layoutLoadStart);
		
		api = new ApiConnection();
		debug = new DebugAct();
		args = new ArrayList <String>();
		args.clear();	
		listResponse = new ArrayList();

		if(debug.isDebug()) {
			editTextEmail.setText("marca@famm.com");
			editTextPassword.setText("monicaca");
		}

		//En la pantalla de enviar vídeo, el tokken se ha caducado y debe iniciar sesión nuevamente
		extras = getIntent().getExtras();
		if (extras != null){
			communication = extras.getBoolean("failSend");
		}
		
		editTextEmail.setOnKeyListener(new View.OnKeyListener(){				
			@Override
	        public boolean onKey(View v, int key, KeyEvent event) {
			if (event.getAction()==KeyEvent.ACTION_DOWN && key == KeyEvent.KEYCODE_ENTER){
	            return true;
	        }
			if (event.getAction()==KeyEvent.ACTION_DOWN && key == KeyEvent.KEYCODE_SPACE){
	           	return true;
	        }
	        return false;
			}
		});

		buttonLogin.setOnClickListener(new TextView.OnClickListener(){
			public void onClick(View view) {	
				email = editTextEmail.getText().toString();
				password = editTextPassword.getText().toString();
				args.clear();
				args.add(email);
				args.add(password);
				if(email.isEmpty() || password.isEmpty()){					
					Toast.makeText(StartScreen.this, getResources().getString(R.string.toastStartSignup), Toast.LENGTH_LONG).show();
				}
				else{
					layoutLoad.setVisibility(View.VISIBLE);
					api.connectionGet(StartScreen.this, "getTokken", args, new Callback<Integer>() {
					    public void onResponse(Integer i) {
					    	listResponse = api.getResult();
					    	jResponseTokken = (JResponseTokken) listResponse.get(0);
					    	tokken = jResponseTokken.getMetadata().getTokken();
					    	//Si la respuesta del servidor ha sido OK code=100
							if(listResponse.size() == 2 ){
								if((Boolean)listResponse.get(1) == true){
									UserBBDD userBBDD = new UserBBDD();	
									if(userBBDD.isEmpty() && tokken != null){
										userBBDD.insertUser(email, tokken);
									}
									else{
										userBBDD.update(email, tokken);
									}


									//El tokken al enviar el vídeo era inválido y el usuario ha tenido que iniciar sesión de nuevo
									if(communication == true){
										Intent intent = new Intent(StartScreen.this, SendVideo.class);
										intent.putExtra("start" , "start");
										startActivity(intent);
										finish();
									}
									else{
										String url = jResponseTokken.getMetadata().getAutologin_url();

										if(url.isEmpty() == false){
											//String url = url.replaceAll("\", "");
											Intent intent = new Intent(StartScreen.this, MainActivity.class);
											intent.putExtra("urlHome", url);
											startActivity(intent);
											finish();
										}
										else{
											noConnection();
										}
									}
								}
							}
					    }
					});					
					
					layoutLoad.setVisibility(View.INVISIBLE);
					
				}
			}			
		});				
	}
	
	public void noConnection(){
		Toast.makeText(this, getResources().getString(R.string.toastStart), Toast.LENGTH_LONG).show();
		finish();
	}
	
	public void wrongData(){
		Toast.makeText(this, getResources().getString(R.string.toastStartSignup), Toast.LENGTH_LONG).show();
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if(layoutLoad.getVisibility() == View.VISIBLE){
			layoutLoad.setVisibility(View.INVISIBLE);
		}	
		super.onDestroy();
	}
	

	@Override
	public void finish() {			
		super.finish();
	}
	
	
	
}
	

