package project;

import game.Dungeon;

public class RunnerTest {

    public RunnerTest(Dungeon d){
        RealTimeController controller = new RealTimeController(d);
        GameWindow gameWindow = new GameWindow(d,controller);
        controller.setGameWindow(gameWindow);
    }
}
