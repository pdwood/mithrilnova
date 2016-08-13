/**
 * This represents an item that can be worn by the player.
 */

package tile;

import java.awt.image.BufferedImage;

public class Equippable implements Item{
	private int strength;//the meaning of this varies depending on the item. It can be changed by various means such as a Sage Orb.
	private Type type;
	public enum Type implements Item{
		ironShield(2),
		jumpAmulet(2),
		silexiumShield(3),
		thornyShield(2),//this one in particular needs an easy way to strengthen
		steampunkHat(0),
		mineHelmet(0),
		bronzeShield(1),
		ninjaGear(0),
		;
		private int defStren;
		private final BufferedImage img;
		private Type(int ds){
			img=misc.Util.loadImg(name());
			defStren=ds;
		}
		@Override
		public BufferedImage getImg(){
			return img;
		}
	}
	public Equippable(String s){
		try{
			type=Type.valueOf(s);
		}catch(IllegalArgumentException e){}
		strength=type.defStren;
	}
	public Equippable(int i){
		type=Type.values()[i];
		strength=type.defStren;
	}
	@Override
	public BufferedImage getImg(){
		return type.img;
	}
	public int getStrength(){
		return strength;
	}
	public void setStrength(int s){
		strength=s;
	}
	@Override
	public String name() {
		return type.name();
	}
	public int ordinal(){
		return type.ordinal();
	}
	public boolean is(String type){
		return name().toLowerCase().indexOf(type.toLowerCase())>=0;//very inefficient
	}
	@Override
	public String toString(){
		String output="";
		if(strength!=type.defStren)output+="+"+(strength-type.defStren)+" ";
		output+=name();
		return output;
	}
}
