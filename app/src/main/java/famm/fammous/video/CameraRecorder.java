package famm.fammous.video;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import famm.fammous.R;
import famm.fammous.util.UserProfile;



@SuppressWarnings("deprecation")
public class CameraRecorder extends Activity implements Callback{

	private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    public MediaRecorder mediaRecorder = new MediaRecorder();   
    private MediaRecorder mediaRecorder2 = new MediaRecorder();
    private Camera cameraPreview;
    private Button buttonRec;
    private UserProfile userProfile;
    private int timeVideo;
    private TextView textViewCountDown;
    private int pulsado = 0;
    private Drawable drawableRec;
    private Drawable drawableStop;
    private Intent intent;
    private Thread thread;
    private boolean seconds10 = false;
    private boolean flag = false;
    private int timeRecord;
    private int TIMEVIDEO;
    private Button buttonSwitch;
    private ImageView imageViewFlash;
    private ImageView imageViewConfiguration;
    private ImageView imageViewCloseDimensions;
    private int numberCameras;
    private CameraInfo ci;
    private boolean recording = false;
    private int presentCamera; //Camara que est� utilizando el dispositivo. 1 -> delantera | 0-> trasera
    private Animation animationCount;
    private Parameters params;   
    private final static String TAG = "CameraRecorder";
    private int resultOrientation;
    private Animation animationRotate;
    private Toast toastRotate;
	private int rotation;
	private static ImageView imageViewRotate;
	private static boolean flagAnimation; //Flag que indica si se ha mostrado la animaci�n de la rotaci�n, para que s�lo se muestre una vez
	private Display display;
	private Camera.Size cameraSizes;
	private Sensor sensor = null;
	private boolean hasSensor = false;
	private SensorManager sensorManager;
	private List<android.hardware.Sensor> sensorList;
	private SensorEventListener sensorEventListener;
	private int contador;
	private int time;
	private Runnable r;
	private float azimuth;
	private float pitch;
	private float roll;
	private boolean flagCountdown = false;
	private static Context context;
	private FrameLayout frameLayoutConfiguration;
	private boolean flagConfiguration = false;
	private List<Size> Sizes;
	private ArrayList <Integer> listConfigurationSizes;
	private RadioButton radioLow, radioMedium, radioHigh;
	private RadioGroup radioGroupQuality;
	private ArrayList listQualityWidth;
	private ArrayList listQualityHeight;
	private int finalHeightQuality;
	private int finalWidthQuality;
	private Parameters paramsQuality;
	private int radioPressed = 1;
	private boolean flagMediaRecorder = true; //Para saber si utiliza mediarecorder o mediarecorder2
	private String videoRoute;
	private int rotateDevice;
	private static final int REQUEST_VIDEO_CAPTURE = 1;
	private VideoView videoView;
	private AlertDialog alertDialog;
	private File f;
	private File f2;
	    
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	     super.onCreate(savedInstanceState);
	     setContentView(R.layout.camera_recorder);
		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				 WindowManager.LayoutParams.FLAG_FULLSCREEN);
	     
	     surfaceView = (SurfaceView) findViewById(R.id.cameraPreview);
	     buttonRec = (Button) findViewById(R.id.buttonRecVideo);   
	     buttonSwitch = (Button) findViewById(R.id.buttonSwitchCamera);
	     textViewCountDown = (TextView) findViewById(R.id.textViewCountDown);


		 cameraPreview = getCameraInstance();
	     surfaceHolder = surfaceView.getHolder();
	     surfaceHolder.addCallback(this);
	     
	     context = this;
	     rotateDevice = 0;
		 contador = 0;
		 time=3;
	     listConfigurationSizes = new ArrayList();
	     listQualityHeight = new ArrayList();
	     listQualityWidth = new ArrayList();
	     
	     sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	     sensorList = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
	     
	     buttonRec.setOnClickListener(new Button.OnClickListener(){
				@Override
				public void onClick(View v) {
						switch(pulsado)
						{					
							case 0:
								buttonRec.setEnabled(false);
								buttonRec.setClickable(false);
								buttonRec.setText(getResources().getString(R.string.buttonStopCamera));
								flagCountdown = true;														
								//frameLayoutConfiguration.setVisibility(View.INVISIBLE);
								hideExtensions();
								//thread();
								//handler.setTexto(""+timeVideo);
								//handler.act();

								final Handler handler = new Handler();
								r = new Runnable() {
									public void run() {
										textViewCountDown.setText("" +time);
										Log.d(TAG, "tiempo: "+time);
										time--;
									}
								};

								//Contador 3,2,1
								new CountDownTimer(3500, 1100) {
									public void onTick(long millisUntilFinished) {
										//textViewCountDown.setText(Long.toString(millisUntilFinished/900));
										//textViewCountDown.
										contador++;
										//Log.d(TAG, "tiempo: "+millisUntilFinished +" y segundos: " +millisUntilFinished/900 +" contador=" +contador);
										handler.postDelayed(r, 0);
										if(contador == 3){
											flagCountdown = false;
											try {
												Log.d(TAG, "Comienza la grabación");

												//thread.start();
												startRecording();
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}

										//textViewCountDown.startAnimation(animationCount);
									}
									public void onFinish() {
										textViewCountDown.setText("");
										buttonRec.setEnabled(true);
										buttonRec.setClickable(true);
										pulsado = 1;
									}
								}.start();	
								
								break;
								
							case 1:		
								buttonRec.setEnabled(false);
								buttonRec.setClickable(false);
								pulsado = 2;	
								stopRecording();
								break;				
						}
				}									
			});	
	     
	     buttonSwitch.setOnClickListener(new Button.OnClickListener(){
				@Override
				public void onClick(View v) {	
					releaseCamera();
					
					if(presentCamera == 1){					
						cameraPreview = Camera.open(0);
						if(cameraPreview != null){
							/*try {
								cameraPreview.setPreviewDisplay(surfaceHolder);
							} catch (IOException e) {								
								e.printStackTrace();
							}        
							//checkFlash();
					        //cameraPreview.startPreview();*/
					        presentCamera = 0;
						}
					}
					else{
						cameraPreview = Camera.open(1);
						if(cameraPreview!=null){						
							/*try {
								cameraPreview.setPreviewDisplay(surfaceHolder);
							} catch (IOException e) {								
								e.printStackTrace();
							}        
							//checkFlash();
					        //cameraPreview.startPreview();*/
					        presentCamera = 1;
						}					
					}					
					//checkFlash();	
					
					surfaceChanged(surfaceHolder,0,0,0);
					
					
					
				}	
	        });
	     
	     //sensorManager.registerListener(sensorEventListener,sensor, 0, null);	
	 }
	    
	 @Override
	 public void onResume(){
		 contador = 0;
		 super.onResume();	    	
	 }
	   
	 @Override
	 public void onPause() {
	     super.onPause();
	 }
	 
	 @Override
	 public void onStop() {
	     super.onStop();
	 }
	 
	 public Camera getCameraInstance(){    
	    	Camera c = null;
	    	try {
	    		int index = getFrontCameraId();
	    		if (index != -1){
	    			c = Camera.open(index);    			   			
	    		}
	    		else{
	    			c = Camera.open(index);
	    		}
	    	}
	    	catch (Exception e){
	    		Log.d("VideoCoder", "Error setting camera preview: " + e.getMessage());
	    	// Camera is not available (in use or does not exist)
	    	}
	    	return c; // returns null if camera is unavailable
	  }
	 
	 public int getFrontCameraId() {
	        ci = new CameraInfo();
	        numberCameras = Camera.getNumberOfCameras();
	        for (int i = 0 ; i < numberCameras; i++) {
	            Camera.getCameraInfo(i, ci);
	            //C�mara frontal
	            if (ci.facing == CameraInfo.CAMERA_FACING_FRONT)
	            {      
	            	presentCamera = 1;
	            	return i;                
	            }
	        }
	        //C�mara trasera
	        presentCamera = 0;
	        return CameraInfo.CAMERA_FACING_BACK; 
	 }
	        
	 @Override
	 public void onConfigurationChanged(Configuration newConfig) {
		 super.onConfigurationChanged(newConfig); 	
		 //setContentView(R.layout.camera_recorder);
		 rotateDevice = 1;
		 Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		 final int orientation = display.getRotation(); 

		/* switch(orientation) {
			case Configuration.ORIENTATION_PORTRAIT:
				setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				break;
			case Configuration.ORIENTATION_LANDSCAPE:
				setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				break;                   
		 }*/
	 }	  
	 
	  @Override
	  public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) { 
		  cameraPreview.setDisplayOrientation(setCameraDisplayOrientation());		  
		  cameraPreview.setParameters(params);
		  try {
	 			cameraPreview.setPreviewDisplay(holder);
	 			cameraPreview.startPreview();
	 		} catch (Exception e) {			
	 			e.printStackTrace();
	 			try {
	 				cameraPreview.setPreviewDisplay(holder);
	 				cameraPreview.startPreview();
	 			} catch (Exception ex) {
	 				ex.printStackTrace();
	 			}
	 		}  
	  }
	  
	 
	  @Override
	  public void surfaceCreated(SurfaceHolder holder) {      	 

		if (cameraPreview != null && rotateDevice == 0) {
         params = cameraPreview.getParameters();            
         Sizes = params.getSupportedPreviewSizes(); // This method will always return a list with at least one element
         
         //Si solo tiene dos tamaños
         if(Sizes.size() == 2){
         	//Los tamaños obtenidos van aumentando
	            if(Sizes.get(0).width < Sizes.get(1).width || Sizes.get(0).height < Sizes.get(1).height){ 
	            	listConfigurationSizes.add(Sizes.get(0).height);	//Baja
	        		listConfigurationSizes.add(Sizes.get(0).width);
	        		listConfigurationSizes.add(Sizes.get(0).height);	//Media
	        		listConfigurationSizes.add(Sizes.get(0).width);
	        		listConfigurationSizes.add(Sizes.get(1).height);	//Alta
	        		listConfigurationSizes.add(Sizes.get(1).width);
	            }
	            else{
	            	listConfigurationSizes.add(Sizes.get(1).height);	//Baja
	        		listConfigurationSizes.add(Sizes.get(1).width);
	        		listConfigurationSizes.add(Sizes.get(1).height);	//Media
	        		listConfigurationSizes.add(Sizes.get(1).width);
	        		listConfigurationSizes.add(Sizes.get(0).height);	//Alta
	        		listConfigurationSizes.add(Sizes.get(0).width);
	            }
         }
         else if(Sizes.size() > 2){
	            //Los tamaños obtenidos van aumentando
	            if(Sizes.get(0).width <= Sizes.get(1).width && Sizes.get(0).width <= Sizes.get(2).width ){
	            	for(int i=0; i<Sizes.size(); i++){
	            		//Rellena el array con las dimensiones disponibles disponibles entre 480 y 640 de alto. 
	                	//Como el dispositivo esta en landscape se tiene que tomar el ancho en vez del alto.
	                	if(Sizes.get(i).width <=640){
	                		listConfigurationSizes.add(Sizes.get(i).height);
	                		listConfigurationSizes.add(Sizes.get(i).width);
	                	}
	            	}
	            }	            
	            else{
	            	for(int i=Sizes.size()-1; i>=0; i--){                	
	                	System.out.println("Size: "+Sizes.get(i).height +Sizes.get(i).width);
	                	//Rellena el array con las dimensiones disponibles disponibles entre 480 y 640 de alto. 
	                	//Como el dispositivo esta en landscape se tiene que tomar el ancho en vez del alto.
	                	if(Sizes.get(i).width <=640){
	                		listConfigurationSizes.add(Sizes.get(i).height);
	                		listConfigurationSizes.add(Sizes.get(i).width);
	                	}
	                }
	            }
         }
         
         //Solo tiene un tama�o
         else if(Sizes.size() == 1){
         	listConfigurationSizes.add(Sizes.get(0).height);	//Baja
     		listConfigurationSizes.add(Sizes.get(0).width);
     		listConfigurationSizes.add(Sizes.get(0).height);	//Media
     		listConfigurationSizes.add(Sizes.get(0).width);
     		listConfigurationSizes.add(Sizes.get(0).height);	//Alta
     		listConfigurationSizes.add(Sizes.get(0).width);
         }
         
         //Obtiene la calidad baja media y alta
         listQualityWidth.add(listConfigurationSizes.get(0));
         listQualityHeight.add(listConfigurationSizes.get(1));
         
         int mediumQuality = listConfigurationSizes.size()/2;
         if (mediumQuality%2 != 0){
         	mediumQuality--;
         }
         listQualityWidth.add(listConfigurationSizes.get(mediumQuality));
         listQualityHeight.add(listConfigurationSizes.get(mediumQuality + 1));
         
         listQualityWidth.add(listConfigurationSizes.get(listConfigurationSizes.size()-2));
         listQualityHeight.add(listConfigurationSizes.get(listConfigurationSizes.size()-1));
         
         finalHeightQuality = (Integer) listQualityHeight.get(1);
         finalWidthQuality = (Integer) listQualityWidth.get(1);
         
         cameraPreview.setDisplayOrientation(setCameraDisplayOrientation());
         //cameraPreview.setDisplayOrientation(90);
         params.setPreviewSize(finalHeightQuality, finalWidthQuality);
         params.setFlashMode(Parameters.FLASH_MODE_OFF);
         try{
         	cameraPreview.setParameters(params);
             Log.i("Surface", "Created");
     	}
     	catch(Exception e){
     		Log.d(TAG, e.toString());
     		Toast.makeText(getApplicationContext(), "Failed to start the camera", Toast.LENGTH_SHORT).show();
     	}  	  
     }
     else if(cameraPreview == null){
     	try{
     		//Toast.makeText(getApplicationContext(), getResources().getString(R.string.toastCameraVideo), Toast.LENGTH_LONG).show();
     	}
     	catch(Exception e){
     		Log.d(TAG, e.toString());
     	}
     }  
     
	}   

	 @Override
	 public void surfaceDestroyed(SurfaceHolder holder) {
	 	if(cameraPreview != null)
	 		cameraPreview.release();  
	 }
	 
	 protected void startRecording() throws IOException
	    {

	        if(cameraPreview==null){
				cameraPreview.stopPreview();
	            cameraPreview = getCameraInstance();
	        }
	           
	        recording = true;
	                
	        mediaRecorder = new MediaRecorder();
	        
	        /*if(getFlashModeSetting() == "torch"){
	        	
	        	if(cameraPreview != null){
	        		cameraPreview.lock();
	        		params.setFlashMode(params.FLASH_MODE_OFF);      	        
	    	        cameraPreview.setParameters(params);    	        
	            	params.setFlashMode(params.FLASH_MODE_TORCH);  
	            	cameraPreview.setParameters(params);
	            	cameraPreview.unlock();
	            }       	  
	        }   */  
	                     
	        cameraPreview.lock();
	        cameraPreview.unlock();
	        
	        mediaRecorder.setCamera(cameraPreview);          
	        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264); 
	        //mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);  
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC); //Codec ACC-LC: MediaRecorder.AudioEncoder.ACC	
			
			mediaRecorder.setVideoSize(finalHeightQuality, finalWidthQuality);	
			//mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
			//mediaRecorder.setVideoSize(320, 240);
			
			System.out.println("Sizes Record" +finalHeightQuality +"x" +finalWidthQuality);
			
			//Nombre del archivo
			DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy_HHmmss");
			Date date = new Date();			
			videoRoute = Environment.getExternalStorageDirectory().getPath() + "/Fammous/video_fammous_" +dateFormat.format(date).toString() +".mp4";
			f = new File(videoRoute);
			
	        if (f.exists())
	        {
	        	Log.d("ARCHIVO: ", "Eliminado");
	        	f.delete();
	        }	               
	        
	        mediaRecorder.setOutputFile(videoRoute);           
	        mediaRecorder.setMaxDuration(timeRecord); // seconds
	        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());   
	        mediaRecorder.setOrientationHint(getAngleRecord());
	        //mediaRecorder.setVideoFrameRate(20);         
	        mediaRecorder.prepare();         
	        
	        try{
	        	mediaRecorder.start();  
	        }
	        catch(Exception e){
	        	Log.e(TAG, "Error al comenzar a grabar con codificacion h264 "+e.toString());
	        	mediaRecorder.release(); 
	            flagMediaRecorder = false;
	            
	        	mediaRecorder2 = new MediaRecorder();
	        	mediaRecorder2.setCamera(cameraPreview);                
	            mediaRecorder2.setAudioSource(MediaRecorder.AudioSource.MIC);
	            mediaRecorder2.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	            mediaRecorder2.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
	    		mediaRecorder2.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT); 
	            //mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);  
	    		mediaRecorder2.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); //Codec ACC-LC: MediaRecorder.AudioEncoder.ACC	
	    		
	    		mediaRecorder2.setVideoSize(finalHeightQuality, finalWidthQuality);	
	    		//mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
	    		//mediaRecorder.setVideoSize(320, 240);
	    		
	    		System.out.println("Sizes Record" +finalHeightQuality +"x" +finalWidthQuality);
	    		
	    		//Nombre del archivo

	        	f2 = new File(videoRoute);
	        	        
	            if (f2.exists())
	            {
	            	Log.d("ARCHIVO: ", "Eliminado");
	            	f2.delete();
	            }	     
	            
	        	mediaRecorder2.setOutputFile(videoRoute);         
	            mediaRecorder2.setMaxDuration(timeRecord); // seconds
	            mediaRecorder2.setPreviewDisplay(surfaceHolder.getSurface());   
	            mediaRecorder2.setOrientationHint(getAngleRecord());
	            //mediaRecorder.setVideoFrameRate(20);   
	            
	            mediaRecorder2.prepare();         
	            try{
	            	mediaRecorder2.start();  
	            }catch(Exception ex){
	            	Log.e(TAG, "Error al comenzar a grabar con codificacion Default"+ex.toString());
	            }           
	        }        
	        System.out.println("Calidad de la grabación "+finalHeightQuality +"x" +finalWidthQuality);
	    }

	 
	    protected void stopRecording() {    	
	    	
	    	flag = true; //Detiene el hilo
	    	 
	        if(mediaRecorder!=null && flagMediaRecorder == true)
	        {
	        	try{
	            	mediaRecorder.stop();
	            	mediaRecorder.release();
	                cameraPreview.lock();
	                cameraPreview.release(); 
	            }
	            catch(IllegalStateException e){
	            	Log.e(TAG, "Error al parar el vídeo" +e.getMessage());
	            }       	        	
	            intent = new Intent(CameraRecorder.this, SendVideo.class);
	            intent.putExtra("encoder", "correct");            
	            intent.putExtra("videoRoute", videoRoute);
	    		startActivity(intent);		
	    		finish();
	        }
	        if(mediaRecorder2 != null && flagMediaRecorder == false)
	        {
	        	try{
	            	mediaRecorder2.stop();
	            	mediaRecorder2.release();
	                cameraPreview.lock();
	                cameraPreview.release(); 
	            }
	            catch(IllegalStateException e){
	            	Log.e(TAG, "Error al parar el vídeo" +e.getMessage());
	            }
	        	//userProfile.setTimeVideo(TIMEVIDEO);
	            intent = new Intent(CameraRecorder.this, SendVideo.class);
	            intent.putExtra("encoder", "failed");
	    		startActivity(intent);		
	    		finish();
	        }
	        
	    }

	    private void releaseMediaRecorder() {

	        if (mediaRecorder != null) {
	        	
	            mediaRecorder.reset(); // clear recorder configuration
	            mediaRecorder.release(); // release the recorder object
	        }
	    }
	 
	 private void releaseCamera() {
	    if (cameraPreview != null) {
	        cameraPreview.lock();
	        cameraPreview.release(); // release the camera for other applications       
	    }
	 }
	 
	 public int setCameraDisplayOrientation() {
	        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
	        android.hardware.Camera.getCameraInfo(presentCamera, info);
	        int rotation = ((Activity)context).getWindowManager().getDefaultDisplay().getRotation();
	        int degrees = 0;
	        switch (rotation) {
	            case Surface.ROTATION_0: degrees = 0; break;
	            case Surface.ROTATION_90: degrees = 90; break;
	            case Surface.ROTATION_180: degrees = 180; break;
	            case Surface.ROTATION_270: degrees = 270; break;
	        }

	        int result;
	        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	            result = (info.orientation + degrees) % 360;
	            result = (360 - result) % 360;  // compensate the mirror
	        }
	        else {  // back-facing
	            result = (info.orientation - degrees + 360) % 360;
	        }
	        return result;	       
	    }

	 public int getAngleRecord(){
		 int angle = 0;
		 int display_mode = getResources().getConfiguration().orientation;
		 if(presentCamera == 1 && display_mode == Configuration.ORIENTATION_PORTRAIT){
			 angle = 270;
		 }
		 if(presentCamera == 0 && display_mode == Configuration.ORIENTATION_PORTRAIT){
			 angle = 90;
		 }
		 return angle;
	 }

	@Override
	public void onBackPressed() {

		if(recording == true) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);

			builder.setTitle(context.getResources().getString(R.string.titleDialogConnection))
					.setMessage(context.getResources().getString(R.string.messageDialogBack))
					.setNegativeButton(context.getResources().getString(R.string.buttonCancelDialogBack), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							if (alertDialog != null) {
								alertDialog.cancel();
							}
						}
					})
					.setPositiveButton(context.getResources().getString(R.string.buttonAcceptDialogBack), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							//isNetworkConnected();

							//Borra el vídeo
							if (f != null || f2 != null) {
								if (f.exists()) {
									Log.d("Video: ", "Eliminado");
									f.delete();
								} else if (f2.exists()) {
									Log.d("Video: ", "Eliminado");
									f.delete();
								}
							}

							((Activity) context).finish();
						}
					});
			alertDialog = builder.create();
			builder.show();
		}
	}
	 
	 private void hideExtensions() {
		buttonSwitch.setVisibility(View.INVISIBLE);
	}
}
