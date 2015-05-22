package se.mah.k3;

public class Level {
	
	
	//relative to screen variables, startposition for level	
	static int relX = 200;
	static int relY = 20;
	static int screenWidth = relX + 540;
	static int screenHeight = relY + 540;
	static int screenWidthForRect = 600;
	static int screenHeightHeightForRect = 600;
	boolean wall1 = false;
	boolean wall2 = false;
	boolean wall3 = false;
	
	//Used for handling gamestates
	enum GameState{
		START,
		GAME,
		END,
		RESTART
	}
	GameState gS = GameState.START;
	
	public Level(){
		gS = GameState.START;
	}
	
	void GameStateUpdate(){
		switch(gS){
		case START:
			
			break;
		case GAME:
			
			break;
		case END:
			
			break;
		case RESTART:
			
			break;
		}
	}
	
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
	
	
	public boolean getWall3(){
		return wall3;
	}
	public void setWall3(){
	}
	

}
