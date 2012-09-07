package eu.clementime.rds;

import static eu.clementime.rds.Constants.MASK_ALPHA_LAYER;
import static eu.clementime.rds.Constants.CAMERA_HEIGHT;
import static eu.clementime.rds.Constants.CAMERA_WIDTH;
import static eu.clementime.rds.Constants.MARGIN_Y;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnAreaTouchListener;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.HorizontalAlign;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

//TODO: write javadoc and enhance /update this class. But you know what it is... it's dev, not production... not so critical... I know it's bad... don't hit me!
/**
* Screen tools for dev.
* @author Cl&eacute;ment
* @version 1.0
*/
public class DevTools extends HUD implements IOnAreaTouchListener {

	DatabaseHandler dbh;
	
	/**
	 * For logs only.
	 */	
	private String className = "DevTools";
	Backup backup;
	public Rectangle mask;
	
	private Font font;
	public Font devFont;

	private Text save1;
	
	private Text load1;

	private int saveId = 0;
	private int loadId = 0;
	
	private Rectangle rect;
	
	public Sprite settings;
	public Text openX;
	private BitmapTextureAtlas devFontBTA;
	
	public DevTools(Camera camera, Font font, DatabaseHandler dbh, Backup backup, Context context, Engine engine, Scene scene) {
		
		Log.i("Clementime", className + "/constructor");
		
		this.mCamera = camera;
		this.font = font;
		this.backup = backup;
		this.dbh = dbh;
			
		BitmapTextureAtlas settingsBTA = new BitmapTextureAtlas(128, 128, TextureOptions.DEFAULT);
		TextureRegion settingsTR = BitmapTextureAtlasTextureRegionFactory.createFromResource(settingsBTA, context, R.drawable.settings, 0, 0);

		settings = new Sprite(0, 0, settingsTR);
		settings.setPosition(CAMERA_WIDTH - settings.getWidth(), MARGIN_Y);
		scene.attachChild(settings);
		scene.registerTouchArea(settings);
		settings.setZIndex(301);

		engine.getTextureManager().loadTexture(settingsBTA);
		this.loadFonts(engine, scene);
	}
	
	public void loadFonts(Engine engine, Scene scene) {
		
		Log.i("Clementime", className + "/loadFonts()");

        FontFactory.setAssetBasePath("font/");
        
		devFontBTA = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		devFont = new Font(devFontBTA, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 50, true, Color.RED);
		engine.getTextureManager().loadTexture(devFontBTA);
   	}
		
	public void setup(Scene scene) {
		
		Log.i("Clementime", className + "/setup()");
		
		openX = new Text(0, 0, devFont, "X", HorizontalAlign.LEFT);
		openX.setPosition(CAMERA_WIDTH/2-openX.getWidth()/2, MARGIN_Y);
		scene.attachChild(openX);
		scene.registerTouchArea(openX);
		openX.setZIndex(301);
		
		mask = new Rectangle(0, 0, 480, 320);
		mask.setColor(1, 1, 1, MASK_ALPHA_LAYER);

		this.attachChild(mask);
		mask.setVisible(false);
		
		this.setOnAreaTouchListener(this);
				
		rect = new Rectangle(0, 0, 50, 45);
        rect.setColor(1, 1, 1);
		mask.attachChild(rect);
		
		Text save = new Text(20, 20, this.font, "SAVE", HorizontalAlign.LEFT);
		mask.attachChild(save);
		
		save1 = new Text(20, 50, this.devFont, "S1", HorizontalAlign.LEFT);
		mask.attachChild(save1);
		
		Text load = new Text(20, 100, this.font, "LOAD", HorizontalAlign.LEFT);
		mask.attachChild(load);
		
		load1 = new Text(20, 130, this.devFont, "L1", HorizontalAlign.LEFT);
		mask.attachChild(load1);	
	}
	
	public void display() {
		
		Log.i("Clementime", className + "/display()");

		mask.setVisible(true);
		
		this.registerTouchArea(save1);
		this.registerTouchArea(load1);

	}
		
	public int hide() {	
		
		int load = 0; 
		
		if (saveId != 0) {
			switch (saveId) {
				case 1: dbh.save(1); break;
			}

			backup.createStateFile("");
			backup.createStateFiles("");
			backup.savePlayerData("");
			Log.d("Clementime", className + "hide: save in S" + saveId);

			saveId = 0;
			load = 2;
		} else if (loadId != 0) load = 2; 
		//loadStateFiles("");
		//loadPlayerData("");
		//Log.d("Clementime","backupFrame/hide: load L" + loadId);

		this.unregisterTouchArea(save1);
		this.unregisterTouchArea(load1);


		mask.setVisible(false);
		this.detachSelf();
		
		return load;
	}
	
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		
		switch(pSceneTouchEvent.getAction()) {

	        case TouchEvent.ACTION_UP:

	    		if (this.mask.isVisible()) {	    		
		    		if (pTouchArea == save1) {
		    			rect.setPosition(save1.getX(), save1.getY()+4);
		    			saveId = 1;
		    		} else if (pTouchArea == load1) {
		    			rect.setPosition(load1.getX(), load1.getY()+4);
		    			loadId = 1;
		    		}
	    		}
	    		
	        	break;
			}
		
		
		return true;
	}	
}
