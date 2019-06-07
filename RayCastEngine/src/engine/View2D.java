package engine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;

import javax.swing.JPanel;
import javax.swing.Timer;

public class View2D extends JPanel {
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;

		// Draw camera
		g.setColor(Color.red);
		fillCircle(g2d, (int)Game.camPos.x, (int)Game.camPos.y, 5);
		
		//Draw map
		for(int x = 0; x < Game.gridWidth; x++) {
			for(int y = 0; y < Game.gridHeight; y++) {
				if(Game.map[x][y] > 0) {
					g2d.setColor(Color.BLACK);
					g2d.fillRect(x*Game.cellWidth, y*Game.cellHeight, Game.cellWidth, Game.cellHeight);
				} else {
					g2d.setColor(Color.GRAY);
					g2d.drawRect(x*Game.cellWidth, y*Game.cellHeight, Game.cellWidth, Game.cellHeight);
				}
			}
		}
		
		//draw rays
		for(int i = 0; i < Game.numberofStrips; i++) {
			double opp = Game.planeWidth/2 - i*Game.stripResolution;
			double adj = Game.planeDist;
			double rayAngle = Math.toDegrees(Math.atan(opp/adj));
			double rayDir = Game.camDir + rayAngle;

			double dist1 = Ray.distance(Game.camPos, Ray.getPointHorAll(Game.camPos.x, Game.camPos.y, rayDir));
			double dist2 = Ray.distance(Game.camPos, Ray.getPointVertAll(Game.camPos.x, Game.camPos.y, rayDir));
			double dist;
			if(dist1 < dist2)
				dist = dist1;
			else
				dist = dist2;
			g2d.setStroke(new BasicStroke(1));
			g2d.setColor(Color.red);
			g2d.drawLine((int)Game.camPos.x, (int)Game.camPos.y, (int)(Game.camPos.x + dist*Math.cos(Math.toRadians(rayDir))), (int)(Game.camPos.y - dist*Math.sin(Math.toRadians(rayDir))));
		}
			
	}
	

	
	private void fillCircle(Graphics2D g2, int x, int y, int r) {
		g2.fillOval(x - r, y - r, r * 2, r * 2);
	}

}
