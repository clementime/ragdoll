package eu.clementime.rds;

import static eu.clementime.rds.Constants.TALK_POSX;
import static eu.clementime.rds.Constants.TALK_POSY;
import static eu.clementime.rds.Constants.ZINDEX_TALK;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.HorizontalAlign;

import android.content.Context;

public class Talk {
	
	private TextureRegion TR1;
	
	public Sprite background;
	public Sprite selectedItem = null;
	
	private Sprite picture;
	
	public Talk(Context context, Engine engine, Scene scene) {
		
		BitmapTextureAtlas BTA = new BitmapTextureAtlas(512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.TR1 = BitmapTextureAtlasTextureRegionFactory.createFromResource(BTA, context, R.drawable.look_bg, 0, 0);

		engine.getTextureManager().loadTexture(BTA);
		
		background = new Sprite(TALK_POSX, TALK_POSY, TR1);	
		background.setVisible(false);

		scene.attachChild(background);
		background.setZIndex(ZINDEX_TALK);
	}
	
	public void display(int pictureId, Font font) {
		background.setVisible(true);
		displayPicture(pictureId, font);
	}
		
	public void hide() {
		background.setVisible(false);
	}
	
	private void displayPicture(int pictureId, Font font) {	
		
		background.detachChildren();
		Text displayedText;

		displayedText = new Text(60, 20, font, "yop", HorizontalAlign.LEFT);
		background.attachChild(displayedText);
	}

}
