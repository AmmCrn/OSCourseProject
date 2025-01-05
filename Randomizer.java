import java.util.Random;

public class Randomizer {
    static private Process[] processList;
    
    public static Process[] Randomize() {
        Random rnd = new Random();
        Process[] processes = new Process[20];
        
        for (int i = 0; i < processes.length; i++) {
            Process process = new Process();
            
            process.processId = i;  // Sequential ID starting from ~1~ ZERO!!!!!!!!!!
            process.burstTime = rnd.nextInt(20) + 1;  // Random burst time 1-20
            process.priority = rnd.nextInt(5) + 1;    // Random priority 1-5
            process.defquantum = rnd.nextFloat() * 10;  // Random quantum 0-10
            process.quantum = process.defquantum;
            process.arrivalTime = rnd.nextInt(30); // Random arrival
            process.IOTime = rnd.nextInt(20); // Random IOTime
            
            // Store the process in the array
            processes[i] = process;
        }
        
        processList = processes;
        return processes;
    }
    
    public static void displayProcesses() {
        for(Process i : processList){
            System.out.println("Process ID: " + i.processId + 
                                 " | Burst Time: " + i.burstTime +
                                 " | Priority: " + i.priority +
                                 " | Default Quantum: " + String.format("%2f", i.defquantum) +
                                 " | Arrival Time : " + i.arrivalTime + 
                                 " | IO Time : " + i.IOTime);
        }
        
    }

    
    public static void main(String[] args) {
       // Randomizer dpqs = new Randomizer();
        
        
      //  dpqs.Randomize();
        
        
        System.out.println("Generated Processes:");
       // dpqs.displayProcesses();
        
        
        //System.out.println("\nBurst Times Array:");
        //for (int i = 0; i < burstTimes.length; i++) {
        //    System.out.println("Index " + i + ": " + burstTimes[i]);
        //}
    }
}