package org.ryuu.gdx.physics.box2d;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.ryuu.gdx.physics.box2d.interfacecontact.InterfaceContactListener;

public class Box2dEngine implements Disposable {
    @Getter
    private final World world;
    @Getter
    private final Box2DDebugRenderer box2DDebugRenderer;
    @Getter
    private final Settings settings;
    private float stepTime = 0;

    public Box2dEngine(Settings settings) {
        if (settings == null) {
            throw new IllegalArgumentException("settings can't be null");
        }
        this.settings = settings;

        world = new World(settings.gravity, settings.isSleep);
        world.setContactListener(new InterfaceContactListener());
        box2DDebugRenderer = new Box2DDebugRenderer(
                false, false, false,
                false, false, false
        );
    }

    /**
     * @param deltaTime Gdx.graphics.getDeltaTime()
     */
    public void step(float deltaTime) {
        stepTime += Math.min(deltaTime, settings.maxStepTime); // avoid death spiral
        float fixedTimeStep = settings.fixedTimeStep;
        while (stepTime >= fixedTimeStep) {
            world.step(fixedTimeStep, settings.velocityIterations, settings.positionIterations);
            stepTime -= fixedTimeStep;
        }
    }

    public void render(Camera camera) {
        box2DDebugRenderer.render(world, camera.combined);
    }

    @Override
    public void dispose() {
        world.dispose();
        box2DDebugRenderer.dispose();
    }

    @AllArgsConstructor
    @ToString
    public static class Settings {
        public Vector2 gravity;
        public boolean isSleep;
        public float fixedTimeStep;
        public float maxStepTime;
        public int velocityIterations;
        public int positionIterations;
    }
}