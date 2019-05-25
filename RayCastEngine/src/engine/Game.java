package engine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class Game {

	// Map
	public static int gridWidth = 15, gridHeight = 15;
	public static int cellWidth = 64, cellHeight = 64, blockHeight = 64;
	public static int[][] map ={{ 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2 },
								{ 1, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2 }, 
								{ 1, 0, 2, 1, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 2 },
								{ 1, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2 }, 
								{ 2, 0, 2, 0, 0, 0, 0, 0, 2, 2, 2, 0, 2, 2, 2 },
								{ 2, 0, 2, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 2 }, 
								{ 1, 0, 0, 0, 0, 3, 2, 0, 0, 0, 0, 0, 0, 0, 2 },
								{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2 }, 
								{ 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 4, 0, 4, 4, 4 },
								{ 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 4 }, 
								{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4 },
								{ 1, 0, 0, 2, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0, 4 }, 
								{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0, 4 },
								{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4 }, 
								{ 1, 1, 1, 1, 1, 1, 1, 4, 4, 4, 4, 4, 4, 4, 4 } };

	// Controls
	static boolean up, down, left, right, turnLeft, turnRight;
	static float spd = 3;

	// Camera vars
	public static Point2D.Double camPos = new Point2D.Double(gridWidth*cellWidth/2, gridHeight*cellHeight/2);
	public static double camDir = 270; // degrees
	public static int camHeight = 64;
	
	public static int m = 2; // Resolution scale
	
	public static double FOV = 90;
	public static int planeWidth = (int) (1920/m);
	public static double hToWRatio = 9.0/16.0; // 16 : 9 aspect ratio
	//public static int planeWidth = (int) (2*planeDist*Math.tan(Math.toRadians(FOV/2)));
	public static int planeHeight = (int) (hToWRatio * planeWidth);
	
	
	public static int planeDist = (int) ((planeWidth/2)/Math.tan(Math.toRadians(FOV/2)));
	
	// 2D view
	static View2D topDownView = new View2D();
	public static int width = gridWidth * cellWidth, height = (1 + gridHeight) * cellHeight;

	// 3D view
	static View3D perspectiveView = new View3D();
	//public static int screenWidth = (int) (planeWidth*multiplier), screenHeight = (int) (planeHeight*multiplier);
	public static int stripResolution = 1;
	public static int numberofStrips = planeWidth / stripResolution;
	
	//Frame updater
	static boolean isRunning = true;
	static double delta;
	static int frames, fps;

//	static int frameTime = 1000/60;// 60 FPS
//	static Timer timer = new Timer(frameTime, new ActionListener() {
//		@Override
//		public void actionPerformed(ActionEvent e) {
//			step();
//			topDownView.repaint();
//			perspectiveView.repaint();
//		}
//	});

	static KeyListener controls = new KeyListener() {

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (Character.toLowerCase(e.getKeyChar()) == 'w')
				up = true;
			if (Character.toLowerCase(e.getKeyChar()) == 'a')
				left = true;
			if (Character.toLowerCase(e.getKeyChar()) == 's')
				down = true;
			if (Character.toLowerCase(e.getKeyChar()) == 'd')
				right = true;

			if (Character.toLowerCase(e.getKeyChar()) == 'j')
				turnLeft = true;
			if (Character.toLowerCase(e.getKeyChar()) == 'k')
				turnRight = true;
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (Character.toLowerCase(e.getKeyChar()) == 'w')
				up = false;
			if (Character.toLowerCase(e.getKeyChar()) == 'a')
				left = false;
			if (Character.toLowerCase(e.getKeyChar()) == 's')
				down = false;
			if (Character.toLowerCase(e.getKeyChar()) == 'd')
				right = false;

			if (Character.toLowerCase(e.getKeyChar()) == 'j')
				turnLeft = false;
			if (Character.toLowerCase(e.getKeyChar()) == 'k')
				turnRight = false;
		}
	};

	public static void main(String[] args) {
		m = Integer.parseInt(JOptionPane.showInputDialog("Enter resolution scale. \n1 - Full resolution\n2 - Optimal\n4 - Fast\n8 - Fastest"));
		
		planeWidth = (int) (Integer.parseInt(JOptionPane.showInputDialog("Enter your display width (in pixels)"))/m);
		planeHeight = (int) (Integer.parseInt(JOptionPane.showInputDialog("Enter your display height (in pixels)"))/m);
		planeDist = (int) ((planeWidth/2)/Math.tan(Math.toRadians(FOV/2)));
		numberofStrips = planeWidth / stripResolution;
		
		System.out.println(numberofStrips);
		
//		// Window frames
//		JFrame frame2D = new JFrame("2D view");
//		frame2D.setSize(width, height);
//		frame2D.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame2D.setResizable(false);
//		frame2D.add(topDownView);
//		frame2D.setVisible(true);

		JFrame frame3D = new JFrame("3D view");
		frame3D.setSize((int) (planeWidth * m), (int) (planeHeight * m));
		frame3D.setLocationRelativeTo(null);
		frame3D.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame3D.setResizable(false);
		frame3D.setUndecorated(true);
		frame3D.add(perspectiveView);
		frame3D.setVisible(true);

		// Add controls
		frame3D.addKeyListener(controls);
		
		long lastLoopTime = System.nanoTime();
		final int TARGET_FPS = 60;
		final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
		long lastFpsTime = 0;

		// keep looping round til the game ends
		while (isRunning) {
			// work out how long its been since the last update, this
			// will be used to calculate how far the entities should
			// move this loop
			long now = System.nanoTime();
			long updateLength = now - lastLoopTime;
			lastLoopTime = now;
			delta = updateLength / ((double) OPTIMAL_TIME);

			// update the frame counter
			lastFpsTime += updateLength;
			frames++;

			// update our FPS counter if a second has passed since
			// we last recorded
			if (lastFpsTime >= 1000000000) {
				lastFpsTime = 0;
				fps = frames;
				frames = 0;
			}

			// update the game logic
			step();

			// draw everyting
			//topDownView.repaint();
			perspectiveView.repaint();

			// we want each frame to take 10 milliseconds, to do this
			// we've recorded when we started the frame. We add 10 milliseconds
			// to this and then factor in the current time to give
			// us our final value to wait for
			// remember this is in ms, whereas our lastLoopTime etc. vars are in ns.
			try {
				Thread.sleep((lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	// Start Frame timer
	// timer.start();
	}
	
	private static void step() {
		if(up) {
			camPos.x += spd * Math.cos(Math.toRadians(camDir)) * delta;
			camPos.y += spd * -Math.sin(Math.toRadians(camDir)) * delta;
		}
		if(down) {
			camPos.x += spd * -Math.cos(Math.toRadians(camDir)) * delta;
			camPos.y += spd * Math.sin(Math.toRadians(camDir)) * delta;
		}
		if(left) {
			camPos.x += spd * Math.cos(Math.toRadians(camDir+90)) * delta;
			camPos.y += spd * -Math.sin(Math.toRadians(camDir+90)) * delta;
		}
		if(right) {
			camPos.x += spd * -Math.cos(Math.toRadians(camDir+90)) * delta;
			camPos.y += spd * Math.sin(Math.toRadians(camDir+90)) * delta;
		}
		
		if(turnLeft)
			camDir+=2 * delta;
		if(turnRight)
			camDir-=2 * delta;
		
		//Convert rdir to standard rotation angle
		if(camDir < 0)
			camDir = 360+camDir;
		else if (camDir > 360) 
			camDir = camDir-360;
	}

}
