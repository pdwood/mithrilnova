/**
 * @author Peter Wood
 * Date: Feb 8, 2013
 * Assignment:
 */
package gui;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import static gui.TileWorld.TILE_SIZE;
public class Cursor extends MouseAdapter{
	private int screenX,screenY;
	private int x;
	private int y;
	private TileWorld tw;
	private boolean visible;
	public static BufferedImage img;
	public void putSelfInTW(TileWorld tw){
		this.tw = tw;
	}
	public Cursor(){
		img=misc.Util.loadImg("cursor");
	}
	public int getX(){
		return x;
	}
	public void updateXY(){
		x = (screenX + (tw.getXOffset()*TILE_SIZE/4)%TILE_SIZE) / TILE_SIZE;
		y = (screenY + (tw.getYOffset()*TILE_SIZE/4)%TILE_SIZE) / TILE_SIZE;
	}
	public int getY(){
		return y;
	}
	public boolean isVisible(){
		return visible;
	}
	@Override
	public void mouseMoved(MouseEvent e){
		screenX=e.getX();
		screenY=e.getY();
		updateXY();
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("click");
		Inventory.getConvPane().setCraftMethod(null);
		tw.respond(x,y,SwingUtilities.isRightMouseButton(e));
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		visible=true;
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		visible=false;
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e){
		Inventory.switchActiveTile(e.getWheelRotation());
	}
}
