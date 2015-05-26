package se.mah.k3;

import java.util.Random;


public class BallLogic {

	private Ball bally = new Ball();
	private User user = new User();
	Level level = new Level();
		
	//relative to screen variables, startposition for level	
	int relX = level.relX;
	int relY = level.relY;
	
	public int player1lives = 5;
	public int player2lives = 5;
	public int player3lives = 5;
	public int player4lives = 5;

	
	//Fluxuating values in X-axis
	int minXSpeed = 1;
	int maxXSpeed = 4;
	
	// Fluxuating values in Y-axis 
	int minYSpeed = -3;
	int maxYSpeed = 3;
	
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
	
	public int bounceX(int i){
		return (i *=-1);
		
	}
	
	public int bounceY(int i){
		return (i *=-1);
		
	}
	//bounceball måste bli
	
	public void checkBounceGoal(){
		
		//if the ball bounce on x-axis on right side
		if (bally.getXPos()>= level.screenWidth - bally.getSize()/2){
			 //Random rand = new Random();
			 bally.setBallXSpeed(-1);
				//Reset the ball
			 player2lives--;
			// System.out.println("player 2 lost a life");
			// System.out.println(String.valueOf(player2lives));
			 reMatch();
			 gameOver();
		}

		
		if(bally.getXPos() <= relX + bally.getSize()/2){
			//Random rand2 = new Random();
			bally.setBallXSpeed(1);
			//Reset the ball
			player1lives--;
//			System.out.println("player 1 lost a life");
//			System.out.println(String.valueOf(player1lives));
			reMatch();
			gameOver();
		}
			
			

//			//Goal on Y axis
//            //Wall 1
//            if (bally.getYPos()<= level.relY + bally.getSize()/2){
//                    bally.setBallYSpeed(1);
//                    reMatch();
//            }
//           
//           
//            //WAll 3
//           
//            if (bally.getYPos()>= level.screenHeight - bally.getSize()/2){
//                    bally.setBallYSpeed(-1);
//                    reMatch();
//            }
			
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

		
		if (bally.getYPos() >= level.screenHeight - bally.getSize()/2|| bally.getYPos() <= level.relY+  bally.getSize()/2){

			int ySpeed = bally.getBallYSpeed();
			bally.setBallYSpeed(bounceY(ySpeed));
			ySpeed = bally.getBallYSpeed();
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
					
				bally.setBallXSpeed(bounceX(xSpeed));
				xSpeed = bally.getBallXSpeed();
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
		//temporary variables

	//	int xSpeed = bally.getBallXSpeed(); 
	//	int ySpeed = bally.getBallYSpeed();
		
		//System.out.println("1x = " + bally.getBallXSpeed());
		//System.out.println("1y = " + bally.getBallYSpeed() + "");
		bally.setBallXSpeed(bounceX(bally.getBallXSpeed()));
	//	xSpeed = bally.getBallXSpeed();
		
		bally.setBallYSpeed(bounceX(bally.getBallYSpeed()));
	//	ySpeed = bally.getBallYSpeed();
		
		
		//System.out.println("2x = " + bally.getBallXSpeed() + "");
		//System.out.println("2y = " + bally.getBallYSpeed() + "\n");
		//Variables for position
		
		int y = bally.getYPos();
		int x = bally.getXPos();

		//upper left corner
		if(bally.getXPos()<= level.relX+100 && bally.getXPos() >= level.relX){
		//	System.out.println("X CORNER");
			
			if(bally.getYPos()<=level.relY+100 && bally.getYPos()>=level.relY){
			//	System.out.println("Y N X CORNER");
				
			}
			
		}

		
		//Jump one step
		bally.setYPos(y	+= bally.getBallYSpeed());
		bally.setXPos(x	+= bally.getBallXSpeed());
	}
	
	
	
	//handles ball respawn on goal score
	public void reMatch(){
		
		bally.setBallYSpeed(bally.getBallYSpeed());
		bally.setXPos(level.screenWidth/2);
		bally.setYPos(level.screenHeight/2);

		Random rand = new Random();
		
		
		bally.setBallYSpeed(rand.nextInt((maxYSpeed - minYSpeed) + 1) -maxYSpeed);
		
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

	
		//Insert kill a life
		
		//Reset the ball
		Random rand2 = new Random();
		
		bally.setBallYSpeed(rand2.nextInt((maxYSpeed - minYSpeed) + 1) -maxYSpeed);
		
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
