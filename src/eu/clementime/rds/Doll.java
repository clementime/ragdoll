package eu.clementime.rds;

import static eu.clementime.rds.Constants.DEFAULT_IMAGE;
import static eu.clementime.rds.Constants.DIRECTION_LEFT;
import static eu.clementime.rds.Constants.DIRECTION_RIGHT;
import static eu.clementime.rds.Constants.STATUS_ACTION;
import static eu.clementime.rds.Constants.ZINDEX_DOLL;
import static eu.clementime.rds.Constants.LOG_ON;
import static eu.clementime.rds.Constants.SCALE_POSITION;


import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
* Everything about our sweet tiny little doll.
* @author Cl&eacute;ment
* @version 1.0
*/
public class Doll {
	
	public AnimatedSprite walking;
	public AnimatedSprite idle;
	private BitmapTextureAtlas dollBTA;
	private BitmapTextureAtlas dollIdleBTA;
	private TiledTextureRegion dollTR;
	private TiledTextureRegion dollIdleTR;
	
	public PhysicsHandler ph;

	/**
	 * For each screen, the doll can move on a flat line, or a little bit inclined line; this is set in database.
	 */	
	public float standardYVelocityRight;
	/**
	 * Left velocity is negative of right velocity. Don't ask me why there are 2 variables. Found it clearer, I guess.
	 */	
	public float standardYVelocityLeft;
	/**
	 * Used if the velocity is changed in relation to standard doll velocity in the screen; for animation purpose.
	 */	
	public float YVelocity = 0;
	
	/**
	 * x position of doll walking centre from its own left side (not from left side of camera or background).
	 */	
	public float staticCenterX;
	
	public int walkDirection;
	public boolean isChased = true;
	
	private DatabaseAccess db;
	private Context context;
	
	public boolean justStarted = true; // avoid a strange behaviour of AndEngine: latency when first setVisible(true) of walking image
	
	/**
	 * For logs only.
	 */	
	private String className = "Doll";
	
	/**
	 * Loads images and creates every sprites, attach them to the AndEngine scene, register touch areas and set ZIndexes.
	 * @param	dbh		database handler is stored for upcoming database calls
 	 * @param	context	Android context, to retrieve files
	 * @param	engine	AndEngine engine, to load textures
	 * @param	scene	AndEngine scene, to attach sprites to scene and register touch areas
	 */	
	public Doll(DatabaseHandler dbh, Context context, Engine engine, Scene scene) {
		
		this.db = new DatabaseAccess(dbh);
		this.context = context;
		
		//dollBTA = new BitmapTextureAtlas(512, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		dollBTA = new BitmapTextureAtlas(2048, 2048, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		dollIdleBTA = new BitmapTextureAtlas(2048, 2048, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		dollTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(dollBTA, context, R.drawable.doll_skin_1, 0, 0, 4, 4);
		dollIdleTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(dollIdleBTA, context, R.drawable.doll_idle_1, 0, 0, 4, 4);
		
		engine.getTextureManager().loadTextures(dollBTA, dollIdleBTA);
		
		walking = new AnimatedSprite(0, 0, dollTR);
		idle = new AnimatedSprite(0, 0, dollIdleTR);

		//idle.stopAnimation(0);
			
		ph = new PhysicsHandler(walking);
		walking.registerUpdateHandler(ph);
		
		scene.attachChild(walking);
		scene.attachChild(idle);
		walking.setZIndex(ZINDEX_DOLL);
		idle.setZIndex(ZINDEX_DOLL);
		
		scene.registerTouchArea(walking);
		scene.registerTouchArea(idle);
		
		this.staticCenterX = walking.getWidth()/2;	
	}
	/**
	 * Launches doll sprite animation and move of doll until a point or item/area, etc...
	 * @param	status		is it normal playing or is an animation running ? (in this case, y velocity is changed) 
 	 * @param	touchedX	x position the doll must move until (if not stopped by boundaries)
	 */	
	public void move(int status, float touchedX) {
		
		if (LOG_ON) Log.i("Clementime", className + "/move()");

		idle.setVisible(false);
		
		// !!!! justStarted avoid a strange behaviour of AndEngine: latency when first setVisible(true) of walking image		
		if (justStarted) {
			justStarted = false;
			walking.setPosition(idle); 
		}
		else walking.setVisible(true);
		
		// if doll wasn't previously walking, launch walking animation	
		if (ph.getVelocityX() == 0) {
			
			if (touchedX > walking.getX()) {
				walkDirection = DIRECTION_RIGHT;
				walking.animate(new long[]{120, 120, 120, 120, 120, 120, 120, 120}, 0, 7, true);	
				if (status == STATUS_ACTION || YVelocity == -1000) ph.setVelocity(70, standardYVelocityRight);
				else ph.setVelocity(70, YVelocity);
			}
			else if (touchedX < walking.getX()) {
				walkDirection = DIRECTION_LEFT;
				walking.animate(new long[]{120, 120, 120, 120, 120, 120, 120, 120}, 8, 15, true);				
				if (status == STATUS_ACTION || YVelocity == -1000) ph.setVelocity(-70, standardYVelocityLeft);
				else ph.setVelocity(-70, YVelocity);
			}	
		}
	}
	/**
	 * Changes doll sprite from walking skin to idle and stop animation playing and moving.
	 */		
	public void stop() {
		
		if (LOG_ON) Log.i("Clementime", className + "/stop()");
		
		ph.setVelocity(0,0);
		walking.setVisible(false);
		idle.setPosition(walking);
		idle.setVisible(true);
		idle.stopAnimation(0);
	}
	/**
	 * Animates idle animation (= doll says no).
	 */	
	public void sayNo() {
		idle.animate(80, 1);
	}
	/**
	 * Get y velocity from database: doll can move on a flat line or on an inclined line, depending on the screen.
	 */
	public void getYVelocity(int screenId) {
		this.standardYVelocityRight = db.selectYVelocity(screenId);
		this.standardYVelocityLeft = -this.standardYVelocityRight;
	}
	/**
	 * Hides or displays walking skin and idle skin of doll, when an animation is running and doll disappears somewhere.
	 */
	public void setVisible(boolean choice) {
		
		if (LOG_ON) Log.i("Clementime", className + "/setVisible()");
		
		if (choice == true) {
			if (ph.getVelocityX() > 0) {
				this.walking.setVisible(true);
				this.idle.setVisible(false);		
			} else {
				this.walking.setVisible(false);
				this.idle.setVisible(true);					
			}
		} else {
			this.walking.setVisible(false);
			this.idle.setVisible(false);		
		}
		
	}
	/**
	 * Finds in which screen doll is in.
	 */	
	public int getScreen() {
		return db.selectDollScreen();
	}
	/**
	 * Finds where the doll should start on an defined screen.
	 */	
	public int[] getStartingPosition() {
		return db.selectDollPosition();
	}
	/**
	 * Changes the aspect of the doll (walking and idle).
	 */
	public void changeSkin(int skinId) {
		dollBTA.clearTextureAtlasSources();
		int bgFile = context.getResources().getIdentifier("doll_skin_" + skinId, "drawable", context.getPackageName());
		if (bgFile == 0) {
			bgFile = context.getResources().getIdentifier(DEFAULT_IMAGE, "drawable", context.getPackageName());
			if (LOG_ON) Log.w("Clementime", className + "/changeSkin(): cannot find skin " + skinId);
		} else if (LOG_ON) Log.d("Clementime", className + "/changeSkin(): load skin " +  + skinId);
		
		BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(dollBTA, context, bgFile, 0, 0, 4, 4);
	}
	
	public void setPosition(float x, float y) {
		walking.setPosition(x, y);
		idle.setPosition(x, y);	
	}
}
