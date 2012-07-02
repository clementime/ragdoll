package eu.clementime.rds;

import static eu.clementime.rds.Constants.ZINDEX_DOLL;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.Context;

public class Doll {
	
	private AnimatedSprite image;
	private BitmapTextureAtlas dollBTA;
	private TiledTextureRegion dollTR;
	
	public PhysicsHandler ph;
	
	public Doll(Context context, Engine engine, Scene scene) {
		
		dollBTA = new BitmapTextureAtlas(512, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		dollTR = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(dollBTA, context, R.drawable.doll, 0, 0, 4, 5);
		
		engine.getTextureManager().loadTextures(dollBTA);
		
		image = new AnimatedSprite(200, 110, dollTR);
		image.stopAnimation(16);
			
		ph = new PhysicsHandler(image);
		image.registerUpdateHandler(ph);
		
		scene.attachChild(image);
		image.setZIndex(ZINDEX_DOLL);
	}
}
