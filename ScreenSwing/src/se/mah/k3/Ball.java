package se.mah.k3;

public class Ball {
	public int speed = 10;
	public int x = 0;
	public int y = 0;
	int screenWidth = 900;
	int size = 50;
	//int screenHeight = ?;
	
	public int getBallSpeed(){
		x +=speed;
		System.out.println("speed= "+ x);
		bounceBall();
		return x;
	}
	
	public void displayBall(){
		//maybe
	}
	
	public int getSize(){
		return size;
	}
	
	public void bounceBall(){
		if (x >= screenWidth || x<=0){
			speed *= -1;
			
		}
//		if x < sceenHeight
		
	//	if y > screenWidth
	//	if y < sceenHeight
	}
	
	
}
