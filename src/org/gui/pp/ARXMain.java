package org.gui.pp;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.FocusAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class ARXMain extends JFrame{

    private JPanel arxMain;
    private JTabbedPane tabbedPane1;
    private JButton browseButton;
    private JTable table1;
    private JTable table2;
    private JButton nextButton;
    private JList list1;
    private JTable table3;
    private JButton addColumnButton;
    JFileChooser fc;
    String[] attributeList;
    HashMap<String,String> attributeSensitivity;
    HashMap<String,HashSet<String>> attributeDomain;


    public ARXMain() {
        add(arxMain);
        setSize(400,500);
        fc = new JFileChooser();
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showOpenDialog(tabbedPane1);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    //This is where a real application would open the file.
                    //log.append("Opening: " + file.getName() + "." + newline);
                    CSVFile Rd = new CSVFile();
                    ArrayList<String[]> Rs2 = Rd.ReadCSVfile(file);
                    attributeList = Rs2.get(0);
                    Rs2.remove(0);
                    createDomainForAttributes(Rs2);
                    MyModel NewModel = new MyModel(attributeList);
                    table1.setModel(NewModel);
                    NewModel.AddCSVData(Rs2);
                    System.out.println("Rows: " + NewModel.getRowCount());
                    System.out.println("Cols: " + NewModel.getColumnCount());
                    populateTable2();
                } else {
                    //log.append("Open command cancelled by user." + newline);
                }

            }
        });


        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attributeSensitivity =  new HashMap<>();
                TableModel mod = table2.getModel();
                for(int i=0; i< table2.getRowCount(); i++){
                    attributeSensitivity.put(attributeList[i], (String) mod.getValueAt(i,1));
                }
                tabbedPane1.setSelectedIndex(2);
                heirarchies();
            }
        });

    }


    public void populateTable2(){
        JComboBox cellValues;
        String[] identifiers = {"Identifier", "Sensitive", "Quasi-Identifying", "Insensitive"};
        cellValues = new JComboBox(identifiers);
        DefaultTableModel mod = new DefaultTableModel();
        mod.addColumn("Type");
        mod.addColumn("Attribute");
        table2.setModel(mod);
        TableColumn tc = table2.getColumn("Attribute");
         tc.setCellEditor(new DefaultCellEditor(cellValues));
        for(String x: attributeList) {
            mod.addRow(new Object[]{x,"Sensitive"});
        }

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();
        renderer.setToolTipText("Click to Choose Sensitivity");
        tc.setCellRenderer(renderer);


    }

    public void heirarchies(){
        ListModel<String> lm = new AbstractListModel<String>() {
            @Override
            public int getSize() {
                return attributeList.length;
            }

            @Override
            public String getElementAt(int index) {
                return attributeList[index];
            }
        };

        list1.setModel(lm);
    }

    public void createDomainForAttributes(ArrayList<String[]> dataRows){
        attributeDomain = new HashMap<>();
        for(int columnNumber = 0; columnNumber < attributeList.length; columnNumber++ ){
            for(int rowNumber = 0; rowNumber < dataRows.size(); rowNumber++){
                if(attributeDomain.containsKey(attributeList[columnNumber])){
                    attributeDomain.get(attributeList[columnNumber]).add(dataRows.get(rowNumber)[columnNumber]);
                }
                else{
                    attributeDomain.put(attributeList[columnNumber], new HashSet<>());
                }
            }
        }
        //System.out.println(attributeDomain.toString());
    }


}


class CSVFile {
    private final ArrayList<String[]> Rs = new ArrayList<String[]>();
    private String[] OneRow;

    public ArrayList<String[]> ReadCSVfile(File DataFile) {
        try {
            BufferedReader brd = new BufferedReader(new FileReader(DataFile));
            int count = 0;
            while (brd.ready()) {
                String st = brd.readLine();
                OneRow = st.split(",");
                Rs.add(OneRow);
                count++;
                //System.out.println(Arrays.toString(OneRow));
            } // end of while
        } // end of try
        catch (Exception e) {
            String errmsg = e.getMessage();
            System.out.println("File not found:" + errmsg);
        } // end of Catch
        return Rs;
    }// end of ReadFile method
}// end of CSVFile class

class MyModel extends AbstractTableModel {
    private String[] columnNames;
    private ArrayList<String[]> Data = new ArrayList<String[]>();

    public MyModel(String[] a){
        columnNames = a;
    }


    public void AddCSVData(ArrayList<String[]> DataIn) {
        this.Data = DataIn;
        this.fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;// length;
    }

    @Override
    public int getRowCount() {
        return Data.size();
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        return Data.get(row)[col];
    }
}
