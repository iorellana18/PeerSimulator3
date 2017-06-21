package cl.usach.sd;

import java.util.ArrayList;
import java.util.Stack;

import peersim.core.GeneralNode;

public class Peer extends GeneralNode{
	
	private int id;
	// Objeto que guarda peers en cache
	private Stack<String> cache;
	// Lista que guarda peers en DHT
	private ArrayList<Integer> DHT;
	// Lista que guarda valores en base de datos
	private ArrayList<Integer> DB;
	// Vecino
	private int vecino;
	// Tamaño de base de datos
	private int sizeDB;
	// Tamaño red
	private int tamanoRed;
	// Tamaño cache
	private int tamanoCache;
	

	public Peer(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}


	public void initPeer(int id,int tamanoCache, int tamanoRed,int d,int sizeDB){
		// Guarda id actual
		setId(id);
		// Obtiene DHT
		myDHT(tamanoRed,d,id);
		// Obtiene id de vecino
		setVecino(id,tamanoRed);
		// Obtiene bade de datos
		obtenerBD(id,sizeDB);
		// Setea datos iniciales
		setTamanoRed(tamanoRed);
		setTamanoCache(tamanoCache);
		setSizeDB(sizeDB);
		setCache(new Stack<String>());
		System.err.print("Nodo: "+id);
		System.out.println("\t\tVecino: "+vecino+"\tDHT: "+DHT+"\t\tCache: "+cache+"\t\tBD: "+DB+"\n");
	}
	//////////////////////////////////////////////////
	// Métodos
	/////////////////////////////////////////////////
	
	// -----------------------------------
	// Método que inicia la base de datos para cada nodo ingresando b cantidad de datos
	// -------------------------------------
	public void obtenerBD(int id, int tamanoBD){
		// Se inicia una lista vacía
		ArrayList<Integer> base = new ArrayList<Integer>();
		// Se obtiene el número del id del nodo, el cual se multiplica por el tamaño de la base 
		// de datos para obtener el número inicial
		int inicial = id * tamanoBD;
		// Se realiza un ciclo para obtener los b números siguientes y añadirlos a la lista
		for(int i=0;i<tamanoBD;i++){
			base.add(inicial);
			inicial++;
		}
		// Se setea la lista obtenida para cada nodo
		setDB(base);
	}
	
	// -----------------------------------
	// Método que calcula el nodo resultante dado su posición y distancia (para DHT)
	// -------------------------------------
	public int calculaSuma(int base, int suma, int tamanoRed){
		if(base+suma>=tamanoRed){
			// Si el id del nodo mas la distancia es mayor al tamaño de la red
			// se le resta el tamaño de la red
			return (base+suma)-tamanoRed;
		}else{
			// Si no se ingresa la suma simple
			return base+suma;
		}
	}
	
	// -----------------------------------
	// Método que calcula el nodo resultante (en sentido inverso) dado su posición y distancia (para DHT)
	// -------------------------------------
	public int calculaResta(int base, int suma, int tamanoRed){
		if(base-suma<0){
			// Si el id del nodo menos la distancia hacia atrás es menor a 0
			// Se le resta al tamaño de la red
			return tamanoRed-(suma-base);
		}else{
			// Si no se ingresa la resta
			return base-suma;
		}
	}
	// -----------------------------------
	// Método que obtiene el DHT para cada nodo dado su id y distancia d
	// -------------------------------------
	public void myDHT(int tamanoRed, int d,int id){
		// Se inicia la lista que guardará los DHT
		ArrayList<Integer> DHT = new ArrayList<Integer>();
		if(tamanoRed%2==0){
			// Si la red tiene una cantidad de nodos par se le asigna
			// el nodo que se encuentra en la mitad de la red
			DHT.add(calculaSuma(id,tamanoRed/2,tamanoRed));
		}else{
			// SI la red tien una cantidad de nodos impar se le asigna
			// el nodo que se encuentra a la mitad + 1
			DHT.add(calculaSuma(id,(tamanoRed/2)+1,tamanoRed));
		}
		// Número que corresponde a 2^2 ya que 2^1 se añade anteriormente
		int numeroMagico = 4; 
		for(int i=0;i<d;i++){
			// Para cada nodo se calcula la distancia n/2^x y se asignan sus DHT
			int distancia = tamanoRed/numeroMagico;
			DHT.add(calculaSuma(id,distancia,tamanoRed));
			DHT.add(calculaResta(id,distancia,tamanoRed));
			// Siguiente denominador
			numeroMagico=numeroMagico*2;
		}

		setDHT(DHT);
	}
	
	
	// -----------------------------------
	// Método que dada una lista y un objetivo calcula cual de los nodos está a una distancia menor
	// -------------------------------------
	public int distanciador(int objetivo, ArrayList<Integer> actual, String contenido){
		// Se inicia con el id del nodo 0
		int menor = 0;
		int min = tamanoRed;
		for(int i=0;i<actual.size();i++){
			int distancia;
			if(objetivo>actual.get(i)){
				// Si objetivo es mayor al nodo actual la ditancia es su resta simple
				distancia = objetivo - actual.get(i);
			}else if(objetivo == actual.get(i)){
				// Si el objetivo es igual al nodo actual quiere decir que se encuentra en DHT
				System.out.println("\t"+contenido+" Nodo objetivo se encuentra en DHT");
				return i;
			}else{
				// Si objetivo es menor que actual la distancia es la resta de la red
				// se resta el id del nodo actual y se suma el objetivo para obtener distancia
				distancia = (tamanoRed - actual.get(i)) + objetivo;
			}
			if(distancia<min){
				// Si la distancia resulta ser menor a la menor actual se guarda como la menor
				menor = i;
				min = distancia;
			}
		}
		
		return menor;
	}
	
	// -----------------------------------
	// Método que calcula el nodo que está a menor distancia del nodo objetivo
	// entre el vecino y los de su DHT
	// -------------------------------------
	public int calcularDistancias(String mensaje, long receptor){
		// Lista que contendrá posiciones para comparar
		ArrayList<Integer> lista = new ArrayList<Integer>();
		// Informa que se realiza este cálculo
		System.out.print("\t"+mensaje+" ");
		System.out.print("Nodo "+id+" calcula distancias con "+vecino);
		// Se añade al vecino
		lista.add(vecino);
		// Se añaden los nodos del DHT
		for(int i=0;i<DHT.size();i++){
			System.out.print(", "+DHT.get(i));
			lista.add(DHT.get(i));
		}
		System.out.println(" ");
		// Retorna el nodo más cercano al objetivo utilizando método anterior
		return lista.get(distanciador((int)receptor,lista,mensaje));
	}
	

	// -----------------------------------
	// Método que imprime de forma ordenada los datos en cache 
	// -------------------------------------
	public String imprimeCache(){
		String imprime = "";
		for(int i=0;i<cache.size();i++){
			imprime=imprime+"["+cache.get(i)+"] ";
		}
		return imprime;
	}
	
	
	// -----------------------------------
	// Método que comprueba si dato ingresado está en cache
	// -------------------------------------
	public Boolean compruebaDato(int dato){
		if(cache.isEmpty()){
			// si cache está vacío retorna falso
			return false;
		}else{
			// si no busca en cada dato del cache
			for(int i=0;i<cache.size();i++){
				// Se separa el dato del cache (compuesto de nodo,dato)
				String separador[] = cache.get(i).split(",");
				// Se convierte dato en String para compararlo fácilmente
				String comparador = String.valueOf(dato);
				if(separador[1].compareTo(comparador)==0){
					// Si se encuentra dato en cache retorna verdadero
					return true;
				}
			}
			// Si no se encontró retorna falso
			return false;
		}
	}
	
	//////////////////////////////////////////////////
	// Getters y Setters
	/////////////////////////////////////////////////
	
	public int getVecino(){return vecino;}
	public void setVecino(int id, int tamanoRed){if(id==(tamanoRed-1)){this.vecino=0;}else{this.vecino=id+1;}}
	public int getId(){return id;}
	public void setId(int id){this.id=id;}
	public Stack<String> getCache() {return cache;}
	public void setCache(Stack<String> cache) {this.cache = cache;}
	public ArrayList<Integer> getDHT() {return DHT;}
	public void setDHT(ArrayList<Integer> dHT) {DHT = dHT;}
	public ArrayList<Integer> getDB() {return DB;}
	public void setDB(ArrayList<Integer> dB) {DB = dB;}
	public void setSizeDB(int sizeDB){this.sizeDB=sizeDB;}
	public int getSizeDB(){return sizeDB;}
	public void setTamanoRed(int tamanoRed){this.tamanoRed=tamanoRed;}
	public int getTamanoRed(){return tamanoRed;}
	public void setTamanoCache(int tamanoCache){this.tamanoCache=tamanoCache;}
	public int getTamanoCache(){return tamanoCache;}
}
