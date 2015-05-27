package se.mah.k3;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import se.mah.k3.Level.GameState;

public class TimerClass {
	Toolkit toolkit;

	Ball ball = new Ball();
	BallLogic bL = new BallLogic(ball);
	
	Level level = new Level();
	  Timer timer;
	  DrawPanel dP = new DrawPanel();
	  
//	 public boolean start = false;
	  
	  public TimerClass(int seconds) {
		    toolkit = Toolkit.getDefaultToolkit();
		    timer = new Timer();
		    timer.schedule(new RemindTask(), seconds * 1000);

	  }
	  
	  class RemindTask extends TimerTask {
		    public void run() {
		    	dP.start = true; // här bestämmer vi att vår start boolean ska bli true när timern körts klart
				System.out.println("Game is starting");
		     // toolkit.beep();
		    	timer.cancel(); //Stops the AWT thread (and everything else)
		    }
		  }
}


