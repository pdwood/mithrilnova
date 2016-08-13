/**
 * A class that statically keeps track of collected items.
 */
package gui;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import tile.Item;
import tile.Tile;
import tile.Tool;
import tile.Equippable;
import tile.Weapon;
public class Inventory extends Component{
	private static final long serialVersionUID = 1L;
	public static final int WIDTH=120, TOP_PAD=54, SIDE_PAD=14, SPACE=20;
	private static int[] inv;
	private static ArrayList<Tool> tools;
	private static ArrayList<Equippable> equips;
	private static ArrayList<Weapon> weapons;
	//private static final Item[] allItems;
	private static Inventory defInv;
	private static ConversionPane convp;
	private static Item activeTile;
	private static Equippable equip;

	static{
		tools=new ArrayList<Tool>();
		equips = new ArrayList<Equippable>();
		weapons = new ArrayList<Weapon>();
		activeTile=Tile.dirt;
		defInv=new Inventory();
		convp=new ConversionPane();
		inv = new int[Tile.values().length];
	}
	public static void change(Tile t, int amount){
		inv[t.ordinal()]+=amount;
		//defInv.paint();
		convp.paint();
	}
	public static void getTool(Tool t){
		tools.add(t);
		defInv.paint();
	}
	public static void getEquip(Equippable e){
		equips.add(e);
		defInv.paint();
	}
	public static void getWeapon(Weapon w){
		weapons.add(w);
		defInv.paint();
	}
	public static int get(Item t){
		if(t instanceof Tile)return inv[((Tile)t).ordinal()];
		return(tools.indexOf(t)+inv.length);
	}
	public static void equip(){
		if(activeTile instanceof Equippable){
			equip=(Equippable)activeTile;
			defInv.paint();
		}
	}
	public static void clear(){
		for(int i=0;i<inv.length;i++){
			inv[i]=0;
		}
		tools.clear();
		equips.clear();
		weapons.clear();
	}
	public static Equippable currentEquip(){
		return equip;
	}
	public static void breakTool(Tool t){
		if(t.getType().use==Tile.bottle)change(Tile.bottle,1);
		tools.remove(t);
		switchActiveTile(1);
		defInv.paint();
	}
	public static void breakWeapon(Weapon w){
		weapons.remove(w);
		switchActiveTile(1);
		defInv.paint();
	}
	public static ConversionPane getConvPane(){
		return convp;
	}
	public static void switchActiveTile(int offset){
		int pos; //figure out the index of the currently selected item
		if(activeTile instanceof Tile)pos = ((Tile)activeTile).ordinal();
		else if(activeTile instanceof Tool) pos=tools.indexOf(activeTile)+inv.length;
		else if(activeTile instanceof Equippable) pos=equips.indexOf(activeTile)+inv.length+tools.size();
		else pos=weapons.indexOf(activeTile)+inv.length+tools.size()+equips.size();
		int prev=pos;
		do{
			pos+=offset;
			if(pos>inv.length+tools.size()+equips.size()+weapons.size()){ //loop back around
				pos=0;
			}
			if(pos==prev)return; //to prevent hangups with empty inventory - if we're back where we started, something's wrong
			if(pos<0)pos=inv.length+tools.size()+equips.size()+weapons.size()-1;
		}while(pos>=inv.length+tools.size()+equips.size()+weapons.size()||pos<0||(pos<inv.length&&inv[pos]==0));
		if(pos<inv.length)activeTile=Tile.values()[pos]; //reassign index to item
		else if(pos<inv.length+tools.size()) activeTile=tools.get(pos-inv.length);
		else if(pos<inv.length+tools.size()+equips.size())activeTile=equips.get(pos-inv.length-tools.size());
		else activeTile=weapons.get(pos-inv.length-tools.size()-equips.size());
		defInv.paint();
		//System.out.println(pos);
	}
	public static Item getActiveTile(){
		return activeTile;
	}
	public static Inventory getDefaultInventory(){
		return defInv;
	}
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH,TileWorld.SCREEN_HEIGHT*TileWorld.TILE_SIZE);
	}
	public static String outputAll(){
		StringBuilder output=new StringBuilder();
		for(int i:inv){
			output.append(i);
			output.append(",");
		}
		output.append("\n");
		for(Tool t:tools){
			output.append(t.getType().ordinal());
			output.append(" ");
			output.append(t.getDur());
			output.append(",");
		}
		output.append("\n");
		for(Equippable e:equips){
			output.append(e.ordinal());
			output.append(" ");
			output.append(e.getStrength());
			output.append(",");
		}
		output.append("\n");
		for(Weapon w:weapons){
			output.append(w.ordinal());
			output.append(" ");
			output.append(w.getDur());
			output.append(",");
		}
		return output.toString();
	}
	public void paint(){
		Graphics g = getGraphics();
		g.setColor(new Color(225,225,225));
		g.fillRect(0,0,WIDTH,TileWorld.SCREEN_HEIGHT*TileWorld.TILE_SIZE);
		g.setColor(Color.BLACK);
		g.drawString("INVENTORY",SIDE_PAD,SIDE_PAD);
		int i = 0;
		int drawnHeight = TOP_PAD;
		while(i<inv.length){
			if(inv[i]>0){
				if(Tile.values()[i]==activeTile)g.drawRect(1, drawnHeight-1, WIDTH-2, SIDE_PAD);
				g.drawImage(Tile.values()[i].getImg(),5,drawnHeight,null);
				g.drawString(Tile.values()[i]+": "+inv[i], SPACE, drawnHeight+(SPACE/2));
				drawnHeight+=SPACE;
			}
			i++;
		}
		for(Tool t:tools){
			if(t==activeTile)g.drawRect(1, drawnHeight-1, WIDTH-2, SIDE_PAD);
			g.drawImage(t.getImg(),5,drawnHeight,null);
			g.drawString(t.toString(), SPACE, drawnHeight+(SPACE/2));
			drawnHeight+=SPACE;
		}
		for(Equippable e:equips){
			if(e==activeTile)g.drawRect(1, drawnHeight-1, WIDTH-2, SIDE_PAD);
			g.drawImage(e.getImg(),5,drawnHeight,null);
			g.drawString(e.toString(), SPACE, drawnHeight+(SPACE/2));
			drawnHeight+=SPACE;
		}
		for(Weapon w:weapons){
			if(w==activeTile)g.drawRect(1, drawnHeight-1, WIDTH-2, SIDE_PAD);
			g.drawImage(w.getImg(),5,drawnHeight,null);
			g.drawString(w.toString(), SPACE, drawnHeight+(SPACE/2));
			drawnHeight+=SPACE;
		}
		g.drawString("Equipped Item:",SIDE_PAD,690);
		g.drawRect(TOP_PAD-1,750-TOP_PAD,SIDE_PAD-1,SIDE_PAD-1);
		if(equip!=null){
			g.drawImage(equip.getImg(),TOP_PAD,750-TOP_PAD+1,null);
		}
	}
}
