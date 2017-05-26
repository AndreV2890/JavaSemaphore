import java.util.LinkedList;
import java.util.Queue;

public class FairSem{
	//valore del semaforo
	private int sem_val;
	//lista FIFO dei thread bloccati sul semaforo
	private Queue<Long> blocked_thread_queue;
	
	private boolean test;
	
	public FairSem(int value){
		sem_val = value;
		blocked_thread_queue = new LinkedList();
		test = false;
	}
	
	public FairSem(int value, boolean test){
		sem_val = value;
		blocked_thread_queue = new LinkedList();
		
		this.test = test;
	}
	
	public synchronized void P() throws InterruptedException{
		//seleziono id del thread chiamante
		long th_id = Thread.currentThread().getId();
		
		//inserisco nella coda l'id del thread chiamante 
		if (blocked_thread_queue.offer(th_id) == false)
			System.err.println("Element not inserted in queue");
		
		if(test)
			System.out.println("FIFO: Thread " + th_id+ " inserito in coda");
		
		//se il semaforo è rosso oppure il thread che si è sbloccato
		//non è il primo in base all'ordinamento FIFO questo si blocca
		while (sem_val <= 0 || th_id != blocked_thread_queue.peek())
			wait();		
		
		//rimuovo il primo elemento della lista
		blocked_thread_queue.poll();
		sem_val--;
		if(test)
			System.out.println("FIFO: Thread " + th_id+ " rimosso dalla coda e mandato in esecuzione");
		
		if(sem_val > 0) notifyAll();
	}
	
	public synchronized void V(){
		sem_val++;
		notifyAll();
	}
	
}