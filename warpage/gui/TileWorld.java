/**
 * WARPAGE BETA
 * Began Feb. 6, 2013
 */
package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RadialGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import misc.Util;

import tile.*;
import static tile.Player.PIXELS_PER_STEP;

//TODO wiring?
//TODO limited durability for shields

//TODO BEFORE NEXT LAUNCH: Fix cursor alignment
public class TileWorld extends Component implements ActionListener{
	private static boolean DEBUG=false;
	private static final long serialVersionUID = 1L;
	public static final int SCREEN_WIDTH=60, TOTAL_WIDTH=600, SCREEN_HEIGHT=60, TOTAL_HEIGHT=240, TILE_SIZE=12, STEPS_PER_TILE=4;
	public static boolean filesIncorrect;//This is set to true if the graphics for the game cannot be found
	private int xOffset,yOffset;//The x-coordinate offset, expressed in units of STEP pixels.
	public static final int NUM_DIMS = 3;//How many dimensions the world contains
	private String name; //world filename
	private Tile[][][] tiles;
	//private Liquid[][][] liquids;
	private Player player;
	private Timer timer;
	private int time;
	//private int atmVar;
	private boolean sunSetting;
	private ArrayList<LightSource>[] lights;
	private ArrayList<Plant>[] plants;
	private ArrayList<Creature>[] creatures;
	private Cursor c;
	private int currDim;
	private Color[] sky={null,new Color(0x400080),new Color(0x002a0f),new Color(0xeab253)};
	private final Tile[] portals={Tile.earthPortal, Tile.astralPortal, Tile.sylvanPortal};//, Tile.fragmentedPortal};
	Image osc; //for double-buffering
	private Image worldImg;
	private SandKeys keyboard;
	private WeaponDisplay wd;
	Graphics2D osg;
	private boolean paused;
	static final Color WATER_COLOR=new Color(0,0,255,128);
	private static Image healthImg=Util.loadImg("health");
	public TileWorld(){
		tiles = new Tile[NUM_DIMS][TOTAL_WIDTH][TOTAL_HEIGHT];
		//liquids=new Liquid[NUM_DIMS][TOTAL_WIDTH][TOTAL_HEIGHT];
		xOffset=0;
		yOffset=0;
	}
	/**
	This class is used to keep track of keyboard input.
	 */
	public class SandKeys implements KeyListener{
		private final int[] KONCD={KeyEvent.VK_UP,KeyEvent.VK_UP,KeyEvent.VK_DOWN,KeyEvent.VK_DOWN,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT,KeyEvent.VK_B,KeyEvent.VK_A};
		private int koncd=0;
		private final HashSet<Integer> pressed = new HashSet<Integer>();// Set of currently pressed keys
		@Override
		public synchronized void keyPressed(KeyEvent e) {
			if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
				pause();
			}else if(e.getKeyCode()==KeyEvent.VK_F12){
				String path=System.getProperty("user.home")+File.separator+"WarpageSaves";
				File out=new File(path+File.separator+name+currDim+".png");
				BufferedImage img=new BufferedImage(TileWorld.TOTAL_WIDTH*TileWorld.TILE_SIZE, TileWorld.TOTAL_HEIGHT*TileWorld.TILE_SIZE, BufferedImage.TYPE_INT_RGB);
				Graphics g=img.getGraphics();
				for(int i=0;i<TileWorld.TOTAL_WIDTH;i++){
					for(int j=0;j<TileWorld.TOTAL_HEIGHT;j++){
						if(tileAt(i,j)!=null){
							g.drawImage(tileAt(i, j).getImg(), i*TileWorld.TILE_SIZE, j*TileWorld.TILE_SIZE, null);
						}
					}
				}
				try {
					ImageIO.write(img, "png", out);
					System.out.println("Successfully outputted image of entire map");
				} catch (IOException derp) {
					derp.printStackTrace();
				}
			}
			if(!paused){
				if(pressed.add(e.getKeyCode())){ //secret...
					if(KONCD[koncd]==e.getKeyCode()){
						koncd++;
						if(koncd==KONCD.length){
							koncd=0;
							Inventory.getEquip(new Equippable(4));
							Util.koncd();
						}
					}
					else koncd=0;
				}
				if(pressed.size()>=1)for(Integer c:pressed){
					switch(c.intValue()){
					case KeyEvent.VK_Q:{
						Inventory.switchActiveTile(-1);
						break;
					}
					case KeyEvent.VK_E:{
						Inventory.switchActiveTile(1);
						break;
					}
					case KeyEvent.VK_UP:
					case KeyEvent.VK_W:{
						player.jump();
						break;
					}
					case KeyEvent.VK_LEFT:
					case KeyEvent.VK_A:{
						player.setDir(false);
						boolean stairs=false;
						Tile t=tileAt((player.getX()-1)/TILE_SIZE, (player.getY()-1)/TILE_SIZE+1);
						if(t!=null&&t.type==Tile.STAIRS_F)stairs=true;
						if(player.canMove(3)||stairs){
							player.move(-3, 0);
							if(stairs)player.move(0,-3);
							if(player.getX()<0)player.move(-player.getX(),0);
						}//else if(Inventory.currentEquip()!=null&&Inventory.currentEquip().ordinal()==6)player.setNoGravity();//for the ninja suit
						break;
					}
					case KeyEvent.VK_RIGHT:
					case KeyEvent.VK_D:{
						player.setDir(true);
						boolean stairs=false;
						Tile t=tileAt(player.getX()/TILE_SIZE+1, (player.getY()-1)/TILE_SIZE+1);
						if(t!=null&&t.type==Tile.STAIRS)stairs=true;
						if(player.canMove(1)||stairs){
							player.move(3, 0);
							if(stairs)player.move(0,-3);
						}//else if(Inventory.currentEquip()!=null&&Inventory.currentEquip().ordinal()==6)player.setNoGravity();//for the ninja suit
						break;
					}
					//case KeyEvent.VK_F1:if(DEBUG){
					//Inventory.change(Tile.fragmentedPortal,1);
					//	tiles[3]=new Tile[TOTAL_WIDTH][TOTAL_HEIGHT];
					//	terrGenShard();
					//}break;
					case KeyEvent.VK_DOWN:
					case KeyEvent.VK_S:
						if(player.getX()%TILE_SIZE==0){
							Tile t=tileAt(player.getX()/TILE_SIZE,player.getY()/TILE_SIZE+1);
							if(t!=null){
								if(t.type==Tile.PORTAL)for(int i=0;i<portals.length;i++){
									if(portals[i]==tileAt(player.getX()/TILE_SIZE,player.getY()/TILE_SIZE+1)){
										currDim=i;
										Util.shiftMusic(currDim);
										break;
									}
								}else if(t==Tile.bed&&player.getHealth()<Player.MAX_HEALTH)player.changeHealth(1);
							}
						}
						if(Inventory.getActiveTile()instanceof Tool){
							Tool t=(Tool)Inventory.getActiveTile();
							Equippable eq=Inventory.currentEquip();
							if(t.is("sageOrb")&&eq!=null){
								eq.setStrength(eq.getStrength()+1);
								t.changeDur(-1);
							}else if(t.is("healPotion")){
								player.changeHealth(3);
								t.changeDur(-1);
							}
							if(t.getDur()<=0){
								Inventory.breakTool(t);
							}

							Inventory.getDefaultInventory().paint();
						}else{
							Inventory.equip();
						}
						break;
						/*case 32:{//spacebar
						for(int i=0;i<TOTAL_WIDTH;i++){
						liquids[currDim][i][0]=new Liquid(2, i, 0, TileWorld.this);
						}
						break;
					}*/
					}
				}
			}
		}
		@Override
		public synchronized void keyReleased(KeyEvent e) {
			pressed.remove(e.getKeyCode());
		}
		@Override
		public void keyTyped(KeyEvent e){
			if(e.getKeyChar()==10) writeToFile();//enter key
		}
	}
	@SuppressWarnings("unchecked")
	public void initialize(String worldName){
		name=worldName;
		osc = createImage(SCREEN_WIDTH*TILE_SIZE,SCREEN_HEIGHT*TILE_SIZE);
		osg = (Graphics2D) osc.getGraphics();
		osg.setFont(new Font("Impact",Font.PLAIN,10));
		time=20000;
		sunSetting=false;
		wd=new WeaponDisplay();
		//atmVar=0;
		lights=new ArrayList[NUM_DIMS];
		for(int i=0;i<NUM_DIMS;i++){
			lights[i]=new ArrayList<LightSource>();
		}
		plants=new ArrayList[NUM_DIMS];
		for(int i=0;i<NUM_DIMS;i++){
			plants[i]=new ArrayList<Plant>();
		}
		creatures=new ArrayList[NUM_DIMS];
		for(int i=0;i<NUM_DIMS;i++){
			creatures[i]=new ArrayList<Creature>();
		}
		player = new Player(this);
		timer = new Timer(10,this);//ten ms per tick
		c = new Cursor();
		c.putSelfInTW(this);
		keyboard=new SandKeys();
		addMouseMotionListener(c);
		addMouseListener(c);
		addMouseWheelListener(c);
		addKeyListener(keyboard);

		//if(DEBUG&&name.equals("ASTRAL_TEST")){ //Experimental biome algorithm
		//	terrGenNew();
		//}
		//else
		if(!readFromFile(name)){
			terrGenEarth();
			terrGenAstral();
			terrGenForest();
			//terrGenShard();
			//Any items in starting inventory should go here.
			//Inventory.change(Tile.fragmentedPortal,1);
			Inventory.getTool(new Tool(Tool.Type.stoneAxe));
		}
		if(Util.getProperty("debug").equals("true")){
			DEBUG=true;
			JOptionPane.showMessageDialog(this, "Debug mode is ON. This may have unintended consequences. Edit your warpage.properties file to correct this.");
		}
		Util.beginMusic(currDim);
		requestFocusInWindow();
		timer.setActionCommand("t");
		timer.start();
	}
	private void terrGenEarth(){
		int x=0;
		int y=SCREEN_HEIGHT;
		int biome=0;
		int biomeLength=0;
		int genLoc=0;
		final int NUM_BIOMES=3;
		do{
			int i=1;
			switch(biome){
			case 0: //hills
				y+=(int)(Math.random()*4)+(int)(Math.random()*4)-3; //change y of terrain by -3 to +3
				if(y<=0)y=1;
				if(y>=TOTAL_HEIGHT)y=TOTAL_HEIGHT-1;
				int plantIndex=(int)(Math.random()*100);
				if(plantIndex<=11){ //generate trees
					tiles[0][x][y]=Tile.nut;
					Plant p=new Plant(Tile.nut,x,y);
					p.setGrowth(plantIndex/3);
					plants[0].add(p);
				}else if(plantIndex<=17){
					tiles[0][x][y]=Tile.cherry;
					Plant p=new Plant(Tile.cherry,x,y);
					p.setGrowth((plantIndex-9)/3);
					plants[0].add(p);
				}
				else tiles[0][x][y]=Tile.grass;

				while(i<Math.random()*3+3&&i+y<TOTAL_HEIGHT&&i+y>=0){
					if(Math.random()<0.15)tiles[0][x][y+i]=Tile.looseStone;
					else tiles[0][x][y+i]=Tile.dirt;
					i++;
				}break;
			case 1:{ //desert
				y+=(int)(Math.random()*2)+(int)(Math.random()*2)-1;
				if(y<=0)y=1;
				if(y>=TOTAL_HEIGHT)y=TOTAL_HEIGHT-1;
				while(i<Math.random()*2+6&&i+y<TOTAL_HEIGHT&&i+y>=0){
					tiles[0][x][y+i]=Tile.sand;
					i++;
				}
				//Pyramid generation: TODO stairs
				if(y+1<TOTAL_HEIGHT&&x-genLoc>40&&biomeLength>12&&y>6&&tiles[0][x-12][y+1]==Tile.sand&&Math.random()>.95){
					genLoc=x;
					for(int j=0;j<13;j++){
						tiles[0][x-j][y]=Tile.sandBrick;
						int k=1;
						while(y+k<TOTAL_HEIGHT&&tiles[0][x-j][y+k]!=Tile.sand){//fill in sand underneath pyramid just in case
							tiles[0][x-j][y+k]=Tile.sand;
							k++;
						}
						if(j>6){
							//tiles[0][x-j-1][y-12+j]=Tile.sandStairs;
							tiles[0][x-j][y-12+j]=Tile.sandBrick;
						}
						else{
							//tiles[0][x-j+1][y-j]=Tile.sandStairs_F;
							tiles[0][x-j][y-j]=Tile.sandBrick;							
						}
					}
					//tiles[0][x-1][y-1]=Tile.door;
					for(int k=(int)(Math.random()*3)+2;k>0;k--){
						tiles[0][x-2-(int)(Math.random()*9)][y-1]=Tile.goldIngot;
					}
				}
			}break;
			case 2:{//snow/mountains
				int mountainDelta=(int)((Math.random()+Math.random())*5)-5; //change y of terrain by -5 to +5
				y+=mountainDelta;
				//System.out.println(mountainDelta);
				if(y<=0)y=1;
				if(y>=TOTAL_HEIGHT)y=TOTAL_HEIGHT-1;
				plantIndex=(int)(Math.random()*100);
				if(plantIndex<28){ //generate trees
					tiles[0][x][y]=Tile.pinecone;
					Plant p=new Plant(Tile.pinecone,x,y);
					p.setGrowth(plantIndex/7);
					plants[0].add(p);
				}else tiles[0][x][y]=Tile.grass;//this should be snow
				while(i<Math.random()*4&&i+y<TOTAL_HEIGHT&&i+y>=0){
					if(Math.random()<0.3)tiles[0][x][y+i]=Tile.looseStone;
					else tiles[0][x][y+i]=Tile.dirt;
					i++;
				}break;
			}
			}//Underground
			while((i+y<TOTAL_HEIGHT-60+(int)(Math.random()*5)||i<10)&&i+y>=0&&i+y<TOTAL_HEIGHT){//stone layers
				if(x>2&&Math.random()>=.995){
					tiles[0][x][y+i]=Tile.marble; //marble occurs in veins
					for(int k=0;k<=5;k++){
						int rx=(int)(Math.random()*3), ry=(int)(Math.random()*3);
						if(tiles[0][x-rx][y+i-ry]==Tile.stone){
							tiles[0][x-rx][y+i-ry]=Tile.marble;
						}	
					}
				}else if((y+i-150)>Math.random()*500){
					tiles[0][x][y+i]=Tile.glitterStone;
				}else{
					double stone=Math.random();
					if(stone>=.97&&i<20){
						tiles[0][x][y+i]=Tile.bronzeOre;
					}else if(stone>=.975){
						tiles[0][x][y+i]=Tile.greenOre;
					}else if(stone>=.96){
						tiles[0][x][y+i]=Tile.sulfur;
					}else if(stone>=.94 &&i>20){
						tiles[0][x][y+i]=Tile.ironOre;
					}else tiles[0][x][y+i]=Tile.stone;
				}
				i++;
			}
			while(i+y<TOTAL_HEIGHT){
				if(Math.random()>.99)tiles[0][x][y+i]=Tile.pyriteOre;
				else tiles[0][x][y+i]=Tile.bedrock;
				i++;
			}
			x++;
			biomeLength++;
			if(biomeLength>Math.random()*35+50){
				biomeLength=0;
				biome=(int)(Math.random()*NUM_BIOMES);
			}
		}while(x<TOTAL_WIDTH);
		for(int i=0;i<TOTAL_WIDTH*TILE_SIZE;i+=20*TILE_SIZE){
			creatures[0].add(new PeacefulAnimal(PeacefulAnimal.PUFF, i, 0, 5, this));
		}
	}
	private void terrGenAstral(){
		for(int i=0;i<TOTAL_WIDTH;i++){
			for (int j=6;j<TOTAL_HEIGHT;j++){
				if(Math.random()>.4){
					//if(Math.random()>.99)tiles[1][i][j]=Tile.enigmaOre; else
					tiles[1][i][j]=Tile.blueStone;
				}
				else if(Math.random()>.5)tiles[1][i][j]=Tile.blueSand;
				else if(Math.random()>.95){
					tiles[1][i][j]=Tile.starShard;
					lights[1].add(new LightSource(new Color(200,200,255),i,j,this));
				}
				if(tiles[1][i][j]==Tile.blueStone&&tiles[1][i][j-1]!=Tile.starShard&&Math.random()>0.99){
					tiles[1][i][j-1]=Tile.cosmicSprout;
					plants[1].add(new Plant(Tile.cosmicSprout,i,j-1));
				}
				if(j>=2&&i<TOTAL_WIDTH-2&&i>=2&&j<TOTAL_HEIGHT-2&&tiles[1][i-2][j-2]==Tile.blueStone&&Math.random()>.995){ //silexium occurs in veins
					for(int k=0;k<=5;k++){
						int x=(int)(Math.random()*5-2), y=(int)(Math.random()*5-2);
						if(tiles[1][x+i][y+j]==Tile.blueStone){
							tiles[1][x+i][y+j]=Tile.silexiumOre;
						}						
					}
				}
			}
		}
	}
	private void terrGenForest(){ //sylvan realm
		for(int j=0;j<TOTAL_HEIGHT/20;j++){
			int x=0;
			int y=(int)(Math.random()*TOTAL_HEIGHT);
			do{	
				int i=1;
				y+=(int)(Math.random()*3)+(int)(Math.random()*3)-2;
				if(y<=0)y=1;
				if(y>=TOTAL_HEIGHT)y=TOTAL_HEIGHT-1;
				double surface=Math.random();
				if(surface>.95){
					tiles[2][x][y]=Tile.glowMushroom;
					lights[2].add(new LightSource(new Color(0x0c397),x,y,this));
				}else if(surface>.85){
					tiles[2][x][y]=Tile.spiralGrass;
				}else if(surface>.80){
					tiles[2][x][y]=Tile.thornBush;
				}
				while(i<Math.random()*2+4&&i+y<TOTAL_HEIGHT&&i+y>=0){
					if(tiles[2][x][y+i]==null)tiles[2][x][y+i]=Tile.moss;
					i++;
				}
				x++;
			}while(x<TOTAL_WIDTH);
		}
		int numVaults=(int)(Math.random()*6)+6;
		int x=0, prevX=0;
		for(int i=0;i<numVaults;i++){//Cursed Vaults
			int y=(int)(Math.random()*(TOTAL_HEIGHT-5));
			while(Math.abs(prevX-x)<30)x=(int)(Math.random()*(TOTAL_WIDTH-5));
			prevX=x;
			//System.out.println(x);
			for(int j=1;j<4;j++){
				tiles[2][x+j][y]=tiles[2][x+j][y+4]=Tile.cursedStone;
			}
			for(int j=0;j<5;j++){
				for(int k=1;k<4;k++)tiles[2][x+j][y+k]=Tile.cursedStone;
			}
			//tiles[2][x+2][y+2]=Tile.redCrystal;
		}
		for(int i=0;i<TOTAL_WIDTH*TILE_SIZE;i+=20*TILE_SIZE){
			creatures[2].add(new PeacefulAnimal(PeacefulAnimal.SILKWORM, i, 2, 5, this));
		}
	}
	/*private void terrGenShard(){//Fragmented Realm
		int x=0;
		while(x<TOTAL_WIDTH){
			int y=TOTAL_HEIGHT/3+(int)(Math.random()*TOTAL_HEIGHT/3);//in the middle third of the y-axis
			int newY=y;
			while(newY<3*TOTAL_HEIGHT/2){
				newY+=(int)(Math.random()*(TOTAL_HEIGHT-y));
				for(int i=y;i<newY&&i<TOTAL_HEIGHT;i++){
					tiles[3][x][i]=Tile.shardStone;
				}
				tiles[3][x][TOTAL_HEIGHT-1]=Tile.shardStone;
				x++;
				if(x>=TOTAL_WIDTH)return;
			}
			while(newY>=y){
				newY-=(int)(Math.random()*(TOTAL_HEIGHT-y));
				for(int i=y;i<newY&&i<TOTAL_HEIGHT;i++){
					tiles[3][x][i]=Tile.shardStone;
				}
				if(tiles[3][x][TOTAL_HEIGHT-2]==null&&Math.random()<.15)tiles[3][x][TOTAL_HEIGHT-2]=Tile.crystalFlower;
				tiles[3][x][TOTAL_HEIGHT-1]=Tile.shardStone;
				x++;
				if(x>=TOTAL_WIDTH)return;
			}
		}
		//put loop here to do random gap in between
	}*/
	/*private void terrGenNew(){ //Experimental algorithm for generating two-dimensional biomes for Astral Realm (as opposed to one-dimensional in Terrestrial Realm)
		System.out.println("tergen start");
		int x=0,y=5;
		int tileCount=1;
		Tile[] all=Tile.values();
		while(tileCount<25){
			int length=x+(int)(Math.random()*5)+60;
			while(y<TOTAL_HEIGHT&&length>0){
				x=0;
				while(x<TOTAL_WIDTH&&tiles[0][x][y]!=null)x++;
				while(x<TOTAL_WIDTH&&x<length){
					tiles[0][x][y]=all[tileCount];
					x++;
				}
				length-=(int)(Math.random()*6)-2;
				y++;
			}
			y=(int)(Math.random()*55)+5;
			tileCount++;
		}		
	}*/
	/*private void derp(){
		sky[2]=Color.BLACK;
		for(int i=0;i<WIDTH;i++){
			for(int j=0;j<HEIGHT;j++){
				int derp = (int)(Math.random()*(Tile.values().length+5));
				if(derp>=5){
					derp-=5;
					tiles[2][i][j]=Tile.values()[derp];
				}else if (derp==4){
					lights[2].add(new LightSource(Color.RED,i,j));
				}
			}
		}
	}*/
	public void respond(int x, int y, boolean rightClick){ //Whenever the world is clicked, this is called.
		System.out.println("player xy: "+player.getX()+", "+player.getY()+"; cursor xy: "+x+", "+y);
		Item t=Inventory.getActiveTile();
		if(t instanceof Weapon&&!rightClick){ //Strike with weapon
			//System.out.println(x*TILE_SIZE+" "+offset*TILE_SIZE+" "+player.getX());
			if(player.getY()-(y+yOffset/STEPS_PER_TILE)*TILE_SIZE>Math.abs(player.getX()-(x+xOffset/STEPS_PER_TILE)*TILE_SIZE)){
				wd.appear(player.getX(), player.getY()-TILE_SIZE);
			}else if(x+xOffset/STEPS_PER_TILE<player.getX()/TILE_SIZE)wd.appear(player.getX()-TILE_SIZE,player.getY());
			else wd.appear(player.getX()+TILE_SIZE,player.getY());
		}else if(!rightClick&&t instanceof Tool&&((Tool)t).getType()==Tool.Type.beamRod){ //Special case: handle beam rod
			wd.appearBeam();
			((Tool)t).changeDur(-1);
		}
		else if(DEBUG||Math.abs((player.getX()/TILE_SIZE-xOffset/STEPS_PER_TILE)-x)+Math.abs((player.getY()/TILE_SIZE-yOffset/STEPS_PER_TILE)-y)<3){//For things that are limited-range
			x+=xOffset/STEPS_PER_TILE;
			y+=yOffset/STEPS_PER_TILE;
			if(rightClick){
				if(tileAt(x,y)==Tile.anvil||tileAt(x,y)==Tile.forge||tileAt(x,y)==Tile.transmuteTable||tileAt(x,y)==Tile.loom)Inventory.getConvPane().setCraftMethod(tileAt(x,y));//crafting station
				else if(tileAt(x,y)==Tile.door)tiles[currDim][x][y]=Tile.openDoor; //open and close doors
				else if(tileAt(x,y)==Tile.openDoor)tiles[currDim][x][y]=Tile.door;
				else if(t instanceof Tool){
					Tool t2=(Tool)t;
					if(t2.getType()==Tool.Type.chisel){ //carve stones with chisel
						if(tiles[currDim][x][y]==Tile.stone){
							tiles[currDim][x][y]=Tile.carvedStone;
							t2.changeDur(-1);
						}
						else if(tiles[currDim][x][y]==Tile.blueStone){
							tiles[currDim][x][y]=Tile.carvedBlueStone;
							t2.changeDur(-1);
						}
						else if(tiles[currDim][x][y]==Tile.sandBrick){
							tiles[currDim][x][y]=Tile.carvedSandBrick;
							t2.changeDur(-1);
						}
						else if(tiles[currDim][x][y]==Tile.marble){
							tiles[currDim][x][y]=Tile.carvedMarble;
							t2.changeDur(-1);
						}
						else if(tiles[currDim][x][y]==Tile.pyriteBlock){
							tiles[currDim][x][y]=Tile.carvedPyrite;
							t2.changeDur(-1);
						}
					}
				}
			}else{//default put and remove functions
				if(tileAt(x, y)==null&&t instanceof Tile)put((Tile)Inventory.getActiveTile(), x, y);
				else remove(x, y);	
				if(t instanceof Tool&&((Tool)t).getDur()==0)Inventory.breakTool(((Tool)t));//catch broken tools
			}

		}
		//else if(Inventory.getActiveTile()instanceof Tool&&((Tool)Inventory.getActiveTile()).getType()==Tool.ToolType.stoneGun)stoneGunRespond(x,y);
		Inventory.getDefaultInventory().paint();
	}
	public void put(Tile t, int x, int y){
		if(tiles[currDim][x][y]==null&&Inventory.get(t)>0){
			switch(t){
			case torch:
			case forge:{
				lights[currDim].add(new LightSource(new Color(255,220,90),x,y,this));
				break;
			}
			case starShard:{
				lights[currDim].add(new LightSource(new Color(200,200,255),x,y,this));
				break;
			}
			case glowMushroom:{
				lights[currDim].add(new LightSource(new Color(0x0c397),x,y,this));
				break;
			}
			//case discoBeacon:lights[currDim].add(new LightSource(Color.MAGENTA,x,y));
			case cherry:
			case nut:
			case pinecone:
				if(tileAt(x,y+1)!=Tile.dirt)return;
				//$FALL-THROUGH$
			case cosmicSprout:{
				plants[currDim].add(new Plant(t,x,y));
				break;
			}
			case earthPortal:{ //ensure place return portal
				if(tiles[0][x][y]!=null&&tiles[0][x][y].type==Tile.PORTAL){
					return; //make sure the return portal won't overwrite another portal
				}
				tiles[0][x][y]=portals[currDim];
				break;
			}
			case astralPortal:{
				if(tiles[1][x][y]!=null&&tiles[1][x][y].type==Tile.PORTAL)return;
				tiles[1][x][y]=portals[currDim];
				break;
			}
			case sylvanPortal:{
				if(tiles[2][x][y]!=null&&tiles[2][x][y].type==Tile.PORTAL)return;
				tiles[2][x][y]=portals[currDim];
				break;
			}
			case fragmentedPortal:{
				if(tiles[3][x][y]!=null&&tiles[3][x][y].type==Tile.PORTAL)return;
				tiles[3][x][y]=portals[currDim];
				break;
			}
			case grass:{
				if(y<TOTAL_HEIGHT-1&&tiles[currDim][x][y+1]!=Tile.dirt){
					return;
				}
				break;
			}
			case pinkFlower:{
				if(tiles[currDim][x][y+1]==Tile.wood&&tiles[currDim][x-1][y+1]!=null&&tiles[currDim][x-1][y+1].type==Tile.TREE_0&&tiles[currDim][x+1][y+1]!=null&&tiles[currDim][x+1][y+1].type==Tile.TREE_0){ //Create sylvan portal
					tiles[currDim][x][y+1]=Tile.sylvanPortal;
					tiles[2][x][y+1]=portals[currDim];
					Inventory.change(t,-1);
					return;
				}
				break;
			}
			default:
				break;
			}
			if(t.type==Tile.STAIRS&&x*TILE_SIZE<player.getX()){
				tiles[currDim][x][y]=Tile.values()[t.ordinal()+1];//ALWAYS make sure stairsf are right after stairs
			}else tiles[currDim][x][y]=t;
			Inventory.change(t,-1);
		}
	}
	public void remove(int x, int y){
		Tile t=tiles[currDim][x][y];
		if(t!=null){
			if(t.type==Tile.PLANT||t.type==Tile.TREE_0){ //remove plant from list and give correct resources
				if(t.type==Tile.TREE_0){
					Item i=Inventory.getActiveTile();
					if(Inventory.getActiveTile()instanceof Tool&&((Tool)i).getType().use==Tile.nut){
						((Tool)i).changeDur(-1);
					}else return;//if not holding an axe, can't cut down trees - but can pick up other kinds of plants.
				}
				int i;
				for(i=0;i<plants[currDim].size();i++){
					Plant p=plants[currDim].get(i);
					if(p.getX()==x&&p.getY()==y){
						switch(t){
						case nut: {
							if(p.getSize()>=3)Inventory.change(Tile.nut, (int)(Math.random()*3)+1);
							Inventory.change(Tile.wood, p.getSize());break;
						}
						case cosmicSprout: Inventory.change(tiles[currDim][x][y],p.getSize()+1);break;
						case cherry:{
							if(p.getSize()>=3)Inventory.change(Tile.cherry, (int)(Math.random()*3)+1);
							Inventory.change(Tile.wood, p.getSize());
							Inventory.change(Tile.pinkFlower, (int)(Math.random()*3)+1);break;
						}
						case pinecone:{
							Inventory.change(Tile.pinecone, (int)(Math.random()*3)+1);
							Inventory.change(Tile.wood, p.getSize()+1);break;
						}
						default:Inventory.change(tiles[currDim][x][y],p.getSize());
						}
						plants[currDim].remove(i);
						break;
					}
				}//if(i==plants[currDim].size())System.err.println("Error: No Plant object for "+t+" at "+x+", "+y);//accidentally triggers when most recent plant is destroyed
			}else if(t.type==Tile.STAIRS_F){
				Inventory.change(Tile.values()[t.ordinal()-1], 1);//get normal stairs, not stairsf
			}else if(t.type==Tile.LIGHT){ //remove light from list
				for(int i=0;i<lights[currDim].size();i++){
					if(lights[currDim].get(i).getX()==x&&lights[currDim].get(i).getY()==y){
						lights[currDim].remove(i);
						break;
					}
				}
				Inventory.change(t,1);
			}
			else if(t.type>=Tile.STONE_0){ //requires pickaxe
				Item i=Inventory.getActiveTile();
				if(Inventory.getActiveTile()instanceof Tool&&((Tool)i).getType().use==Tile.stone&&((Tool)i).getType().ordinal()>=t.type-10){//using ordinal() is extremely kludgy
					((Tool)i).changeDur(-1);																								//and unstable, because if more pickaxes
					if(t==Tile.greenOre)Inventory.change(Tile.gel,1);																	    //are added, all the other tools must be
					else if(t==Tile.pyriteOre)Inventory.change(Tile.pyrite, 1);															    //shifted over, which messes up save files
					else if(t==Tile.pyriteBlock||t==Tile.carvedPyrite)Inventory.change(Tile.pyrite, 4);
					else if(t==Tile.carvedBlueStone)Inventory.change(Tile.blueStone, 1);
					else if(t==Tile.carvedSandBrick)Inventory.change(Tile.sandBrick, 1);
					else if(t==Tile.carvedMarble)Inventory.change(Tile.marble, 1);
					else if(t==Tile.bedrock||t==Tile.mossyStone||t==Tile.carvedStone)Inventory.change(Tile.stone, 1);
					else Inventory.change(t,1);
				}
				else return;
			}
			else switch(t){
			case cursedStone:{
				Item i=Inventory.getActiveTile();
				if(i!=null&&((Tool)i).getType()==Tool.Type.goldPick){
					((Tool)i).changeDur(-1);
					return;
				}
				//Inventory.change(something);
				break;
			}
			case dirt:{
				if (tileAt(x,y-1)==Tile.grass||tileAt(x,y-1)==Tile.nut) remove(x,y-1);
				Inventory.change(Tile.dirt, 1);
				break;
			}
			case brick:{
				Inventory.change(Tile.stone, 1);
				break;
			}
			case astralPortal:{ //remove return portals
				tiles[1][x][y]=null;
				Inventory.change(t,1);
				break;
			}
			case earthPortal:{
				tiles[0][x][y]=null;
				Inventory.change(t,1);
				break;
			}
			case sylvanPortal:{
				tiles[2][x][y]=null;
				Inventory.change(t,1);
				break;
			}
			case fragmentedPortal:{
				tiles[3][x][y]=null;
				Inventory.change(t,1);
				break;
			}
			case openDoor:{
				Inventory.change(Tile.door, 1);
				break;
			}
			default:{
				Inventory.change(t,1);
			}
			/*case gunStone:{
				if(Inventory.getActiveTile()instanceof Tool&&((Tool)Inventory.getActiveTile()).getType()==Tool.ToolType.stoneGun){
					((Tool)Inventory.getActiveTile()).changeDur(1);
					break;
				}
			}*/
			}
			tiles[currDim][x][y]=null;
		}
	}
	public void pause(){
		paused=!paused;
		Util.setMusic(!paused);
		if(paused){
			Graphics2D g=(Graphics2D) getGraphics();
			g.setFont(new Font("Impact",Font.ITALIC,60));
			g.drawString("GAME PAUSED", 120, 120);
			//g.setFont(new Font("Impact",Font.ITALIC,20));
			//g.drawString("Press enter to save", 120, 170);
		}
	}
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(SCREEN_WIDTH*TILE_SIZE,SCREEN_HEIGHT*TILE_SIZE);
	}
	//simple accessor methods
	public Cursor getPointer(){
		return c;
	}
	public Player getPlayer(){
		return player;
	}
	public WeaponDisplay getWeaponDisplay(){
		return wd;
	}
	public int getXOffset(){
		return xOffset;
	}
	public int getYOffset(){
		return yOffset;
	}
	public void changeXOffset(int delta){
		xOffset+=delta;
	}
	public void changeYOffset(int delta){
		yOffset+=delta;
	}
	public boolean isSolid(int x, int y){
		if(x<0||y<0||x>=TOTAL_WIDTH||y>=TOTAL_HEIGHT)return true;
		return tiles[currDim][x][y]!=null&&tiles[currDim][x][y].isSolid;
	}
	public Tile tileAt(int x, int y) {
		if(x<0||y<0||x>=TOTAL_WIDTH||y>=TOTAL_HEIGHT)return null;
		return tiles[currDim][x][y];
	}
	/*public Liquid liquidAt(int x, int y) {
		if(x<0||y<0||x>=TOTAL_WIDTH||y>=TOTAL_HEIGHT)return null;
		return liquids[currDim][x][y];
	}

	public void addLiquid(Liquid l, int x, int y){
		liquids[currDim][x][y]=l;
	}*/
	public Image getImg(){
		return worldImg;
	}
	private int[][]getSunlight(){
		int depth;
		int[][]lighting=new int[SCREEN_WIDTH+1][SCREEN_HEIGHT+1];
		for(int i=0;i<=SCREEN_WIDTH;i++){
			if(isSolid(i+xOffset/STEPS_PER_TILE,yOffset/STEPS_PER_TILE)){
				continue;//so it doesn't expose ores at the top of the map
			}
			int j=-1;
			depth=2;
			do{
				j++;
				if(j>SCREEN_WIDTH)break;
				for(int k=-depth;k<=depth;k++){
					if(i+k>=0&&i+k<=SCREEN_WIDTH)lighting[i+k][j]++;
				}
				if(isSolid(i+xOffset/STEPS_PER_TILE,j+yOffset/STEPS_PER_TILE)&&tileAt(i+xOffset/STEPS_PER_TILE,j+yOffset/STEPS_PER_TILE)!=Tile.glass)depth--;//Glass should let in light, even if it is technically solid
			}while(depth>=0&&j<SCREEN_HEIGHT-1);
		}
		return lighting;
	}
	/*public void paint1(){//for testing lighting system
		int[][] lighting=getSunlight();
		for(int i=0;i<=SCREEN_WIDTH;i++){
			for(int j=0;j<SCREEN_HEIGHT;j++){
				osg.drawImage(Tile.values()[lighting[i][j]].getImg(), TILE_SIZE*i, TILE_SIZE*j, null);
			}
		}
		worldImg=osc;
		getGraphics().drawImage(worldImg, 0, 0, null);
	}*/
	public void paint(){
		player.move();
		osg.setColor(sky[currDim]);
		osg.fillRect(0, 0, SCREEN_WIDTH*TILE_SIZE,SCREEN_HEIGHT*TILE_SIZE);
		int[][] lighting=getSunlight();
		if(Inventory.currentEquip()!=null&&Inventory.currentEquip().ordinal()==5){//if wearing a mining helmet, draw a light source behind the player
			osg.setPaint(new RadialGradientPaint((float)(player.getX())+TILE_SIZE/2-xOffset*PIXELS_PER_STEP,
					(float)(player.getY())+TILE_SIZE/2-yOffset*PIXELS_PER_STEP,
					2*TILE_SIZE,
					new float[]{0,1},
					new Color[]{new Color(200,200,255,255),new Color(200,200,255,0)}));
			osg.fillOval(player.getX()-3*TILE_SIZE/2-xOffset*PIXELS_PER_STEP, player.getY()-3*TILE_SIZE/2-yOffset*PIXELS_PER_STEP, (4*TILE_SIZE), (4*TILE_SIZE));
			int lx=player.getX()/TILE_SIZE-xOffset/STEPS_PER_TILE,ly=player.getY()/TILE_SIZE-yOffset/STEPS_PER_TILE;
			for(int i=lx-4;i<=lx+4;i++){
				for(int j=ly-4;j<=ly+4;j++){
					if(i>=0&&i<=SCREEN_WIDTH&&j>=0&&j<SCREEN_HEIGHT)lighting[i][j]=Math.max(0,Math.min(5,lighting[i][j]+6-Math.abs(lx-i)-Math.abs(ly-j)));
				}
			}
		}
		for(LightSource l:lights[currDim]){
			osg.setPaint(l.getGradient());
			osg.fillOval((int)((l.getX()-1.5)*TILE_SIZE-xOffset*PIXELS_PER_STEP), (int)((l.getY()-1.5)*TILE_SIZE-yOffset*PIXELS_PER_STEP), (4*TILE_SIZE), (4*TILE_SIZE));

			//if(l.getX()>0&&tiles[currDim][l.getX()][l.getY()]==Tile.discoBeacon)l.nextRainbowColor();
			int lx=l.getX()-xOffset/STEPS_PER_TILE,ly=l.getY()-yOffset/STEPS_PER_TILE;
			for(int i=lx-4;i<=lx+4;i++){
				for(int j=ly-4;j<=ly+4;j++){
					if(i>=0&&i<=SCREEN_WIDTH&&j>=0&&j<SCREEN_HEIGHT)lighting[i][j]=Math.max(0,Math.min(5,lighting[i][j]+6-Math.abs(lx-i)-Math.abs(ly-j)));
				}
			}
		}
		for(int i=xOffset/STEPS_PER_TILE;i<=xOffset/STEPS_PER_TILE+SCREEN_WIDTH;i++){
			for(int j=SCREEN_HEIGHT+yOffset/STEPS_PER_TILE;j>=yOffset/STEPS_PER_TILE;j--){//iterate from bottom to top to fix gravity?
				Tile t=tiles[currDim][i][j];
				if(t!=null){
					if(t==Tile.stone&&j<TOTAL_HEIGHT-1&&tiles[currDim][i][j+1]==Tile.moss){
						t=tiles[currDim][i][j]=Tile.mossyStone;
					}
					if(t.gravity&&j+1!=TOTAL_HEIGHT&&tiles[currDim][i][j+1]==null){
						//System.out.println(j + "falling");
						tiles[currDim][i][j+1]=t;
						tiles[currDim][i][j]=null;
					}
					/*}else if(t==Tile.wire){
						if(tileAt(i,j-1)!=null&&tileAt(i,j-1).type==Tile.TECH)osg.drawImage(Tile.wireV, TILE_SIZE*(i-offset), TILE_SIZE*j, null);
						if(tileAt(i-1,j)!=null&&tileAt(i-1,j).type==Tile.TECH)osg.drawImage(Tile.wireH, TILE_SIZE*(i-offset), TILE_SIZE*j, null);
						if(tileAt(i,j+1)!=null&&tileAt(i,j+1).type==Tile.TECH)osg.drawImage(Tile.wireV, TILE_SIZE*(i-offset), TILE_SIZE*j+6, null);
						if(tileAt(i+1,j)!=null&&tileAt(i+1,j).type==Tile.TECH)osg.drawImage(Tile.wireH, TILE_SIZE*(i-offset)+6, TILE_SIZE*j, null);
					}*/
					else if(t.type!=Tile.PLANT&&t.type!=Tile.TREE_0){//if it's a plant draw the plant image instead
						if(DEBUG&&keyboard.pressed.contains(KeyEvent.VK_SPACE)){
							osg.drawImage(t.getImg(), TILE_SIZE*i-xOffset*PIXELS_PER_STEP, TILE_SIZE*j-yOffset*PIXELS_PER_STEP, null);
						}else osg.drawImage(t.getDarkenedImg(lighting[i-xOffset/STEPS_PER_TILE][j-yOffset/STEPS_PER_TILE]), TILE_SIZE*i-xOffset*PIXELS_PER_STEP, TILE_SIZE*j-yOffset*PIXELS_PER_STEP, null);
					}
					//else if(currDim==1&&tiles[currDim][i][j]==null&&Math.random()<.05)osg.drawImage(Tile.sparkle, TILE_SIZE*(i-offset), TILE_SIZE*j, null);
				}/*else{
					for(int u=0;u<TILE_SIZE;++u){
						int totalX = i*TILE_SIZE+u;
						if(totalX>=SCREEN_WIDTH*TILE_SIZE)break;
						for(int v=0;v<TILE_SIZE;++v){
							int totalY = j*TILE_SIZE+v;
							if(totalY>=SCREEN_HEIGHT*TILE_SIZE)break;
							int r = ((totalX+time)^totalY)&0xff;
							int g = (totalX&(totalY+time))&0xff;
							int b = (totalX|totalY)&0xff;
							((BufferedImage)osc).setRGB(totalX, totalY, (r<<16)|(g<<8)|b);
						}
					}
				}*/
			}
		}
		for(Plant p:plants[currDim]){//TODO update so it only draws plants on screen
			if(p!=null)osg.drawImage(
					p.getImg(),
					TILE_SIZE*p.getX()-xOffset*PIXELS_PER_STEP,
					TILE_SIZE*(p.getY()+1)-p.getImg().getHeight()-yOffset*PIXELS_PER_STEP,
					null);
		}
		/*int liquidTotal=0;
		for(int i=offset/STEP;i<=offset/STEP+SCREEN_WIDTH;i++){
			for(int j=HEIGHT-1;j>=0;j--){
				if(liquids[currDim][i][j]!=null){
					Liquid l=liquids[currDim][i][j];
					l.tick();
					if(l.getDepth()<=0){
						//System.out.println("Removing empty liquid at "+i+", "+j);
						liquids[currDim][i][j]=null;
					}else{
						osg.setPaint(WATER_COLOR);
						osg.fillRect(i*TILE_SIZE-offset*STEP, (j+1)*TILE_SIZE-l.getDepth(), TILE_SIZE, l.getDepth());
						liquidTotal+=l.getDepth();
					}
				}
			}
		}
		System.out.println(liquidTotal);//debug feature*/
		worldImg=osc;
		for(int i=0;i<creatures[currDim].size();i++){//draw the creatures
			Creature c=creatures[currDim].get(i);
			c.move();
			if(c instanceof PeacefulAnimal&&((PeacefulAnimal)c).typeIndex()==PeacefulAnimal.SILKWORM&&tiles[currDim][c.getX()/TILE_SIZE][c.getY()/TILE_SIZE]==null&&Math.random()>.9998){
				tiles[currDim][c.getX()/TILE_SIZE][c.getY()/TILE_SIZE]=Tile.cocoon;
			}
			if(c.getHealth()<=0){
				if(c.toString().equals("blader")&&Math.random()>.6)Inventory.change(Tile.ironIngot, 1);//bladers carry iron and drop it when killed
				creatures[currDim].remove(c);
				i--;
			}
			else osg.drawImage(c.getImg(), c.getX()-(xOffset*PIXELS_PER_STEP), c.getY()-(yOffset*PIXELS_PER_STEP), null);
		}
		//Can put code from SandKeys.keyPressed here to increase framerate and make ninja suit work.
		osg.drawImage(player.getImg(), player.getX()-(xOffset*PIXELS_PER_STEP), player.getY()+TILE_SIZE-player.getImg().getHeight()-(yOffset*PIXELS_PER_STEP), null);
		if(Inventory.currentEquip()!=null&&Inventory.currentEquip().ordinal()==4) osg.drawImage(Inventory.currentEquip().getImg(), player.getX()-(xOffset*PIXELS_PER_STEP), player.getY()-TILE_SIZE/2-(yOffset*PIXELS_PER_STEP), null);//draw the steampunk hat
		for(int i=0;i<player.getHealth();i++)osg.drawImage(healthImg,TILE_SIZE*i,0,null);
		if(wd.isVisible()){//draw the weapon if visible
			if(wd.isMagic()){
				wd.goToXY((player.getX()+TILE_SIZE*(c.getX()+xOffset/STEPS_PER_TILE))/2,(player.getY()+TILE_SIZE*(c.getY()+yOffset/STEPS_PER_TILE))/2);
			}
			osg.drawImage(wd.display(), wd.getX()-(xOffset*PIXELS_PER_STEP), wd.getY()-(yOffset*PIXELS_PER_STEP), null);
		}
		if(c.isVisible())osg.drawImage(Cursor.img,c.getX()*TILE_SIZE-(xOffset*PIXELS_PER_STEP)%TILE_SIZE,c.getY()*TILE_SIZE-(yOffset*PIXELS_PER_STEP)%TILE_SIZE,null);
		
		if(DEBUG){
			osg.setColor(Color.black);
			osg.drawString("x offset: "+xOffset, TILE_SIZE, 2*TILE_SIZE);
			osg.drawString("y offset: "+yOffset, TILE_SIZE, 3*TILE_SIZE);
		}
		
		getGraphics().drawImage(worldImg, 0, 0, null);
	}
	public void writeToFile(){
		getGraphics().drawString("Saving...", 360, 50);
		String path = Util.getProperty("saveLocation");
		if(path==null) path = System.getProperty("user.home")+File.separator+"WarpageSaves";
		new File(path).mkdirs();
		File save=new File(path+File.separator+name+".dat");
		try {
			if(!save.exists()){
				save.createNewFile();
			}
			BufferedWriter write = new BufferedWriter(new FileWriter(save, false));
			write.write(currDim+","+player.getX()+","+player.getY()+","+time*(sunSetting?-1:1)+","+player.getHealth()); //player coordinates
			write.newLine();
			write.write(Inventory.outputAll()); //inventory
			write.newLine();
			for(int i=0;i<plants.length;i++){ //all plants in worlds
				for(Plant p:plants[i]){
					write.write(i+" "+p.getTile().ordinal()+" "+p.getX()+" "+p.getY()+" "+p.getSize()+",");
				}
			}
			write.newLine();
			for(int i=0;i<plants.length;i++){ //all creatures in worlds
				for(Creature c:creatures[i]){
					write.write(i+" "+c.getID()+" "+c.getX()+" "+c.getY()+" "+c.getHealth()+",");
				}
			}
			write.newLine();
			for(int i=0;i<NUM_DIMS;i++){ //all tiles in worlds
				for(int j=0;j<TOTAL_WIDTH;j++){
					for(int k=0;k<TOTAL_HEIGHT;k++){
						if(tiles[i][j][k]==null)write.write("-1,");
						else write.write(tiles[i][j][k].ordinal()+",");
					}
					write.newLine();
				}
				write.newLine();
			}
			write.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public boolean readFromFile(String worldName){
		File save=new File(System.getProperty("user.home")+File.separator+"WarpageSaves"+File.separator+worldName+".dat");
		try {
			if(!save.exists()){
				return false;
			}
			BufferedReader read = new BufferedReader(new FileReader(save));
			String[] input=read.readLine().split(",");
			currDim=Integer.parseInt(input[0]); //player coordinates
			player.goTo(Integer.parseInt(input[1]),Integer.parseInt(input[2]));
			xOffset=(Math.min(player.getX()/TILE_SIZE,TOTAL_WIDTH)-SCREEN_WIDTH/2)*STEPS_PER_TILE;
			if(xOffset<0)xOffset=0;
			yOffset=(Math.min(player.getY()/TILE_SIZE,TOTAL_HEIGHT)-SCREEN_HEIGHT/2)*STEPS_PER_TILE;
			if(yOffset<0)yOffset=0;
			time=Math.abs(Integer.parseInt(input[3]));
			sunSetting=Integer.parseInt(input[3])<0;
			player.changeHealth(Integer.parseInt(input[4])-player.getHealth());
			input=read.readLine().split(",");
			for(int i=0;i<input.length;i++){ //inventory
				Inventory.change(Tile.values()[i], Integer.parseInt(input[i]));
			}
			input=read.readLine().split(",");//tools
			for(int i=0;i<input.length;i++){
				String[]moreInput=input[i].split(" ");
				try{
					Tool t=new Tool(Tool.Type.values()[Integer.parseInt(moreInput[0])]);
					t.setDur(Integer.parseInt(moreInput[1]));
					Inventory.getTool(t);
				}catch(NumberFormatException e){}
			}
			input=read.readLine().split(",");//equips
			for(int i=0;i<input.length;i++){
				String[]moreInput=input[i].split(" ");
				try{
					Equippable e=new Equippable(Integer.parseInt(moreInput[0]));
					e.setStrength(Integer.parseInt(moreInput[1]));
					Inventory.getEquip(e);
				}catch(NumberFormatException e){}
			}
			input=read.readLine().split(",");//weapons
			for(int i=0;i<input.length;i++){
				String[]moreInput=input[i].split(" ");
				try{
					Weapon w=new Weapon(Integer.parseInt(moreInput[0]));
					w.setDur(Integer.parseInt(moreInput[1]));
					Inventory.getWeapon(w);
				}catch(NumberFormatException e){}
			}
			input=read.readLine().split(",");//plants
			for(int i=0;i<input.length-1;i++){
				String[]moreInput=input[i].split(" ");
				int dim=Integer.parseInt(moreInput[0]);
				plants[dim].add(new Plant(Tile.values()[Integer.parseInt(moreInput[1])],Integer.parseInt(moreInput[2]),Integer.parseInt(moreInput[3])));
				plants[dim].get(plants[dim].size()-1).setGrowth(Integer.parseInt(moreInput[4]));
			}
			input=read.readLine().split(",");//creatures
			for(int i=0;i<input.length-1;i++){
				String[]moreInput=input[i].split(" ");
				int dim=Integer.parseInt(moreInput[0]);
				switch(Integer.parseInt(moreInput[1])){
				case 0:creatures[dim].add(new PeacefulAnimal(PeacefulAnimal.PUFF,Integer.parseInt(moreInput[2]),Integer.parseInt(moreInput[3]),Integer.parseInt(moreInput[4]),this));break;
				case 1:creatures[dim].add(new Enemy("blader",Integer.parseInt(moreInput[2]),Integer.parseInt(moreInput[3]),Integer.parseInt(moreInput[4]),3,this));break;
				case 2:creatures[dim].add(new PeacefulAnimal(PeacefulAnimal.SILKWORM,Integer.parseInt(moreInput[2]),Integer.parseInt(moreInput[3]),Integer.parseInt(moreInput[4]),this));break;
				default:creatures[dim].add(new Enemy("err",Integer.parseInt(moreInput[2]),Integer.parseInt(moreInput[3]),Integer.parseInt(moreInput[4]),3,this));break;
				}

			}
			int index=-1;
			for(int i=0;i<NUM_DIMS;i++){
				for(int j=0;j<TOTAL_WIDTH;j++){
					String rawInput=read.readLine();
					if(rawInput!=null)input=rawInput.split(",");
					for(int k=0;k<TOTAL_HEIGHT;k++){
						try{							
							if(input.length>1){
								index=Integer.parseInt(input[k]);
							}
						}catch(ArrayIndexOutOfBoundsException e){
							index=-1;
						}catch(NumberFormatException e){}
						if(index<0){
							tiles[i][j][k]=null;
						}
						else tiles[i][j][k]=Tile.values()[index];
						if(tiles[i][j][k]!=null&&tiles[i][j][k].type==Tile.LIGHT){
							switch(tiles[i][j][k]){
							case forge:
							case torch:
								lights[i].add(new LightSource(new Color(255,220,90),j,k,this));
								break;
							case starShard:
								lights[i].add(new LightSource(new Color(200,200,255),j,k,this));
								break;
							case glowMushroom:
								lights[i].add(new LightSource(new Color(0x0c397),j,k,this));
								break;
							default:
								lights[i].add(new LightSource(Color.WHITE,j,k,this));
							}
						}
					}
				}
				read.readLine();
			}
			read.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		//String ac=arg0.getActionCommand();
		if(!paused){
			time+=sunSetting?-1:1;
			if(time==5*60*100){//five minutes in each half day * 60 sec/min * 100 tick/sec
				sunSetting=true;
			}
			if(time==0){
				sunSetting=false;
				for(ArrayList<Plant> plist:plants){
					for(Plant p:plist){
						p.grow();
					}
				}
				int max=(int)(Math.random()*16)+16;
				for(int i=0;i<max;i++){
					creatures[0].add(new Enemy("blader", (int)(Math.random()*TOTAL_WIDTH)*TILE_SIZE, 0, 5, 3, this));
				}
			}
			//try{
			sky[0]=new Color(0,time/(118*2),time/118);//118 = max time / 255, rounded up
			//}catch(Exception e){
			//	System.out.println(time);
			//	e.printStackTrace();
			//}
			if(player.getHealth()<=0){
				JOptionPane.showMessageDialog(this, "You have died.");
				for(ArrayList<LightSource> l:lights)l.clear();
				for(ArrayList<Plant> p:plants)p.clear();
				for(ArrayList<Creature> c:creatures)c.clear();
				Inventory.clear();
				keyboard.pressed.clear();
				if(!readFromFile(name)){
					JOptionPane.showMessageDialog(this, "There is no save file for this world.\nPress OK to exit.\n(Protip: Save your world next time by pressing Enter.)");
					System.exit(0);
				}
			}
			paint();
		}
	}
}
