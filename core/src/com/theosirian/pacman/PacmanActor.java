package com.theosirian.pacman;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Objects;

public class PacmanActor {

    private Animation anim;
    private TextureRegion currentFrame;
    private float animTime = 0f;

    public enum Direction {
        NONE, UP, DOWN, LEFT, RIGHT
    }

    private Direction direction;
    private Direction previousDirection;
    private Direction movementPrediction;
    private int movementPredictionCounter;

    private float rotation, speed;
    private Rectangle bounds;
    private TiledMapTileLayer collisionLayer;
    private Vector2 position, targetPosition, size, origin, scale;

    public PacmanActor(int x, int y, TiledMapTileLayer collisionLayer) {
        Texture texture = new Texture(Gdx.files.internal("pacman.png"));
        TextureRegion[] regions = new TextureRegion[3];
        for (int i = 0; i < 3; i++) {
            regions[i] = new TextureRegion(texture, 16 * i, 0, 16, 16);
        }
        anim = new Animation(0.1f, regions);
        this.collisionLayer = collisionLayer;
        position = new Vector2(x, y);
        targetPosition = new Vector2(x, y);
        size = new Vector2(16, 16);
        origin = new Vector2(8, 8);
        scale = new Vector2(1, 1);
        setRotation(0);
        setBounds(getX(), getY(), 16, 16);
        speed = 2;
        direction = Direction.NONE;
        previousDirection = Direction.NONE;
        movementPrediction = Direction.NONE;
        movementPredictionCounter = 0;
    }

    public void update(float delta) {
        animTime += delta;
        currentFrame = anim.getKeyFrame(direction != Direction.NONE ? animTime : 0, true);
        if (getTargetX() != getX() || getTargetY() != getY()) {
            position.set(targetPosition);
        } else {
            Vector2 wantToMove = Vector2.Zero;
            boolean noPrediction = true;
            if (movementPrediction != Direction.NONE) {
                wantToMove.set(position.cpy().mulAdd(getDirectionVector(movementPrediction), speed));
                if (canMoveTo(collisionLayer, wantToMove)) {
                    System.out.println("Movement Predicted.");
                    setDirection(movementPrediction);
                    movementPrediction = Direction.NONE;
                    movementPredictionCounter = 0;
                    noPrediction = false;
                } else if (movementPredictionCounter > 12) {
                    System.out.println("Movement Prediction Expired.");
                    movementPrediction = Direction.NONE;
                    movementPredictionCounter = 0;
                } else {
                    System.out.println("Trying Prediction Next Frame.");
                    movementPredictionCounter++;
                }
            }
            if (noPrediction) {
                wantToMove.set(position.cpy().mulAdd(getDirectionVector(direction), speed));
                if (canMoveTo(collisionLayer, wantToMove)) {
                    if (getX() != wantToMove.x || getY() != wantToMove.y) {
                        System.out.printf("{%d, %d}->{%.2f, %.2f}\n", getX(), getY(), wantToMove.x, wantToMove.y);
                    }
                    setDirection(direction);
                    targetPosition.set(wantToMove);
                } else {
                    wantToMove.set(position.cpy().mulAdd(getDirectionVector(previousDirection), speed));
                    if (canMoveTo(collisionLayer, wantToMove)) {
                        System.out.println("Skipping Blocked Direction.");
                        System.out.println("Activating Movement Prediction.");
                        movementPrediction = direction;
                        movementPredictionCounter = 0;
                        setDirection(previousDirection);
                        targetPosition.set(wantToMove);
                    }
                }
            }
        }
        setRotation(dirToDeg(direction));
        setBounds(getX(), getY(), getWidth(), getHeight());
    }

    public void draw(Batch batch) {
        batch.draw(currentFrame, position.x, position.y, origin.x, origin.y, size.x, size.y, scale.x, scale.y, rotation);
    }

    private static float dirToDeg(Direction dir) {
        switch (dir) {
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

    private static Vector2 getDirectionVector(Direction dir) {
        switch (dir) {
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

    private static boolean canMoveTo(TiledMapTileLayer collisionLayer, Vector2 targetPosition) {
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

    public Direction getDirection() {
        return direction;
    }

    public PacmanActor setDirection(Direction dir) {
        this.previousDirection = this.direction;
        this.direction = dir;
        return this;
    }

    public float getRotation() {
        return rotation;
    }

    public PacmanActor setRotation(float rotation) {
        this.rotation = rotation;
        return this;
    }

    public int getX() {
        return (int) position.x;
    }

    public PacmanActor setX(int x) {
        this.position.x = x;
        return this;
    }

    public int getY() {
        return (int) position.y;
    }

    public PacmanActor setY(int y) {
        this.position.y = y;
        return this;
    }

    public int getWidth() {
        return (int) size.x;
    }

    public PacmanActor setWidth(int width) {
        this.size.x = width;
        return this;
    }

    public int getHeight() {
        return (int) size.y;
    }

    public PacmanActor setHeight(int height) {
        this.size.y = height;
        return this;
    }

    public float getOriginX() {
        return origin.x;
    }

    public PacmanActor setOriginX(float originX) {
        this.origin.x = originX;
        return this;
    }

    public float getOriginY() {
        return origin.y;
    }

    public PacmanActor setOriginY(float originY) {
        this.origin.y = originY;
        return this;
    }

    public float getScaleX() {
        return scale.x;
    }

    public PacmanActor setScaleX(float scaleX) {
        this.scale.x = scaleX;
        return this;
    }

    public float getScaleY() {
        return scale.y;
    }

    public PacmanActor setScaleY(float scaleY) {
        this.scale.y = scaleY;
        return this;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public PacmanActor setBounds(Rectangle bounds) {
        this.bounds = bounds;
        return this;
    }

    public PacmanActor setBounds(float x, float y, float width, float height) {
        if (bounds != null) {
            bounds.setX(x);
            bounds.setY(y);
            bounds.setWidth(width);
            bounds.setHeight(height);
        } else {
            bounds = new Rectangle(x, y, width, height);
        }
        return this;
    }

    public int getTargetY() {
        return (int) targetPosition.y;
    }

    public PacmanActor setTargetY(int targetY) {
        this.targetPosition.y = targetY;
        return this;
    }

    public int getTargetX() {
        return (int) targetPosition.x;
    }

    public PacmanActor setTargetX(int targetX) {
        this.targetPosition.x = targetX;
        return this;
    }

}
