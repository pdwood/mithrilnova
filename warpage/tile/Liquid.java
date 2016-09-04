package tile;

import gui.TileWorld;

@Deprecated
public class Liquid{
	public static int MAX_DEPTH = TileWorld.TILE_SIZE;
	private int d;
	private int x,y;
	private TileWorld tw;
	public Liquid(int d, int x, int y, TileWorld tw){
		this.d=d;
		this.x=x;
		this.y=y;
		this.tw=tw;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getDepth(){
		return d;
	}
	public void tick(){
		if(!tw.isSolid(x,y+1)){//down
			Liquid that=tw.liquidAt(x, y+1);
			if(that==null){
				that=new Liquid(d,x,y+1,tw);
				tw.addLiquid(that, x, y+1);
				d=0;
				return;
			}else if (that.d<=MAX_DEPTH){
				that.d+=this.d;
				if(that.d>MAX_DEPTH){
					this.d=that.d-MAX_DEPTH;
					that.d=MAX_DEPTH;
				}else{
					this.d=0;
					return;
				}
			}
		}
		if(!tw.isSolid(x-1,y+1)&&!tw.isSolid(x+1, y+1)){//diagonal both sides
			Liquid left=tw.liquidAt(x-1, y+1);
			Liquid right=tw.liquidAt(x+1, y+1);			
			if(left==null){
				left=new Liquid(0,x-1,y+1,tw);
				tw.addLiquid(left, x-1, y+1);
			}
			if(right==null){
				right=new Liquid(0,x+1,y+1,tw);
				tw.addLiquid(right, x+1, y+1);
			}
			left.d+=d/2;
			this.d-=d/2;
			if(left.d>MAX_DEPTH){
				this.d+=left.d-MAX_DEPTH;
				left.d=MAX_DEPTH;
			}
			right.d=d;
			if(right.d>MAX_DEPTH){
				this.d=right.d-MAX_DEPTH;
				right.d=MAX_DEPTH;
			}else{
				this.d=0;
				return;
			}
		}else if(!tw.isSolid(x-1,y+1)){//diagonal one side
			Liquid left=tw.liquidAt(x-1, y+1);
			if(left==null){
				left=new Liquid(0,x-1,y+1,tw);
				tw.addLiquid(left, x-1, y+1);
			}
			left.d+=this.d;
			if(left.d>MAX_DEPTH){
				this.d=left.d-MAX_DEPTH;
				left.d=MAX_DEPTH;
			}else{
				this.d=0;
				return;
			}
		}else if(!tw.isSolid(x+1,y+1)){
			Liquid right=tw.liquidAt(x+1, y+1);
			if(right==null){
				right=new Liquid(0,x+1,y+1,tw);
				tw.addLiquid(right, x+1, y+1);
			}
			right.d+=this.d;
			if(right.d>MAX_DEPTH){
				this.d=right.d-MAX_DEPTH;
				right.d=MAX_DEPTH;
			}else{
				this.d=0;
				return;
			}
		}
		if(!tw.isSolid(x+1, y)){//straight to one side
			if(!tw.isSolid(x-1,y)){//straight both sides
				Liquid left=tw.liquidAt(x-1, y);
				if(left==null){
					left=new Liquid(0,x-1,y,tw);
					tw.addLiquid(left, x-1, y);
				}
				left.d+=this.d/3;
				this.d-=this.d/3;
				if(left.d>MAX_DEPTH){
					this.d+=left.d-MAX_DEPTH;
					left.d=MAX_DEPTH;
				}
			}
			Liquid right=tw.liquidAt(x+1, y);
			if(right==null){
				right=new Liquid(0,x+1,y,tw);
				tw.addLiquid(right, x+1, y);
			}
			right.d+=this.d/2;
			this.d-=this.d/2;
			if(right.d>MAX_DEPTH){
				this.d=right.d-MAX_DEPTH;
				right.d=MAX_DEPTH;
			}
		}else if(!tw.isSolid(x-1,y)){
			Liquid left=tw.liquidAt(x-1, y);
			if(left==null){
				left=new Liquid(0,x-1,y,tw);
				tw.addLiquid(left, x-1, y);
			}
			left.d+=this.d/2;
			this.d-=this.d/2;
			if(left.d>MAX_DEPTH){
				this.d+=left.d-MAX_DEPTH;
				left.d=MAX_DEPTH;
			}
		}
		assert(d<=MAX_DEPTH);
	}
}
