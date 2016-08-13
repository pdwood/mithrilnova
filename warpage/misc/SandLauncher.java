/**
 * Driver class for entire game.
 */

package misc;

import gui.Inventory;
import gui.SaveDialog;
import gui.TileWorld;

import java.awt.FlowLayout;
//import java.awt.event.ActionEvent;

//import javax.swing.JMenu;
//import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JFrame;
//import javax.swing.JMenuBar;

public class SandLauncher{
	public static void main(String[] args){
		JPanel content = new JPanel();
		final JFrame frame = new JFrame("Warpage - Version Canonical Beta");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		frame.setVisible(true);
		/*JMenuBar menubar=new JMenuBar(); //this did not work.
		JMenu menu=new JMenu("Menu");
		JMenuItem mi1=new JMenuItem("Save game");
		mi1.setActionCommand("s");
		mi1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		mi1.addActionListener(tw);
		JMenuItem mi2=new JMenuItem("Pause");
		mi2.setActionCommand("p");
		mi2.addActionListener(tw);
		menu.add(mi1);
		menu.add(mi2);
		menubar.add(menu);
		frame.setJMenuBar(menubar);*/
		TileWorld tw = new TileWorld();
		content.add(Inventory.getDefaultInventory());
		content.add(tw);
		content.add(Inventory.getConvPane());
		frame.setContentPane(content);
		frame.pack();
		frame.repaint();
		Inventory.getConvPane().initialize();
		if(TileWorld.filesIncorrect){
			javax.swing.JOptionPane.showMessageDialog(frame, "Error reading necessary image files.\nPlease ensure that the folder containing this game\nalso includes a \"data\" subfolder with the necessary files.");
		}else{
			SaveDialog prompt=new SaveDialog(tw);
			prompt.show();
		}
	}
}