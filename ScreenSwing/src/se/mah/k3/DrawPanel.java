
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

import se.mah.k3.DrawPanel.TimerClass.StartGameTimer;

import java.io.File;
import java.io.FileInputStream;

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
	private boolean startTimer = false;
	private boolean restartTimer = false;

	int ballXPos = level.screenWidth;
	int ballYPos = level.screenHeight;
	int listCount;

	int paddlePosY;
	int paddlePosX;
	int paddleBottom;
	int paddleTop;
	int paddleLeft;
	int paddleRight;
	int y;
	int x;
	//player ping
	long playerDelay;
	int playerPing;
	int playerDelayint;
	int playerPingSize;
	
	public Font ExoExtraLightSize;
	public Font ExoBoldSize;

	public Polygon polyTRC;
	public Polygon polyTLC;
	public Polygon polyBRC;
	public Polygon polyBLC;
	public Polygon polyBally;

	int startValue;
	boolean startFinished = false;
	boolean timerStarted = false;
		
	//background image
	
	Image img1 = Toolkit.getDefaultToolkit().getImage("src/images/bakgrundur.jpg");

	//A vector is like an ArrayList a little bit slower but Thread-safe. This means that it can handle concurrent changes. 
	private Vector<User> users= new Vector<User>();
	//Font font = new Font("Verdana", Font.PLAIN, 20); 

	Color blue = Color.decode("#599bb9");
	Color green= Color.decode("#8cba66");
	Color red = Color.decode("#d35959");
	Color yellow= Color.decode("#e5d672");
	Color gray = Color.decode("#2b2b2b");

	//draw out players
	Image player1 = Toolkit.getDefaultToolkit().getImage("src/images/paddle_left.png");
	Image player2 = Toolkit.getDefaultToolkit().getImage("src/images/paddle_right.png");
	Image player3 = Toolkit.getDefaultToolkit().getImage("src/images/paddle_top.png");
	Image player4 = Toolkit.getDefaultToolkit().getImage("src/images/paddle_bottom.png");

	Image LeftDead = Toolkit.getDefaultToolkit().getImage("src/image/deadver.jpg");
	Image RightDead = Toolkit.getDefaultToolkit().getImage("src/image/deadver.jpg");		
	Image TopDead = Toolkit.getDefaultToolkit().getImage("src/image/deadhor.jpg");
	Image BottomDead = Toolkit.getDefaultToolkit().getImage("src/image/deadhor.jpg");
	
	
	public DrawPanel() {

		try {
			loadFont();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
				int place = Collections.binarySearch(users, new User(arg0.getKey(),0,0,0,0)); // Den sista nollan här är den jag vill att listan ska sorteras efter. Den bestäms senare när users skapas i onChildAdded
				
				if((place <= users.size()) && (users.size()>-1)){
					
				for (DataSnapshot dataSnapshot : dsList) {					 
					if (dataSnapshot.getKey().equals("xRel")){
						try {
							users.get(place).setxRel((double)dataSnapshot.getValue());
						} catch (Exception e) {
							System.out.println("Error reading: " + place);
							e.printStackTrace();
						}		
					}
					if (dataSnapshot.getKey().equals("yRel")){
						try {
							users.get(place).setyRel((double)dataSnapshot.getValue());
						} catch (Exception e) {
							System.out.println("Error reading: " + place);
							e.printStackTrace();
						}
					}
					if (dataSnapshot.getKey().equals("RoundTripTo")){
						myFirebaseRef.child(arg0.getKey()).child("RoundTripBack").setValue((long)dataSnapshot.getValue()+1);
					}
					if (dataSnapshot.getKey().equals("ping")){
						try {
							users.get(place).setDelay((long)dataSnapshot.getValue());
						} catch (Exception e) {
							System.out.println("Error reading: " + place);
							e.printStackTrace();	
						}
					}					
				}
				}

				repaint();
			}

			//We got a new user
			@Override
			public void onChildAdded(DataSnapshot arg0, String arg1) {
				listCount = users.size();
				if (arg0.hasChildren()){
					//System.out.println("ADD user with Key: "+arg1+ arg0.getKey());
					Random r = new Random();
					int x = r.nextInt(getSize().width);
					int y = r.nextInt(getSize().height); 
					listCount = users.size();
					//System.out.println("number of players: " + users.size()); //räknar antal spelar och skriver ut i konsollen. (börjar på 0)

					if (listCount ==0){
						User user = new User(arg0.getKey(), level.relX+11, level.relX+11, ships[0].player1lives, 1); // create player 1
						if (!users.contains(user)){
							users.add(user);
							user.userHeight = 100;
							user.userWidth = 10;
							ships[0].player1lives = 5;
							
							//user.setColor(blue);
							//System.out.println("player 1 in");
							//System.out.println("Player " + user.getId() + " has position " + user.getPosition());
							myFirebaseRef.child(arg0.getKey()).child("position").setValue((int)user.getPosition());
						}
					}

					if (listCount ==1){
						User user = new User(arg0.getKey(), level.screenWidth-11, level.screenWidth-10, ships[0].player2lives, 2); // create player 2

						if (!users.contains(user)){
							users.add(user);
							user.userHeight = 100;
							user.userWidth = 10;

							ships[0].player2lives = 5;
							
//							Color green = Color.decode("#8cba66");
//							user.setColor(green);
							//System.out.println("player 2 in");
							//System.out.println("Player " + user.getId() + " has position " + user.getPosition());
							myFirebaseRef.child(arg0.getKey()).child("position").setValue((int)user.getPosition());
						}
					}	

					if (listCount == 2){
						User user = new User(arg0.getKey(), level.relX+11, level.relY+11 , ships[0].player3lives, 3); // create player 3
						if(!users.contains(user)){
							users.add(user);
							user.userHeight = 100;
							user.userWidth = 10;
							
							ships[0].player3lives = 5;
							
//							Color red = Color.decode("#d35959");
//							user.setColor(red);
							//System.out.println("player 3 in");
							//System.out.println("Player " + user.getId() + " has position " + user.getPosition());
							myFirebaseRef.child(arg0.getKey()).child("position").setValue((int)user.getPosition());
						}
					}
					if (listCount == 3){
						User user = new User(arg0.getKey(),level.relX-11,level.screenHeight-11, ships[0].player4lives, 4); // create player 4
						if (!users.contains(user)){
							users.add(user);
							user.userHeight = 100;
							user.userWidth = 10;
							
							ships[0].player4lives = 5;
							
							//user.setColor(yellow);
							//myFirebaseRef.child(arg0.getKey()).child("playercolor").setValue("#e5d672");
							//System.out.println("player 4 in");
							//System.out.println("Player " + user.getId() + " has position " + user.getPosition());
							myFirebaseRef.child(arg0.getKey()).child("position").setValue((int)user.getPosition());
						}
					}

					System.out.println("Users in the game : " + users.size());

//					//Check if too many players
//					if(listCount >3){
//						for(int i = users.size(); i>4; i--){
//						users.remove(i);
//						}
//					}

				}
			}

			@Override
			public void onCancelled(FirebaseError arg0) {
			}
		});

	}

	// här är vår metod för att starta timern
	public void initiateTimer(){
		new TimerClass();
		startTimer = true;
	}
	
	public void loadFont() throws Exception{
			File f = new File("lib/ExoExtraLight.ttf");
			FileInputStream in = new FileInputStream(f);
			Font ExoExtraLight = Font.createFont(Font.TRUETYPE_FONT, in);
			ExoExtraLightSize = ExoExtraLight.deriveFont(25f);
			
			File f2 = new File("lib/ExoBold.ttf");
			FileInputStream in2 = new FileInputStream(f2);
			Font ExoBold = Font.createFont(Font.TRUETYPE_FONT, in2);
			ExoBoldSize = ExoBold.deriveFont(25f);
		}

	
	
//	// Sets the boundaries for the paddles
//	public void setPlayerBoundsY(User user){
//
//		if(y - playerPingSize/2 < 100){
//			paddlePosY = 100;
//			System.out.println("playerboundstop");
//		} else {
//			paddlePosY = y - playerPingSize;
//			System.out.println("ok to move");
//		}
//
//		if(y - playerPingSize/2 > level.screenHeight){
//			y = level.screenHeight;
//			System.out.println("playerboundsbottom");
//		} else{
//			y = (int)(user.getyRel()*getSize().height);
//			System.out.println("ok to move");
//		}
//	}
//	



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
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(0, 0, getSize().width, getSize().height);
		g2.setColor(Color.black);
		

		g2.setFont(ExoBoldSize);
		g.setFont(ExoBoldSize);

//		ships[0].speed = 0;

		g2.finalize();
		if(start == false){
//			ships[0].speed=0;
			
			ballXPos = level.screenWidth/2;
			ballYPos = level.screenHeight/2;
			
			Color c = new Color(19,156,234);
			g2.setColor(c);
			//g2.fillRect(0,0,1000,700);
			Image imgStart = Toolkit.getDefaultToolkit().getImage("src/images/Startscreen2.jpg");

			g2.drawImage(imgStart, 0, 0, this);
			Color gray = Color.decode("#2b2b2b");
			g.setColor(gray);

			g.drawString("The game will start when two or more players connect", level.screenWidth/2-170, level.screenHeight/2);

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
				Thread.sleep(4);

			}
		} catch (Exception e) {
			System.out.println(e);
		}


		super.repaint();

		g.setColor(gray);
		
		if(users.size() >= 1){
			g.setColor(blue);
			String player1name = users.get(0).getId();
			g.drawString(player1name + " connected", 425, 500);
		}

		if(users.size() >= 2){
			g.setColor(green);
			String player2name = users.get(1).getId();
			g.drawString(player2name + " connected", 425, 550);
		}

		if(users.size() >= 3){
			g.setColor(red);
			String player3name = users.get(2).getId();
			g.drawString(player3name + " connected", 425, 600);
		}

		if(users.size() >= 4){
			g.setColor(yellow);
			String player4name = users.get(3).getId();
			g.drawString(player4name + " connected", 425, 650);
		}


		super.repaint();

		if(users.size()>=2){
			//Background
			g2.drawImage(img1, 0, 0, this); 

	if(startTimer == false && start == false){
			initiateTimer(); // här försöker vi starta timern när 2 spelare har anslutit till spelet
			//g2.drawString("Ready?", level.screenWidth/2,level.screenHeight/2);
	}		
			
			
			if(start == true){ // när timern kört klart och gjort om start till true, ska skärmen ändras till spelplanen och spelet ska laddas
				restartTimer = false;
				ships[0].move();
				//ships[0].paint(g2);
				Image boll = Toolkit.getDefaultToolkit().getImage("src/images/boll.png");
				g2.drawImage(boll, ballXPos, ballYPos, ball.getSize(),ball.getSize(), this);
				
				
//				ships[0].speed = 2;
				
				ballXPos = ships[0].xPos;
				ballYPos = ships[0].yPos;
			} else {
				g2.drawString(""+ startValue , level.screenWidth/2 + 100,level.screenHeight/2);
			}
			
				//ball
				//g2.fillOval(ballXPos, ballYPos, ball.getSize(), ball.getSize());
//				boll = Toolkit.getDefaultToolkit().getImage("src/images/boll.png");
//				g2.drawImage(boll, ballXPos, ballYPos, ball.getSize(),ball.getSize(), this);

				//	    	System.out.println("Game started");

				//Test
				for (User user : users) {
					
					if(users.size()>=2){  // defines how many players that needs to be in the game for it to start			

						//System.out.println(y - (playerPingSize/2));
						paddleTop = y - playerPingSize;
						paddleBottom = y;
						
						paddlePosY = y - playerPingSize;
					

						y = (int)(user.getyRel()*getSize().height);
						x = (int)(user.getxRel()*getSize().width);

						// sets appropriate height to players based on ping
						setPlayerHeight(user);
//						setPlayerBoundsY(user);
						
						
						
						//Draws paddle from center of finger placement on android
						playerPingSize = user.userHeight + playerPing/4;


						// defines how many players that needs to be in the game for it to start
						if(users.size()>=2){ 
							//start = true;
							//System.out.println(y);
							//Check if too many players
							if(users.size()>4){
								for(int i = users.size(); i>4; i--){
								users.remove(i);
								}
							}
							
							g2.setColor(user.getColor());

//							//draw out players
//							Image player1 = Toolkit.getDefaultToolkit().getImage("src/images/paddle_left.png");
//							Image player2 = Toolkit.getDefaultToolkit().getImage("src/images/paddle_right.png");
//							Image player3 = Toolkit.getDefaultToolkit().getImage("src/images/paddle_top.png");
//							Image player4 = Toolkit.getDefaultToolkit().getImage("src/images/paddle_bottom.png");

						//	g2.drawImage(player3, level.screenWidth/2, level.relY+11,          playerPingSize, user.userWidth,this);
						//	g2.drawImage(player4, level.screenWidth/2,  level.screenHeight-11, playerPingSize, user.userWidth, this);
							
							//PLAYER 1 STUFF
							if(user.getPosition() == 1){
								//Set player 1 color
								Color blue = users.get(0).getColor();
								g.setColor(blue);
								//	g2.fillRect(level.relX+1, y - (playerPingSize/2), user.userWidth, playerPingSize);

								//Draw out paddle
								if (ships[0].playerOneOut() == false){
									ships[0].paddleOneHit(level.relX+1, 240+ y - (playerPingSize/2), user.userWidth, playerPingSize);
									g2.drawImage(player1, level.relX+1, 240+ y - (playerPingSize/2), user.userWidth, playerPingSize, this);
								} else {
									ships[0].paddleOneHit(level.relX+1, level.relY+70, 10, level.relY+530);
									g2.drawImage(LeftDead, level.relX+1, level.relY+70, 10, level.relY+530, this);
								}

								
//								System.out.println("index of player one position = " + users.indexOf(user));
								// draw out player 1 info
					
								//This prints out the ping to drawpanel
								String player1Delay = String.valueOf(users.get(0).getDelay());
								g.drawString("Ping: " + player1Delay, 20, 475);
								String player1name = users.get(0).getId();
								g.drawString(player1name, 20, 445);
								//String livesLeftPlayerOne = String.valueOf(ships[0].player1lives);

								if(ships[0].player1lives == 5){
									Image heart = Toolkit.getDefaultToolkit().getImage("src/images/heart.png");
									g2.drawImage(heart, 166, 400, 20,18, this);
									g2.drawImage(heart, 166, 425, 20,18, this);
									g2.drawImage(heart, 166, 450, 20,18, this);
									g2.drawImage(heart, 166, 475, 20,18, this);
									g2.drawImage(heart, 166, 500, 20,18, this);
								}
								if(ships[0].player1lives == 4){
									Image heart = Toolkit.getDefaultToolkit().getImage("src/images/heart.png");
									g2.drawImage(heart, 166, 400, 20,18, this);
									g2.drawImage(heart, 166, 425, 20,18, this);
									g2.drawImage(heart, 166, 450, 20,18, this);
									g2.drawImage(heart, 166, 475, 20,18, this);
								}
								if(ships[0].player1lives == 3){
									Image heart = Toolkit.getDefaultToolkit().getImage("src/images/heart.png");
									g2.drawImage(heart, 166, 400, 20,18, this);
									g2.drawImage(heart, 166, 425, 20,18, this);
									g2.drawImage(heart, 166, 450, 20,18, this);
								}
								if(ships[0].player1lives == 2){
									Image heart = Toolkit.getDefaultToolkit().getImage("src/images/heart.png");
									g2.drawImage(heart, 166, 400, 20,18, this);
									g2.drawImage(heart, 166, 425, 20,18, this);
								}
								if(ships[0].player1lives == 1){
									Image heart = Toolkit.getDefaultToolkit().getImage("src/images/heart.png");
									g2.drawImage(heart, 166, 400, 20,18, this);
								}
								if(ships[0].player1lives == 0){
									Image skull = Toolkit.getDefaultToolkit().getImage("src/images/skull.png");
									g2.drawImage(skull, 166, 450, 18,24, this);
								}
								
								//g.drawString(livesLeftPlayerOne + " Lives left ", 20, 450); // this prints out how many lives player one has left
								ships[0].paddleOneHit(level.relX+1, 250 + y - (playerPingSize/2), user.userWidth, playerPingSize);
							}   else if (user.getPosition() == 2 ){
								
								//PLAYER 2 STUFF
								
								Color green = users.get(1).getColor();
								g.setColor(green);
								
								if (ships[0].playerTwoOut() == false){
									ships[0].paddleTwoHit(level.screenWidth-11, 250 + y - (playerPingSize/2), user.userWidth, playerPingSize);
									g2.drawImage(player2, level.screenWidth-11, 250 + y - (playerPingSize/2), user.userWidth, playerPingSize, this);
								} else {
									ships[0].paddleTwoHit(level.screenWidth-11,level.relY+70, 10, level.relY+530);
									g2.drawImage(RightDead,level.screenWidth-11,level.relY+70, 10, level.relY+530, this);
								}

								// draw out player 2 info
								
								//System.out.println("index of player TWO position = " + users.indexOf(user));

								String player2Delay = String.valueOf(users.get(1).getDelay());
								g.drawString("Ping: " + player2Delay, 845, 475);
								String player2name = users.get(1).getId();
								//System.out.println(player2name);
								g.drawString(player2name, 845, 445);
								//String livesLeftPlayerTwo = String.valueOf(ships[0].player2lives);
								
								if(ships[0].player2lives == 5){
									Image heart = Toolkit.getDefaultToolkit().getImage("src/images/heart.png");
									g2.drawImage(heart, 815, 400, 20,18, this);
									g2.drawImage(heart, 815, 425, 20,18, this);
									g2.drawImage(heart, 815, 450, 20,18, this);
									g2.drawImage(heart, 815, 475, 20,18, this);
									g2.drawImage(heart, 815, 500, 20,18, this);
								}
								if(ships[0].player2lives == 4){
									Image heart = Toolkit.getDefaultToolkit().getImage("src/images/heart.png");
									g2.drawImage(heart, 815, 400, 20,18, this);
									g2.drawImage(heart, 815, 425, 20,18, this);
									g2.drawImage(heart, 815, 450, 20,18, this);
									g2.drawImage(heart, 815, 475, 20,18, this);
								}
								if(ships[0].player2lives == 3){
									Image heart = Toolkit.getDefaultToolkit().getImage("src/images/heart.png");
									g2.drawImage(heart, 815, 400, 20,18, this);
									g2.drawImage(heart, 815, 425, 20,18, this);
									g2.drawImage(heart, 815, 450, 20,18, this);
								}
								if(ships[0].player2lives == 2){
									Image heart = Toolkit.getDefaultToolkit().getImage("src/images/heart.png");
									g2.drawImage(heart, 815, 400, 20,18, this);
									g2.drawImage(heart, 815, 425, 20,18, this);
								}
								if(ships[0].player2lives == 1){
									Image heart = Toolkit.getDefaultToolkit().getImage("src/images/heart.png");
									g2.drawImage(heart, 815, 400, 20,18, this);
								}
								if(ships[0].player2lives == 0){
									Image skull = Toolkit.getDefaultToolkit().getImage("src/images/skull.png");
									g2.drawImage(skull, 815, 450, 18,24, this);
								}
								//g.drawString(livesLeftPlayerTwo + " Lives left ", 820, 450); // this prints out how many lives player two has left
							
							}
							
							//PLAYER 3 STUFF
							if (users.size() < 3 ){
								ships[0].paddleThreeHit (level.relX+70, level.relY+1 , level.relX+530, 10);
								//System.out.println("3 in");
							}
							if (user.getPosition() == 3){								
								Color red = users.get(2).getColor();
								g.setColor(red);
								//g2.fillRect(1000, user.getyPos(), user.userHeight, user.userWidth);
								
								if(ships[0].playerThreeOut() == false){
									ships[0].paddleThreeHit (250 + y-(playerPingSize/2), level.relY+1 , playerPingSize ,user.userWidth);
									g2.drawImage(player3, 250 + y-(playerPingSize/2), level.relY+1 ,playerPingSize, user.userWidth ,this);
								} else {
									ships[0].paddleThreeHit (level.relX+70, level.relY+1 , level.relX+530, 10);
									g2.drawImage(TopDead, level.relX+70, level.relY+1 , level.relX+530 , 10,this);
								}
								
								
								//System.out.println("index of player THRREE position = " + users.indexOf(user));
								// draw out player 3 info
								
								String player3Delay = String.valueOf(users.get(2).getDelay());
								g.drawString("Ping: " + player3Delay, 450, 105);
								String player3name = users.get(2).getId();
								//System.out.println(player3name);
								g.drawString(player3name, 450, 75);
								//String livesLeftPlayerThree = String.valueOf(ships[0].player3lives);
								
								if(ships[0].player3lives == 5){
									Image heart = Toolkit.getDefaultToolkit().getImage("src/images/heart.png");
									g2.drawImage(heart, 450, 119, 20,18, this);
									g2.drawImage(heart, 475, 119, 20,18, this);
									g2.drawImage(heart, 500, 119, 20,18, this);
									g2.drawImage(heart, 525, 119, 20,18, this);
									g2.drawImage(heart, 550, 119, 20,18, this);
								}
								if(ships[0].player3lives == 4){
									Image heart = Toolkit.getDefaultToolkit().getImage("src/images/heart.png");
									g2.drawImage(heart, 450, 119, 20,18, this);
									g2.drawImage(heart, 475, 119, 20,18, this);
									g2.drawImage(heart, 500, 119, 20,18, this);
									g2.drawImage(heart, 525, 119, 20,18, this);
								}
								if(ships[0].player3lives == 3){
									Image heart = Toolkit.getDefaultToolkit().getImage("src/images/heart.png");
									g2.drawImage(heart, 450, 119, 20,18, this);
									g2.drawImage(heart, 475, 119, 20,18, this);
									g2.drawImage(heart, 500, 119, 20,18, this);
								}
								if(ships[0].player3lives == 2){
									Image heart = Toolkit.getDefaultToolkit().getImage("src/images/heart.png");
									g2.drawImage(heart, 450, 119, 20,18, this);
									g2.drawImage(heart, 475, 119, 20,18, this);
								}
								if(ships[0].player3lives == 1){
									Image heart = Toolkit.getDefaultToolkit().getImage("src/images/heart.png");
									g2.drawImage(heart, 450, 119, 20,18, this);
								}
								if(ships[0].player3lives == 0){
									Image skull = Toolkit.getDefaultToolkit().getImage("src/images/skull.png");
									g2.drawImage(skull, 500, 119, 18,24, this);
								}
								//g.drawString(livesLeftPlayerThree + " Lives left ", 450, 80); // this prints out how many lives player three has left
						
								}
							
							//PLAYER 4 STUFF
							if (users.size() < 4){
								ships[0].paddleFourHit(level.relX+70,level.screenHeight-11 , level.relX+530, 10);
								//System.out.println("4 in");
							}
							
							if (user.getPosition() == 4){
								Color yellow = users.get(3).getColor();
								g.setColor(yellow);
								//g2.fillRect(1000, user.getyPos(), user.userHeight, user.userWidth);
								
								if(ships[0].playerFourOut() == false){
									ships[0].paddleFourHit (250 + y-(playerPingSize/2),level.screenHeight-15 , playerPingSize ,user.userWidth);
									g2.drawImage(player4, 250 + y-(playerPingSize/2), level.screenHeight-15 , playerPingSize, user.userWidth ,this);
								} else {
									ships[0].paddleFourHit(level.relX+70,level.screenHeight-11 , level.relX+530, 10);
									g2.drawImage(BottomDead,level.relX+70,level.screenHeight-11 , level.relX+530, 10,this);
								}
								
							//	System.out.println("index of player FOUR position = " + users.indexOf(user));
								// draw out player 4 info
			
								String player4Delay = String.valueOf(users.get(3).getDelay());
								g.drawString("PING = "+ player4Delay, 450,845);
								String player4name = users.get(3).getId();
								//System.out.println(player4name);
								g.drawString(player4name, 450, 815);
								//String livesLeftPlayerFour = String.valueOf(ships[0].player4lives);
								
								if(ships[0].player4lives == 5){
									Image heart = Toolkit.getDefaultToolkit().getImage("src/images/heart.png");
									g2.drawImage(heart, 450, 764, 20,18, this);
									g2.drawImage(heart, 475, 764, 20,18, this);
									g2.drawImage(heart, 500, 764, 20,18, this);
									g2.drawImage(heart, 525, 764, 20,18, this);
									g2.drawImage(heart, 550, 764, 20,18, this);
								}
								if(ships[0].player4lives == 4){
									Image heart = Toolkit.getDefaultToolkit().getImage("src/images/heart.png");
									g2.drawImage(heart, 450, 764, 20,18, this);
									g2.drawImage(heart, 475, 764, 20,18, this);
									g2.drawImage(heart, 500, 764, 20,18, this);
									g2.drawImage(heart, 525, 764, 20,18, this);
								}
								if(ships[0].player4lives == 3){
									Image heart = Toolkit.getDefaultToolkit().getImage("src/images/heart.png");
									g2.drawImage(heart, 450, 764, 20,18, this);
									g2.drawImage(heart, 475, 764, 20,18, this);
									g2.drawImage(heart, 500, 764, 20,18, this);
								}
								if(ships[0].player4lives == 2){
									Image heart = Toolkit.getDefaultToolkit().getImage("src/images/heart.png");
									g2.drawImage(heart, 450, 764, 20,18, this);
									g2.drawImage(heart, 475, 764, 20,18, this);
								}
								if(ships[0].player4lives == 1){
									Image heart = Toolkit.getDefaultToolkit().getImage("src/images/heart.png");
									g2.drawImage(heart, 450, 764, 20,18, this);
								}
								if(ships[0].player4lives == 0){
									Image skull = Toolkit.getDefaultToolkit().getImage("src/images/skull.png");
									g2.drawImage(skull, 500, 764, 18,24, this);
								}
							
								//g.drawString(livesLeftPlayerFour + " Lives left ",  450,  840); // this prints out how many lives player four has left								
							}
							
							//System.out.println("size on list = " + users.size());
							
							Image imgOutside = Toolkit.getDefaultToolkit().getImage("src/images/outside.png");
							g2.drawImage(imgOutside, 0, 0, this);


								
//								
								}
								
								// these if statements decide which player wins
							if (ships[0].player1Win() == true){
								String player1Wins = users.get(0).getId() + " wins!";
								g.drawString(player1Wins, 450, 200);
								ships[0].speed = 0;
								ships[0].position.x = 450;
								ships[0].position.y = 300;
								if(restartTimer == false){
								new RestartTimer();
								restartTimer = true;
								}
							}
						    if (ships[0].player2Win() == true){
								String player2Wins = users.get(1).getId() + " wins!";
								g.drawString(player2Wins, 450, 200);
								ships[0].speed = 0;
								ships[0].position.x = 450;
								ships[0].position.y = 300;
								if(restartTimer == false){
								new RestartTimer();
								restartTimer = true;
								}
								}
							if (ships[0].player3Win() == true){
								String player3Wins = users.get(2).getId() + " wins!";
								g.drawString(player3Wins, 450, 200);
			 					ships[0].speed = 0;
						    	ships[0].position.x = 450;
						    	ships[0].position.y = 300;	
						    	if(restartTimer == false){
									new RestartTimer();
									restartTimer = true;
									}
						    }
							if (ships[0].player4Win() == true){
								String player4Wins = users.get(3).getId() + " wins!";
								g.drawString(player4Wins, 450, 200);
								ships[0].speed = 0;
						    	ships[0].position.x = 450;
						    	ships[0].position.y = 300;
						    	if(restartTimer == false){
									new RestartTimer();
									restartTimer = true;
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
					timer.schedule(new StartGameTimer(),0, 1000);
					
				}

				class StartGameTimer extends TimerTask {
					
					int wait = 2;
					int count = 10;
					
					public void run() {
						timerStarted = true;
					if(startFinished == false){
						if (count > 0){
							count--;
							startValue = count;
							//System.out.println("COUNT = " + count);
						} else {
							timer.cancel();
							startFinished = true;
							start = true; // här bestämmer vi att vår start boolean ska bli true när timern körts klart
							 //Stops the AWT thread (and everything else)
							
							
						}
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
							ships[0].speed = 3;
							timer.cancel();
						}
					}
				}
			}	
			
			public class BallSpeedTimer{
				Toolkit toolkit;
				Timer timer = new Timer();
				public BallSpeedTimer(){
					toolkit = Toolkit.getDefaultToolkit();
					timer = new Timer();
					timer.schedule(new BallTimer(), 0, 1*1000);
				}
				class BallTimer extends TimerTask{
					public void run(){
						timer.cancel();
						}
					}
				}
			}
		
		
