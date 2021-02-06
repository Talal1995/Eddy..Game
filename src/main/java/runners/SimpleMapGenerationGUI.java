package runners;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import collectors.GenerationCollector;
import collectors.MapCollector;
import collectors.RenderedMapCollector;
import game.Game;
import gui.InteractiveGUIController;
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
import util.eventrouting.EventRouter;
import util.eventrouting.events.Start;

/**
 * This class just creates a map from any specific method.
 * 
 * @author Alberto Alvarez, Malmö University
 */
public class SimpleMapGenerationGUI extends Application {

	final static Logger logger = LoggerFactory.getLogger(InteractiveGUI.class);
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
	public void start(Stage stage) 
	{
		EventRouter router = EventRouter.getInstance();
		
		// Set up a new game
		game = new Game();
//		router.postEvent(new Start(6));

		MapRenderer.getInstance();
		
		
		Parent root;
		
		stage.setOnCloseRequest(e ->{
			Platform.exit();
			System.exit(0);
		});
		
		try {
			root = FXMLLoader.load(getClass().getResource("/gui/SimpleMapGUI.fxml"));

			Scene scene = new Scene(root, 1024, 500);
			//stage.getIcons().add(new Image(getClass().getResourceAsStream("/graphics/icon.png"))); 
			stage.setTitle("SINGLE MAP GENERATOR");
			stage.setScene(scene);
			stage.show();



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
