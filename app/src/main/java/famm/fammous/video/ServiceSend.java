package famm.fammous.video;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

import famm.fammous.MainActivity;
import famm.fammous.R;
import famm.fammous.bbdd.VideoBBDD;
import famm.fammous.connection.ApiConnection;
import famm.fammous.connection.Callback;
import famm.fammous.start.SplashScreen;
import famm.fammous.start.StartScreen;


public class ServiceSend extends Service{
	
	private String vacio = " ";
	private String TAG ="ServiceSend";
	private ComunicationService cService;
	private String idVideo;
	private VideoBBDD videoBBDD;
	private int lastAttachment;
	private static final int NOTIF_ALERTA_ID = 1;
	private NotificationCompat.Builder builder;
	private NotificationManager notificationManager;
	private boolean flagError = false;
	private ApiConnection api;
	private ArrayList listResponse;

	
	@Override
	public IBinder onBind(Intent intent) {
		return null;	
	}
	
	
	
	//Comienza la ejecución del servicio
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       
        cService = new ComunicationService();   
        api = new ApiConnection();
        lastAttachment = 0;      
        listResponse = new ArrayList();
		
        //Notificacion
        builder =
				new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_notification40)
				.setLargeIcon((((BitmapDrawable)getResources()
				.getDrawable(R.drawable.ic_notification40)).getBitmap()))
				.setAutoCancel(true)				
				.setContentTitle(getResources().getString(R.string.notificationTitleVideo));



		builder.setProgress(0, 0, true);
		
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);    	 
    	notificationManager.notify(NOTIF_ALERTA_ID, builder.build());



    	
    	api.connectionPost(ComunicationService.getContext(), ComunicationService.getFunction(), ComunicationService.getArgs(), ComunicationService.getParams(), new Callback<Integer>(){
			@Override
			public void onResponse(Integer t) {		
				listResponse = api.getResult();
				if(listResponse.size() == 2){
					if((Boolean)listResponse.get(1) == true){
						flagError = false;
					}
					else{
						flagError = true;
					}				
				}
				else{
					flagError = true;
				}
				onDestroy();
			}
			
		});
        return START_NOT_STICKY;
    }
 
    @Override
    public void onDestroy() {
    	super.onDestroy(); 
    	Log.d(TAG, "Servicio destruido");
        //Avisa que ha enviado el v�deo correctamente
    	if(flagError == false){
	    	try{
	    		builder.setProgress(0, 0, false);
	        	builder.setContentTitle(getResources().getString(R.string.notificationTitleFinalVideo));

				Intent notifyIntent = new Intent(this, SplashScreen.class);

				notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
						Intent.FLAG_ACTIVITY_CLEAR_TASK);

				PendingIntent pendingIntent =
						PendingIntent.getActivity(
								this,
								0,
								notifyIntent,
								PendingIntent.FLAG_UPDATE_CURRENT
						);

				builder.setContentIntent(pendingIntent);
	        	notificationManager.notify(NOTIF_ALERTA_ID, builder.build());

	    	}catch(Exception e){
	    		Log.e(TAG, "Error al modificar el texto del service (Envío OK)"+e.toString());
	    	}   
    	}
    	else{
    		try{
        		builder.setProgress(0, 0, false);
            	builder.setContentTitle(getResources().getString(R.string.notificationTitleErrorVideo));

				Intent notifyIntent = new Intent(this, SendVideo.class);

				notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
						Intent.FLAG_ACTIVITY_CLEAR_TASK);

				PendingIntent pendingIntent =
						PendingIntent.getActivity(
								this,
								0,
								notifyIntent,
								PendingIntent.FLAG_UPDATE_CURRENT
						);

				videoBBDD = new VideoBBDD();
				//Inserta la ruta del vídeo, el título y el comentario
				videoBBDD.insertVideo(ComunicationService.getParams().get(1).toString(), ComunicationService.getParams().get(3).toString(), ComunicationService.getParams().get(5).toString());

				builder.setContentIntent(pendingIntent);
				notificationManager.notify(NOTIF_ALERTA_ID, builder.build());
				//notificationManager.cancel(NOTIF_ALERTA_ID);
        	}catch(Exception e){
        		Log.e(TAG, "Error al modificar el texto del service (Error al enviar)"+e.toString());
        	}
    	}
    }       
}
