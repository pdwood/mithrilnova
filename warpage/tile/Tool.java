/**
 * This class represents anything that has a limited number of uses, cannot be placed in the world, and is not explicitly a weapon.
 */
package tile;

import java.awt.image.BufferedImage;

public class Tool implements Item {
	public enum Type implements Item{		
		stonePick(75,Tile.stone),
		bronzePick(125,Tile.stone),
		ironPick(200,Tile.stone),
		silexiumPick(400,Tile.stone),
		//starHammer(300,Tile.stone), //currently not used, but might come back.
		chisel(100, null),
		sageOrb(3,null),
		healPotion(3,Tile.bottle),
		goldPick(100,Tile.cursedStone),
		stoneGun(15,null),
		beamRod(50,null), //is technically a weapon but calling it one required too many special cases
		stoneAxe(50,Tile.nut),
		bronzeAxe(100,Tile.nut),
		ironAxe(150,Tile.nut),
		;
		private final BufferedImage img;
		public final Tile use;
		private final int maxDurability;
		private Type(int dur, Tile type){	
			maxDurability=dur;
			this.use=type;
			img=misc.Util.loadImg(name());
		}
		@Override
		public BufferedImage getImg() {
			return img;
		}
	}
	private int dur;
	private Type t;
	public Tool(Type t){
		this.t=t;
		dur=t.maxDurability;
	}
	@Override
	public BufferedImage getImg(){
		return t.getImg();
	}
	@Override
	public String toString(){
		return t+" "+dur+"/"+t.maxDurability;
	}
	public void changeDur(int amount){
		dur+=amount;
	}
	public void setDur(int d){
		dur=d;
	}
	public int getDur(){
		return dur;
	}
//	public int getMaxDur(){//I forget what I was thinking when I added this method - I think it was once used at some point
//		System.out.println("getMaxDur() was called. Interesting. That method actually has a point.");
//		return t.maxDurability;
//	}
	public Type getType(){
		return t;
	}
	@Override
	public String name(){
		return t.name();
	}
	public boolean is(String type){//inefficient?...
		return type.equals(name());
	}
	
}
