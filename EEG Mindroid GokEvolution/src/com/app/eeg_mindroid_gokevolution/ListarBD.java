package com.app.eeg_mindroid_gokevolution;

import com.app.eeg_mindroid_gokevolution.R;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListarBD extends Activity {
	private BDSQLHelper bdHelper;
	private SQLiteDatabase datosBD;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Pon el setContenView primero para que se puedan cargar todas las vistas bien
		setContentView(R.layout.lista_datos_bd);
        super.onCreate(savedInstanceState);
        
        bdHelper = new BDSQLHelper(getBaseContext());
		// Abre la base de datos (la crea si es necesario)
		datosBD = bdHelper.getWritableDatabase();
        
        ListView listView1 = (ListView) findViewById(R.id.listaDatosBD);
        
        String[] valoresBD = bdHelper.listarDatos();
        
        // Si no hay datos, muestro mensaje
        if(valoresBD.length == 0) {
        	valoresBD = new String[1];
        	valoresBD[0] = getString(R.string.txt_no_datos_guardados);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, valoresBD);
        
        listView1.setAdapter(adapter);
	}
}
