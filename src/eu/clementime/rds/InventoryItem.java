package eu.clementime.rds;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class InventoryItem  extends Sprite {
	//public class InventoryItem extends Entity {

	public int id;
	public int andEngineId;
	public Sprite big;
	public Sprite small;
	
	public float scaledCenterX;
	public float scaledWidth;
	public float scaledHeight;
	
	public InventoryItem(int id, Sprite big, Sprite small, TextureRegion box) {
		super(0, 0, box.getWidth(), box.getHeight(), box);
		
		this.id = id;
		this.big = big;
		this.small = small;		
	}
	
	public void setPosition(int x, int y) {
		super.setPosition(x, y);
	}
	
	public void setAndEngineId(int id) {
		this.andEngineId = id;
	}
}