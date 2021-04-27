package com.app.eeg_mindroid_gokevolution;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.app.eeg_mindroid_gokevolution.R;

public class PreferenciasBaseDatos extends PreferenceActivity{
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragmentEntranmiento()).commit();
	}
	
	public static class MyPreferenceFragmentEntranmiento extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferencias_bbdd);
        }
    }
}