/**
 * Represents an entity tied to a specific tile that grows over time and 
 */
package tile;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Plant{
	private int growthStage;
	private Tile type;
	private static HashMap<Tile,BufferedImage[]> imageMap;
	private int x,y;
	static{
		imageMap=new HashMap<Tile,BufferedImage[]>();
		int[] growthMaxes={3,4,4,4};
		Tile[] types={Tile.cosmicSprout,Tile.nut,Tile.cherry,Tile.pinecone};
		String[] names={"cosmicSprout","tree","sakura","pine"};
		for(int i=0;i<types.length;i++){
			BufferedImage[] imgs=new BufferedImage[growthMaxes[i]];
			for(int j=0;j<growthMaxes[i];j++){
				imgs[j]=misc.Util.loadCustomImg(names[i]+j);
			}
			imageMap.put(types[i],imgs);
		}
	}
	public Plant(Tile t, int x, int y){
		growthStage=0;
		type=t;
		this.y=y;
		this.x=x;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public void grow(){
		growthStage=Math.min(growthStage+1,getMaxSize());
	}
	private int getMaxSize(){
		switch(type){
		case cosmicSprout:return 2;
		case pinecone:
		case cherry:
		case nut:return 3;
		default: return 0;
		}
	}
	public int getSize(){
		return growthStage;
	}
	public Tile getTile(){
		return type;
	}
	public void setGrowth(int g){
		growthStage=Math.min(g,getMaxSize());
	}
	public BufferedImage getImg(){
		/*switch(type){
		case cosmicSprout:return imageMap.get(Tile.cosmicSprout)[growthStage/2];//divide by a constant term to calculate growth speed
		case cherry: return imageMap.get(Tile.cherry)[growthStage/3];
		case nut: return imageMap.get(Tile.nut)[growthStage/3];
		case pinecone: return imageMap.get(Tile.pinecone)[growthStage/3];
		default:*/

		return imageMap.get(type)[growthStage];//throw new IllegalArgumentException("Tile inaccurately marked as plant");
		//}
	}
}
