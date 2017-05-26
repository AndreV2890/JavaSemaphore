import java.util.LinkedList;
import java.util.Queue;

class Producer_A extends Thread {
	private Mailbox_A server;
	private SynchPort<Integer> myPort;
	
	//public Producer(Mailbox server,int priority) {
	public Producer_A(Mailbox_A server) {
		this.server = server;
		myPort = new SynchPort<Integer>();
	}
	
	public void run() {
		try {
			for (int i = 0; i < 5; i++) {
				server.request_to_insert(myPort);
				myPort.send(new Message<Integer>(i, null));
			}
		} catch (InterruptedException e) {
			System.out.println("Exception found on producer"+Thread.currentThread().getId());
		}
	}
}

class Consumer_A extends Thread {
	private Mailbox_A server;
	private SynchPort<Integer> myPort;
	
	public Queue<Boolean> testingConsistency;
	
	public Consumer_A(Mailbox_A server) {
		this.server = server;
		myPort = new SynchPort<Integer>();
	}
	public void run() {
		try {
			for (int i = 0; i < 50; i++) {
				server.request_to_remove(myPort);
				Message<Integer> msg = myPort.receive();
				System.out.println("Messagio ricevuto dal thread: "+ msg.getIdThread()+". Valore: "+ msg.getData());
				
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			System.out.println("Exception found on producer"+Thread.currentThread().getId());
		}
	}
}

public class Mailbox_A_Test{
	public static void main(String[] args) throws InterruptedException {
		System.out.println("Inizio");
		// create a new server
		Mailbox_A server = new Mailbox_A();
		// server starts
		server.start();
		
		// create and start 10 producer
		Producer_A[] producers = new Producer_A[10];
		for (int i = 0; i < 10; i++) {
			producers[i] = new Producer_A(server);
			producers[i].start();
		}
		
		// create and start the consumer
		System.out.println("Consumer Start");
		Consumer_A consumer = new Consumer_A(server);
		consumer.start();


		// join all
		for (int i = 0; i < 10; i++) 
			producers[i].join();
		consumer.join();
		
		System.out.println("Fine");
	}
}