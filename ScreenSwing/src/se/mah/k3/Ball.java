package se.mah.k3;
import java.util.Random;

public class Ball {

	//Current Speed for the ball
	private int ySpeed = 1;
	private int xSpeed = 1;
	
	//Ball size
	private int size = 20;
	
	//Current position for the ball
	private int x = 0;
	private int y = 0;

	
	
	public Ball(){

		
	}
	
	public int getXPos(){
		
		return x;
	}
	
	public int getYPos(){
		
		return y;
	}
	public void setYPos(int yPos){
		y = yPos;
	}
	
	public void setXPos(int xPos){
		x = xPos;
	}
	
	public int getBallXSpeed(){

		return xSpeed;	
	
	}
	
	public int getBallYSpeed(){

		return ySpeed;
	}	
		
	
	public int getSize(){
		return size;
	}
	
	public void setBallXSpeed(int speed){
		this.xSpeed = speed;
		
	}
	
	public void setBallYSpeed(int speed){
		this.ySpeed = speed;
		
	}
	
	public void setBallSize(int size){
		this.size = size;
		
	}
	

	
}