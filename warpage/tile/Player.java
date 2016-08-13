/**
 * This class is used to represent the player character.
 */

package tile;

import gui.TileWorld;
import gui.Inventory;

public class Player extends Creature{
	public static final int MAX_HEALTH=20, STEP=TileWorld.TILE_SIZE/4;
	private int jumps;//tracks upward momentum
	private boolean noGravity;//if this is true the player will not fall
	public Player(TileWorld tw){
		super("player1",(TileWorld.SCREEN_WIDTH/2)*TileWorld.TILE_SIZE,0,MAX_HEALTH,tw);
	}
	@Override
	public void move(){//this method is called each tick.
		if(jumps>10){//move faster if jumping higher
			jumps--;
			move(0, -STEP*2);
		}
		else if(jumps>0){//move up if there is upward momentum
			jumps--;
			move(0, -STEP);
		}
		/*else if(Inventory.getActiveTile()==Equippable.cape){ the removed cape item slowed falling rate
			move(0, 3);
		}*/
		else if(noGravity){
		noGravity=false;
		}else move(0, STEP);//fall if there is no upward momentum

	}
	public void move(int x, int y){//because the superclass method calls canMove() and that messed up the stairs
		TileWorld tw=getTW();
		goTo(getX()+x,getY());//to bypass stairs.
		super.move(0, y);
		if(x<0&&tw.getXOffset()>0&&getX()-(tw.getXOffset()*STEP)<=5*TileWorld.TILE_SIZE){
			tw.changeXOffset(x/STEP);			
		}else if(x>0&&tw.getXOffset()/4+TileWorld.SCREEN_WIDTH<TileWorld.TOTAL_WIDTH&&getX()-(tw.getXOffset()*STEP)>=(TileWorld.SCREEN_WIDTH-5)*TileWorld.TILE_SIZE){
			tw.changeXOffset(x/STEP);
		}
		if(y<0&&tw.getYOffset()>0&&getY()-(tw.getYOffset()*STEP)<=5*TileWorld.TILE_SIZE){
			tw.changeYOffset(y/STEP);
		}else if(y>0&&tw.getYOffset()/4+TileWorld.SCREEN_HEIGHT<TileWorld.TOTAL_HEIGHT&&getY()-(tw.getYOffset()*STEP)>=(TileWorld.SCREEN_HEIGHT-5)*TileWorld.TILE_SIZE){
			tw.changeYOffset(y/STEP);
		}
		tw.getPointer().updateXY();
	}
	public void jump(){//call this method to initiate jumping sequence
		if(jumps>0){
			return;
		}
		if(noGravity||!canMove(2)){
			jumps=10;
			if(getY()+TileWorld.TILE_SIZE<TileWorld.TOTAL_HEIGHT*TileWorld.TILE_SIZE
					&&(getTW().tileAt(getX()/TileWorld.TILE_SIZE, (getY()+1)/TileWorld.TILE_SIZE+1)==Tile.gel
					||(getX()%12!=0&&getTW().tileAt(getX()/TileWorld.TILE_SIZE+1, (getY()+1)/TileWorld.TILE_SIZE+1)==Tile.gel))){
				jumps+=5;
			}
			if(Inventory.currentEquip()!=null&&Inventory.currentEquip().is("jumpAmulet")){
				jumps+=Inventory.currentEquip().getStrength();
			}
			noGravity=false;
		}
	}
	public int getJumps(){
		return jumps;
	}
	public void setNoGravity(){
		noGravity=true;
	}
}
