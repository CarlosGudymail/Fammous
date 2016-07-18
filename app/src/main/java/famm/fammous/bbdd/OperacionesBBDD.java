package famm.fammous.bbdd;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Esta clase implementa las operaciones de insertar, obtener, borrar y actualizar que realizan las
 * demas clases.
 */
public class OperacionesBBDD {
	
	static SQLiteDatabase sqlite;
	private static StartBBDD startBBDD;
	private static VideoBBDD videoBBDD;
	private static Context context;
	private UserBBDD userBBDD;
	/**
	 * Constructor.
	 */
	@SuppressWarnings("static-access")
	public OperacionesBBDD (SQLiteDatabase sqlite){
		this.sqlite = sqlite;
	}
	
	/**
	 * Constructor.
	 */
	public OperacionesBBDD (){		
	}
	
	/**
	 * M�todo que utilizan las dem�s clases cuando desean borrar, actualizar o insertar datos en la base de datos.
	 */
	public void modificar(String sql) {		
		if(sqlite != null){					
			sqlite.execSQL(sql);			
		}		
	}

	/**
	 * Metodo que utilizan las demas clases cuando desean obtener datos de la base de datos.
	 * Devuelve un array con los datos que se han solicitado.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public ArrayList obtener(String sql, String nombreTabla) {
		
		ArrayList list = new ArrayList();		
		Cursor c = sqlite.rawQuery(sql,null);
		//Asegura de que existe al menos un registro
		if (c.moveToFirst()) {
		     //Rellena el array con cada campo hasta que el metodo isNull() devuelva true al encontrar un campo vacio
		    for(int i=0; c.isNull(i)!=true; i++){		    	
		    	list.add(c.getString(i));			    	
		    	
		     } while(c.moveToNext());
		}  		    		
		return list;
	}

	public void init(Context context){
		if(OperacionesBBDD.sqlite == null){
			startBBDD = new StartBBDD(context);
			try {
				startBBDD.createDataBase(context);
			} catch (IOException ioe) {
				throw new Error("Unable to create database");
			}

			//Base de datos en modo escritura
			sqlite = startBBDD.getWritableDatabase();

			if(sqlite != null){
				userBBDD = new UserBBDD(sqlite);
				videoBBDD = new VideoBBDD(sqlite);
				startBBDD.crearTablas(sqlite); //Crea las tablas en la base de datos si no han sido creadas
			}
		}
	}
	
	
	@SuppressWarnings("static-access")
	public void setSqlite(SQLiteDatabase sqlite){
		this.sqlite = sqlite;
	}
	
	public SQLiteDatabase getSqlite(){
		return OperacionesBBDD.sqlite;
	}
	
}
