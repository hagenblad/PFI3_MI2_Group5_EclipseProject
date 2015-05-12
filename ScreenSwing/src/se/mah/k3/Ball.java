<<<<<<< HEAD
package se.mah.k3;

public class Ball {

	public int x = 100;
	public int y = 100;
	int ySpeed = 7;
	int xSpeed = 10;
	int screenWidth = 900;
	int screenHeight = 900;
	int size = 40;
	
	
	public int getBallXSpeed(){
		x +=xSpeed;
		System.out.println("xspeed= "+ x);
		bounceBall();
		return x;
	}
		

	public int getBallYSpeed(){
		y +=ySpeed;
		System.out.println("yspeed= "+ y);
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
		if (x >= screenWidth - size/2|| x <= 0 + size/2){
			xSpeed = xSpeed*-1;
			x += xSpeed;
		}
		if (y >= screenHeight - size/2|| y <= 0+  size/2){
			ySpeed *= -1;
			y += ySpeed;
			
		}
	}
	
	
}
=======
package se.mah.k3;
import java.util.Random;
public class Ball {


	//BallThread ballThread = new BallThread();
	int x = 100;
	int y = 100;
	float ySpeed = 7;
	float xSpeed = 10;
	int screenWidth = 900;
	int screenHeight = 900;
	int size = 40;
	public Ball(){
		//constructor
		
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
		if (x >= screenWidth - size/2|| x <= 0 + size/2){
			 Random rand = new Random();

			    // nextInt is normally exclusive of the top value,
			    // so add 1 to make it inclusive
			    int sideSelect = rand.nextInt((1 - 0) + 1) +0;
			
			System.out.println("SIDE SELECT  " + sideSelect);
		
			//Ball to the left
			if(sideSelect == 0){
				xSpeed = rand.nextInt((-4 - -10) + 1) -10;
			}
			//Ball to the right
			else{
				xSpeed = rand.nextInt((8 - 1) + 1) +4;
			}
			ySpeed = rand.nextInt((5 - -5) + 1) -5;
			x = screenWidth/2;
			y = screenHeight/2;
		}
		//if ball bounces on y-axis
		if (y >= screenHeight - size/2|| y <= 0+  size/2){
			ySpeed *= -1;
			y += ySpeed;
			
		}
	}
	
	
}
>>>>>>> master
