package cl.usach.sd;

import java.util.ArrayList;

import peersim.core.CommonState;
import peersim.core.GeneralNode;

public class SuperPeer extends GeneralNode {
	
	public SuperPeer(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	private int id;
	private ArrayList<Integer> DHT;
	private int cantidadSuperPeers;
	private int puerto;
	private int tamanoSubRed;
	private ArrayList<Integer> subRed;
	private int ultimoNodo;
	
	public void initSuperPeer(int id,ArrayList<Integer> DHT,int cantidadSuperPeers,int minimoSubRed,int maximoSubRed){
		setId(id);
		generarPuerto();
		generarTamanoSubRed(minimoSubRed,maximoSubRed);
		setCantidadSuperPeers(cantidadSuperPeers);
		generarDHT(DHT);
		System.err.print("IP: "+id);
		System.out.println("\tPuerto:"+puerto+"\tDHT: "+getDHT());
	}
	
	
//////////////////////////////////////////////////
// Métodos
/////////////////////////////////////////////////

// -----------------------------------
// Método que genera puertos de forma aleatoria entre 3000 y 4000
// -------------------------------------
	public void generarPuerto(){
		setPuerto(3000+CommonState.r.nextInt(1000));
	}
	
// -----------------------------------
// Método calcula tamaño sub red de Super Peer
// -------------------------------------
	public void generarTamanoSubRed(int minimoSubRed,int maximoSubRed){
		setTamanoSubRed(minimoSubRed+CommonState.r.nextInt(maximoSubRed-minimoSubRed));
	}
	
// -----------------------------------
// Método genera DHT segun id
// -------------------------------------
	
	public void generarDHT(ArrayList<Integer> DHT){
		ArrayList<Integer> newDHT = new ArrayList<Integer>();
		for(int i=0;i<DHT.size();i++){
			if((DHT.get(i)+id)<cantidadSuperPeers){
				newDHT.add(DHT.get(i)+id);
			}else{
				newDHT.add((DHT.get(i)+id)-cantidadSuperPeers);
			}
		}
		setDHT(newDHT);
	}
	
// -----------------------------------
// Método genera SubRed 
// -------------------------------------
	public void generarSubRed(int ultimoNodo){
		ArrayList<Integer> red = new ArrayList();
		for(int i=1;i<=tamanoSubRed;i++){
			red.add(ultimoNodo+i);
			if(i==tamanoSubRed){setUltimoNodo(ultimoNodo+i);}
		}
		setSubRed(red);
	}

	
	
//////////////////////////////////////////////////
//Constructores
/////////////////////////////////////////////////
	
	public void setDHT(ArrayList<Integer> DHT){this.DHT=DHT;}
	public ArrayList<Integer> getDHT(){return DHT;}
	public void setId(int id){this.id=id;}
	public int getId(){return id;}
	public void setCantidadSuperPeers(int cantidadSuperPeers){this.cantidadSuperPeers=cantidadSuperPeers;}
	public int getCantidadSuperPeers(){return cantidadSuperPeers;}
	public void setPuerto(int puerto){this.puerto=puerto;}
	public int getPuerto(){return puerto;}
	public void setTamanoSubRed(int tamanoSubRed){this.tamanoSubRed=tamanoSubRed;}
	public int getTamanoSubRed(){return tamanoSubRed;}
	public void setSubRed(ArrayList<Integer> subRed){this.subRed=subRed;}
	public ArrayList<Integer> getSubRed(){return subRed;}
	public void setUltimoNodo(int ultimoNodo){this.ultimoNodo=ultimoNodo;}
	public int getUltimoNodo(){return ultimoNodo;}
}
