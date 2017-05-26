class ThreadProducer<T> extends Thread {
	
	PortArray<Integer> prts;
	SynchPort<Integer> prt;
	int portsSize;
	long waitingMillis;
	int id;
	int msgNum;
	
	public ThreadProducer(PortArray prts, SynchPort prt, int portsSize, int id, int waitingMillis)
	{
		this.id = id;
		this.prts = prts;
		this.prt = prt;
		this.portsSize = portsSize;
		this.waitingMillis = waitingMillis;
	}
	
	public void run() {
		Message<Integer> m;
		try {
			for (int j=0; j<portsSize; j++){
				System.out.println("\tProduttore " + this.id + " aspetta di inviare alla porta "+ j);
				try {
					Thread.sleep(waitingMillis);
				} catch (InterruptedException ie) {
					System.err.println("InterruptedException sleeping");
				}
				m = new Message<Integer> (this.id, prt);
				System.out.println("\t\tProduttore " + this.id + " invia alla porta "+ j);
				prts.send(m, j);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class ThreadConsumer<T> extends Thread {
	
	PortArray<Integer> prts;
	int portsSize;
	int id;
	int v[];
	int n;
	int waitingMillis;

	public ThreadConsumer(PortArray prts, int portsSize, int id, int array[], int dim, int waitingMillis)
	{
		this.id = id;
		this.prts = prts;
		this.portsSize = portsSize;
		this.v = array;
		this.n = dim;
		this.waitingMillis = waitingMillis;
	}
	
	public void run() {
		try {
			System.out.println("Consumatore " + this.id + " ---> aspetta di ricevere");	
			try {
				Thread.sleep(waitingMillis);
			} catch (InterruptedException ie) {
				System.err.println("InterruptedException sleeping");
			}
			for(int i = 0; i < n*2; i++){
				Msg_Rcv m = prts.receive(v,v.length);
				System.out.println("Consumatore " + this.id + " ---> riceve sulla porta "+ m.num_port + " messaggio dal produttore " + m.message.data);		
			}	
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

public class PortArray_Test {
	private static PortArray<Integer> prConsumer;
	private static SynchPort<Integer> prProducer1;
	private static SynchPort<Integer> prProducer2;
	private static int portsSize;

	public static void main (String[] args) {
		
		System.out.println("Main: starting \n");
		
		portsSize = 3;
		PortArray<Integer> ports = new PortArray<Integer>(portsSize);
		prConsumer = new PortArray<Integer>(portsSize);
		prProducer1 = new SynchPort<Integer>();
		prProducer2 = new SynchPort<Integer>();
		
		int z[] = {1};
		int w[] = {0, 2};
		int v[] = {2, 0, 1};
		int n = 0;
		int m = 0;
		int array[];
		int array2[] = null;
		double random = Math.random();
		if(random > 0.5){
			System.out.println("--------------Comportamento previsto Consumer--------------");
			System.out.println("\nSingolo Consumer che effettua la lettura su tutte le porte \n");
			System.out.println("-----------------------------------------------------------\n");
			array = v;
			n = v.length;
		} else{
			System.out.println("--------------Comportamento previsto Consumer--------------");
			System.out.println("\n Due Consumer che effettua la lettura su porte differenti");
			System.out.println("--> Consumer 0: legge le porte 0, 2");
			System.out.println("--> Consumer 1: legge la porta 1\n");
			System.out.println("-----------------------------------------------------------\n");
			array = w;
			array2 = z;
			n = w.length;
			m = z.length;
		}

		ThreadConsumer<Integer> cons = new ThreadConsumer<Integer>(prConsumer, portsSize, 0, array, n, 100);
		ThreadConsumer<Integer> cons1 = null;
		if(m > 0){
			cons1 = new ThreadConsumer<Integer>(prConsumer, portsSize, 1, array2, m, 100);
		}
		ThreadProducer<Integer> prod1 = new ThreadProducer<Integer>(prConsumer, prProducer1, portsSize, 1, 50);
		ThreadProducer<Integer> prod2 = new ThreadProducer<Integer>(prConsumer, prProducer1, portsSize, 2, 70);

		cons.start();
		if(m > 0) cons1.start();
		prod1.start();
		prod2.start();

		try {
			cons.join();
			if(m > 0) cons1.join();
			prod1.join();
			prod2.join();
		} catch (InterruptedException ie) {
			System.err.println("InterruptedException joining");
		}

		System.out.println("Main: finished");

	}

}
