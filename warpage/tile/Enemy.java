package tile;

import gui.Inventory;
//import gui.Inventory;
import gui.TileWorld;
import gui.WeaponDisplay;

public class Enemy extends Creature {
	private int yVel,xVel;
	private int attack;
	private Player player;
	private WeaponDisplay wd;
	public Enemy(String filePath, int x, int y, int h, int attack, TileWorld tw) {
		super(filePath,x,y,h,tw);
		yVel=0;
		player=tw.getPlayer();
		wd=tw.getWeaponDisplay();
		this.attack=attack;
	}
	@Override
	public void move(){
		int deltaX=Math.abs(player.getX()-getX());
		int deltaY=Math.abs(player.getY()-getY());
		if(deltaX>360||deltaX<deltaY*2){//limited field of view
			if(Math.random()>.95)setDir(!isRight());
		}
		else if(player.getX()>getX())setDir(false);
		else setDir(true);
		boolean flag=false;//horriblepathetic kludge
		if(yVel>0&&canMove(0)){
			move(0,-3);
			yVel--;
		}else if(canMove(2)){
			move(0,3);
		}else flag=true;
		if(xVel>0){
			if(canMove(1)){
				move(3,0);
				xVel--;
			}else xVel=0;
		}else if(xVel<0){
			if(canMove(3)){
				move(-3,0);
				xVel++;
			}else xVel=0;
		}else flag=true;
		if(flag){
			if(canMove(isRight()?3:1))move(isRight()?-2:2,0);
			else if(!canMove(2))yVel=10;
		}
		if(wd.isVisible()&&Math.abs(wd.getX()-getX())<=TileWorld.TILE_SIZE&&Math.abs(wd.getY()-getY())<=TileWorld.TILE_SIZE){
			if(wd.isMagic()){
				changeHealth(-2);
			}else{
				Weapon w=getTW().getWeaponDisplay().getWeapon();
				changeHealth(-w.getDamage());
				w.changeDur(-1);
				if(w.getDur()<=0)gui.Inventory.breakWeapon(w);
				if(wd.getX()>getX())xVel-=20;
				else xVel+=20;
				//System.out.println("Taking damage. Health is now "+getHealth());
				//wd.hide(); TODO Might want to put this line back in. Weird glitches with swords doing way more damage than they're supposed to.
			}
		}
		else if(Math.abs(player.getX()-getX())<=TileWorld.TILE_SIZE&&Math.abs(player.getY()-getY())<=TileWorld.TILE_SIZE){
			int dmg=attack;
			if(Inventory.currentEquip()!=null&&Inventory.currentEquip().is("shield")){
				dmg-=Inventory.currentEquip().getStrength();
				if(Inventory.currentEquip().name().equals("thornyShield"))this.changeHealth(-Inventory.currentEquip().getStrength());
			}
			if(dmg<0)dmg=0;
			player.changeHealth(-dmg);
			if(player.getX()>getX())xVel-=20;
			else xVel+=20;
		}
		//if(getX()%3!=0)System.out.println(getX());
	}
}

