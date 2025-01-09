public class Bridge {
    Scheduler SCHEDULER;
    final int processLength = 20;
    public Process[] generateProcesses()
    {
        SCHEDULER = new Scheduler();
        return SCHEDULER.processList;
    }

    public String getFinishTime(int PID)
    {
        Process p = SCHEDULER.processList[PID];
        if(!p.finished)
            return "Not finished";
        
        return "" + (p.runTime + p.WaitingTime);
    }

    public boolean scheduleFinished(){
        return SCHEDULER.allFinished();
    }

    public String isFinished(int PID){
        return SCHEDULER.processList[PID].finished ? "Yes" : "No";
    }

    public Process[] getProcessList()
    {
        if(SCHEDULER == null)
            return null;

        return SCHEDULER.processList;

    }
    public Process getCurrentProcess()
    {
        if(SCHEDULER == null)
            return null;
        return SCHEDULER.current;
    }

    public void NextCycle()
    {
        SCHEDULER.nextCycle();
    }

    public Process getNextProcess()
    {
        if(SCHEDULER == null)
            return null;

        int[] readyQueueRef = SCHEDULER.readyQueue;
        int queueSize = SCHEDULER.readyQueueSize;
        Process[] pListRef = SCHEDULER.processList;
        // first check if there is an actual process in the readyQueue
    	if(readyQueueRef[0] == -1)
            return null;
        
        // if there is something in the readyQueue, select it for now
        int next_PID = readyQueueRef[0];
        // then loop through the readyQueue to see if any process has a higher
        // -priority than the currently selected one
        for(int i = 0; i < queueSize; i++){
            if(pListRef[readyQueueRef[i]].priority < pListRef[next_PID].priority){
                next_PID = readyQueueRef[i];
            }
        }
        // once highest priority process has been found return it and update queue
        return pListRef[next_PID];
    }
}
