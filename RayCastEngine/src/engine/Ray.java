package engine;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class Ray {
	static Point2D.Double getPointHor(Graphics2D g2d, double rx, double ry, double rdir) {
		//Convert rdir to standard rotation angle
		if(rdir < 0)
			rdir = 360+rdir;
		else if (rdir > 360) 
			rdir = rdir-360;
		
		double ay, ax;
		double xa, ya;
		
		//Find point A and Ya
		if(rdir > 0 && rdir < 180) {
			ay = Math.floor(Game.camPos.y/Game.cellHeight) * Game.cellHeight - 1;
			ya = -Game.cellHeight;
		} else if(rdir> 180 && rdir < 360) {
			ay = Math.floor(Game.camPos.y/Game.cellHeight) * Game.cellHeight + Game.cellHeight;
			ya = Game.cellHeight;
		} else {
			ay = Game.camPos.y;
			ya = 0;
		}
		ax = Game.camPos.x + (Game.camPos.y-ay)/Math.tan(Math.toRadians(rdir));
		
		//Finding Xa
		if(0 < rdir && rdir < 180) 
			xa = Game.cellHeight/Math.tan(Math.toRadians(rdir));
		else
			xa = -Game.cellHeight/Math.tan(Math.toRadians(rdir));
		
		//Check if A touches wall
		int mapX = (int) (ax/Game.cellWidth);
		int mapY = (int) (ay/Game.cellHeight);
		
		////Prevent using A point outside map
		if(mapX > Game.gridWidth-1 || mapX < 0)
			return new Point2D.Double(Integer.MAX_VALUE, Integer.MAX_VALUE);
		if(mapY > Game.gridHeight-1 || mapY < 0)
			return new Point2D.Double(Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		//Do collision check
		if(Game.map[mapX][mapY] > 0 && Game.map[mapX][mapY] != 3) {
			return new Point2D.Double(ax, ay);
		}
		
		
		
		//Finding all other points where ray crosses horizontal grid lines
		//If points touches wall, return the distance
		double x = ax;
		double y = ay;
		int maxSteps = 20;
		for(int i = 0; i < maxSteps; i++) {
			x += xa;
			y += ya;
			
			mapX = (int) (x/Game.cellWidth);
			mapY = (int) (y/Game.cellHeight);
			
			//Prevent using point outside map
			if(mapX > Game.gridWidth-1 || mapX < 0)
				break;
			if(mapY > Game.gridHeight-1 || mapY < 0)
				break;
			
			//Check for collision
			if(Game.map[mapX][mapY] > 0 && Game.map[mapX][mapY] != 3) {
				return new Point2D.Double(x, y);
			}
		}
		
		return new Point2D.Double(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	static Point2D.Double getPointVert(Graphics2D g2d, double rx, double ry, double rdir) {
		//Convert rdir to standard rotation angle
		if(rdir < 0)
			rdir = 360+rdir;
		else if (rdir > 360) 
			rdir = rdir-360;
		
		double ay, ax;
		double xa, ya;
		
		//Find point A and Xa
		if(rdir < 90 || rdir > 270) {
			ax = Math.floor(Game.camPos.x/Game.cellWidth) * Game.cellWidth + Game.cellWidth;
			xa = Game.cellHeight;
		} else if(rdir > 90 || rdir < 270) {
			ax = Math.floor(Game.camPos.x/Game.cellWidth) * Game.cellWidth - 1;
			xa = -Game.cellHeight;
		} else {
			ax = Game.camPos.x;
			xa = 0;
		}
		ay = Game.camPos.y + (Game.camPos.x-ax)*Math.tan(Math.toRadians(rdir));
		
		//Finding Ya
		if(rdir < 90 || rdir > 270) 
			ya = -Game.cellHeight*Math.tan(Math.toRadians(rdir));
		else
			ya = Game.cellHeight*Math.tan(Math.toRadians(rdir));
		
		//Check if A touches wall
		int mapX = (int) (ax/Game.cellWidth);
		int mapY = (int) (ay/Game.cellHeight);
		
		//Prevent using A point outside map
		if(mapX > Game.gridWidth-1 || mapX < 0)
			return new Point2D.Double(Integer.MAX_VALUE, Integer.MAX_VALUE);
		if(mapY > Game.gridHeight-1 || mapY < 0)
			return new Point2D.Double(Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		//Collision check
		if(Game.map[mapX][mapY] > 0 && Game.map[mapX][mapY] != 3) {
			return new Point2D.Double(ax, ay);
		}
		
		
		//Finding all other points where ray crosses horizontal grid lines
		//If points touches wall, return the distance
		double x = ax;
		double y = ay;
		int maxSteps = 20;
		for(int i = 0; i < maxSteps; i++) {
			x += xa;
			y += ya;
			
			mapX = (int) (x/Game.cellWidth);
			mapY = (int) (y/Game.cellHeight);
			
			//Prevent using point outside map
			if(mapX > Game.gridWidth-1 || mapX < 0)
				break;
			if(mapY > Game.gridHeight-1 || mapY < 0)
				break;
			
			//Check for collision
			if(Game.map[mapX][mapY] > 0 && Game.map[mapX][mapY] != 3) {
				return new Point2D.Double(x, y);
			}
		}
		
		return new Point2D.Double(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	public static double distance(Point2D.Double one, Point2D.Double two) {       
		return Math.sqrt((two.y - one.y) * (two.y - one.y) + (two.x - one.x) * (two.x - one.x));
	}
	
	public static double squaredDistance(Point2D.Double one, Point2D.Double two) {       
		return (two.y - one.y) * (two.y - one.y) + (two.x - one.x) * (two.x - one.x);
	}
}
