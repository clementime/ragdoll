package eu.clementime.rds;

import static eu.clementime.rds.Constants.MARGIN_Y;
import static eu.clementime.rds.Constants.SET_BACKGROUND_POSITION_Y;
import static eu.clementime.rds.Constants.SCALE_POSITION;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class ScreenItem extends Sprite {

	public int id;
	public int andEngineId;
	
	public int actionTake;
	public int actionLook;
	
	public boolean takeable;
	public boolean foreground;
	
	public float x;
	public float y;
	
	//public ScreenItem(int id, int x, int y, boolean take, boolean look, boolean talk, boolean exit, boolean takeable, boolean inventory, TextureRegion tr) {
	public ScreenItem(int id, float x, float y, int take, int look, boolean takeable, boolean foreground, TextureRegion tr) {
		super(x, y, tr.getWidth(), tr.getHeight(), tr);
		
		this.id = id;

		this.actionTake = take;
		this.actionLook = look;
		this.takeable = takeable;
		this.foreground = foreground;
		
		this.x = x;
		this.y = y;
	}
}