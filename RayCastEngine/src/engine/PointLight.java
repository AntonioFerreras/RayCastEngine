package engine;

import java.awt.geom.Point2D;

public class PointLight {
	private float intensity;
	private int radius;
	private double x, y;

	public PointLight(double x, double y, int radius, float intensity) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.intensity = intensity;
	}

	public float calculateIntensity(double columnX, double columnY) {
		Point2D.Double columnPoint = new Point2D.Double(columnX, columnY);
		Point2D.Double lightPoint = new Point2D.Double(x, y);
		
		double dist = Ray.distance(lightPoint, columnPoint);
		double rayDir = Ray.direction(lightPoint, columnPoint);
		
		if (dist > radius)
			return 0.0f;
		else {
			// Horizontal line collision point
			Point2D.Double p1 = Ray.getPointHorAll(x, y, rayDir);

			// Vertical line collision point
			Point2D.Double p2 = Ray.getPointVertAll(x, y, rayDir);

			// Distance from each possible collision
			double dist1 = Ray.squaredDistance(lightPoint, p1);
			double dist2 = Ray.squaredDistance(lightPoint, p2);

			double rayDistance;
			
			//The angle the ray hits the wall at (steepness)
			double steepness = 0;
			
			// Smallest distance is correct
			if (dist1 < dist2) {
				rayDistance = Math.sqrt(dist1);
				
				//Calculate steepness
				if(rayDir <= 90)
					steepness = 90 - rayDir;
				else if (rayDir <= 180)
					steepness = rayDir-90;
				else if (rayDir <= 270)
					steepness = 270 - rayDir;
				else if (rayDir <= 360)
					steepness = rayDir-270;
				
			} else {
				rayDistance = Math.sqrt(dist2);
				
				//Calculate steepness
				if(rayDir <= 90)
					steepness = rayDir;
				else if (rayDir <= 180)
					steepness = 180-rayDir;
				else if (rayDir <= 270)
					steepness = rayDir-180;
				else if (rayDir <= 360)
					steepness = 360 - rayDir;
			}
			
			//If the difference between the rayDistance and actual distance to light source, 
			//is above a certain threshold, 
			//then the column has no direct line-of-sight to the light point
			if(Math.abs(dist - rayDistance) > 1) {
				//System.out.println(rayDistance + " "+  dist);
				//System.out.println("(" + columnPoint.x + "," + columnPoint.y + ") " + dist);
				return 0.0f;
			}
			else {
				//Factor-in distance for intensity 
				float output =  (float) map(dist, 0, radius, intensity, 0);
				
				//Convert steepness to a float from 1 to 0
				steepness /= 90;
				steepness = 1 - steepness;
				
				//Then Factor-in the steepness of the angle
				output *= steepness; 
				
				return output;
			}
		}
	}

	private double map(double n, double start1, double stop1, double start2, double stop2) {
		return ((n - start1) / (stop1 - start1)) * (stop2 - start2) + start2;
	}

	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point2D.Double getLocation() {
		return new Point2D.Double(x, y);
	}

	public void setIntensity(float i) {
		intensity = i;
	}

	public float getIntensity() {
		return intensity;
	}

	public void setRadius(int r) {
		radius = r;
	}

	public int getRadius() {
		return radius;
	}
}
