package com.app.eeg_mindroid_gokevolution;

public class GokEvolution {
	// Nivel actual de GokEvolution
	public static Integer nivelActual;
	// Puntos actuales
	public static Integer puntosActuales;
	// Puntos a sumar en funcion de cada nivel si concentracion >= 50% (facil, normal, dificil)
	public static Integer sumarPuntos[][] = {{18, 16, 14, 12, 10},{15, 13, 11, 9, 7},{12, 10, 8, 6, 5}};
	// Puntos a restar en funcion de cada nivel si concentracion < 50% (facil, normal, dificil)
	public static Integer restarPuntos[][] = {{2, 3, 4, 5, 6}, {3, 4, 5, 6, 7}, {5, 6, 7, 8, 9}};
	// Puntos para alcanzar cada estado
	public static Integer puntosNecesariosNiveles[] = {100, 250, 450, 700, 900};
	//public static Integer puntosNecesariosNiveles[] = {50, 150, 200, 250, 320};
	/*** TIEMPO EN SEGUNDOS NECESARIO PARA ALCANZAR CADA NIVEL SUPONIENDO PARTIDA PERFECTA ***/
	/*
	 * FACIL: 120s = 2min y 0s
	 * NORMAL: 163s = 2min y 43s
	 * DIFICIL: 229s = 3min y 49s
	 */
	// Dificultad 0 (facil), 1 (normal), 2 (dificil)
	public static Integer dificultad = 0;
	
	// Con estas dos variables cuento el tiempo que estara el primer y segundo sonido del aura
	private static boolean primerAura = true;
	private static int contadorAura = 0;
	
	public static void reiniciarDatosGokEvolution() {
		nivelActual = 0;
		puntosActuales = 0;
	}
	
	public static Integer calcularNivelActual() {
		Integer nivel;
		
		// 100
		if(puntosActuales < puntosNecesariosNiveles[0]) {
			nivel = 0;
		// 250
		} else if(puntosActuales >= puntosNecesariosNiveles[0] && puntosActuales < puntosNecesariosNiveles[1]) {
			nivel = 1;
		} else if(puntosActuales >= puntosNecesariosNiveles[1] && puntosActuales < puntosNecesariosNiveles[2]) {
			nivel = 2;
		} else if(puntosActuales >= puntosNecesariosNiveles[2] && puntosActuales < puntosNecesariosNiveles[3]) {
			nivel = 3;
		} else {
			nivel = 4;
		}
		
		return nivel;
	}
	
	public static void calcularAnimacionActual(int valorNivelSSJ) {
		// Cuando empieza un nuevo nivel esta falso hasta que llegue al 30% o mas
		if(!EEG_Mindroid_GokEvolution.puedeEstarCansado && valorNivelSSJ >= 30)
			EEG_Mindroid_GokEvolution.puedeEstarCansado = true;
		
		switch(nivelActual) {
			case 0:
				animacionesGokEvolutionNormal(valorNivelSSJ);
				break;
			case 1:
				animacionesGokEvolutionSSJ1(valorNivelSSJ);
				break;
			case 2:
				animacionesGokEvolutionSSJ2(valorNivelSSJ);
				break;
			case 3:
				animacionesGokEvolutionSSJ3(valorNivelSSJ);
				break;
			case 4:
				animacionesGokEvolutionSSJ4(valorNivelSSJ);
				break;
		}
	}
	
	private static void animacionesGokEvolutionNormal(int valorNivelSSJ) {
		SurfaceViewGokEvolution.spriteActual = 7;
		if(valorNivelSSJ > 54)
			SurfaceViewGokEvolution.animacionActual = SurfaceViewGokEvolution.tipoAnimacion.GOKEVOLUTION_NORMAL_INTENTO_SSJ1;
		else
			SurfaceViewGokEvolution.animacionActual = SurfaceViewGokEvolution.tipoAnimacion.GOKEVOLUTION_NORMAL_INICIO_PARADO;
	
	}
	
	private static void animacionesGokEvolutionSSJ1(int valorNivelSSJ) {
		if(valorNivelSSJ > 60) {
			if(primerAura) {
				if(contadorAura == 0) {
					if(EEG_Mindroid_GokEvolution.sonidosActivados)
						EEG_Mindroid_GokEvolution.sonido.play(EEG_Mindroid_GokEvolution.sonidosGokEvolutionSSJ1[1], 1, 1, 1, 0, 1);
				} else if(contadorAura == 1) {
					primerAura = false;
					if(EEG_Mindroid_GokEvolution.sonidosActivados)
						EEG_Mindroid_GokEvolution.sonido.play(EEG_Mindroid_GokEvolution.sonidosGokEvolutionSSJ1[2], 1, 1, 1, 0, 1);
				}
				contadorAura++;
			} else {
				if(EEG_Mindroid_GokEvolution.sonidosActivados)
					EEG_Mindroid_GokEvolution.sonido.play(EEG_Mindroid_GokEvolution.sonidosGokEvolutionSSJ1[2], 1, 1, 1, 0, 1);
			}
			
			SurfaceViewGokEvolution.spriteActual = 3;
			SurfaceViewGokEvolution.animacionActual = SurfaceViewGokEvolution.tipoAnimacion.GOKEVOLUTION_SSJ1_INTENTO_SSJ2;
		} else {
			contadorAura = 0;
			primerAura = true;
			if(valorNivelSSJ > 30 || (valorNivelSSJ <= 30 && !EEG_Mindroid_GokEvolution.puedeEstarCansado)) {
				SurfaceViewGokEvolution.spriteActual = 9;
				SurfaceViewGokEvolution.animacionActual = SurfaceViewGokEvolution.tipoAnimacion.GOKEVOLUTION_SSJ1_REPOSO;
			} else {
				SurfaceViewGokEvolution.spriteActual = 6;
				SurfaceViewGokEvolution.animacionActual = SurfaceViewGokEvolution.tipoAnimacion.GOKEVOLUTION_SSJ1_CANSADO;
			}
		}
	}
	
	private static void animacionesGokEvolutionSSJ2(int valorNivelSSJ) {
		if(valorNivelSSJ > 66) {
			if(primerAura) {
				if(contadorAura == 0) {
					if(EEG_Mindroid_GokEvolution.sonidosActivados)
						EEG_Mindroid_GokEvolution.sonido.play(EEG_Mindroid_GokEvolution.sonidosGokEvolutionSSJ234[1], 1, 1, 1, 0, 1);
				} else if(contadorAura == 1) {
					primerAura = false;
					if(EEG_Mindroid_GokEvolution.sonidosActivados)
						EEG_Mindroid_GokEvolution.sonido.play(EEG_Mindroid_GokEvolution.sonidosGokEvolutionSSJ234[2], 1, 1, 1, 0, 1);
				}
				contadorAura++;
			} else {
				if(EEG_Mindroid_GokEvolution.sonidosActivados)
					EEG_Mindroid_GokEvolution.sonido.play(EEG_Mindroid_GokEvolution.sonidosGokEvolutionSSJ234[2], 1, 1, 1, 0, 1);
			}
			
			SurfaceViewGokEvolution.spriteActual = 7;
			SurfaceViewGokEvolution.animacionActual = SurfaceViewGokEvolution.tipoAnimacion.GOKEVOLUTION_SSJ2_INTENTO_SSJ3;
		} else {
			contadorAura = 0;
			primerAura = true;
			if(valorNivelSSJ > 30 || (valorNivelSSJ <= 30 && !EEG_Mindroid_GokEvolution.puedeEstarCansado)) {
				SurfaceViewGokEvolution.spriteActual = 19;
				SurfaceViewGokEvolution.animacionActual = SurfaceViewGokEvolution.tipoAnimacion.GOKEVOLUTION_SSJ2_REPOSO;
			} else {
				SurfaceViewGokEvolution.spriteActual = 13;
				SurfaceViewGokEvolution.animacionActual = SurfaceViewGokEvolution.tipoAnimacion.GOKEVOLUTION_SSJ2_CANSADO;
			}
		}
	}
	
	private static void animacionesGokEvolutionSSJ3(int valorNivelSSJ) {
		if(valorNivelSSJ > 72) {
			if(primerAura) {
				if(contadorAura == 0) {
					if(EEG_Mindroid_GokEvolution.sonidosActivados)
						EEG_Mindroid_GokEvolution.sonido.play(EEG_Mindroid_GokEvolution.sonidosGokEvolutionSSJ234[1], 1, 1, 1, 0, 1);
				} else if(contadorAura == 1) {
					primerAura = false;
					if(EEG_Mindroid_GokEvolution.sonidosActivados)
						EEG_Mindroid_GokEvolution.sonido.play(EEG_Mindroid_GokEvolution.sonidosGokEvolutionSSJ234[2], 1, 1, 1, 0, 1);
				}
				contadorAura++;
			} else {
				if(EEG_Mindroid_GokEvolution.sonidosActivados)
					EEG_Mindroid_GokEvolution.sonido.play(EEG_Mindroid_GokEvolution.sonidosGokEvolutionSSJ234[2], 1, 1, 1, 0, 1);
			}
			
			SurfaceViewGokEvolution.spriteActual = 12;
			SurfaceViewGokEvolution.animacionActual = SurfaceViewGokEvolution.tipoAnimacion.GOKEVOLUTION_SSJ3_INTENTO_SSJ4;
		} else {
			contadorAura = 0;
			primerAura = true;
			
			if(valorNivelSSJ > 30 || (valorNivelSSJ <= 30 && !EEG_Mindroid_GokEvolution.puedeEstarCansado)) {
				SurfaceViewGokEvolution.spriteActual = 24;
				SurfaceViewGokEvolution.animacionActual = SurfaceViewGokEvolution.tipoAnimacion.GOKEVOLUTION_SSJ3_REPOSO;
			} else {
				SurfaceViewGokEvolution.spriteActual = 18;
				SurfaceViewGokEvolution.animacionActual = SurfaceViewGokEvolution.tipoAnimacion.GOKEVOLUTION_SSJ3_CANSADO;
			}
		}
	}
	
	private static void animacionesGokEvolutionSSJ4(int valorNivelSSJ) {
		if(valorNivelSSJ > 78 && valorNivelSSJ <= 99) {
			if(primerAura) {
				if(contadorAura == 0) {
					if(EEG_Mindroid_GokEvolution.sonidosActivados)
						EEG_Mindroid_GokEvolution.sonido.play(EEG_Mindroid_GokEvolution.sonidosGokEvolutionSSJ234[1], 1, 1, 1, 0, 1);
				} else if(contadorAura == 1) {
					primerAura = false;
					if(EEG_Mindroid_GokEvolution.sonidosActivados)
						EEG_Mindroid_GokEvolution.sonido.play(EEG_Mindroid_GokEvolution.sonidosGokEvolutionSSJ234[2], 1, 1, 1, 0, 1);
				}
				contadorAura++;
			} else {
				if(EEG_Mindroid_GokEvolution.sonidosActivados)
					EEG_Mindroid_GokEvolution.sonido.play(EEG_Mindroid_GokEvolution.sonidosGokEvolutionSSJ234[2], 1, 1, 1, 0, 1);
			}
			
			SurfaceViewGokEvolution.spriteActual = 18;
			SurfaceViewGokEvolution.animacionActual = SurfaceViewGokEvolution.tipoAnimacion.GOKEVOLUTION_SSJ4_INTENTO_ONDA_VITAL;
		} else if(valorNivelSSJ >= 100) {
			EEG_Mindroid_GokEvolution.animacionEjecutandose = true;
			
			if(SurfaceViewGokEvolution.contadorIteracionesOndaVital == 0 && EEG_Mindroid_GokEvolution.sonidosActivados)
				EEG_Mindroid_GokEvolution.sonido.play(EEG_Mindroid_GokEvolution.sonidosGokEvolutionSSJ4[1], 1, 1, 1, 0, 1);
				
			SurfaceViewGokEvolution.spriteActual = 37;
			SurfaceViewGokEvolution.animacionActual = SurfaceViewGokEvolution.tipoAnimacion.GOKEVOLUTION_SSJ4_ONDA_VITAL;
		} else {
			contadorAura = 0;
			primerAura = true;
			
			SurfaceViewGokEvolution.spriteActual = 24;
			SurfaceViewGokEvolution.animacionActual = SurfaceViewGokEvolution.tipoAnimacion.GOKEVOLUTION_SSJ4_REPOSO;
		}
	}
	
	// Aqui llegara justo al alcanzar un nuevo nivel
	public static void ejecutarTransformacionSSJ() {
		EEG_Mindroid_GokEvolution.animacionEjecutandose = true;
		SurfaceViewGokEvolution.spriteActual = 0;
		
		switch(nivelActual) {
			case 1:
				SurfaceViewGokEvolution.animacionActual = SurfaceViewGokEvolution.tipoAnimacion.GOKEVOLUTION_TRANSFORMACION_SSJ1;
				break;
			case 2:
				SurfaceViewGokEvolution.animacionActual = SurfaceViewGokEvolution.tipoAnimacion.GOKEVOLUTION_TRANSFORMACION_SSJ2;
				break;
			case 3:
				SurfaceViewGokEvolution.animacionActual = SurfaceViewGokEvolution.tipoAnimacion.GOKEVOLUTION_TRANSFORMACION_SSJ3;
				break;
			case 4:
				SurfaceViewGokEvolution.animacionActual = SurfaceViewGokEvolution.tipoAnimacion.GOKEVOLUTION_TRANSFORMACION_SSJ4;
				break;
		}
	}
}
