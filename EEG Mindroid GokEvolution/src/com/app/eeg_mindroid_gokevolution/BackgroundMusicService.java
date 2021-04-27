package com.app.eeg_mindroid_gokevolution;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class BackgroundMusicService extends Service {
	public static MediaPlayer musica;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
    public void onCreate() {
        super.onCreate();
        musica = MediaPlayer.create(this, nuevaMusica(EEG_Mindroid_GokEvolution.idMusicaActual));
        musica.setVolume(50,50);
    }
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		musica.setLooping(true); // Set looping
        musica.setVolume(50,50);
        musica.start();
        
        return START_STICKY;
    }

    public void onStart(Intent intent, int startId) {
    	
    }
    public IBinder onUnBind(Intent arg0) {
        return null;
    }

    public void onStop() {

    }
    public void onPause() {
    	musica.pause();
    }
    @Override
    public void onDestroy() {
    	musica.stop();
    	musica.release();
    }

    @Override
    public void onLowMemory() {

    }
    
    private int nuevaMusica(int idMusica) {
    	switch(idMusica) {
	    	case 0:
	    		return R.raw.celula_1;
	    	case 1:
	    		return R.raw.celula_2;
	    	case 2:
	    		return R.raw.celula_3;
	    	case 3:
	    		return R.raw.freezer;
	    	case 4:
	    		return R.raw.resumen_capitulo;
	    	case 5:
	    		return R.raw.majin_boo;
	    	case 6:
	    		return R.raw.gokevolution_entreno_kamisama;
	    	case 7:
	    		return R.raw.vegeta;
	    	case 8:
	    		return R.raw.ejercito_rojo;
	    	case 9:
	    		return R.raw.piccolo;
	    	default:
	    		return R.raw.celula_1;
    	}
    }
}
