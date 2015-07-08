import java.applet.*;

/*
 * Sound is handled by this class.
 * static variables are used so they can be called at 
 * any time by from class without the use of objects
 * 
 * if sound file not present in source folder, game will not run
 * no file exception handling 
 */

public class Sound {
 
 public static final AudioClip Menu = Applet.newAudioClip(Sound.class.getResource("title_music.wav"));
 public static final AudioClip Game = Applet.newAudioClip(Sound.class.getResource("game_music.wav"));
 public static final AudioClip Boss = Applet.newAudioClip(Sound.class.getResource("boss_music.wav"));
 
 public static final AudioClip Click = Applet.newAudioClip(Sound.class.getResource("click.wav"));
 public static final AudioClip PlayerWalk = Applet.newAudioClip(Sound.class.getResource("player_walk.wav"));
 
 public static final AudioClip ZombieHurt1 = Applet.newAudioClip(Sound.class.getResource("zombie_hurt1.wav"));
 public static final AudioClip ZombieHurt2 = Applet.newAudioClip(Sound.class.getResource("zombie_hurt2.wav"));
 public static final AudioClip ZombieHurt3 = Applet.newAudioClip(Sound.class.getResource("zombie_hurt3.wav"));
 public static final AudioClip ZombieWalk1 = Applet.newAudioClip(Sound.class.getResource("zombie_walk1.wav"));
 public static final AudioClip ZombieWalk2 = Applet.newAudioClip(Sound.class.getResource("zombie_walk2.wav"));
 public static final AudioClip ZombieWalk3 = Applet.newAudioClip(Sound.class.getResource("zombie_walk3.wav"));
 
 public static final AudioClip PistolShoot = Applet.newAudioClip(Sound.class.getResource("pistol_shot.wav"));
 public static final AudioClip MagnumShoot = Applet.newAudioClip(Sound.class.getResource("magnum_shot.wav"));
 public static final AudioClip ShotgunShoot = Applet.newAudioClip(Sound.class.getResource("shotgun_shot.wav"));
 public static final AudioClip M16Shoot = Applet.newAudioClip(Sound.class.getResource("m16_shot.wav"));
 
 public static final AudioClip PistolReload = Applet.newAudioClip(Sound.class.getResource("pistol_reload.wav"));
 public static final AudioClip ShotgunReload= Applet.newAudioClip(Sound.class.getResource("shotgun_reload.wav"));
 public static final AudioClip MagnumReload = Applet.newAudioClip(Sound.class.getResource("magnum_reload.wav"));
 public static final AudioClip M16Reload = Applet.newAudioClip(Sound.class.getResource("m16_reload.wav"));
 
 public static final AudioClip HealthPickup = Applet.newAudioClip(Sound.class.getResource("health_pickup.wav"));
 public static final AudioClip MagnumPickup = Applet.newAudioClip(Sound.class.getResource("magnum_pickup.wav"));
 public static final AudioClip ShotgunPickup = Applet.newAudioClip(Sound.class.getResource("shotgun_pickup.wav"));
 public static final AudioClip M16Pickup = Applet.newAudioClip(Sound.class.getResource("m16_pickup.wav"));
 
}