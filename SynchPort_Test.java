import java.util.LinkedList;
import java.util.Queue;

class testingThread extends Thread
{
	SynchPort<String> prIn;
	SynchPort<String> prOut;
	long waitingMillis;
	int type;
	int id;
	int msgNum;

	// type = 0 means CONSUMER, otherwise PRODUCER
	testingThread(SynchPort<String> prIn, SynchPort<String> prOut, long waitingMillis, int type, int id, int msgNum)
	{
		this.id = id;
		this.prIn = prIn;
		this.prOut = prOut;
		this.waitingMillis = waitingMillis;
		this.type = type;
		this.msgNum = msgNum;
		//System.out.println("Thread "+this.getId()+":\tcreated");
	}

	public void run()
	{
		Message<String> m;
		switch (this.type) {
		// CONSUMER
		case 0:
			for (int i=0; i<msgNum; i++) {
				System.out.println(">> Consumatore --> attesa per un messaggio");
				try {
					m = prIn.receive();
					System.out.println(">> Consumatore --> ricevuto messaggio:\""+m.getData()+'\"');
				} catch (InterruptedException ie) {
					System.err.println("InterruptedException receiving");
				}
				// Simulating message consumption by waiting some time
				try {
					Thread.sleep(waitingMillis);
				} catch (InterruptedException ie) {
					System.err.println("InterruptedException sleeping");
				}
			}
			break;
			// PRODUCER
		default:
			for (int i=0; i<msgNum; i++) {
				m = new Message<String> ("Messaggio num " + i + " da thread "+id, prIn);
				// Simulating message production by waiting some time
				try {
					Thread.sleep(waitingMillis);
				} catch (InterruptedException ie) {
					System.err.println("InterruptedException sleeping");
				}
				try {
					System.out.println("\t\t-- Produttore " + this.id + " --> invio messaggio");
					prOut.send(m);
				} catch (InterruptedException ie) {
					System.err.println("InterruptedException sending");
				}
			}
			break;
		}
	}
}

public class SynchPort_Test{
	//private static testingQueue tq;
	private static SynchPort<String> prConsumer;
	private static SynchPort<String> prProducer1;
	private static SynchPort<String> prProducer2;
	private static SynchPort<String> prProducer3;
	private static SynchPort<String> prProducer4;
	private static SynchPort<String> prProducer5;
	private static SynchPort<String> prProducer6;

	public static void main(String[] args)
	{
		System.out.println("Main: starting");
		
		System.out.println("------------------------------------------------------------------------------------");
		System.out.println("Scopo del test:");
		System.out.println("1) ricevere tutti i pacchetti inviati ---> (6 produttori x 4 messaggi = 24 messaggi)");
		System.out.println("2) ricevere i messaggi rispettando l'ordine FIFO");
		System.out.println("------------------------------------------------------------------------------------");
		
		final boolean testing = true;

		prConsumer = new SynchPort<String>();
		prProducer1 = new SynchPort<String>();
		prProducer2 = new SynchPort<String>();
		prProducer3 = new SynchPort<String>();
		prProducer4 = new SynchPort<String>();
		prProducer5 = new SynchPort<String>();
		prProducer6 = new SynchPort<String>();

		testingThread consumer;
		testingThread producer1;
		testingThread producer2;
		testingThread producer3;
		testingThread producer4;
		testingThread producer5;
		testingThread producer6;

		//Creazione dei thread
		consumer = new testingThread(prConsumer, null, 200, 0, 0, 24);
		producer1 = new testingThread(prProducer1, prConsumer, 30, 1, 1, 4);
		producer2 = new testingThread(prProducer2, prConsumer, 40, 1, 2, 4);
		producer3 = new testingThread(prProducer3, prConsumer, 50, 1, 3, 4);
		producer4 = new testingThread(prProducer1, prConsumer, 60, 1, 4, 4);
		producer5 = new testingThread(prProducer2, prConsumer, 70, 1, 5, 4);
		producer6 = new testingThread(prProducer3, prConsumer, 80, 1, 6, 4);

		//Avvio dei thread
		consumer.start();
		producer1.start();
		producer2.start();
		producer3.start();
		producer4.start();
		producer5.start();
		producer6.start();

		//Attesa dei thread
		try {
			consumer.join();
			producer1.join();
			producer2.join();
			producer3.join();
			producer4.join();
			producer5.join();
			producer6.join();
		} catch (InterruptedException ie) {
			System.err.println("InterruptedException joining");
		}

		System.out.println("Main: finished");
	}
}
