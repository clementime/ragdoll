package eu.clementime.rds;

import static eu.clementime.rds.Constants.ACTION_EXIT;
import static eu.clementime.rds.Constants.ACTION_LOOK;
import static eu.clementime.rds.Constants.ACTION_POINTER_OUT;
import static eu.clementime.rds.Constants.ACTION_POINTER_SIZE;
import static eu.clementime.rds.Constants.ACTION_TAKE;
import static eu.clementime.rds.Constants.ACTION_TALK;
import static eu.clementime.rds.Constants.DIRECTION_LEFT;
import static eu.clementime.rds.Constants.DIRECTION_RIGHT;
import static eu.clementime.rds.Constants.ZINDEX_ACTION;
import static eu.clementime.rds.Constants.MARGIN_Y;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.entity.Entity;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.Context;
import android.util.Log;

public class ActionsManager extends Entity {
		
	private TiledTextureRegion takeTR;
	private TiledTextureRegion lookTR;
	private TiledTextureRegion talkTR;
	public TiledTextureRegion exitLeftTR;
	public TiledTextureRegion exitRightTR;
	
	public AnimatedSprite take;
	public AnimatedSprite look;
	public AnimatedSprite talk;
	public AnimatedSprite exitLeft;
	public AnimatedSprite exitRight;

	public void load(Context context, Engine engine, Scene scene) {
				
		Log.d("Clementime", "ActionHandler/loadImages()");
		
		final BitmapTextureAtlas BTA = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		takeTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(BTA, context, R.drawable.action_take, 0, 0, 6, 1);
		lookTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(BTA, context, R.drawable.action_look, 0, 61, 6, 1);
		talkTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(BTA, context, R.drawable.action_talk, 0, 122, 6, 1);
		exitLeftTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(BTA, context, R.drawable.action_exit_left, 0, 183, 6, 1);
		exitRightTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(BTA, context, R.drawable.action_exit_right, 0, 244, 6, 1);

		engine.getTextureManager().loadTexture(BTA);
		
		take = new AnimatedSprite(0, 0, takeTR);
		look = new AnimatedSprite(0, 0, lookTR);
		talk = new AnimatedSprite(0, 0, talkTR);
		exitLeft = new AnimatedSprite(0, 0, exitLeftTR);
		exitRight = new AnimatedSprite(0, 0, exitRightTR);

		deactivate();
		
		this.attachChild(take);
		this.attachChild(look);
		this.attachChild(talk);
//		this.attachChild(exitLeft);
//		this.attachChild(exitRight);
		
		scene.registerTouchArea(take);
		scene.registerTouchArea(look);
		scene.registerTouchArea(talk);
		
		this.setZIndex(ZINDEX_ACTION);
		scene.attachChild(this);
	}	
		
	public void activate(float x, float y, float width, float height, int[] itemStates, boolean area) {
		
		int take = itemStates[0];
		int look = itemStates[1];
		int talk = itemStates[2];
		int exit = itemStates[3];

		deactivate();

		Log.d("Clementime", "ActionHandler/activate(): activate take: " + take + "-look: " + look + "-talk: " + talk + "-exit: " + exit);
		
		// if area, pointers are shown in centre area, if item, above or below
		if (area) {
			y = y + height/2 - ACTION_POINTER_SIZE/2;
		} else {
			// pointers are set usually on top of item, on bottom if not enough space available
			if (y <= ACTION_POINTER_SIZE) y = y + height;
			else y = y - ACTION_POINTER_SIZE;			
		}

		int count = 0;
		
		if (take > 0) count = count + 1;
		if (look > 0) count = count + 1;
		if (talk > 0) count = count + 1;
		if (exit > 0) count = count + 1;

		// only one pointer authorised for this item
		if (count == 1) {
			x = x + width/2 - ACTION_POINTER_SIZE/2;
			this.setPosition(x, y + MARGIN_Y);
			
			if (take > 0) {
				this.take.setPosition(0, 0);
				this.take.setVisible(true);
				this.take.animate(150, true);
			} else if (look > 0) {
				this.look.setPosition(0, 0);
				this.look.setVisible(true);
				this.look.animate(150, true);				
			} else if (talk > 0) {
				this.talk.setPosition(0, 0);
				this.talk.setVisible(true);
				this.talk.animate(150, true);
			} else if (exit > 0) {
				if (x <= 100) {
					this.exitLeft.setPosition(0, 0);
					this.exitLeft.setVisible(true);
					this.exitLeft.animate(150, true);				
				} else {
					this.exitRight.setPosition(0, 0);
					this.exitRight.setVisible(true);
					this.exitRight.animate(150, true);				
				}
			}
		}
		// two pointers authorised for this item
		else if (count == 2) {
			x = x + width/2 - ACTION_POINTER_SIZE;
			this.setPosition(x, y + MARGIN_Y);
			
			if (take > 0 && look > 0) {
				this.take.setPosition(0, 0);
				this.take.setVisible(true);
				this.take.animate(150, true);
				this.look.setPosition(ACTION_POINTER_SIZE, 0);
				this.look.setVisible(true);
				this.look.animate(150, true);
			} else if (take > 0 && talk > 0) {
				this.take.setPosition(0, 0);
				this.take.setVisible(true);
				this.take.animate(150, true);
				this.talk.setPosition(ACTION_POINTER_SIZE, 0);
				this.talk.setVisible(true);
				this.talk.animate(150, true);
			} else if (look > 0 && talk > 0) {
				this.look.setPosition(0, 0);
				this.look.setVisible(true);
				this.look.animate(150, true);
				this.talk.setPosition(ACTION_POINTER_SIZE, 0);
				this.talk.setVisible(true);
				this.talk.animate(150, true);
			} else if (look > 0 && exit > 0) {
				if (exit == DIRECTION_LEFT) {
					this.exitLeft.setPosition(0, 0);
					this.exitLeft.setVisible(true);
					this.exitLeft.animate(150, true);				
				} else if (exit == DIRECTION_RIGHT) {
					this.exitRight.setPosition(0, 0);
					this.exitRight.setVisible(true);
					this.exitRight.animate(150, true);				
				}
				this.look.setPosition(ACTION_POINTER_SIZE, 0);
				this.look.setVisible(true);
				this.look.animate(150, true);
			}
		}
		// three pointers authorised for this item: take, look, talk only
		else if (count == 3) {
			x = x + width/2 - 3*ACTION_POINTER_SIZE/2;
			this.setPosition(x, y + MARGIN_Y);
			this.take.setPosition(0, 0);
			this.take.setVisible(true);
			this.take.animate(150, true);
			this.look.setPosition(ACTION_POINTER_SIZE, 0);
			this.look.setVisible(true);
			this.look.animate(150, true);
			this.talk.setPosition(2*ACTION_POINTER_SIZE, 0);
			this.talk.setVisible(true);
			this.talk.animate(150, true);
		}
	}

	public void deactivate() {
		
		Log.d("Clementime", "ActionHandler/deactivate()");

		this.take.setVisible(false);
		this.look.setVisible(false);
		this.talk.setVisible(false);
		this.exitLeft.setVisible(false);
		this.exitRight.setVisible(false);
		// pointers are set out of reach
		this.take.setPosition(0, ACTION_POINTER_OUT);
		this.look.setPosition(0, ACTION_POINTER_OUT);
		this.talk.setPosition(0, ACTION_POINTER_OUT);
		this.exitLeft.setPosition(0, ACTION_POINTER_OUT);
		this.exitRight.setPosition(0, ACTION_POINTER_OUT);
		
		this.take.stopAnimation();
		this.look.stopAnimation();
		this.talk.stopAnimation();
		this.exitLeft.stopAnimation();
		this.exitRight.stopAnimation();
	}
	
	public void freeze(int action) {
		
		Log.d("Clementime", "ActionHandler/freeze()");
		
		this.take.stopAnimation();
		this.look.stopAnimation();
		this.talk.stopAnimation();
		this.exitLeft.stopAnimation();
		this.exitRight.stopAnimation();
		
		this.take.setVisible(false);
		this.look.setVisible(false);
		this.talk.setVisible(false);
		this.exitLeft.setVisible(false);
		this.exitRight.setVisible(false);
		
		if (action == ACTION_TAKE) this.take.setVisible(true);
		if (action == ACTION_LOOK) this.look.setVisible(true);
		if (action == ACTION_TALK) this.talk.setVisible(true);
		if (action == ACTION_EXIT) this.exitLeft.setVisible(true);
		if (action == ACTION_EXIT) this.exitRight.setVisible(true);
	}
}
