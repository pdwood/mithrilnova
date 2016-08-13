/**
 * The basic unit of the world. Everything is made from these.
 */

package tile;

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import misc.Util;
public enum Tile implements Item{
	dirt(true,true,0),
	stone(false,true,Tile.STONE_0),
	brick(false,true,0),
	grass(true,false,0),
	torch(false,false,Tile.LIGHT),
	woodBoards(false,true,0),
	//redCrystal(true,false,0),//not available - currently used as a placeholder in cursed vaults
	gel(false,true,0),
	greenOre(false,true,Tile.STONE_0),//not available - yields gel when mined
	glitterStone(false,true,Tile.STONE_2),
	starShard(false,false,Tile.LIGHT),
	astralPortal(false,true,Tile.PORTAL),
	blueStone(false,true,Tile.STONE_2),
	cosmicSprout(false,false,Tile.PLANT),
	nut(false,false,Tile.TREE_0),
	wood(false,true,0),
	bedrock(false,true,Tile.STONE_3),//not available - yields stone when mined
	blueSand(true,true,0),
	looseStone(true,true,0),
	ironOre(false,true,Tile.STONE_1),
	pyriteOre(false,true,Tile.STONE_3),
	anvil(true,true,0),
	forge(false,true,Tile.LIGHT),
	sand(true,true,0),
	glass(false,true,0),
	bottle(true,false,0),
	door(false,true,0),
	openDoor(false,false,0),
	cherry(false,false,Tile.TREE_0),
	pinkFlower(true,false,0),
	silexiumOre(false,true,Tile.STONE_2),
	carvedStone(false,true,Tile.STONE_0),
	carvedBlueStone(false,true,Tile.STONE_2),
	earthPortal(false,true,Tile.PORTAL),
	sandBrick(false,true,Tile.STONE_0),
	carvedSandBrick(false,true,Tile.STONE_0),
	sylvanPortal(false,true,Tile.PORTAL),
	moss(false,true,0),
	marble(false,true,Tile.STONE_1),
	carvedMarble(false,true,Tile.STONE_1),
	marblePillar(false,true,Tile.STONE_1),
	transmuteTable(false,true,0),
	ironIngot(true,false,0),
	glowMushroom(false,false,Tile.LIGHT),
	cursedStone(false,true,0),
	mossyStone(false,true,Tile.STONE_0),
	goldIngot(true,false,0),
	spiralGrass(true,false,0),
	thornBush(true,false,0),
	pinecone(false,false,Tile.TREE_0),
	cocoon(false,false,0),
	loom(true,false,0),
	cloth(true,false,0),
	bed(true,true,0),
	tapestry(false,false,0),
	goldTapestry(false,false,0),
	sulfur(false,true,Tile.STONE_0),
	pyrite(true,false,0),
	pyriteBlock(false,true,Tile.STONE_0),
	carvedPyrite(false,true,Tile.STONE_0),
	silexiumIngot(true,false,0),
	greenTapestry(false,false,0),
	pinkTapestry(false,false,0),
	damagedPortal(false,true,0),//TODO name this
	fragmentedPortal(false,true,Tile.PORTAL),
	shardStone(false, true, Tile.STONE_2),
	woodStairs(false,true,Tile.STAIRS),
	woodStairs_F(false,true,Tile.STAIRS_F),
	brickStairs(false,true,Tile.STAIRS),
	brickStairs_F(false,true,Tile.STAIRS_F),
	sandStairs(false,true,Tile.STAIRS),
	sandStairs_F(false,true,Tile.STAIRS_F),
	marbleStairs(false,true,Tile.STAIRS),
	marbleStairs_F(false,true,Tile.STAIRS_F),
	blueStairs(false,true,Tile.STAIRS),
	blueStairs_F(false,true,Tile.STAIRS_F),
	bronzeOre(false,true,Tile.STONE_0),
	bronzeIngot(true,false,0),
	crystalFlower(true,false,0),
	//enigmaOre(false,true,Tile.STONE_1),
	wire(false,false,0/*Tile.TECH*/),//wire and door would be mechanisms
	//lantern(false,false,Tile.TECH) //planned torch+glass+wire, only glows when active
	;
	public static final int NO_SPECIAL=0, LIGHT=1, PLANT=2, PORTAL=3, STAIRS=4, STAIRS_F=5, TREE_0=6, STONE_0=10, STONE_1=11, STONE_2=12, STONE_3=13;//higher STONE constants mean harder stones and thus need better pickaxes
	private final BufferedImage[] imgs;
	public final boolean gravity;
	public final boolean isSolid;
	public static final BufferedImage wireH,wireV;
	private static RescaleOp[] darkeners=new RescaleOp[6];
	public final int type;
	static{
		wireH=Util.loadImg("wireH");
		wireV=Util.loadImg("wireV");
		for(int i=0;i<darkeners.length;i++)darkeners[i]=new RescaleOp(new float[]{i/5f,i/5f,i/5f,1},new float[]{0,0,0,0},null);
		for(Tile t:values())t.fillImgs();
	}
	private Tile(boolean g, boolean s, int t){
		gravity=g;
		isSolid=s;
		type=t;
		imgs=new BufferedImage[6];
		imgs[5]=Util.loadCustomImg(name());//uses custom texture packs if installed
	}
	private void fillImgs(){
		for(int i=0;i<imgs.length;i++){
			imgs[i]=Tile.darkeners[i].filter(imgs[5], null);
		}
	}
	@Override
	public BufferedImage getImg(){
		return imgs[5];
	}
	public BufferedImage getDarkenedImg(int light){
		return imgs[light];
	}
}
