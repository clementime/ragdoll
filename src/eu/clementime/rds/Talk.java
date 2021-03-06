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

/**
* This class manages talking bubbles between doll and characters.
* @author Cl&eacute;ment
* @version 1.0
*/
public class Talk {
	
	private DatabaseAccess db;
	
	/**
	 * For logs only.
	 */	
	private String className = "Talk";
	//private Context context;
	//public int screenId;
	
	private TextureRegion TR1;
	private TextureRegion TR2;
	
	public Sprite background;
	public Sprite selectedItem = null;
	
	private Sprite picture;
	
	/**
	 * Loads images and creates every sprites, attach them to the AndEngine scene, register touch areas and set ZIndexes.
	 * @param	dbh		database handler is stored for upcoming database calls
 	 * @param	context	Android context, to retrieve files
	 * @param	engine	AndEngine engine, to load textures
	 * @param	scene	AndEngine scene, to attach sprites to scene and register touch areas
	 */	
	public Talk(DatabaseHandler dbh, Context context, Engine engine, Scene scene) {
	//public Talk(DatabaseHandler dbh, Context context, int screenId, Engine engine, Scene scene) {
		
		this.db = new DatabaseAccess(dbh);
		//this.context = context;
		//this.screenId = screenId;
		
		BitmapTextureAtlas BTA = new BitmapTextureAtlas(512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		BitmapTextureAtlas BTA2 = new BitmapTextureAtlas(512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.TR1 = BitmapTextureAtlasTextureRegionFactory.createFromResource(BTA, context, R.drawable.dialog_bubble, 0, 0);
		this.TR2 = BitmapTextureAtlasTextureRegionFactory.createFromResource(BTA2, context, R.drawable.dialog_bubble_test, 0, 0);

		engine.getTextureManager().loadTextures(BTA,BTA2);
		
		background = new Sprite(TALK_POSX, TALK_POSY, TR1);	
		background.setVisible(false);

		scene.attachChild(background);
		background.setZIndex(ZINDEX_TALK);
	}
	/**
	 * Displays bubble and picture in it.
	 */
	public void display(Scene scene, int pictureId, Font font) {
		background.setVisible(true);
		displayPicture(scene, pictureId, font);
	}
	/**
	 * Hides bubble and attached picture.
	 */
	public void hide() {
		background.setVisible(false);
	}
	/**
	 * Displays picture that explained feelings of the doll, inside a bubble.
	 */
	private void displayPicture(Scene scene, int pictureId, Font font) {	
		
		background.detachChildren();
//		Text displayedText;
//
//		displayedText = new Text(60, 20, font, "yop", HorizontalAlign.LEFT);
//		background.attachChild(displayedText);
		
		picture = new Sprite(10, 10, TR2);	
		background.attachChild(picture);
		//picture.setZIndex(ZINDEX_TALK);
	}

}
