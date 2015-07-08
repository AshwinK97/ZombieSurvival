/**
 * Authors: Cole McGinnis, Stefen Cross, Ashwin Kannan
 * Description: top down shooter with zombie survival theme
 * Date last edited: June 15, 2015
 */

/********************* Update Log ***********************************
 * 
 * - added basic interface with health bar
 * - enemy AI, follows player within certain vicinity
 * - gun class with single projectile
 * - projectiles fire using arrow keys
 * - single enemy re-spawns at random location
 * - health packs spawn at random locations
 * - enemies are handled with array + different types
 * - basic sprite for player based on movement direction
 * - different types of enemies, boss rounds
 * - gun drops and reload timers added
 * - variable wave sizes for enemies and increased difficulty
 * - waveSize indicates the actual wave size -1
 * - implemented pause button and force reload button
 * - sound class with sound effects and background music
 * 
 *********************************************************************/

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;
import java.awt.Image;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
class Game extends JPanel {

	private final static int frameX = 640;
	private final static int frameY = 480;
	private final int trueDrawTimer = 100;
	private int timesPlayed = -1;
	private int score, wave, waveSize, lastScore, lastWave;
	private int drawTimer = trueDrawTimer;
	private int mouseX, mouseY, clickX, clickY;
	private boolean but1, but2, but3;
	private boolean isEnemyAttack;
	private boolean isPause, isMenuOpen, isPlay, isInstructions;
	private Random random = new Random();
	private Image background;

	private Player player;
	private Enemy[] enemy;
	private Gun gun;
	private Item healthPack;
	private Item gunDrop;
	
	/*
	 * Constructor creates mouse and keyboard listeners 
	 * calls the initialize() method that resets all parameters
	 */

	public Game() {
		
		addMouseMotionListener(new MouseMotionListener() { // for button illumination

			public void mouseMoved(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
				menu(0, 0, mouseX, mouseY);
			}

			public void mouseDragged(MouseEvent arg0) {
			}
		});
		
		initialize(); // sets up game

		addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				if (!isPause) {
					player.keyReleased(e);
					if (gun.getType() != 2)
						gun.keyPressed(e);
				}
			}
			
			/*
			 * contains pause button, allows user to pause game while playing
			 * actuates on the "P" key or the "esc" key
			 * stops drawing entities and instead draws a message to alert player
			 * that the game is paused, when un-paused draws the round the player 
			 * was last on to give user an indication as to their progress
			 */

			public void keyPressed(KeyEvent e) {
				if (!isPause) {
					player.keyPressed(e);
					if (gun.getType() == 2)
						gun.keyPressed(e);
				}
				if ((e.getKeyCode() == KeyEvent.VK_P || e.getKeyCode() == KeyEvent.VK_ESCAPE)
						&& !gun.getIsReloading() && !isMenuOpen && !isInstructions) {
					isPause = !isPause;
					player.setVelX(0);
					player.setVelY(0);
					if (!isPause)
						drawTimer = 100;
					Sound.Click.play();
					Sound.PlayerWalk.stop();
				}
			}
		});
		addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
			}
			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}
			public void mousePressed(MouseEvent e) {
				clickX = e.getX();
				clickY = e.getY();
				menu(clickX, clickY, mouseX, mouseY);
			}
			public void mouseReleased(MouseEvent arg0) {
			}
		});
		setFocusable(true);
	}
	
	/*
	 * resets all game variables and closes all stages except for the menu
	 * sets a temporary score and wave variable to the previous wave and score
	 * before initalizing the new score and wave so that it can be shown on screen
	 * when during the next game over
	 * 
	 * creates all objects of the entities in the game
	 * stops the game music and starts the menu music
	 * loads background for menu
	 */
	
	public void initialize() {
		Sound.Game.stop();
		Sound.Menu.play();
		loadImage();
		
		timesPlayed++;
		waveSize = 5;
		lastScore = score;
		lastWave = wave;
		score = 0;
		wave = 0;
		isPlay = false;
		isPause = false;
		isInstructions = false;
		but1 = false;
		but2 = false;
		but3 = false;
		isMenuOpen = true;

		player = new Player();
		enemy = new Enemy[waveSize];
		generateEnemy();
		gun = new Gun(0, frameX, frameY);
		healthPack = new Item(-1, -100, -100);
		gunDrop = new Item(-1, -150, -150);
	}
	
	/*
	 * parameters: the value of the mouse's x, y locations, determined 
	 * by the mouseMotionListener and the x, y value of where the mouse 
	 * is clicked, determined by the mouseListener
	 * 
	 * using mouseX mouseY, checks whether player is hovering over a button or not
	 * using clickX, clickY, checks whether player has clicked button or not
	 * plays sound when button is clicked and changes the game stage
	 * 
	 * locations are predetermined and set
	 * if button is hovered over, boolean set to true and used in paint method
	 * to illuminate the button for more interactivity
	 */

	public void menu(int clickX, int clickY, int mouseX, int mouseY) {
		if (isMenuOpen) {
			if (clickX >= frameX / 2 - 100 && clickX <= frameX / 2 + 100
					&& clickY >= frameY / 4 - 25 && clickY <= frameY / 4 + 75) {
				Sound.Click.stop();
				Sound.Click.play();
				Sound.Menu.stop();
				Sound.Game.play();
				isMenuOpen = false;
				isPlay = true;
			}
			if (clickX >= frameX / 2 - 100 && clickX <= frameX / 2 + 100
					&& clickY >= frameY / 2 + 25 && clickY <= frameY / 2 + 125) {
				Sound.Click.stop();
				Sound.Click.play();
				isMenuOpen = false;
				isInstructions = true;
			}
			if (mouseX >= frameX / 2 - 100 && mouseX <= frameX / 2 + 100
					&& mouseY >= frameY / 4 - 25 && mouseY <= frameY / 4 + 75) {
				but1 = true;
			}
			else if (mouseX >= frameX / 2 - 100 && mouseX <= frameX / 2 + 100
					&& mouseY >= frameY / 2 + 25 && mouseY <= frameY / 2 + 125) {
				but2 = true;
			}
			else {
				but1 = false;
				but2 = false;
				but3 = false;
			}
		} else if (isInstructions) {
			if (clickX >= frameX / 2 + 100 && clickX <= frameX / 2 + 300
					&& clickY >= frameY / 2 + 100 && clickY <= frameY / 2 + 200) {
				Sound.Click.stop();
				Sound.Click.play();
				isInstructions = false;
				isMenuOpen = true;
			}
			if (mouseX >= frameX / 2 + 100 && mouseX <= frameX / 2 + 300
					&& mouseY >= frameY / 2 + 100 && mouseY <= frameY / 2 + 200) {
				but3 = true;
			}
			else {
				but1 = false;
				but2 = false;
				but3 = false;
			}
		}
	}
	
	/*
	 * checks if enemy makes contact with player
	 * based on the x, y of player and x, y +- the width and height
	 * of the enemy
	 * 
	 * if player is hit, enemy attack timer begins to countdown
	 * when countdown = 0, player health is reduced by 1 and 
	 * attack timer is reset, this restricts the enemies to only
	 * attack at a specific rate based on their attackTimer
	 */

	public void checkEnemyCollisions() {
		for (int i = 0; i < enemy.length; i++) {
			if (player.getBounds().intersects(enemy[i].getBounds())) {
				enemy[i].setVel(0);
				setIsEnemyAttack(enemy[i].attack());
				if (enemy[i].attack() == true) {
					player.takeDamage(1);
					enemy[i].setAttackTimer(enemy[i].getTrueTimer());
				}
			} else
				enemy[i].setVel(enemy[i].getTrueVel());
		}
	}
	
	/*
	 * checks if gun projectiles make contact with enemies
	 * m16 is given its seperate range for accuracy to allow
	 * the player to spray and be less accurate with shots
	 * contains standard accuracy test to see if projectile 
	 * makes contact with enemy
	 * 
	 * if enemy is shot, health is reduced
	 * if enemy is killed, score increased, enemy[i].setState(false);
	 */

	public void checkEnemyShot() {
		for (int i = 0; i < enemy.length; i++) {
			if (gun.getType() == 2) { // to detect the high fire rate weapons
				if (((gun.getX() >= enemy[i].getX() - 50) && (gun.getX() <= enemy[i]
						.getX() + enemy[i].getWidth() + 50))
						&& ((gun.getY() >= enemy[i].getY() - 50) && (gun.getY() <= enemy[i]
								.getY() + enemy[i].getHeight() + 50))) {
					if (gun.getProjectile()) {
						enemy[i].setHealth(enemy[i].getHealth()
								- gun.getDamage());
						if (enemy[i].getHealth() <= 0 && enemy[i].getAlive())
							score++;
						gun.reset();
					}
				}
			} else if (((gun.getX() >= enemy[i].getX() - 10) && (gun.getX() <= enemy[i]
					.getX() + enemy[i].getWidth() + 10))
					&& ((gun.getY() >= enemy[i].getY() - 10) && (gun.getY() <= enemy[i]
							.getY() + enemy[i].getHeight() + 10))) {
				if (gun.getProjectile()) {
					enemy[i].setHealth(enemy[i].getHealth() - gun.getDamage());
					if (enemy[i].getHealth() <= 0 && enemy[i].getAlive())
						score++;
					gun.reset();
				}
			}
		}
	}
	
	/*
	 * checks if player makes contact with a drop item
	 * health drops and gun drops handled separately 
	 * in case of spawn overlap
	 */

	public void checkItem() {
		if (player.getBounds().intersects(healthPack.getBounds())
				&& healthPack.getState()) {
			player.setHealth(1);
			healthPack.soundEffect();
			healthPack.setState(false);
		}
		if (player.getBounds().intersects(gunDrop.getBounds())
				&& gunDrop.getState()) {
			gun.setType(gunDrop.getIndex());
			gunDrop.soundEffect();
			gunDrop.setState(false);
		}
	}
	
	/*
	 * Randomly generates items
	 * health pack - 50% chance
	 * magnum and shotgun - 10% chance
	 * m15 - 20% chance
	 */

	public void generateItem() {
		int temp = random.nextInt(2) + 1;
		if (temp == 1) {
			healthPack = new Item(0, random.nextInt(590) + 1,
					random.nextInt(340) + 1);
		}
		temp = random.nextInt(100);
		if (temp < 10) {
			gunDrop = new Item(1, random.nextInt(590) + 1,
					random.nextInt(340) + 1);
		} else if (temp < 20) {
			gunDrop = new Item(3, random.nextInt(590) + 1,
					random.nextInt(340) + 1);
		} else if (temp < 40) {
			gunDrop = new Item(2, random.nextInt(590) + 1,
					random.nextInt(340) + 1);
		}
	}
	
	/*
	 * Creates new enemies once all enemies from previous wave are gone
	 * generates a enemy every 10 rounds
	 * generateItem() invoked here to generate items along with enemies 
	 * at the start of each round
	 */

	public void generateEnemy() {
		Sound.Boss.stop();
		generateItem();
		wave++;
		if (wave == 1 || (wave >= 10 && wave % 10 == 0))
			waveSize = 5;
		else if (wave >= 2 && wave % 2 == 0)
			waveSize++;
		drawTimer = trueDrawTimer;
		if (wave % 10 == 0) {
			enemy = new Enemy[1];
			enemy[0] = new Enemy(wave);
		} else {
			enemy = new Enemy[waveSize];
			for (int k = 0; k < enemy.length; k++) {
				enemy[k] = new Enemy(wave);
			}
		}
	}
	
	/*
	 * Main game loop is here, all variables pass through atleast once
	 * checks states, collisions, how far player has progressed 
	 */

	// ///////////***GAME LOOP***////////////
	private void update() {

		if (isPlay) {
			if (player.getAlive() && !isPause) {
				checkEnemyCollisions();
				checkEnemyShot();
				checkItem();
				if (gun.getProjectile())
					player.update(true, gun.getDirection(), gun.getType());
				else
					player.update(false, -1, gun.getType());
				gun.update(player.getX(), player.getY());
				for (int i = 0; i < enemy.length; i++) {
					if (enemy[i].getAlive())
						enemy[i].update(player.getX(), player.getY());
				}
			}
			int kills = 0;
			for (int i = 0; i < enemy.length; i++) {
				if (!enemy[i].getAlive()) {
					kills++;
				}
				if (kills == enemy.length)
					generateEnemy();
			}
			if (!player.getAlive()) {
				stopSoundEffect();
				initialize();
			}
		}
	}
	// ///////////***GAME LOOP***////////////
	
	/*
	 * used to preload images like backgrounds
	 * catches exception if image not found
	 */

	public void loadImage() {
		try {
			background = ImageIO.read(new File("Background.png"));
		} catch (IOException ex) {
			System.out.println("Error reading background");
		}
	}
	
	/*
	 * Kills any sound that may remain playing from a previous section
	 */
	
	public void stopSoundEffect() {
		Sound.Boss.stop();
		Sound.PistolReload.stop();
		Sound.MagnumReload.stop();
		Sound.M16Reload.stop();
		Sound.ShotgunReload.stop();
		Sound.HealthPickup.stop();
	}
	
	/*
	 * All graphics are drawn here
	 * each entity contains a separate draw() method
	 * methods are then invoked here for easy access
	 * anti-aliasing used to smooth edges
	 * method will only paint if:
	 * - menu open
	 * - game open
	 * - help open
	 */

	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		super.paint(g);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setFont(new Font("Arial", Font.BOLD, 14));

		if (isMenuOpen) {
			g2d.drawImage(background, 0, 0, null);
			if (but1) {
				g2d.setColor(Color.WHITE);
				g2d.fillRect(frameX / 2 - 100, frameY / 4 - 25, 200, 100);
				g2d.setColor(Color.BLACK);
				g2d.setFont(new Font("Times New Roman", Font.BOLD, 48));
				g2d.drawString("PLAY", frameX / 2 - 65, frameY / 4 + 37);
			}
			else if (!but1) {
				g2d.setColor(Color.BLACK);
				g2d.fillRect(frameX / 2 - 100, frameY / 4 - 25, 200, 100);
				g2d.setColor(Color.WHITE);
				g2d.setFont(new Font("Times New Roman", Font.BOLD, 48));
				g2d.drawString("PLAY", frameX / 2 - 65, frameY / 4 + 37);
			}
			if (but2) {
				g2d.setColor(Color.WHITE);
				g2d.fillRect(frameX / 2 - 100, frameY / 2 + 25, 200, 100);
				g2d.setColor(Color.BLACK);
				g2d.setFont(new Font("Times New Roman", Font.BOLD, 48));
				g2d.drawString("HELP", frameX / 2 - 65, frameY / 2 + 90);
			}
			else if (!but2) {
				g2d.setColor(Color.BLACK);
				g2d.fillRect(frameX / 2 - 100, frameY / 2 + 25, 200, 100);
				g2d.setColor(Color.WHITE);
				g2d.setFont(new Font("Times New Roman", Font.BOLD, 48));
				g2d.drawString("HELP", frameX / 2 - 65, frameY / 2 + 90);
			}
			if (timesPlayed>0) {
				g2d.setColor(Color.BLACK);
				g2d.setFont(new Font("Times New Roman", Font.BOLD, 24));
				g2d.drawString("Last Played", 470, 365);
				g2d.drawString("__________", 470, 365);
				g2d.drawString("Score: " + lastScore * 100, 452, 400); // stats
				g2d.drawString("Wave: " + lastWave, 450, 430);
			}
		}

		else if (isInstructions) {
			g2d.drawImage(background, 0, 0, null);
			g2d.setColor(Color.BLACK);
			g2d.drawRect(65, 50, 525, 295);
			g2d.setFont(new Font("Times New Roman", Font.BOLD, 48));
			if (but3) {
				g2d.setColor(Color.WHITE);
				g2d.fillRect(frameX / 2 + 100, frameY / 2 + 100, 200, 100);
				g2d.setColor(Color.BLACK);
				g2d.drawString("BACK", frameX / 2 + 130, frameY / 2 + 164);
			}
			else if (!but3){
				g2d.setColor(Color.BLACK);
				g2d.fillRect(frameX / 2 + 100, frameY / 2 + 100, 200, 100);
				g2d.setColor(Color.WHITE);
				g2d.drawString("BACK", frameX / 2 + 130, frameY / 2 + 164);
			}
			g2d.setColor(Color.BLACK);
			g2d.drawString("WASD keys to move", 80, 100);
			g2d.drawString("Arrows keys to shoot", 80, 155);
			g2d.drawString("R to reload, P to pause", 80, 210);
			g2d.drawString("Pick up health and guns", 80, 265);
			g2d.drawString("Kill moar zaambies!!!11", 80, 320);
		}

		else if (isPlay) {
			g2d.drawImage(background, 0, 0, null);

			if (player.getAlive()) {
				gunDrop.draw(g2d);
				healthPack.draw(g2d);
				for (int i = 0; i < enemy.length; i++)
					// enemies
					enemy[i].draw(g2d);

				g2d.setColor(Color.DARK_GRAY);
				g2d.fillRect(0, 380, 640, 100); // interface

				if (gun.getProjectile())
					gun.draw(g2d); // bullets
				player.draw(g2d);

				g2d.setColor(Color.white); // ammo counter
				g2d.drawString("Ammo: ", 2, 430);
				g2d.drawRect(52, 415, gun.getMaxAmmo() * 10, 20);
				if (!gun.getIsReloading()) {
					g2d.setColor(Color.YELLOW);
					g2d.fillRect(55, 418, (gun.getAmmo() * 10) - 5, 15);
					g2d.setColor(Color.BLACK);
					g2d.drawString(gun.getAmmo() + "/" + gun.getClipSize(),
							(gun.getMaxAmmo() * 10) / 2 + 35, 430);
				} else {
					g2d.setFont(new Font("Arial", Font.BOLD, 14));
					g2d.setColor(Color.YELLOW);
					g2d.drawString("Reloading: " + gun.getTempTimer() / 30,
							(gun.getMaxAmmo() * 10) / 2 + 10, 430);
				}

				g2d.setColor(Color.white);
				g2d.drawString("Score: " + score * 100, 540, 400); // stats
				g2d.drawString("Wave: " + wave, 540, 415);

				if (drawTimer > 0) {
					g2d.setFont(new Font("Times New Roman", Font.BOLD, 16));
					g2d.setColor(Color.BLACK);
					if (wave % 10 == 0) {
						g2d.drawRect(frameX / 2 - 40, frameY / 2 - 42, 108, 25);
						g2d.drawString("Boss Round", frameX / 2 - 25,
								frameY / 2 - 25);
					} else {
						g2d.drawRect(frameX / 2 - 40, frameY / 2 - 42, 95, 25);
						g2d.drawString("Wave: " + wave, frameX / 2 - 25,
								frameY / 2 - 25);
					}
					drawTimer--;
				}

				if (isPause) {
					g2d.setFont(new Font("Times New Roman", Font.BOLD, 18));
					g2d.setColor(Color.BLACK);
					g2d.drawString("Game Paused", frameX / 2 - 40, 25);
				}
			}
		}
	}
	
	/*
	 * frame is created, parameters for frame are set
	 * frame's default location is in the center
	 * frame will always be on top of other windows
	 * 
	 * Game object is created, added as component to frame
	 * loop is created with a thread to slow down processing
	 * and allow for smoother gameplay and graphics
	 */

	public static void main(String args[]) throws InterruptedException {
		JFrame frame = new JFrame("12 Nights in PHS");
		frame.setSize(frameX, frameY);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setAlwaysOnTop(true);
		
		Game game = new Game();
		frame.add(game);
		frame.setVisible(true);
		while (true) {
			game.update();
			game.repaint();
			Thread.sleep(15);
		}
	}

	public boolean getIsEnemyAttack() {
		return isEnemyAttack;
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public int getClickX() {
		return clickX;
	}

	public int getClickY() {
		return clickY;
	}

	public void setIsEnemyAttack(boolean isEnemyAttack) {
		this.isEnemyAttack = isEnemyAttack;
	}
}