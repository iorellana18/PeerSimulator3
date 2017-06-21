package cl.usach.sd;

import java.util.ArrayList;
import java.util.Stack;

import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.WireKOut;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;

public class Layer implements Cloneable, EDProtocol {
	private static final String PAR_TRANSPORT = "transport";
	private static String prefix = null;
	private int transportId;
	private int layerId;

	/**
	 * M�todo en el cual se va a procesar el mensaje que ha llegado al Nodo
	 * desde otro Nodo. Cabe destacar que el mensaje va a ser el evento descrito
	 * en la clase, a trav�s de la simulaci�n de eventos discretos.
	 */
	@Override
	public void processEvent(Node myNode, int layerId, Object event) {
		/**Este metodo trabajar� sobre el mensaje*/
		/**A modo de ejemplo, elegiremos cualquier nodo, y a ese nodo le enviaremos el mensaje 
		 * con las siguientes condiciones de ejemplo: 
		 * Si el nodo actual es del tipo 0: suma 1 a su valor
		 * Si el nodo actual es del tipo 1: resta 1 a su valor
		 * */	
		Message message = (Message) event;
		//Imprimie mensaje
		//System.out.println(message.getMensaje());
		

		sendmessage(myNode, layerId, message);
		getStats();
	}

	private void getStats() {
		Observer.message.add(1);
	}

	public void sendmessage(Node currentNode, int layerId, Object message) {
		/**Con este m�todo se enviar� el mensaje de un nodo a otro
		 * CurrentNode, es el nodo actual
		 * message, es el mensaje que viene como objeto, por lo cual se debe trabajar sobre �l
		 */
		// Castear mensaje
		Message mensaje = (Message)message;
		// Casteo de nodo
		Peer nodoActual = (Peer)currentNode;
		
		// Obtener datos de mensaje
		// Contenido en formato [emisor,receptor,dato buscado]
		String contenido = mensaje.getMensaje();
		// Obtiene receptor
		long receptor = mensaje.getReceptor();
		// Dato que se está buscando
		long dato = mensaje.getDato();
		// Lista con nodos recorridos
		ArrayList<Integer> camino = mensaje.getCamino();
		
		// Datos Peer actual
		// ID nodo actual
		long id = nodoActual.getID();
		// Tamaño cache
		int tamanoCache = nodoActual.getTamanoCache();
		// Vecino del nodo actual
		long vecino = ((Linkable) nodoActual.getProtocol(0)).getNeighbor(0).getID();
		// Cache del nodo actual
		Stack<String> cache = nodoActual.getCache();
		
		// Si mensaje aún no se ha entregado
		if(mensaje.getRecibido()==false){
			// Si nodo actual es el receptor del mensaje
			if(id == receptor){
				System.out.println("\t"+contenido+" Nodo "+id+" tiene respuesta a consulta "+dato);
				// se cambia el estado de recibido
				mensaje.setRecibido(true);
				System.out.println("\t"+contenido+" Nodo "+id+" ha pasado por los nodos "+mensaje.getCamino());
				// Obtener último nodo
				int envioDeVuelta = camino.get(camino.size()-1);
				// Quita último dato del camino
				camino.remove(camino.size()-1);
				// Setea camino sin último dato
				mensaje.setCamino(camino);
				System.out.println("\t"+contenido+" Nodo "+id+" envía respuesta a nodo "+envioDeVuelta);
				// Envía mensaje a nodo anterior
				((Transport) nodoActual.getProtocol(transportId)).send(nodoActual, Network.get(envioDeVuelta), mensaje, layerId);
			}else{
				// Revisar si cache está vacío
				if(cache.isEmpty()){
					System.out.println("\t"+contenido+" Nodo "+id+" no tiene consulta "+dato+" en cache ("+nodoActual.imprimeCache()+")");
					// Nodo objetivo es vecino?
					if(vecino==receptor){
						System.out.println("\t"+contenido+" Nodo "+id+" envía consulta a nodo vecino "+receptor);
						// Se añade nodo al camino por el que pasa
						camino.add((int)id);
						mensaje.setCamino(camino);
						// Se envía mensaje a vecino
						((Transport) nodoActual.getProtocol(transportId)).send(nodoActual, Network.get((int) vecino), mensaje, layerId);
					}else{
						// Si no, calcula distancia de vecino con DHT para ver quien está más cerca
						int peerCercano = nodoActual.calcularDistancias(contenido,receptor);
						System.out.println("\t"+contenido+" Nodo "+id+" envía consulta a nodo "+peerCercano);
						// Se añade nodo al camino por el que pasa
						camino.add((int)id);
						mensaje.setCamino(camino);
						// Se envía al peer más cercano al objetivo
						((Transport) nodoActual.getProtocol(transportId)).send(nodoActual, Network.get(peerCercano), mensaje, layerId);
					}
				}else{
					// Buscar en cache si no está vacío
					if(nodoActual.compruebaDato((int)dato)){
						// Si está en cache informar que se tiene dato
						System.out.println("\t"+contenido+" Nodo "+id+" tiene dato "+dato+" en cache ("+nodoActual.imprimeCache()+")");
						// Se cambia estado de recepción de mensaje
						mensaje.setRecibido(true);
						if(camino.isEmpty()){
							// Está en cache del emisor
							System.out.println("\t"+contenido+" Nodo "+id+" obtiene respuesta desde su cache");
						}else{
							// Obtener último nodo
							System.out.println("\t"+contenido+" Nodo "+id+" ha pasado por los nodos "+mensaje.getCamino());
							int envioDeVuelta = camino.get(camino.size()-1);
							// Se quita ultimo nodo del camino y se setea
							camino.remove(camino.size()-1);
							mensaje.setCamino(camino);
							System.out.println("\t"+contenido+" Nodo "+id+" envía respuesta a nodo "+envioDeVuelta);
							// Enviar a nodo anterior
							((Transport) nodoActual.getProtocol(transportId)).send(nodoActual, Network.get(envioDeVuelta), mensaje, layerId);
						}
					}else{
						System.out.println("\t"+contenido+" Nodo "+id+" no tiene consulta "+dato+" en cache ("+nodoActual.imprimeCache()+")");
						// COdigo duplicado
						// Nodo es vecino?
						if(vecino==receptor){
							System.out.println("\t"+contenido+" Nodo "+id+" envía consulta a nodo vecino "+receptor);
							// Se añade nodo al camino por el que pasa
							camino.add((int)id);
							mensaje.setCamino(camino);
							((Transport) nodoActual.getProtocol(transportId)).send(nodoActual, Network.get((int) vecino), mensaje, layerId);
						}else{
							// Si no, calcula distancia de vecino con DHT para ver quien está más cerca
							int peerCercano = nodoActual.calcularDistancias(contenido,receptor);
							System.out.println("\t"+contenido+" Nodo "+id+" envía consulta a nodo "+peerCercano);
							// Se añade nodo al camino por el que pasa
							camino.add((int)id);
							mensaje.setCamino(camino);
							((Transport) nodoActual.getProtocol(transportId)).send(nodoActual, Network.get(peerCercano), mensaje, layerId);
						}
					}
				}
			}
		}else{
			// Si mensaje ya fue recibido se envía de vuelta hasta el nodo inicial
			if(camino.size()>0){
				// Guardar en cache
				//Comprueba si dato ya existe en cache
				if(nodoActual.compruebaDato((int)dato)){
					System.out.println("\t"+contenido+" Dato "+dato+" ya se encuentra en cache : "+cache);
				}else{
					// SI no comprueba si hay espacio en cache
					if(cache.size()<tamanoCache){
						// Si hay espacio se añade directamente
						cache.push(receptor+","+dato);
						nodoActual.setCache(cache);
						System.out.println("\t"+contenido+" Nodo "+id+" actualiza cache : "+nodoActual.imprimeCache());
					}else{
						// Si no se remueve nodo más antiguo
						System.out.println("\t"+contenido+" Nodo "+id+" con cache lleno : "+nodoActual.imprimeCache());
						cache.remove(0);
						cache.push(receptor+","+dato);
						nodoActual.setCache(cache);
						System.out.println("\t"+contenido+" Nodo "+id+" actualiza cache : "+nodoActual.imprimeCache());
					}
				}
				// Enviar mensaje a nodo anterior y luego lo borra
				int enviar = camino.get(camino.size()-1);
				camino.remove(camino.size()-1);
				// Actualiza camino que queda por recorrer y envía
				mensaje.setCamino(camino);
				System.out.println("\t"+contenido+" Nodo "+id+" envía respuesta a nodo "+enviar);
				((Transport) nodoActual.getProtocol(transportId)).send(nodoActual, Network.get(enviar), mensaje, layerId);
			}else{
				// Nodo emisor que envió la consulta inicialmente
				System.out.println("\t"+contenido+" Nodo "+id+" Recibió respuesta");
			}
			
			
		}

	}
	
	
	
	

	public Layer(String prefix) {
		/**
		 * Inicialización del Nodo
		 */
		Layer.prefix = prefix;
		transportId = Configuration.getPid(prefix + "." + PAR_TRANSPORT);
		/**
		 * Siguiente capa del protocolo
		 */
		layerId = transportId + 1;
	}

	

	/**
	 * Definir Clone() para la replicacion de protocolo en nodos
	 */
	public Object clone() {
		Layer dolly = new Layer(Layer.prefix);
		return dolly;
	}
}
