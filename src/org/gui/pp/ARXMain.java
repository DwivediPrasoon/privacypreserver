package org.gui.pp;

import org.apache.hadoop.record.compiler.JFile;
import org.deidentifier.arx.*;
import org.deidentifier.arx.criteria.DistinctLDiversity;
import org.deidentifier.arx.criteria.EntropyLDiversity;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.criteria.RecursiveCLDiversity;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.Charset;
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
    private JButton goToConfigurationButton;
    private JComboBox delimiterBox;
    private JTabbedPane tabbedPane2;
    private JSlider suppressionRateSlider;
    private JSlider kValueSlider;
    private JComboBox chooseLDiversityAlgo;
    private JSlider selectLValue;
    private JSlider selectCValue;
    private JButton addToConfigurationButton;
    private JButton goToAnonymizerButton;
    private JComboBox selectSensitiveAttribute;
    private JSlider maxSnapshotSizeDataset;
    private JSlider maxSnapshotSizeSnapshot;
    private JSlider selectHistorySize;
    private JButton anonymizeButton;
    private JTable oldTable;
    private JTable resultTable;
    private JTextField minInfoLossScore;
    private JTextField maxInfoLossScore;
    private JButton exportToCSVButton;
    private JButton retryWithSomeOtherButton;
    private JButton goToExportDataButton;
    private JButton browseButton1;
    JFileChooser fc;
    String[] attributeList;
    HashMap<String,String> attributeSensitivity;
    HashMap<String,String> attributeType;
    HashMap<String,HashSet<String>> attributeDomain;
    HashMap<String, ArrayList<String[]>> attributeHeirarchyMap;
    Table3Model tm = null;
    File source = null;
    File destination = null;
    ARXConfiguration config;
    ARXResult result;
    Data data;
    ARXAnonymizer anonymizer;


    public ARXMain() {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        add(arxMain);
        setSize(1000,1000);
        fc = new JFileChooser();
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                                int returnVal = fc.showOpenDialog(tabbedPane1);

                                if (returnVal == JFileChooser.APPROVE_OPTION) {
                                    File file = fc.getSelectedFile();
                                    //This is where a real application would open the file.
                                    //log.append("Opening: " + file.getName() + "." + newline);
                                    source = file;
                                    CSVFile Rd = new CSVFile();
                                    ArrayList<String[]> Rs2 = Rd.ReadCSVfile(file, delimiterBox.getSelectedItem().toString());
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
                attributeType = new HashMap<>();
                TableModel mod = table2.getModel();
                for(int i=0; i< table2.getRowCount(); i++){
                    attributeSensitivity.put(attributeList[i], (String) mod.getValueAt(i,1));
                    attributeType.put(attributeList[i],(String)mod.getValueAt(i,2));
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
        goToConfigurationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (source != null) {
                        convertToRowWiseHierarchy(tm.columnWiseData);
                        prepareAnonymizer();
                        initializeSensitiveJComboBox();
                        tabbedPane1.setSelectedIndex(3);
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        addToConfigurationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(config == null)
                    config = ARXConfiguration.create();
                if(tabbedPane2.getSelectedIndex() == 0){
                    config.setSuppressionLimit(suppressionRateSlider.getValue()/100d);
                }
                else if(tabbedPane2.getSelectedIndex() == 1){
                    config.addPrivacyModel(new KAnonymity(kValueSlider.getValue()));
                }
                else if(tabbedPane2.getSelectedIndex() == 2 && selectSensitiveAttribute.getSelectedItem()!=null){
                    String algo = chooseLDiversityAlgo.getSelectedItem().toString();
                    switch (algo){
                        case "Distinct L Diversity": config.addPrivacyModel(new DistinctLDiversity(selectSensitiveAttribute.getSelectedItem().toString(),
                                selectLValue.getValue()));
                                selectSensitiveAttribute.removeItemAt(selectSensitiveAttribute.getSelectedIndex());
                                break;
                        case "Entropy L Diversity": config.addPrivacyModel(new EntropyLDiversity(selectSensitiveAttribute.getSelectedItem().toString(),
                                selectLValue.getValue()));
                            selectSensitiveAttribute.removeItemAt(selectSensitiveAttribute.getSelectedIndex());
                            break;
                        case "Recursive L Diversity": config.addPrivacyModel(new RecursiveCLDiversity(selectSensitiveAttribute.getSelectedItem().toString(),
                                selectCValue.getValue()/100d,
                                selectLValue.getValue()));
                            selectSensitiveAttribute.removeItemAt(selectSensitiveAttribute.getSelectedIndex());
                            break;
                    }
                }
            }
        });
        goToAnonymizerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane1.setSelectedIndex(4);
            }
        });
        anonymizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                anonymizer();
            }
        });
        retryWithSomeOtherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                anonymizer = null;
                config = null;
                tabbedPane1.setSelectedIndex(1);
            }
        });
        goToExportDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane1.setSelectedIndex(6);
                initialsizeExportData();
            }
        });
        exportToCSVButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportDataToCSV();
            }
        });
        browseButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int res = fc.showOpenDialog(tabbedPane1);
                if(res == JFileChooser.APPROVE_OPTION){
                    destination = fc.getCurrentDirectory();

                    exportToCSVButton.setEnabled(true);
                }
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            }
        });
    }

    public void setColumnColor(JTable table){
        table.setDefaultRenderer(table.getColumnClass(0), new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                Component rendererComp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                        row, column);

                String name = table.getColumnName(column);
                String sensitivity = attributeSensitivity.get(name);
                switch (sensitivity){
                    case "Identifying": rendererComp.setForeground(Color.red);
                                        rendererComp .setBackground(Color.white);
                                        break;
                    case "Sensitive" : rendererComp.setForeground(Color.green);
                                        rendererComp .setBackground(Color.white);
                                        break;
                    case "Insensitive" :rendererComp.setForeground(Color.blue);
                                        rendererComp .setBackground(Color.white);
                                        break;
                    case "Quasi-Identifying" :rendererComp.setForeground(Color.black);
                                        rendererComp .setBackground(Color.white);
                                        break;
                }
                return rendererComp ;
            }
        });

    }

    public void exportDataToCSV(){
        try {
            System.out.println(source.getName());
            File newFile = new File(destination.getAbsolutePath()+"/Anonymized_"+source.getName());
            DataHandle handle = result.getOutput();
            FileWriter fw = new FileWriter(newFile);
            int att;
            for(att=0; att<handle.getNumColumns()-1; att++){
                fw.write(handle.getAttributeName(att)+",");
            }
            fw.write(handle.getAttributeName(att)+"\n");
            for(int row=0, col; row<handle.getNumRows(); row++){
                for(col=0; col<handle.getNumColumns()-1; col++){
                    fw.write(handle.getValue(row,col)+",");
                }
                fw.write(handle.getValue(row,col)+"\n");
            }
            fw.close();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public void initialsizeExportData(){
        ARXLattice.ARXNode node = result.getGlobalOptimum();
        minInfoLossScore.setText(node.getLowestScore().toString());
        maxInfoLossScore.setText(node.getHighestScore().toString());
    }

    public void populateTable2(){
        JComboBox cellValues, dataType;
        String[] identifiers = {"Identifying", "Sensitive", "Quasi-Identifying", "Insensitive"};
        String[] dataTypes = {"Integer", "Decimal", "String", "NULL", "Date"};
        cellValues = new JComboBox(identifiers);
        dataType = new JComboBox(dataTypes);
        DefaultTableModel mod = new DefaultTableModel();
        mod.addColumn("Attribute Name");
        mod.addColumn("Privacy Type");
        mod.addColumn("Data Type");
        table2.setModel(mod);
        TableColumn tc = table2.getColumn("Privacy Type");
         tc.setCellEditor(new DefaultCellEditor(cellValues));
        TableColumn tc1 = table2.getColumn("Data Type");
        tc1.setCellEditor(new DefaultCellEditor(dataType));
        for(String x: attributeList) {
            mod.addRow(new Object[]{x,"Insensitive", "String"});
        }

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();
        renderer.setToolTipText("Click to Choose Sensitivity");
        tc.setCellRenderer(renderer);


    }

    public void initializeSensitiveJComboBox(){
        ArrayList<String> sensitiveAttibutes = new ArrayList<>();
        for(String attribute: attributeSensitivity.keySet()){
            if(attributeSensitivity.get(attribute).equals("Sensitive"))
                sensitiveAttibutes.add(attribute);
        }
        selectSensitiveAttribute.setModel(new DefaultComboBoxModel(sensitiveAttibutes.toArray()));
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

    //Function to convert columnWiseData for Attribute's heirarchy to rowWiseData
    public void convertToRowWiseHierarchy(HashMap<String, ArrayList<String[]>> columnWiseData){
        attributeHeirarchyMap = new HashMap<>();
        for(String attribute: columnWiseData.keySet()){
            ArrayList<String[]> tableColumnWise = columnWiseData.get(attribute);
            ArrayList<String[]> tableRowWise = new ArrayList<>();
            for(int rowNumber=0; rowNumber<tableColumnWise.get(0).length; rowNumber++) {
                String[] row = new String[tableColumnWise.size()];
                for(int columnNumber=0; columnNumber<tableColumnWise.size(); columnNumber++)
                    row[columnNumber] = tableColumnWise.get(columnNumber)[rowNumber];
                tableRowWise.add(row);
            }
            attributeHeirarchyMap.put(attribute,tableRowWise);
        }
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

    public void prepareAnonymizer(){
        try {
            DataSource dataSource = (DataSource) DataSource.createCSVSource(source, Charset.defaultCharset(), delimiterBox.getSelectedItem().toString().charAt(0), true);

            //Adding columns
            for(String x: attributeType.keySet()){
                String type = attributeType.get(x);
                switch(type) {
                    case "String": dataSource.addColumn(x, DataType.STRING);
                                    break;
                    case "Integer": dataSource.addColumn(x, DataType.INTEGER);
                                    break;
                    case "NULL" : dataSource.addColumn(x, DataType.NULL_VALUE);
                                    break;
                    case "Decimal": dataSource.addColumn(x, DataType.DECIMAL);
                                    break;
                    case "Date": dataSource.addColumn(x, DataType.DATE);
                                    break;
                }
            }

            //creating data sources
            data = Data.create(dataSource);

            //defining sensitivity of the attributes
            //{"Identifying", "Sensitive", "Quasi-Identifying", "Insensitive"}
            for(String x: attributeSensitivity.keySet()){
                String s = attributeSensitivity.get(x);
                switch (s){
                    case "Identifying" : data.getDefinition().setAttributeType(x, AttributeType.IDENTIFYING_ATTRIBUTE);
                                        break;
                    case "Sensitive": data.getDefinition().setAttributeType(x, AttributeType.SENSITIVE_ATTRIBUTE);
                                        break;
                    case "Quasi-Identifying" : data.getDefinition().setAttributeType(x, AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
                                                break;
                    case "Insensitive": data.getDefinition().setAttributeType(x, AttributeType.INSENSITIVE_ATTRIBUTE);
                                        break;
                }
            }

            //Making Heirarchies
            for(String attibute: attributeHeirarchyMap.keySet()){
                AttributeType.Hierarchy hierarchy = AttributeType.Hierarchy.create(attributeHeirarchyMap.get(attibute).iterator());
                data.getDefinition().setHierarchy(attibute,hierarchy);
            }


        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void anonymizer(){
        try {
            anonymizer = new ARXAnonymizer();
            anonymizer.setHistorySize(selectHistorySize.getValue());
            anonymizer.setMaximumSnapshotSizeDataset(maxSnapshotSizeDataset.getValue() / 100d);
            anonymizer.setMaximumSnapshotSizeSnapshot(maxSnapshotSizeSnapshot.getValue() / 100d);
            result = anonymizer.anonymize(data, config);

            if (result.isResultAvailable()) {
                initializeResultTables();
                tabbedPane1.setSelectedIndex(5);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    public void initializeResultTables(){
        oldTable.setModel(table1.getModel());
        setColumnColor(oldTable);
        oldTable.updateUI();
        resultTable.setModel(new AbstractTableModel() {
            DataHandle handle = result.getOutput();
            @Override
            public int getRowCount() {
                return handle.getNumRows();
            }

            @Override
            public int getColumnCount() {
                return handle.getNumColumns();
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return handle.getValue(rowIndex, columnIndex);
            }

            @Override
            public String getColumnName(int columnIndex){
                return handle.getAttributeName(columnIndex);
            }
        });
        setColumnColor(resultTable);
        resultTable.updateUI();
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