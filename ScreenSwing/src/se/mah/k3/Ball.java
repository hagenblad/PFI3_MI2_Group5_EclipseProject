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
