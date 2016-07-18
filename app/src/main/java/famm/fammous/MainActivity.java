package famm.fammous;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import famm.fammous.connection.JResponseTokken;
import famm.fammous.start.StartScreen;
import famm.fammous.util.ConnectionDetector;
import famm.fammous.util.ImageLoadTask;
import famm.fammous.util.UserProfile;
import famm.fammous.video.CameraRecorder;

/*import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;*/



@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends AppCompatActivity{

	private WebView webView;
	private static String urlFammous = "https://famm.fammous.com/";
	private ConnectionDetector connectionDetector;
	private Bundle extras = null;
	private String urlHome;
	//private InterstitialAd interstitial;
	private String TAG = "MainActivity";
	private boolean flagLogin = false;
	private ImageView imageViewProfile;
	private UserProfile userProfile;
	private JResponseTokken jResponseTokken;
	private Toast toastWelcome;
	private View view;
	private static boolean flagFirst = false;
	private TextView textViewWelcome;
	private ImageLoadTask imageLoadTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		LayoutInflater inflater = getLayoutInflater();
		view = inflater.inflate(R.layout.custom_toast_layout,
				(ViewGroup) findViewById(R.id.relativeLayoutCustomToast));

		imageViewProfile = (ImageView) view.findViewById(R.id.imageViewCustomToast);
		textViewWelcome = (TextView) view.findViewById(R.id.textViewWelcomeToast);


		userProfile = new UserProfile();
		imageLoadTask = new ImageLoadTask();
		jResponseTokken = userProfile.getjResponseTokken();

		if (jResponseTokken != null){
			try{
				textViewWelcome.setText(String.format(getResources().getString(R.string.messageToastWelcome), jResponseTokken.getMetadata().getUser_data().getName()));

				String path = Environment.getExternalStorageDirectory().getPath() + "/Fammous/ProfilePic.jpg";
				imageViewProfile.setImageURI(Uri.parse("file://" +path));
				/*Bitmap bmp = BitmapFactory.decodeFile(path);
				imageViewProfile.setImageBitmap(bmp);*/

			}catch(Exception e){
				e.printStackTrace();
			}
		}
		//imageViewProfile.setImageURI(Uri.parse(urlImage));

		Log.d(TAG, "MainActivity");

		//Admob
		/*MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.admobId));
		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
		    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		    .build();
		    mAdView.loadAd(adRequest);*/
		
		/*mAdView.loadAd(adRequest);
		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId(getResources().getString(R.string.admobId));
		AdRequest request = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.build();
		interstitial.loadAd(request);
		
		interstitial.setAdListener(new AdListener() {
			public void onAdLoaded() {
				displayInterstitial();
			}
		});*/



		toastWelcome = new Toast(this);
		toastWelcome.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 15);
		toastWelcome.setDuration(Toast.LENGTH_LONG);
		toastWelcome.setView(view);
		toastWelcome.show();

		extras = this.getIntent().getExtras();
		if(extras != null){
			urlHome = extras.getString("urlHome");
		}

		if (savedInstanceState == null)
		{
			((WebView)findViewById(R.id.webView1)).restoreState(savedInstanceState);
		}
		connectionDetector = new ConnectionDetector(this);

		Log.d("StartScreen", "Inicio");

		webView = (WebView) findViewById (R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);

		this.webView.getSettings().setUserAgentString(
			    this.webView.getSettings().getUserAgentString()
			    + " "
			    + getString(R.string.user_agent_suffix)
			);


		if(connectionDetector.isNetworkConnected() == true){

			String language = Locale.getDefault().getLanguage();
			if(language.equals("es")){
				urlFammous.concat("/es");
			}
			else{
				urlFammous.concat("/en");
			}

			if(urlHome == null){
				webView.loadUrl(urlFammous);
			}
			else{
				webView.loadUrl(urlHome);
			}
		}
		else{
			Toast.makeText(this, this.getResources().getString(R.string.toastSplash), Toast.LENGTH_LONG).show();
		}

		//webviewLoadURL("https://famm.fammous.com/es/fammous/tokkenlogin/a129159df4937d268967cbfb30b5893b");

		//Controlador para el webView, detecta cuando la p�gina comienza o termina de ser cargada
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				/*if(url.contains("/?login")){
					String redirectUrl = "https://famm.fammous.com/es/fammous/timeline";
					view.loadUrl(redirectUrl);
					return;
				}*/

 				Log.d(TAG, "Pagina cargada: " +url);
				if(url.contains("/login_form") && flagLogin == false){
					flagLogin = true;
					Intent i = new Intent(MainActivity.this, StartScreen.class);
					startActivity(i);

				}
				if(url.contains("/online_recorder")){
					Intent i = new Intent(MainActivity.this, CameraRecorder.class);
					startActivity(i);
				}
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.d(TAG, "Cargando la página: " +url);
			}

			@Override
		    public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
		    }

			 //Permite cargar la p�gina si hay un error en el certificado de https
			 @Override
			 public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
			    handler.proceed();
			 }
		});


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    //Si el usuario pulsa atr�s vuelve atr�s en el historial en lugar de cerrar la aplicaci�n
	    if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
	        webView.goBack();
	        return true;
	    }
	    // Si pulsa otra tecla o no hay p�ginas en el historial, realiza el comportamiento por defecto de la aplicaci�n
	    return super.onKeyDown(keyCode, event);
	}

	public void webviewLoadURL(String url) {
        Log.d("StartScreen", "Now loading " + url);
        /*webView.clearHistory();
        webView.clearFormData();
        webView.clearCache(true);*/
        webView.loadUrl(url);
    }

	/*public void displayInterstitial() {
		if (interstitial.isLoaded()) {
			Log.d(TAG, "Mostrar publicidad");
			interstitial.show();
		}
	}*/

	@Override
	protected void onSaveInstanceState(Bundle outState )
	{
		super.onSaveInstanceState(outState);
		webView.saveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		webView.restoreState(savedInstanceState);
	}

	@Override
	public void onResume(){
		super.onResume();

	}
}