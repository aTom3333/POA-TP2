package TP2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class World {
    private Food freshFood;
    private List<Food> oldFood;
    private ReadWriteLock foodLock; // Lock for every food
    private List<Pigeon> pigeons;
    private AtomicReference<Dimension> dim;

    private static Random rand = new Random();

    public Dimension getDim() {
        return dim.get();
    }

    public void setDim(Dimension dim) {
        this.dim.set(dim);
    }

    public World() {
        oldFood = new ArrayList<>();
        foodLock = new ReentrantReadWriteLock(true);
        pigeons = new ArrayList<>();
        dim = new AtomicReference<>();
    }

    public void render(Graphics g) {
        foodLock.readLock().lock();
        if(freshFood != null)
            freshFood.render(g);
        oldFood.forEach(f -> f.render(g));
        foodLock.readLock().unlock();
        pigeons.forEach(p -> { // Safe because the pigeons list is only used within the main thread
            p.getLock().lock();
            p.render(g);
            p.getLock().unlock();
        });
    }

    public void spawnFood(Point position) {
        foodLock.writeLock().lock();
        if(freshFood != null)
            oldFood.add(freshFood);
        freshFood = new Food(position);
        foodLock.writeLock().unlock();
    }

    public void dispawnFood(Food food) {
        foodLock.writeLock().lock();
        nonLockedDispawnFood(food);
        foodLock.writeLock().unlock();
    }

    public void nonLockedDispawnFood(Food food) {
        if(food == freshFood)
            freshFood = null;
        else {
            oldFood.remove(food);
        }
    }

    public Food getFreshFood() {
        return freshFood;
    }

    public List<Food> getOldFood() {
        return oldFood;
    }

    public ReadWriteLock getFoodLock() {
        return foodLock;
    }

    public synchronized void spawnPigeon(Point position) {
        Pigeon newPig = new Pigeon(position, this);
        pigeons.add(newPig);
        new Thread(newPig).start();
    }

    public Point randomPos() {
        Dimension d = getDim();
        return new Point(rand.nextInt(d.width), rand.nextInt(d.height));
    }

    public Point randomPosNear(Point src, double thresh) {
        Dimension d = getDim();
        if(src.getX() < 0 || src.getX() > d.width || src.getY() < 0 || src.getY() > d.height)
            throw new RuntimeException("Point not inside world");
        Point p;
        do {
            p = randomPos();
        } while(p.distance(src) > thresh);
        return p;
    }
}
