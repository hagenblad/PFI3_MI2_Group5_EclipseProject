package se.mah.k3;

import java.util.Random;


public class BallLogic {

	private Ball bally = new Ball();
	private User user = new User();
	
	public int player1lives = 5;
	public int player2lives = 5;
	public int player3lives = 5;
	public int player4lives = 5;

	Level level = new Level();
	
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
	
	//Corner bounce-check
	cornerBounce();
	
	//Check if player hit
	paddleBounce(x,y, width, height);
	
	

	}
	//bounceball måste bli
	
	public void checkBounceGoal(){
		
		//if the ball bounce on x-axis on right side
		if (bally.getXPos()>= level.screenWidth - bally.getSize()/2){
			 //Random rand = new Random();
			 bally.setBallXSpeed(-1);
			 player2lives--;
			 System.out.println("player 2 lost a life");
			 System.out.println(String.valueOf(player2lives));
			 reMatch();
			 gameOver();
		}

		
			if(bally.getXPos() <= level.relX + bally.getSize()/2){
				//Random rand2 = new Random();
				bally.setBallXSpeed(1);
				player1lives--;
				System.out.println("player 1 lost a life");
				System.out.println(String.valueOf(player1lives));
				reMatch();
				gameOver();
			}
			
			//Goal on Y axis
            //Wall 1
            if (bally.getYPos()<= level.relY + bally.getSize()/2){
                    bally.setBallYSpeed(1);
                    reMatch();
            }
           
           
            //WAll 3
           
            if (bally.getYPos()>= level.screenHeight - bally.getSize()/2){
                    bally.setBallYSpeed(-1);
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
		int radie = bally.getSize() /2;
		int area =	radie * radie * 3;
		
		
		
		if (bally.getYPos() >= level.screenHeight - bally.getSize()/2|| bally.getYPos() <= level.relY+  bally.getSize()/2){

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
			
		//	System.out.println("Nice X pos");	
			
			if( bally.getYPos() >= yPos -5 && bally.getYPos() <= yPos + height+5){
				
			//	System.out.println("Nice YYY Pos");
				
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
	
	public void cornerBounce(){
		//upper left corner
		if(bally.getXPos()<= level.relX+100 && bally.getXPos() >= level.relX){
		//	System.out.println("X CORNER");
			
			if(bally.getYPos()<=level.relY+100 && bally.getYPos()>=level.relY){
			//	System.out.println("Y N X CORNER");
				
			}
			
		}
		
	}
	
	
	//restarts on goal score
	public void reMatch(){
		bally.setBallYSpeed(bally.getBallYSpeed());
		bally.setXPos(level.screenWidth/2);
		bally.setYPos(level.screenHeight/2);
		Random rand = new Random();
		bally.setBallYSpeed(rand.nextInt((maxYSpeed - minYSpeed) + 1) -maxYSpeed);
		//Insert kill a life
		
	}
	
	// restarts the match when a player's life count is 0
		public void gameOver(){
			if (player1lives == 0){
				System.out.println("Player 2 wins");
				reMatch();
				player1lives = 5;
				player2lives = 5;
//				bally.setXPos(screenWidth/2);
//				bally.setYPos(screenHeight/2);
//				bally.setBallYSpeed(0);
//				bally.setBallXSpeed(0);
			}
		
			if(player2lives == 0){
				System.out.println("Player 1 Wins");
				reMatch();
				player1lives = 5;
				player2lives = 5;
//				bally.setXPos(screenWidth/2);
//				bally.setYPos(screenHeight/2);
//				bally.setBallYSpeed(0);
//				bally.setBallXSpeed(0);
			}
		}

}
