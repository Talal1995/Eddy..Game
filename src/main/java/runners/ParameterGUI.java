package runners;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import collectors.GenerationCollector;
import collectors.MapCollector;
import collectors.RenderedMapCollector;
import game.Game;
import gui.utils.MapRenderer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import util.config.ConfigurationUtility;
import util.config.MissingConfigurationException;

/**
 * This class is simply a program launcher.
 * 
 * @author Johan Holmberg, Malmö University
 */
public class ParameterGUI extends Application {

	final static Logger logger = LoggerFactory.getLogger(ParameterGUI.class);
	private static ConfigurationUtility config;

	private Game game;
	private MapCollector mapCollector;
	private RenderedMapCollector renderedMapCollector;
	private GenerationCollector generationCollector;

	/**
	 * This is the GUI entry point.
	 * 
	 * @param args Those arguments aren't used.
	 */
	public static void main(String[] args) {
		logger.info("Starting program");
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/gui/ParameterGUI.fxml"));

			Scene scene = new Scene(root,800, 600);
			stage.getIcons().add(new Image(getClass().getResourceAsStream("/graphics/icon.png"))); 
			stage.setTitle("Eddy - Evolutionary Dungeon Designer");
			stage.setScene(scene);
			stage.show();

			// Set up a new game
			game = new Game();

			// Set up a bunch of collectors
			mapCollector = new MapCollector();
			renderedMapCollector = new RenderedMapCollector();
			generationCollector = new GenerationCollector();

			MapRenderer.getInstance();

		} catch (Exception e) {
			logger.error("Couldn't load GUI: " + e.getMessage(), e);
			System.exit(0);
		}
	}

	@Override
	public void stop(){
		game.stop();
	}
}
