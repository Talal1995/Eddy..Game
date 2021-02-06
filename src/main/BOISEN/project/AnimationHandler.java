package project;

import javafx.animation.AnimationTimer;

public class AnimationHandler extends AnimationTimer{

    private  RealTimeController controller;
    private long lastUpdate;

    public AnimationHandler(RealTimeController cont) {

        this.controller = cont;
    }

    @Override
    public void start(){
        System.out.println("start");
        lastUpdate = System.nanoTime();
        super.start();
    }

    @Override
    public void handle(long now) {

        long elapsedNanoSeconds = now - lastUpdate;

        double elapsedSeconds = elapsedNanoSeconds / 1_000_000_000.0; //1 sec = 1 billion nanoseconds.

        if(elapsedSeconds > 0.5){
            controller.performEnemyAction();
            controller.updateArrows();
            lastUpdate = now;
        }


    }
}
