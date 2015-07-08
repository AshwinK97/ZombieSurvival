import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Image;
import java.awt.event.KeyEvent;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Player {

	private final int width = 20, height = 20;
	private int x, y;
	private int velX = 0, velY = 0, vel = 4;
	private int gDirection = 0;
	private int gunType;
	private int health, maxHealth = 10;
	private int walkTemp;
	private boolean isWalking;
	private boolean alive;
	private Image sprite;
	
	/*
	 * Constructor sets the players x and y value
	 * dynamic health to its final health value
	 * and sets the state of the player to alive
	 */

	public Player() {
		x = 40;
		y = 30;
		health = maxHealth;
		alive = true;
	}
	
	/*
	 * allows the player to sustain a loss in their health
	 * similar to the setHealth(int health) method, but instead
	 * removes health rather than adding health
	 */

	public void takeDamage(int damage) {
		health = health - damage;
	}
	
	/*
	 * updates data for player 
	 * takes in 3 parameters from the gun class
	 * whether or not the gun is firing
	 * the direction in which it is firing
	 * the type of gun held
	 * 
	 * if the gun is firing, it gives the firing direction
	 * higher priority than the movement direction, used 
	 * for when loading images for player 
	 * 
	 * sets the players x and y value based on the vel 
	 * set by the keyListener() method, allows for movement
	 */

	public void update(boolean isFiring, int gDirection, int gunType) {
		this.gunType = gunType;
		if (isFiring)
			this.gDirection = gDirection; // player only faces movement direction when not shooting

		if (health <= 0)
			alive = false;

		if (x >= 610 && velX == vel)
			velX = 0;
		if (x <= 5 && velX == -vel)
			velX = 0;
		if (y >= 355 && velY == vel)
			velY = 0;
		if (y <= 5 && velY == -vel)
			velY = 0;
		x += velX;
		y += velY;
	}
	
	/*
	 * loops the player walking sound 
	 * if and only if a movement key is
	 * held, else it simply plays it once
	 * or not at all if no keys pressed
	 */
	
	public void walkSound() {
		if (isWalking && walkTemp==0) {
			Sound.PlayerWalk.stop();
			Sound.PlayerWalk.loop();
			walkTemp++;
		}
		else if (!isWalking)
			Sound.PlayerWalk.stop();
	}
	
	/*
	 * loads images based on the player direction
	 * direction is decided in the update() method
	 * different up, down, left, right images for all
	 * 4 gun types possible
	 * 
	 * catches exception when image cannot be read
	 */

	public void loadImage(int direction) {
		try {
			if (direction == 0) {
				if (gunType == 0)
					sprite = ImageIO.read(new File("Man-Up.png"));
				else if (gunType == 1)
					sprite = ImageIO.read(new File("ManMagnum-Up.png"));
				else if (gunType == 2)
					sprite = ImageIO.read(new File("ManMP7-Up.png"));
				else if (gunType == 3)
					sprite = ImageIO.read(new File("ManSpaz-Up.png"));
			} else if (direction == 1) {
				if (gunType == 0)
					sprite = ImageIO.read(new File("Man-Right.png"));
				else if (gunType == 1)
					sprite = ImageIO.read(new File("ManMagnum-Right.png"));
				else if (gunType == 2)
					sprite = ImageIO.read(new File("ManMP7-Right.png"));
				else if (gunType == 3)
					sprite = ImageIO.read(new File("ManSpaz-Right.png"));
			} else if (direction == 2) {
				if (gunType == 0)
					sprite = ImageIO.read(new File("Man-Down.png"));
				else if (gunType == 1)
					sprite = ImageIO.read(new File("ManMagnum-Down.png"));
				else if (gunType == 2)
					sprite = ImageIO.read(new File("ManMP7-Down.png"));
				else if (gunType == 3)
					sprite = ImageIO.read(new File("ManSpaz-Down.png"));
			} else if (direction == 3) {
				if (gunType == 0)
					sprite = ImageIO.read(new File("Man-Left.png"));
				else if (gunType == 1)
					sprite = ImageIO.read(new File("ManMagnum-Left.png"));
				else if (gunType == 2)
					sprite = ImageIO.read(new File("ManMP7-Left.png"));
				else if (gunType == 3)
					sprite = ImageIO.read(new File("ManSpaz-Left.png"));
			}
		} catch (IOException ex) {
			System.out.println("Error reading player image");
		}
	}
	
	/*
	 * instructions to draw the health bar and sprite for player
	 * only used in the paint() method in Game class
	 */

	public void draw(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setColor(Color.white);
		g2d.drawString("Health: ", 2, 405);
		g2d.drawRect(52, 390, ((15 * maxHealth) + 5), 20);
		g2d.setColor(Color.red);
		g2d.fillRect(55, 393, health * 15, 15);
		g2d.setColor(Color.white);
		if (maxHealth >= 10)
			g2d.drawString(health + "/" + maxHealth,
					(((20 * maxHealth) / 2) + 12), 405);
		else
			g2d.drawString(health + "/" + maxHealth,
					(((20 * maxHealth) / 2) + 15), 405);

		loadImage(gDirection);
		g2d.drawImage(sprite, x, y, null);

	}
	
	/*
	 * no walking when keys aren't pressed
	 * sets walkTemp = 0 for the walkSound method
	 * indicates that no sound should be played
	 * if specific key is released, the velocity 
	 * for that key's plane is set to 0
	 */

	public void keyReleased(KeyEvent e) {
		walkTemp = 0;
		isWalking = false;
		walkSound();
		if (e.getKeyCode() == KeyEvent.VK_A)
			velX = 0;
		if (e.getKeyCode() == KeyEvent.VK_D)
			velX = 0;
		if (e.getKeyCode() == KeyEvent.VK_W)
			velY = 0;
		if (e.getKeyCode() == KeyEvent.VK_S)
			velY = 0;
	}
	
	/*
	 * if key is pressed walking is set to true
	 * and walksound() is called, so sound is played 
	 * for as long as key is held
	 * direction is set based on key pressed
	 * velocity is changed based on key pressed
	 */

	public void keyPressed(KeyEvent e) {
		isWalking = true;
		walkSound();
		if (e.getKeyCode() == KeyEvent.VK_A) {
			gDirection = 3;
			velX = -vel;
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {
			gDirection = 1;
			velX = vel;
		}
		if (e.getKeyCode() == KeyEvent.VK_W) {
			gDirection = 0;
			velY = -vel;
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			gDirection = 2;
			velY = vel;
		}
	}
	
	/*
	 * returns a rectangle object that can be used to 
	 * check for intersections and overlap in the 
	 * Game class
	 */

	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
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

	public boolean getAlive() {
		return alive;
	}

	public int getHealth() {
		return health;
	}

	public int getGunType() {
		return gunType;
	}
	
	public void setVel(int vel) {
		this.vel = vel;
	}
	
	public void setVelX(int velX) {
		this.velX = velX;
	}
	public void setVelY(int velY) {
		this.velY = velY;
	}
	
	/*
	 * used when player picks up health pack
	 * allows player to replenish dynamic health only
	 */

	public void setHealth(int heal) {
		if (!(this.health + heal > maxHealth)) {
			this.health += heal;
		}
	}

	public void setGunType(int gunType) {
		this.gunType = gunType;
	}
}