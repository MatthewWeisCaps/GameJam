package com.jam.game.systems;

import java.util.Comparator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.jam.game.components.AnimationComponent;
import com.jam.game.components.BodyComponent;
import com.jam.game.components.TextureComponent;
import com.jam.game.components.TransformComponent;

import Utils.Mappers;

public class RenderingSystem extends SortedIteratingSystem {

	static final float PPM = 32.0f; //Pixels per meter
	
    // this gets the height and width of our camera frustrum based off the width and height of the screen and our pixel per meter ratio
	static final float FRUSTUM_WIDTH = Gdx.graphics.getWidth()/PPM;
    static final float FRUSTUM_HEIGHT = Gdx.graphics.getHeight()/PPM;
    
	public static final float PIXELS_TO_METERS = 1.0f / PPM;
	
	// static method to get screen width in meters
    private static Vector2 meterDimensions = new Vector2();
    private static Vector2 pixelDimensions = new Vector2();
    public static Vector2 getScreenSizeInMeters(){
        meterDimensions.set(Gdx.graphics.getWidth()*PIXELS_TO_METERS,
                            Gdx.graphics.getHeight()*PIXELS_TO_METERS);
        return meterDimensions;
    }
    
    public static Vector2 getScreenInPixels() {
    	pixelDimensions.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    	return pixelDimensions;
    }
    
    public static float pixelsToMeters(float pixelValue) {
    	return pixelValue * PIXELS_TO_METERS;
    }
    
    private SpriteBatch batch;
    private Array<Entity> renderQueue;
    private Comparator<Entity> comparator;
    private static OrthographicCamera cam;
    
    private ComponentMapper<TextureComponent> textureM;
    private ComponentMapper<TransformComponent> transformM;
    
    @SuppressWarnings("unchecked")
	public RenderingSystem(SpriteBatch batch) {
        // gets all entities with a TransofmComponent and TextureComponent
        super(Family.all(TransformComponent.class).one(TextureComponent.class, AnimationComponent.class).get(), new ZComparator());
        
        //creates out componentMappers
        textureM = ComponentMapper.getFor(TextureComponent.class);
        transformM = ComponentMapper.getFor(TransformComponent.class);

        // create the array for sorting entities
        renderQueue = new Array<Entity>();
     
        this.batch = batch;  // set our batch to the one supplied in constructor

        // set up the camera to match our screen size
        cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        cam.position.set(FRUSTUM_WIDTH / 2f, FRUSTUM_HEIGHT / 2f, 0);
    }

    @Override
    public void update(float deltaTime) {
    	super.update(deltaTime);
    	
    	//Sort render Queue based on Z index
    	renderQueue.sort(comparator); // TODO
    	
    	//Update camera and sprite batch
    	cam.update();
    	batch.setProjectionMatrix(cam.combined);
    	batch.enableBlending();
    	batch.begin();
    	
    	//Each entity in our render queue
    	for(Entity e : renderQueue) {
    		TextureComponent tex = textureM.get(e);
    		TransformComponent t = transformM.get(e);
    		AnimationComponent a = Mappers.animationMap.get(e);
    		BodyComponent body = Mappers.bodyMap.get(e);
    		
    		if (tex != null) {
    			if(tex.region != null) {
            		float width = tex.region.getRegionWidth();
            		float height = tex.region.getRegionHeight();
            		
            		float originX = width/2.0f;
            		float originY = height/2.0f;
            		
            		batch.draw(tex.region, t.pos.x - originX, t.pos.y - originY, originX, originY, width, height, pixelsToMeters(t.scale.x), pixelsToMeters(t.scale.y), 0);
        		}
    		} else if (a != null && body != null) {
    			a.stateTime += deltaTime;
    			a.animations.get(a.currentAnimation).draw(batch, body.b2dBody);
    		} else {
    			continue;
    		}
    	}
    	
    	batch.end();
    	renderQueue.clear();
    }
    
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		renderQueue.add(entity);
	}
	
	public static OrthographicCamera getCamera() {
		return cam;
	}

}
