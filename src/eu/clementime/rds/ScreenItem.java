package eu.clementime.rds;

import static eu.clementime.rds.Constants.MARGIN_Y;
import static eu.clementime.rds.Constants.SET_BACKGROUND_POSITION_Y;
import static eu.clementime.rds.Constants.SCALE_POSITION;
import static eu.clementime.rds.Constants.LOG_ON;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.util.Log;

/**
* Items that are displayed on screen.
* @author Cl&eacute;ment
* @version 1.0
*/
public class ScreenItem extends Sprite {

	public int id;
	public int andEngineId;
	
	public int actionTake;
	public int actionLook;
	public int actionTalk;
	
	public boolean takeable;
	public boolean foreground;
	
	public float x;
	public float y;
	
	private static String className = "ScreenItem";
	
	//public ScreenItem(int id, int x, int y, boolean take, boolean look, boolean talk, boolean exit, boolean takeable, boolean inventory, TextureRegion tr) {
	public ScreenItem(int id, float x, float y, int take, int look, int talk, boolean takeable, boolean foreground, TextureRegion tr) {
		super(x, y, tr.getWidth(), tr.getHeight(), tr);
		
		this.id = id;

		this.actionTake = take;
		this.actionLook = look;
		this.actionTalk = talk;
		this.takeable = takeable;
		this.foreground = foreground;
		
		// scale every position depending mdpi/hdpi with SCALE_POSITION 
		// and then move on Y depending screen size using SET_BACKGROUND_POSITION_Y (background too big for screen) and MARGIN_Y (background smaller than screen)
		this.x = x * SCALE_POSITION;
		this.y = y * SCALE_POSITION - SET_BACKGROUND_POSITION_Y + MARGIN_Y;
		
		if (LOG_ON) Log.i("Clementime", className + "/Constructor(): x: " + this.x + " - y: " + this.y + " - scale: " + SCALE_POSITION + " - margin: " + MARGIN_Y + " y*scale: " + (y*SCALE_POSITION));
	}
}