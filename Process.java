 class Process {
    int processId;
    int burstTime; 
    int IOTime;
    int priority;
    float defquantum;
    int arrivalTime;
    float WaitingTime=0;
    float TotalTime=0;
    float runTime = 0;
    double quantum;
    float age=0;
    boolean finished = false;
    public void print(){
        System.out.println("ID: " + processId + " / quantum: " + quantum);
    }
}