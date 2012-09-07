package eu.clementime.rds;

/* CONSTANTS */
import static eu.clementime.rds.Constants.MOVE_ARROWS_POSY;
import static eu.clementime.rds.Constants.MOVE_LEFT_ARROW_POSX;
import static eu.clementime.rds.Constants.MOVE_RIGHT_ARROW_POSX;
import static eu.clementime.rds.Constants.ZINDEX_ARROW;
import static eu.clementime.rds.Constants.ZINDEX_CIRCLE;
import static eu.clementime.rds.Constants.POINTER_CIRCLE;
import static eu.clementime.rds.Constants.POINTER_DOLL;
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

/**
* Game tools manage all tools that are used to play: pointers, moving arrows and action manager.
* @author Cl&eacute;ment
* @version 1.0
*/
public class GameTools extends Entity {
		
	private Context context;
	private DatabaseAccess db;
	
	/**
	 * For logs only.
	 */	
	private String className = "GameTools";
	public int screenId;
	
	private TiledTextureRegion TR1;
	private TiledTextureRegion TR2;
	private TiledTextureRegion TR3;
	private TiledTextureRegion TR4;
	private TiledTextureRegion TR5;
	
	public AnimatedSprite leftArrow;
	public AnimatedSprite rightArrow;
	public AnimatedSprite defaultPointer;
	public AnimatedSprite walkingPointer;
	public AnimatedSprite dollPointer;
	
	public ActionsManager am;

	private BitmapTextureAtlas defaultFontBTA;
	private BitmapTextureAtlas defaultFont2BTA;
	private BitmapTextureAtlas fontBTA;
	public Font defaultFont;
	public Font defaultFont2;
	public Font font;
	
	public String language;
	
	public boolean rightHanded;
	public int pointer = POINTER_CIRCLE;
	
	/**
	 * Game items, including action manager, and fonts are loaded and created directly in the constructor.
	 * @param	dbh			database handler is stored for upcoming database calls
 	 * @param	context		Android context, to retrieve files
	 * @param	engine		AndEngine engine, to load textures
	 * @param	scene		AndEngine scene, to attach sprites to scene and register touch areas
	 */	
	public GameTools(DatabaseHandler dbh, Context context, Engine engine, Scene scene) {
		
		if (LOG_ON) Log.i("Clementime", className + "/constructor()");
		
		this.db = new DatabaseAccess(dbh);
		
		this.context = context;
		
		this.language = db.selectLanguage(context);
		this.am = new ActionsManager();
		
		loadGameItems(engine, scene);
		loadFonts(engine, scene);
	}
	
	// TODO: check size of all BTA to reduce them as much as possible
	/**
	 * Loads images and creates every sprites, attach them to the AndEngine scene,
	 * hides them which aren't used at start, register touch areas and set ZIndexes.
	 * @param	engine	AndEngine engine, to load textures
	 * @param	scene	AndEngine scene, to attach sprites to scene and register touch areas
	 */	
	public void loadGameItems(Engine engine, Scene scene) {
		
		if (LOG_ON) Log.i("Clementime", className + "/loadGameItems()");
		
		BitmapTextureAtlas BTA = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		this.TR1 = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(BTA, context, R.drawable.move_left, 0, 0, 5, 1);
		this.TR2 = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(BTA, context, R.drawable.move_right, 0, 30, 5, 1);
		this.TR3 = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(BTA, context, R.drawable.animated_circle, 0, 60, 4, 3);
		this.TR4 = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(BTA, context, R.drawable.walking_pointer, 0, 210, 4, 3);
		this.TR5 = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(BTA, context, R.drawable.doll_pointer, 150, 210, 4, 3);
		
		engine.getTextureManager().loadTexture(BTA);
		
		leftArrow = new AnimatedSprite(MOVE_LEFT_ARROW_POSX, CAMERA_HEIGHT + MOVE_ARROWS_POSY - MARGIN_Y, TR1);
		rightArrow = new AnimatedSprite(CAMERA_WIDTH + MOVE_RIGHT_ARROW_POSX, CAMERA_HEIGHT + MOVE_ARROWS_POSY - MARGIN_Y, TR2);
		defaultPointer = new AnimatedSprite(0, 0, TR3);
		walkingPointer = new AnimatedSprite(0, 0, TR4);
		dollPointer = new AnimatedSprite(0, 0, TR5);
		
		leftArrow.setVisible(false);
		rightArrow.setVisible(false);
		
		leftArrow.setAlpha(0.7f);
		rightArrow.setAlpha(0.7f);

		defaultPointer.stopAnimation(11);
		walkingPointer.stopAnimation(11);
		dollPointer.stopAnimation(11);
		leftArrow.stopAnimation(4);
		rightArrow.stopAnimation(4);
		
		scene.attachChild(leftArrow);
		scene.attachChild(rightArrow);
		scene.attachChild(defaultPointer);
		scene.attachChild(walkingPointer);
		scene.attachChild(dollPointer);
		
		scene.registerTouchArea(leftArrow);
		scene.registerTouchArea(rightArrow);
		
		defaultPointer.setZIndex(ZINDEX_CIRCLE);
		walkingPointer.setZIndex(ZINDEX_CIRCLE);
		dollPointer.setZIndex(ZINDEX_CIRCLE);
		leftArrow.setZIndex(ZINDEX_ARROW);
		rightArrow.setZIndex(ZINDEX_ARROW);
		
		this.am.load(this.context, engine, scene);
	}
	/**
	 * Loads fonts for introduction screen or error messages. 
	 * @param	engine	AndEngine engine, to load textures
	 * @param	scene	AndEngine scene, to attach sprites to scene and register touch areas
	 */	
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
	/**
	 * Indicates where the player touched the screen (animated pointer). 
	 * @param	engine	AndEngine engine, to load textures
	 * @param	scene	AndEngine scene, to attach sprites to scene and register touch areas
	 */	
	public void showPointer(float x, float y) {
		
		if (LOG_ON) Log.i("Clementime", className + "/showdefaultPointer()");
		
		if (pointer == POINTER_WALK) {
			walkingPointer.setPosition(x, y);
			walkingPointer.setVisible(true);
			walkingPointer.animate(50, false);
		} else if (pointer == POINTER_DOLL){
			dollPointer.setPosition(x, y);
			dollPointer.setVisible(true);
			dollPointer.animate(50, false);
		} else {
			defaultPointer.setPosition(x, y);
			defaultPointer.setVisible(true);
			defaultPointer.animate(50, false);
		}
		pointer = POINTER_CIRCLE; // set to default value after each use
	}
	/**
	 * When it isn't needed anymore, hides pointer and stops its animation. 
	 */	
	public void hidePointer() {
		
		if (LOG_ON) Log.i("Clementime", className + "/hidePointer()");
		
		walkingPointer.setVisible(false);
		dollPointer.setVisible(false);
		defaultPointer.setVisible(false);
		
		walkingPointer.stopAnimation(11);
		dollPointer.stopAnimation(11);
		defaultPointer.stopAnimation(11);
	}
	/**
	 * When border of screen is reached, hides moving arrows; otherwise, displays them. 
	 * @param	xMinCamera	indicates where the camera is (scene/background x, not local x)
	 * @param	xMaxCamera	indicates where the camera is (scene/background x, not local x)
	 * @param	xMinScreen	indicates minimum x where doll can go (scene/background x, not local x)
	 * @param	xMaxScreen	indicates maximum x where doll can go (scene/background x, not local x)
	 */	
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
	/**
	 * Settings information: the player can choose either he/her wants items displayed at the
	 * left of his/her finger or at the right when dragging it in or outside inventory. 
	 * @return	hand used by player (left or right), chosen by him/her in the settings	
	 */	
	public int getPlayingHand() {
		return db.selectPlayingHand();
	}
}


