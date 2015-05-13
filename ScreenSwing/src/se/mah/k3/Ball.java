package se.mah.k3;
import java.util.Random;

public class Ball {
	
	//Current position for the ball
	int x = 100;
	int y = 100;
	
	//Current Speed for the ball
	double ySpeed = 7;
	double xSpeed = 10;
	
	//relative to screen variables, startposition for level
	
	int relX = 120;
	int relY = 40;
	
	//Relative Screen size
	int screenWidth = relX +545;
	int screenHeight = relY +540;
	
	//Ball size
	int size = 10;
	
	//Fluxuating values in X-axis
	int minXSpeed = 1;
	int maxXSpeed = 4;
	
	// Fluxuating values in Y-axis 
	int minYSpeed = -1;
	int maxYSpeed = 1;
	
	//lifes
	//public int player1Lifes =5;
	
	//public int player2Lifes = 5;
	
	public Ball(){

		
	}
	
	public int getBallXSpeed(){
		x +=xSpeed;
		//System.out.println("xspeed= "+ x);
		bounceBall();
		return x;	
	
	}
	
	public int getBallYSpeed(){
		y +=ySpeed;
		//System.out.println("yspeed= "+ y);
		bounceBall();
		return y;
	}	
		
	public void displayBall(){
		//maybe
	}
	
	public int getSize(){
		return size;
	}
	
	public void bounceBall(){
		
		//if the ball bounce on x-axis
		if (x >= screenWidth - size/2|| x <= relX + size/2){
			 Random rand = new Random();
			  
			 //SideSelect randomizes which direction the ball starts with
			 int sideSelect = rand.nextInt((1 - 0) + 1) +0;
			 
			//Ball to the left
			if(sideSelect == 0){
				//xSpeed = rand.nextInt(((minXSpeed*-1) - (-maxXSpeed)) + 1) - maxXSpeed;
					xSpeed = -1;
			}
			//Ball to the right
			else{
				//xSpeed = rand.nextInt((maxXSpeed - minXSpeed) + 1) + maxXSpeed;
				xSpeed =  1;
			}
			//Reset the ball
			ySpeed = rand.nextInt((maxYSpeed - minYSpeed) + 1) -maxYSpeed;
			if(ySpeed == 0){
				Random r = new Random();
				int i = r.nextInt(1);
				
				if(i == 0){
					ySpeed = -2;
				}
				if(i == 1){
					ySpeed = 2;
				}
				
				
			}
			x = screenWidth/2;
			y = screenHeight/2;
		}
		
		//if ball bounces on y-axis
		if (y >= screenHeight - size/2|| y <= relY+  size/2){
			ySpeed *=-1;
			y += ySpeed;
			
		}
		
	}
		
	
	public void paddleBounce(){
		
		xSpeed *= -1;
		x +=xSpeed;
		
	}
	
	
}