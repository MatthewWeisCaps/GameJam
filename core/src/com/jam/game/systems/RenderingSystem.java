package com.jam.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jam.game.components.AnimationComponent;
import com.jam.game.components.BodyComponent;
import com.jam.game.components.StateComponent;
import com.jam.game.components.TextureComponent;
import com.jam.game.components.TransformComponent;
import com.jam.game.screens.GameScreen;
import com.jam.game.utils.Mappers;

public class RenderingSystem extends SortedIteratingSystem {

	static final float PPM = 32.0f; //Pixels per meter
	
    // this gets the height and width of our camera frustrum based off the width and height of the screen and our pixel per meter ratio
	static final float FRUSTUM_WIDTH = GameScreen.VIRTUAL_WIDTH;
    static final float FRUSTUM_HEIGHT = GameScreen.VIRTUAL_HEIGHT;
    
	public static final float PIXELS_TO_METERS = 1.0f / PPM;
	
//    public static Vector2 getScreenSizeInMeters(){
//        meterDimensions.set(Gdx.graphics.getWidth()*PIXELS_TO_METERS,
//                            Gdx.graphics.getHeight()*PIXELS_TO_METERS);
//        return meterDimensions;
//    }
    
//    public static Vector2 getScreenInPixels() {
//    	pixelDimensions.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//    	return pixelDimensions;
//    }
    
//    public static float pixelsToMeters(float pixelValue) {
//    	return pixelValue * PIXELS_TO_METERS;
//    }
    
    private SpriteBatch batch;
//    private OrthographicCamera cam;
    private FitViewport viewport;
    
    @SuppressWarnings("unchecked")
	public RenderingSystem(SpriteBatch batch) {
        // gets all entities with a TransofmComponent and TextureComponent
        super(Family.all(TransformComponent.class).one(TextureComponent.class, AnimationComponent.class).get(),
        		new ZComparator(), Priority.RENDER.PRIORITY);
     
        this.batch = batch;  // set our batch to the one supplied in constructor

        // set up the camera to match our screen size
//        cam = new OrthographicCamera();
        //cam.position.set(FRUSTUM_WIDTH, FRUSTUM_HEIGHT, 0);
        viewport = new FitViewport(GameScreen.VIRTUAL_WIDTH, GameScreen.VIRTUAL_HEIGHT);
        viewport.apply(true);
//        viewport.setScreenSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        viewport.setWorldSize(GameScreen.VIRTUAL_WIDTH, GameScreen.VIRTUAL_HEIGHT);
        
        shapeRenderer.setColor(Color.TAN);
    }

    @Override
    public void update(float deltaTime) {
    	super.update(deltaTime);
    	
    	//Sort render Queue based on Z index
//    	renderQueue.sort(comparator); // TODO
    	
    	//Update camera and sprite batch
    	viewport.getCamera().update();
    	
    	batch.setProjectionMatrix(viewport.getCamera().combined);
    	batch.enableBlending();
//    	batch.begin();
//    	
//    	//Each entity in our render queue
//    	for(Entity e : renderQueue) {
//    		TextureComponent tex = Mappers.textureMap.get(e);
//    		TransformComponent t = Mappers.transformMap.get(e);
//    		AnimationComponent a = Mappers.animationMap.get(e);
//    		BodyComponent body = Mappers.bodyMap.get(e);
//    		
//    		if (tex != null) {
//    			if(tex.region != null) {
//            		float width = tex.region.getRegionWidth();
//            		float height = tex.region.getRegionHeight();
//            		
//            		float originX = width/2.0f;
//            		float originY = height/2.0f;
//            		
//            		batch.draw(tex.region, t.pos.x - originX, t.pos.y - originY, originX, originY, width, height, pixelsToMeters(t.scale.x), pixelsToMeters(t.scale.y), 0);
//        		}
//    		} else if (a != null && body != null) {
//    			a.stateTime += deltaTime;
//    			a.animations.get(a.currentAnimation).draw(batch, body.b2dBody);
//    		} else {
//    			continue;
//    		}
//    	}
//    	
//    	batch.end();
//    	renderQueue.clear();
    }
    
    float factor = 1.0f;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
//		renderQueue.add(entity);
		batch.begin();
		
		TextureComponent tex = Mappers.textureMap.get(entity);
		TransformComponent t = Mappers.transformMap.get(entity);
		AnimationComponent a = Mappers.animationMap.get(entity);
		BodyComponent body = Mappers.bodyMap.get(entity);
		
		//state.beginThrowingRopeTime + 0.80f > GdxAI.getTimepiece().getTime()
		if (tex != null) {
			if(tex.region != null) {
        		float width = tex.region.getRegionWidth();
        		float height = tex.region.getRegionHeight();
        		
        		float originX = width/2.0f;
        		float originY = height/2.0f;
        		
        		float _factor = 1.0f;
        		
        		batch.draw(tex.region, t.pos.x - originX, t.pos.y - originY, originX, originY, width, height, _factor*(t.scale.x), _factor*(t.scale.y), 0);
        		
    		}
		} else if (a != null && body != null) {
			StateComponent state = Mappers.stateMap.get(entity);
			
			
//			if (!Mappers.playerMap.has(entity)) {
//				a.stateTime += deltaTime;
////				a.animations.get(a.currentAnimation).setScale(factor);
////				a.animations.get(a.currentAnimation).draw(batch, body.b2dBody);
//			} else {
			if (state != null && state.isThrowingRope) {
				batch.end();
				Gdx.gl.glLineWidth(2);
				shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
				shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//				shapeRenderer.setColor(Color.TAN); // done above
				
				Vector2 origin = null;
				Vector2 dest = null;
				
				if (state.ropeJointDef != null) {
					origin = state.ropeJointDef.bodyA.getWorldPoint(state.ropeJointDef.localAnchorA);
					dest = state.ropeJointDef.bodyB.getWorldPoint(state.ropeJointDef.localAnchorB);
				} else {
					System.out.println("here");
									
					origin = state.invalidThrowStart;
					dest = state.invalidThrowEnd;
					System.out.println(origin + "; " + dest);

				}
				
				
				float progress = (GdxAI.getTimepiece().getTime()-state.beginThrowingRopeTime)/PlayerControlSystem.ROPE_CAST_TIME;
				
				float x = MathUtils.lerp(origin.x, dest.x, progress);
				float y = MathUtils.lerp(origin.y, dest.y, progress);
				Vector2 target = new Vector2(x, y);
				
				
//						origin.interpolate(state.ropeJointDef.bodyB.getWorldPoint(state.ropeJointDef.localAnchorB),
//								(GdxAI.getTimepiece().getTime()-state.beginThrowingRopeTime)/PlayerControlSystem.ROPE_CAST_TIME,
//								Interpolation.linear);
				
//				System.out.println((GdxAI.getTimepiece().getTime()-state.beginThrowingRopeTime)/PlayerControlSystem.ROPE_CAST_TIME);
				shapeRenderer.line(origin, target);
						
//				System.out.println(state.ropeJointDef.bodyA.getWorldPoint(state.ropeJointDef.localAnchorA) + " to " + 
//						state.ropeJointDef.bodyA.getWorldPoint(state.ropeJointDef.localAnchorA)
//						.interpolate(state.ropeJointDef.bodyB.getWorldPoint(state.ropeJointDef.localAnchorB),
//								(GdxAI.getTimepiece().getTime()-state.beginThrowingRopeTime)/PlayerControlSystem.ROPE_CAST_TIME,
//						Interpolation.linear));
				shapeRenderer.end();
				Gdx.gl.glLineWidth(1);
				batch.begin();
//				a.stateTime += deltaTime;
//				if (a.currentAnimation == PlayerAnims.IDLE_LEFT || a.currentAnimation == PlayerAnims.JUMP_LEFT
//						|| a.currentAnimation == PlayerAnims.WALK_LEFT) {
//					a.animations.get(PlayerAnims.IDLE_LEFT).draw(batch, body.b2dBody);
//				} else {
//					a.animations.get(PlayerAnims.IDLE_RIGHT).draw(batch, body.b2dBody);
//				}
				
			} else if (state != null && state.isSwinging && state.ropeJoint != null) {
				batch.end();
				Gdx.gl.glLineWidth(2);
				shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
				shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//					shapeRenderer.setColor(Color.TAN); // done above
				shapeRenderer.line(state.ropeJoint.getAnchorA(), state.ropeJoint.getAnchorB());
				shapeRenderer.end();
				Gdx.gl.glLineWidth(1);
				batch.begin();
			}
		}
		
		a.stateTime += deltaTime;
//		a.animations.get(a.currentAnimation).setScale(PPM);
		a.animations.get(a.currentAnimation).draw(batch, body.b2dBody);
    	
		batch.end();
	}
	
	public OrthographicCamera getCamera() {
		return (OrthographicCamera) viewport.getCamera();
	}
	
	public SpriteBatch getBatch() {
		return batch;
	}
	
	public FitViewport getViewport() {
		return viewport;
	}

}
