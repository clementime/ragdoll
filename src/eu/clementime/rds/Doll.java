package eu.clementime.rds;

import static eu.clementime.rds.Constants.DEFAULT_IMAGE;
import static eu.clementime.rds.Constants.DIRECTION_LEFT;
import static eu.clementime.rds.Constants.DIRECTION_RIGHT;
import static eu.clementime.rds.Constants.STATUS_ACTION;
import static eu.clementime.rds.Constants.ZINDEX_DOLL;
import static eu.clementime.rds.Constants.SCALE;

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
	private BitmapTextureAtlas dollBTA;
	private TiledTextureRegion dollTR;
	
	public PhysicsHandler ph;
	
	public float standardYVelocityRight;
	public float standardYVelocityLeft;
	public float YVelocity = 0;
	
	public float centerX;
	
	public int walkDirection;
	public boolean isChased = true;
	
	private DatabaseAccess db;
	private Context context;
	
	public Doll(DatabaseHandler dbh, Context context, Engine engine, Scene scene) {
		
		this.db = new DatabaseAccess(dbh);
		this.context = context;
		
		//dollBTA = new BitmapTextureAtlas(512, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		dollBTA = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		dollTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(dollBTA, context, R.drawable.doll_skin_1, 0, 0, 6, 4);
		
		engine.getTextureManager().loadTextures(dollBTA);
		
		image = new AnimatedSprite(200, 110, dollTR);
		
		// TODO: scale depending screen
		image.setScaleCenter(0,0);
		image.setScale(SCALE);
		image.stopAnimation(16);
			
		ph = new PhysicsHandler(image);
		image.registerUpdateHandler(ph);
		
		scene.attachChild(image);
		image.setZIndex(ZINDEX_DOLL);
		
		scene.registerTouchArea(image);
		
		this.centerX = image.getWidth()/2;
	}

	public void move(int status, float touchedX) {
		
		Log.i("Clementime", "Screen/moveDoll()");

		// if doll wasn't previously walking, launch walking animation	
		if (ph.getVelocityX() == 0) {
			
			if (touchedX > image.getX()) {
				changeSkin(1);
				walkDirection = DIRECTION_RIGHT;
				image.animate(new long[]{120, 120, 120, 120, 120, 120, 120, 120}, 0, 7, true);	
				if (status == STATUS_ACTION || YVelocity == -1000) ph.setVelocity(70, standardYVelocityRight);
				else ph.setVelocity(70, YVelocity);
			}
			else if (touchedX < image.getX()) {
				changeSkin(2);
				walkDirection = DIRECTION_LEFT;
				image.animate(new long[]{120, 120, 120, 120, 120, 120, 120, 120}, 8, 15, true);				
				if (status == STATUS_ACTION || YVelocity == -1000) ph.setVelocity(-70, standardYVelocityLeft);
				else ph.setVelocity(-70, YVelocity);
			}	
		}
	}
	
	public void sayNo() {
		image.animate(new long[]{120, 120, 120, 120}, 16, 19, 4);		
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
	
	public int[] getPosition() {
		return db.selectDollPosition();
	}
	
	public void changeSkin(int skinId) {
		dollBTA.clearTextureAtlasSources();
		int bgFile = context.getResources().getIdentifier("doll_skin_" + skinId, "drawable", context.getPackageName());
		if (bgFile == 0) {
			bgFile = context.getResources().getIdentifier(DEFAULT_IMAGE, "drawable", context.getPackageName());
			Log.w("Clementime", "Doll/changeSkin(): cannot find skin " + skinId);
		} else Log.d("Clementime", "Doll/changeSkin(): load skin " +  + skinId);
		
		BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(dollBTA, context, bgFile, 0, 0, 6, 4);
		image.setScale(SCALE);
	}
}
