package org.gui.pp;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JButton browseForACsvButton;
    private JButton submitButton;
    JFileChooser fc;
    String[] attributeList;
    HashMap<String,String> attributeSensitivity;
    HashMap<String,HashSet<String>> attributeDomain;
    Table3Model tm = null;


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
                                    ArrayList<String[]> Rs2 = Rd.ReadCSVfile(file, ",");
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

        list1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(list1.getSelectedValue()!= null) {
                    if (tm == null) {
                        tm = new Table3Model(attributeDomain, attributeList[0]);
                        table3.setModel(tm);

                    }
                    String changed = (String)list1.getSelectedValue();
                    tm.updateTable(changed);
                }
            }
        });
        addColumnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(tm != null)
                    tm.addColumn();

            }
        });
        browseForACsvButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showOpenDialog(tabbedPane1);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    tm.ReadFromCsv(fc.getSelectedFile());

                }
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
        ArrayList<String> quasiIdentifyingAttributes = new ArrayList<>();
        Set<String> keys = attributeSensitivity.keySet();
        Iterator<String> i = keys.iterator();
        while(i.hasNext()){
            String attribute = i.next();
            if(attributeSensitivity.get(attribute).equals("Quasi-Identifying"))
                quasiIdentifyingAttributes.add(attribute);
        }
        System.out.println(quasiIdentifyingAttributes);
        ListModel<String> lm = new AbstractListModel<String>() {
            @Override
            public int getSize() {
                return quasiIdentifyingAttributes.size();
            }

            @Override
            public String getElementAt(int index) {
                return quasiIdentifyingAttributes.get(index);
            }
        };

        list1.setModel(lm);
        list1.setSelectedIndex(0);
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

    public void Anonymize(){

    }


}


class CSVFile {
    private final ArrayList<String[]> Rs = new ArrayList<String[]>();
    private String[] OneRow;

    public ArrayList<String[]> ReadCSVfile(File DataFile, String dilimiter) {
        try {
            BufferedReader brd = new BufferedReader(new FileReader(DataFile));
            int count = 0;
            while (brd.ready()) {
                String st = brd.readLine();
                OneRow = st.split(dilimiter);
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

class Table3Model extends AbstractTableModel{
    HashMap<String, HashSet<String>> local;
    HashMap<String, ArrayList<String[]>> columnWiseData;
    String currentAttribute;
    ArrayList<String[]> currentData;


    public Table3Model(HashMap<String, HashSet<String>> local, String currentAttribute){
        this.local = local;
        columnWiseData = new HashMap<>();
        processColumnWiseData(local);
        currentData = columnWiseData.get(currentAttribute);
        this.currentAttribute = currentAttribute;

    }

    public void processColumnWiseData(HashMap<String, HashSet<String>> local){
        Set<String> attributes = local.keySet();
        Iterator<String> iterator = attributes.iterator();
        while(iterator.hasNext()){
            String attribute = iterator.next();
            HashSet<String> columnValues = local.get(attribute);
            Iterator<String> iterator1 = columnValues.iterator();
            ArrayList<String[]> attributeData = new ArrayList<>();

            String[] firstColumn =  new String[columnValues.size()+1];
            int count = 1;
            firstColumn[0] = "level 0";
            while(iterator1.hasNext()){
                firstColumn[count++] = iterator1.next();
            }
            attributeData.add(firstColumn);
            columnWiseData.put(attribute,attributeData);
        }

    }

    @Override
    public int getRowCount() {
        return currentData.get(0).length-1;
    }

    @Override
    public int getColumnCount() {
        int i = currentData.size();
        //System.out.println(i);
        return i;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return currentData.get(columnIndex)[0];
    }



    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object i =  currentData.get(columnIndex)[rowIndex+1];
        //System.out.println((String)i);
        return i;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        currentData.get(columnIndex)[rowIndex] = (String)aValue;
    }

    public void updateTable(String attributeSelected){
        currentAttribute = attributeSelected;
        currentData = columnWiseData.get(currentAttribute);
        this.fireTableStructureChanged();

    }

    public void addColumn(){
        String ColumnName = "level"+getColumnCount();
        String[] data = new String[currentData.get(0).length];
        data[0] = ColumnName;
        for(int i=1; i<data.length; i++)
            data[i] = "*";
        currentData.add(data);
        this.fireTableStructureChanged();

    }

    public void ReadFromCsv(File file){
            CSVFile Rd = new CSVFile();
            currentData = new ArrayList<>();
            ArrayList<String[]> data = Rd.ReadCSVfile(file, ";");
            if (data.get(0).length != 0) {
                int columnSize = data.get(0).length;
                int rowSize = data.size();
                for(int i=0; i< columnSize; i++){
                    String[] tmp = new String[rowSize+1];
                    tmp[0] = "level"+i;
                    for(int j=1; j< rowSize+1; j++){
                        tmp[j] = data.get(j-1)[i];
                    }
                    currentData.add(tmp);
                }
                columnWiseData.put(currentAttribute,currentData);
                this.fireTableStructureChanged();
            }
    }



}