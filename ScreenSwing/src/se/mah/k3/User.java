package se.mah.k3;

import java.awt.Color;

import com.firebase.client.DataSnapshot;

public class User implements Comparable<User>{
	
	private String id;
	private int xPos = 100;
	private int yPos = 100;
	private double xRel=0;
	private double yRel=0;
	private int lives;
	private long delay; // int here makes more sense than a double/long?
	public int userHeight = 0;
	public int userWidth = 0;
	private Integer position;
	

	private Color c = new Color(100,100,100);
	

	public User(String id, int xPos, int yPos, int lives, int position) {
		this.id = id;
		this.xPos = xPos;
		this.yPos = yPos;
		this.lives = lives;
		this.position = position;
	}
	
	public User(){
		// empty construcotr
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getxPos() {
		return xPos;
	}
	public void setxPos(int xPos) {
		this.xPos = xPos;
	}
	public int getyPos() {
		return yPos;
	}
	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public double getyRel() {
		return yRel;
	}
	public void setyRel(double yRel) {
		this.yRel = yRel;
	}
	public double getxRel() {
		return xRel;
	}
	public void setxRel(double xRel) {
		this.xRel = xRel;
	}
	
	public int getLives() {
		return lives;
	}
	public void setLives(int lives) {
		this.lives = lives;
	}
	
	public long getDelay() {
		return delay;
	}
	public void setDelay(long d) {
		this.delay = d;
	}
	
	public int getPosition(){
		return position;
	}
	
	public void setPosition(int position){
		this.position = position;
	}
	
	
	@Override
	public int compareTo(User o) {
		return position.compareTo(o.getPosition()); // Ändrade här att jämföra position istället för Id
	}
	public Color getColor() {
		return c;
	}
	public void setColor(Color c) {
		this.c = c;
	}
	
}

