package com.app.eeg_mindroid_gokevolution;

import java.util.ArrayList;
import java.util.List;

import com.app.eeg_mindroid_gokevolution.R;
import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGEegPower;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

// En esta clase estan todos los datos que se repiten entre los menus
public class EEG_MenuVistaComun extends Activity {
	/*** DATOS RELACIONADOS CON NEUROSKY ***/
	// Adaptador para conectar bluetooth
	protected BluetoothAdapter bluetoothAdapter;
	// Objeto con todos los datos de neurosky
	protected TGDevice tgDevice;
	// Booleano para indicar si queremos recibir datos en raw o no
	protected static final boolean rawEnabled = false;
	// ProgressDialog que se muestre mientras conectamos con el bluetooth
	protected ProgressDialog pDCargandoBluetooth;
	// Listener para activar los metodos correspondientes por cada dato que nos llegue de Neurosky
	protected static List<NeuroskyListener> listenersNeurosky;
	/***************************************************/
	
	// Son protected para que puedan ser llamados desde las clases que heredan
	//protected VerticalProgressBar progressBarConcentracion;
	//protected VerticalProgressBar progressBarSeleccion;
	protected ImageView imagenCalidadConexion;
	protected ImageView imagenBateria;
	protected ImageView imagenMindClock;
	protected TextView textoPorcentajeBateria;
	protected TextView textoSegundosSeleccion;
	
	// Segundos del contador de cambio de botones
	//protected int SEGUNDOS_SELECCION;
	
	// Contador de segundos para seleccionar un boton
	//protected int contadorSegundosSeleccion;
	// Indice del boton seleccionado (el primero por defecto)
	protected int indiceBotonSeleccionado = 0;
	
	// Indices de las barras de concentracion y seleccion
	protected int nivelBarraConcentracion = 0;
	protected int nivelBarraSeleccion = 0;
	
	// Porcentaje necesario del nivel de la barra de concentracion para que se active la barra de seleccion
	protected int porcentajeActivarBarraConcentracion;
	
	// Tiempo de seleccion de un boton
	protected int tiempoSeleccionBoton;
	
	// Nivel de señal (200 = desconectado)
	protected int nivelSeñalConexion = 200;
	
	// Lista de los botones de cada pantalla
	protected List<Button> listaBotones;
	
	// Inicio aplicacion nueva. Es para que al pasar de un menu a otro NO seleccione el primer boton en el onPause()
	protected boolean aplicacionIniciada = false;
	
	// Resultado de las acciones a realizar. En -1 es NO realizar nada y cuando tome un valor sera el indice de la 
	// lista de botones de la pantalla
	protected int resultadoAccion = -1;
	
	// Preferencias
	protected SharedPreferences preferencias;
	
	// Creamos un BroadcastReceiver que nos avise cuando cambia el % de la bateria
	protected BroadcastReceiver porcentajeBateriaReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			int level = intent.getIntExtra("level", 0);
			// Hago esto para escribirlo bien sin mover el icono de la bateria
			if (level == 100) {
				calculaImagenBateria(level);
				textoPorcentajeBateria.setText(String.valueOf(level) + "%");
			} else if (level >= 10) {				
				calculaImagenBateria(level);
				textoPorcentajeBateria.setText("  " + String.valueOf(level) + "%");
			} else {
				calculaImagenBateria(level);
				textoPorcentajeBateria.setText("    " + String.valueOf(level) + "%");
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//progressBarConcentracion = (VerticalProgressBar) findViewById(R.id.progressbarConcentracion);
		//progressBarSeleccion = (VerticalProgressBar) findViewById(R.id.progressbarSeleccion);
		imagenCalidadConexion = (ImageView) findViewById(R.id.imagenCalidadConexion);
		imagenBateria = (ImageView) findViewById(R.id.imagenPorcentajeBateria);
		imagenMindClock = (ImageView) findViewById(R.id.imagenMindClock);
		textoPorcentajeBateria = (TextView) findViewById(R.id.textoPorcentajeBateria);
		textoSegundosSeleccion = (TextView) findViewById(R.id.textoSegundosSeleccion);
		
		// Iniciamos las preferencias
		preferencias = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		
		/*** INICIACION DATOS NEUROSKY ***/
		
		listenersNeurosky = new ArrayList<NeuroskyListener>();
		
		// Iniciamos bluetooth y progressdialog mientras que se carga el bluetooth
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pDCargandoBluetooth = new ProgressDialog(this);
        pDCargandoBluetooth.setCancelable(false);
        pDCargandoBluetooth.setCanceledOnTouchOutside(false);
        
        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
        	// Alert user that Bluetooth is not available
        	Toast.makeText(this, getString(R.string.bluetooth_no_encendido), 999999).show();
        }else {
        	/* create the TGDevice */
        	tgDevice = new TGDevice(bluetoothAdapter, handler);
        	if(tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED)
        		tgDevice.connect(rawEnabled);   
        }  
        
        /***************************************************/
	}
	
	@Override
    public void onResume() {
    	super.onResume();
    	// Registramos el Receiver del porcentaje de bateria
        this.registerReceiver(this.porcentajeBateriaReceiver, 
        	    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        
        // Este es el contador de la pantalla principal
        //SEGUNDOS_SELECCION = Integer.valueOf(preferencias.getString("contador_segundos", "10"));
        // Porcentaje de la barra de concentracion en el que empieza a contar la barra de seleccion
        porcentajeActivarBarraConcentracion = Integer.valueOf(preferencias.getString("porcentaje_seleccion", "50"));
        // Tiempo para que un boton se seleccione
     	tiempoSeleccionBoton = Integer.valueOf(preferencias.getString("contador_seleccion", "4"));
        

		nivelBarraSeleccion = 0;
		nivelBarraConcentracion = 0;
    }
    
	@Override
	protected void onPause() {
		super.onPause();
		this.unregisterReceiver(porcentajeBateriaReceiver);
		
		nivelBarraSeleccion = 0;
		nivelBarraConcentracion = 0;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// Esto es para que no de el error de view not attached to window manager
		if ((pDCargandoBluetooth != null) && pDCargandoBluetooth.isShowing())
			pDCargandoBluetooth.dismiss();
	    
		pDCargandoBluetooth = null;
	}
	
	private void calculaImagenBateria(int level) {
		if(level <= 100 && level >= 92) {
			imagenBateria.setImageResource(R.drawable.bateria100_92);
		} else if(level >= 84 && level <= 91) {
			imagenBateria.setImageResource(R.drawable.bateria91_84);
		} else if(level >= 76 && level <= 83) {
			imagenBateria.setImageResource(R.drawable.bateria83_76);
		} else if(level >= 68 && level <= 75) {
			imagenBateria.setImageResource(R.drawable.bateria75_68);
		} else if(level >= 60 && level <= 67) {
			imagenBateria.setImageResource(R.drawable.bateria67_60);
		} else if(level >= 52 && level <= 59) {
			imagenBateria.setImageResource(R.drawable.bateria59_52);
		} else if(level >= 44 && level <= 51) {
			imagenBateria.setImageResource(R.drawable.bateria51_44);
		} else if(level >= 36 && level <= 43) {
			imagenBateria.setImageResource(R.drawable.bateria43_36);
		} else if(level >= 29 && level <= 35) {
			imagenBateria.setImageResource(R.drawable.bateria35_29);
		} else if(level >= 22 && level <= 28) {
			imagenBateria.setImageResource(R.drawable.bateria28_22);
		} else if(level >= 14 && level <= 21) {
			imagenBateria.setImageResource(R.drawable.bateria21_14);
		} else if(level >= 7 && level <= 13) {
			imagenBateria.setImageResource(R.drawable.bateria13_7);
		} else {
			imagenBateria.setImageResource(R.drawable.bateria6_0);
		}
	}
	
	private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
            case TGDevice.MSG_STATE_CHANGE:

            	switch (msg.arg1) {
	                case TGDevice.STATE_IDLE:
	                    break;
	                case TGDevice.STATE_CONNECTING:		                	
	                	pDCargandoBluetooth.setMessage(getString(R.string.conectando_bluetooth));
	                	pDCargandoBluetooth.show();
	                	break;		                    
	                case TGDevice.STATE_CONNECTED:
	                	// Esto es para que no de el error de view not attached to window manager
	                	if ((pDCargandoBluetooth != null) && pDCargandoBluetooth.isShowing())
	            			pDCargandoBluetooth.dismiss();

	                	tgDevice.start();
	                    break;
	                case TGDevice.STATE_NOT_FOUND:
	                	break;
	                case TGDevice.STATE_NOT_PAIRED:
	                	Toast.makeText(EEG_MenuVistaComun.this, getString(R.string.bluetooth_no_emparejado), 999999).show();
	                	pDCargandoBluetooth.dismiss();
	                	break;
	                case TGDevice.STATE_DISCONNECTED:
            	}

                break;
            case TGDevice.MSG_POOR_SIGNAL:
            	//newSignalValue(String.valueOf(msg.arg1));
            	nivelSeñalConexion = msg.arg1;
            	
            	switch(nivelSeñalConexion) {
	    			case 0:
	    				imagenCalidadConexion.setImageResource(R.drawable.circle_green);
	    				break;
	    			case 200:
	    				imagenCalidadConexion.setImageResource(R.drawable.circle_red);
	    				//listaBotones.get(indiceBotonSeleccionado).setPressed(true);
	    				nivelBarraSeleccion = 0;
	    				//progressBarSeleccion.setProgress(nivelBarraSeleccion);
	    				break;
	    			default:
	    				imagenCalidadConexion.setImageResource(R.drawable.circle_yellow);
	    				//listaBotones.get(indiceBotonSeleccionado).setPressed(true);
	    				nivelBarraSeleccion = 0;
	    				//progressBarSeleccion.setProgress(nivelBarraSeleccion);
	    				break;
            	}

            	launchNewSignal(nivelSeñalConexion);

                break;
            case TGDevice.MSG_RAW_DATA:	  
            	launchNewRaw(String.valueOf(msg.arg1));
            	break;
            // No funciona con Neurosky Mobile
            //case TGDevice.MSG_HEART_RATE:
            case TGDevice.MSG_ATTENTION:
            	launchNewConcentration(String.valueOf(msg.arg1));
            	break;
            case TGDevice.MSG_MEDITATION:
            	launchNewRelax(String.valueOf(msg.arg1));
            	break;
            case TGDevice.MSG_BLINK:
            	launchNewBlink(String.valueOf(msg.arg1));
            	break;
            // Devuelve siempre 512, asi que lo obvio
            //case TGDevice.MSG_RAW_COUNT:
            case TGDevice.MSG_LOW_BATTERY:
            	Toast.makeText(getApplicationContext(), getString(R.string.bateria_baja), Toast.LENGTH_SHORT).show();
            	break;
            // No funciona con Neurosky Mobile
            //case TGDevice.MSG_RAW_MULTI: 
            	//TGRawMulti rawM = (TGRawMulti)msg.obj;
            case TGDevice.MSG_EEG_POWER:
            	//TGEegPower ep = (TGEegPower)msg.obj;
            	launchNewEEGPower((TGEegPower)msg.obj);
            	break;
            default:
            	break;
        	}
        }
    };
    
    private static void launchNewSignal(Integer signal) {
		if (listenersNeurosky != null && !listenersNeurosky.isEmpty()) {
			for (NeuroskyListener listener : listenersNeurosky) {
				listener.newSignalValue(signal);
			}
		}
	}
    
    private static void launchNewEEGPower(TGEegPower eegPower) {
		if (listenersNeurosky != null && !listenersNeurosky.isEmpty()) {
			for (NeuroskyListener listener : listenersNeurosky) {
				listener.newEEGPower(eegPower);
			}
		}
	}
    
    private static void launchNewConcentration(String concentration) {
		if (listenersNeurosky != null && !listenersNeurosky.isEmpty())
			for (NeuroskyListener listener : listenersNeurosky) {
				listener.newConcentrationValue(concentration);
			}
	}
    
    private static void launchNewRelax(String relax) {
		if (listenersNeurosky != null && !listenersNeurosky.isEmpty()) {
			for (NeuroskyListener listener : listenersNeurosky) {
				listener.newRelaxValue(relax);
			}
		}
	}
    
    private static void launchNewBlink(String blink) {
		if (listenersNeurosky != null && !listenersNeurosky.isEmpty()) {
			for (NeuroskyListener listener : listenersNeurosky) {
				listener.newBlinkValue(blink);
			}
		}
	}
    
    private static void launchNewRaw(String raw) {
		if (listenersNeurosky != null && !listenersNeurosky.isEmpty()) {
			for (NeuroskyListener listener : listenersNeurosky) {
				listener.newRawValue(raw);
			}
		}
	}
    
    // Interfaz que avisara a todas las clases que la implementen de los datos recibidos en Neurosky
    // Las señales llegan cada segundo en el orden en el que estan declarados los metodos excepto el 
    // parpadeo y los datos RAW
    interface NeuroskyListener {
    	// Señal de la conexion (0 -> Correcta, 200 -> Desconectado, Otro -> Conectado pero no correcto)
		public void newSignalValue(Integer value);
		// Objeto con todas las señales EEG (Delta, Theta, LowAlpha, HighAlpha, LowBeta, HighBeta, LowGamma, HighGamma)
		public void newEEGPower(TGEegPower eegPower);
		// Concentracion / Atencion
		public void newConcentrationValue(String value);
		// Relajacion / Meditacion
		public void newRelaxValue(String value);
		// Parpadeo (Solo se invoca al parpadear) 
		public void newBlinkValue(String value);
		// Señal RAW con datos en bruto (Se llama 512 veces por segundo)
		public void newRawValue(String value);
	}
}
