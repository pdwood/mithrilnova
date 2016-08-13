package misc;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import tile.Tile;

public class GraphicTester {

	public static void main(String[] args) {
		JPanel content = new JPanel();
		JFrame frame = new JFrame("GRAPHIC TEST");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		frame.setVisible(true);
		@SuppressWarnings("serial")
		Component c=new Component(){
			@Override
			public Dimension getPreferredSize(){
				return new Dimension(Tile.values().length*12,24);
			}
			@Override
			public void paint(java.awt.Graphics g){
				Tile[] all=Tile.values();
				for(int i=0;i<all.length;i++){
					g.drawImage(Util.loadImg(all[i].name()), i*12, 0, null);
					g.drawImage(Util.loadCustomImg(all[i].name()), i*12, 12, null);
				}
			}
		};
		content.add(c);
		frame.setContentPane(content);
		frame.pack();
		frame.repaint();
	}

}
