package cl.usach.sd;

import java.util.ArrayList;

import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Linkable;
import peersim.core.Network;

public class Initialization implements Control {
	String prefix;

	int idLayer;
	int idTransport;

	//Valores que sacaremos del archivo de configuraci�n	
	int argExample;
	int initValue;

	public Initialization(String prefix) {
		this.prefix = prefix;
		this.idLayer = Configuration.getPid(prefix + ".protocol");
		this.idTransport = Configuration.getPid(prefix + ".transport");
		
	}

	/**
	 * Ejecuci�n de la inicializaci�n en el momento de crear el overlay en el
	 * sistema
	 */
	@Override
	public boolean execute() {
		System.out.println("Construyendo red");
		/**
		 * Para comenzar tomaremos un nodo cualquiera de la red, a trav�s de un random
		 */
		//int nodoInicial = CommonState.r.nextInt(Network.size());
		
		//Valores iniciales que se obtienen de archivo de configuración
		int tamanoRed = Network.size();
		int m = Configuration.getInt(prefix + ".m");
		int n = Configuration.getInt(prefix + ".n");
		int TTL = Configuration.getInt(prefix + ".TTL");
		int KRW = Configuration.getInt(prefix + ".KRW");
		int cache = Configuration.getInt(prefix + ".Cache");
		
		// Se imprimen valores iniciales
		System.out.println("Valores iniciales");
		System.out.println("s:\t"+tamanoRed+"\t [Cantidad de Super peers]");
		System.out.println("m:\t"+m+"\t [Mínimo tamaño subred]");
		System.out.println("n:\t"+n+"\t [Máximo tamaño subred]");
		System.out.println("TTL:\t"+TTL+"\t [Time To Live]");
		System.out.println("KRW:\t"+KRW+"\t [K-Random Walk]");
		System.out.println("Cache:\t"+cache+"\t [Tamaño cache]");
		System.out.println("---");
		System.out.println("DHT :\t"+obtenerDistancias(tamanoRed).size()+"\t [Tamaño DHT]");
		System.out.println("---");
		System.out.println("Información de Super Peers:");
		for(int i=0;i<tamanoRed;i++){
			// Inicializa los valores de cada Peer
			((SuperPeer)Network.get(i)).initSuperPeer(i,obtenerDistancias(tamanoRed),tamanoRed,m,n);
		}
		
		System.out.println("Generarndo Sub Redes:");
		int subNodo = 0;
		for(int i=0;i<tamanoRed;i++){
			((SuperPeer)Network.get(i)).generarSubRed(subNodo);
			subNodo = ((SuperPeer)Network.get(i)).getUltimoNodo();
		}
		
		return true;
	}
	
	// -----------------------------------
	// Método que calcula distancias para DHT para imprimirlos al inicio
	// -------------------------------------
	public ArrayList<Integer> obtenerDistancias(int tamanoRed){
		/// 2^x
		ArrayList<Integer> denominadores = new ArrayList<Integer>();
		// Valores x
		ArrayList<Integer> x = new ArrayList<Integer>();
		// 2^0
		int inicial = 1;
		int valorX = 1;
		denominadores.add(valorX);
		// Obtiene denominadores hasta completar tamaño de la red
		while(inicial<tamanoRed){
			inicial = inicial * 2;
			if(inicial<tamanoRed){
				denominadores.add(inicial);
				x.add(valorX);
				valorX++;
			}
		}
		return denominadores;
	}
	


}
