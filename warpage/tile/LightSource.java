/**
 * This class represents light emitted by a Tile.
 */

package tile;
import gui.TileWorld;
import java.awt.Color;
import java.awt.RadialGradientPaint;

public class LightSource{
	private Color c;
	private TileWorld tw;
	private int x,y;
	public LightSource(Color c, int x, int y, TileWorld tw){
		this.c=c;
		this.x=x;
		this.y=y;
		this.tw=tw;
	}
	public Color getColor(){
		return c;
	}
	public RadialGradientPaint getGradient(){//horribly inefficient, but must calculate a new gradient each time because offset changes
		return new RadialGradientPaint((float)(getX()+0.5)*TileWorld.TILE_SIZE-tw.getXOffset()*TileWorld.TILE_SIZE/4, //there must be a simpler way to do this
				(float)(getY()+0.5)*TileWorld.TILE_SIZE-tw.getYOffset()*TileWorld.TILE_SIZE/4,
				2*TileWorld.TILE_SIZE,
				new float[]{0,1},
				new Color[]{c,new Color(getColor().getRed(), getColor().getGreen(), getColor().getBlue(), 0)});
	}
	public int getX(){
		return x;
	}
	public void moveX(int x){//for scrolling
		this.x+=x;
	}
	public int getY(){
		return y;
	}
	public void setColor(Color c){
		this.c=c;
	}
	//this was used in a removed feature
	public void nextRainbowColor(){
		if(c.getRed()==255&&c.getGreen()<255&&c.getBlue()==0){// + green
			setColor(new Color(255,c.getGreen()+15,0));
		}else if(c.getRed()>0&&c.getGreen()==255&&c.getBlue()==0){// - red
			setColor(new Color(c.getRed()-15,255,0));
		}else if(c.getRed()==0&&c.getGreen()==255&&c.getBlue()<255){// + blue
			setColor(new Color(0,255,c.getBlue()+15));
		}else  if(c.getRed()==0&&c.getGreen()>0&&c.getBlue()==255){// - green
			setColor(new Color(0,c.getGreen()-15,255));
		}else if(c.getRed()<255&&c.getGreen()==0&&c.getBlue()==255){// + red
			setColor(new Color(c.getRed()+15,0,255));
		}else  if(c.getRed()==255&&c.getGreen()==0&&c.getBlue()>0){// - blue
			setColor(new Color(255,0,c.getBlue()-15));
		}
	}
}
