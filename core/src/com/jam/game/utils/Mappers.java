package com.jam.game.utils;

import com.badlogic.ashley.core.ComponentMapper;
import com.jam.game.components.*;

public class Mappers {
	public static ComponentMapper<AnimationComponent> animationMap = ComponentMapper.getFor(AnimationComponent.class);
	public static ComponentMapper<BodyComponent> bodyMap = ComponentMapper.getFor(BodyComponent.class);
	public static ComponentMapper<CollisionComponent> collisionMap = ComponentMapper.getFor(CollisionComponent.class);
	public static ComponentMapper<PlayerComponent> playerMap = ComponentMapper.getFor(PlayerComponent.class);
	public static ComponentMapper<StateComponent> stateMap = ComponentMapper.getFor(StateComponent.class);
	public static ComponentMapper<TextureComponent> textureMap = ComponentMapper.getFor(TextureComponent.class);
	public static ComponentMapper<TransformComponent> transformMap = ComponentMapper.getFor(TransformComponent.class);
	public static ComponentMapper<TypeComponent> typeMap = ComponentMapper.getFor(TypeComponent.class);

}
