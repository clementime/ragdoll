package eu.clementime.rds;

/* CONSTANTS */
import static eu.clementime.rds.Constants.MOVE_ARROWS_POSY;
import static eu.clementime.rds.Constants.MOVE_LEFT_ARROW_POSX;
import static eu.clementime.rds.Constants.MOVE_RIGHT_ARROW_POSX;
import static eu.clementime.rds.Constants.ZINDEX_ARROW;
import static eu.clementime.rds.Constants.ZINDEX_CIRCLE;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.entity.Entity;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.Context;
import android.util.Log;

public class GameTools extends Entity {
	
	private final int CAMERA_WIDTH;
	private final int CAMERA_HEIGHT;
	private final int MARGIN;
		
	private Context context;
	public int screenId;
	
	private TiledTextureRegion TR1;
	private TiledTextureRegion TR2;
	private TiledTextureRegion TR3;
	
	public AnimatedSprite leftArrow;
	public AnimatedSprite rightArrow;
	public AnimatedSprite animatedCircle;
	
	public float xMin;
	public float xMax;
	
	public Rectangle mask;
	
	public GameTools(Context context, int cw, int ch, int MARGIN) {
		
		Log.i("Clementime", "GameToolsManager/constructor()");
		
		this.context = context;
		this.CAMERA_WIDTH = cw;
		this.CAMERA_HEIGHT = ch;
		this.MARGIN = MARGIN;
		
		this.mask = new Rectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		this.mask.setColor(0, 0, 0, 0);
	}

	// TODO: check size of all BTA to reduce them as much as possible
	public void loadGameItems(Engine engine, Scene scene) {
		
		Log.i("Clementime", "GameToolsManager/loadGameItems()");
		
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
	}
	
	public void showAnimatedCircle(float x, float y) {
		
		Log.i("Clementime", "GameToolsManager/showAnimatedCircle()");
		
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


