package eu.clementime.rds;

import static eu.clementime.rds.Constants.MASK_ALPHA_LAYER;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene.IOnAreaTouchListener;
import org.anddev.andengine.entity.scene.Scene.ITouchArea;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.util.HorizontalAlign;

import android.util.Log;

public class DevTools extends HUD implements IOnAreaTouchListener {

	DatabaseHandler dbh;
	Backup devTools;
	public Rectangle mask;
	
	private Font font;
	private Font bigFont;

	private Text save1;
	
	private Text load1;

	private int saveId = 0;
	private int loadId = 0;
	
	private Rectangle rect;
	
	public DevTools(Camera camera, Font font, Font bigFont, DatabaseHandler dbh, Backup devTools) {
		this.mCamera = camera;
		this.font = font;
		this.bigFont = bigFont;
		this.devTools = devTools;
		//this.app = app;
		this.dbh = dbh;
		//this.context = context;
		
		this.setup();
	}

		
	public void setup() {
		
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
		
		save1 = new Text(20, 50, this.bigFont, "S1", HorizontalAlign.LEFT);
		mask.attachChild(save1);
		
		Text load = new Text(20, 100, this.font, "LOAD", HorizontalAlign.LEFT);
		mask.attachChild(load);
		
		load1 = new Text(20, 130, this.bigFont, "L1", HorizontalAlign.LEFT);
		mask.attachChild(load1);	
	}
	
	public void display() {
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

			devTools.createStateFile("");
			devTools.createStateFiles("");
			devTools.savePlayerData("");
			Log.d("Clementime","DevToolsManager/hide: save in S" + saveId);

			saveId = 0;
			load = 2;
		} else if (loadId != 0) load = 2; 
		//loadStateFiles("");
		//loadPlayerData("");
		//Log.d("Clementime","DevToolsFrame/hide: load L" + loadId);

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
