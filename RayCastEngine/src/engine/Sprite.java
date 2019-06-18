package engine;

import java.awt.Image;
import java.awt.geom.Point2D;

public class Sprite {
	private double x, y;
	private Image image;
	private float scale = 1.0f;
	
	public Sprite(Image image, double x, double y) {
		this.image = image;
		this.x = x;
		this.y = y;
	}
	
	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point2D.Double getLocation() {
		return new Point2D.Double(x, y);
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Image getImage() {
		return image;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getScale() {
		return scale;
	}
}
