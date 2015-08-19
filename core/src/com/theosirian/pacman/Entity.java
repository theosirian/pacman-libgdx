package com.theosirian.pacman;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Objects;

public class Entity {

	public enum Direction {
		NONE, UP, DOWN, LEFT, RIGHT;

		private Direction opposite;

		static {
			NONE.opposite = NONE;
			UP.opposite = DOWN;
			DOWN.opposite = UP;
			RIGHT.opposite = LEFT;
			LEFT.opposite = RIGHT;
		}

		public Direction getOppositeDirection() {
			return opposite;
		}

		public Vector2 getUnitVector(){
			switch (this) {
				case UP:
					return new Vector2(0, 1);
				case DOWN:
					return new Vector2(0, -1);
				case RIGHT:
					return new Vector2(1, 0);
				case LEFT:
					return new Vector2(-1, 0);
				default:
					return new Vector2(0, 0);
			}
		}

		public float toRotation(){
			switch (this) {
				case UP:
					return 90;
				case DOWN:
					return 270;
				case RIGHT:
					return 0;
				case LEFT:
					return 180;
				default:
					return 0;
			}
		}

		static Direction parseString(String str){
			str = str.trim().toUpperCase();
			switch (str) {
				case "UP":
					return Direction.UP;
				case "DOWN":
					return Direction.DOWN;
				case "RIGHT":
					return Direction.RIGHT;
				case "LEFT":
					return Direction.LEFT;
				case "NONE":
				default:
					return Direction.NONE;
			}
		}
	}

	protected TextureRegion currentFrame;
	protected TiledMapTileLayer collisionLayer;
	protected Vector2 position, size, origin, scale;
	protected float rotation;
	protected Direction direction;
	protected Rectangle bounds;

	public Entity(int x, int y, TiledMapTileLayer collisionLayer) {
		position = new Vector2(x, y);
		size = new Vector2(Settings.SPRITE_WIDTH, Settings.SPRITE_HEIGHT);
		origin = new Vector2(Settings.SPRITE_WIDTH / 2, Settings.SPRITE_HEIGHT / 2);
		scale = new Vector2(1, 1);
		rotation = 0;
		setBounds(getX(), getY(), Settings.SPRITE_WIDTH, Settings.SPRITE_HEIGHT);
		this.collisionLayer = collisionLayer;
	}

	public void update(float delta){
		setBounds(getX(), getY(), Settings.SPRITE_WIDTH, Settings.SPRITE_HEIGHT);
	}

	public void draw(Batch batch) {
		batch.draw(currentFrame, position.x, position.y, origin.x, origin.y, size.x, size.y, scale.x, scale.y, rotation);
	}

	public void dispose() {

	}

	public boolean testCollision(Vector2 targetPosition) {
		TiledMapTileLayer.Cell c1 = collisionLayer.getCell((int) (targetPosition.x / 16f), (int) (targetPosition.y / 16f));
		TiledMapTileLayer.Cell c2 = collisionLayer.getCell((int) (targetPosition.x / 16f), (int) (Math.ceil(targetPosition.y / 16f)));
		TiledMapTileLayer.Cell c3 = collisionLayer.getCell((int) (Math.ceil(targetPosition.x / 16f)), (int) (targetPosition.y / 16f));
		TiledMapTileLayer.Cell c4 = collisionLayer.getCell((int) (Math.ceil(targetPosition.x / 16f)), (int) (Math.ceil(targetPosition.y / 16f)));
		if (c1 == null || Objects.equals(c1.getTile().getProperties().get("block", String.class), "0")) {
			if (c2 == null || Objects.equals(c2.getTile().getProperties().get("block", String.class), "0")) {
				if (c3 == null || Objects.equals(c3.getTile().getProperties().get("block", String.class), "0")) {
					if (c4 == null || Objects.equals(c4.getTile().getProperties().get("block", String.class), "0")) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public TiledMapTileLayer getCollisionLayer(){
		return this.collisionLayer;
	}

	public void setCollisionLayer(TiledMapTileLayer collisionLayer) {
		this.collisionLayer = collisionLayer;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction dir) {
		this.direction = dir;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public int getX() {
		return (int) position.x;
	}

	public void setX(int x) {
		this.position.x = x;
	}

	public int getY() {
		return (int) position.y;
	}

	public void setY(int y) {
		this.position.y = y;
	}

	public int getWidth() {
		return (int) size.x;
	}

	public void setWidth(int width) {
		this.size.x = width;
	}

	public int getHeight() {
		return (int) size.y;
	}

	public void setHeight(int height) {
		this.size.y = height;
	}

	public float getOriginX() {
		return origin.x;
	}

	public void setOriginX(float originX) {
		this.origin.x = originX;
	}

	public float getOriginY() {
		return origin.y;
	}

	public void setOriginY(float originY) {
		this.origin.y = originY;
	}

	public float getScaleX() {
		return scale.x;
	}

	public void setScaleX(float scaleX) {
		this.scale.x = scaleX;
	}

	public float getScaleY() {
		return scale.y;
	}

	public void setScaleY(float scaleY) {
		this.scale.y = scaleY;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public void setBounds(float x, float y, float width, float height) {
		if (bounds != null) {
			bounds.setX(x);
			bounds.setY(y);
			bounds.setWidth(width);
			bounds.setHeight(height);
		} else {
			bounds = new Rectangle(x, y, width, height);
		}
	}
}
