package famm.fammous.start;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import famm.fammous.R;
import famm.fammous.connection.ApiConnection;
import famm.fammous.connection.Callback;
import famm.fammous.util.DebugAct;


public class Signup extends Activity implements AdapterView.OnItemSelectedListener {

    private Button buttonImage, buttonSend;
    private EditText editTextEmail, editTextPassword, editTextName, editTextSurname, editTextDate, editTextCity, editTextAddress, editTextProvince, editTextPhone;
    private String email, password, languageString, name, surname, birth_date, city, countryString, genderString, address, province;
    private int language, country, gender, groupType, phone;
    private int [] interest;
    private File file;
    private static final int PICK_PHOTO_FOR_AVATAR = 0;
    private Context context;
    private ImageView imageView;
    private ApiConnection api;
    private ArrayList params;
    private ArrayList args;
    private DebugAct debug;
    private static final String TAG = "Signup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        Log.d(TAG, "OnCreate");

        buttonImage = (Button) findViewById(R.id.buttonImageSignup);
        buttonSend = (Button) findViewById(R.id.buttonSendSignup);
        imageView = (ImageView) findViewById(R.id.imageViewUserSignup);
        editTextEmail = (EditText) findViewById(R.id.editTextEmailSignup);
        editTextSurname = (EditText) findViewById(R.id.editTextSurnameSignup);
        editTextPassword = (EditText) findViewById(R.id.editTextPasswordSignup);
        editTextName = (EditText) findViewById(R.id.editTextNameSignup);
        editTextDate = (EditText) findViewById(R.id.editTextBirthdaySignup);
        editTextCity = (EditText) findViewById(R.id.editTextCitySignup);
        editTextAddress = (EditText) findViewById(R.id.editTextAddressSignup);
        editTextProvince = (EditText) findViewById(R.id.editTextProvinceSignup);

        context = this;
        api = new ApiConnection();
        params = new ArrayList();
        debug = new DebugAct();
        //args = new ArrayList();

        if(debug.isDebug()){
        }

        Spinner spinner = (Spinner) findViewById(R.id.spinnerGenderSignup);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinnerGenderSignup, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        languageString = Locale.getDefault().getLanguage();
        if(languageString.startsWith("es")){
            language = 0;
        }
        else{
            language = 1;
        }
        params.add("langugage");
        params.add(language);

        Spinner citizenship = (Spinner)findViewById(R.id.spinnerCountriesSignup);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, countries);
        citizenship.setAdapter(adapter2);

        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEditTextEmpty() == true) {
                    api.connectionPost(context, "signup", args, params, new Callback<Integer>() {
                        @Override
                        public void onResponse(Integer t) {
                            Log.d(TAG, "Respuesta");
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        parent.getItemAtPosition(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }

    //Obtiene la foto de la galería
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(data.getData());
                file = new File(data.getData().getPath());
                imageView.setImageURI(data.getData());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    protected boolean checkEditTextEmpty() {

        if(editTextEmail.getText().toString().trim().length() == 0){
            editTextEmail.setError(getResources().getString(R.string.editTextErrorVideo));
            return false;
        }
        else if(editTextPassword.getText().toString().trim().length() == 0){
            editTextPassword.setError(getResources().getString(R.string.editTextErrorVideo));
            return false;
        }
        else if(editTextName.getText().toString().trim().length() == 0){
            editTextName.setError(getResources().getString(R.string.editTextErrorVideo));
            return false;
        }
        else if(editTextSurname.getText().toString().trim().length() == 0){
            editTextSurname.setError(getResources().getString(R.string.editTextErrorVideo));
            return false;
        }
        else if(editTextDate.getText().toString().trim().length() == 0){
            editTextDate.setError(getResources().getString(R.string.editTextErrorVideo));
            return false;
        }
        else if(editTextCity.getText().toString().trim().length() == 0){
            editTextCity.setError(getResources().getString(R.string.editTextErrorVideo));
            return false;
        }
        else if(editTextAddress.getText().toString().trim().length() == 0){
            editTextAddress.setError(getResources().getString(R.string.editTextErrorVideo));
            return false;
        }
        else if(editTextProvince.getText().toString().trim().length() == 0){
            editTextProvince.setError(getResources().getString(R.string.editTextErrorVideo));
            return false;
        }
        else if(editTextPhone.getText().toString().trim().length() == 0){
            editTextPhone.setError(getResources().getString(R.string.editTextErrorVideo));
            return false;
        }
        return true;
    }
}
