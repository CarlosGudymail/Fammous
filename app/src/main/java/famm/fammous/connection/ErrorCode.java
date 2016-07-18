package famm.fammous.connection;

import android.content.Context;
import android.widget.Toast;

import famm.fammous.R;

public class ErrorCode {
	
	Context context;
	
	public ErrorCode(Context context){
		this.context = context;
	}
	
	public void action(int code){
		switch(code){
		//Fallo en la forma o cantidad de argumentos
		case 400001:						
			break;
		//No hay m�s elementos en la base de datos
		case 400002:					
			break;
		//Nombre de usuario/email o contrase�a incorrectas
		case 400003:
			wrongData();
			break;
		//El token ha sido invalidado
		case 400004:					
			break;
		}	
	}
	
	public void wrongData(){
		/*if(context.getClass().getName().equals("famm.fammous.start.SplashScreen")){
			Intent i = new Intent(context, StartScreen.class);
			context.startActivity(i);
			((Activity) context).finish();
		}
		else{
			Toast.makeText(context, context.getResources().getString(R.string.toastStartSignup), Toast.LENGTH_LONG).show();
		}	*/	
		
		Toast.makeText(context, context.getResources().getString(R.string.toastStartSignup), Toast.LENGTH_LONG).show();
	}
}
