package com.app.eeg_mindroid_gokevolution;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.eeg_mindroid_gokevolution.EEG_MenuVistaComun.NeuroskyListener;
import com.neurosky.thinkgear.TGEegPower;

public class EEG_Mindroid_GokEvolution extends EEG_MenuVistaComun implements EscuchadorEventosGokEvolution, NeuroskyListener {
	// SurfaceView con la imagen de gokevolution
	private SurfaceViewGokEvolution sVGokEvolution;
	// Progressbar con la concentracion y el nivel de gokevolution
	private ProgressBar pBNivelConcentracion, pBNivelGokEvolutionSSJ;
	// Variable para controlar si la sesion ha sido iniciada o no. Si una animacion esta ejecutandose NO sube ni baja
	// la barra de nivel (EXCEPTO EN LAS ANIMACIONES DE REPOSO)
	public static boolean sesionEmpezada = false, animacionEjecutandose = false, juegoTerminado = false;
	private static ImageView imageCaraGokEvolution;
	private TextView txtNivelSSJ;
	 // Con esta variable compruebo si ha cambiado el nivel actual para llamar al escuchador
    // y que cambie el texto de la barra del nombre del nivel
    private int nivelActual = 0;
    // Para controlar el valor de la barra azul de 0 a 100
    private int valorProgresoBarraNivel;
    private Button btnEmpezarPausar, btnOpciones;
    // Fuentes
    //private Typeface fuenteGokEvolution;
    // Esta variable la usare para saber si al pulsar atras para salir estabamos en medio de una partida o no
    private boolean partidaPausada = false;
    
    // Contador de tiempo
    private int tiempoPartida = 0;
    private TextView txtPartida;
    // Variable si puede estar cansado o no (solo cuando empieza un nuevo nivel y supera el umbral del 30%)
    public static boolean puedeEstarCansado = false;
    public static int idMusicaActual = 0;
    private Intent musicaFondo;
    public static boolean musicaActivada, sonidosActivados;
    public static SoundPool sonido;
    public static int[] sonidosGokEvolutionNormal = new int[2], sonidosGokEvolutionSSJ1 = new int[3], sonidosGokEvolutionSSJ234 = new int[3], 
    		sonidosGokEvolutionSSJ4 = new int[2];
    
	private boolean pruebaCansancio = false;
	// Hago esto para esperar que se carguen los datos y no pete
	//private Timer simuladorCargandoDatos;
	//private ProgressDialog pDCargandoDatos;
	//private int contadorSegundosCargandoDatos = 0;
	static Display resolucion;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Pon el setContenView primero para que se puedan cargar todas las vistas bien
        resolucion = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if(resolucion.getWidth() == 1280 && (resolucion.getHeight() == 752 || resolucion.getHeight() == 800) || resolucion.getWidth() == 2560) {
        	Toast.makeText(this, "ANCHO: " + resolucion.getWidth() + ", ALTO: " + resolucion.getHeight(), 1000).show();
        	setContentView(R.layout.main_gokevolution_surface_tablet);
        }  else
        	setContentView(R.layout.main_gokevolution_surface);
        	
       

        // Toast.makeText(this, "WIDTH: " + resolucion.getWidth() + ", HEIGHT: " + resolucion.getHeight(), 1000).show();

        super.onCreate(savedInstanceState);
        // Esto es para que la pantalla SIEMPRE este encendida
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        /*** LISTENER NEUROSKY (nos suscribimos para recibir eventos) ***/
        listenersNeurosky.add(this);

        pBNivelConcentracion = (ProgressBar) findViewById(R.id.progressBarNivelConcentracion);
        pBNivelGokEvolutionSSJ = (ProgressBar) findViewById(R.id.progressBarNivelSSJ);
        sVGokEvolution = (SurfaceViewGokEvolution) findViewById(R.id.surfaceViewGokEvolution);
        imageCaraGokEvolution = (ImageView) findViewById(R.id.imageGokEvolutionCara);
        txtNivelSSJ = (TextView) findViewById(R.id.txtNivelSSJ);
        btnEmpezarPausar = (Button) findViewById(R.id.btnEmpezarPausar);
        btnOpciones = (Button) findViewById(R.id.btnOpciones);
        txtPartida = (TextView) findViewById(R.id.textoTiempo);
        //txtConcentracion = (TextView) findViewById(R.id.txtNivelConcentracion);
        //fuenteGokEvolution = Typeface.createFromAsset(getAssets(), "saiyan_sans.ttf");
        
        //txtNivelSSJ.setTypeface(fuenteGokEvolution);
        //txtConcentracion.setTypeface(fuenteGokEvolution);
        
        // Le decimos al surfaceview que seremos su escuchador
        sVGokEvolution.setOnEscuchadorDB(this);
        sVGokEvolution.setActivityDB(this);
        
        pBNivelConcentracion.setProgress(0);
        pBNivelGokEvolutionSSJ.setProgress(0);
        
        GokEvolution.reiniciarDatosGokEvolution();
        
        sonido = new SoundPool(5, AudioManager.STREAM_MUSIC , 0);
        // Cargamos los sonidos
        cargarSonidosGokEvolution(GokEvolution.nivelActual);
        /*sonidosGokEvolutionNormal[0] = sonido.load(this, R.raw.teleport, 0);
        sonidosGokEvolutionNormal[1] = sonido.load(this, R.raw.intento_ssj1, 0);
        sonidosGokEvolutionSSJ1[0] = sonido.load(this, R.raw.transformacion_ssj1, 0);
        sonidosGokEvolutionSSJ1[1] = sonido.load(this, R.raw.aura_ssj1_1, 0);
        sonidosGokEvolutionSSJ1[2] = sonido.load(this, R.raw.aura_ssj1_2, 0);
        
        sonidosGokEvolutionSSJ4[0] = sonido.load(this, R.raw.transformacion_ssj4, 0);
        sonidosGokEvolutionSSJ4[1] = sonido.load(this, R.raw.onda_vital, 0);*/
        
        btnEmpezarPausar.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				sesionEmpezada = !sesionEmpezada;
				
				if(!sesionEmpezada) {
					sesionEmpezada = false;
					showDialog(0);
				} else {
					// Al iniciar la partida ponemos el sprite actual a 0 y la animacion a mostrar
					SurfaceViewGokEvolution.animacionActual = SurfaceViewGokEvolution.tipoAnimacion.GOKEVOLUTION_NORMAL_INICIO_TELETRANSPORTE;
					SurfaceViewGokEvolution.spriteActual = 0;
					if(sonidosActivados)
						sonido.play(sonidosGokEvolutionNormal[0], 1, 1, 1, 0, 1);
					if(musicaActivada)
			    		EEG_Mindroid_GokEvolution.this.startService(musicaFondo);
					btnEmpezarPausar.setText(getString(R.string.btn_parar));
					btnOpciones.setVisibility(View.INVISIBLE);
					juegoTerminado = false;
					GokEvolution.dificultad = Integer.valueOf(preferencias.getString("dificultad", "0"));
					animacionEjecutandose = true;
					GokEvolution.reiniciarDatosGokEvolution();
					tiempoPartida = 0;
					txtPartida.setText(tiempoFormateado(tiempoPartida));
					SurfaceViewGokEvolution.spriteActual = 0;
					SurfaceViewGokEvolution.spriteOndaVital = 0;
					SurfaceViewGokEvolution.spritesIniciados = false;
					SurfaceViewGokEvolution.contadorIteracionesOndaVital = 0;
				}
			}
		});
        
        btnOpciones.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				imagenCalidadConexion.setImageResource(R.drawable.circle_red);
				Intent goOpcionesDificultad = new Intent(EEG_Mindroid_GokEvolution.this, OpcionesDificultad.class);
			    startActivity(goOpcionesDificultad);
			}
		});

        musicaFondo = new Intent(this, BackgroundMusicService.class);
        //this.startService(musicaFondo);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		this.stopService(musicaFondo);
		// Finalizamos la conexion bluetooth
		//AmarinoListenerConexionBT.onPause(this, this);
		// Lo mismo que arriba, pero hago funcion para poder llamarlo desde el boton salir ;)
		//finalizarAmarinoBT();
		
		// Desconectamos el bluetooth. No hay problema porque al iniciar en el onResume se activa con Amarino
    	//BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); 
		//mBluetoothAdapter.disable();
	}
	
	@Override
    public void onResume() {
    	super.onResume();
    	// Iniciamos la conexion bluetooth
    	//AmarinoListenerConexionBT.onResume(this, this);
    	musicaActivada = preferencias.getBoolean("musica_fondo", true);
    	sonidosActivados = preferencias.getBoolean("sonidos", true);
    	idMusicaActual = Integer.valueOf(preferencias.getString("musica_fondo_tipos", "0"));
    }
	
	@Override
    public void onDestroy() {
    	super.onDestroy();
    	tgDevice.close();
    	this.stopService(musicaFondo);
	}

	public void cambiarCaraGokEvolution(int nivel) {
		switch(nivel) {
			case 0:
				imageCaraGokEvolution.setImageResource(R.drawable.gokevolution_cara_normal);
				break;
			case 1:
				imageCaraGokEvolution.setImageResource(R.drawable.gokevolution_cara_ssj1);
				break;
			case 2:
				imageCaraGokEvolution.setImageResource(R.drawable.gokevolution_cara_ssj2);
				break;
			case 3:
				imageCaraGokEvolution.setImageResource(R.drawable.gokevolution_cara_ssj3);
				break;
			case 4:
				imageCaraGokEvolution.setImageResource(R.drawable.gokevolution_cara_ssj4);
				break;
			default:
				break;
			}
	}
	
	// Segun el nivel en el que estemos, ponderamos la barra
	public void cambiarBarraNivelGokEvolution(int nivel) {
		pBNivelGokEvolutionSSJ.setProgress(nivel);
	}

	private void cambiarTituloNivelGokEvolution(int nivel) {
		switch(nivel) {
			case 0:
				txtNivelSSJ.setText(getString(R.string.nivel_normal));
				break;
			case 1:
				txtNivelSSJ.setText(getString(R.string.nivel_ssj1));
				break;
			case 2:
				txtNivelSSJ.setText(getString(R.string.nivel_ssj2));
				break;
			case 3:
				txtNivelSSJ.setText(getString(R.string.nivel_ssj3));
				break;
			case 4:
				txtNivelSSJ.setText(getString(R.string.nivel_ssj4));
				break;
		}
	}
	
	@Override
    protected Dialog onCreateDialog(int id){
		Dialog dialogo = null;
		switch(id) {
			case 0:
				dialogo = alertDialogPararJuego();
				break;
			case 1:
				dialogo = alertDialogSalirJuego();
				break;
		}
		
        return dialogo; 
    }
	
	private AlertDialog alertDialogPararJuego() {
	    AlertDialog miAlerta = null;
	    //Se instancia el constructor de la alerta (Conext de parámetro)
	    AlertDialog.Builder miConstructor = new AlertDialog.Builder(this);
	    //Se establece el título de la alerta
	    miConstructor.setIcon(android.R.drawable.ic_lock_power_off);
	    //miConstructor.setTitle(R.string.confirmar_cierre);
	    miConstructor.setTitle(R.string.menu_parar);
	    //Agregar iconoc a la izquierda del título
	    miConstructor.setMessage(R.string.menu_parar_gokevolution);
	    //La alerta será cancelable 
	    miConstructor.setCancelable(true);

	    //Se añada el botón "Si", junto con su listener
	    miConstructor.setPositiveButton(R.string.btn_aceptar, new DialogInterface.OnClickListener() {
	       public void onClick(DialogInterface dialog, int id) {
	    	   pararPartida();
	       }
	    });
	   
	    //Se añade el botón "No", junto con su listener
	    miConstructor.setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int id) {
	    		sesionEmpezada = true;
	        }
	    });

	    //Se crea la alerta a través del constructor
	    miAlerta = miConstructor.create();
	        
	    return miAlerta; 
	 }
	
	private AlertDialog alertDialogSalirJuego() {
	    AlertDialog miAlerta = null;
	    //Se instancia el constructor de la alerta (Conext de parámetro)
	    AlertDialog.Builder miConstructor = new AlertDialog.Builder(this);
	    //Se establece el título de la alerta
	    miConstructor.setIcon(android.R.drawable.ic_lock_power_off);
	    //miConstructor.setTitle(R.string.confirmar_cierre);
	    miConstructor.setTitle(R.string.menu_salir);
	    //Agregar iconoc a la izquierda del título
	    miConstructor.setMessage(R.string.menu_salir_texto);
	    //La alerta será cancelable 
	    miConstructor.setCancelable(true);

	    //Se añada el botón "Si", junto con su listener
	    miConstructor.setPositiveButton(R.string.btn_aceptar, new DialogInterface.OnClickListener() {
	       public void onClick(DialogInterface dialog, int id) {
	    	   getApplication().stopService(musicaFondo);
	      	   //finalizarAmarinoBT();
	       	   finish();
	       	   System.exit(0);
	       }
	    });
	   
	    //Se añade el botón "No", junto con su listener
	    miConstructor.setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int id) {
	    		if(partidaPausada) {
	    			sesionEmpezada = true;
	    			partidaPausada = false;
	    		}
	    		
	        	dialog.cancel();
	        }
	    });

	    //Se crea la alerta a través del constructor
	    miAlerta = miConstructor.create();
	        
	    return miAlerta; 
	 }
	
	// Hago esto para NO salir al pulsar atras
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		
		// Si es la tecla 4 (boton atras) entonces hago esto. Lo hago asi porque al subir y bajar el
		// volumen tambien saltaba con el menu
		if(keyCode == 4) {
			if(sesionEmpezada) {
				sesionEmpezada = false;
				partidaPausada = true;
			}
			showDialog(1);
		}
		
		return false;
	}

	private void pararPartida() {
		this.stopService(musicaFondo);
		sesionEmpezada = false;
		pBNivelConcentracion.setProgress(0);
		pBNivelGokEvolutionSSJ.setProgress(0);
		btnEmpezarPausar.setText(getString(R.string.btn_empezar));
		btnOpciones.setVisibility(View.VISIBLE);
		txtNivelSSJ.setText(getString(R.string.nivel_normal));
		GokEvolution.reiniciarDatosGokEvolution();
		cambiarCaraGokEvolution(GokEvolution.nivelActual);
		tiempoPartida = 0;
		txtPartida.setText(tiempoFormateado(tiempoPartida));
		SurfaceViewGokEvolution.spriteActual = 0;
		SurfaceViewGokEvolution.spriteOndaVital = 0;
		SurfaceViewGokEvolution.spritesIniciados = false;
	}
	
	public void finalizarPartida() {
		Intent goFinPartida = new Intent(this, FinPartida.class);
		startActivity(goFinPartida);
		finish();
	}
	
	private void cargarSonidosGokEvolution(int nivel) {
		switch(nivel) {
			case 0:
				sonidosGokEvolutionNormal[0] = sonido.load(this, R.raw.teleport, 0);
		        sonidosGokEvolutionNormal[1] = sonido.load(this, R.raw.intento_ssj1, 0);
		        sonido.unload(sonidosGokEvolutionSSJ1[0]);
		        sonido.unload(sonidosGokEvolutionSSJ1[1]);
		        sonido.unload(sonidosGokEvolutionSSJ1[2]);
		        sonido.unload(sonidosGokEvolutionSSJ234[0]);
		        sonido.unload(sonidosGokEvolutionSSJ234[1]);
		        sonido.unload(sonidosGokEvolutionSSJ234[2]);
		        sonido.unload(sonidosGokEvolutionSSJ4[0]);
		        sonido.unload(sonidosGokEvolutionSSJ4[1]);
				break;
			case 1:
				sonido.unload(sonidosGokEvolutionNormal[0]);
			    sonido.unload(sonidosGokEvolutionNormal[1]);
		        sonidosGokEvolutionSSJ1[0] = sonido.load(this, R.raw.transformacion_ssj1, 0);
		        sonidosGokEvolutionSSJ1[1] = sonido.load(this, R.raw.aura_ssj1_1, 0);
		        sonidosGokEvolutionSSJ1[2] = sonido.load(this, R.raw.aura_ssj1_2, 0);
		        sonido.unload(sonidosGokEvolutionSSJ4[0]);
		        sonido.unload(sonidosGokEvolutionSSJ4[1]);
				break;
			case 2:
				sonido.unload(sonidosGokEvolutionNormal[0]);
			    sonido.unload(sonidosGokEvolutionNormal[1]);
			    sonido.unload(sonidosGokEvolutionSSJ1[0]);
		        sonido.unload(sonidosGokEvolutionSSJ1[1]);
		        sonido.unload(sonidosGokEvolutionSSJ1[2]);
		        // Lo pongo aqui solo porque tiene que pasar por aqui para llegar al nivel 2, 3 y 4
		        sonidosGokEvolutionSSJ234[0] = sonido.load(this, R.raw.transformacion_ssj23, 0);
		        sonidosGokEvolutionSSJ234[1] = sonido.load(this, R.raw.aura_ssj234_1, 0);
		        sonidosGokEvolutionSSJ234[2] = sonido.load(this, R.raw.aura_ssj234_2, 0);
		        sonido.unload(sonidosGokEvolutionSSJ4[0]);
		        sonido.unload(sonidosGokEvolutionSSJ4[1]);
				break;
			case 3:
				// No hacemos nada porque ya tenemos cargados los sonidos arriba
				break;
			case 4:
		        sonidosGokEvolutionSSJ4[0] = sonido.load(this, R.raw.transformacion_ssj4, 0);
		        sonidosGokEvolutionSSJ4[1] = sonido.load(this, R.raw.onda_vital, 0);
				break;
		}
	}
	
	private String tiempoFormateado(int tiempo){
		if(tiempo >= 0) {
			int int_mins = ((tiempo)/60);
			int int_secs = ((tiempo)%60);
			String mins = "";
			String secs = "";
			if(int_mins<10){
				mins="0"+int_mins;
			}
			else{
				mins = ""+int_mins;
			}
			
			if(int_secs<10){
				secs="0"+int_secs;
			}
			else{
				secs = ""+int_secs;
			}
			return mins+":"+secs;
		} else {
			return "00:00";
		}
	}
    
    /*** METODOS HEREDADOS DE LA INTERFAZ DE NEUROSKY ***/

	public void newSignalValue(Integer value) {
		// No hacemos nada
	}

	public void newEEGPower(TGEegPower eegPower) {
		// No hacemos nada
	}

	public void newConcentrationValue(String value) {
		if(sesionEmpezada && nivelSeñalConexion == 0) {
			txtPartida.setText(tiempoFormateado(tiempoPartida));
			tiempoPartida++;
			
			int valor = Integer.valueOf(value);

			pBNivelConcentracion.setProgress(valor);
			//pBNivelConcentracion.setProgress(Integer.valueOf(valor));
			
			// Sumamos los puntos de gokevolution
			//GokEvolution.puntosActuales += Integer.valueOf(value);
			if(!animacionEjecutandose) {
				if(valor >= 50) {
					GokEvolution.puntosActuales += GokEvolution.sumarPuntos[GokEvolution.dificultad][GokEvolution.nivelActual];
				} else {
					GokEvolution.puntosActuales -= GokEvolution.restarPuntos[GokEvolution.dificultad][GokEvolution.nivelActual];
					
					// Con estas lineas evitamos que se pueda bajar de nivel
					if(GokEvolution.nivelActual != 0) {
						if(GokEvolution.puntosActuales < GokEvolution.puntosNecesariosNiveles[GokEvolution.nivelActual-1])
							GokEvolution.puntosActuales = GokEvolution.puntosNecesariosNiveles[GokEvolution.nivelActual-1];
					} else {
						if(GokEvolution.puntosActuales < 0)
							GokEvolution.puntosActuales = 0;
					}
				}
			// Si se estan ejecutando las animaciones solo sumo a la barra azul si se aumenta de nivel
			} else {
				if(valor >= 50)
					GokEvolution.puntosActuales += GokEvolution.sumarPuntos[GokEvolution.dificultad][GokEvolution.nivelActual];
			}
			
			// Calculamos en el nivel en el que nos encontramos
			GokEvolution.nivelActual = GokEvolution.calcularNivelActual();
					
			// Si ha cambiado el nivel de gokevolution respecto al actual para
			// que cambie el texto de la barra de nivel
			if(GokEvolution.nivelActual != nivelActual) {
				nivelActual = GokEvolution.nivelActual;
				cambiarTituloNivelGokEvolution(GokEvolution.nivelActual);
				cambiarCaraGokEvolution(GokEvolution.nivelActual);
				cargarSonidosGokEvolution(GokEvolution.nivelActual);
				GokEvolution.ejecutarTransformacionSSJ();
				puedeEstarCansado = false;
			}
			
			// Ponderamos la barra de nivel en funcion del que estemos. Si el nivel es distinto de 0, restamos 
			// el valor anterior. Por ejemplo, si estamos en el nivel 1 y tenemos 120 puntos, restamos 100 que
			// es lo que hay que sumar para llegar al nivel para que se haga bien la ponderacion
			if(GokEvolution.nivelActual != 0) {
				valorProgresoBarraNivel = Integer.valueOf(
						((GokEvolution.puntosActuales-GokEvolution.puntosNecesariosNiveles[GokEvolution.nivelActual-1])*100)/((GokEvolution.nivelActual != 4) ? 
								GokEvolution.puntosNecesariosNiveles[GokEvolution.nivelActual] - GokEvolution.puntosNecesariosNiveles[GokEvolution.nivelActual-1] : 
									GokEvolution.puntosNecesariosNiveles[GokEvolution.nivelActual-1]));
				pBNivelGokEvolutionSSJ.setProgress(valorProgresoBarraNivel);
			} else {
				valorProgresoBarraNivel = Integer.valueOf(
						(GokEvolution.puntosActuales*100)/((GokEvolution.nivelActual != 4) ? 
								GokEvolution.puntosNecesariosNiveles[GokEvolution.nivelActual] : GokEvolution.puntosNecesariosNiveles[GokEvolution.nivelActual-1]));
				pBNivelGokEvolutionSSJ.setProgress(valorProgresoBarraNivel);
			}	
			//Log.d("BARRA","BARRA: PUNTOS ACTUALES: " + GokEvolution.puntosActuales + ", NIVEL: " + GokEvolution.nivelActual + " ==> " + 
					//valorProgresoBarraNivel);
			
			if(!animacionEjecutandose)
				GokEvolution.calcularAnimacionActual(valorProgresoBarraNivel);
		}
	}

	public void newRelaxValue(String value) {
		// No hacemos nada
	}

	public void newBlinkValue(String value) {
		// No hacemos nada
	}

	public void newRawValue(String value) {
		// No hacemos nada
	}
	
	/************************************************/
}
