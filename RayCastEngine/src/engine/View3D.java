package engine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class View3D extends JPanel {

	// Controls
	boolean up, down, left, right, turnLeft, turnRight;

	// Textures
	boolean drawTextures = true, drawFog = true;
	Image sprBrick, sprBrown, sprTallWall, sprSky;
	BufferedImage sprFloor;
	int skyWidth, skyHeight;
	Color[][] floorTexArray = {};

	// Other
	double drawDist = Game.cellWidth * 14;
	float m = Game.m;
	int scaledPlaneWidth = (int) (Game.planeWidth * m);
	int scaledPlaneHeight = (int) (Game.planeHeight * m);
	boolean drawFPS = true;

	public View3D() {
		// Load textures
		try {
			sprBrick = ImageIO.read(new File("resources/bricks.png"));
			sprBrown = ImageIO.read(new File("resources/brownstone.png"));
			sprFloor = ImageIO.read(new File("resources/planks_oak.png"));
			sprTallWall = ImageIO.read(new File("resources/sandstone_carved.png"));
			sprSky = ImageIO.read(new File("resources/sky.png"));
			skyWidth = sprSky.getWidth(null) / 3;
			skyHeight = sprSky.getHeight(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		// Draw sky
		int a1 = (int) (map(Game.camDir + Game.FOV / 2, 0, 360, skyWidth * 2, skyWidth));
		int a2 = (int) (map(Game.camDir - Game.FOV / 2, 0, 360, skyWidth * 2, skyWidth));

		g2d.drawImage(sprSky, 0, 0, scaledPlaneWidth, scaledPlaneHeight / 2, a1, 0, a2, skyHeight, null);

		g2d.setColor(new Color(50, 50, 50));
		g2d.fillRect(0, scaledPlaneHeight / 2, scaledPlaneWidth, scaledPlaneHeight / 2);

		// Cast rays
		for (int i = 0; i < Game.numberofStrips; i++) {
			// Height in world units of current block
			int currentWallHeight = 64;

			// Ray vars
			double opp = Game.planeWidth / 2 - i * Game.stripResolution;
			double adj = Game.planeDist;
			double rayAngle = Math.toDegrees(Math.atan(opp / adj));
			double rayDir = Game.camDir + rayAngle;
			
			// Horizontal line collision point
			Point2D.Double p1 = Ray.getPointHor(g2d, Game.camPos.x, Game.camPos.y, rayDir);

			// Vertical line collision point
			Point2D.Double p2 = Ray.getPointVert(g2d, Game.camPos.x, Game.camPos.y, rayDir);

			// Distance from each possible collision
			double dist1 = Ray.squaredDistance(Game.camPos, p1);
			double dist2 = Ray.squaredDistance(Game.camPos, p2);

			int dist;
			int texOffset;

			// Smallest distance is correct
			Point2D.Double correctP;
			if (dist1 < dist2) {
				dist = (int) Math.sqrt(dist1);
				texOffset = (int) (p1.x % Game.cellWidth);
				correctP = p1;
			} else {
				dist = (int) Math.sqrt(dist2);
				texOffset = (int) (p2.y % Game.cellWidth);
				correctP = p2;
			}

			// Calculate undistorted distance to camera
			double correctDist = (int) (dist * Math.cos(Math.toRadians(Math.abs(rayAngle))));

			// Calculate point hit on ray into grid coords
			int mapX = (int) (correctP.x / Game.cellWidth);
			int mapY = (int) (correctP.y / Game.cellHeight);
			
			// Choose texture and block height
			Image texCurrent;
			if (Game.map[mapX][mapY] == 1) {
				texCurrent = sprBrick;
				currentWallHeight = Game.blockHeight;
			} else if (Game.map[mapX][mapY] == 2) {
				texCurrent = sprBrown;
				currentWallHeight = Game.blockHeight;
			} else if (Game.map[mapX][mapY] == 3) {
				texCurrent = sprTallWall;
				currentWallHeight = Game.blockHeight * 2;
			} else {
				texCurrent = sprBrick;
				currentWallHeight = Game.blockHeight;
			}

			// Strip Height on screen
			int projectedHeight = (int) (currentWallHeight / correctDist * Game.planeDist);
			// Strip height on screen for normal-sized block
			int standardProjectedHeight = (int) (Game.blockHeight / correctDist * Game.planeDist);
			
			// Draw strips
			g2d.setStroke(new BasicStroke(0));
			if (!drawTextures) {
				g2d.setColor(new Color(135, 56, 98));

				g2d.fillRect((int) (Game.stripResolution * i * m),
						(int) (((Game.planeHeight / 2 + standardProjectedHeight / 2) - projectedHeight) * m),
						(int) (Game.stripResolution * m), (int) (projectedHeight * m));
			} else {
				//draw wall
				g2d.drawImage(texCurrent, (int) (Game.stripResolution * i * m),
						(int) (((Game.planeHeight / 2 + standardProjectedHeight / 2) - projectedHeight) * m),
						(int) ((Game.stripResolution * i + Game.stripResolution) * m),
						(int) ((Game.planeHeight / 2 + standardProjectedHeight / 2) * m), texOffset, 0,
						texOffset + Game.stripResolution, currentWallHeight, null);

				// Draw floor
				// int row = (int) (scaledPlaneHeight/2 + projectedHeight*m/2);
				// while(row <= scaledPlaneHeight) {
				// double straightDist = ((double)Game.camHeight * (double)Game.planeDist) /
				// (row - scaledPlaneHeight/2);
				// double actualDist = straightDist/Math.cos(Math.toRadians(rayAngle));
				// //double oppDist = straightDist*Math.tan(Math.toRadians(Math.abs(rayAngle)));
				//
				//
				// double texX = Game.camPos.x + (actualDist * (float)
				// Math.cos(Math.toRadians(rayDir)));
				// double texY = Game.camPos.y + (actualDist * (float)
				// Math.sin(Math.toRadians(rayDir)));
				//
				// int floorTexOffsetX = (int) (texX % Game.cellWidth);
				// int floorTexOffsetY = (int) (texY % Game.cellHeight);
				//
				//
				// g2d.setColor(new Color(sprFloor.getRGB(floorTexOffsetX, floorTexOffsetY)));
				// g2d.fillRect((int) (Game.stripResolution * i * m), row, 1, 1);
				//
				//
				// //Go one pixel down
				// row++;
				// }
			}
			// Draw fog
			if(drawFog) {
				int alpha = (int) (map(correctDist, 0, drawDist, 0, 255));
				if (alpha > 255)
					alpha = 255;
				g2d.setColor(new Color(50, 50, 50, alpha));
				g2d.setStroke(new BasicStroke(0));
				g2d.fillRect((int) (Game.stripResolution * i * m),
						(int) (((Game.planeHeight / 2 + standardProjectedHeight / 2) - projectedHeight) * m),
						(int) (Game.stripResolution * m), (int) (projectedHeight * m));
			}
		}

		// Draw FPS
		if (drawFPS) {
			g2d.setFont(new Font("Consolas", Font.PLAIN, 24));
			g2d.setColor(Color.RED);
			g2d.drawString("FPS: " + Game.fps, 50, 50);
			g2d.drawString("DTime: " + Game.delta, 50, 120);
		}
	}

	private double map(double n, double start1, double stop1, double start2, double stop2) {
		return ((n - start1) / (stop1 - start1)) * (stop2 - start2) + start2;
	}
}
