import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class cpusched {
    private JFrame frame;
    private JTable processTable;
    private DefaultTableModel tableModel;
    private JTextField burstTimeField, quantumField, priorityField, finishTimeField;
    private JTextField currProcDisplay, nextInQDisplay;
    private JPanel ganttChartPanel;

    public cpusched() {
        // Main frame
        frame = new JFrame("CPU Scheduler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Input panel with buttons
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JButton newProcButton = new JButton("New Processes");
        inputPanel.add(newProcButton);
        JButton finishCycleButton = new JButton("Finish Cycle");
        inputPanel.add(finishCycleButton);
        JButton nextStepButton = new JButton("Next Step");
        inputPanel.add(nextStepButton);

        // Add Clear Gantt Chart button
        JButton clearGanttButton = new JButton("Clear Gantt Chart");
        inputPanel.add(clearGanttButton);  // Add the clear button to the panel

        // Table with Process Name, Priority, and Burst Time columns
        String[] columnNames = {"Process Name", "Priority", "Burst Time"};
        tableModel = new DefaultTableModel(columnNames, 0);
        processTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(processTable);

        // Panel for process attributes
        JPanel attributePanel = new JPanel();
        attributePanel.setLayout(new GridLayout(5, 2));

        attributePanel.add(new JLabel("Burst Time:"));
        burstTimeField = new JTextField();
        burstTimeField.setEditable(false);
        attributePanel.add(burstTimeField);

        attributePanel.add(new JLabel("Quantum:"));
        quantumField = new JTextField();
        quantumField.setEditable(false);
        attributePanel.add(quantumField);

        attributePanel.add(new JLabel("Priority:"));
        priorityField = new JTextField();
        priorityField.setEditable(false);
        attributePanel.add(priorityField);

        attributePanel.add(new JLabel("Finish Time:"));
        finishTimeField = new JTextField();
        finishTimeField.setEditable(false);
        attributePanel.add(finishTimeField);

        // Panels for current running and next in queue
        JPanel pannelforcurrdisplay = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel currentR = new JLabel("Currently Running:");
        currProcDisplay = new JTextField(10);
        pannelforcurrdisplay.add(currentR);
        pannelforcurrdisplay.add(currProcDisplay);

        JPanel panelfornextinq = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nextInQ = new JLabel("Next in Queue:");
        nextInQDisplay = new JTextField(10);
        panelfornextinq.add(nextInQ);
        panelfornextinq.add(nextInQDisplay);

        // Sub-panel to group current and next panels
        JPanel eastSubPanel = new JPanel();
        eastSubPanel.setLayout(new BoxLayout(eastSubPanel, BoxLayout.Y_AXIS));
        eastSubPanel.add(pannelforcurrdisplay);
        eastSubPanel.add(Box.createVerticalStrut(10)); // Add spacing
        eastSubPanel.add(panelfornextinq);

        // Parent panel to group the table and eastSubPanel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(eastSubPanel, BorderLayout.EAST);

        // Gantt chart panel
        ganttChartPanel = new JPanel();
        ganttChartPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        ganttChartPanel.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));

        // Wrap the Gantt chart panel in a JScrollPane
        JScrollPane ganttScrollPane = new JScrollPane(ganttChartPanel);
        ganttScrollPane.setPreferredSize(new Dimension(780, 150)); // Adjust height as needed
        ganttScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        ganttScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        // Add action listener to New Processes button
        newProcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear the Gantt chart and the table
                tableModel.setRowCount(0);
                ganttChartPanel.removeAll();  // Clear Gantt chart
                ganttChartPanel.revalidate();  // Refresh Gantt chart display
                
                for (int i = 1; i <= 20; i++) {
                    String processName = "P" + i;
                    int priority = (int) (Math.random() * 10) + 1; // Random priority
                    int burstTime = (int) (Math.random() * 10) + 1; // Random burst time
                    tableModel.addRow(new Object[]{processName, priority, burstTime});
                }

                // Clear the "Next in Queue" field after resetting the processes
                nextInQDisplay.setText("None");
            }
        });

        // Add action listener to Clear Gantt Chart button
        clearGanttButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear the Gantt chart whenever the "Clear Gantt Chart" button is clicked
                ganttChartPanel.removeAll();
                ganttChartPanel.revalidate();  // Refresh the Gantt chart display
            }
        });

        // Add selection listener to update side panel fields
        processTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                int selectedRow = processTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Update fields with selected process data
                    burstTimeField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    priorityField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    currProcDisplay.setText(tableModel.getValueAt(selectedRow, 0).toString());
                }
            }
        });
        
        finishCycleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Loop until all processes are executed
                while (tableModel.getRowCount() > 0) {
                    // Find the process with the highest priority (lowest number)
                    int highestPriorityRow = -1;
                    int highestPriority = Integer.MAX_VALUE;
        
                    // Loop to find the highest priority process
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        int priority = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
                        if (priority < highestPriority) {
                            highestPriority = priority;
                            highestPriorityRow = i;
                        }
                    }
        
                    // Get the process details
                    String processName = (String) tableModel.getValueAt(highestPriorityRow, 0);
                    int burstTime = Integer.parseInt(tableModel.getValueAt(highestPriorityRow, 2).toString());
        
                    // Add the process to the Gantt chart
                    JLabel processBlock = new JLabel(processName + " (" + burstTime + ")");
                    processBlock.setOpaque(true);
                    processBlock.setBackground(Color.CYAN);
                    processBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    ganttChartPanel.add(processBlock);
                    ganttChartPanel.revalidate();
        
                    // Update "Currently Running"
                    currProcDisplay.setText(processName);
        
                    // Remove the process from the table after execution
                    tableModel.removeRow(highestPriorityRow);
        
                    // Update the "Next in Queue" based on the highest priority of remaining processes
                    if (tableModel.getRowCount() > 0) {
                        int nextHighestPriority = Integer.MAX_VALUE;
                        int nextHighestPriorityRow = -1;
        
                        // Find the process with the next highest priority
                        for (int i = 0; i < tableModel.getRowCount(); i++) {
                            int priority = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
                            if (priority < nextHighestPriority) {
                                nextHighestPriority = priority;
                                nextHighestPriorityRow = i;
                            }
                        }
        
                        String nextProcessName = (String) tableModel.getValueAt(nextHighestPriorityRow, 0);
                        nextInQDisplay.setText(nextProcessName);
                    } else {
                        nextInQDisplay.setText("None");
                    }
                }
            }
        });

        nextStepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tableModel.getRowCount() > 0) {
                    // Find the process with the highest priority (lowest number)
                    int highestPriorityRow = -1;
                    int highestPriority = Integer.MAX_VALUE;
                    
                    // Loop to find the highest priority process
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        int priority = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
                        if (priority < highestPriority) {
                            highestPriority = priority;
                            highestPriorityRow = i;
                        }
                    }
        
                    // Get the process details
                    String processName = (String) tableModel.getValueAt(highestPriorityRow, 0);
                    int burstTime = Integer.parseInt(tableModel.getValueAt(highestPriorityRow, 2).toString());
        
                    // Add the process to the Gantt chart
                    JLabel processBlock = new JLabel(processName + " (" + burstTime + ")");
                    processBlock.setOpaque(true);
                    processBlock.setBackground(Color.CYAN);
                    processBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    ganttChartPanel.add(processBlock);
                    ganttChartPanel.revalidate();
        
                    // Update "Currently Running"
                    currProcDisplay.setText(processName);
                    
                    // Remove the process from the table after execution
                    tableModel.removeRow(highestPriorityRow);
        
                    // Update the "Next in Queue" based on the highest priority of remaining processes
                    if (tableModel.getRowCount() > 0) {
                        int nextHighestPriority = Integer.MAX_VALUE;
                        int nextHighestPriorityRow = -1;
                        
                        // Find the process with the next highest priority
                        for (int i = 0; i < tableModel.getRowCount(); i++) {
                            int priority = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
                            if (priority < nextHighestPriority) {
                                nextHighestPriority = priority;
                                nextHighestPriorityRow = i;
                            }
                        }
        
                        String nextProcessName = (String) tableModel.getValueAt(nextHighestPriorityRow, 0);
                        nextInQDisplay.setText(nextProcessName);
                    } else {
                        nextInQDisplay.setText("None");
                    }
                }
            }
        });

        // Add components to the frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER); // Add parent panel with table and eastSubPanel
        frame.add(attributePanel, BorderLayout.WEST);
        frame.add(ganttScrollPane, BorderLayout.SOUTH); // Add the JScrollPane for Gantt Chart

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(cpusched::new);
    }
}
