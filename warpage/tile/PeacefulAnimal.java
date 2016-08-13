/**
 * This class represents non-hostile mobs. Just for decoration, so far.
 */
package tile;

import gui.TileWorld;
//import gui.WeaponDisplay;

public class PeacefulAnimal extends Creature {
	public static final int PUFF=0,SILKWORM=1;
	private int type;
	private int yVel;
	//private WeaponDisplay wd;
	public PeacefulAnimal(int type, int x, int y, int h, TileWorld tw) {
		super(indexToName(type),x,y,h,tw);
		this.type=type;
		yVel=0;
	}
	public int typeIndex(){
		return type;
	}
	private static String indexToName(int index){
		switch(index){
		case PUFF:return"puff";
		case SILKWORM:return"silkworm";
		default:return "";
		}
	}
	@Override
	public void move(){
		if(Math.random()>.95)setDir(!isRight());
		if(yVel>0&&canMove(0)){
			move(0,-3);
			yVel--;
		}else if(canMove(2)){
			move(0,3);
		}
		if(canMove(isRight()?3:1))move(isRight()?-2:2,0);
		else if(!canMove(2)&&Math.random()>.8) yVel=10;
		if(getX()<0)goTo(0,getY());
		else if(getX()>TileWorld.TILE_SIZE*TileWorld.TOTAL_WIDTH)goTo(TileWorld.TILE_SIZE*TileWorld.TOTAL_WIDTH,getY());
		if(getY()<0)goTo(getX(),0);
		else if(getY()>TileWorld.TILE_SIZE*TileWorld.TOTAL_HEIGHT)goTo(getX(),TileWorld.TILE_SIZE*TileWorld.TOTAL_HEIGHT);
		//if(wd.isVisible()&&Math.abs(wd.getX()-getX())<=TileWorld.TILE_SIZE&&Math.abs(wd.getY()-getY())<=TileWorld.TILE_SIZE){
		//	if(wd.isMagic()){
		//		changeHealth(-2);
		//	}else{
		//		Weapon w=getTW().getWeaponDisplay().getWeapon();
		//		changeHealth(-w.getDamage());
		//		w.changeDur(-1);
		//		if(w.getDur()<=0)gui.Inventory.breakWeapon(w);
		//		//if(wd.getX()>getX())xVel-=10;
		//		//else xVel+=10;
		//	}
		//}//if this code is not commented out, the animal can be attacked
	}
}

