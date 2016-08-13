package gui;
import java.awt.image.BufferedImage;

import tile.Weapon;

public class WeaponDisplay {
	private boolean visibility;
	private boolean magic; //true if beam rod
	private int countdown;
	private Weapon w;
	private int x,y;
	static final BufferedImage blast=misc.Util.loadImg("blast");
	public WeaponDisplay(){
		countdown=10;
	}
	public boolean isVisible(){
		return visibility;
	}
	public boolean isMagic(){
		return magic;
	}
	public void appear(int x, int y){
		magic=false;
		this.x=x;
		this.y=y;
		if(!visibility&&Inventory.getActiveTile() instanceof Weapon){
			w=(Weapon)(Inventory.getActiveTile());
			countdown=10;
			visibility=true;
		}		
	}
	public void hide(){
		countdown=0;
		visibility=false;
	}
	public void appearBeam(){
		countdown=200;
		magic=true;
		visibility=true;
	}
	public void goToXY(int x,int y){
		this.x=x;
		this.y=y;
	}
	public BufferedImage display(){
		countdown--;
		if(countdown==0){
			visibility=false;
		}
		if(magic)return blast;
		return w.getImg();
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public Weapon getWeapon(){
		return w;
	}
}
