package gui.controls;

import java.io.IOException;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

/**
 * This control is used to display a labeled image.
 * 
 * @author Johan Holmberg, Malmö University
 * @author Chelsi Nolasco, Malmö University
 * @author Axel Österman, Malmö University
 */
public class LabeledCanvas extends BorderPane {
	@FXML private ResizableCanvas canvas;
	@FXML private AnchorPane canvasPane;
	@FXML private BorderPane rootPane;

	private Image rotatingThingie;
	private ImageView actualRotatingThingie;
	private RotateTransition transition;
	
	private GraphicsContext gc;
	private boolean waiting = true;
	
	/**
	 * Creates an instance of this class.
	 */
	public LabeledCanvas() {
		super();
		init("");
	}
	
	/**
	 * Creates an instance of this class.
	 * 
	 * @param label The control's label.
	 */
	public LabeledCanvas(String label) {
		super();
		init(label);
	}
	
	private void init(String label) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
				"/gui/controls/LabeledCanvas.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
		
		rootPane.setMinSize(0, 0);
		canvasPane.setMinSize(0, 0);
		
		canvas.widthProperty().bind(canvasPane.widthProperty());
		canvas.heightProperty().bind(canvasPane.heightProperty());
		canvasPane.setPrefSize(rootPane.widthProperty().doubleValue(), rootPane.heightProperty().doubleValue());
		
		getStyleClass().add("labeled-canvas");

		gc = canvas.getGraphicsContext2D();
		
//		actualRotatingThingie = new ImageView();
		rotatingThingie = new Image("/graphics/waiting.png");
//		actualRotatingThingie.setImage(rotatingThingie);
		transition = new RotateTransition(Duration.millis(5000), canvas);
		transition.setInterpolator(Interpolator.LINEAR);
		transition.setFromAngle(0);
		transition.setToAngle(360);
		transition.setCycleCount(Animation.INDEFINITE);
		
//		waitForImage(true);
//		
//		addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
//
//			@Override
//			public void handle(MouseEvent event) {
//				highlight(true);
//			}
//			
//		});
//		addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
//
//			@Override
//			public void handle(MouseEvent event) {
//				highlight(false);
//			}
//			
//		});
	}
	
	/**
	 * Displays an image on the canvas.
	 * 
	 * @param image An image.
	 */
	public void draw(Image image) {
		if (image == null) {
			waitForImage(true);
		} else {
			waitForImage(false);
			canvas.draw(image);
		}
	}
	
	/**
	 * Returns the canvas's graphics context.
	 * 
	 * @return A graphics context.
	 */
	public GraphicsContext getGraphicsContext() {
		return gc.getCanvas().getGraphicsContext2D();
	}
	
	/**
	 * Gets the label's text.
	 * 
	 * @return The label's text value.
	 */
    public String getText() {
        return null;
    }
	
	/**
	 * Sets the label's text.
	 * 
	 * @param label The text to display.
	 */
    public void setText(String value) {

    }

    /**
     * Gets the label's text property.
     * 
     * @return The label's text property.
     */
    public StringProperty textProperty() {
        return  null;
    }
    
    /**
     * Highlights the control.
     * 
     * @param state True if highlighted, otherwise false.
     */
    private void highlight(boolean state) {
    	if (state && !waiting) {
    		setStyle("-fx-border-width: 2px; -fx-border-color: #6b87f9");
    	} else {
    		setStyle("-fx-border-width: 0px");
    	}
    }
    
    
    /**
     * Waits for a new image and displays a rotating wheel.
     * 
     * @param state True if waiting, otherwise false.
     */
    private void waitForImage(boolean state) {
    	waiting = state;
    	
    	if (waiting) {
    		canvas.draw(rotatingThingie);
    		transition.play();
    	} else {
    		transition.stop();
    		
    		RotateTransition rt = new RotateTransition(Duration.ONE, canvas);
    		rt.setToAngle(0);
    		rt.setCycleCount(1);
    		rt.play();
    	}
    }
    
    public void resizeRotatingThingie()
    {
//    	actualRotatingThingie = new Image(ro)
    	double minSize = Math.min(getPrefHeight(), getPrefWidth());
    	System.out.println(minSize);
    	actualRotatingThingie.setFitWidth(5);
    	actualRotatingThingie.setFitHeight(50);
    	actualRotatingThingie.setPreserveRatio(true);
    	actualRotatingThingie.setSmooth(true);
        actualRotatingThingie.setCache(true); 
//    	rotatingThingie = new Image("/graphics/waiting.png", minSize, minSize, true, true);
    }
}
