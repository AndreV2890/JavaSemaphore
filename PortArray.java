import java.util.List;
import java.util.ArrayList;

class PortStruct<T>{
	public SynchPort<T> port;
	public int blocked;
}

class Msg_Rcv<T> extends Message<T>{
	public Message<T> message;
	public int num_port;
	
	public Msg_Rcv(Message<T> mess, int num_port){
		this.message = mess;		
		this.num_port = num_port;
	}
}

public class PortArray<T>{
	//Array of SynchPort
	private ArrayList<PortStruct<T>> port_array;
	
	//Semaforo per la mutua esclusione
	private FairSem mutex;
	//Semaforo per il dato disponibile
	private FairSem data_available;
	
	//Indice per la gestione RoundRobin dell'array delle porte
	private int RR_index;
	
	//contatori dei thread sender bloccati
	private int cont_rcv_blocked;
	
	public PortArray(int dim){
		port_array = new ArrayList<PortStruct<T>>();
		for (int i=0; i<dim; i++) {
			PortStruct<T> elem = new PortStruct<T>();
			elem.port = new SynchPort<T>();
			elem.blocked = 0;
			port_array.add(elem);
		}
				
		mutex = new FairSem(1);
		data_available = new FairSem(0);
		
		RR_index = -1;
		cont_rcv_blocked = 0;
	}
	
	public void send(Message<T> mess, int p) throws InterruptedException{
		if(p > port_array.size()){
			System.out.println("Indice della porta maggiore della dimensione dell'array");
			return;
		}
		
		mutex.P();
		port_array.get(p).blocked++;
		while(cont_rcv_blocked!=0){
			cont_rcv_blocked--;
			data_available.V();
		}
		mutex.V();
		
		port_array.get(p).port.send(mess);
	}
	
	public Msg_Rcv<T> receive(int v[], int n) throws InterruptedException{
		Msg_Rcv<T> mess_rcv;
		Message<T> mess;
		mutex.P();	
		
		//verifico se nelle porte contenute v è presente un messaggio
		//altrimenti mi blocco sul semaforo data_available
		while(!check_port(v, n)){
			cont_rcv_blocked++;
			mutex.V();
			data_available.P();
			mutex.P();
		}
		
		port_array.get(RR_index).blocked--;
		
		mess = port_array.get(RR_index).port.receive();
		mess_rcv = new Msg_Rcv<T>(mess, RR_index);
		mutex.V();
				
		return mess_rcv;
	}
	
	
	private boolean check_port(int v[], int n) throws InterruptedException{
		for(int i = 0; i< port_array.size(); i++){
			RR_index = (RR_index+1) % port_array.size();
			if((port_array.get(RR_index).blocked > 0))
				for(int j = 0; j < n; j++)
					if(RR_index == v[j]){
						return true;
					}
		}
		return false;
	}
}