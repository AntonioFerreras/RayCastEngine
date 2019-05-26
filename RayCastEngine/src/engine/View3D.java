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
	Color fogColor = new Color(207, 222, 247);
	Color shadeColor = new Color(0,0,0,70);
	int m = Game.m;
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

		//Recalculate Screen Vars
		m = Game.m;
		scaledPlaneWidth = (int) (Game.planeWidth * m);
		scaledPlaneHeight = (int) (Game.planeHeight * m);

		// Draw sky
		int a1 = (int) (map(Game.camDir + Game.FOV / 2, 0, 360, skyWidth * 2, skyWidth));
		int a2 = (int) (map(Game.camDir - Game.FOV / 2, 0, 360, skyWidth * 2, skyWidth));

		g2d.drawImage(sprSky, 0, 0, scaledPlaneWidth, scaledPlaneHeight / 2, a1, 0, a2, skyHeight, null);

		//Draw floor
		g2d.setColor(new Color(50, 50, 50));
		g2d.fillRect(0, scaledPlaneHeight / 2, scaledPlaneWidth, scaledPlaneHeight / 2);

		// Cast rays
		for (int i = 0; i < Game.numberofStrips; i++) {
			// Ray vars
			double opp = Game.planeWidth / 2 - i * Game.stripResolution;
			double adj = Game.planeDist;
			double rayAngle = Math.toDegrees(Math.atan(opp / adj));
			double rayDir = Game.camDir + rayAngle;
			
			/****************Calculations for normal walls*****************/
			// Height in world units of current block
			int currentWallHeight = 64;

			// Horizontal line collision point
			Point2D.Double p1 = Ray.getPointHor(g2d, Game.camPos.x, Game.camPos.y, rayDir);

			// Vertical line collision point
			Point2D.Double p2 = Ray.getPointVert(g2d, Game.camPos.x, Game.camPos.y, rayDir);

			// Distance from each possible collision
			double dist1 = Ray.squaredDistance(Game.camPos, p1);
			double dist2 = Ray.squaredDistance(Game.camPos, p2);

			double dist;
			int texOffset;

			// Smallest distance is correct
			Point2D.Double correctP;
			boolean isShaded;
			if (dist1 < dist2) {
				dist = Math.sqrt(dist1);
				texOffset = (int) (p1.x % Game.cellWidth);
				correctP = p1;
				isShaded = true;
			} else {
				dist = Math.sqrt(dist2);
				texOffset = (int) (p2.y % Game.cellWidth);
				correctP = p2;
				isShaded = false;
			}

			// Calculate undistorted distance to camera
			double correctDist = dist * Math.cos(Math.toRadians(Math.abs(rayAngle)));
			
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
			} else {
				texCurrent = sprBrick;
				currentWallHeight = Game.blockHeight;
			}
			
			// Strip Height on screen
			int projectedHeight = (int) (currentWallHeight / correctDist * Game.planeDist);
			// Strip height on screen for normal-sized block
			int standardProjectedHeight = (int) (Game.blockHeight / correctDist * Game.planeDist);
			
			/****************Calculations for tall walls*****************/

			int currentWallHeightTall = 0;
			
			// Horizontal line collision point for tall walls only
			Point2D.Double p1Tall = Ray.getPointHorTall(g2d, Game.camPos.x, Game.camPos.y, rayDir);

			// Vertical line collision point for tall walls only
			Point2D.Double p2Tall = Ray.getPointVertTall(g2d, Game.camPos.x, Game.camPos.y, rayDir);

			// Distance from each possible collision for tall walls only
			double dist1Tall = Ray.squaredDistance(Game.camPos, p1Tall);
			double dist2Tall = Ray.squaredDistance(Game.camPos, p2Tall);

			double distTall;
			int texOffsetTall;

			// Smallest distance is correct
			Point2D.Double correctPTall;
			boolean isShadedTall;
			if (dist1Tall < dist2Tall) {
				distTall = Math.sqrt(dist1Tall);
				texOffsetTall = (int) (p1Tall.x % Game.cellWidth);
				correctPTall = p1Tall;
				isShadedTall = true;
			} else {
				distTall = Math.sqrt(dist2Tall);
				texOffsetTall = (int) (p2Tall.y % Game.cellWidth);
				correctPTall = p2Tall;
				isShadedTall = false;
			}

			// Calculate undistorted distance to camera
			double correctDistTall = distTall * Math.cos(Math.toRadians(Math.abs(rayAngle)));

			// Calculate point hit on ray into grid coords
			int mapXTall = (int) (correctPTall.x / Game.cellWidth);
			int mapYTall = (int) (correctPTall.y / Game.cellHeight);
			
			// Choose texture and block height
			Image texCurrentTall = sprTallWall;
			boolean canDrawTallWall;
			
			//If point is inside map
			if(mapXTall < Game.gridWidth && mapYTall < Game.gridHeight) {
				if (Game.map[mapXTall][mapYTall] == 3) {
					texCurrentTall = sprTallWall;
					currentWallHeightTall = Game.blockHeight*2;
					canDrawTallWall = true;
				}else {
					canDrawTallWall = false;
				}
			} else {
				canDrawTallWall = false;
			}
			// Strip Height on screen for tall walls only
			int projectedHeightTall = (int) (currentWallHeightTall / correctDistTall * Game.planeDist);
			// Strip height on screen for normal-sized block
			int standardProjectedHeightTall = (int) (Game.blockHeight / correctDistTall * Game.planeDist);
			
			//Decide whether to draw tall wall behind or infront
			boolean tallWallIsFront;
			if(dist < distTall) {
				tallWallIsFront = false;
			} else {
				tallWallIsFront = true;
			}
			
			
			/******Drawing strips*******/
			g2d.setStroke(new BasicStroke(0));
			if (!drawTextures) {
				g2d.setColor(Color.white);

				g2d.fillRect(Game.stripResolution * i * m,
						((Game.planeHeight / 2 + standardProjectedHeight / 2) - projectedHeight) * m,
						Game.stripResolution * m, projectedHeight * m);
			} else {
				if(!tallWallIsFront) {
					if(canDrawTallWall) {
						// Draw tall wall
						g2d.drawImage(texCurrentTall, Game.stripResolution * i * m,
								((Game.planeHeight / 2 + standardProjectedHeightTall / 2) - projectedHeightTall) * m,
								(Game.stripResolution * i + Game.stripResolution) * m,
								(Game.planeHeight / 2 + standardProjectedHeightTall / 2) * m, texOffsetTall, 0,
								texOffsetTall + Game.stripResolution, currentWallHeightTall, null);

						if(isShadedTall) {
							//Draw wall shading
							g2d.setColor(shadeColor);
							g2d.fillRect(Game.stripResolution * i * m,
									((Game.planeHeight / 2 + standardProjectedHeightTall / 2) - projectedHeightTall) * m,
									Game.stripResolution * m, projectedHeightTall * m);
						}
						
						// Draw Fog when tall wall is behind normal wall
						if (!tallWallIsFront && drawFog) {
							int alpha = calculateFogAlpha(correctDistTall);

							g2d.setColor(new Color(fogColor.getRed(), fogColor.getGreen(), fogColor.getBlue(), alpha));
							g2d.fillRect(Game.stripResolution * i * m,
									((Game.planeHeight / 2 + standardProjectedHeightTall / 2) - projectedHeightTall)
											* m,
									Game.stripResolution * m, projectedHeightTall * m);

						}
					}
					
					// draw normal wall
					g2d.drawImage(texCurrent, Game.stripResolution * i * m,
							((Game.planeHeight / 2 + standardProjectedHeight / 2) - projectedHeight) * m,
							(Game.stripResolution * i + Game.stripResolution) * m,
							(Game.planeHeight / 2 + standardProjectedHeight / 2) * m, texOffset, 0,
							texOffset + Game.stripResolution, currentWallHeight, null);
					if(isShaded) {
						//Draw wall shading
						g2d.setColor(shadeColor);
						g2d.fillRect(Game.stripResolution * i * m,
								((Game.planeHeight / 2 + standardProjectedHeight / 2) - projectedHeight) * m,
								Game.stripResolution * m, projectedHeight * m);
					}
				} else {
					// draw wall
					g2d.drawImage(texCurrent, Game.stripResolution * i * m,
							((Game.planeHeight / 2 + standardProjectedHeight / 2) - projectedHeight) * m,
							(Game.stripResolution * i + Game.stripResolution) * m,
							(Game.planeHeight / 2 + standardProjectedHeight / 2) * m, texOffset, 0,
							texOffset + Game.stripResolution, currentWallHeight, null);
					
					if(isShaded) {
						//Draw wall shading
						g2d.setColor(shadeColor);
						g2d.fillRect(Game.stripResolution * i * m,
								((Game.planeHeight / 2 + standardProjectedHeight / 2) - projectedHeight) * m,
								Game.stripResolution * m, projectedHeight * m);
					}
					
					if(canDrawTallWall) {
						//Draw tall wall
						g2d.drawImage(texCurrentTall, Game.stripResolution * i * m,
								((Game.planeHeight / 2 + standardProjectedHeightTall / 2) - projectedHeightTall) * m,
								(Game.stripResolution * i + Game.stripResolution) * m,
								(Game.planeHeight / 2 + standardProjectedHeightTall / 2) * m, texOffsetTall, 0,
								texOffsetTall + Game.stripResolution, currentWallHeightTall, null);
						
						if(isShadedTall) {
							//Draw wall shading
							g2d.setColor(shadeColor);
							g2d.fillRect(Game.stripResolution * i * m,
									((Game.planeHeight / 2 + standardProjectedHeightTall / 2) - projectedHeightTall) * m,
									Game.stripResolution * m, projectedHeightTall * m);
						}
					}
				}
				

				

//				// Draw floor
//				 int row = (int) (scaledPlaneHeight/2 + projectedHeight*m/2);
//				 while(row <= scaledPlaneHeight) {
//				 double straightDist = ((double)Game.camHeight * (double)Game.planeDist) /
//				 (row - scaledPlaneHeight/2);
//				 double actualDist = straightDist/Math.cos(Math.toRadians(rayAngle));
//				 //double oppDist = straightDist*Math.tan(Math.toRadians(Math.abs(rayAngle)));
//				
//				
//				 double texX = Game.camPos.x + (actualDist * (float)
//				 Math.cos(Math.toRadians(rayDir)));
//				 double texY = Game.camPos.y + (actualDist * (float)
//				 Math.sin(Math.toRadians(rayDir)));
//				
//				 int floorTexOffsetX = (int) (texX % Game.cellWidth);
//				 int floorTexOffsetY = (int) (texY % Game.cellHeight);
//				
//				
//				 g2d.setColor(new Color(sprFloor.getRGB(floorTexOffsetX, floorTexOffsetY)));
//				 g2d.fillRect((int) (Game.stripResolution * i * m), row, 1, 1);
//				
//				
//				 //Go one pixel down
//				 row++;
//				 }
			}
			
			// Draw fog
			if(drawFog) {
				if(!tallWallIsFront) {
					int alpha = calculateFogAlpha(correctDist);
					
					//Draw fog for normal wall
					g2d.setColor(new Color(fogColor.getRed(), fogColor.getGreen(), fogColor.getBlue(), alpha));
					g2d.fillRect(Game.stripResolution * i * m,
							((Game.planeHeight / 2 + standardProjectedHeight / 2) - projectedHeight) * m,
							Game.stripResolution * m, projectedHeight * m);
				}
				

				
				if (canDrawTallWall && tallWallIsFront) {
					int alpha = calculateFogAlpha(correctDistTall);
					
					g2d.setColor(new Color(fogColor.getRed(), fogColor.getGreen(), fogColor.getBlue(), alpha));
					g2d.fillRect(Game.stripResolution * i * m,
							((Game.planeHeight / 2 + standardProjectedHeightTall / 2) - projectedHeightTall) * m,
							 Game.stripResolution * m, projectedHeightTall * m);
					
				}

				
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
	
	private int calculateFogAlpha(double dist) {
		int minimumDist = Game.cellWidth * 9;
		double drawDist = Game.cellWidth * 30;
		
		int alpha;
		if(dist >= minimumDist) {
			alpha = (int) (map(dist, minimumDist, drawDist, 0, 255));
			if (alpha > 255)
				alpha = 255;
		} else {
			alpha = 0;
		}
		
		return alpha;
	}

	private double map(double n, double start1, double stop1, double start2, double stop2) {
		return ((n - start1) / (stop1 - start1)) * (stop2 - start2) + start2;
	}
}
