package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Timer;

public class Input extends KeyAdapter {
	private KeyListener listener;
	
	public void setListener(KeyListener l) {
		listener = l;
	}
	
        int numKeys = 4;
        Timer t[] = new Timer[numKeys];

        private void stopTimers() {
                for(int i = 0; i < numKeys; i++) {
                        t[i].stop();
                }
        }

        public Input() {
                int delay = 60;
                ActionListener taskPerformerU = new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                        	listener.upArrowUp();
                                t[0].stop();
                        }
                };
                ActionListener taskPerformerL = new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                        	listener.leftArrowUp();
                                t[1].stop();
                        }
                };
                ActionListener taskPerformerR = new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                        	listener.rightArrowUp();
                                t[2].stop();
                        }
                };
                ActionListener taskPerformerS = new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                        	listener.spaceUp();
                                t[3].stop();
                        }
                };

                t[0] = new Timer(delay, taskPerformerU);
                t[1] = new Timer(delay, taskPerformerL);
                t[2] = new Timer(delay, taskPerformerR);
                t[3] = new Timer(delay, taskPerformerS);
        }

        public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_UP) {
                        if (t[0].isRunning())
                                t[0].restart();
                        else {
                                stopTimers();
                                listener.upArrowDown();
                        }
                }
                if (keyCode == KeyEvent.VK_LEFT) {
                        if (t[1].isRunning())
                                t[1].restart();
                        else {
                                stopTimers();
                                listener.leftArrowDown();
                        }
                }
                if (keyCode == KeyEvent.VK_RIGHT) {
                        if (t[2].isRunning())
                                t[2].restart();
                        else {
                                stopTimers();
                                listener.rightArrowDown();
                        }
                }
                if (keyCode == KeyEvent.VK_SPACE) {
                        if (t[3].isRunning())
                                t[3].restart();
                        else {
                                stopTimers();
                                listener.spaceDown();
                        }
                }
        }

        public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_UP) {
                        if (t[0].isRunning())
                                t[0].restart();
                        else
                                t[0].start();
                }
                if (keyCode == KeyEvent.VK_LEFT) {
                        if (t[1].isRunning())
                                t[1].restart();
                        else
                                t[1].start();
                }
                if (keyCode == KeyEvent.VK_RIGHT) {
                        if (t[2].isRunning())
                                t[2].restart();
                        else
                                t[2].start();
                }
                if (keyCode == KeyEvent.VK_SPACE) {
                        if (t[3].isRunning())
                                t[3].restart();
                        else
                                t[3].start();
                }
        }

        
}