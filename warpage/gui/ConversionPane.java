/**
 * The right sidebar, where items are turned into other items
 */

package gui;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.event.MouseInputListener;

import tile.Conversion;
import tile.Item;
import tile.Tile;
import tile.Tool;
import tile.Equippable;
import tile.Weapon;

public class ConversionPane extends Component implements MouseInputListener{
	private static final long serialVersionUID = 1L;
	private static final int NUM_RECIPES=Conversion.values().length;
	private static BufferedImage arrowImg;
	private static Item[] outputs;
	private Tile method; //keeps track of crafting station
	//private Inventory inv;
	public ConversionPane(){
		//inv = Inventory.getDefaultInventory();
		outputs=new Item[NUM_RECIPES];
		arrowImg=misc.Util.loadImg("arrow");
		method=null;
	}
	public void initialize(){
		addMouseListener(this);
	}
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(Inventory.WIDTH,TileWorld.SCREEN_HEIGHT*TileWorld.TILE_SIZE);
	}
	public void setCraftMethod(Tile t){
		method=t;
		paint();
	}
	public void paint(){
		Graphics g = getGraphics();
		g.setColor(new Color(225,225,225));
		g.fillRect(0,0,Inventory.WIDTH,TileWorld.SCREEN_HEIGHT*TileWorld.TILE_SIZE);
		g.setColor(Color.BLACK);
		g.drawString("CRAFTING",Inventory.SIDE_PAD,Inventory.SIDE_PAD);
		int drawnHeight = Inventory.TOP_PAD;
		int i=0;
		while(Conversion.values()[i].method!=method)i++;
		Conversion c=Conversion.values()[i];
		while(c.method==method){
			boolean canMake=true;
			for(int j=0;j<c.ingredients.length;j++){
				if(Inventory.get(c.ingredients[j])<c.quantities[j])canMake=false;
			}
			if(canMake){//populate list with available items
				try{//keep trying until the type of the item is determined
					outputs[(drawnHeight-Inventory.TOP_PAD)/Inventory.SPACE]=Tile.valueOf(c.name());
				}catch(IllegalArgumentException e1){
					try{
						outputs[(drawnHeight-Inventory.TOP_PAD)/Inventory.SPACE]=Tool.Type.valueOf(c.name());//the enums Tool.Type, Weapon.Type, Equippable.Type, etc implement
					}catch(IllegalArgumentException e2){				 //Item as kind of a kludgy way of letting them be in the outputs array
						try{
							outputs[(drawnHeight-Inventory.TOP_PAD)/Inventory.SPACE]=Equippable.Type.valueOf(c.name());
						}catch(IllegalArgumentException e3){
							outputs[(drawnHeight-Inventory.TOP_PAD)/Inventory.SPACE]=Weapon.Type.valueOf(c.name());
						}
					}
				}
				g.drawImage(arrowImg,Inventory.TOP_PAD+1,drawnHeight,null);
				g.drawString(String.valueOf(c.output),80,drawnHeight+Inventory.SPACE/2);
				g.drawImage(outputs[(drawnHeight-Inventory.TOP_PAD)/Inventory.SPACE].getImg(),90,drawnHeight,null);
				int xpos=35;
				for(int j=0;j<c.ingredients.length;j++){//draw all the ingredients for the recipe
					g.drawImage(c.ingredients[j].getImg(),xpos,drawnHeight,null);
					if(c.quantities[j]>1){
						xpos-=10;
						g.drawString(String.valueOf(c.quantities[j]),xpos,drawnHeight+10);
					}
					xpos-=15;
				}
				drawnHeight+=Inventory.SPACE;
			}
			i++;
			if(i<Conversion.values().length){
				c=Conversion.values()[i];
			}else break;
		}
		for(i=(drawnHeight-Inventory.TOP_PAD)/Inventory.SPACE;i<NUM_RECIPES;i++){//make sure that all the recipes we won't use are blank
			outputs[i]=null;
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		//System.out.println(e.getY());
		Item t=outputs[(e.getY()-Inventory.TOP_PAD)/Inventory.SPACE]; //map mouse location to conversion, then redistribute items accordingly
		if(t instanceof Tile)Inventory.change((Tile)t, Conversion.valueOf(t.name()).output);
		else if(t instanceof Tool.Type)Inventory.getTool(new Tool((Tool.Type)t));
		else if(t instanceof Equippable.Type)Inventory.getEquip(new Equippable(t.toString()));
		else if(t instanceof Weapon.Type)Inventory.getWeapon(new Weapon(t.toString()));
		if(t!=null){
			Conversion c=Conversion.valueOf(t.name());
			for(int i=0;i<c.ingredients.length;i++){
				Inventory.change(c.ingredients[i],-c.quantities[i]);
			}
			Inventory.getDefaultInventory().paint();
		}
	}
	//Unnecessary methods
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
	@Override
	public void mouseDragged(MouseEvent arg0) {
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
	}
}
