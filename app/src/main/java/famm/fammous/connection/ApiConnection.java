package famm.fammous.connection;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import famm.fammous.R;
import famm.fammous.util.UserProfile;


public class ApiConnection{
	
	private static AsyncHttpClient client;
	private static final String URL = "https://famm.fammous.com/api/gate/";
	private Context context = null;
	private static int codeClass;
	private UserProfile userProfile = new UserProfile();
	private ArrayList listResponse = new ArrayList();
	private ErrorCode errorCode;
	private String TAG = "ApiConnection";
	/*context: se utiliza para mostrar los toast
	 *function: es la funcion de la api que se va a llamar
	 *	1- getTokken
	 *args: lista de los argumentos
	 */
	
	public ArrayList connectionGet(Context context, final String funct, final List<String> args, final Callback<Integer> callback){
		
		this.context = context;
		String arguments = "";
		for(int i=0; i<args.size(); i++){
			arguments = arguments + args.get(i) + "/";
		}
		/*Builder builder = new AsyncHttpClientConfig.Builder();
	    builder.setCompressionEnabled(true)
	        .setAllowPoolingConnection(true)
	        .setRequestTimesout(30000)
	        .build();
	    AsyncHttpClient client = new AsyncHttpClient(builder.build());*/	
		
		client = new AsyncHttpClient();
		client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
		client.get(URL + funct + "/" + arguments, new AsyncHttpResponseHandler() {
				
			@Override
			public void onStart() {
				// called before request is started	
				Log.d("APIconnection", "onStart");	
			}
	
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"			
				listResponse = getFunction(response);
				if (callback != null) {
				    callback.onResponse(statusCode);
				}
			}
	
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				Log.d("APIconnection", "onFailure");		    	
				listResponse = getFunction(errorResponse);	
				if (callback != null) {
				    callback.onResponse(statusCode);
				}
			}
	
			@Override
			public void onRetry(int retryNo) {
				// called when request is retried
				Log.d("StartScreen", "onRetry");
			}			
		});
		
		return listResponse;
	}	
	
	
	public ArrayList connectionPost(Context context, String funct, final List<String> args, ArrayList listParams,  final Callback<Integer> callback){
		
		this.context = context;
		String arguments = "";
		if(args != null) {
			for (int i = 0; i < args.size(); i++) {
				arguments = arguments + args.get(i) + "/";
			}
		}
		RequestParams params = new RequestParams();
		try {
		    
			/*params.put("file", new File(urlFile));
		    params.put("title", "titleUser");
		    params.put("message", "messageUser");*/
		    for(int i=0; i<listParams.size(); i+=2){
		    	if(listParams.get(i) == "file"){
		    		params.put((String) listParams.get(i), new File((String) listParams.get(i+1)));
		    	}
		    	else{
			    	params.put((String) listParams.get(i), listParams.get(i+1));
		    	}

		    }
		    
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		}
		/*Builder builder = new AsyncHttpClientConfig.Builder();
	    builder.setCompressionEnabled(true)
	        .setAllowPoolingConnection(true)
	        .setRequestTimesout(30000)
	        .build();
	    AsyncHttpClient client = new AsyncHttpClient(builder.build());*/	
		
		client = new AsyncHttpClient();
		client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
		client.post(URL + funct + "/" + arguments, params, new AsyncHttpResponseHandler() {
				
			@Override
			public void onStart() {
				// called before request is started	
				Log.d("APIconnection", "onStart");	
			}
	
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"		
				if(response.length != 0 && statusCode == 200){
					listResponse = getFunction(response);					
				}
				else{
					listResponse.add(true);
					listResponse.add(true);					
				}
				if (callback != null) {
				    callback.onResponse(statusCode);
				}
			}
	
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				Log.d("APIconnection", "onFailure");
				if(errorResponse.length != 0){
					try{
						listResponse = getFunction(errorResponse);						
					}
					catch(Exception e2){
						Log.d(TAG, "Fallo de intento de envío, no se ha podido procesar la respuesta del servidor: ");
						e2.printStackTrace();
					}
				}
				if (callback != null) {
				    callback.onResponse(statusCode);
				}
			}
	
			@Override
			public void onRetry(int retryNo) {
				// called when request is retried
				Log.d("StartScreen", "onRetry");
			}			
		});
		
		return listResponse;
	}	
	
	
	public ArrayList getFunction(byte[] response){
		boolean flagOk = false;
		listResponse.clear();
		ApiConnection jsonToObject = new ApiConnection();
		JResponseTokken jResponseT = jsonToObject.getTokken(new String(response));	
		String status = jResponseT.getResponseStatus();
		int code = 0;
		if(jResponseT.getResponseCode().isEmpty()){
			
		}
		else{
			code = Integer.parseInt(jResponseT.getResponseCode());
		}
				
		
		if(status.equals("OK") && code == 100){
			flagOk = true;			
			/*Intent i = new Intent(context, MainActivity.class);
			context.startActivity(i);*/
		}
		else if(code != 100){
			errorCode = new ErrorCode(context);
			errorCode.action(code);
		}
		else{
			noConnection();
		}		
		listResponse.add(jResponseT);
		listResponse.add(flagOk);

		userProfile.setjResponseTokken(jResponseT);
		
		return listResponse;
	}
	
	
	//Token {values, user_data{key-value}, user_group{key-value}}
	public JResponseTokken getTokken(String response){
		JResponseTokken jResponse = null;
		try{							
			String json = response;    
			Gson gson = new Gson();
			jResponse = gson.fromJson(json, JResponseTokken.class);					
		}catch(JsonSyntaxException e){
			e.printStackTrace();
		}
		return jResponse;
	}
	
	//Token {values, user_data{key-value}, user_group{key-value}}
	public JResponserecordVideo sendVideo(String response){
		JResponserecordVideo jResponse = null;
		try{							
			String json = response;    
			Gson gson = new Gson();
			jResponse = gson.fromJson(json, JResponserecordVideo.class);					
		}catch(Exception e){
			e.printStackTrace();
		}
		return jResponse;
	}
	
	public void noConnection(){
		Toast.makeText(context, context.getResources().getString(R.string.toastStart), Toast.LENGTH_LONG).show();
		//((Activity) context).finish();
	}
	
	
	
	public ArrayList getResult(){
		return listResponse;
	}
	
}
