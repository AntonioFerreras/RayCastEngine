package engine;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class Ray {
	static int checkCollisionHor(double x, double y) {
		int mapX = (int) (x/Game.cellWidth);
		int mapY = (int) (y/Game.cellHeight);
		
		return Game.map[mapX][mapY];
	}
	
	static int checkCollisionVert(double x, double y) {
		int mapX = (int) (x/Game.cellWidth);
		int mapY = (int) (y/Game.cellHeight);
		
		return Game.map[mapX][mapY];
	}
	
	
	static Point2D.Double getPointHor(double rx, double ry, double rdir) {
		//Convert rdir to standard rotation angle
		if(rdir < 0)
			rdir = 360+rdir;
		else if (rdir > 360) 
			rdir = rdir-360;
		
		double ay, ax;
		double xa, ya;
		
		//Find point A and Ya
		if(rdir > 0 && rdir < 180) {
			ay = Math.floor(ry/Game.cellHeight) * Game.cellHeight - 0.001d;
			ya = -Game.cellHeight;
		} else if(rdir> 180 && rdir < 360) {
			ay = Math.floor(ry/Game.cellHeight) * Game.cellHeight + Game.cellHeight;
			ya = Game.cellHeight;
		} else {
			ay = ry;
			ya = 0;
		}
		ax = rx + (ry-ay)/Math.tan(Math.toRadians(rdir));
		
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
		if(checkCollisionHor(ax, ay) > 0 && checkCollisionHor(ax, ay) != 3) {
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
			if(checkCollisionHor(x, y) > 0 && checkCollisionHor(x, y) != 3) {
				return new Point2D.Double(x, y);
			}
		}
		
		return new Point2D.Double(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	static Point2D.Double getPointVert(double rx, double ry, double rdir) {
		//Convert rdir to standard rotation angle
		if(rdir < 0)
			rdir = 360+rdir;
		else if (rdir > 360) 
			rdir = rdir-360;
		
		double ay, ax;
		double xa, ya;
		
		//Find point A and Xa
		if(rdir < 90 || rdir > 270) {
			ax = Math.floor(rx/Game.cellWidth) * Game.cellWidth + Game.cellWidth;
			xa = Game.cellHeight;
		} else if(rdir > 90 || rdir < 270) {
			ax = Math.floor(rx/Game.cellWidth) * Game.cellWidth - 0.001d;
			xa = -Game.cellHeight;
		} else {
			ax = rx;
			xa = 0;
		}
		ay = ry + (rx-ax)*Math.tan(Math.toRadians(rdir));
		
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
		if(checkCollisionVert(ax, ay) > 0 && checkCollisionVert(ax, ay) != 3) {
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
			if(checkCollisionVert(x, y) > 0 && checkCollisionVert(x, y) != 3) {
				return new Point2D.Double(x, y);
			}
		}
		
		return new Point2D.Double(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	static Point2D.Double getPointHorTall(double rx, double ry, double rdir) {
		//Convert rdir to standard rotation angle
		if(rdir < 0)
			rdir = 360+rdir;
		else if (rdir > 360) 
			rdir = rdir-360;
		
		double ay, ax;
		double xa, ya;
		
		//Find point A and Ya
		if(rdir > 0 && rdir < 180) {
			ay = Math.floor(ry/Game.cellHeight) * Game.cellHeight - 0.001d;
			ya = -Game.cellHeight;
		} else if(rdir> 180 && rdir < 360) {
			ay = Math.floor(ry/Game.cellHeight) * Game.cellHeight + Game.cellHeight;
			ya = Game.cellHeight;
		} else {
			ay = ry;
			ya = 0;
		}
		ax = rx + (ry-ay)/Math.tan(Math.toRadians(rdir));
		
		//Finding Xa
		if(0 < rdir && rdir < 180) 
			xa = Game.cellHeight/Math.tan(Math.toRadians(rdir));
		else
			xa = -Game.cellHeight/Math.tan(Math.toRadians(rdir));
		
		//Distance from a to ray
		//double dist = distance(rx, ry, ax, ay);
		
		//Check if A touches wall
		int mapX = (int) (ax/Game.cellWidth);
		int mapY = (int) (ay/Game.cellHeight);
		
		////Prevent using A point outside map
		if(mapX > Game.gridWidth-1 || mapX < 0)
			return new Point2D.Double(Integer.MAX_VALUE, Integer.MAX_VALUE);
		if(mapY > Game.gridHeight-1 || mapY < 0)
			return new Point2D.Double(Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		//Do collision check
		if(Game.map[mapX][mapY] == 3) {
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
			if(Game.map[mapX][mapY] == 3) {
				return new Point2D.Double(x, y);
			}
		}
		return new Point2D.Double(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	static Point2D.Double getPointVertTall(double rx, double ry, double rdir) {
		//Convert rdir to standard rotation angle
		if(rdir < 0)
			rdir = 360+rdir;
		else if (rdir > 360) 
			rdir = rdir-360;
		
		double ay, ax;
		double xa, ya;
		
		//Find point A and Xa
		if(rdir < 90 || rdir > 270) {
			ax = Math.floor(rx/Game.cellWidth) * Game.cellWidth + Game.cellWidth;
			xa = Game.cellHeight;
		} else if(rdir > 90 || rdir < 270) {
			ax = Math.floor(rx/Game.cellWidth) * Game.cellWidth - 0.001d;
			xa = -Game.cellHeight;
		} else {
			ax = rx;
			xa = 0;
		}
		ay = ry + (rx-ax)*Math.tan(Math.toRadians(rdir));
		
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
		if(Game.map[mapX][mapY] == 3) {
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
			if(Game.map[mapX][mapY] == 3) {
				return new Point2D.Double(x, y);
			}
		}
		
		return new Point2D.Double(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	static Point2D.Double getPointHorAll(double rx, double ry, double rdir) {
		//Convert rdir to standard rotation angle
		if(rdir < 0)
			rdir = 360+rdir;
		else if (rdir > 360) 
			rdir = rdir-360;
		
		double ay, ax;
		double xa, ya;
		
		//Find point A and Ya
		if(rdir > 0 && rdir < 180) {
			ay = Math.floor(ry/Game.cellHeight) * Game.cellHeight - 0.001d;
			ya = -Game.cellHeight;
		} else if(rdir> 180 && rdir < 360) {
			ay = Math.floor(ry/Game.cellHeight) * Game.cellHeight + Game.cellHeight;
			ya = Game.cellHeight;
		} else {
			ay = ry;
			ya = 0;
		}
		ax = rx + (ry-ay)/Math.tan(Math.toRadians(rdir));
		
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
		if(checkCollisionHor(ax, ay) > 0) {
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
			if(checkCollisionHor(x, y) > 0) {
				return new Point2D.Double(x, y);
			}
		}
		
		return new Point2D.Double(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	static Point2D.Double getPointVertAll(double rx, double ry, double rdir) {
		//Convert rdir to standard rotation angle
		if(rdir < 0)
			rdir = 360+rdir;
		else if (rdir > 360) 
			rdir = rdir-360;
		
		double ay, ax;
		double xa, ya;
		
		//Find point A and Xa
		if(rdir < 90 || rdir > 270) {
			ax = Math.floor(rx/Game.cellWidth) * Game.cellWidth + Game.cellWidth;
			xa = Game.cellHeight;
		} else if(rdir > 90 || rdir < 270) {
			ax = Math.floor(rx/Game.cellWidth) * Game.cellWidth - 0.001d;
			xa = -Game.cellHeight;
		} else {
			ax = rx;
			xa = 0;
		}
		ay = ry + (rx-ax)*Math.tan(Math.toRadians(rdir));
		
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
		if(checkCollisionVert(ax, ay) > 0) {
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
			if(checkCollisionVert(x, y) > 0) {
				return new Point2D.Double(x, y);
			}
		}
		
		return new Point2D.Double(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	public static double distance(Point2D.Double one, Point2D.Double two) {       
		return Math.sqrt((two.y - one.y) * (two.y - one.y) + (two.x - one.x) * (two.x - one.x));
	}
	
	public static double direction(Point2D.Double one, Point2D.Double two) {
	    double angle = -Math.toDegrees(Math.atan2(one.y - two.y, one.x - two.x)) + 180;

	    return angle;
	}
	
	public static double squaredDistance(Point2D.Double one, Point2D.Double two) {       
		return (two.y - one.y) * (two.y - one.y) + (two.x - one.x) * (two.x - one.x);
	}
}
