import java.util.LinkedList;
import java.util.Queue;

class Producer_B extends Thread {
	private Mailbox_B server;
	private SynchPort<Integer> myPort;
	public int priority;
	
	public Producer_B(Mailbox_B server,int priority) {
		this.server = server;
		this.priority = priority;
		myPort = new SynchPort<Integer>();
	}
	
	public void run() {
		try {
			for (int i = 0; i < 5; i++) {
				server.request_to_insert(myPort,priority);
				myPort.send(new Message<Integer>(i, null));
			}
		} catch (InterruptedException e) {
			System.out.println("Exception found on producer"+Thread.currentThread().getId());
		}
	}
}

class Consumer_B extends Thread {
	private Mailbox_B server;
	private SynchPort<Integer> myPort;
	
	public Queue<Boolean> testingConsistency;
	
	public Consumer_B(Mailbox_B server) {
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

public class Mailbox_B_Test{
	public static void main(String[] args) throws InterruptedException {
		System.out.println("Inizio");
		// create a new server
		Mailbox_B server = new Mailbox_B();
		// server starts
		server.start();
		
		// create and start 10 producer
		Producer_B[] producers = new Producer_B[10];
		for (int i = 0; i < 10; i++) {
			producers[i] = new Producer_B(server,i+1);
			producers[i].start();
		}
		
		// create and start the consumer
		System.out.println("Consumer Start");
		Consumer_B consumer = new Consumer_B(server);
		consumer.start();


		// join all
		for (int i = 0; i < 10; i++) 
			producers[i].join();
		consumer.join();
		
		System.out.println("Fine");
	}
}