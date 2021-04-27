package com.app.eeg_mindroid_gokevolution;

import com.app.eeg_mindroid_gokevolution.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SVGokEvolutionAnimacionCompleta extends SurfaceView {
	private Context context;
	private SurfaceHolder holder;
    private GameLoopThread gameLoopThread;
	// FPS del juego
	private int FPS = 5;
	// La primera vez que se inicia el juego aparece la animacion inicial
	private boolean inicioJuego = true;
	
	// Numero de Sprites actuales a mostrar de las animaciones
	private int spriteActual = 0;
	
    // Tamaño de los sprites (pequeño = 0, normal = 1, grande = 2)
    private int tamañoSprite = 1; // 108x180 (en dp)
    // Tamaño en pixeles de los dp de arriba
    private int pxAlto, pxAncho;
    // Establecemos las coordenadas directamente de donde dibujar, asi no hay que
    // calcularlo cada vez. Equivalen a: (bmpGokEvolutionNormalInicio[0].getWidth() / 2)
    // (bmpGokEvolutionNormalInicio[0].getHeight() / 16) respectivamente
    // El / 16 es para que se quede mas pegado al suelo
    private int anchoDibujar, altoDibujar;
    private Bitmap fondo, fondoEscalado;
    // Aqui ponderamos el valor de la barra azul que nos indica el nivel actual de gokevolution
    private int nivelBarraActualGokEvolutionPonderada;
    
    private EscuchadorEventosGokEvolution escuchadorDB;
    private Activity actividadMain;
    
    // Aqui tenemos todos los tipos de animacion disponibles para ver cual cargaremos
    private static enum tipoAnimacion {GOKEVOLUTION_NORMAL_INICIO_TELETRANSPORTE, GOKEVOLUTION_NORMAL_INICIO_PARADO,
    	GOKEVOLUTION_NORMAL_INTENTO_SSJ1, GOKEVOLUTION_NORMAL_MALA_CONCENTRACION, GOKEVOLUTION_TRANSFORMACION_SSJ1, GOKEVOLUTION_SSJ1_CANSADO,
    	GOKEVOLUTION_SSJ1_REPOSO, GOKEVOLUTION_TRANSFORMACION_SSJ2, GOKEVOLUTION_SSJ2_CANSADO, GOKEVOLUTION_SSJ2_REPOSO, GOKEVOLUTION_TRANSFORMACION_SSJ3,
    	GOKEVOLUTION_SSJ3_CANSADO, GOKEVOLUTION_SSJ3_REPOSO, GOKEVOLUTION_TRANSFORMACION_SSJ4, GOKEVOLUTION_SSJ4_REPOSO};
    private tipoAnimacion animacionActual = tipoAnimacion.GOKEVOLUTION_NORMAL_INICIO_TELETRANSPORTE;
    
    /*** IMAGENES GOKEVOLUTION ***/
	private Bitmap bmpGokEvolutionNormal[];
	private Bitmap bmpGokEvolutionSSJ1[];
	private Bitmap bmpGokEvolutionSSJ2[];
	private Bitmap bmpGokEvolutionSSJ3[];
	private Bitmap bmpGokEvolutionSSJ4[];
	
	
	public SVGokEvolutionAnimacionCompleta(Context context) {
	    super(context);
	}
	
    public SVGokEvolutionAnimacionCompleta(Context ctx, AttributeSet attrs){
		super(ctx, attrs);
		context = ctx;

		gameLoopThread = new GameLoopThread(this);
		holder = getHolder();
		holder.setFormat(PixelFormat.TRANSLUCENT);
		holder.addCallback(new SurfaceHolder.Callback() {
			
			public void surfaceDestroyed(SurfaceHolder holder) {
				boolean retry = true;
				gameLoopThread.setRunning(false);
				while(retry) {
					try {
						gameLoopThread.join();
						retry = false;
					} catch(InterruptedException e) {}
				}
			}
			
			public void surfaceCreated(SurfaceHolder holder) {
				// Esto lo usamos para dibujar el fondo con el tatami
				// Esto lo usamos para dibujar el fondo con el tatami
				fondo = BitmapFactory.decodeResource(getResources(), R.drawable.tatami);
				float scaleWidth = (float)fondo.getWidth()/(float)getWidth();
			    float scaleHeight = (float)fondo.getHeight()/(float)getHeight();
			    int newWidth = Math.round(fondo.getWidth()/scaleWidth);
			    int newHeight = Math.round(fondo.getHeight()/scaleHeight);
			    fondoEscalado = Bitmap.createScaledBitmap(fondo, newWidth, newHeight, true);
				gameLoopThread.setRunning(true);
				gameLoopThread.start();
			}
			
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {}
		});
	}
    
    @Override
	protected void onDraw(Canvas canvas) {
    	canvas.drawBitmap(fondoEscalado, 0, 0, null);
    	canvas.drawBitmap(mostrarImagen(), (getWidth() / 2) - anchoDibujar,
				(getHeight() / 2) - altoDibujar, null);
    }
	
	class GameLoopThread extends Thread {
		private SVGokEvolutionAnimacionCompleta view;
		private boolean running = false;
		
		public GameLoopThread(SVGokEvolutionAnimacionCompleta view) {
			this.view = view;
		}
		
		public void setRunning(boolean run) {
			running = run;
		}
		
		@Override
		public void run() {
			long ticksPS = 1000 / FPS;
            long startTime;
            long sleepTime;
            
			while(running) {
				Canvas c = null;
				startTime = System.currentTimeMillis();
				try {
					c = view.getHolder().lockCanvas();
					synchronized (view.getHolder()) {
						view.onDraw(c);
					}
				} finally {
					if(c != null)
						view.getHolder().unlockCanvasAndPost(c);
				}
				sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
				try {
					if(sleepTime > 0)
						sleep(sleepTime);
					else
						sleep(10);
				} catch(Exception e) {}
			}
		}
	}
	
	private Bitmap mostrarImagen() {
		switch(animacionActual) {
			case GOKEVOLUTION_NORMAL_INICIO_TELETRANSPORTE:
				iniciarSpritesGokEvolutionNormal();
				return animacionGokEvolutionNormalInicioTeletransporte();
			case GOKEVOLUTION_NORMAL_MALA_CONCENTRACION:
				return animacionGokEvolutionNormalMalaConcentracion();
			case GOKEVOLUTION_NORMAL_INICIO_PARADO:
				return animacionGokEvolutionNormalInicioParado();
			case GOKEVOLUTION_NORMAL_INTENTO_SSJ1:
				return animacionGokEvolutionNormalIntentoSSJ1();
			case GOKEVOLUTION_TRANSFORMACION_SSJ1:
				iniciarSpritesGokEvolutionSSJ1();
				return animacionGokEvolutionTransformacionSSJ1();
			case GOKEVOLUTION_SSJ1_CANSADO:
				return animacionGokEvolutionSSJ1Cansado();
			case GOKEVOLUTION_SSJ1_REPOSO:
				return animacionGokEvolutionSSJ1Reposo();
			case GOKEVOLUTION_TRANSFORMACION_SSJ2:
				iniciarSpritesGokEvolutionSSJ2();
				return animacionGokEvolutionTransformacionSSJ2();
			case GOKEVOLUTION_SSJ2_CANSADO:
				return animacionGokEvolutionSSJ2Cansado();
			case GOKEVOLUTION_SSJ2_REPOSO:
				return animacionGokEvolutionSSJ2Reposo();
			case GOKEVOLUTION_TRANSFORMACION_SSJ3:
				iniciarSpritesGokEvolutionSSJ3();
				return animacionGokEvolutionTransformacionSSJ3();
			case GOKEVOLUTION_SSJ3_CANSADO:
				return animacionGokEvolutionSSJ3Cansado();
			case GOKEVOLUTION_SSJ3_REPOSO:
				return animacionGokEvolutionSSJ3Reposo();
			case GOKEVOLUTION_TRANSFORMACION_SSJ4:
				iniciarSpritesGokEvolutionSSJ4();
				return animacionGokEvolutionTransformacionSSJ4();
			case GOKEVOLUTION_SSJ4_REPOSO:
				return animacionGokEvolutionSSJ4Reposo();
			default:
				iniciarSpritesGokEvolutionNormal();
				return animacionGokEvolutionNormalInicioTeletransporte();
		}
	}
	
	/*** INICIALIZACIONES Y DESTRUCCIONES DE SPRITES ***/ 
	private void iniciarSpritesGokEvolutionNormal() {
		bmpGokEvolutionNormal = new Bitmap[9];
		bmpGokEvolutionSSJ1 = null;
		bmpGokEvolutionSSJ2 = null;
		bmpGokEvolutionSSJ3 = null;
		bmpGokEvolutionSSJ4 = null;
		
		bmpGokEvolutionNormal[0] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionnormal_teletransporte1);
		bmpGokEvolutionNormal[1] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionnormal_teletransporte2);
		bmpGokEvolutionNormal[2] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionnormal_teletransporte3);
		bmpGokEvolutionNormal[3] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionnormal_teletransporte4);
		bmpGokEvolutionNormal[4] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionnormal_esperando1);
		bmpGokEvolutionNormal[5] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionnormal_esperando2);
		bmpGokEvolutionNormal[6] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionnormal_esperando3);
		bmpGokEvolutionNormal[7] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionnormal_inicio1);
		bmpGokEvolutionNormal[8] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionnormal_inicio2);
		
		// Establecemos el tamaño del muñeco (Nos valdra para todas las animaciones restantes)
		pxAlto = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics());
		pxAncho = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 108, getResources().getDisplayMetrics());
		
		// Escalamos las imagenes
		for(int i=0;i<bmpGokEvolutionNormal.length;i++)
			bmpGokEvolutionNormal[i] = Bitmap.createScaledBitmap(bmpGokEvolutionNormal[i], pxAncho, pxAlto, true);
		
		// Establecemos la posicion donde se dibujara tomando de ejemplo un sprite
		anchoDibujar = (bmpGokEvolutionNormal[0].getWidth() / 2);
		altoDibujar = (bmpGokEvolutionNormal[0].getHeight() / 16);
	}
	
	private void iniciarSpritesGokEvolutionSSJ1() {
		bmpGokEvolutionNormal = null;
		bmpGokEvolutionSSJ1 = new Bitmap[12];
		bmpGokEvolutionSSJ2 = null;
		bmpGokEvolutionSSJ3 = null;
		bmpGokEvolutionSSJ4 = null;
		
		bmpGokEvolutionSSJ1[0] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj1_transformacion1);
		bmpGokEvolutionSSJ1[1] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj1_transformacion2);
		bmpGokEvolutionSSJ1[2] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj1_transformacion3);
		bmpGokEvolutionSSJ1[3] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj1_transformacion4);
		bmpGokEvolutionSSJ1[4] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj1_transformacion5);
		bmpGokEvolutionSSJ1[5] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj1_transformacion6);
		bmpGokEvolutionSSJ1[6] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj1_cansado1);
		bmpGokEvolutionSSJ1[7] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj1_cansado2);
		bmpGokEvolutionSSJ1[8] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj1_cansado3);
		bmpGokEvolutionSSJ1[9] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj1_reposo1);
		bmpGokEvolutionSSJ1[10] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj1_reposo2);
		bmpGokEvolutionSSJ1[11] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj1_reposo3);
		
		for(int i=0;i<bmpGokEvolutionSSJ1.length;i++)
			bmpGokEvolutionSSJ1[i] = Bitmap.createScaledBitmap(bmpGokEvolutionSSJ1[i], pxAncho, pxAlto, true);
	}
	
	private void iniciarSpritesGokEvolutionSSJ2() {
		bmpGokEvolutionNormal = null;
		bmpGokEvolutionSSJ1 = null;
		bmpGokEvolutionSSJ2 = new Bitmap[32];
		bmpGokEvolutionSSJ3 = null;
		bmpGokEvolutionSSJ4 = null;
		
		bmpGokEvolutionSSJ2[0] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_transformacion1);
		bmpGokEvolutionSSJ2[1] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_transformacion2);
		bmpGokEvolutionSSJ2[2] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_transformacion3);
		bmpGokEvolutionSSJ2[3] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_transformacion4);
		bmpGokEvolutionSSJ2[4] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_transformacion5);
		bmpGokEvolutionSSJ2[5] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_transformacion6);
		bmpGokEvolutionSSJ2[6] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_transformacion7);
		bmpGokEvolutionSSJ2[7] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_transformacion8);
		bmpGokEvolutionSSJ2[8] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_transformacion9);
		bmpGokEvolutionSSJ2[9] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_transformacion10);
		bmpGokEvolutionSSJ2[10] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_transformacion11);
		bmpGokEvolutionSSJ2[11] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_transformacion12);
		bmpGokEvolutionSSJ2[12] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_transformacion13);
		bmpGokEvolutionSSJ2[13] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_cansado1);
		bmpGokEvolutionSSJ2[14] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_cansado2);
		bmpGokEvolutionSSJ2[15] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_cansado3);
		bmpGokEvolutionSSJ2[16] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_cansado4);
		bmpGokEvolutionSSJ2[17] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_cansado5);
		bmpGokEvolutionSSJ2[18] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_cansado6);
		bmpGokEvolutionSSJ2[19] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_reposo1);
		bmpGokEvolutionSSJ2[20] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_reposo2);
		bmpGokEvolutionSSJ2[21] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_reposo3);
		bmpGokEvolutionSSJ2[22] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_reposo4);
		bmpGokEvolutionSSJ2[23] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_reposo5);
		bmpGokEvolutionSSJ2[24] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_reposo6);
		bmpGokEvolutionSSJ2[25] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_reposo7);
		bmpGokEvolutionSSJ2[26] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_reposo8);
		bmpGokEvolutionSSJ2[27] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_reposo9);
		bmpGokEvolutionSSJ2[28] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_reposo10);
		bmpGokEvolutionSSJ2[29] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_reposo11);
		bmpGokEvolutionSSJ2[30] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_reposo12);
		bmpGokEvolutionSSJ2[31] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj2_reposo13);
		
		for(int i=0;i<bmpGokEvolutionSSJ2.length;i++)
			bmpGokEvolutionSSJ2[i] = Bitmap.createScaledBitmap(bmpGokEvolutionSSJ2[i], pxAncho, pxAlto, true);
	}
	
	private void iniciarSpritesGokEvolutionSSJ3() {
		bmpGokEvolutionNormal = null;
		bmpGokEvolutionSSJ1 = null;
		bmpGokEvolutionSSJ2 = null;
		bmpGokEvolutionSSJ3 = new Bitmap[30];
		bmpGokEvolutionSSJ4 = null;
		
		bmpGokEvolutionSSJ3[0] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_transformacion1);
		bmpGokEvolutionSSJ3[1] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_transformacion2);
		bmpGokEvolutionSSJ3[2] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_transformacion3);
		bmpGokEvolutionSSJ3[3] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_transformacion4);
		bmpGokEvolutionSSJ3[4] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_transformacion5);
		bmpGokEvolutionSSJ3[5] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_transformacion6);
		bmpGokEvolutionSSJ3[6] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_transformacion7);
		bmpGokEvolutionSSJ3[7] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_transformacion8);
		bmpGokEvolutionSSJ3[8] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_transformacion9);
		bmpGokEvolutionSSJ3[9] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_transformacion10);
		bmpGokEvolutionSSJ3[10] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_transformacion11);
		bmpGokEvolutionSSJ3[11] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_transformacion12);
		bmpGokEvolutionSSJ3[12] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_transformacion13);
		bmpGokEvolutionSSJ3[13] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_transformacion14);
		bmpGokEvolutionSSJ3[14] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_transformacion15);
		bmpGokEvolutionSSJ3[15] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_transformacion16);
		bmpGokEvolutionSSJ3[16] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_transformacion17);
		bmpGokEvolutionSSJ3[17] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_transformacion18);
		bmpGokEvolutionSSJ3[18] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_cansado1);
		bmpGokEvolutionSSJ3[19] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_cansado2);
		bmpGokEvolutionSSJ3[20] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_cansado3);
		bmpGokEvolutionSSJ3[21] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_cansado4);
		bmpGokEvolutionSSJ3[22] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_cansado5);
		bmpGokEvolutionSSJ3[23] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_cansado6);
		bmpGokEvolutionSSJ3[24] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_reposo1);
		bmpGokEvolutionSSJ3[25] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_reposo2);
		bmpGokEvolutionSSJ3[26] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_reposo3);
		bmpGokEvolutionSSJ3[27] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_reposo4);
		bmpGokEvolutionSSJ3[28] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_reposo5);
		bmpGokEvolutionSSJ3[29] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj3_reposo6);
		
		for(int i=0;i<bmpGokEvolutionSSJ3.length;i++)
			bmpGokEvolutionSSJ3[i] = Bitmap.createScaledBitmap(bmpGokEvolutionSSJ3[i], pxAncho, pxAlto, true);
	}
	
	private void iniciarSpritesGokEvolutionSSJ4() {
		bmpGokEvolutionNormal = null;
		bmpGokEvolutionSSJ1 = null;
		bmpGokEvolutionSSJ2 = null;
		bmpGokEvolutionSSJ3 = null;
		bmpGokEvolutionSSJ4 = new Bitmap[37];
		
		bmpGokEvolutionSSJ4[0] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion1);
		bmpGokEvolutionSSJ4[1] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion2);
		bmpGokEvolutionSSJ4[2] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion3);
		bmpGokEvolutionSSJ4[3] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion4);
		bmpGokEvolutionSSJ4[4] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion5);
		bmpGokEvolutionSSJ4[5] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion6);
		bmpGokEvolutionSSJ4[6] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion7);
		bmpGokEvolutionSSJ4[7] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion8);
		bmpGokEvolutionSSJ4[8] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion9);
		bmpGokEvolutionSSJ4[9] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion10);
		bmpGokEvolutionSSJ4[10] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion11);
		bmpGokEvolutionSSJ4[11] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion12);
		bmpGokEvolutionSSJ4[12] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion13);
		bmpGokEvolutionSSJ4[13] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion14);
		bmpGokEvolutionSSJ4[14] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion15);
		bmpGokEvolutionSSJ4[15] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion16);
		bmpGokEvolutionSSJ4[16] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion17);
		bmpGokEvolutionSSJ4[17] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion18);
		bmpGokEvolutionSSJ4[18] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion19);
		bmpGokEvolutionSSJ4[19] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion20);
		bmpGokEvolutionSSJ4[20] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion21);
		bmpGokEvolutionSSJ4[21] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion22);
		bmpGokEvolutionSSJ4[22] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion23);
		bmpGokEvolutionSSJ4[23] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_transformacion24);
		bmpGokEvolutionSSJ4[24] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_reposo1);
		bmpGokEvolutionSSJ4[25] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_reposo2);
		bmpGokEvolutionSSJ4[26] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_reposo3);
		bmpGokEvolutionSSJ4[27] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_reposo4);
		bmpGokEvolutionSSJ4[28] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_reposo5);
		bmpGokEvolutionSSJ4[29] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_reposo6);
		bmpGokEvolutionSSJ4[30] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_reposo7);
		bmpGokEvolutionSSJ4[31] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_reposo8);
		bmpGokEvolutionSSJ4[32] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_reposo9);
		bmpGokEvolutionSSJ4[33] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_reposo10);
		bmpGokEvolutionSSJ4[34] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_reposo11);
		bmpGokEvolutionSSJ4[35] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_reposo12);
		bmpGokEvolutionSSJ4[36] = BitmapFactory.decodeResource(getResources(), R.drawable.gokevolutionssj4_reposo13);
		
		for(int i=0;i<bmpGokEvolutionSSJ4.length;i++)
			bmpGokEvolutionSSJ4[i] = Bitmap.createScaledBitmap(bmpGokEvolutionSSJ4[i], pxAncho, pxAlto, true);
	}
	
	/*** ANIMACIONES ***/
	private Bitmap animacionGokEvolutionNormalInicioTeletransporte() {
		//Log.d("ANIMACION","ANIMACION: animacionGokEvolutionNormalInicioTeletransporte");
		// Cargamos el siguiente sprite
		spriteActual++;
		
		if(spriteActual == 1) {
			if(escuchadorDB != null) {
				actividadMain.runOnUiThread(new Runnable() {
					
					public void run() {
						escuchadorDB.cambiarCaraGokEvolution(0);
					}
				});
			}
		}
		
		// El 4 (realmente la 4-1) es cuando finaliza la animacion de inicio y dejamos lista la de intento
		// de ssj1 que seria la siguiente cuando se de el caso
		if(spriteActual >= 4)
			animacionActual = tipoAnimacion.GOKEVOLUTION_NORMAL_MALA_CONCENTRACION;
		// Devolvemos el anterior sprite
		return bmpGokEvolutionNormal[spriteActual-1];
	}
	
	private Bitmap animacionGokEvolutionNormalMalaConcentracion() {
		spriteActual++;
		//Log.d("ANIMACION","ANIMACION: animacionGokEvolutionNormalMalaConcentracion: " + spriteActual);
		
		if(spriteActual >= 7)
			animacionActual = tipoAnimacion.GOKEVOLUTION_NORMAL_INICIO_PARADO;
		
		return bmpGokEvolutionNormal[spriteActual-1];
	}
	
	// Esta es un UNICO sprite
	private Bitmap animacionGokEvolutionNormalInicioParado() {
		animacionActual = tipoAnimacion.GOKEVOLUTION_NORMAL_INTENTO_SSJ1;
			
		return bmpGokEvolutionNormal[7];
	}
	
	private Bitmap animacionGokEvolutionNormalIntentoSSJ1() {
		//Log.d("ANIMACION","ANIMACION: animacionGokEvolutionNormalIntentoSSJ1");
		spriteActual++;
		
		if(spriteActual >= 9) {
			animacionActual = tipoAnimacion.GOKEVOLUTION_TRANSFORMACION_SSJ1;
			spriteActual = 0;
			
			return bmpGokEvolutionNormal[bmpGokEvolutionNormal.length-1];
		}
		
		return bmpGokEvolutionNormal[spriteActual-1];
	}
	
	private Bitmap animacionGokEvolutionTransformacionSSJ1() {
		//Log.d("ANIMACION","ANIMACION: animacionGokEvolutionTransformacionSSJ1");
		spriteActual++;
		
		if(spriteActual >= 6) {
			animacionActual = tipoAnimacion.GOKEVOLUTION_SSJ1_REPOSO;
			if(escuchadorDB != null) {
				actividadMain.runOnUiThread(new Runnable() {
					
					public void run() {
						escuchadorDB.cambiarCaraGokEvolution(1);
					}
				});
			}
		}
			
		return bmpGokEvolutionSSJ1[spriteActual-1];
	}
	
	private Bitmap animacionGokEvolutionSSJ1Cansado() {
		spriteActual++;
		//Log.d("ANIMACION","ANIMACION: animacionGokEvolutionSSJ1Reposo: " + spriteActual);
		
		if(spriteActual >= 9) {
			animacionActual = tipoAnimacion.GOKEVOLUTION_SSJ1_CANSADO;
			spriteActual = 0;
			return bmpGokEvolutionSSJ1[bmpGokEvolutionSSJ1.length-1];
		}
		
		return bmpGokEvolutionSSJ1[spriteActual-1];
	}
	
	private Bitmap animacionGokEvolutionSSJ1Reposo() {
		spriteActual++;
		//Log.d("ANIMACION","ANIMACION: animacionGokEvolutionSSJ1Reposo: " + spriteActual);
		
		if(spriteActual >= 12) {
			animacionActual = tipoAnimacion.GOKEVOLUTION_TRANSFORMACION_SSJ2;
			spriteActual = 0;
			return bmpGokEvolutionSSJ1[bmpGokEvolutionSSJ1.length-1];
		}
		
		return bmpGokEvolutionSSJ1[spriteActual-1];
	}
	
	private Bitmap animacionGokEvolutionTransformacionSSJ2() {
		//Log.d("ANIMACION","ANIMACION: animacionGokEvolutionTransformacionSSJ1");
		spriteActual++;
		
		if(spriteActual >= 13) {
			animacionActual = tipoAnimacion.GOKEVOLUTION_SSJ2_CANSADO;
			
			if(escuchadorDB != null) {
				actividadMain.runOnUiThread(new Runnable() {
					
					public void run() {
						escuchadorDB.cambiarCaraGokEvolution(2);
					}
				});
			}
			
			return bmpGokEvolutionSSJ2[12];
		}
			
		return bmpGokEvolutionSSJ2[spriteActual-1];
	}
	
	private Bitmap animacionGokEvolutionSSJ2Cansado() {
		spriteActual++;
		//Log.d("ANIMACION","ANIMACION: animacionGokEvolutionSSJ1Reposo: " + spriteActual);
		
		if(spriteActual >= 19) {
			animacionActual = tipoAnimacion.GOKEVOLUTION_SSJ2_REPOSO;
			return bmpGokEvolutionSSJ2[18];
		}
		
		return bmpGokEvolutionSSJ2[spriteActual-1];
	}
	
	private Bitmap animacionGokEvolutionSSJ2Reposo() {
		spriteActual++;
		//Log.d("ANIMACION","ANIMACION: animacionGokEvolutionSSJ1Reposo: " + spriteActual);
		
		if(spriteActual >= 32) {
			animacionActual = tipoAnimacion.GOKEVOLUTION_TRANSFORMACION_SSJ3;
			spriteActual = 0;
			return bmpGokEvolutionSSJ2[bmpGokEvolutionSSJ2.length-1];
		}
		
		return bmpGokEvolutionSSJ2[spriteActual-1];
	}
	
	private Bitmap animacionGokEvolutionTransformacionSSJ3() {
		//Log.d("ANIMACION","ANIMACION: animacionGokEvolutionTransformacionSSJ1");
		spriteActual++;
		
		if(spriteActual >= 18) {
			animacionActual = tipoAnimacion.GOKEVOLUTION_SSJ3_CANSADO;
			
			if(escuchadorDB != null) {
				actividadMain.runOnUiThread(new Runnable() {
					
					public void run() {
						escuchadorDB.cambiarCaraGokEvolution(3);
					}
				});
			}
			
			return bmpGokEvolutionSSJ3[17];
		}
			
		return bmpGokEvolutionSSJ3[spriteActual-1];
	}
	
	private Bitmap animacionGokEvolutionSSJ3Cansado() {
		spriteActual++;
		//Log.d("ANIMACION","ANIMACION: animacionGokEvolutionSSJ1Reposo: " + spriteActual);
		
		if(spriteActual >= 24) {
			animacionActual = tipoAnimacion.GOKEVOLUTION_SSJ3_REPOSO;
			return bmpGokEvolutionSSJ3[23];
		}
		
		return bmpGokEvolutionSSJ3[spriteActual-1];
	}
	
	private Bitmap animacionGokEvolutionSSJ3Reposo() {
		spriteActual++;
		//Log.d("ANIMACION","ANIMACION: animacionGokEvolutionSSJ1Reposo: " + spriteActual);
		
		if(spriteActual >= 30) {
			animacionActual = tipoAnimacion.GOKEVOLUTION_TRANSFORMACION_SSJ4;
			spriteActual = 0;
			return bmpGokEvolutionSSJ3[bmpGokEvolutionSSJ3.length-1];
		}
		
		return bmpGokEvolutionSSJ3[spriteActual-1];
	}
	
	private Bitmap animacionGokEvolutionTransformacionSSJ4() {
		//Log.d("ANIMACION","ANIMACION: animacionGokEvolutionTransformacionSSJ1");
		spriteActual++;
		
		if(spriteActual >= 24) {
			animacionActual = tipoAnimacion.GOKEVOLUTION_SSJ4_REPOSO;
			
			if(escuchadorDB != null) {
				actividadMain.runOnUiThread(new Runnable() {
					
					public void run() {
						escuchadorDB.cambiarCaraGokEvolution(4);
					}
				});
			}
			
			return bmpGokEvolutionSSJ4[23];
		}
			
		return bmpGokEvolutionSSJ4[spriteActual-1];
	}
	
	private Bitmap animacionGokEvolutionSSJ4Reposo() {
		spriteActual++;
		//Log.d("ANIMACION","ANIMACION: animacionGokEvolutionSSJ1Reposo: " + spriteActual);
		
		if(spriteActual >= 37) {
			animacionActual = tipoAnimacion.GOKEVOLUTION_NORMAL_INICIO_TELETRANSPORTE;
			spriteActual = 0;
			return bmpGokEvolutionSSJ4[bmpGokEvolutionSSJ4.length-1];
		}
		
		return bmpGokEvolutionSSJ4[spriteActual-1];
	}
	
	/*** ESCUCHADOR DE EVENTOS ***/
	public void setOnEscuchadorDB(EscuchadorEventosGokEvolution escuchador) {
		this.escuchadorDB = escuchador;
	}
	
	/*** PASO LA ACTIVIDAD PARA PODERLA LLAMAR EN SEGUNDO PLANO EN EL ESCUCHADOR DE EVENTOS ***/
	public void setActivityDB(Activity actividad) {
		actividadMain = actividad;
	}
}
