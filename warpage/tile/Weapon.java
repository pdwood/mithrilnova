/**
 * An enum representing a weapon with a limited durability
 */

package tile;

import java.awt.image.BufferedImage;

public class Weapon implements Item {
	private Type type;
	private int dur;
	public enum Type implements Item{//This is only for melee weapons. The beam rod doesn't count.
		woodSword(1,40),
		ironSword(2,90),
		glassKnife(3,40),
		silexiumSword(5,120),
		bronzeSword(1,70),
		;
		private final int dmg, maxDur;
		private final BufferedImage img;
		private Type(int dmg, int dur){
			img=misc.Util.loadImg(name());
			this.dmg=dmg;
			maxDur=dur;
		}
		@Override
		public BufferedImage getImg(){
			return img;
		}
	}
	public Weapon(String s){
		try{
			type=Type.valueOf(s);
		}catch(IllegalArgumentException e){}
		dur=type.maxDur;
	}
	public Weapon(int i){
		type=Type.values()[i];
		dur=type.maxDur;
	}
	@Override
	public BufferedImage getImg(){
		return type.img;
	}
	public int getDamage(){
		return type.dmg;
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
	@Override
	public String name() {
		return type.name();
	}
	public int ordinal(){
		return type.ordinal();
	}
	public boolean is(String type){
		return type.equals(name());
	}
	@Override
	public String toString(){//formatted for use in inventory
		return type+" "+dur+"/"+type.maxDur;
	}
}
