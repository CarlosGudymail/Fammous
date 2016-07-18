package famm.fammous.video;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;

import famm.fammous.R;
import famm.fammous.bbdd.OperacionesBBDD;
import famm.fammous.bbdd.UserBBDD;
import famm.fammous.bbdd.VideoBBDD;
import famm.fammous.connection.ApiConnection;
import famm.fammous.connection.Callback;
import famm.fammous.connection.JResponseTokken;
import famm.fammous.start.StartScreen;
import famm.fammous.util.DebugAct;

public class SendVideo extends Activity{
	//setOrientationHint
	
	private Button buttonSend, buttonDelete;
	private VideoView videoView;
	private ImageView imageViewThumbnail, imageViewPlay;
	private EditText editTextTitle, editTextComment;
	private FrameLayout frameLayoutVideo;
	private Uri uri;
	private static String videoRoute;
	private ApiConnection api;
	private UserBBDD userBBDD;
	private OperacionesBBDD opBBDD;
	private VideoBBDD videoBBDD;
	private ArrayList args, list, video;
	private ArrayList listResponse;
	private Context context;
	private String email, tokken;
	private JResponseTokken jResponseTokken;
	private ComunicationService cService;
	private String title;
	private String comment;
	private String TAG = "SendVideo";
	private boolean playPressed;
	private DebugAct debug;


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_send);
		
		buttonSend = (Button) findViewById(R.id.buttonSendVideo);
		buttonDelete = (Button) findViewById(R.id.buttonDeleteVideo);
		imageViewThumbnail	= (ImageView) findViewById (R.id.imageViewThumbnailSendVideo);
		editTextTitle = (EditText) findViewById (R.id.editTextTitleSendVideo);
		editTextComment = (EditText) findViewById (R.id.editTextCommentSendVideo);
		imageViewPlay = (ImageView) findViewById (R.id.imageViewPlaySendVideo);
		videoView = (VideoView) findViewById (R.id.videoViewSend);
		frameLayoutVideo = (FrameLayout) findViewById (R.id.frameLayoutSendVideo);

		debug = new DebugAct();
		jResponseTokken = new JResponseTokken();
		api = new ApiConnection();

		opBBDD = new OperacionesBBDD();
		opBBDD.init(this);
		userBBDD = new UserBBDD();
		videoBBDD = new VideoBBDD();

		list = userBBDD.get();
		if(list.size() == 2) {
			email = (String) list.get(0);
			tokken = (String) list.get(1);
		}
		
		Bundle extras = getIntent().getExtras();
		
	    try{
	    	if(debug.isDebug() == false && extras != null){
		    	videoRoute = extras.getString("videoRoute");
	    	}
	    	else{
	    		videoRoute = Environment.getExternalStorageDirectory().getPath() + "/Fammous/video_fammous_06062016_183433.mp4";
				editTextTitle.setText("titulo");
				editTextComment.setText("comentario");
	    	}

			if(videoBBDD.isEmpty() == false){
				video = new ArrayList();
				video = videoBBDD.get();
				if(video.size() == 3) {
					videoRoute = video.get(0).toString();
					editTextTitle.setText(video.get(1).toString());
					editTextComment.setText(video.get(2).toString());
				}
				videoBBDD.delete();
			}
	    	//
	    	uri = Uri.parse(videoRoute);	    	
	    	//No se ha podido utilizar el codificador h264
		   	//if(extras.getString("encoder").equals("failed")){		   
		   	//}
		   	
		   	videoView.setVideoURI(uri);		   	
	        videoView.requestFocus();
	        videoView.seekTo(50);
	    }
		catch(Exception e){
			e.printStackTrace();
		}
		
		Bitmap bmThumbnail = null;
		bmThumbnail = ThumbnailUtils.createVideoThumbnail(uri.toString(), Thumbnails.MINI_KIND);
		
		if(bmThumbnail != null){
			imageViewThumbnail.setImageBitmap(bmThumbnail);
		}

		context = this;
		
		frameLayoutVideo.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(playPressed == false){	
					playPressed = true;
					imageViewThumbnail.setVisibility(View.INVISIBLE);
					imageViewPlay.setVisibility(View.INVISIBLE);
					//videoView.setVisibility(View.VISIBLE);
					//Comienza el hilo del tiempo
					videoView.start();						
				}
				else{			
					playPressed = false;
					imageViewThumbnail.setVisibility(View.VISIBLE);
					imageViewPlay.setVisibility(View.VISIBLE);
					videoView.pause();									
				}						
			}			
		});
		
		//Si se ha terminado el video
        videoView.setOnCompletionListener(new OnCompletionListener(){
			@Override
			public void onCompletion(MediaPlayer mp) {
				imageViewPlay.setVisibility(View.VISIBLE);
				playPressed=false;				
			}	    	
	    });
		
		buttonSend.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(checkEditTextEmpty() == true){
					args = new ArrayList<String>();
					args.add(tokken);
					api.connectionGet(context, "checkTokken", args, new Callback<Integer>(){
						@Override
						public void onResponse(Integer t) {
							listResponse = api.getResult();
							jResponseTokken = (JResponseTokken) api.getResult().get(0);

							String responseCode = jResponseTokken.getResponseCode();
							//Fallo a propósito del tokken
							/*Bundle extras = getIntent().getExtras();
							if(extras != null){
								String commString = extras.getString("start");
								if(commString != null){
									if(extras.getString("start").equals("start")){
										responseCode = "100";
									}
									else{
										responseCode = "400004";
									}
								}
							}*/

							if(listResponse.size() == 2 && responseCode.equals("100")){
								if((Boolean)listResponse.get(1) == true){
									showToastSend();
									args.clear();
									args.add(tokken);
									ArrayList<String> listParams = new ArrayList<String> ();
									listParams.clear();
									listParams.add("file");
									listParams.add(videoRoute);
									listParams.add("title");
									listParams.add(editTextTitle.getText().toString().trim());
									listParams.add("message");
									listParams.add(editTextComment.getText().toString().trim());

									cService = new ComunicationService();
									ComunicationService.setContext(context);
									ComunicationService.setFunction("recordVideo");
									ComunicationService.setParams(listParams);
									ComunicationService.setArgs(args);
									startService(new Intent(getBaseContext(), ServiceSend.class));
									finish();
								}
								else{
									showToastFailSend();
								}
							}
							else{
								//El tokken no es válido
								showToastFailSend();
								sendToStart();
							}
						}
					});
				}
			}			
		});
		
		buttonDelete.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {		
				File file = new File(videoRoute);
				file.delete();
				Intent i = new Intent(SendVideo.this, CameraRecorder.class);
				startActivity(i);
				finish();
			}			
		});
		
		editTextTitle.setOnKeyListener(new OnKeyListener() {
	        public boolean onKey(View v, int keyCode, KeyEvent event) {
	            // If the event is a key-down event on the "enter" button
	            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
	                return true;
	            }
	            return false;
	        }
	    });
		
		editTextComment.setOnKeyListener(new OnKeyListener() {
	        public boolean onKey(View v, int keyCode, KeyEvent event) {
	            // If the event is a key-down event on the "enter" button
	            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
	                return true;
	            }
	            return false;
	        }
	    });
	}

	private void sendToStart() {
		Intent i = new Intent(this, StartScreen.class);
		i.putExtra("failSend", true);
		startActivity(i);
	}

	protected boolean checkEditTextEmpty() {
		
		if(editTextTitle.getText().toString().trim().length() == 0){
			editTextTitle.setError(getResources().getString(R.string.editTextErrorVideo));
			return false;
		}
		return true;		
	}
	
	public void showToastSend() {
		Toast.makeText(context, getResources().getString(R.string.notificationTitleVideo), Toast.LENGTH_LONG).show();
	}
	
	public void showToastFailSend() {
		Toast.makeText(context, getResources().getString(R.string.notificationTitleErrorVideo), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStart() {
	    super.onStart();   
	    videoView.start();	
	    videoView.pause();	    
	}
	
	@Override
	public void onStop(){
		super.onStop();
		if(videoView != null){
			videoView.stopPlayback();
		}
	}
}
