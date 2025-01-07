package dpqs;

/*
 * Class containing all the information required of a process
 */
class Process {
	int processId; // process identifier 0-19
    
    int burstTime; // time of activity needed to finish the task
    int IOTime; // time of IO needed to finish the task
    
    float defquantum; // default quantum, the one it starts with
    int arrivalTime; // denotes at what time the process is sent to readyQueue

    int priority;
    float WaitingTime = 0; // time waiting in the readyQueue
    float runTime = 0; // time running as current process
    float TotalTime = 0; // total time both ran and waited
    double quantum; // quantum that is to be changed dynamically
    
    float age=0; // TODO: is this to prevent starvation in the aging process? aren't we just using waitingTime for that?
    
    boolean finished = false;
    // debug function
    public void print(){
        System.out.println("ID: " + processId + " / quantum: " + quantum);
    }
}