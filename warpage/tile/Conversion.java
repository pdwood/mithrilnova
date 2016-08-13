/**
 * List of crafting recipes
 */

package tile;

public enum Conversion {//MUST be sorted by crafting station, or else they won't work correctly.
	brick(new Tile[]{Tile.stone},new int[]{1},1,null),
	torch(new Tile[]{Tile.wood,Tile.sulfur},new int[]{1,1},5,null),
	stonePick(new Tile[]{Tile.wood, Tile.looseStone},new int[]{1,1},1,null),
	stoneAxe(new Tile[]{Tile.wood, Tile.looseStone},new int[]{1,1},1,null),
	woodSword(new Tile[]{Tile.wood},new int[]{2},1,null),
	anvil(new Tile[]{Tile.ironIngot},new int[]{4},1,null),	
	astralPortal(new Tile[]{Tile.glitterStone},new int[]{3},1,null),
	forge(new Tile[]{Tile.stone, Tile.torch}, new int[]{3,1},1,null),
	woodBoards(new Tile[]{Tile.wood},new int[]{2},3,null),
	door(new Tile[]{Tile.wood},new int[]{3},1,null),
	woodStairs(new Tile[]{Tile.woodBoards},new int[]{2},3,null),
	brickStairs(new Tile[]{Tile.brick},new int[]{2},3,null),
	sandStairs(new Tile[]{Tile.sandBrick},new int[]{2},3,null),
	marbleStairs(new Tile[]{Tile.marble},new int[]{2},3,null),
	blueStairs(new Tile[]{Tile.blueStone},new int[]{2},3,null),
	marblePillar(new Tile[]{Tile.marble},new int[]{1},1,null),
	transmuteTable(new Tile[]{Tile.marble, Tile.glitterStone},new int[]{2,2},1,null),
	healPotion(new Tile[]{Tile.bottle, Tile.pinkFlower},new int[]{1,3},1,null),
	goldPick(new Tile[]{Tile.wood, Tile.goldIngot},new int[]{1,1},1,null),
	thornyShield(new Tile[]{Tile.wood, Tile.thornBush},new int[]{2,2},1,null),
	loom(new Tile[]{Tile.wood, Tile.cocoon}, new int[]{2,2},1,null),
	bed(new Tile[]{Tile.wood, Tile.cloth},new int[]{4,3},1,null),
	pyriteBlock(new Tile[]{Tile.pyrite},new int[]{4},1,null),
	ironPick(new Tile[]{Tile.wood, Tile.ironIngot},new int[]{1,1},1,Tile.anvil),
	ironAxe(new Tile[]{Tile.wood, Tile.ironIngot},new int[]{1,1},1,Tile.anvil),
	ironShield(new Tile[]{Tile.ironIngot},new int[]{3},1,Tile.anvil),
	ironSword(new Tile[]{Tile.ironIngot},new int[]{2},1,Tile.anvil),
	chisel(new Tile[]{Tile.ironIngot},new int[]{1},1,Tile.anvil),
	mineHelmet(new Tile[]{Tile.starShard, Tile.ironIngot},new int[]{1,2},1,Tile.anvil),
	silexiumPick(new Tile[]{Tile.wood, Tile.silexiumIngot},new int[]{1,1},1,Tile.anvil),
	silexiumShield(new Tile[]{Tile.silexiumIngot},new int[]{3},1,Tile.anvil),
	silexiumSword(new Tile[]{Tile.silexiumIngot},new int[]{2},1,Tile.anvil),
	glass(new Tile[]{Tile.sand},new int[]{1},1,Tile.forge),
	bottle(new Tile[]{Tile.glass},new int[]{1},2,Tile.forge),
	jumpAmulet(new Tile[]{Tile.glass,Tile.gel},new int[]{1,1},1,Tile.forge),
	glassKnife(new Tile[]{Tile.glass},new int[]{2},1,Tile.forge),//Should this be 2 glass instead of 1 glass+1 wood?
	bronzeIngot(new Tile[]{Tile.bronzeOre},new int[]{1},1,Tile.forge),
	bronzePick(new Tile[]{Tile.wood, Tile.bronzeIngot},new int[]{1,1},1,Tile.forge),
	bronzeAxe(new Tile[]{Tile.wood, Tile.bronzeIngot},new int[]{1,1},1,Tile.forge),
	bronzeShield(new Tile[]{Tile.bronzeIngot},new int[]{3},1,Tile.forge),
	bronzeSword(new Tile[]{Tile.bronzeIngot},new int[]{2},1,Tile.forge),
	ironIngot(new Tile[]{Tile.ironOre},new int[]{1},1,Tile.forge),
	silexiumIngot(new Tile[]{Tile.silexiumOre},new int[]{1},1,Tile.forge),
	cloth(new Tile[]{Tile.cocoon},new int[]{1},1,Tile.loom),
	tapestry(new Tile[]{Tile.cloth},new int[]{3},1,Tile.loom),
	goldTapestry(new Tile[]{Tile.goldIngot,Tile.cloth},new int[]{1,3},1,Tile.loom),
	sandBrick(new Tile[]{Tile.cosmicSprout, Tile.sand}, new int[]{3,1},1,Tile.transmuteTable),
	marble(new Tile[]{Tile.cosmicSprout, Tile.stone}, new int[]{4,2},1,Tile.transmuteTable),
	goldIngot(new Tile[]{Tile.cosmicSprout, Tile.ironIngot}, new int[]{4,3},1,Tile.transmuteTable),
	sageOrb(new Tile[]{Tile.cosmicSprout}, new int[]{25},1,Tile.transmuteTable),
	beamRod(new Tile[]{Tile.goldIngot, Tile.glitterStone}, new int[]{2, 1}, 1, Tile.transmuteTable),
	;
	public Tile[] ingredients;
	public int[] quantities;
	public int output;
	public Tile method;
	private Conversion(Tile[] ingredients, int[] quantities, int output, Tile method){ //parameters are self-explanatory
		this.ingredients=ingredients;
		this.quantities=quantities;
		this.output=output;
		this.method=method;
	}
}
