package com.app.eeg_mindroid_gokevolution;

import java.util.ArrayList;
import java.util.List;

public class DatosEEG {
	// Metodo publicos para ganar velocidad
	public static List<Integer> sesion;
	public static List<Integer> segundo;
	public static List<Integer> reposo;
	public static List<Integer> concentracion;
	public static List<Integer> relajacion;
	public static List<Integer> delta;
	public static List<Integer> theta;
	public static List<Integer> lowAlpha;
	public static List<Integer> highAlpha;
	public static List<Integer> lowBeta;
	public static List<Integer> highBeta;
	public static List<Integer> lowGamma;
	public static List<Integer> highGamma;
	
	public static void iniciarDatos() {
		sesion = new ArrayList<Integer>();
		segundo = new ArrayList<Integer>();
		reposo = new ArrayList<Integer>();
		concentracion = new ArrayList<Integer>();
		relajacion = new ArrayList<Integer>();
		delta = new ArrayList<Integer>();
		theta = new ArrayList<Integer>();
		lowAlpha = new ArrayList<Integer>();
		highAlpha = new ArrayList<Integer>();
		lowBeta = new ArrayList<Integer>();
		highBeta = new ArrayList<Integer>();
		lowGamma = new ArrayList<Integer>();
		highGamma = new ArrayList<Integer>();
	}
	
	public static void reiniciarDatos() {
		sesion.clear();
		segundo.clear();
		reposo.clear();
		concentracion.clear();
		relajacion.clear();
		delta.clear();
		theta.clear();
		lowAlpha.clear();
		highAlpha.clear();
		lowBeta.clear();
		highBeta.clear();
		lowGamma.clear();
		highGamma.clear();
	}
}
