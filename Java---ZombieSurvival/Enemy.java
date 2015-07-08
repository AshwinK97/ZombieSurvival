import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class Enemy {

	private int wave;
	private int x = 0, y = 0;
	private int cX, cY;
	private int vel;
	private int direction = 0; // 0=up, 1=right, 2=down, 3=left
	private int trueVel;
	private int width, height;
	private String type = null;
	private int species;
	private boolean alive;
	private int health;
	private int attackTimer; 
	private int trueTimer;
	
	private Random rand = new Random();
	private Image sprite;

	public Enemy(int wave) {
		this.wave = wave;
		vel = trueVel;
		spawn();
		initType(wave);
		alive = true;
		loadImage(direction);
	}
	
	/*
	 * uses a simple algorithm to act as artificial intelligence for enemy movement
	 * checks where the player is, and makes sure enemy x, y are within 50 pixels 
	 * of that, then as it approaches, the more narrow the range becomes until it 
	 * is required for the enemy to be within 10 pixels to allows for realistic movement
	 * 
	 * calls the walk() method to play walking sounds to add to immersion
	 * checks to see whether the enemy should be alive or not
	 */

	public void update(int pX, int pY) { // enemy movement, A.I.

		int tempDirection = direction;
		walk(); 

		cX = x + (width / 2);
		cY = y + (height / 2);

		if (health <= 0)
			alive = false;

		if (alive) {
			 if (((int) Math.abs(pX - cX) >= 60 || (int) Math.abs(pY - cY) >= 60)) {
				if (pX - 50 > cX) {
					x += vel;
					direction = 1;
				}
				if (pX + 50 < cX) {
					x -= vel;
					direction = 3;
				}
				if (pY - 50 > cY) {
					y += vel;
					direction = 2;
				}
				if (pY + 50 < cY) {
					y -= vel;
					direction = 0;
				}
			} else {
				if (pX - 10 > cX) {
					x += vel;
					direction = 1;
				}
				if (pX + 10 < cX) {
					x -= vel;
					direction = 3;
				}
				if (pY - 10 > cY) {
					y += vel;
					direction = 2;
				}
				if (pY + 10 < cY) {
					y -= vel;
					direction = 0;
				}
			}
		} else {
			x = -100;
			y = -100;
		}
		if (tempDirection != direction) {
			loadImage(direction);
		}
	}

	public boolean attack() {
		if (attackTimer == 0)
			return true;
		attackTimer--;
		return false;
	}
	
	/*
	 * randomly generates zombie roar sounds
	 * adds to ambience and makes the game 
	 * feel more immersive
	 * 
	 * 1/10000 chance of playing sound
	 */
	
	public void walk() {
		int temp = rand.nextInt(10000);
		if (temp == 1) {
			Sound.ZombieWalk1.stop();
			Sound.ZombieWalk1.play();
		}
		else if (temp == 11) {
			Sound.ZombieWalk2.stop();
			Sound.ZombieWalk2.play();
		}
		else if (temp == 111) {
			Sound.ZombieWalk3.stop();
			Sound.ZombieWalk3.play();
		}
	}
	
	/*
	 * similar to to walk() but generates
	 * much more frequently and only called when 
	 * enemy is shot
	 * 
	 * 1/15 - 1/10 chance of playing sound
	 */
	
	public void hurt() {
		int temp = rand.nextInt(300);
		if (temp < 20) {
			Sound.ZombieHurt1.stop();
			Sound.ZombieHurt1.play();
		}
		else if (temp < 40) {
			Sound.ZombieHurt2.stop();
			Sound.ZombieHurt2.play();
		}
		else if (temp < 70 && health<=1) {
			Sound.ZombieHurt3.stop();
			Sound.ZombieHurt3.play();
		}
	}
	
	/*
	 * sets enemy location to random location
	 * within a set range, only spawns beyond
	 * the edges of screen within 500 - 600
	 * pixels to cover before on screen
	 */

	public void spawn() {
		Random random = new Random();
		int side = random.nextInt(4);

		if (side == 0) { // top
			x = random.nextInt(590) + 1;
			y = -400;
		} else if (side == 1) { // bottom
			x = random.nextInt(590) + 1;
			y = 380 + 350;
		} else if (side == 2) { // left
			x = -350;
			y = random.nextInt(340) + 1;
		} else if (side == 3) { // right
			x = 590 + 350;
			y = random.nextInt(340) + 1;
		}
	}
	
	/*
	 * randomly sets type of enemy and initalizes 
	 * enemy stats based on type
	 * every 10 rounds, boss is forced to spawn
	 * randomly selects between 2 species of enemy
	 * which are differentiated by the animation design 
	 */

	public void initType(int wave) {
		Random random = new Random();
		int temp = random.nextInt(50) + 1;

		if (wave % 10 == 0) {
			Sound.Boss.loop();
			type = "super mutant";
			width = 80;
			height = 80;
			health = 80;
			trueVel = 3;
			trueTimer = 30 - (wave/10);
			species = temp % 2;
		} else if (temp > 0 && temp <= 40) {
			type = "chaser";
			width = 30;
			height = 30;
			health = 3;
			trueVel = 2;
			trueTimer = 50 - (wave/10);
			species = temp % 2;
		} else if (temp > 40 && temp <= 49) {
			type = "devourer";
			width = 40;
			height = 40;
			health = 6;
			trueVel = 1;
			trueTimer = 40 - (wave/10);
			species = temp % 2;
		} else if (temp == 50) {
			type = "colossal";
			width = 60;
			height = 60;
			health = 10;
			trueVel = 1;
			trueTimer = 40 - (wave/10);
			species = temp % 2;
		}
	}
	
	/*
	 * loads sprite for enemy 
	 * different images for each direction, type and species
	 * catches any exceptions in loading image files
	 */

	public void loadImage(int direction) {
		try {
			if (type.equals("chaser")) {
				if (species == 0) {
					if (direction == 0) {
						sprite = ImageIO.read(new File("Zombie-up.png"));
					} else if (direction == 1) {
						sprite = ImageIO.read(new File("Zombie-right.png"));
					} else if (direction == 2) {
						sprite = ImageIO.read(new File("Zombie-down.png"));
					} else if (direction == 3) {
						sprite = ImageIO.read(new File("Zombie-left.png"));
					}
				} else if (species == 1) {
					if (direction == 0) {
						sprite = ImageIO.read(new File("Zombie1-up.png"));
					} else if (direction == 1) {
						sprite = ImageIO.read(new File("Zombie1-right.png"));
					} else if (direction == 2) {
						sprite = ImageIO.read(new File("Zombie1-down.png"));
					} else if (direction == 3) {
						sprite = ImageIO.read(new File("Zombie1-left.png"));
					}
				}
			} else if (type.equals("colossal")) {
				if (species == 0) {
					if (direction == 0) {
						sprite = ImageIO.read(new File("Devourer-up.png"));
					} else if (direction == 1) {
						sprite = ImageIO.read(new File("Devourer-right.png"));
					} else if (direction == 2) {
						sprite = ImageIO.read(new File("Devourer-down.png"));
					} else if (direction == 3) {
						sprite = ImageIO.read(new File("Devourer-left.png"));
					}
				} else if (species == 1) {
					if (direction == 0) {
						sprite = ImageIO.read(new File("Devourer1-up.png"));
					} else if (direction == 1) {
						sprite = ImageIO.read(new File("Devourer1-right.png"));
					} else if (direction == 2) {
						sprite = ImageIO.read(new File("Devourer1-down.png"));
					} else if (direction == 3) {
						sprite = ImageIO.read(new File("Devourer1-left.png"));
					}
				}
			} else if (type.equals("devourer")) {
				if (species == 0) {
					if (direction == 0) {
						sprite = ImageIO.read(new File("Colossal-up.png"));
					} else if (direction == 1) {
						sprite = ImageIO.read(new File("Colossal-right.png"));
					} else if (direction == 2) {
						sprite = ImageIO.read(new File("Colossal-down.png"));
					} else if (direction == 3) {
						sprite = ImageIO.read(new File("Colossal-left.png"));
					}
				} else if (species == 1) {
					if (direction == 0) {
						sprite = ImageIO.read(new File("Colossal1-up.png"));
					} else if (direction == 1) {
						sprite = ImageIO.read(new File("Colossal1-right.png"));
					} else if (direction == 2) {
						sprite = ImageIO.read(new File("Colossal1-down.png"));
					} else if (direction == 3) {
						sprite = ImageIO.read(new File("Colossal1-left.png"));
					}
				}
			} else if (type.equals("super mutant")) {
				if (species == 0) {
					if (direction == 0) {
						sprite = ImageIO.read(new File("Devourer-up.png"));
					} else if (direction == 1) {
						sprite = ImageIO.read(new File("Devourer-right.png"));
					} else if (direction == 2) {
						sprite = ImageIO.read(new File("Devourer-down.png"));
					} else if (direction == 3) {
						sprite = ImageIO.read(new File("Devourer-left.png"));
					}
				} else if (species == 1) {
					if (direction == 0) {
						sprite = ImageIO.read(new File("Devourer1-up.png"));
					} else if (direction == 1) {
						sprite = ImageIO.read(new File("Devourer1-right.png"));
					} else if (direction == 2) {
						sprite = ImageIO.read(new File("Devourer1-down.png"));
					} else if (direction == 3) {
						sprite = ImageIO.read(new File("Devourer1-left.png"));
					}
				}
			}
		} catch (IOException ex) {
			System.out.println("Error reading enemy image: " + type);
		}
	}
	
	/*
	 * instructions to draw the health bar and sprite for enemy
	 * only used in the paint() method in Game class
	 */

	public void draw(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (alive) {
			g2d.drawImage(sprite, x, y, null);
			g2d.setColor(Color.red);
			if (type.equals("super mutant"))
				g2d.fillRect(x, y + height + 1, (7 * health) / 4, 4); 
			else
				g2d.fillRect((x), y + height + 1, (7 * health), 4);
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

	public int getWave() {
		return wave;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getHealth() {
		return health;
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

	public int getVel() {
		return vel;
	}

	public int getTrueVel() {
		return trueVel;
	}

	public int getTrueTimer() {
		return trueTimer;
	}

	public void setAttackTimer(int attackTimer) {
		this.attackTimer = attackTimer;
	}

	public void setWave(int wave) {
		this.wave = wave;
	}
	
	/*
	 * allows enemy to sustain damage and invokes 
	 * the hurt() method to use for sound effects
	 */

	public void setHealth(int health) {
		hurt();
		this.health = health;
	}

	public void setVel(int vel) {
		this.vel = vel;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}