/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PropertiesPanel.java 
 *
 * Created on 14/08/2009, 13:37:17
 */
package edu.memphis.ccrg.lida.framework.gui.panels;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

/**
 * A {@link GuiPanel} displaying the configuration files being used.
 * 
 * @author Javier Snaider
 */
public class ConfigurationFilesPanel extends GuiPanelImpl {

	private static final Logger logger = Logger
			.getLogger(ConfigurationFilesPanel.class.getCanonicalName());
	private Properties properties;
	private String propertiesFile = "configs/lidaConfig.properties";

	/** Creates new form PropertiesPanel */
	public ConfigurationFilesPanel() {
		initComponents();
	}

	@Override
	public void initPanel(String[] params) {
		properties = new Properties();
		if (params.length > 0) {
			propertiesFile = params[0];
		}

		try {
			properties.load(new BufferedReader(new FileReader(propertiesFile)));
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "Error reading properties  {0}", e
					.toString());
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error reading properties  {0}", e
					.toString());
		}
		fileNameTextField.setText(propertiesFile);

	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jToolBar1 = new javax.swing.JToolBar();
		fileNameTextField = new javax.swing.JTextField();
		threadPane = new javax.swing.JScrollPane();
		PropertiesTable = new javax.swing.JTable();

		jToolBar1.setRollover(true);

		fileNameTextField.setEditable(false);
		jToolBar1.add(fileNameTextField);

		PropertiesTable.setModel(new PropertiesTableModel());
		threadPane.setViewportView(PropertiesTable);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup().addComponent(jToolBar1,
						javax.swing.GroupLayout.DEFAULT_SIZE, 390,
						Short.MAX_VALUE).addContainerGap()).addComponent(
				threadPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400,
				Short.MAX_VALUE));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addComponent(
												jToolBar1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												25,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												threadPane,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												215, Short.MAX_VALUE)));
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JTable PropertiesTable;
	private javax.swing.JTextField fileNameTextField;
	private javax.swing.JToolBar jToolBar1;
	private javax.swing.JScrollPane threadPane;

	// End of variables declaration//GEN-END:variables

	private class PropertiesTableModel extends AbstractTableModel {

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public int getRowCount() {
			return properties.size();
		}

		@Override
		public String getColumnName(int column) {
			if (column == 0) {
				return "Key";
			} else {
				return "Value";
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return getKey(rowIndex);
			} else {
				return properties.get(getKey(rowIndex));
			} // if-else

		}

		private String getKey(int a_index) {
			String retval = "";
			Enumeration<Object> e = properties.keys();
			for (int i = 0; i < a_index + 1; i++) {
				retval = (String) e.nextElement();
			} // for

			return retval;
		}

		@Override
		public void setValueAt(Object value, int row, int column) {
			if (column == 1) {
				properties.setProperty(getKey(row), (String) value);
			}
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return (column == 1);
		}
	}// inner class

	@Override
	public void display(Object o) {
		if (o instanceof Properties) {
			properties = (Properties) o;
		}
	}
}
