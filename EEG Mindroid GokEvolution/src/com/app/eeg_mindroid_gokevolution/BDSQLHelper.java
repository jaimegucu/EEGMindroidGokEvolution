package com.app.eeg_mindroid_gokevolution;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BDSQLHelper extends SQLiteOpenHelper implements EEG_MindroidBDInterface {
	private static String NOMBRE_BD = "EEG_MINDROID";
	private static int VERSION_BD = 1;
	
	public BDSQLHelper(Context context) {
		super(context, NOMBRE_BD, null, VERSION_BD);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE DATOS_EEG( " +
				"ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + 
				"SESION INTEGER NOT NULL, " + 
				"SEGUNDO INTEGER NOT NULL," + 
				"REPOSO INTEGER NOT NULL, " + 
				"CONCENTRACION INTEGER NOT NULL, " + 
				"RELAJACION INTEGER NOT NULL, " +  
				"DELTA INTEGER NOT NULL, " + 
				"THETA INTEGER NOT NULL, " + 
				"LOW_ALPHA INTEGER NOT NULL," +
				"HIGH_ALPHA INTEGER NOT NULL," +
				"LOW_BETA INTEGER NOT NULL," +
				"HIGH_BETA INTEGER NOT NULL," +
				"LOW_GAMMA INTEGER NOT NULL," +
				"HIGH_GAMMA INTEGER NOT NULL);"); 
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

	public void guardarDatosSesionActual() {
		SQLiteDatabase db = getWritableDatabase();
		
		// Lo hago asi en vez de usar ContentValues para no crear un objeto nuevo por cada linea
		// Introduce una linea por cada tamaño de cualquiera de las listas de Integer que contiene mi clase Datos EEG
		for(int i=0;i<DatosEEG.concentracion.size();i++) {
			db.execSQL("INSERT INTO DATOS_EEG (SESION, SEGUNDO, REPOSO, CONCENTRACION, RELAJACION, " +
					"DELTA, THETA, LOW_ALPHA, HIGH_ALPHA, LOW_BETA, HIGH_BETA, LOW_GAMMA, HIGH_GAMMA) " +
					"VALUES (" + 
					DatosEEG.sesion.get(i) + ", " +
					DatosEEG.segundo.get(i) + ", " +
					DatosEEG.reposo.get(i) + ", " +
					DatosEEG.concentracion.get(i) + ", " +
					DatosEEG.relajacion.get(i) + ", " +
					DatosEEG.delta.get(i) + ", " +
					DatosEEG.theta.get(i) + ", " +
					DatosEEG.lowAlpha.get(i) + ", " +
					DatosEEG.highAlpha.get(i) + ", " +
					DatosEEG.lowBeta.get(i) + ", " +
					DatosEEG.highBeta.get(i) + ", " +
					DatosEEG.lowGamma.get(i) + ", " +
					DatosEEG.highGamma.get(i) + ")");
		}
		
		db.close();
		// Ponemos los datos a 0 de nuevo. Lo hago aqui para evitar problemas
		DatosEEG.reiniciarDatos();
	}

	public void reiniciarDatos() {
		SQLiteDatabase db = getWritableDatabase();
		
		db.execSQL("DELETE FROM DATOS_EEG");
		
		db.close();
	}

	public String[] listarDatos() {
		SQLiteDatabase db = getReadableDatabase();
		List<String> datosBD = new ArrayList<String>();
		String lineaBD = "";
		//String[] a = datosBD.toArray(new String[datosBD.size()]);
		
		Cursor cursor = db.rawQuery("SELECT SESION, SEGUNDO, REPOSO, CONCENTRACION, RELAJACION, DELTA, THETA, " +
				"LOW_ALPHA, HIGH_ALPHA, LOW_BETA, HIGH_BETA, LOW_GAMMA, HIGH_GAMMA FROM DATOS_EEG", null);
		
		while(cursor.moveToNext()) {
			for(int i=0;i<cursor.getColumnCount();i++) {
				if(i!=cursor.getColumnCount()-1)
					lineaBD = lineaBD + cursor.getString(i) + ", ";
				else
					lineaBD = lineaBD + cursor.getString(i);
			}
			
			datosBD.add(lineaBD);
			lineaBD = "";
		}
		
		return datosBD.toArray(new String[datosBD.size()]);
	}
	
	// Esto es para cuando no borro los datos anteriores y no me deja exportar porque hay muchos
	public void borrarAlgunosDatos() {
		SQLiteDatabase db = getWritableDatabase();
		
		db.execSQL("DELETE FROM DATOS_EEG WHERE ID >= 10868 AND ID <= 11668");
		
		db.close();
	}
}
