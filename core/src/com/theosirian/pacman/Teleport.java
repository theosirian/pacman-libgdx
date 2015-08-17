package com.theosirian.pacman;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Teleport {

    protected Vector2 position, teleportPosition, size, origin, scale, offset;
    protected float rotation;
    protected Rectangle bounds;

    protected Pacman pacman;

    public Teleport(int x, int y, int targetX, int targetY, Pacman.Direction outDirection, Pacman pacman) {
        position = new Vector2(x, y);
        teleportPosition = new Vector2(targetX, targetY);
        size = new Vector2(2, 2);
        origin = new Vector2(8, 8);
        scale = new Vector2(1, 1);
        setRotation(0);
        offset = outDirectionToOffset(outDirection);
        setBounds(getX() + offset.x, getY() + offset.y, 16, 16);
        this.pacman = pacman;
    }

    public void update(float delta) {
        setBounds(getX() + offset.x, getY() + offset.y, 16, 16);
        if (bounds.contains(pacman.getBounds())) {
            pacman.teleport((int) (teleportPosition.x), (int) (teleportPosition.y));
            System.out.println("Teleport!");
        }
    }

    public void draw(Batch batch) {

    }

    public void dispose() {

    }

    public static Vector2 outDirectionToOffset(Pacman.Direction dir) {
        switch (dir) {
            case UP:
                return new Vector2(0, 16);
            case DOWN:
                return new Vector2(0, -16);
            case RIGHT:
                return new Vector2(16, 0);
            case LEFT:
                return new Vector2(-16, 0);
            case NONE:
            default:
                return Vector2.Zero;
        }
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
