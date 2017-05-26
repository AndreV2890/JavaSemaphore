class testingThread_FS extends Thread{
	FairSem fs;
	long waitingMillis;

	testingThread_FS(FairSem fs, long waitingMillis)
	{
		this.fs = fs;
		this.waitingMillis = waitingMillis;
		System.out.println("Thread "+this.getId()+":\tcreated");
	}
	
	public void run()
	{
		try {
			fs.P();
		} catch (InterruptedException ie){
			System.err.println("InterruptedException waiting");
		};

		//System.out.println("Thread "+this.getId()+":\twaiting some time...");
		try {
			Thread.sleep(waitingMillis);
		} catch (InterruptedException ie) {
			System.err.println("InterruptedException sleeping");
		}
		
		System.out.println("Thread "+this.getId()+":\tEsecuzione Completata");
		fs.V();
	}
}

public class FairSem_Test{
	private static FairSem fs;
	
	public static void main(String[] args){
		System.out.println("Main: starting");
		
		int val =2;
		int num_thread = 10;
		
		System.out.println();
		System.out.println("-----------------------------");
		System.out.println("Semaforo inizializzato a "+val);
		System.out.println("Numero dei thread: "+num_thread);
		System.out.println("-----------------------------");
		System.out.println();
		
		fs = new FairSem(val, true);
		testingThread_FS thread_array[] = new testingThread_FS[num_thread];
		
		//Creazione dei thread
		for (int i=0; i<num_thread; i++)
			thread_array[i] = new testingThread_FS(fs, 10);

		//Avvio dei thread
		for (int i=0; i<num_thread; i++)
			thread_array[i].start();

		//Attesa dei thread
		for (int i=0; i<num_thread; i++) {
			try {
				thread_array[i].join();
			} catch (InterruptedException ie) {
				System.err.println("Main: InterruptedException calling join method");
			}
		}
		System.out.println("Main: finishing");
	}
}