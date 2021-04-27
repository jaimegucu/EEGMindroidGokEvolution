package com.app.eeg_mindroid_gokevolution;

import java.util.ArrayList;
import java.util.List;

import com.app.eeg_mindroid_gokevolution.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListaOpciones extends ListActivity{
	private List<String> listaOpciones;
	private ListAdapter adaptador;
	private ListView listViewOpciones;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Pon el setContenView primero para que se puedan cargar todas las vistas bien
		setContentView(R.layout.lista_opciones);
        super.onCreate(savedInstanceState);
        
        listaOpciones = new ArrayList<String>();
        
        listaOpciones.add(getString(R.string.btn_opciones_generales));
        listaOpciones.add(getString(R.string.btn_opciones_base_datos));
        listaOpciones.add(getString(R.string.btn_salir));
        
        listViewOpciones = (ListView) findViewById(android.R.id.list);
        
        adaptador = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listaOpciones);
        
        // Asociamos el adaptador a la vista
        listViewOpciones.setAdapter(adaptador);
        
        listViewOpciones.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView< ?> arg0, View arg1, int arg2, long arg3) {
        	 
        	      ListAdapter la = (ListAdapter) arg0.getAdapter();
        	      
        	      // Accede al menu de opciones en funcion de que boton se pulse en el listview
        	      if(la.getItem(arg2).toString().equals(getString(R.string.btn_aplicaciones))) {
        	    	  
        	      } else if(la.getItem(arg2).toString().equals(getString(R.string.btn_juegos))) {
        	    	  
        	      } else if(la.getItem(arg2).toString().equals(getString(R.string.btn_entrenamiento))) {
        	      } else if(la.getItem(arg2).toString().equals(getString(R.string.btn_opciones_generales))) {
        	    	  Intent intentOpcionesGenerales = new Intent(ListaOpciones.this, PreferenciasGenerales.class);
      				  startActivity(intentOpcionesGenerales);
        	      } else if(la.getItem(arg2).toString().equals(getString(R.string.btn_opciones_ayuda))) {
        	      } else if(la.getItem(arg2).toString().equals(getString(R.string.btn_opciones_acercade))) {
        	      } else if(la.getItem(arg2).toString().equals(getString(R.string.btn_opciones_salir))) {
        	    	  finish();
        	      } else if(la.getItem(arg2).toString().equals(getString(R.string.btn_opciones_base_datos))) {
        	    	  Intent intentOpcionesBaseDatos = new Intent(ListaOpciones.this, PreferenciasBBDD.class);
      				  startActivity(intentOpcionesBaseDatos);
        	      }
        	   };
        	});
	}
}
