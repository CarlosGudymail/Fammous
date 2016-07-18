package famm.fammous.bbdd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
 
/**
 * Esta clase inicia la base de datos.
 */
public class StartBBDD extends SQLiteOpenHelper{ 
    
	@SuppressLint("SdCardPath")
	private final String DB_PATH = "/data/data/com.fammous.client/databases/";
 
    //El nombre del archivo de la base de datos
    private final static String DB_NAME = "bbdd.sqlite";
 
    private static SQLiteDatabase myDataBase;  
    private final Context myContext;
 
    public StartBBDD(Context context) {
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }      
     
    /**
     * Metodo que crea una base de datos vacia y escribe en ella la base de datos propia.
     * */
    public void createDataBase(Context contexto) throws IOException{
 
    	//Comprueba si ya existe la base de datos
    	boolean dbExist = checkDataBase(); 
 
    	if(dbExist){   		
    	}
    	else{
    		//Si no existe crea una nueva base de datos en la ruta por defecto
    		this.getReadableDatabase(); 
        	try {
        		//Copia database.sqlite en la nueva base de datos creada        		
    			copyDataBase(); 
    		} catch (IOException e) {         		
        	}
    	}
    }
 
    /**
     * Metodo que comprueba si ya existe la base de datos.
     */
    private boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS); 
    	}catch(SQLiteException e){     		
     	}
 
    	if(checkDB != null){ 
    		checkDB.close(); 
    	}
 
    	return checkDB != null ? true : false;
    } 
 
    /**
     * Metodo que copia la base de datos sqlite de la carpeta assets a la nueva base de datos.
     * */
    private void copyDataBase() throws IOException{
 
    	//Abre la base de datos del fichero    	
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
 
    	//La direcci�n de la nueva base de datos    	
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Abre la nueva base de datos    	
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//Transfiere bytes desde el archivo a la nueva base de datos    	
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Cierra los streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close(); 
    }
 
    /**
     * Metodo que abre la base de datos.
     */
    public void openDataBase() throws SQLException{
    	String myPath = DB_PATH + DB_NAME;
    	File file = new File(myPath);    
    	if (file.exists() && !file.isDirectory()){        
    		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    	}
    }
 
   
	public synchronized void close() {    	
    	if(myDataBase != null)
    		myDataBase.close(); 
    	super.close(); 
	}
 
    public SQLiteDatabase getBBDD(){
    	return myDataBase;
    }
	
    /**
     * Metodo que crea todas las tablas de la base de datos.
     */
	public void crearTablas(SQLiteDatabase db) {
		
		String sqlCreate1 = "CREATE TABLE IF NOT EXISTS User (_id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, tokken TEXT)";
		String sqlCreate2 = "CREATE TABLE IF NOT EXISTS VideoData (_id INTEGER PRIMARY KEY AUTOINCREMENT, route TEXT, title TEXT, comment TEXT)";
		/*String sqlCreate3 = "CREATE TABLE IF NOT EXISTS Contacts (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT)";
		String sqlCreate4 = "CREATE TABLE IF NOT EXISTS Code (_id INTEGER PRIMARY KEY AUTOINCREMENT,code TEXT, url TEXT, logo TEXT, name TEXT, background TEXT, text TEXT, argument TEXT)";*/
		
		db.execSQL(sqlCreate1); 
		db.execSQL(sqlCreate2);
		/*db.execSQL(sqlCreate3);
		db.execSQL(sqlCreate4);*/
	}
 
	 /**
     * Metodo que borra todas las tablas de la base de datos.
     */
	public void borrarTablas(SQLiteDatabase db) {
		String sqlDelete1 = "DROP TABLE IF EXISTS User";
		String sqlDelete2 = "DROP TABLE IF EXISTS VideoData";
		/*String sqlDelete3 = "DROP TABLE IF EXISTS Contacts";
		String sqlDelete4 = "DROP TABLE IF EXISTS Code";*/
        db.execSQL(sqlDelete1); 
        db.execSQL(sqlDelete2);
        /*db.execSQL(sqlDelete3);
        db.execSQL(sqlDelete4);*/
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {		
	}
}
