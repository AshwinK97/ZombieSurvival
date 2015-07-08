import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Image;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;

public class Item {

	private int index;
	private int x, y;
	private final int width = 30, height = 30;
	private boolean state;
	private Image sprite;
	
	/*
	 * requires a x, y variable for setting spawn location
	 * uses index variable to determine the properties of the item
	 * sets the state = true to allow it to be visible and active
	 * loads a sprite for the item based on the index specified
	 */

	public Item(int index, int x, int y) {
		this.index = index;
		this.x = x;
		this.y = y;
		state = true;
		loadImage(index);
	}
	
	/*
	 * loads images based on provided index
	 * catches exceptions in loading image files
	 */

	public void loadImage(int index) {
		try {
			if (index == 0) {
				sprite = ImageIO.read(new File("Medpack.png"));
			} else if (index == 1) {
				sprite = ImageIO.read(new File("Magnum.png"));
			} else if (index == 2) {
				sprite = ImageIO.read(new File("Mp7.png"));
			} else if (index == 3) {
				sprite = ImageIO.read(new File("Spaz.png"));
			}
		} catch (IOException ex) {
			System.out.println("Error reading drop image " + index);
		}
	}
	
	/*
	 * plays sound effect for item pick up
	 * based on which item was picked up
	 * uses index variable to figure out which item
	 */
	
	public void soundEffect() {
		if (index == 0) {
			Sound.HealthPickup.stop();
			Sound.HealthPickup.play();
		} else if (index == 1) {
			Sound.MagnumPickup.stop();
			Sound.MagnumPickup.play();
		} else if (index == 2) {
			Sound.M16Pickup.stop();
			Sound.M16Pickup.play();
		} else if (index == 3) {
			Sound.ShotgunPickup.stop();
			Sound.ShotgunPickup.play();
		}
	}
	
	/*
	 * instructions to draw the item sprite 
	 * when active state = true
	 * can only be invoked in the Game class
	 */

	public void draw(Graphics2D g2d) {

		if (state)
			g2d.drawImage(sprite, x, y, null);
	}
	
	/*
	 * returns a rectangle object that can be used to 
	 * check for intersections and overlap in the 
	 * Game class
	 */

	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}

	public int getIndex() {
		return index;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean getState() {
		return state;
	}
	
	/*
	 * state can be set to false when item is picked up or new one spawns
	 */

	public void setState(boolean state) {
		this.state = state;
	}
}
