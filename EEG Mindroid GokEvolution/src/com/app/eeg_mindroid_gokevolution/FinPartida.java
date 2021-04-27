package com.app.eeg_mindroid_gokevolution;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;

public class FinPartida extends Activity{
	private Timer timerSplash;
	private int contadorSegundos = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Pon el setContenView primero para que se puedan cargar todas las vistas bien
		setContentView(R.layout.fin_partida);
        super.onCreate(savedInstanceState);
        // Esto es para que la pantalla SIEMPRE este encendida
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        timerSplash = new Timer();
        
        timerSplash.schedule(new TimerTask() {
			
			@Override
			public void run() {
				contadorSegundos++;
				if(contadorSegundos == 5)
					FinPartida.this.runOnUiThread(new Runnable() {
						
						public void run() {
							Intent goMain = new Intent(FinPartida.this, EEG_Mindroid_GokEvolution.class);
							startActivity(goMain);
							timerSplash.cancel();
						}
					});
			}
		}, 0, 1000);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		return false;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
