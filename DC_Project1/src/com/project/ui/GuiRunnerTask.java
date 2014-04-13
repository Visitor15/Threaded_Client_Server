package com.project.ui;

import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.project.dc_server.DCServerTask;
import com.project.framework.Task;
import com.project.tasks.SimplePersistentTask;

public class GuiRunnerTask extends SimplePersistentTask {
	
	private JList FileListGui;

    private JList ServerListGui;
    private JList ClientListGui;
    public JPanel Pan;

    public DefaultListModel fList;
    public DefaultListModel cList;
    public DefaultListModel sList;

    private ArrayList<String> filers;
    private ArrayList<String> servers;
    private ArrayList<String> clients;

	public GuiRunnerTask() {
		setTaskId("GUI");
	}
	
	@Override
	public void executeTask() {
		boolean exists;
        clients = new ArrayList<String>();
        servers = new ArrayList<String>();
        filers = new ArrayList<String>();
        
        
        JFrame frame = new JFrame("Server #ID:" + DCServerTask.serverID);
        $$$setupUI$$$();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(Pan);
        frame.pack();
        frame.setVisible(true);
        
        
        while (true) {


            for (int i = 0; i < DCServerTask.clientfiles.size(); i++) {
                filers.add(DCServerTask.clientfiles.get(i).fName);
                for (int j = 0; j < DCServerTask.clientfiles.get(i).hosts.size(); j++) {
                    clients.add(DCServerTask.clientfiles.get(i).hosts.get(j).IPaddress + ":" + DCServerTask.clientfiles.get(i).hosts.get(j).port);
                }
            }

            for (int i = 0; i < DCServerTask.servers.size(); i++) {
                servers.add(DCServerTask.servers.get(i).serverIP);
            }

            for (int i = 0; i < servers.size(); i++) {
                if (!sList.contains(servers.get(i)))
                    sList.addElement(servers.get(i));
            }
            for (int i = 0; i < clients.size(); i++) {
                if (!cList.contains(clients.get(i)))
                    cList.addElement(clients.get(i));
            }
            for (int j = 0; j < filers.size(); j++) {
                if (!fList.contains(filers.get(j)))
                    fList.addElement(filers.get(j));
            }

            for (int i = 0; i < sList.size(); i++) {
                if (!servers.contains(sList.get(i)))
                    sList.remove(i);
            }
            for (int i = 0; i < cList.size(); i++) {
                if (!clients.contains(cList.get(i)))
                    cList.remove(i);
            }
            for (int i = 0; i < fList.size(); i++) {
                if (!filers.contains(fList.get(i)))
                    fList.remove(i);
            }

            clients.clear();
            servers.clear();
            filers.clear();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            	System.out.println("ERROR WITH GUI RUNNER!");
            	
                System.out.println(e);
            }
        }
	}

	@Override
	public Task fromBytes(byte[] byteArray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onFinished() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] toBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	private void createUIComponents() {

        fList = new DefaultListModel();
        sList = new DefaultListModel();
        cList = new DefaultListModel();
        FileListGui = new JList(fList);
        ServerListGui = new JList(sList);
        ClientListGui = new JList(cList);
        // TODO: place custom component creation code here
    }
	
	 /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        Pan = new JPanel();
        Pan.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(9, 1, new Insets(0, 0, 0, 0), -1, -1));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        Pan.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Files");
        Pan.add(label1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        Pan.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Clients");
        Pan.add(label2, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        Pan.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        Pan.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane1.setViewportView(FileListGui);
        final JScrollPane scrollPane2 = new JScrollPane();
        Pan.add(scrollPane2, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane2.setViewportView(ClientListGui);
        final JLabel label3 = new JLabel();
        label3.setText("Servers");
        Pan.add(label3, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane3 = new JScrollPane();
        Pan.add(scrollPane3, new com.intellij.uiDesigner.core.GridConstraints(8, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane3.setViewportView(ServerListGui);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return Pan;
    }
}