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
    
    private SpriteBatch batch;
//    private OrthographicCamera cam;
    private FitViewport viewport;
    
    @SuppressWarnings("unchecked")
	public RenderingSystem(SpriteBatch batch) {
        // gets all entities with a TransofmComponent and TextureComponent
        super(Family.all(TransformComponent.class).one(TextureComponent.class, AnimationComponent.class).get(),
        		new ZComparator(), Priority.RENDER.PRIORITY);
     
        this.batch = batch;  // set our batch to the one supplied in constructor

        viewport = new FitViewport(GameScreen.VIRTUAL_WIDTH, GameScreen.VIRTUAL_HEIGHT);

        
        shapeRenderer.setColor(Color.TAN);
    }

    @Override
    public void update(float deltaTime) {
    	super.update(deltaTime);
    	
    	//Update camera and sprite batch
    	viewport.getCamera().update();
    	
    	batch.setProjectionMatrix(viewport.getCamera().combined);
    	batch.enableBlending();
    }
    
    float factor = 1.0f;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    
	@Override
	protected void processEntity(Entity entity, float deltaTime) {	
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
			
			if (state != null && state.isThrowingRope) {
				batch.end();
				Gdx.gl.glLineWidth(2);
				shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
				shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
				
				Vector2 origin = null;
				Vector2 dest = null;
				
				if (state.ropeJointDef != null) {
					origin = state.ropeJointDef.bodyA.getWorldPoint(state.ropeJointDef.localAnchorA);
					dest = state.ropeJointDef.bodyB.getWorldPoint(state.ropeJointDef.localAnchorB);
				} else {	
					origin = state.invalidThrowStart;
					dest = state.invalidThrowEnd;
				}
				
				
				float progress = (GdxAI.getTimepiece().getTime()-state.beginThrowingRopeTime)/PlayerControlSystem.ROPE_CAST_TIME;
				
				float x = MathUtils.lerp(origin.x, dest.x, progress);
				float y = MathUtils.lerp(origin.y, dest.y, progress);
				Vector2 target = new Vector2(x, y);
				
				
				shapeRenderer.line(origin, target);
						
				shapeRenderer.end();
				Gdx.gl.glLineWidth(1);
				batch.begin();
				
			} else if (state != null && state.isSwinging && state.ropeJoint != null) {
				batch.end();
				Gdx.gl.glLineWidth(2);
				shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
				shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
				shapeRenderer.line(state.ropeJoint.getAnchorA(), state.ropeJoint.getAnchorB());
				shapeRenderer.end();
				Gdx.gl.glLineWidth(1);
				batch.begin();
			}
		}
		
		a.stateTime += deltaTime;
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
