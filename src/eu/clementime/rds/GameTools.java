package eu.clementime.rds;

/* CONSTANTS */
import static eu.clementime.rds.Constants.MOVE_ARROWS_POSY;
import static eu.clementime.rds.Constants.MOVE_LEFT_ARROW_POSX;
import static eu.clementime.rds.Constants.MOVE_RIGHT_ARROW_POSX;
import static eu.clementime.rds.Constants.ZINDEX_ARROW;
import static eu.clementime.rds.Constants.ZINDEX_CIRCLE;

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
	
	private final int CAMERA_WIDTH;
	private final int CAMERA_HEIGHT;
	private final int MARGIN;
		
	private Context context;
	private DatabaseAccess db;
	public int screenId;
	
	private TiledTextureRegion TR1;
	private TiledTextureRegion TR2;
	private TiledTextureRegion TR3;
	
	public AnimatedSprite leftArrow;
	public AnimatedSprite rightArrow;
	public AnimatedSprite animatedCircle;
	
	public ActionsManager am;
	
	public float xMin;
	public float xMax;
	
	private BitmapTextureAtlas defaultFontBTA;
	private BitmapTextureAtlas defaultFont2BTA;
	private BitmapTextureAtlas fontBTA;
	public Font defaultFont;
	public Font defaultFont2;
	public Font font;
	
	public String language;
	
	public GameTools(DatabaseHandler dbh, Context context, int cw, int ch, int MARGIN, Engine engine, Scene scene) {
		
		Log.i("Clementime", "GameTools/constructor()");
		
		this.db = new DatabaseAccess(dbh);
		
		this.context = context;
		this.CAMERA_WIDTH = cw;
		this.CAMERA_HEIGHT = ch;
		this.MARGIN = MARGIN;
		
		this.language = db.selectLanguage(context);
		this.am = new ActionsManager();
		
		loadGameItems(engine, scene);
		loadFonts(engine, scene);
		
//		this.mask = new Rectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
//		this.mask.setColor(0, 0, 0, 0);
	}
	
	// TODO: check size of all BTA to reduce them as much as possible
	public void loadGameItems(Engine engine, Scene scene) {
		
		Log.i("Clementime", "GameTools/loadGameItems()");
		
		BitmapTextureAtlas BTA = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		this.TR1 = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(BTA, context, R.drawable.move_left, 0, 0, 5, 1);
		this.TR2 = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(BTA, context, R.drawable.move_right, 0, 30, 5, 1);
		this.TR3 = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(BTA, context, R.drawable.animated_circle, 0, 60, 4, 3);
		
		engine.getTextureManager().loadTexture(BTA);
		
		leftArrow = new AnimatedSprite(MOVE_LEFT_ARROW_POSX, CAMERA_HEIGHT + MOVE_ARROWS_POSY, TR1);
		rightArrow = new AnimatedSprite(CAMERA_WIDTH + MOVE_RIGHT_ARROW_POSX, CAMERA_HEIGHT + MOVE_ARROWS_POSY, TR2);
		animatedCircle = new AnimatedSprite(0, 0, TR3);
		
		leftArrow.setVisible(false);
		rightArrow.setVisible(false);
		
		leftArrow.setAlpha(0.7f);
		rightArrow.setAlpha(0.7f);

		animatedCircle.stopAnimation(11);
		leftArrow.stopAnimation(4);
		rightArrow.stopAnimation(4);
		
		scene.attachChild(leftArrow);
		scene.attachChild(rightArrow);
		scene.attachChild(animatedCircle);
		
		animatedCircle.setZIndex(ZINDEX_CIRCLE);
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

	public void showAnimatedCircle(float x, float y) {
		
		Log.i("Clementime", "GameTools/showAnimatedCircle()");
		
		animatedCircle.setPosition(x, y);
		animatedCircle.setVisible(true);
		animatedCircle.animate(50, false);
	}
	
	public void checkBorders(float xMin, float xMax) {
		
		if (leftArrow.isVisible()) {
			if (this.xMin >= xMin)
				leftArrow.setVisible(false);
			else
				leftArrow.setPosition(xMin + MOVE_LEFT_ARROW_POSX, leftArrow.getY());
		} else if (this.xMin < xMin) {
			leftArrow.setPosition(xMin + MOVE_LEFT_ARROW_POSX, leftArrow.getY());
			leftArrow.setVisible(true);
		}
		 
		if (rightArrow.isVisible()) {
			if (this.xMax <= xMax)
				rightArrow.setVisible(false);
			else
				rightArrow.setPosition(xMin + MOVE_RIGHT_ARROW_POSX, rightArrow.getY());
		} else if (this.xMax > xMax) {
			rightArrow.setPosition(xMin + MOVE_RIGHT_ARROW_POSX, rightArrow.getY());
			rightArrow.setVisible(true);
		}
	}
}


