package com.app.eeg_mindroid_gokevolution;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

import com.app.eeg_mindroid_gokevolution.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.widget.Toast;

public class PreferenciasBBDD extends PreferenceActivity{
	private static Preference preferenceClickListarDatosBD;
	private static Preference preferenceClickBorrarDatosBD;
	private static Preference preferenceClickExportarDatosBD;
	
	// Uso esto mismo para borrar ya que no me sale el dialog
	private static int contadorBorrarBD = 0;
	private static int contadorExportarBD = 0;
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragmentEntrenamiento(getBaseContext())).commit();
	}
	
	@SuppressLint("ValidFragment")
	public class MyPreferenceFragmentEntrenamiento extends PreferenceFragment {
		private Context context;
		private Writer writer;
		
		public MyPreferenceFragmentEntrenamiento() {}
		
		public MyPreferenceFragmentEntrenamiento(Context context) {
			this.context = context;
		}
		
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferencias_bbdd);
            
            preferenceClickListarDatosBD = (Preference) findPreference("ver_datos");
            preferenceClickBorrarDatosBD = (Preference) findPreference("eliminar_datos");
            preferenceClickExportarDatosBD = (Preference) findPreference("exportar_datos");
            
            preferenceClickListarDatosBD.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				
				public boolean onPreferenceClick(Preference preference) {
					Intent intent = new Intent(context, ListarBD.class);
		            startActivity(intent);
		            return true;
				}
			});

            preferenceClickBorrarDatosBD.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				
				public boolean onPreferenceClick(Preference preference) {
					contadorBorrarBD++;
					
					if(contadorBorrarBD % 5 == 0) {
						BDSQLHelper bdHelper;
				    	SQLiteDatabase datosBD;
				   		bdHelper = new BDSQLHelper(context);
				   		// Abre la base de datos (la crea si es necesario)
				   		datosBD = bdHelper.getWritableDatabase();
				   		bdHelper.reiniciarDatos();
				    	Toast.makeText(context, getString(R.string.txt_datos_eliminados), 1000).show();
					} else if(contadorBorrarBD == 1) {
						Toast.makeText(context, getString(R.string.txt_borrar_pulsar_5_veces), 1000).show();
					}
					
		            return true;
				}
			});
            
            preferenceClickExportarDatosBD.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				
				public boolean onPreferenceClick(Preference preference) {
					contadorExportarBD++;
					
					if(contadorExportarBD % 5 == 0) {
						BDSQLHelper bdHelper;
				    	SQLiteDatabase datosBD;
				   		bdHelper = new BDSQLHelper(context);
				   		// Abre la base de datos (la crea si es necesario)
				   		datosBD = bdHelper.getWritableDatabase();
				   		String[] listaDatosBD = bdHelper.listarDatos();
				   		
				   		if(listaDatosBD.length != 0) {
				   			boolean errorExportarDatos = false;
				   			Date date = new Date();
				   			String month = (String) android.text.format.DateFormat.format("MM", date);
				   			String year = (String) android.text.format.DateFormat.format("yyyy", date);
				   			String day = (String) android.text.format.DateFormat.format("dd", date);
				   			String hour = (date.getHours() < 10)? 
				   					"0" + date.getHours() : String.valueOf(date.getHours());
				   			String minutes = (date.getMinutes() < 10)? 
						   			"0" + date.getMinutes() : String.valueOf(date.getMinutes());
				   			//String nombreArchivoExportado = "datosEEG_" + day + "-" + month + "-" + "-" + year + ".txt";
				   			
				   			// Genero el fichero siempre
				   			String nombreArchivoExportado = "datosEEG.txt";
				   			
				   			try {
								OutputStreamWriter out = new OutputStreamWriter(openFileOutput(nombreArchivoExportado, MODE_WORLD_WRITEABLE));
								
								for (String s : listaDatosBD) {  
					   				out.write(s + '\n');  
					   			} 
								
								out.close();
								
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				   			
				   			// Comparto los datos del fichero generado desde la BD
				   			
				   			InputStream instream;
							try {
								instream = openFileInput("datosEEG.txt");
								
								// if file the available for reading
							    if (instream != null) {		    	
							    	
								      // prepare the file for reading
								      InputStreamReader inputreader = new InputStreamReader(instream);
								      BufferedReader buffreader = new BufferedReader(inputreader);
								       
								      String lineaBD = null;
								      //We initialize a string "line" 
								      String datosACompartir = "";
								      String fechaCreacionDatos = day + "/" + month + "/" +
												year + " - " + hour + ":" + minutes;
								      
								      while (( lineaBD = buffreader.readLine()) != null)
								    	  datosACompartir = datosACompartir + lineaBD + '\n';
								      Intent goCompartir = new Intent(android.content.Intent.ACTION_SEND);
								      goCompartir.setType("text/plain");
								      goCompartir.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(
											R.string.txt_fecha_captura_datos) + " " + fechaCreacionDatos);
									  goCompartir.putExtra(android.content.Intent.EXTRA_TEXT, datosACompartir);
								      startActivity(goCompartir);
							    }
							} catch (FileNotFoundException e) {
								errorExportarDatos = true;
							} catch (IOException e) {
								errorExportarDatos = true;
							}

				   			/*FileOutputStream outputStream;
				   			
				   			try {
				   			  outputStream = context.openFileOutput("wapo.txt", Context.MODE_WORLD_READABLE);
				   			  for (String s : listaDatosBD) {  
				   			      outputStream.write(s.getBytes());  
				   			  } 
				   			  
				   			  outputStream.flush();
				   			  outputStream.close();
				   			} catch (Exception e) {
				   			  Toast.makeText(context, getString(R.string.txt_error_exportar_datos), 2000).show();
				   			  errorExportarDatos = true;
				   			}*/
				   			
				   			if(errorExportarDatos)
				   				Toast.makeText(context, getString(R.string.txt_error_exportar_datos), 2000).show();
				   		} else {
				   			Toast.makeText(context, getString(R.string.txt_no_datos_guardados_exportar), 1500).show();
				   		}
				   		
					} else if(contadorExportarBD == 1) {
						Toast.makeText(context, getString(R.string.txt_exportar_pulsar_5_veces), 1000).show();
					}
					
					return true;
				}
			});
        }
    }
}