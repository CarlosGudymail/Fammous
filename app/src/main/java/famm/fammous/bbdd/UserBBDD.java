package famm.fammous.bbdd;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Esta clase implementa las operaciones de insertar, actualizar, borrar y obtener datos del usuario.
 */
public class UserBBDD {
	
	static OperacionesBBDD op;	
	static SQLiteDatabase db;
	
	public UserBBDD (SQLiteDatabase db){
		op = new OperacionesBBDD(db);
	}
	
	public UserBBDD(){		
	}
	
	
	public void insertUser(String email, String tokken){		
		String sql = "INSERT INTO User (_id, email, tokken) VALUES ('1','" + email + "','" +tokken+ "')";
		op.modificar(sql);	
	}
	
	public void delete(){
		String sql = "DELETE FROM User";
		op.modificar(sql);
	}
	
	@SuppressWarnings("rawtypes")
	public ArrayList get(){
		ArrayList list = new ArrayList();
		String sql = "SELECT email, tokken FROM User WHERE _id = 1";
		list = op.obtener(sql, "User");
		return list;
	}
	
	public void update(String email, String tokken){
		String sql = "UPDATE User SET email = '"+email+"', tokken = '"+tokken+"' WHERE _id=1";
		op.modificar(sql);
	}
	
	public boolean isEmpty(){		
		String sql = "SELECT COUNT(*) FROM User";
		try {
			ArrayList list = op.obtener(sql, "User");
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
