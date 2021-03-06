package cl.usach.sd;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.util.IncrementalStats;

public class Observer implements Control {

	private int layerId;
	private String prefix;
	private int argExample;
	
	public static IncrementalStats message = new IncrementalStats();

	public Observer(String prefix) {
		this.prefix = prefix;
		this.layerId = Configuration.getPid(prefix + ".protocol");
	}

	@Override
	public boolean execute() {
		int size = Network.size();
		for (int i = 0; i < Network.size(); i++) {
			if (!Network.get(i).isUp()) {
				size--;
			}
		}

		
				
		String s = String.format("[time=%d]:[with N=%d nodes] [%d Total send message]", CommonState.getTime(), size,
				(int) message.getSum());
		System.err.println("\nOBSERVER");
		for (int i = 0; i < Network.size(); i++) {		
			
			//System.err.println("\nNodo "+((Peer)Network.get(i)).getID()+"\t: "+((Peer)Network.get(i)).getDHT()+"\tCACHE: "+((Peer)Network.get(i)).imprimeCache()+"\tBD: "+((Peer)Network.get(i)).getDB());
		
		}
		System.err.println(s);

		return false;
	}
	
	

}
