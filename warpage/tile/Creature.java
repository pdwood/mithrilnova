package tile;

import gui.TileWorld;

import java.awt.image.BufferedImage;

public abstract class Creature {
	private int x,y;
	private int health;//the creature should be removed from the TileWorld if this reaches 0
	private String name;
	private BufferedImage img;
	private TileWorld tw;
	private boolean right;
	private final BufferedImage flip; //stores mirrored image until a faster algorithm can be found
	public Creature(String filePath, int x, int y, int h, TileWorld tw){
		name=filePath;
		this.x=x;
		this.y=y;
		this.health=h;
		this.tw = tw;
		img=misc.Util.loadImg(filePath);
		int width=img.getWidth();
		flip=new BufferedImage(width, img.getHeight(), img.getType());
		for(int i = 1; i < width; i++) {
			for(int j = 0; j<img.getHeight(); j++) {
				flip.setRGB(width-i, j, img.getRGB(i, j));
			}
		}
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getHealth(){
		return health;
	}
	/**
	 * @param direction 0=up, 1=right, 2=down, 3=left
	 * @return true if there is nothing blocking it, false if it is blocked by a solid tile or screen edge
	 */
	public boolean canMove(int direction){
		switch(direction%4){
		case 0:	return!(tw.isSolid(x/TileWorld.TILE_SIZE, (y-1)/TileWorld.TILE_SIZE)||(x%12!=0&&tw.isSolid(x/TileWorld.TILE_SIZE+1, (y-1)/TileWorld.TILE_SIZE)));
		case 1:	return!(tw.isSolid((x+1)/TileWorld.TILE_SIZE+1, y/TileWorld.TILE_SIZE)||(y%12!=0&&tw.isSolid((x+1)/TileWorld.TILE_SIZE+1, y/TileWorld.TILE_SIZE+1)));
		case 2:	return!(tw.isSolid(x/TileWorld.TILE_SIZE, (y+1)/TileWorld.TILE_SIZE+1)||(x%12!=0&&tw.isSolid(x/TileWorld.TILE_SIZE+1, (y+1)/TileWorld.TILE_SIZE+1)));
		case 3:	return!(tw.isSolid((x-1)/TileWorld.TILE_SIZE, y/TileWorld.TILE_SIZE)||(y%12!=0&&tw.isSolid((x-1)/TileWorld.TILE_SIZE, y/TileWorld.TILE_SIZE+1)));
		default: return false;//impossible
		}
	}
	public void move(int horiz, int vert){
		if(vert!=0&&canMove(vert>0?2:0)){
			y+=vert;
		}
		if(horiz!=0&&canMove(horiz>0?1:3)){
			x+=horiz;
		}
	}
	public void changeHealth(int amount){
		health+=amount;
	}
	public abstract void move();//this is what the creature does each tick
	public void goTo(int x, int y){
		this.x=x;
		this.y=y;
	}
	//true if facing right, false if facing left
	public void setDir(boolean right){
		this.right=right;
	}
	public boolean isRight(){
		return right;
	}
	public BufferedImage getImg(){
		if(right)return img;
		return flip;
	}
	public TileWorld getTW(){
		return tw;
	}
	@Override
	public String toString(){
		return name;
	}
	public int getID(){
		/*
		 * Puff = 0
		 * Blader = 1
		 * Silkworm = 2
		 * Other hostile = 3
		 * Other = 4
		 */
		if(this instanceof Enemy){
			if(this.toString().equals("blader"))return 1;
			return 3;
		}
		else if(this instanceof PeacefulAnimal){
			switch(((PeacefulAnimal)(this)).typeIndex()){
			case PeacefulAnimal.PUFF: return 0;
			case PeacefulAnimal.SILKWORM: return 2;
			}
		}return 4;
	}
}
