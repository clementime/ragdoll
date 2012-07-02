package eu.clementime.rds;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class ScreenItem extends Sprite {

	public int id;
	public int andEngineId;

	public float x;
	public float y;
	
	public int actionTake;
	public int actionLook;
	public int actionTalk;
	
	public boolean takeable;
	public boolean foreground;
	
	//public ScreenItem(int id, int x, int y, boolean take, boolean look, boolean talk, boolean exit, boolean takeable, boolean inventory, TextureRegion tr) {
	public ScreenItem(int id, float x, float y, int take, int look, int talk, boolean takeable, boolean foreground, TextureRegion tr) {
		super(x, y, tr.getWidth(), tr.getHeight(), tr);
		
		this.id = id;
		
		this.x = x;
		this.y = y;

		this.actionTake = take;
		this.actionLook = look;
		this.actionTalk = talk;
		this.takeable = takeable;
		this.foreground = foreground;
	}
}