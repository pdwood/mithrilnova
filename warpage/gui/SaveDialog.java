/**
 * Very simple dialog box that appears at startup.
 */
package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SaveDialog implements ActionListener{
	private JTextField inputPanel;
	private JFrame frame;
	private JPanel panel;
	private TileWorld tw;
	public SaveDialog(TileWorld tw){
		this.tw=tw;
		frame=new JFrame("Select World");
		panel=new JPanel();
		panel.setLayout(new GridLayout(3,1,0,10));
		panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(new JLabel("Please enter name of world to load."));
		panel.add(new JLabel("(If world does not exist yet, a new one will be generated with the given name.)"));
		inputPanel=new JTextField(25);
		inputPanel.addActionListener(this);
		panel.add(inputPanel);
		frame.setContentPane(panel);
	}
	public void show(){
		frame.pack();
		frame.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		tw.initialize(inputPanel.getText());
		Inventory.getDefaultInventory().paint();
		frame.dispose();
	}
}
