package project;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

public class InfoGUI extends Pane {
    GameInfo gameInfo;
    private Label lblHitsTaken = new Label("Hits taken : ");
    private Label lblStepsTaken = new Label("Steps taken : ");
    private Label lblDoorsOpened = new Label("Doors opened : ");
    private Label lblEnemiesKilled = new Label("Enemies killed : ");
    private Label lblBossesKilled = new Label("Bosses killed : ");
    private Label lblChestsOpened = new Label("Chests opened : ");
    private Label lblPoints = new Label("Points : ");
    private Label lblAttacksMade = new Label("Attacks made : ");
    private Label lblTimer = new Label("Time passed : ");


    public InfoGUI(GameInfo gameInfo, long timer){
        this.gameInfo = gameInfo;
        initGUI(timer);
    }

    private void initGUI(long timer){

        Canvas labelCanvas = new Canvas(500, 500); //TODO This will stay like this but it shoudl be responsive!!
        this.getChildren().add(labelCanvas);

        arrangeControls(lblHitsTaken, 20, 20, gameInfo.getHitsTaken());
        arrangeControls(lblStepsTaken, 20, 75, gameInfo.getStepsTaken());
        arrangeControls(lblDoorsOpened, 20, 130, gameInfo.getDoorsOpened());
        arrangeControls(lblEnemiesKilled, 20, 185, gameInfo.getEnemiesKilled());
        arrangeControls(lblBossesKilled, 20, 240, gameInfo.getBossesKilled());
        arrangeControls(lblChestsOpened, 20, 295, gameInfo.getChestsOpened());
        arrangeControls(lblPoints, 20, 350, gameInfo.getPoints());
        arrangeControls(lblAttacksMade, 20, 405, gameInfo.getAttacksMade());
        arrangeControls(lblTimer, 20, 455, timer);
        lblTimer.setText(lblTimer.getText() + " seconds");

        this.getChildren().add(lblHitsTaken);
        this.getChildren().add(lblStepsTaken);
        this.getChildren().add(lblDoorsOpened);
        this.getChildren().add(lblEnemiesKilled);
        this.getChildren().add(lblBossesKilled);
        this.getChildren().add(lblChestsOpened);
        this.getChildren().add(lblPoints);
        this.getChildren().add(lblAttacksMade);
        this.getChildren().add(lblTimer);

    }
    private void arrangeControls(Label obj, double xPos, double yPos, long score)
    {
        obj.setTranslateX(xPos);
        obj.setTranslateY(yPos);
        obj.setText(obj.getText() + score);
        obj.setFont(new Font("Serif", 30));
    }
}
