import java.util.LinkedList;
import java.util.Queue;

class Priority_Port_List<T>{
	public SynchPort<T> port;
	public int priority;
	
	public Priority_Port_List(SynchPort<T> port, int priority){
		this.port = port;
		this.priority = priority;
	}
}

public class Mailbox_B extends Thread{
	private int buffer[];
	private int buffer_id_sender[];
	
	private PortArray<Integer> ports; 
	
	private LinkedList<Priority_Port_List<Integer>> waiting_senders;
	
	//gestione circolare dei buffer
	private int index;
	private int last;
	private int count;
	private final int dim = 4;
	
	private final int INSERT_DATA = 0;
	private final int REMOVE_DATA = 1;
	
	public Mailbox_B(){
		this.last = 0;
		this.index = 0;
		this.count = 0;
		this.buffer = new int[dim];
		this.buffer_id_sender = new int[dim];
		for(int i = 0; i < dim; i++){
			this.buffer[i] = -1;
			this.buffer_id_sender[i] = -1;
		}
		
		ports = new PortArray<Integer>(2);
		waiting_senders = new LinkedList<Priority_Port_List<Integer>>();
		
		setDaemon(true);
	}
	
	//Servizio offerto ai produttori
	public void request_to_insert(SynchPort<Integer> port, int priority) throws InterruptedException{
		ports.send(new Message<Integer>(priority, port), INSERT_DATA);
	}
	
	//Servizio offerto al consumatore
	public void request_to_remove(SynchPort<Integer> port) throws InterruptedException{
		ports.send(new Message<Integer>(null, port), REMOVE_DATA);
	}
	
	//Rimuove un dato dal buffer e lo invia alla porta "Port"
	private void send_data(SynchPort<Integer> Port) throws InterruptedException{
		int value;
		int id;
		
		value = buffer[last];
		id = buffer_id_sender[last];
			
		last = (last+1)%dim;
		count--;
		
		Port.send(new Message<Integer>(value, null, id));
	}
	
	//Riceve un dato dalla porta "Port" e lo memorizza nel buffer
	private void receive_data(SynchPort<Integer> Port) throws InterruptedException{
		Message<Integer> m = Port.receive();
		buffer[index] = m.getData();
		buffer_id_sender[index] = m.getIdThread();
		
		index = (index+1)%dim;
		count++;		
	}
	
	public void run(){
		try{
			while(true){
				Msg_Rcv<Integer> msg_rcv = ports.receive(new int[] {INSERT_DATA, REMOVE_DATA},2);
				switch(msg_rcv.num_port){
					case INSERT_DATA:{
						switch(count){
							case 4:{
								Message<Integer> mess = msg_rcv.message;
								int position = 0;				
								while (waiting_senders.size() > position && mess.getData() > waiting_senders.get(position).priority) 
									position++;														
								waiting_senders.add(position,new Priority_Port_List<Integer>(mess.getPort(),mess.getData()));
								break;
							}
							default:{
								receive_data(msg_rcv.message.getPort());
								break;
							}
						}						
						break;
					}
					case REMOVE_DATA:{
						switch(count){
							case 4:{
								send_data(msg_rcv.message.getPort());
								if (waiting_senders.size() > 0){
									receive_data(waiting_senders.remove().port);
								}	
								break;
							}
							default:{
								send_data(msg_rcv.message.getPort());
								break;
							}
						}
						break;
					}
				}			
			}			
		}catch (InterruptedException e) {
			System.out.println("exception found in the server behaviour");
		}
	}
}