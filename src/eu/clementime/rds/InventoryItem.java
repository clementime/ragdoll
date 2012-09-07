package eu.clementime.rds;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

/**
* Item stored in doll inventory, with small sprite for normal view, big sprite for zoom or drag and drop,
* and inventory item sprite itself for... uh? for what? oh, yes, for the box containing the item!! A bit tricky, I confess.
* @author Cl&eacute;ment
* @version 1.0
*/
public class InventoryItem  extends Sprite {
	//public class InventoryItem extends Entity {

	public int id;
	public int andEngineId;
	public Sprite big;
	public Sprite small;
	
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