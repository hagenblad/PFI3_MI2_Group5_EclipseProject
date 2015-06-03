
package se.mah.k3;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Arrays;
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
	
	Boolean player1in = false;
	Boolean player2in = false;
	Boolean player3in = false;
	Boolean player4in = false;


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
			public void onChildRemoved(DataSnapshot arg0) {
				System.out.println(arg0);
				
				long i = (long) arg0.child("position").getValue();
				System.out.println(": postion datasnap   "+i);
				
				if(i == 1){
					player1in = false;
					System.out.println("1 out");
				}
				if(i == 2){
					player2in = false;
					System.out.println("2 out");
				}
				if(i == 3){
					player3in = false;
					System.out.println("3 out");
				}
				if(i == 4){
					player4in = false;
					System.out.println("4 out");
				}
				
				for (User u : users){
					if(u.getPosition() == i){
						users.remove(u);
					}
				}
				
//				users.remove(users.get((int) i));
				for(User user : users){
					System.out.println(user.getId());
					System.out.println(user.getPosition());
				}
			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {}

			//A user changed some value so update
			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
				Iterable<DataSnapshot> dsList= arg0.getChildren();
				
				int place = Collections.binarySearch(users, new User(arg0.getKey(),0,0,0,0)); 
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
				int tempPosition = 1;
				if (arg0.hasChildren()){
					Random r = new Random();
					int x = r.nextInt(getSize().width);
					int y = r.nextInt(getSize().height); 
					listCount = users.size();
					
					System.out.println(users.size()+ "users.size");

					

					if (listCount ==0){
						int slot = 0;
						if(player1in == true && player2in == true && player3in == true && player4in == false){
							slot = 4;
							player4in = true;
							System.out.println("4");
						}
						if(player1in == true && player2in == true && player3in == false && player4in == false){
							slot = 3;
							player3in = true;
							System.out.println("3");
						}
						if(player1in == true && player2in == false && player3in == false && player4in == false){
							slot = 2;
							player2in = true;
							System.out.println("2");
						}
						if(player1in == false && player2in == false && player3in == false && player4in == false){
							slot = 1;
							player1in = true;
							System.out.println("1");
						}
						User user = new User(arg0.getKey(), level.relX+11, level.relX+11, ships[0].player1lives, slot); // create player 1
						if (!users.contains(user)){
							users.add(user);
							user.userHeight = 100;
							user.userWidth = 10;
							ships[0].player1lives = 5;
							myFirebaseRef.child(arg0.getKey()).child("position").setValue((int)user.getPosition());
						}
					}

					if (listCount ==1){
						int slot = 0;
						if(player1in == true && player2in == true && player3in == true && player4in == false){
							slot = 4;
							player4in = true;
							System.out.println("4");
						}
						if(player1in == true && player2in == true && player3in == false && player4in == false){
							slot = 3;
							player3in = true;
							System.out.println("3");
						}
						if(player1in == true && player2in == false && player3in == false && player4in == false){
							slot = 2;
							player2in = true;
							System.out.println("2");
						}
						if(player1in == false && player2in == false && player3in == false && player4in == false){
							slot = 1;
							player1in = true;
							System.out.println("1");
						}
						User user = new User(arg0.getKey(), level.screenWidth-11, level.screenWidth-10, ships[0].player2lives, slot); // create player 2

						if (!users.contains(user)){
							users.add(user);
							user.userHeight = 100;
							user.userWidth = 10;
							ships[0].player2lives = 5;
							myFirebaseRef.child(arg0.getKey()).child("position").setValue((int)user.getPosition());
						}
					}	

					if (listCount == 2){
						int slot = 0;
						if(player1in == true && player2in == true && player3in == true && player4in == false){
							slot = 4;
							player4in = true;
							System.out.println("4");
						}
						if(player1in == true && player2in == true && player3in == false && player4in == false){
							slot = 3;
							player3in = true;
							System.out.println("3");
						}
						if(player1in == true && player2in == false && player3in == false && player4in == false){
							slot = 2;
							player2in = true;
							System.out.println("2");
						}
						if(player1in == false && player2in == false && player3in == false && player4in == false){
							slot = 1;
							player1in = true;
							System.out.println("1");
						}
						User user = new User(arg0.getKey(), level.relX+11, level.relY+11 , ships[0].player3lives, slot); // create player 3
						if(!users.contains(user)){
							users.add(user);
							user.userHeight = 100;
							user.userWidth = 10;							
							ships[0].player3lives = 5;
							myFirebaseRef.child(arg0.getKey()).child("position").setValue((int)user.getPosition());
						}
					}
					if (listCount == 3){
						int slot = 0;
						if(player1in == true && player2in == true && player3in == true && player4in == false){
							slot = 4;
							player4in = true;
							System.out.println("4");
						}
						if(player1in == true && player2in == true && player3in == false && player4in == false){
							slot = 3;
							player3in = true;
							System.out.println("3");
						}
						if(player1in == true && player2in == false && player3in == false && player4in == false){
							slot = 2;
							player2in = true;
							System.out.println("2");
						}
						if(player1in == false){
							slot = 1;
							player1in = true;
							System.out.println("1");
						}
						User user = new User(arg0.getKey(),level.relX-11,level.screenHeight-11, ships[0].player4lives, slot); // create player 4
						if (!users.contains(user)){
							users.add(user);
							user.userHeight = 100;
							user.userWidth = 10;							
							ships[0].player4lives = 5;
							myFirebaseRef.child(arg0.getKey()).child("position").setValue((int)user.getPosition());
						}
					}
					
					

					System.out.println("Users in the game : " + users.size());

				}
				Collections.sort(users);
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

		Graphics2D g2= (Graphics2D) g;
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(0, 0, getSize().width, getSize().height);
		g2.setColor(Color.black);


		g2.setFont(ExoBoldSize);
		g.setFont(ExoBoldSize);


		g2.finalize();
		if(start == false){

			ballXPos = level.screenWidth/2;
			ballYPos = level.screenHeight/2;

			Color c = new Color(19,156,234);
			g2.setColor(c);
			Image imgStart = Toolkit.getDefaultToolkit().getImage("src/images/Startscreen2.jpg");

			g2.drawImage(imgStart, 0, 0, this);
			Color gray = Color.decode("#2b2b2b");
			g.setColor(gray);

			g.drawString("The game will start when two or more players connect", level.screenWidth/2-205, level.screenHeight/2);

		}else{

			ballXPos = ships[0].getXPos();
			ballYPos = ships[0].getYPos(); 


		}
		//Background

		Color c = new Color(19,156,234);
		g2.setColor(c);

		Area areaBall = new Area (polyBally);
		Area areaTLC = new Area (polyTLC);
		Area areaBLC = new Area (polyBLC);
		Area areaTRC = new Area (polyTRC);
		Area areaBRC = new Area (polyBRC);

		//corner collision
		if(areaTLC.intersects(ballXPos, ballYPos, ball.getSize(), ball.getSize())){
			System.out.println("CORNER");
			ships[0].topLeftCornerBounce();

		}else if(areaBLC.intersects(ballXPos, ballYPos, ball.getSize(), ball.getSize())){
			System.out.println("CORNER");
			ships[0].bottomLeftCornerBounce();
		}else if(areaTRC.intersects(ballXPos, ballYPos, ball.getSize(), ball.getSize())){
			System.out.println("CORNER");
			ships[0].topRightCornerBounce();
		}else if(areaBRC.intersects(ballXPos, ballYPos, ball.getSize(), ball.getSize())){
			System.out.println("CORNER");
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
			Image imgOutside = Toolkit.getDefaultToolkit().getImage("src/images/outside.png");
			g2.drawImage(imgOutside, 0, 0, this);

			if(startTimer == false && start == false){
				initiateTimer(); 
			}		


			if(start == true){ 
				restartTimer = false;
				ships[0].move();
				Image boll = Toolkit.getDefaultToolkit().getImage("src/images/boll.png");
				g2.drawImage(boll, ballXPos, ballYPos, ball.getSize(),ball.getSize(), this);

				ballXPos = ships[0].xPos;
				ballYPos = ships[0].yPos;
			} else {
				g2.drawString(""+ startValue , level.screenWidth/2 + 100,level.screenHeight/2);
			}

			//Test
			for (User user : users) {

				if(users.size()>=2){  // defines how many players that needs to be in the game for it to start			

					paddleTop = y - playerPingSize;
					paddleBottom = y;

					paddlePosY = y - playerPingSize;


					y = (int)(user.getyRel()*getSize().height);
					x = (int)(user.getxRel()*getSize().width);

					// sets appropriate height to players based on ping
					setPlayerHeight(user);						

					//Draws paddle from center of finger placement on android
					playerPingSize = user.userHeight + playerPing/4;

					// defines how many players that needs to be in the game for it to start
					if(users.size()>=2){ 
						//Check if too many players
						if(users.size()>4){
							for(int i = users.size(); i>4; i--){
								users.remove(i);
							}
						}

						g2.setColor(user.getColor());

						//PLAYER 1 STUFF
						if(user.getPosition() == 1){
							//Set player 1 color
							Color blue = user.getColor();
							g.setColor(blue);

							//Draw out paddle
							if (ships[0].playerOneOut() == false){
								ships[0].paddleOneHit(level.relX+1, 240+ y - (playerPingSize/2), user.userWidth, playerPingSize);
								g2.drawImage(player1, level.relX+1, 240+ y - (playerPingSize/2), user.userWidth, playerPingSize, this);
							} else {
								ships[0].paddleOneHit(level.relX+1, level.relY+70, 10, level.relY+530);
								g2.drawImage(LeftDead, level.relX+1, level.relY+70, 10, level.relY+530, this);
							}

							// draw out player 1 info
							String player1name = user.getId();
							g.drawString(player1name, 20, 445);
							String player1Delay = String.valueOf(user.getDelay());
							g.drawString("Ping: " + player1Delay, 20, 475);

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
							if (ships[0].player1Win() == true){
								String player1Wins = user.getId() + " wins!";
								g.drawString(player1Wins, 450, 200);
								ships[0].speed = 0;
								ships[0].position.x = 450;
								ships[0].position.y = 300;
								if(restartTimer == false){
									new RestartTimer();
									restartTimer = true;
								}
							}
						}   else if (user.getPosition() == 2 ){

							//PLAYER 2 STUFF

							Color green = user.getColor();
							g.setColor(green);

							if (ships[0].playerTwoOut() == false){
								ships[0].paddleTwoHit(level.screenWidth-11, 250 + y - (playerPingSize/2), user.userWidth, playerPingSize);
								g2.drawImage(player2, level.screenWidth-11, 250 + y - (playerPingSize/2), user.userWidth, playerPingSize, this);
							} else {
								ships[0].paddleTwoHit(level.screenWidth-11,level.relY+70, 10, level.relY+530);
								g2.drawImage(RightDead,level.screenWidth-11,level.relY+70, 10, level.relY+530, this);
							}

							// draw out player 2 info
							String player2name = user.getId();
							g.drawString(player2name, 845, 445);
							String player2Delay = String.valueOf(user.getDelay());
							g.drawString("Ping: " + player2Delay, 845, 475);

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
							if (ships[0].player2Win() == true){
								String player2Wins = user.getId() + " wins!";
								g.drawString(player2Wins, 450, 200);
								ships[0].speed = 0;
								ships[0].position.x = 450;
								ships[0].position.y = 300;
								if(restartTimer == false){
									new RestartTimer();
									restartTimer = true;
								}
							}
						}

						//PLAYER 3 STUFF
						if (users.size() < 3 ){
							ships[0].paddleThreeHit (level.relX+70, level.relY+1 , level.relX+530, 10);
						}
						if (user.getPosition() == 3){								
							Color red = user.getColor();
							g.setColor(red);

							if(ships[0].playerThreeOut() == false){
								ships[0].paddleThreeHit (250 + y-(playerPingSize/2), level.relY+1 , playerPingSize ,user.userWidth);
								g2.drawImage(player3, 250 + y-(playerPingSize/2), level.relY+1 ,playerPingSize, user.userWidth ,this);
							} else {
								ships[0].paddleThreeHit (level.relX+70, level.relY+1 , level.relX+530, 10);
								g2.drawImage(TopDead, level.relX+70, level.relY+1 , level.relX+530 , 10,this);
							}

							// draw out player 3 info
							String player3name = user.getId();
							g.drawString(player3name, 450, 75);
							String player3Delay = String.valueOf(user.getDelay());
							g.drawString("Ping: " + player3Delay, 450, 105);

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
							if (ships[0].player3Win() == true){
								String player3Wins = user.getId() + " wins!";
								g.drawString(player3Wins, 450, 200);
								ships[0].speed = 0;
								ships[0].position.x = 450;
								ships[0].position.y = 300;	
								if(restartTimer == false){
									new RestartTimer();
									restartTimer = true;
								}
							}
						}

						//PLAYER 4 STUFF
						if (users.size() < 4){
							ships[0].paddleFourHit(level.relX+70,level.screenHeight-11 , level.relX+530, 10);
						}

						if (user.getPosition() == 4){
							Color yellow = user.getColor();
							g.setColor(yellow);

							if(ships[0].playerFourOut() == false){
								ships[0].paddleFourHit (250 + y-(playerPingSize/2),level.screenHeight-15 , playerPingSize ,user.userWidth);
								g2.drawImage(player4, 250 + y-(playerPingSize/2), level.screenHeight-15 , playerPingSize, user.userWidth ,this);
							} else {
								ships[0].paddleFourHit(level.relX+70,level.screenHeight-11 , level.relX+530, 10);
								g2.drawImage(BottomDead,level.relX+70,level.screenHeight-11 , level.relX+530, 10,this);
							}

							// draw out player 4 info
							String player4name = user.getId();
							g.drawString(player4name, 450, 815);
							String player4Delay = String.valueOf(user.getDelay());
							g.drawString("PING = "+ player4Delay, 450,845);

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
							if (ships[0].player4Win() == true){
								String player4Wins = user.getId() + " wins!";
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
					} else {
						timer.cancel();
						startFinished = true;
						start = true; 				
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
					count--;
				} else {
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


