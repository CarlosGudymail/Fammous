package famm.fammous.bbdd;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Esta clase implementa las operaciones de insertar, actualizar, borrar y obtener datos del video guardado.
 */
public class VideoBBDD {

	static OperacionesBBDD op;
	static SQLiteDatabase db;

	public VideoBBDD(SQLiteDatabase db){
		op = new OperacionesBBDD(db);
	}

	public VideoBBDD(){
	}
	
	
	public void insertVideo(String route, String title, String comment){
		String sql = "INSERT INTO VideoData (_id, route, title, comment) VALUES ('1','" + route + "','" +title+ "','" +comment+ "')";
		op.modificar(sql);	
	}
	
	public void delete(){
		String sql = "DELETE FROM VideoData WHERE _id = 1";
		op.modificar(sql);
	}
	
	@SuppressWarnings("rawtypes")
	public ArrayList get(){
		ArrayList list = new ArrayList();
		String sql = "SELECT route, title, comment FROM VideoData WHERE _id = 1";
		list = op.obtener(sql, "VideoData");
		return list;
	}
	
	public void update(String route, String title, String comment){
		String sql = "UPDATE VideoData SET route = '"+route+"', title = '"+title+"', comment = '"+comment+"' WHERE _id=1";
		op.modificar(sql);
	}
	
	public boolean isEmpty(){		
		String sql = "SELECT COUNT(*) FROM VideoData";
		try {
			ArrayList list = op.obtener(sql, "VidedoData");
			if((Integer.parseInt(list.get(0).toString()))==0)	{
				list.clear();
				return true;
			}
			list.clear();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return false;		
	}
}
