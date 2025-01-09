

/*
 * This class acts as the main component of the project
 * It effectively acts as both the process scheduler and the processor running processes
 * at least for our simulation purposes
 */
public class Scheduler {
    
	// simulated ready queue that simply stores process IDs, an empty space is denoted by -1
    int[] readyQueue = new int[20];
    int readyQueueSize = 0; // TODO: consider turning into a data structure
    Process[] processList; // list of processes generated by the Randomizer class
    Process current = null; // current running process in the processor
    int time = 0; // total time passed since Scheduler start

    /*
     * ensures all items in the queue are positioned correctly
     * TODO: consider changing to a data structure since this is spaghetti code effectively
     */
    private void updateQueue(){
        int size = 0;
        for (int i = 0; i < readyQueueSize; i++) {
            if(readyQueue[i] == -1 && readyQueue[i+1] != -1) {
                readyQueue[i] = readyQueue[i+1];
                readyQueue[i+1] = -1;
            }
            if(readyQueue[i] != -1)
                size++;
        }
        readyQueueSize = size;
        printQueue();
        System.out.println("Ready queue size: " + readyQueueSize); 
        // readyQueueSize--;
    }

    // constructor, set all readyQueue indexes to empty and generate random processes
    public Scheduler(){
        for (int i = 0; i < readyQueue.length; i++) {
            readyQueue[i] = -1;
        }
        
        processList = Randomizer.Randomize();
    }

    // priority calculation function as denoted in project proposal
    public void calculatePriority(int PID){
        //Priority = Base Priority + (Waiting Time / Total Time) + I/O Factor
        int priority = processList[PID].priority + processList[PID].priority + ((int) (processList[PID].WaitingTime / processList[PID].TotalTime)) + processList[PID].IOTime;
        processList[PID].priority = priority;
    }

    /*
     *  Quantum calculation function as denoted in project proposal
     *  TODO: 'previous burst time' should be the previous processes' time in the processor
     */
    public void calculateQuantum(int prev, int PID){
        //𝑁𝑒𝑤𝑇𝑖𝑚𝑒𝑄𝑢𝑎𝑛𝑡𝑢𝑚 = 𝛼 × Pr 𝑒 𝑣𝑖𝑜𝑢𝑠𝐵𝑢𝑟𝑠𝑡𝑇𝑖𝑚𝑒 + (1 − 𝛼) × 𝐷𝑒𝑓𝑎𝑢𝑙𝑡 𝑄𝑢𝑎𝑛𝑡𝑢𝑚
        double alpha = 0.5;
        double NewQuantum = alpha * processList[prev].burstTime + (1-alpha) * processList[PID].defquantum;
        System.out.println("calculateQuantum() run, result: " + NewQuantum);
        processList[PID].quantum = NewQuantum; 
    }
    /*
     * Aging function to prevent starvation, should be run on all processes in the readyQueue
     * if a process has been in the readyqueue for a laaarge amount of time, make sure it's priority is bumped
     */
    public void AgeProcess(Process prc){
        //quatum expires and process gets aged
        if (prc.WaitingTime > prc.defquantum*30) {
            prc.priority--;
        }
    }

    // debug function, delete later
    void printQueue(){
        for(int i : readyQueue)
            System.out.print(i + ", ");
            System.out.println();
    }

    /*
     * Switch current process for next one in queue with the highest priority (lowest int)
     */
    private Process contextSwitch(){
    	// first check if there is an actual process in the readyQueue
    	if(readyQueue[0] == -1)
    		return null;
    	
    	// if there is something in the readyQueue, select it for now
        int next_PID = readyQueue[0];
        int index = 0;
        // then loop through the readyQueue to see if any process has a higher
        // -priority than the currently selected one
        for(int i = 0; i < readyQueueSize; i++){
            if(processList[readyQueue[i]].priority < processList[next_PID].priority){
                next_PID = readyQueue[i];
                index = i;
            }
        }
        // once highest priority process has been found return it and update queue
        readyQueue[index] = -1;
        updateQueue();
        return processList[next_PID];
    }

    public void NextCycle(){
        // when a process arrives, send it to the ready queue
        for (int i = 0; i < processList.length; i++) {
            if(processList[i].arrivalTime == time){
                readyQueue[readyQueueSize++] = i;
                updateQueue();//TODO REMINDER--in the simulation this might have to be a readyq
                printQueue();
            }
        }
        
        // a milisecond of time has passed TODO: consider a loop going at 0.01ms, i.e. 100 times
        time++;

        // if no process currently running; get new one from the readyQueue
        if(current == null) {
        	current = contextSwitch(); // will return null if queue is empty
        	if(current == null) return; // if the current process is still null, there is no work being done
            /* old code, could be more useful?
             * if(readyQueue[0] != -1)
                current = contextSwitch(current);
                else return;
                */
        }
        System.out.println("QUANTUM RN "+ current.processId +" is " +current.quantum);
        current.runTime += 1; // 1ms of runtime done
        current.quantum -= 1; // 1ms less of quantum remaining

        // check if the process is finished by seeing if the runtime is larger than it's bursttime
        
        if(current.runTime > current.burstTime){
            current.finished = true;
            System.out.println((current.processId+1) + " finished, time for " + (readyQueue[0]+1));
            
            // switch to next process, if any
            current = contextSwitch();
            
            // if nothing next in queue, do not continue the function
            if(current == null)
            	return;
        }
        
        // check if a process' quantum has elapsed
        if(current.quantum <= 0){
        	// calculate new priority, and send back to the end of the queue
            calculatePriority(current.processId);
            readyQueue[readyQueueSize++] = current.processId;
            
            // is there anything in the ready queue?
            Process nextProcess = contextSwitch(); // -> never returns null, since prevProcess is sent to queue
            
            // calculate its new dynamic quantum based on previous tasks' performance
            calculateQuantum(current.processId, nextProcess.processId);
            
            
            // add on any excess runtime in the simulation from the previous process
            // note: current.quantum is either 0 or negative, hence the + operator
            nextProcess.quantum += current.quantum;
            nextProcess.runTime += current.quantum;
            System.out.println((current.processId+1) + " quantum ended, time for " + (nextProcess.processId+1));
            
            current = nextProcess;
        }

        System.out.println("[" + time + "] Quantum left for " + (current.processId+1) + ": " + current.quantum);
    }

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        Randomizer.displayProcesses();
        for (int i = 0; i < 50; i++) {
            scheduler.NextCycle();
        }
    }
}
