package com.app.eeg_mindroid_gokevolution;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class OpcionesDificultad extends PreferenceActivity{
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragmentGeneral()).commit();
	}
	
	public static class MyPreferenceFragmentGeneral extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferencias_opciones);
        }
    }
}