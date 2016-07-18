package famm.fammous.util;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import famm.fammous.R;

public class ConnectionDetector {

	Context context;
	AlertDialog alertDialog;
	
	public ConnectionDetector(Context context){
		this.context = context;
	}
	
	public boolean isNetworkConnected() {
		
		  ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		  NetworkInfo ni = cm.getActiveNetworkInfo();
		  
		  if (ni == null) {
			  noConnection();        //No hay conexion a internet
			  return false;
		  }
		  else
			  return true;
	}

	public void noConnection(){

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(context.getResources().getString(R.string.titleDialogConnection))
				.setMessage(context.getResources().getString(R.string.messageDialogConnection))
				/*.setNegativeButton(context.getResources().getString(R.string.buttonCancelDialogConnection), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if(alertDialog != null) {
							alertDialog.cancel();
						}
					}
				})*/
				.setCancelable(false)
				.setPositiveButton(context.getResources().getString(R.string.buttonRetryDialogConnection), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//isNetworkConnected();
						((Activity) context).finish();
						Intent intent = new Intent();
						intent.setClass(context, context.getClass());
						context.startActivity(intent);
					}
				});
		alertDialog = builder.create();
		builder.show();
	}
}
