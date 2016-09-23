package com.theosirian.pacman;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.theosirian.pacman.entity.Entity;
import com.theosirian.pacman.entity.Pacman;
import com.theosirian.pacman.entity.Teleport;
import com.theosirian.pacman.pacdot.BigPacdot;
import com.theosirian.pacman.pacdot.BonusPacdot;
import com.theosirian.pacman.pacdot.Pacdot;
import com.theosirian.pacman.pacdot.SmallPacdot;
import com.theosirian.pacman.util.Settings;

import java.util.*;

import static com.theosirian.pacman.entity.Entity.Direction.*;

public class Game extends ApplicationAdapter implements InputProcessor {

	private SpriteBatch batch;
	private BitmapFont font;
	private TiledMap tiledMap;
	private OrthographicCamera camera;
	private TiledMapRenderer tiledMapRenderer;
	private Pacman pacman;
	private List<Pacdot> pacdots, destroyed;
	private List<Teleport> teleportPoints;
	private List<Vector2> playerSpawnPoints, fruitSpawnPoints, ghostSpawnPoints;
	private Random rand;
	private String[] mapFiles;
	private int currentMap;
	private float transition, transitionTime;
	private TiledMap transitionMap;
	private TiledMapRenderer transitionMapRenderer;
	private int pacdotCount, bonusPacdotCount;

	private float idleTime = 0.0f;
	private boolean playerIsMoving = false;

	private int pacmanGraph[][];
	private Queue<Entity.Direction> stepQueue = new LinkedList<>();
	private List<Entity.Direction> tryMoves = new ArrayList<>();
	private List<Integer> teleportHashes = new ArrayList<>();

	private Vector2 aiTargetPosition = new Vector2();
	private Pacdot aiPacdotTarget;


	@Override
	public void create() {
		Gdx.input.setInputProcessor(this);
		mapFiles = Gdx.files.internal(Settings.MAP_LIST_PATH).readString().replaceAll("\r", "").split("\n");
		batch = new SpriteBatch();
		rand = new Random();
		int width = 19 * 16;
		int height = 21 * 16;
		camera = new OrthographicCamera(width, height);
		camera.setToOrtho(false, width, height);
		camera.update();
		transition = 0;
		transitionTime = 2f;
		transitionMap = null;
		tryMoves.add(LEFT);
		tryMoves.add(RIGHT);
		tryMoves.add(UP);
		tryMoves.add(DOWN);
		loadTextures();
		loadMap(mapFiles[currentMap].trim());
		loadFont(Settings.FONT_PATH, Settings.FONT_SIZE);
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
		if (transition > 0) {
			camera.update();
			transitionMap.getLayers().get("Map").setOpacity(transition / transitionTime);
			transitionMapRenderer.setView(camera);
			transitionMapRenderer.render();
			tiledMap.getLayers().get("Map").setOpacity(1 - (transition / transitionTime));
			tiledMapRenderer.setView(camera);
			tiledMapRenderer.render();
			transition -= Gdx.graphics.getDeltaTime();
		} else {
			camera.update();
			tiledMapRenderer.setView(camera);
			tiledMapRenderer.render();
			idleTime += Gdx.graphics.getDeltaTime();
			if (playerIsMoving) {
				if (idleTime > 3.5f) {
					playerIsMoving = false;
					stepQueue.clear();
					aiTargetPosition.set(pacman.getX(), pacman.getY());
				}
			} else {
				Vector2 aiCurrPosition = new Vector2(pacman.getX(), pacman.getY());
				if (aiCurrPosition.epsilonEquals(aiTargetPosition, 0.001f)) {
					if (stepQueue.isEmpty()) {
						if (aiPacdotTarget == null || aiPacdotTarget.isDestroy())
							aiPacdotTarget = Ai.decidePacdot(pacman, pacdots);
						Ai.pathfind(pacmanGraph, pacman, stepQueue, teleportHashes, aiPacdotTarget);
						aiTargetPosition.set(pacman.getX(), pacman.getY());
						System.out.println("Pathfinding! " + stepQueue.size() + " new steps.");
						pacman.setDirection(NONE);
					} else {
						Entity.Direction dir = stepQueue.poll();
						pacman.setDirection(dir);
						aiTargetPosition.set(pacman.getX(), pacman.getY()).mulAdd(dir.getUnitVector(), pacman.getSpeed());
					}
				}
			}
		}
		pacman.update(Gdx.graphics.getDeltaTime());
		pacdots.forEach(p -> p.update(Gdx.graphics.getDeltaTime()));
		pacdots.stream().filter(Pacdot::isDestroy).forEach(p -> {
			destroyed.add(p);
			p.dispose();
		});
		pacdots.removeAll(destroyed);
		float percentage = ((float) pacdots.size()) / pacdotCount;
		switch (bonusPacdotCount) {
			case 0:
				if (percentage <= 0.7f) {
					Vector2 spawn = fruitSpawnPoints.get(rand.nextInt(fruitSpawnPoints.size()));
					pacdots.add(new BonusPacdot((int) spawn.x * 16, (int) spawn.y * 16, pacman));
					bonusPacdotCount++;
				}
				break;
			case 1:
				if (percentage <= 0.3f) {
					Vector2 spawn = fruitSpawnPoints.get(rand.nextInt(fruitSpawnPoints.size()));
					pacdots.add(new BonusPacdot((int) spawn.x * 16, (int) spawn.y * 16, pacman));
					bonusPacdotCount++;
				}
				break;
			default:
				break;
		}
		destroyed.clear();
		teleportPoints.forEach(tp -> tp.update(Gdx.graphics.getDeltaTime()));
		if (pacdots.isEmpty()) {
			transitionMap = tiledMap;
			transitionMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
			transition = transitionTime;
			loadMap(mapFiles[(currentMap = (currentMap + 1) % mapFiles.length)].trim());
		} else {
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			for (Pacdot p : pacdots) p.draw(batch);
			pacman.draw(batch);
			font.setColor(Color.WHITE);
			font.draw(batch, "SCORE: " + String.format("%04d", pacman.getScore()), 4, 12);
			batch.end();
		}
	}

	@Override
	public void dispose() {
		System.out.println("Disposing of Textures...");
		SmallPacdot.sprite.dispose();
		BigPacdot.sprite.dispose();
		BonusPacdot.sprite.dispose();
	}

	private boolean loadTextures() {
		SmallPacdot.sprite = new Texture(Gdx.files.internal("pacman-small-pacdot.png"));
		BigPacdot.sprite = new Texture(Gdx.files.internal("pacman-big-pacdot.png"));
		BonusPacdot.sprite = new Texture(Gdx.files.internal("pacman-bonus-pacdot.png"));
		return SmallPacdot.sprite != null && BigPacdot.sprite != null && BonusPacdot.sprite != null;
	}

	private boolean loadFont(String file, int size) {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(file));
		FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
		params.size = size;
		font = generator.generateFont(params);
		generator.dispose();
		return font != null;
	}

	private boolean loadMap(String mapFile) {
		tiledMap = new TmxMapLoader().load(mapFile);
		MapProperties mapProperties = tiledMap.getProperties();
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		TiledMapTileLayer collisionLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Map");
		TiledMapTileLayer objectLayer = ((TiledMapTileLayer) tiledMap.getLayers().get("Objects"));
		MapLayer teleportLayer = tiledMap.getLayers().get("Teleports");
		objectLayer.setOpacity(0);
		if (pacdots != null) {
			pacdots.forEach(Pacdot::dispose);
			pacdots.clear();
		} else {
			pacdots = new ArrayList<>();
		}
		if (destroyed != null) {
			destroyed.forEach(Pacdot::dispose);
			destroyed.clear();
		} else {
			destroyed = new ArrayList<>();
		}
		if (playerSpawnPoints != null) {
			playerSpawnPoints.clear();
		} else {
			playerSpawnPoints = new ArrayList<>();
		}
		if (fruitSpawnPoints != null) {
			fruitSpawnPoints.clear();
		} else {
			fruitSpawnPoints = new ArrayList<>();
		}
		if (ghostSpawnPoints != null) {
			ghostSpawnPoints.clear();
		} else {
			ghostSpawnPoints = new ArrayList<>();
		}
		if (teleportPoints != null) {
			teleportPoints.forEach(Teleport::dispose);
			teleportPoints.clear();
		} else {
			teleportPoints = new ArrayList<>();
		}
		for (int i = 0, l1 = mapProperties.get("width", Integer.class); i < l1; i++) {
			for (int j = 0, l2 = mapProperties.get("height", Integer.class); j < l2; j++) {
				TiledMapTileLayer.Cell c = objectLayer.getCell(i, j);
				if (c == null) continue;
				TiledMapTile t = c.getTile();
				if (t == null) continue;
				MapProperties properties = t.getProperties();
				if (properties == null) continue;
				String type = properties.get("type", String.class);
				if (type == null) continue;
				switch (type) {
					case "bigDot":
						pacdots.add(new BigPacdot(i * 16, j * 16, pacman));
						System.out.printf("Big Dot: {%d;%d}\n", i, j);
						break;
					case "smallDot":
						pacdots.add(new SmallPacdot(i * 16, j * 16, pacman));
						break;
					case "ghostSpawn":
						ghostSpawnPoints.add(new Vector2(i, j));
						System.out.printf("Ghost Spawn: {%d;%d}\n", i, j);
						break;
					case "fruitSpawn":
						fruitSpawnPoints.add(new Vector2(i, j));
						System.out.printf("Fruit Spawn: {%d;%d}\n", i, j);
						break;
					case "playerSpawn":
						playerSpawnPoints.add(new Vector2(i, j));
						System.out.printf("Player Spawn: {%d;%d}\n", i, j);
						break;
				}
			}
		}
		pacdotCount = pacdots.size();
		bonusPacdotCount = 0;
		Vector2 spawn = playerSpawnPoints.get(rand.nextInt(playerSpawnPoints.size()));
		if (pacman == null) {
			pacman = new Pacman((int) spawn.x * 16, (int) spawn.y * 16, collisionLayer, stepQueue, aiTargetPosition);
		} else {
			pacman.teleport((int) spawn.x * 16, (int) spawn.y * 16).stopMoving().setCollisionLayer(collisionLayer);
		}
		aiTargetPosition.set(pacman.getX(), pacman.getY());
		stepQueue.clear();
		int n = collisionLayer.getWidth() * collisionLayer.getHeight();
		pacmanGraph = new int[n][n];
		for (int i = 0; i < collisionLayer.getWidth(); i++) {
			for (int j = 0; j < collisionLayer.getHeight(); j++) {
				for (int k = 0; k < n; k++) {
					pacmanGraph[make_hash(i, j)][k] = 0;
				}
				for (Entity.Direction dir : tryMoves) {
					Vector2 uvec = dir.getUnitVector();
					int a = (int) (uvec.x + i);
					int b = (int) (uvec.y + j);
					TiledMapTileLayer.Cell cell = collisionLayer.getCell(a, b);
					if (cell != null && Objects.equals(cell.getTile().getProperties().get("block", String.class), "0")) {
						pacmanGraph[make_hash(i, j)][make_hash(a, b)] = 1;
					}
				}
			}
		}
		pacdots.forEach(p -> p.setPacman(pacman));
		teleportHashes.clear();
		for (MapObject m : teleportLayer.getObjects()) {
			if (m.getProperties().containsKey("x") && m.getProperties().containsKey("y") && m.getProperties().containsKey("targetX") && m.getProperties().containsKey("targetY") && m.getProperties().containsKey("outDirection")) {
				int x = m.getProperties().get("x", Float.class).intValue() / 16;
				int y = m.getProperties().get("y", Float.class).intValue() / 16;
				int targetX = Integer.parseInt(m.getProperties().get("targetX", String.class));
				int targetY = Integer.parseInt(m.getProperties().get("targetY", String.class));
				String outDirection = m.getProperties().get("outDirection", String.class);
				teleportPoints.add(new Teleport(x * 16, y * 16, targetX * 16, targetY * 16, Entity.Direction.parseString(outDirection), pacman));
				pacmanGraph[make_hash(x, y)][make_hash(targetX, targetY)] = 1;
				pacmanGraph[make_hash(targetX, targetY)][make_hash(x, y)] = 1;
				teleportHashes.add(make_hash(x, y));
				teleportHashes.add(make_hash(targetX, targetY));
				System.out.printf("Teleport: {%d;%d} -> {%d;%d} (%s)\n", x, y, targetX, targetY, outDirection);
			}
		}
		return true;
	}

	private int make_hash(int i, int j) {
		return i * pacman.getCollisionLayer().getHeight() + j;
	}

	@Override
	public boolean keyDown(int keycode) {
		//TODO: ADD MOVEMENT KEYS
		switch (keycode) {
			case Input.Keys.F1:
				pacman.setHeuristic(Pacman.Heuristic.EUCLIDEAN);
				break;
			case Input.Keys.F2:
				pacman.setHeuristic(Pacman.Heuristic.MANHATTAN);
				break;
			case Input.Keys.W:
			case Input.Keys.UP:
				pacman.setDirection(Pacman.Direction.UP);
				playerIsMoving = true;
				idleTime = 0.0f;
				break;
			case Input.Keys.S:
			case Input.Keys.DOWN:
				pacman.setDirection(Pacman.Direction.DOWN);
				playerIsMoving = true;
				idleTime = 0.0f;
				break;
			case Input.Keys.A:
			case Input.Keys.LEFT:
				pacman.setDirection(Pacman.Direction.LEFT);
				playerIsMoving = true;
				idleTime = 0.0f;
				break;
			case Input.Keys.D:
			case Input.Keys.RIGHT:
				pacman.setDirection(Pacman.Direction.RIGHT);
				playerIsMoving = true;
				idleTime = 0.0f;
				break;
			case Input.Keys.ESCAPE:
				Gdx.app.exit();
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
