package gui.controls;

import javafx.application.Platform;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

/**
 * A class to take care of the non-resizable canvas problem. Adapted from
 * {@link http://dlsc.com/2014/04/10/javafx-tip-1-resizable-canvas/}.
 * 
 * @author Dirk Lennermann
 * @author Johan Holmberg, Malmö University
 */
public class ResizableCanvas extends Canvas {
	
	private Image image;

	public ResizableCanvas() {
		// Redraw canvas when size changes.
		widthProperty().addListener(evt -> draw());
		heightProperty().addListener(evt -> draw());
		draw();
	}
	
	/**
	 * Draws an image on this canvas.
	 * 
	 * @param image The image to draw.
	 */
	public void draw(Image image) {
		this.image = image;
		Platform.runLater(() -> {
			draw();
		});
	}
	
	/**
	 * Draws an image on this canvas.
	 * 
	 * @param image The image to draw.
	 */
	public void drawRotationThingie(Image image) {
		this.image = image;
		Platform.runLater(() -> {
			drawRotationThingie();
		});
	}

	private void draw() {
		double width = getWidth();
		double height = getHeight();
		double size = Math.min(width, height);

		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, width, height);

		if (image == null) {
			gc.setStroke(Color.RED);
			gc.strokeLine(0, 0, width, height);
			gc.strokeLine(0, height, width, 0);
		} else {
//			gc.drawImage(image, width/2 - size/2, height/2 - size/2, size, size);
			gc.drawImage(image, 0, 0, width, height);
		}
	}
	
	private void drawRotationThingie() {
		double width = getWidth();
		double height = getHeight();
		double size = Math.min(width, height);

		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, width, height);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);

		if (image == null) {
			gc.setStroke(Color.RED);
			gc.strokeLine(0, 0, width, height);
			gc.strokeLine(0, height, width, 0);
		} else {
//			gc.drawImage(image, width/2 - size/2, height/2 - size/2, size, size);
//			gc.drawImage(image, width/2.0, height/2.0, size, size);
			gc.drawImage(image, size, size);
		}
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	public double prefWidth(double height) {
		return getWidth();
	}

	@Override
	public double prefHeight(double width) {
		return getHeight();
	}
}