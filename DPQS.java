public class DPQS {
    
    int[] readyQueue = new int[20];
    int readyQueueSize = 0;
    Process[] Processes;
    Process current = null;
    int time = 0;

    private void updateQueue(){
        int size = 0;
        for (int i = 0; i < readyQueueSize; i++) {
            if(readyQueue[i] == -1 && readyQueue[i+1] != -1)
                readyQueue[i] = readyQueue[i+1];
            if(readyQueue[i] != -1)
                size++;
        }
        readyQueueSize = size;
        // readyQueueSize--;
    }

    public DPQS(){
        for (int i = 0; i < readyQueue.length; i++) {
            readyQueue[i] = -1;
        }
        
        Processes = Randomizer.Randomize();
    }

    public void calculatePriority(int PID){
        //Priority = Base Priority + (Waiting Time / Total Time) + I/O Factor
        int priority = Processes[PID].priority + Processes[PID].priority + ((int) (Processes[PID].WaitingTime / Processes[PID].TotalTime)) + Processes[PID].IOTime;
        Processes[PID].priority = priority;
    }

    public void CalculateQuantum(int prev, int PID){
        //𝑁𝑒𝑤𝑇𝑖𝑚𝑒𝑄𝑢𝑎𝑛𝑡𝑢𝑚 = 𝛼 × Pr 𝑒 𝑣𝑖𝑜𝑢𝑠𝐵𝑢𝑟𝑠𝑡𝑇𝑖𝑚𝑒 + (1 − 𝛼) × 𝐷𝑒𝑓𝑎𝑢𝑙𝑡 𝑄𝑢𝑎𝑛𝑡𝑢𝑚
        double alpha = 0.5;
        double NewQuantum = alpha * Processes[prev].burstTime + (1-alpha) * Processes[PID].defquantum;
        System.out.println("What the fuck is the new quant: " + NewQuantum);
        Processes[PID].quantum = NewQuantum; 
    }
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

    private Process contextSwitch(Process current){
        if(readyQueue[0] != -1){
            int next_PID = readyQueue[0];
            int index = 0;
            for(int i = 0; i < readyQueueSize; i++){
                if(Processes[readyQueue[i]].priority < Processes[next_PID].priority){
                    next_PID = readyQueue[i];
                    index = i;
                }
            }
            readyQueue[index] = -1;
            updateQueue();
            return Processes[next_PID];
            //System.out.println("Current process : " + current.processId);
        } else {
            //time++;
            return null;
        }
    }

    public void NextCycle(){
        // when a process arrives, send it to the ready queue
        for (int i = 0; i < Processes.length; i++) {
            if(Processes[i].arrivalTime == time){
                readyQueue[readyQueueSize++] = i;
                updateQueue();
                printQueue();
            }
        }
        time++;

        // if no process currently running; get new one
        if(current == null) {
            if(readyQueue[0] != -1)
                current = contextSwitch(current);
                else return;
        }

        // run the current process
        current.runTime += 1;
        current.quantum -= 1;

        // is the process finished OR its quantum finished
        if(current.runTime > current.burstTime){
            // process has finished
            current.finished = true;
            System.out.println((current.processId+1) + " finished, time for " + (readyQueue[0]+1));
            // switch to next process
            if(readyQueue[0] != -1) {
                current = Processes[readyQueue[0]];
                // add runtime to next process TODO
                readyQueue[0] = -1;
                updateQueue();
                return;
            } else {
                current = null;
                return;
            }
        }
        if(current.quantum <= 0){
            // process is not finished but its quantum is
            calculatePriority(current.processId);
            // is there anything in the ready queue?
            if(readyQueue[0] != -1){
                Process nextProcess = Processes[readyQueue[0]];
                CalculateQuantum(current.processId, nextProcess.processId);
                nextProcess.quantum += current.quantum;
                nextProcess.runTime += current.quantum;
                readyQueue[readyQueueSize] = current.processId;
                readyQueue[0] = -1;
                updateQueue();
                System.out.println((current.processId+1) + " quantum ended, time for " + (nextProcess.processId+1));
                current = nextProcess;
            } else {
                current = null;
                return;
            }
        }

        System.out.println("[" + time + "] Quantum left for " + (current.processId+1) + ": " + current.quantum);
    }

    public static void main(String[] args) {
        DPQS scheduler = new DPQS();
        Randomizer.displayProcesses();
        for (int i = 0; i < 50; i++) {
            scheduler.NextCycle();
        }
    }
}
