package misc;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import tile.Tile;

public class GraphicTester2 {

	public static void main(String[] args) {
		final int SIZE=(int)(Math.sqrt(Tile.values().length));
		JPanel content = new JPanel();
		JFrame frame = new JFrame("GRAPHIC TEST");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		frame.setVisible(true);
		@SuppressWarnings("serial")
		Component c=new Component(){
			@Override
			public Dimension getPreferredSize(){
				return new Dimension((SIZE+1)*12,(SIZE+2)*12);
			}
			@Override
			public void paint(java.awt.Graphics g){
				Tile[] all=Tile.values();
				for(int i=0;i<all.length;i++){
					System.out.println(i);
					if(Util.loadImg(all[i].name())!=Util.err){
						g.drawImage(Util.loadImg(all[i].name()), (i%SIZE)*12, (i/SIZE)*12, null);
					}
				}
			}
		};
		content.add(c);
		frame.setContentPane(content);
		frame.pack();
		frame.repaint();
	}

}
