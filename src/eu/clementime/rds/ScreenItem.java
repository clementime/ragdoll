package eu.clementime.rds;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import static eu.clementime.rds.Constants.SCALE;

public class ScreenItem extends Sprite {

	public int id;
	public int andEngineId;
	
	public int actionTake;
	public int actionLook;
	
	public boolean takeable;
	public boolean foreground;
	
	//public ScreenItem(int id, int x, int y, boolean take, boolean look, boolean talk, boolean exit, boolean takeable, boolean inventory, TextureRegion tr) {
	public ScreenItem(int id, float x, float y, int take, int look, boolean takeable, boolean foreground, TextureRegion tr) {
		super(x, y, tr.getWidth(), tr.getHeight(), tr);
		
		this.id = id;

		this.actionTake = take;
		this.actionLook = look;
		this.takeable = takeable;
		this.foreground = foreground;
		
		this.setScaleCenter(0,0);
		this.setScale(SCALE);
		this.setPosition(x * SCALE, y * SCALE);
	}
}