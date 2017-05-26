public class Message<T>{
	
	//Messaggio
	public T data;
	//Porta del mittente
	public SynchPort<T> port;
	//Id del thread che invia il messaggio
	public long id_thread;
	
	public Message(){
		this.data = null;
		this.port = null;
		this.id_thread = Thread.currentThread().getId();
	}
	
	public Message(T data, SynchPort<T> port){
		this.data = data;
		this.port = port;
		this.id_thread = Thread.currentThread().getId();
	}
	
	public Message(T data, SynchPort<T> port, long id_thread){
		this.data = data;
		this.port = port;
		this.id_thread = id_thread;
	}
	
	public T getData(){
		return data;
	}

	public SynchPort<T> getPort(){
		return port;
	}
	
	public int getIdThread(){
		return (int) id_thread;
	}
	
}