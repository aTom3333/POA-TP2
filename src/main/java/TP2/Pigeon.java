package TP2;

import java.awt.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Pigeon implements Entity, Runnable {
    private Point position;
    private World world;
    private Lock lock;
    private long lastTime;
    private boolean requestedDead = false;
    private boolean hungry = false;
    private boolean running = false;
    private Point dest;

    private final static float speed = 200; // pixel/second

    public Pigeon(Point position, World world) {
        this.position = position;
        this.world = world;
        lastTime = System.nanoTime();
        lock = new ReentrantLock();
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.GRAY);
        int r = 12;
        g.fillOval(position.x-r, position.y-r, 2*r+1, 2*r+1);
    }

    @Override
    public void run() {
        boolean dead = false;
        while(!dead) {
            double elaspsedTime = getElapsedTime();
            boolean wantToEat = false;
            world.getFoodLock().readLock().lock();
            if(world.getFreshFood() != null && (hungry || Math.random() < 0.2)) { // 20% to go to food
                running = false;
                hungry = true;
                if(moveTo(world.getFreshFood().getPosition(), elaspsedTime)) {
                    // Eat
                    wantToEat = true;
                }
            } else {
                runAway(elaspsedTime);
            }
            world.getFoodLock().readLock().unlock();
            if(wantToEat) {
                eat();
                hungry = false;
            }
            try {
                Thread.sleep(30);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            lock.lock();
            dead = requestedDead;
            lock.unlock();
        }
    }

    private double getElapsedTime() {
        long old = lastTime;
        lastTime = System.nanoTime();
        return (lastTime-old)/1_000_000_000.0;
    }

    private boolean moveTo(Point pos, double elapsedTime) {
        double max = elapsedTime * speed;
        boolean isAtLocation = false;
        lock.lock();
        double dist = pos.distance(position);
        if(dist > max) {
            // Make progress
            double newX = position.getX() + (pos.getX() - position.getX()) * max/dist;
            double newY = position.getY() + (pos.getY() - position.getY()) * max/dist;
            position.setLocation(newX, newY);
        } else {
            // Go to location
            position.setLocation(pos);
            isAtLocation = true;
        }
        lock.unlock();

        return isAtLocation;
    }

    private void eat() {
        world.getFoodLock().writeLock().lock();
        // Someone else could have eaten the food just before our eyes, we need to check their is still food
        if(world.getFreshFood() != null) {
            world.nonLockedDispawnFood(world.getFreshFood());
        }
        world.getFoodLock().writeLock().unlock();
    }

    public Lock getLock() {
        return lock;
    }

    public void kill() {
        lock.lock();
        requestedDead = true;
        lock.unlock();
    }

    private void runAway(double elapsedTime) {
        if(running) {
            if(moveTo(dest, elapsedTime))
                running = false;
        } else if(Math.random() < 0.01) { // 1% to begin moving randomly
            running = true;
            dest = world.randomPosNear(position, 400);
        }
    }
}
