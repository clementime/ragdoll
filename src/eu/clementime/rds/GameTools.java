package eu.clementime.rds;

/* CONSTANTS */
import static eu.clementime.rds.Constants.MOVE_ARROWS_POSY;
import static eu.clementime.rds.Constants.MOVE_LEFT_ARROW_POSX;
import static eu.clementime.rds.Constants.MOVE_RIGHT_ARROW_POSX;
import static eu.clementime.rds.Constants.ZINDEX_ARROW;
import static eu.clementime.rds.Constants.ZINDEX_CIRCLE;
import static eu.clementime.rds.Constants.POINTER_CIRCLE;
import static eu.clementime.rds.Constants.POINTER_WALK;
import static eu.clementime.rds.Constants.CAMERA_WIDTH;
import static eu.clementime.rds.Constants.CAMERA_HEIGHT;
import static eu.clementime.rds.Constants.MARGIN_Y;
import static eu.clementime.rds.Constants.LOG_ON;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.entity.Entity;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

public class GameTools extends Entity {
		
	private Context context;
	private DatabaseAccess db;
	public int screenId;
	
	private TiledTextureRegion TR1;
	private TiledTextureRegion TR2;
	private TiledTextureRegion TR3;
	private TiledTextureRegion TR4;
	
	public AnimatedSprite leftArrow;
	public AnimatedSprite rightArrow;
	public AnimatedSprite animatedCircle;
	public AnimatedSprite walkingPointer;
	
	public ActionsManager am;

	private BitmapTextureAtlas defaultFontBTA;
	private BitmapTextureAtlas defaultFont2BTA;
	private BitmapTextureAtlas fontBTA;
	public Font defaultFont;
	public Font defaultFont2;
	public Font font;
	
	public String language;
	
	public boolean rightHanded;
	
	public GameTools(DatabaseHandler dbh, Context context, Engine engine, Scene scene) {
		
		if (LOG_ON) Log.i("Clementime", "GameTools/constructor()");
		
		this.db = new DatabaseAccess(dbh);
		
		this.context = context;
		
		this.language = db.selectLanguage(context);
		this.am = new ActionsManager();
		
		loadGameItems(engine, scene);
		loadFonts(engine, scene);
	}
	
	// TODO: check size of all BTA to reduce them as much as possible
	public void loadGameItems(Engine engine, Scene scene) {
		
		if (LOG_ON) Log.i("Clementime", "GameTools/loadGameItems()");
		
		BitmapTextureAtlas BTA = new BitmapTextureAtlas(256, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		this.TR1 = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(BTA, context, R.drawable.move_left, 0, 0, 5, 1);
		this.TR2 = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(BTA, context, R.drawable.move_right, 0, 30, 5, 1);
		this.TR3 = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(BTA, context, R.drawable.animated_circle, 0, 60, 4, 3);
		this.TR4 = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(BTA, context, R.drawable.walking_pointer, 0, 210, 4, 3);
		
		engine.getTextureManager().loadTexture(BTA);
		
		leftArrow = new AnimatedSprite(MOVE_LEFT_ARROW_POSX, CAMERA_HEIGHT + MOVE_ARROWS_POSY - MARGIN_Y, TR1);
		rightArrow = new AnimatedSprite(CAMERA_WIDTH + MOVE_RIGHT_ARROW_POSX, CAMERA_HEIGHT + MOVE_ARROWS_POSY - MARGIN_Y, TR2);
		animatedCircle = new AnimatedSprite(0, 0, TR3);
		walkingPointer = new AnimatedSprite(0, 0, TR4);
		
		leftArrow.setVisible(false);
		rightArrow.setVisible(false);
		
		leftArrow.setAlpha(0.7f);
		rightArrow.setAlpha(0.7f);

		animatedCircle.stopAnimation(11);
		walkingPointer.stopAnimation(11);
		leftArrow.stopAnimation(4);
		rightArrow.stopAnimation(4);
		
		scene.attachChild(leftArrow);
		scene.attachChild(rightArrow);
		scene.attachChild(animatedCircle);
		scene.attachChild(walkingPointer);
		
		scene.registerTouchArea(leftArrow);
		scene.registerTouchArea(rightArrow);
		
		animatedCircle.setZIndex(ZINDEX_CIRCLE);
		walkingPointer.setZIndex(ZINDEX_CIRCLE);
		leftArrow.setZIndex(ZINDEX_ARROW);
		rightArrow.setZIndex(ZINDEX_ARROW);
		
		this.am.load(this.context, engine, scene);
	}
	
	public void loadFonts(Engine engine, Scene scene) {
		
        FontFactory.setAssetBasePath("font/");
		
        fontBTA = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		font = FontFactory.createFromAsset(fontBTA, this.context, "arfmoochikncheez.ttf", 20, true, Color.BLACK);
		engine.getTextureManager().loadTexture(fontBTA);     
        
		defaultFontBTA = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		defaultFont = new Font(defaultFontBTA, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 24, true, Color.BLACK);
		engine.getTextureManager().loadTexture(defaultFontBTA);
		
		defaultFont2BTA = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		defaultFont2 = new Font(defaultFont2BTA, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 24, true, Color.RED);
		engine.getTextureManager().loadTexture(defaultFont2BTA);
	}

	public void showAnimatedCircle(float x, float y, int type) {
		
		if (LOG_ON) Log.i("Clementime", "GameTools/showAnimatedCircle()");
		
		if (type == POINTER_WALK) {
			walkingPointer.setPosition(x, y);
			walkingPointer.setVisible(true);
			walkingPointer.animate(50, false);		
		} else {
			animatedCircle.setPosition(x, y);
			animatedCircle.setVisible(true);
			animatedCircle.animate(50, false);
		}
	}

	public void hideAnimatedCircle() {
		
		if (LOG_ON) Log.i("Clementime", "GameTools/hideAnimatedCircle()");
		
		walkingPointer.setVisible(false);
		animatedCircle.setVisible(false);
	}
	
	public void checkBorders(float xMinCamera, float xMaxCamera, float xMinScreen, float xMaxScreen) {
		
		if (leftArrow.isVisible()) {
			if (xMinScreen >= xMinCamera)
				leftArrow.setVisible(false);
			else
				leftArrow.setPosition(xMinCamera + MOVE_LEFT_ARROW_POSX, leftArrow.getY());
		} else if (xMinScreen < xMinCamera) {
			leftArrow.setPosition(xMinCamera + MOVE_LEFT_ARROW_POSX, leftArrow.getY());
			leftArrow.setVisible(true);
		}
		 
		if (rightArrow.isVisible()) {
			if (xMaxScreen <= xMaxCamera)
				rightArrow.setVisible(false);
			else
				rightArrow.setPosition(xMaxCamera + MOVE_RIGHT_ARROW_POSX, rightArrow.getY());
		} else if (xMaxScreen > xMaxCamera) {
			rightArrow.setPosition(xMaxCamera + MOVE_RIGHT_ARROW_POSX, rightArrow.getY());
			rightArrow.setVisible(true);
		}
	}

	public int getPlayingHand() {
		return db.selectPlayingHand();
	}
}


