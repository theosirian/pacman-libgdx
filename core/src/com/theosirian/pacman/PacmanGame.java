package com.theosirian.pacman;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import java.util.Objects;

public class PacmanGame extends ApplicationAdapter implements InputProcessor {

    private ShapeRenderer shape;
    private SpriteBatch batch;
    private TiledMap tiledMap;
    private MapProperties mapProperties;
    private OrthographicCamera camera;
    private TiledMapRenderer tiledMapRenderer;
    private PacmanActor petMan;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        tiledMap = new TmxMapLoader().load("pacmap1.tmx");
        mapProperties = tiledMap.getProperties();
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        TiledMapTileLayer collisionLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Map");
        petMan = new PacmanActor(9 * 16, 9 * 16, collisionLayer);
        Gdx.input.setInputProcessor(this);
        batch = new SpriteBatch();
        shape = new ShapeRenderer();
    }

    @Override
    public void resume() {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        petMan.update(Gdx.graphics.getDeltaTime());

        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0, l1 = mapProperties.get("width", Integer.class); i < l1; i++) {
            for (int j = 0, l2 = mapProperties.get("height", Integer.class); j < l2; j++) {
                TiledMapTileLayer.Cell c = ((TiledMapTileLayer) tiledMap.getLayers().get("Map")).getCell(i, j);
                if (Objects.equals(c.getTile().getProperties().get("block", String.class), "1")) {
                    shape.setColor(Color.RED);
                } else {
                    shape.setColor(Color.GREEN);
                }
                shape.rect(i * 16 + 6, j * 16 + 6, 4, 4);
            }
        }
        shape.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        petMan.draw(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        //petMan.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.W:
                petMan.setDirection(PacmanActor.Direction.UP);
                break;
            case Input.Keys.S:
                petMan.setDirection(PacmanActor.Direction.DOWN);
                break;
            case Input.Keys.A:
                petMan.setDirection(PacmanActor.Direction.LEFT);
                break;
            case Input.Keys.D:
                petMan.setDirection(PacmanActor.Direction.RIGHT);
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
