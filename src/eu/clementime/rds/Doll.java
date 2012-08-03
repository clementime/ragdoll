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

public class Doll {
	
	public AnimatedSprite image;
	public AnimatedSprite idle;
	private BitmapTextureAtlas dollBTA;
	private BitmapTextureAtlas dollIdleBTA;
	private TiledTextureRegion dollTR;
	private TiledTextureRegion dollIdleTR;
	
	public PhysicsHandler ph;
	
	public float standardYVelocityRight;
	public float standardYVelocityLeft;
	public float YVelocity = 0;
	
	public float staticCenterX;
	
	public int walkDirection;
	public boolean isChased = true;
	
	private DatabaseAccess db;
	private Context context;
	private String className = "Doll";
	
	public Doll(DatabaseHandler dbh, Context context, Engine engine, Scene scene) {
		
		this.db = new DatabaseAccess(dbh);
		this.context = context;
		
		//dollBTA = new BitmapTextureAtlas(512, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		dollBTA = new BitmapTextureAtlas(2048, 2048, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		dollIdleBTA = new BitmapTextureAtlas(2048, 2048, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		dollTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(dollBTA, context, R.drawable.doll_skin_1, 0, 0, 4, 4);
		dollIdleTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(dollBTA, context, R.drawable.doll_idle_1, 0, 0, 4, 4);
		
		engine.getTextureManager().loadTextures(dollBTA, dollIdleBTA);
		
		image = new AnimatedSprite(200, 110, dollTR);
		idle = new AnimatedSprite(200, 110, dollIdleTR);
		
		image.setVisible(false);
		idle.stopAnimation(0);
			
		ph = new PhysicsHandler(image);
		image.registerUpdateHandler(ph);
		
		scene.attachChild(image);
		scene.attachChild(idle);
		image.setZIndex(ZINDEX_DOLL);
		idle.setZIndex(ZINDEX_DOLL);
		
		scene.registerTouchArea(image);
		scene.registerTouchArea(idle);
		
		this.staticCenterX = image.getWidth()/2;
	}

	public void move(int status, float touchedX) {
		
		if (LOG_ON) Log.i("Clementime", className + "/move()");

		idle.setVisible(false);
		image.setVisible(true);
		
		// if doll wasn't previously walking, launch walking animation	
		if (ph.getVelocityX() == 0) {
			
			if (touchedX > image.getX()) {
				walkDirection = DIRECTION_RIGHT;
				image.animate(new long[]{120, 120, 120, 120, 120, 120, 120, 120}, 0, 7, true);	
				if (status == STATUS_ACTION || YVelocity == -1000) ph.setVelocity(70, standardYVelocityRight);
				else ph.setVelocity(70, YVelocity);
			}
			else if (touchedX < image.getX()) {
				walkDirection = DIRECTION_LEFT;
				image.animate(new long[]{120, 120, 120, 120, 120, 120, 120, 120}, 8, 15, true);				
				if (status == STATUS_ACTION || YVelocity == -1000) ph.setVelocity(-70, standardYVelocityLeft);
				else ph.setVelocity(-70, YVelocity);
			}	
		}
	}
	
	public void stop() {
		
		if (LOG_ON) Log.i("Clementime", className + "/stop()");
		
		image.setVisible(false);
		idle.setPosition(image);
		idle.setVisible(true);
	}
	
	public void sayNo() {
		idle.animate(new long[]{120, 120, 120, 120}, 0, 15, 4);		
	}
	
	public void getYVelocity(int screenId) {
		this.standardYVelocityRight = db.selectYVelocity(screenId);
		this.standardYVelocityLeft = -this.standardYVelocityRight;
	}
	
	public void setVisible(boolean choice) {
		this.image.setVisible(choice);
	}
	
	public int getScreen() {
		return db.selectDollScreen();
	}
	
	public int[] getStartingPosition() {
		int[] position = db.selectDollPosition();
		position[0] *= SCALE_POSITION;
		position[1] *= SCALE_POSITION;
		return position;
	}
	
	public void changeSkin(int skinId) {
		dollBTA.clearTextureAtlasSources();
		int bgFile = context.getResources().getIdentifier("doll_skin_" + skinId, "drawable", context.getPackageName());
		if (bgFile == 0) {
			bgFile = context.getResources().getIdentifier(DEFAULT_IMAGE, "drawable", context.getPackageName());
			if (LOG_ON) Log.w("Clementime", className + "/changeSkin(): cannot find skin " + skinId);
		} else if (LOG_ON) Log.d("Clementime", className + "/changeSkin(): load skin " +  + skinId);
		
		BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(dollBTA, context, bgFile, 0, 0, 4, 4);
	}
}
