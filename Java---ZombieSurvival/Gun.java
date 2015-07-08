import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;

public class Gun {

	private int frameX, frameY;
	private int velX, velY, vel;
	private int x, y, radius = 5;
	private String name;
	private int type;
	private int ammo, maxAmmo, clip;
	private int reloadTimer, tempTimer;
	private int damage;
	private int spray;
	private int range;
	private int direction;
	private boolean projectile;
	private boolean isReloading;
	
	/*
	 * constructor requires a type to set which type of
	 * gun to equip, and requires frame x, y variables
	 * to act as boundaries for projectile paths
	 */

	public Gun(int type, int frameX, int frameY) {
		this.frameX = frameX;
		this.frameY = frameY;
		this.type = type;
		setType(type);
	}
	
	/*
	 * based on the type of the gun, sets all
	 * stats that effect how the gun behaves and 
	 * is used
	 * if method is called, reloading is set to false
	 * if method is called, reloading sound is killed
	 */

	public void setType(int type) {
		this.type = type;
		isReloading = false;

		if (type == 0) { // Pistol
			name = "Pistol";
			vel = 40;
			damage = 1;
			range = 200;
			spray = 1;
			clip = 30;
			maxAmmo = clip;
			ammo = maxAmmo;
			reloadTimer = 110;
			tempTimer = reloadTimer;
		} else if (type == 1) { // Magnum
			name = "Magnum";
			vel = 40;
			damage = 2;
			range = 500;
			spray = 3;
			clip = 75;
			maxAmmo = clip / 3;
			ammo = maxAmmo;
			reloadTimer = 155;
			tempTimer = reloadTimer;
		} else if (type == 2) { // M16
			name = "M16";
			vel = 120;
			damage = 2;
			range = 300;
			spray = 1;
			clip = 90;
			maxAmmo = clip / 3;
			ammo = maxAmmo;
			reloadTimer = 145;
			tempTimer = reloadTimer;
		} else if (type == 3) { // Spaz
			name = "Spaz";
			vel = 20;
			damage = 3;
			range = 150;
			spray = 4;
			clip = 45;
			maxAmmo = clip / 3;
			ammo = maxAmmo;
			reloadTimer = 200;
			tempTimer = reloadTimer;
		}
		
		if (type!=0)
			stopReloadEffect();
	}
	
	/*
	 * checks to see difference in mag ammo and
	 * max capacity of mag, then subtracts from the required
	 * ammo to fill the mag from the clip
	 * only can reload if clip is not empty, if mag is not full, 
	 * if game not paused 
	 * 
	 * reloading sounds are played when reloading, last for duration
	 * that matches the gun reloading duration for added realism
	 * 
	 * if gun is fully depleted, gun equipped is defaulted back to 
	 * the pistol, pistol has infinite ammo
	 */

	public void reload() {
		if (clip==0 && ammo==0 && reloadTimer!=110) {
			reloadTimer = 110;
			tempTimer = reloadTimer;
		}
		if (type==2 && ammo==0)
			Sound.M16Shoot.stop();
		isReloading = true;
		if (tempTimer == reloadTimer) {
			if (type == 0 || clip == 0)
				Sound.PistolReload.play();
			else if (type == 1)
				Sound.MagnumReload.play();
			else if (type == 2)
				Sound.M16Reload.play();
			else if (type == 3)
				Sound.ShotgunReload.play();
		}
		if (tempTimer == 0) {
			if (clip == 0 && ammo == 0 || type == 0) {
				setType(0);
			}
			else {
				int oldClip = clip;
				clip = clip - (maxAmmo - ammo);
				if (clip < 0) {
					clip = 0;
					ammo = ammo + (oldClip - clip);
				} else
					ammo = maxAmmo;
				tempTimer = reloadTimer;
			}
			isReloading = false;
		} else
			tempTimer--;
	}
	
	/*
	 * depletes ammo when fired, invoked the method
	 * to play gunshot sounds and sets the state of
	 * the projectile to false to reset its location 
	 * to the player
	 */

	public void reset() {
		ammo--;
		soundEffect();
		projectile = false;
	}
	
	/*
	 * plays firing sound effect based on which gun
	 * is equipped and is only called if projectile 
	 * reaches maximum range specific for that gun
	 */
	
	public void soundEffect() {
		if (type == 0) {
			Sound.PistolShoot.stop();
			Sound.PistolShoot.play();
		} else if (type == 1) {
			Sound.MagnumShoot.stop();
			Sound.MagnumShoot.play();
		} else if (type == 2) {
			Sound.M16Shoot.stop();
			Sound.M16Shoot.play();
		} else if (type == 3) {
			Sound.ShotgunShoot.stop();
			Sound.ShotgunShoot.play();
		}
	}
	
	/*
	 * kills any reloading sounds when gun is swapped 
	 * or when game is restarted
	 */
	
	public void stopReloadEffect() {
		Sound.PistolReload.stop();
		Sound.MagnumReload.stop();
		Sound.M16Reload.stop();
		Sound.ShotgunReload.stop();
	}
	
	/*
	 * takes the player x, y location as input
	 * sets the loaction of the bullet to be underneath 
	 * the player when not firing, resets the player after
	 * each shot
	 * controls the velocity of projectile
	 * cuts projectile path off when it reaches edge of screen
	 */

	public void update(int px, int py) {
		if (ammo <= 0 || isReloading)
			reload();
		if (px - x >= range || x - px >= range || py - y >= range
				|| y - py >= range)
			reset();
		if (direction == 2 && y >= 340 && projectile)
			reset();
		if (projectile) {
			this.x += velX;
			this.y += velY;
		} else if (!projectile) {
			this.x = px + 8;
			this.y = py + 8;
			velX = 0;
			velY = 0;
		}
		if (x >= frameX || x <= 0 || y >= frameY || y <= 0) {
			reset();
		}
	}
	
	/*
	 * instructions to draw the projectile images based on 
	 * the type of gun being fired 
	 * can only be invoked in the Game class
	 */

	public void draw(Graphics2D g2d) {

		g2d.setColor(Color.BLACK);
		if (type == 0)
			g2d.fillOval(x, y, radius * spray, radius * spray);
		else if (type == 1) {
			g2d.setColor(Color.RED);
			g2d.fillOval(x, y, radius * spray, radius * spray);
		} else if (type == 2) {
			g2d.setColor(Color.BLUE);
			if (direction == 1 || direction == 3)
				g2d.fillOval(x, y, (radius * 3), (radius * 3) / 4);
			else
				g2d.fillOval(x, y, (radius * 3) / 4, (radius * 3));
		} else if (type == 3) {
			g2d.setColor(Color.DARK_GRAY);
			g2d.fillRect(x, y, radius * spray, radius * spray);
		}
	}

	/*
	 * similar to the keyReleased() method
	 * only used for when the m16 is equipped 
	 * to allow for fully automatic firing of weapon
	 * due to the nature of the keyPressed() method
	 */
	
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_R) {
			if (clip != 0 && ammo != maxAmmo)
				reload();
		}
		if (ammo > 0 && !isReloading) {
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				direction = 3;
				projectile = true;
				velX = -vel;
			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				direction = 1;
				projectile = true;
				velX = vel;
			} else if (e.getKeyCode() == KeyEvent.VK_UP) {
				direction = 0;
				projectile = true;
				velY = -vel;
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				direction = 2;
				projectile = true;
				velY = vel;
			}
		} else {
			if (e.getKeyCode() == KeyEvent.VK_LEFT)
				direction = 3;
			else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				direction = 1;
			else if (e.getKeyCode() == KeyEvent.VK_UP)
				direction = 0;
			else if (e.getKeyCode() == KeyEvent.VK_DOWN)
				direction = 2;
		}
	}
	
	/*
	 * checks if key is released
	 * if key trigger keys are released, gun is fired 
	 * projectile is set to true and projectile vel 
	 * is set along with it's direction for use 
	 * in the player class
	 * 
	 * if gun is being reloaded, direction is changed to draw image
	 * but the gun is not fired and the state of the projectile 
	 * is kept the same
	 * 
	 * if ammo is not full and clip is no empty, reload
	 * actuated on "R" key 
	 */

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_R) {
			if (clip != 0 && ammo != maxAmmo)
				reload();
		}
		if (ammo > 0 && !isReloading) {
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				direction = 3;
				projectile = true;
				velX = -vel;
			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				direction = 1;
				projectile = true;
				velX = vel;
			} else if (e.getKeyCode() == KeyEvent.VK_UP) {
				direction = 0;
				projectile = true;
				velY = -vel;
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				direction = 2;
				projectile = true;
				velY = vel;
			}
		} else {
			if (e.getKeyCode() == KeyEvent.VK_LEFT)
				direction = 3;
			else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				direction = 1;
			else if (e.getKeyCode() == KeyEvent.VK_UP)
				direction = 0;
			else if (e.getKeyCode() == KeyEvent.VK_DOWN)
				direction = 2;
		}
	}
	
	/*
	 * returns a rectangle object that can be used to 
	 * check for intersections and overlap in the 
	 * Game class
	 */

	public Rectangle getBounds() {
		return new Rectangle(x, y, radius, radius);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	public int getDirection() {
		return direction;
	}

	public int getAmmo() {
		return ammo;
	}

	public int getMaxAmmo() {
		return maxAmmo;
	}

	public int getClipSize() {
		return clip;
	}

	public int getReloadTimer() {
		return reloadTimer;
	}

	public boolean getIsReloading() {
		return isReloading;
	}

	public int getTempTimer() {
		return tempTimer;
	}

	public int getDamage() {
		return damage;
	}

	public int getRange() {
		return range;
	}

	public int getSpray() {
		return spray;
	}

	public boolean getProjectile() {
		return projectile;
	}

	public void setAmmo(int ammo) {
		this.ammo = ammo;
	}
}
