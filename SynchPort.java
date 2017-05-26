import java.util.LinkedList;
import java.util.Queue;

public class SynchPort<T>{
	
	// Buffer contenente il messaggio da scambiare tra le porte
	private Message<T> mess;
	
	//semaforo per il sender che aspetta che la porta sia vuota
	//prima di inviare un messaggio
	private FairSem empty;
	
	//semaforo per il receiver che prima di leggere il messaggio 
	//aspetta che la porta sia piena
	private FairSem full;
	
	//semaforo per rendere sincrona l'operazione
	private FairSem sync;
	
	public SynchPort(){
		empty = new FairSem(1);
		full = new FairSem(0);
		sync = new FairSem(0);
	}
	
	public void send(Message<T> m) throws InterruptedException{	
		empty.P();
		this.mess = m;
		full.V();
		sync.P();	
	}
	
	public Message<T> receive() throws InterruptedException{
		Message<T> m;
		full.P();
		m = this.mess;
		empty.V();
		sync.V();
		return m;	
	}	
}