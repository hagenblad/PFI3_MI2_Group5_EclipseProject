package se.mah.k3;

public class Level {
	
	
	//relative to screen variables, startposition for level	
		static int relX = 120;
		static int relY = 40;
		static int screenWidth = relX +545;
		static int screenHeight = relY +540;
		boolean wall1 = false;
		boolean wall2 = false;
		boolean wall3 = false;
		
	
	public boolean getWall1(){
		return wall1;
	}
	public void setWall1(){	
	}
		
	
	public boolean getWall2(){
		return wall2;
	}
	public void setWall2(){
	}
	
	
	public boolean getWAll3(){
		return wall3;
	}
	public void setWall3(){
	}
	

}
