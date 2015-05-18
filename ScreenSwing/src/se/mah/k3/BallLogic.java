package se.mah.k3;

import java.util.Random;

public class BallLogic {

	private Ball bally = new Ball();
	private User user = new User();
	
	public int player1lives = 5;
	public int player2lives = 5;

	//relative to screen variables, startposition for level	
	int relX = 120;
	int relY = 40;
	
	//Relative Screen size
	int screenWidth = relX +545;
	int screenHeight = relY +540;
	
	//Fluxuating values in X-axis
	int minXSpeed = 1;
	int maxXSpeed = 4;
	
	// Fluxuating values in Y-axis 
	int minYSpeed = -1;
	int maxYSpeed = 1;
	
	//public int player2Lifes = 5;
	public BallLogic(Ball ball){
		this.bally = ball;
		//this.user = player;
	}
	
	//compare-funktion här boolean??
	//
	public void comparePosition(int x, int y, int width, int height){
	
	// Move ball
	moveBall();
	//Check if goal
	checkBounceGoal();
	//Check if bounce wall
	checkBounceWall();
	//Check if player hit
	paddleBounce(x,y, width, height);
	

	}
	//bounceball måste bli
	
	public void checkBounceGoal(){
		
		//if the ball bounce on x-axis on right side
		if (bally.getXPos()>= screenWidth - bally.getSize()/2){
			 //Random rand = new Random();
			 bally.setBallXSpeed(-1);
			 player2lives--;
			 System.out.println("player 2 lost a life");
			 System.out.println(String.valueOf(player2lives));
			 reMatch();
		}

		
			if(bally.getXPos() <= relX + bally.getSize()/2){
				//Random rand2 = new Random();
				bally.setBallXSpeed(1);
				player1lives--;
				System.out.println("player 1 lost a life");
				System.out.println(String.valueOf(player1lives));
				reMatch();
			}
			

			
			if(bally.getBallYSpeed()== 0){
				Random r = new Random();
				int i = r.nextInt(1);
				
				if(i == 0){
					bally.setBallYSpeed(-2);
				}
				if(i == 1){
					bally.setBallYSpeed(2);
				}
				
				
			}

		
		
	}
	//if ball bounces on y-axis
	public void checkBounceWall(){

		if (bally.getYPos() >= screenHeight - bally.getSize()/2|| bally.getYPos() <= relY+  bally.getSize()/2){

			int ySpeed = bally.getBallYSpeed();
			bally.setBallYSpeed(ySpeed *= -1);
			
			//jump one step before exiting the loop
			int y = bally.getYPos(); 
			bally.setYPos(y	+= ySpeed);
			
		}
		
	}
	
	public void paddleBounce(int xPos, int yPos, int width, int height){
		//paddle collision
		if(bally.getXPos() >= xPos -5 && bally.getXPos() <= xPos + width+5 ){
			
			System.out.println("Nice X pos");	
			
			if( bally.getYPos() >= yPos -5 && bally.getYPos() <= yPos + height+5){
				
				System.out.println("Nice YYY Pos");
				
				int xSpeed = bally.getBallXSpeed(); 
					
				bally.setBallXSpeed(xSpeed*= -1);
				int tempx = bally.getXPos();
				bally.setXPos(tempx += xSpeed);
			
			}

		
		}
	}
	
	//Moves every frame
	public void moveBall(){
		int tempx,tempx2,tempy,tempy2;
		
		//iterate x
		tempx = bally.getXPos();
		tempx2 = bally.getBallXSpeed();

		
		//iterate y
		tempy = bally.getYPos();
		tempy2 = bally.getBallYSpeed();
		
		//insert
		bally.setXPos(tempx + tempx2);
		bally.setYPos(tempy + tempy2);
		
	}
	
	
	
	//restarts on goal score
	public void reMatch(){
		
		bally.setBallYSpeed(bally.getBallYSpeed());
		bally.setXPos(screenWidth/2);
		bally.setYPos(screenHeight/2);
		
		//Reset the ball
		Random rand = new Random();
		
		bally.setBallYSpeed(rand.nextInt((maxYSpeed - minYSpeed) + 1) -maxYSpeed);
		
	}

}
