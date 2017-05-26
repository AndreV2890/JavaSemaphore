CC = javac
FLAGS = 

FairSem_Test:	
	$(CC) $(FLAGS) FairSem.java FairSem_Test.java
	
SynchPort_Test:
	$(CC) $(FLAGS) SynchPort.java SynchPort_Test.java Message.java FairSem.java
	
PortArray_Test:
	$(CC) $(FLAGS) PortArray_Test.java PortArray.java SynchPort.java Message.java FairSem.java
	
Mailbox_A_Test:
	$(CC) $(FLAGS) Mailbox_A_Test.java Mailbox_A.java SynchPort.java PortArray.java Message.java FairSem.java
	
Mailbox_B_Test:
	$(CC) $(FLAGS) Mailbox_B_Test.java Mailbox_B.java SynchPort.java PortArray.java Message.java FairSem.java

all: FairSem_Test SynchPort_Test PortArray_Test Mailbox_A_Test Mailbox_B_Test

clean:
	rm -rf *.class 


	