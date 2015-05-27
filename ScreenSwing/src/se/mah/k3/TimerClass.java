package se.mah.k3;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import se.mah.k3.Level.GameState;

public class TimerClass {
	Toolkit toolkit;
	
	  Timer timer;
	  	  
	  public TimerClass(int seconds) {
		    toolkit = Toolkit.getDefaultToolkit();
		    timer = new Timer();
		    timer.schedule(new RemindTask(), seconds * 1000);

	  }
	  
	  class RemindTask extends TimerTask {
		    public void run() {
				System.out.println("Game is starting");
		     // toolkit.beep();
		    	timer.cancel(); //Stops the AWT thread (and everything else)
		    }
		  }
}


