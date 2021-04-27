package com.app.eeg_mindroid_gokevolution;

import java.util.List;

public interface EEG_MindroidBDInterface {
	public void guardarDatosSesionActual();
	public void reiniciarDatos();
	public String[] listarDatos();
	public void borrarAlgunosDatos();
	/*public void reiniciarJuego();
	public List<Integer> getDatosLaberinto(int id, int nivel);
	public List<String> getDatosLaberintoUsuario(int id, int nivel);
	public void actualizarLaberintoUsuario(int id, int nivel, String laberintoGuardado, Integer sumaActual, 
			Integer tiempoActual, Integer movimientosTotales, String laberintoFilasSeleccionadas, 
			String laberintoColumnasSeleccionadas, Integer calificacion, Integer puntuacion);
	public void actualizarLaberintoGenerado(String laberintoGenerado, String laberintoGeneradoGuardado, Integer filas, 
			Integer columnas, Integer oro, Integer sumaActual, String laberintoFilasSeleccionadas, 
			String laberintoColumnasSeleccionadas, Integer tiempoActual, Integer calificacion);
	public List<Integer> getNumeroOros();
	public List<Integer> getNumeroPlatas();
	public List<Integer> getNumeroBronces();
	public int getTiempoTotalJugado();
	public int getPuntosTotales();
	public int getMovimientosTotales();
	public String getTableroLimpio(int nivel, int id);
	public void eliminarTableroGenerado();*/
}
