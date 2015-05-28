
package se.mah.k3;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.awt.Polygon;
import java.awt.geom.Area;

import javax.swing.JPanel;

import se.mah.k3.Level.GameState;
import se.mah.k3.TimerClass.RemindTask;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class DrawPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Firebase myFirebaseRef;
	private Ball ball = new Ball();

	// Boolean = work on mac;
	// work on mac = false;
	//hdmi (== non existing);
	//creates a ball
	BallLogicV2[] ships = BallLogicV2.lives(4);

	Level level = new Level();

	private boolean start = false;

	int ballXPos = level.screenWidth;
	int ballYPos = level.screenHeight;

	int listCount;

	int paddlePosY;
	int paddlePosX;
	int paddleBottom;
	int paddleTop;
	int y;

	//player ping
	long playerDelay;
	int playerPing;
	int playerDelayint;
	int playerPingSize;

	public Polygon polyTRC;
	public Polygon polyTLC;
	public Polygon polyBRC;
	public Polygon polyBLC;
	public Polygon polyBally;

	//background image
	
	Image img1 = Toolkit.getDefaultToolkit().getImage("src/images/bakgrundis.jpg");
	
	//private int player1lives = 5;
	//private int player2lives = 5;


	//A vector is like an ArrayList a little bit slower but Thread-safe. This means that it can handle concurrent changes. 
	private Vector<User> users= new Vector<User>();
	//	private Vector<User> horizontalUsers = new Vector<User>();
	Font font = new Font("Verdana", Font.PLAIN, 20); //Har du Exo installerat, byt "Verdana" till "Exo"..

	public DrawPanel() {


		myFirebaseRef = new Firebase("https://pingispong.firebaseio.com/");
		myFirebaseRef.removeValue(); //Cleans out everything
		myFirebaseRef.child("ScreenNbr").setValue(Constants.screenNbr);  //Has to be same as on the app. So place specific can't you see the screen you don't know the number
		myFirebaseRef.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildRemoved(DataSnapshot arg0) {}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {}

			//A user changed some value so update
			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
				Iterable<DataSnapshot> dsList= arg0.getChildren();
				Collections.sort(users);
				//				Collections.sort(horizontalUsers);
				int place = Collections.binarySearch(users, new User(arg0.getKey(),0,0, 5)); //Find the user username has to be unique uses the method compareTo in User
				//				int position = Collections.binarySearch(horizontalUsers, new User(arg0.getKey(),0,0,5));
				for (DataSnapshot dataSnapshot : dsList) {					 
					if (dataSnapshot.getKey().equals("xRel")){
						users.get(place).setxRel((double)dataSnapshot.getValue());
						//						 horizontalUsers.get(position).setxRel((double)dataSnapshot.getValue());
					}
					if (dataSnapshot.getKey().equals("yRel")){
						users.get(place).setyRel((double)dataSnapshot.getValue());
						//						 horizontalUsers.get(position).setyRel((double)dataSnapshot.getValue());
					}
					if (dataSnapshot.getKey().equals("RoundTripTo")){
						myFirebaseRef.child(arg0.getKey()).child("RoundTripBack").setValue((long)dataSnapshot.getValue()+1);
					}
					// reach the ping variable in firebase
					if (dataSnapshot.getKey().equals("ping")){
						users.get(place).setDelay((long)dataSnapshot.getValue());
						//						 horizontalUsers.get(position).setDelay((long)dataSnapshot.getValue());
					}
				}

				repaint();
			}

			//We got a new user
			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {
				if (arg0.hasChildren()){
					//System.out.println("ADD user with Key: "+arg1+ arg0.getKey());
					Random r = new Random();
					int x = r.nextInt(getSize().width);
					int y = r.nextInt(getSize().height); 
					listCount = users.size();
					System.out.println("number of players: " + listCount); //räknar antal spelar och skriver ut i konsollen. (börjar på 0)

					if (listCount ==0){
						User user = new User(arg0.getKey(), level.relX+11, level.relX+11, ships[0].player1lives); // create player 1
						if (!users.contains(user)){
							users.add(user);
							user.userHeight = 100;
							user.userWidth = 10;
							Color blue = Color.decode("#599bb9");
							user.setColor(blue);
							System.out.println("player 1 in");
							myFirebaseRef.child(arg0.getKey()).child("playercolor").setValue("#599bb9");
						}
					}

					if (listCount ==1){
						User user = new User(arg0.getKey(), level.screenWidth-11, level.screenWidth-10, ships[0].player2lives); // create player 2

						if (!users.contains(user)){
							users.add(user);
							user.userHeight = 100;
							user.userWidth = 10;
							Color red = Color.decode("#d35959");
							user.setColor(red);
							System.out.println("player 2 in");
							myFirebaseRef.child(arg0.getKey()).child("playercolor").setValue("#d35959");

						}
					}	

					if (listCount == 2){
						User user = new User(arg0.getKey(),100, level.relY+10 , ships[0].player3lives); // create player 3
						if(!users.contains(user)){
							users.add(user);
							user.userHeight = 100;
							user.userWidth = 10;
							Color green= Color.decode("#8cba66");
							user.setColor(green);
							myFirebaseRef.child(arg0.getKey()).child("playercolor").setValue("#8cba66");
						}
					}
					if (listCount == 3){
						User user = new User(arg0.getKey(),100,level.screenHeight-10, ships[0].player4lives); // create player 4
						if (!users.contains(user)){
							users.add(user);
							user.userHeight = 100;
							user.userWidth = 10;
							Color yellow= Color.decode("#e5d672");
							user.setColor(yellow);
							myFirebaseRef.child(arg0.getKey()).child("playercolor").setValue("#e5d672");
				
						}
					}
				}
			}

			@Override
			public void onCancelled(FirebaseError arg0) {

			}
		});

	}

	// här är vår metod för att starta timern
	public void initiateTimer(){
		//		System.out.println("About to start game countdown.");
		new TimerClass();
		//	    System.out.println("Countdown started, game will start in 5 seconds.");
		//		}
	}

	public void setPlayerBounds(User user){

		if(paddlePosY < 100){
			paddlePosY = 100;
		} else {
			paddlePosY = y - playerPingSize;
		}

		if(y > level.screenHeight){
			y = level.screenHeight;
		} else{
			y = (int)(user.getyRel()*getSize().height);
		}
	}


	public void setPlayerHeight(User user){
		playerDelay = user.getDelay();
		playerDelayint = (int) playerDelay;

		if(playerDelay > 0 && playerDelay < 600){
			playerPing = 0;
		}
		if(playerDelay > 600 && playerDelay < 900){
			playerPing = 150;
		}

		if(playerDelay > 900 && playerDelay < 1200){
			playerPing = 300;
		}
		if (playerDelay > 1200){
			playerPing = 450;
		} else{ 
			playerPing = (int) playerDelay;
		}
	}


	//Called when the screen needs a repaint.
	@Override
	public void paint(Graphics g) {

		//TOP LEFT CORNER
		polyTLC = new Polygon(level.TLCxpoints, level.TLCypoints, level.npoints);


		//TOP RIGHT CORNER
		polyTRC = new Polygon(level.TRCxpoints, level.TRCypoints, level.npoints);



		//BOT LEFT CORNER
		polyBLC = new Polygon(level.BLCxpoints, level.BLCypoints, level.npoints);


		//BOT RIGHT CORNER
		polyBRC = new Polygon(level.BRCxpoints, level.BRCypoints, level.npoints);

		//Ball polygon
		int ballNpoints = 4;
		int ballPolysizeX[] = {0 + ballXPos, 0 + ballXPos,10 + ballXPos, 10 + ballXPos};
		int ballPolysizeY[] = {0 + ballYPos, 10 + ballYPos,10 + ballYPos,0 + ballYPos};

		polyBally = new Polygon(ballPolysizeX,ballPolysizeY,ballNpoints);

		//super.paint(g);
		Graphics2D g2= (Graphics2D) g;
		g2.setFont(font);
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(0, 0, getSize().width, getSize().height);
		g2.setColor(Color.black);

//		ships[0].speed = 0;

		g2.finalize();
		if(start == false){
//			ships[0].speed=0;
			
			ballXPos = level.screenWidth/2;
			ballYPos = level.screenHeight/2;

			Color c = new Color(19,156,234);
			g2.setColor(c);
			//g2.fillRect(0,0,1000,700);
			Image imgStart = Toolkit.getDefaultToolkit().getImage("src/images/Startscreen.jpg");

			g2.drawImage(imgStart, 0, 0, this);
			Color gray = Color.decode("#2b2b2b");
			g.setColor(gray);
			g.drawString("The game will start when two players connects", level.screenWidth/2-120, level.screenHeight/2 + 200);
		}else{
			//	    	ballXPos = ball.getXPos();
			//			ballYPos = ball.getYPos();

			ballXPos = ships[0].getXPos();
			//System.out.println("X "+ ballXPos);
			ballYPos = ships[0].getYPos(); 
			//System.out.println(""+ballYPos);
			//ball
			//g2.fillOval(ballXPos, ballYPos, ball.getSize(), ball.getSize());



		}
		//Background


		Color c = new Color(19,156,234);
		g2.setColor(c);
		//	g2.drawRect (level.relX, level.relY, level.screenWidthForRect, level.screenHeightHeightForRect);	


		//CORNERS
		//TOP LEFT CORNER


		Area areaBall = new Area (polyBally);
		Area areaTLC = new Area (polyTLC);
		Area areaBLC = new Area (polyBLC);
		Area areaTRC = new Area (polyTRC);
		Area areaBRC = new Area (polyBRC);

		//	    g.drawPolygon(polyTLC);
		//	    g.fillPolygon (polyTLC);
		//	    //BOT LEFT CORNER 
		//	    g.drawPolygon(polyBLC);
		//	    g.fillPolygon (polyBLC);
		//	    //TOP RIGHT CORNER
		//	    g.drawPolygon(polyTRC);
		//	    g.fillPolygon (polyTRC);
		//	    //BOT RIGHT CORNER
		//	    g.drawPolygon(polyBRC);
		//	    g.fillPolygon (polyBRC);

		//g.setColor(Color.black);
		//g.fillRect(0,0,width,height);

		//corner collision
		if(areaTLC.intersects(ballXPos, ballYPos, ball.getSize(), ball.getSize())){
			System.out.println("CORNER");
			//ballLogic.cornerBounce();
			ships[0].topLeftCornerBounce();

		}else if(areaBLC.intersects(ballXPos, ballYPos, ball.getSize(), ball.getSize())){
			System.out.println("CORNER");
			//ballLogic.cornerBounce();
			ships[0].bottomLeftCornerBounce();
		}else if(areaTRC.intersects(ballXPos, ballYPos, ball.getSize(), ball.getSize())){
			System.out.println("CORNER");
			//ballLogic.cornerBounce();
			ships[0].topRightCornerBounce();
		}else if(areaBRC.intersects(ballXPos, ballYPos, ball.getSize(), ball.getSize())){
			System.out.println("CORNER");
			//ballLogic.cornerBounce();
			ships[0].bottomRightCornerBounce();
		}
		

		try {
			// thread to sleep for 1000 milliseconds
			if(users.size()==1){
				Thread.sleep(3);
			}else if(users.size()>1){
				Thread.sleep(6);

			}
		} catch (Exception e) {
			System.out.println(e);
		}


		super.repaint();

		Color gray = Color.decode("#2b2b2b");
		g.setColor(gray);
		
		if(users.size() >= 1){
			String player1name = users.get(0).getId();
			g.drawString(player1name + " connected", 425, 100);
		}

		if(users.size() == 2){
			String player2name = users.get(1).getId();
			g.drawString(player2name + " connected", 425, 150);
			initiateTimer(); // här försöker vi starta timern när 2 spelare har anslutit till spelet
		}

		if(users.size() == 3){
			String player3name =users.get(2).getId();
			g.drawString(player3name + " connected", 425, 200);
		}

		if(users.size() == 4){
			String player4name = users.get(3).getId();
			g.drawString(player4name + " connected", 425, 250);
		}


		super.repaint();

		if(users.size()>=2){
			//Background
			g2.drawImage(img1, 0, 0, this); 

			if(start == true){ // när timern kört klart och gjort om start till true, ska skärmen ändras till spelplanen och spelet ska laddas

				ships[0].move();
				//ships[0].paint(g2);
				Image boll = Toolkit.getDefaultToolkit().getImage("src/images/boll.png");
				g2.drawImage(boll, ballXPos, ballYPos, ball.getSize(),ball.getSize(), this);
				
//				ships[0].speed = 2;
				
				ballXPos = ships[0].xPos;
				ballYPos = ships[0].yPos;

				//ball
				//g2.fillOval(ballXPos, ballYPos, ball.getSize(), ball.getSize());
//				boll = Toolkit.getDefaultToolkit().getImage("src/images/boll.png");
//				g2.drawImage(boll, ballXPos, ballYPos, ball.getSize(),ball.getSize(), this);

				//	    	System.out.println("Game started");

				//Test
				for (User user : users) {
					
					if(users.size()>=2){  // defines how many players that needs to be in the game for it to start			

						paddleTop = y - playerPingSize;
						paddleBottom = y;

						paddlePosY = y - playerPingSize;


						y = (int)(user.getyRel()*getSize().height);
					

						// sets appropriate height to players based on ping
						setPlayerHeight(user);
						setPlayerBounds(user);

						//Draws paddle from center of finger placement on android
						playerPingSize = user.userHeight + playerPing/4;


						// defines how many players that needs to be in the game for it to start
						if(users.size()>=1){ 
							start = true;
							
							//Check if too many players
							if(users.size()>4){
								for(int i = users.size(); i>=4; i--){
								users.remove(i);
								}
							}
							
							g2.setColor(user.getColor());

							//draw out players
							Image player1 = Toolkit.getDefaultToolkit().getImage("src/images/paddle_left.png");
							Image player2 = Toolkit.getDefaultToolkit().getImage("src/images/paddle_right.png");
							Image player3 = Toolkit.getDefaultToolkit().getImage("src/images/paddle_top.png");
							Image player4 = Toolkit.getDefaultToolkit().getImage("src/images/paddle_bottom.png");

						//	g2.drawImage(player3, level.screenWidth/2, level.relY+11,          playerPingSize, user.userWidth,this);
						//	g2.drawImage(player4, level.screenWidth/2,  level.screenHeight-11, playerPingSize, user.userWidth, this);
							
							
							if(users.indexOf(user) == 0){
								//	g2.fillRect(level.relX+1, y - (playerPingSize/2), user.userWidth, playerPingSize);
								ships[0].paddleOneHit(level.relX+1, y - (playerPingSize/2), user.userWidth, playerPingSize);
								
								// draw out player 1 info
								Color blue = users.get(0).getColor();
								g.setColor(blue);
								//This prints out the ping to drawpanel
								String player1Delay = String.valueOf(users.get(0).getDelay());
								g.drawString("PING = " + player1Delay, 20, 610);
								String player1name = users.get(0).getId();
								//System.out.println(player1name);
								g.drawString(player1name, 20, 550);
								String livesLeftPlayerOne = String.valueOf(ships[0].player1lives);
								g.drawString(livesLeftPlayerOne + " Lives left ", 20, 580); // this prints out how many lives player one has left
							
								
								g2.drawImage(player1, level.relX+1, y - (playerPingSize/2), user.userWidth, playerPingSize, this);

							}   else if (users.indexOf(user)==1 ){
								
								// draw out player 2 info
								Color red = users.get(1).getColor();
								g.setColor(red);
								String player2Delay = String.valueOf(users.get(1).getDelay());
								g.drawString("PING = " + player2Delay, 807, 130);
								String player2name = users.get(1).getId();
								//System.out.println(player2name);
								g.drawString(player2name, 807, 70);
								String livesLeftPlayerTwo = String.valueOf(ships[0].player2lives);
								g.drawString(livesLeftPlayerTwo + " Lives left ", 807, 100); // this prints out how many lives player two has left
							
								
								//		g2.fillRect(level.screenHeight-11, y - (playerPingSize/2), user.userWidth, playerPingSize);
								ships[0].paddleTwoHit(level.screenWidth-11, y - (playerPingSize/2), user.userWidth, playerPingSize);
								g2.drawImage(player2, level.screenWidth-11, y - (playerPingSize/2), user.userWidth, playerPingSize, this);
								
							}

//							else if (users.indexOf(user) == 2){
//								//g2.fillRect(1000, user.getyPos(), user.userHeight, user.userWidth);
//								ships[0].paddleThreeHit(level.relY-11, level.screenWidth-11 ,playerPingSize, user.userWidth );
//								
//								// draw out player 3 info
//								Color green = users.get(2).getColor();
//								g.setColor(green);
//								String player3Delay = String.valueOf(users.get(2).getDelay());
//								g.drawString("PING = " + player3Delay, 20, 130);
//								String player3name = users.get(2).getId();
//								//System.out.println(player3name);
//								g.drawString(player3name, 20, 70);
//								String livesLeftPlayerThree = String.valueOf(ships[0].player3lives);
//								g.drawString(livesLeftPlayerThree + " Lives left ", 20, 100); // this prints out how many lives player three has left
//
//								
//								g2.drawImage(player3, y - (playerPingSize/2) , level.relY+11 ,playerPingSize, user.userWidth,this);
//								}
//							else if (users.indexOf(user) == 3){
//								//g2.fillRect(1000, user.getyPos(), user.userHeight, user.userWidth);
//
//								// draw out player 4 info
//								Color yellow = users.get(3).getColor();
//								g.setColor(yellow);
//								String player4Delay = String.valueOf(users.get(3).getDelay());
//								g.drawString("PING = "+ player4Delay, 807,610);
//								String player4name = users.get(3).getId();
//								//System.out.println(player4name);
//								g.drawString(player4name, 807, 550);
//								String livesLeftPlayerFour = String.valueOf(ships[0].player4lives);

//								g.drawString(livesLeftPlayerFour,  807,  580); // this prints out how many lives player four has left								
								
								
								// these if statements decide which player wins
							if (ships[0].player1Win() == true){
								String player1Wins = users.get(0).getId() + " wins!";
								g.drawString(player1Wins, 450, 200);
								ships[0].speed = 0;
								ships[0].position.x = 450;
								ships[0].position.y = 300;
								new RestartTimer();
							}
						    if (ships[0].player2Win() == true){
								String player2Wins = users.get(1).getId() + " wins!";
								g.drawString(player2Wins, 450, 200);
								ships[0].speed = 0;
								ships[0].position.x = 450;
								ships[0].position.y = 300;
								new RestartTimer();
							}
							if (ships[0].player3Win() == true){
								String player3Wins = users.get(2).getId() + " wins!";
								g.drawString(player3Wins, 450, 200);
			 					ships[0].speed = 0;
						    	ships[0].position.x = 450;
						    	ships[0].position.y = 300;	
						    	new RestartTimer();
							}
							if (ships[0].player4Win() == true){
								String player4Wins = users.get(3).getId() + " wins!";
								g.drawString(player4Wins, 450, 200);
								ships[0].speed = 0;
						    	ships[0].position.x = 450;
						    	ships[0].position.y = 300;	
						    	new RestartTimer();
							}						

//								g.drawString(livesLeftPlayerFour,  807,  580); // this prints out how many lives player four has left
//
//								
//								ships[0].paddleFourHit(y - (playerPingSize/2),level.relX+10, playerPingSize,user.userWidth);
//
//								g2.drawImage(player4, y - (playerPingSize/2),  level.screenHeight-11, playerPingSize, user.userWidth, this);
//								}

						}		
					}
				}
			}
		}
	}

			public class TimerClass {
				Toolkit toolkit;

				Timer timer = new Timer();

				public TimerClass() {
					toolkit = Toolkit.getDefaultToolkit();
					timer = new Timer();
					timer.schedule(new StartGameTimer(), 0, 1 * 1000);

				}

				class StartGameTimer extends TimerTask {
					int count = 5;
					public void run() {
						if (count > 0){
							//toolkit.beep();
							//	System.out.println(count + " seconds left");
							count--;
						} else {
							//toolkit.beep();
							//		System.out.println(count + " seconds left, Game is starting!");
							start = true; // här bestämmer vi att vår start boolean ska bli true när timern körts klart
							timer.cancel(); //Stops the AWT thread (and everything else)
						}
					}
				
				}
			}
			public class RestartTimer {
				Toolkit toolkit;
				Timer timer = new Timer();
				
				public RestartTimer(){
					toolkit = Toolkit.getDefaultToolkit();
					timer = new Timer();
					timer.schedule(new GameTimer(), 0, 1*1000);
				}
				
				class GameTimer extends TimerTask{
					int count = 5;
					public void run (){
						if (count > 0){
						//	toolkit.beep();
							count--;
						} else {
						//	toolkit.beep();
//							System.out.println("Game is restarting");
							ships[0].player1lives = 5;
							ships[0].player2lives = 5;
							ships[0].player3lives = 5;
							ships[0].player4lives = 5;
							ships[0].speed = 2;
							timer.cancel();
						}
					}
				}
			}			


		}
	
	



