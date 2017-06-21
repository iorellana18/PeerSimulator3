package cl.usach.sd;

import java.util.ArrayList;

/**
 * Clase la cual vamos a utilizar para enviar datos de un Peer a otro
 */
public class Message {
	// ID de quien envía mensaje
	private long emisor;
	// ID de quien debe recibir mensaje
	private long receptor;
	// Arreglo que guarda IDs de nodos por donde pasa para llegar
	private ArrayList<Integer> camino;
	// Para conocer si mensaje va de ida o vuelta
	private Boolean recibido;
	// Dato que se busca
	private long dato;
	// Mensaje
	private String mensaje;
	
	// Método para iniciar mensaje, sólo hace falta el emisor y receptor
	public Message(long emisor, long receptor, long dato, String mensaje){
		setEmisor(emisor);
		setReceptor(receptor);
		setCamino(new ArrayList<Integer>());
		// Recibido se inicia como falso cuando se envía
		setRecibido(false);
		setDato(dato);
		setMensaje(mensaje);
	}

	// Constructores
	public void setEmisor(long emisor){this.emisor=emisor;}
	public long getEmisor(){return emisor;}
	public void setReceptor(long receptor){this.receptor=receptor;}
	public long getReceptor(){return receptor;}
	public void setCamino(ArrayList<Integer> camino){this.camino=camino;}
	public ArrayList<Integer> getCamino(){return camino;}
	public void setRecibido(Boolean recibido){this.recibido=recibido;}
	public Boolean getRecibido(){return recibido;}
	public void setDato(long dato){this.dato=dato;}
	public long getDato(){return dato;}
	public void setMensaje(String mensaje){this.mensaje=mensaje;}
	public String getMensaje(){return mensaje;}
}
