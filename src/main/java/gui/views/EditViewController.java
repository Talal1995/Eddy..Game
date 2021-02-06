package gui.views;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import finder.geometry.Point;
import finder.patterns.Pattern;
import finder.patterns.micro.Connector;
import finder.patterns.micro.Corridor;
import finder.patterns.micro.Chamber;
import game.ApplicationConfig;
import game.Room;
import game.TileTypes;
import game.Game.MapMutationType;
import gui.controls.Drawer;
import generator.algorithm.Algorithm.AlgorithmTypes;
import gui.controls.InteractiveMap;
import gui.controls.LabeledCanvas;
import gui.controls.Modifier;
import gui.utils.MapRenderer;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import util.config.ConfigurationUtility;
import util.config.MissingConfigurationException;
import util.eventrouting.EventRouter;
import util.eventrouting.Listener;
import util.eventrouting.PCGEvent;
import util.eventrouting.events.MapUpdate;
import util.eventrouting.events.StartMapMutate;

/**
 * his class controls the interactive application's edit view.
 * 
 * @author Johan Holmberg, Malmö University
 */
public class EditViewController extends BorderPane implements Listener {
	
	@FXML private List<LabeledCanvas> mapDisplays;
	@FXML private StackPane mapPane;
	@FXML private GridPane legend;
	@FXML private ToggleGroup brushes;
	@FXML private ToggleButton lockBrush;
	@FXML private ToggleButton patternButton;
	@FXML private ToggleButton lockButton;
	@FXML private ToggleButton zoneButton;
	@FXML private Slider zoneSlider;
	
	private InteractiveMap mapView;
	private Canvas patternCanvas;
	private Canvas warningCanvas;
	private Canvas zoneCanvas;
	private Canvas lockCanvas;
	private Canvas brushCanvas;
	//test canvas
	private Canvas heatMapCanvas;
	
	private boolean isActive = false;
	private boolean isFeasible = true;
	private TileTypes brush = null;
	private HashMap<Integer, Room> rooms = new HashMap<Integer, Room>();
	private int nextMap = 0;
	
	private MapRenderer renderer = MapRenderer.getInstance();
	private static EventRouter router = EventRouter.getInstance();
	private final static Logger logger = LoggerFactory.getLogger(EditViewController.class);
	private ApplicationConfig config;
	
	public Drawer myBrush;

	/**
	 * Creates an instance of this class.
	 */
	public EditViewController() {
		super();
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
				"/gui/interactive/EditView.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
			config = ApplicationConfig.getInstance();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		} catch (MissingConfigurationException e) {
			logger.error("Couldn't read config file.");
		}
		
		router.registerListener(this, new MapUpdate(null));
		
		myBrush = new Drawer();
		myBrush.AddmodifierComponent("Lock", new Modifier(lockBrush));
		
		init();
		
		zoneSlider.valueProperty().addListener((obs, oldval, newVal) -> { 
			redrawPatterns(mapView.getMap());
			});
	}
	
	/**
	 * Initialises the edit view.
	 */
	private void init() {
		initMapView();
		initMiniMaps();
		initLegend();
	}

	/**
	 * Initialises the map view and creates canvases for pattern drawing and
	 * infeasibility notifications.
	 */
	private void initMapView() {
		int width = 420;
		int height = 420;
		
		mapView = new InteractiveMap();
		StackPane.setAlignment(mapView, Pos.CENTER);
		mapView.setMinSize(width, height);
		mapView.setMaxSize(width, height);
		mapPane.getChildren().add(mapView);
		
		heatMapCanvas = new Canvas(width, height);
		StackPane.setAlignment(heatMapCanvas, Pos.CENTER);
		mapPane.getChildren().add(heatMapCanvas);
		heatMapCanvas.setVisible(true);
		heatMapCanvas.setMouseTransparent(true);
		heatMapCanvas.setOpacity(0.8f);
		
		brushCanvas = new Canvas(width, height);
		StackPane.setAlignment(brushCanvas, Pos.CENTER);
		mapPane.getChildren().add(brushCanvas);
		brushCanvas.setVisible(false);
		brushCanvas.setMouseTransparent(true);
		brushCanvas.setOpacity(1.0f);
		
		lockCanvas = new Canvas(width, height);
		StackPane.setAlignment(lockCanvas, Pos.CENTER);
		mapPane.getChildren().add(lockCanvas);
		lockCanvas.setVisible(false);
		lockCanvas.setMouseTransparent(true);
		lockCanvas.setOpacity(0.4f);
		
		zoneCanvas = new Canvas(width, height);
		StackPane.setAlignment(zoneCanvas, Pos.CENTER);
		mapPane.getChildren().add(zoneCanvas);
		zoneCanvas.setVisible(false);
		zoneCanvas.setMouseTransparent(true);
		
		patternCanvas = new Canvas(width, height);
		StackPane.setAlignment(patternCanvas, Pos.CENTER);
		mapPane.getChildren().add(patternCanvas);
		patternCanvas.setVisible(false);
		patternCanvas.setMouseTransparent(true);
		
		warningCanvas = new Canvas(width, height);
		StackPane.setAlignment(warningCanvas, Pos.CENTER);
		mapPane.getChildren().add(warningCanvas);
		warningCanvas.setVisible(false);
		warningCanvas.setMouseTransparent(true);
		
		GraphicsContext gc = warningCanvas.getGraphicsContext2D();
		gc.setStroke(Color.rgb(255, 0, 0, 1.0));
		gc.setLineWidth(3);
		gc.strokeRect(1, 1, width - 1, height - 1);
		gc.setLineWidth(1);
		gc.setStroke(Color.rgb(255, 0, 0, 0.9));
		gc.strokeRect(3, 3, width - 6, height - 6);
		gc.setStroke(Color.rgb(255, 0, 0, 0.8));
		gc.strokeRect(4, 4, width - 8, height - 8);
		gc.setStroke(Color.rgb(255, 0, 0, 0.7));
		gc.strokeRect(5, 5, width - 10, height - 10);
		gc.setStroke(Color.rgb(255, 0, 0, 0.6));
		gc.strokeRect(6, 6, width - 12, height - 12);
		gc.setStroke(Color.rgb(255, 0, 0, 0.5));
		gc.strokeRect(7, 7, width - 14, height - 14);
		gc.setStroke(Color.rgb(255, 0, 0, 0.4));
		gc.strokeRect(8, 8, width - 16, height - 16);
		gc.setStroke(Color.rgb(255, 0, 0, 0.3));
		gc.strokeRect(9, 9, width - 18, height - 18);
		gc.setStroke(Color.rgb(255, 0, 0, 0.2));
		gc.strokeRect(10, 10, width - 20, height - 20);
		gc.setStroke(Color.rgb(255, 0, 0, 0.1));
		gc.strokeRect(11, 11, width - 22, height - 22);
	}

	/**
	 * Intialises the mini map view.
	 */
	private void initMiniMaps() {
		mapView.addEventFilter(MouseEvent.MOUSE_CLICKED, new EditViewEventHandler());
//		mapView.addEventFilter(MouseEvent.MOUSE_ENTERED, new EditViewEventHandler());
		mapView.addEventFilter(MouseEvent.MOUSE_MOVED, new EditViewMouseHover());
		getMap(0).addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> {
			replaceMap(0);
		});
		getMap(1).addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> {
			replaceMap(1);
		});
		getMap(2).addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> {
			replaceMap(2);
		});
		getMap(3).addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> {
			replaceMap(3);
		});
		resetMiniMaps();
	}
	
	/**
	 * Initialises the legend view.
	 */
	private void initLegend() {
		ConfigurationUtility c = config.getInternalConfig();
		
		legend.setVgap(10);
		legend.setHgap(10);
		legend.setPadding(new Insets(10, 10, 10, 10));
		
		Label title = new Label("Pattern legend");
		title.setStyle("-fx-font-weight: bold");
		legend.add(title, 0, 0, 2, 1);
		
		legend.add(new ImageView(new Image(c.getString("map.tiles.doorenter"), 40, 40, false, false)), 0, 1);
		legend.add(new Label("Entrance door"), 1, 1);
		
		legend.add(new ImageView(new Image(c.getString("map.tiles.door"), 40, 40, false, false)), 0, 2);
		legend.add(new Label("Door"), 1, 2);
		
		legend.add(new ImageView(new Image(c.getString("map.mesopatterns.ambush"), 40, 40, false, false)), 0, 3);
		legend.add(new Label("Ambush"), 1, 3);
		
		legend.add(new ImageView(new Image(c.getString("map.mesopatterns.guard_room"), 40, 40, false, false)), 0, 4);
		legend.add(new Label("Guard chamber"), 1, 4);
		
		legend.add(new ImageView(new Image(c.getString("map.mesopatterns.guarded_treasure"), 40, 40, false, false)), 0, 5);
		legend.add(new Label("Guarded treasure"), 1, 5);
		
		legend.add(new ImageView(new Image(c.getString("map.mesopatterns.treasure_room"), 40, 40, false, false)), 0, 6);
		legend.add(new Label("Treasure chamber"), 1, 6);
		
		legend.add(new ImageView(new Image(c.getString("map.examples.chamber"), 40, 40, true, true)), 0, 7);
		legend.add(new Label("Chamber"), 1, 7);
		
		legend.add(new ImageView(new Image(c.getString("map.examples.corridor"), 40, 40, true, true)), 0, 8);
		legend.add(new Label("Corridor"), 1, 8);
		
		legend.add(new ImageView(new Image(c.getString("map.examples.connector"), 40, 40, true, true)), 0, 9);
		legend.add(new Label("Connector"), 1, 9);
		
		legend.add(new ImageView(new Image(c.getString("map.examples.dead_end"), 40, 40, true, true)), 0, 10);
		legend.add(new Label("Dead end"), 1, 10);
	}
	
	/**
	 * Resets the mini maps for a new run of map generation.
	 */
	private void resetMiniMaps() {
		nextMap = 0;
		
		getMap(0).draw(null);
		getMap(0).setText("Waiting for map...");

		getMap(1).draw(null);
		getMap(1).setText("Waiting for map...");

		getMap(2).draw(null);
		getMap(2).setText("Waiting for map...");

		getMap(3).draw(null);
		getMap(3).setText("Waiting for map...");
	}

	@Override
	public void ping(PCGEvent e) {
		if (e instanceof MapUpdate) {
			if (isActive) {
				Room room = (Room) ((MapUpdate) e).getPayload();
				UUID uuid = ((MapUpdate) e).getID();
				LabeledCanvas canvas;
				synchronized (mapDisplays) {
					canvas = mapDisplays.get(nextMap);
//					canvas.setText("Got map:\n" + uuid);
					canvas.setText("");
					rooms.put(nextMap, room);
					nextMap++;
				}
				
				Platform.runLater(() -> {
					int[][] matrix = room.toMatrix();
					canvas.draw(renderer.renderMap(matrix));
				});
			}
		}
	}
	
	/**
	 * Gets the interactive map.
	 * 
	 * @return An instance of InteractiveMap, if it exists.
	 */
	public InteractiveMap getMap() {
		return mapView;
	}
	
	/**
	 * Gets one of the maps (i.e. a labeled view displaying a map) being under
	 * this object's control.
	 * 
	 * @param index An index of a map.
	 * @return A map if it exists, otherwise null.
	 */
	public LabeledCanvas getMap(int index) {
		return mapDisplays.get(index);
	}
	
	/**
	 * Marks this control as being in an active or inactive state.
	 * 
	 * @param state The new state.
	 */
	public void setActive(boolean state) {
		isActive = state;
	}
	
	/**
	 * Updates this control's map.
	 * 
	 * @param room The new map.
	 */
	public void updateMap(Room room) {
		mapView.updateMap(room);
		redrawPatterns(room);
		redrawLocks(room);
		mapIsFeasible(room.isIntraFeasible());
		resetMiniMaps();
	}
	
	/**
	 * Gets the current map being controlled by this controller.
	 * 
	 * @return The current map.
	 */
	public Room getCurrentMap() {
		return mapView.getMap();
	}
	
	/**
	 * Renders the map, making it possible to export it.
	 * 
	 * @return A rendered version of the map.
	 */
	public Image getRenderedMap() {
		return renderer.renderMap(mapView.getMap().toMatrix());
	}
	
	/**
	 * Selects a brush.
	 * 
	 * "Why is this public?",  you ask. Because of FXML's method binding.
	 * I'm sorry, this is a disgusting way of handling things...
	 */
	public void selectBrush() {
		if (brushes.getSelectedToggle() == null) {
			brush = null;
			mapView.setCursor(Cursor.DEFAULT);
			
		} else {
			mapView.setCursor(Cursor.HAND);
			
			switch (((ToggleButton) brushes.getSelectedToggle()).getText()) {
			case "Floor":
				brush = TileTypes.FLOOR;
				break;
			case "Wall":
				brush = TileTypes.WALL;
				break;
			case "Treasure":
				brush = TileTypes.TREASURE;
				break;
			case "Enemy":
				brush = TileTypes.ENEMY;
				break;
			}
		}
		
		myBrush.SetMainComponent(brush);
		
	}
	
	/**
	 * Toggles the main use of the lock modifier in the brush
	 */
	public void selectLockModifier()
	{
		myBrush.ChangeModifierMainValue("Lock", lockBrush.isSelected());
	}
	
	/**
	 * Toggles the display of patterns on top of the map.
	 * 
	 * "Why is this public?",  you ask. Because of FXML's method binding.
	 */
	public void togglePatterns() {
		if (patternButton.isSelected()) {
			patternCanvas.setVisible(true);
		} else {
			patternCanvas.setVisible(false);
		}
	}
	
	/**
	 * Toggles the display of zones on top of the map.
	 * 
	 */
	public void toggleZones() {
		if (zoneButton.isSelected()) {
			zoneCanvas.setVisible(true);
		} else {
			zoneCanvas.setVisible(false);
		}
	}
	
	/**
	 * Toggles the display of zones on top of the map.
	 * 
	 */
	public void toggleLocks() {
		if (lockButton.isSelected()) {
			lockCanvas.setVisible(true);
		} else {
			lockCanvas.setVisible(false);
		}
	}
	
	/**
	 * Generates four new mini maps.
	 * 
	 * "Why is this public?",  you ask. Because of FXML's method binding.
	 */
	public void generateNewMaps() {
		resetMiniMaps();
		generateNewMaps(mapView.getMap());
	}
	
	/**
	 * Marks the map as being infeasible.
	 * 
	 * @param state
	 */
	public void mapIsFeasible(boolean state) {
		isFeasible = state;
		
		warningCanvas.setVisible(!isFeasible);
	}
	
	/**
	 * Generates four new mini maps.
	 * 
	 * "Why is this public?",  you ask. Because of FXML's method binding.
	 */
	public void generateNewMaps(Room room) {
		// TODO: If we want more diversity in the generated maps, then send more StartMapMutate events.
		//router.postEvent(new StartMapMutate(map, MapMutationType.Preserving, AlgorithmTypes.Similarity, 2, true)); //TODO: Move some of this hard coding to ApplicationConfig
		router.postEvent(new StartMapMutate(room, MapMutationType.Preserving, AlgorithmTypes.Symmetry, 4, true)); //TODO: Move some of this hard coding to ApplicationConfig
		//router.postEvent(new StartMapMutate(map, MapMutationType.Preserving, AlgorithmTypes.SymmetryAndSimilarity, 2, true)); //TODO: Move some of this hard coding to ApplicationConfig
		//router.postEvent(new StartMapMutate(map, MapMutationType.ComputedConfig, AlgorithmTypes.Native, 2, true)); //TODO: Move some of this hard coding to ApplicationConfig
	}
	
	/**
	 * Replaces the map with one of the generated ones.
	 * 
	 * @param index The new map's index.
	 */
	private void replaceMap(int index) {
		Room room = rooms.get(index);
		if (room != null) {
			generateNewMaps(room);
			updateMap(room);
		}
	}
	
	/**
	 * Composes a list of micro patterns with their respective colours for the
	 * map renderer to use.
	 * 
	 * @param patterns The patterns to analyse.
	 * @return A map that maps each pattern instance to a colour.
	 */
	private HashMap<Pattern, Color> colourPatterns(List<Pattern> patterns) {
		HashMap<Pattern, Color> patternMap = new HashMap<Pattern, Color>();
		
		patterns.forEach((pattern) -> {
			if (pattern instanceof Chamber) {
				patternMap.put(pattern, Color.BLUE);
			} else if (pattern instanceof Corridor) {
				patternMap.put(pattern, Color.RED);
			} else if (pattern instanceof Connector) {
				patternMap.put(pattern, Color.YELLOW);
			}
		});
		
		return patternMap;
	}

	/**
	 * Redraws the pattern, based on the current map layout.
	 * 
	 * @param container
	 */
	private synchronized void redrawPatterns(Room room) {
		//Change those 2 width and height hardcoded values (420,420)
		patternCanvas.getGraphicsContext2D().clearRect(0, 0, 420, 420);
		zoneCanvas.getGraphicsContext2D().clearRect(0, 0, 420, 420);
		
		renderer.drawPatterns(patternCanvas.getGraphicsContext2D(), room.toMatrix(), colourPatterns(room.getPatternFinder().findMicroPatterns()));
		renderer.drawGraph(patternCanvas.getGraphicsContext2D(), room.toMatrix(), room.getPatternFinder().getPatternGraph());
		renderer.drawMesoPatterns(patternCanvas.getGraphicsContext2D(), room.toMatrix(), room.getPatternFinder().getMesoPatterns());
		renderer.drawZones(zoneCanvas.getGraphicsContext2D(), room.toMatrix(), room.root, (int)(zoneSlider.getValue()),Color.BLACK);
	}
	
	/***
	 * Redraw the lock in the map --> TODO: this should be in the renderer
	 * @param room
	 */
	private void redrawLocks(Room room)
	{
		lockCanvas.getGraphicsContext2D().clearRect(0, 0, 420, 420);
		
		for(int i = 0; i < room.getRowCount(); ++i)
		{
			for(int j = 0; j < room.getColCount(); ++j)
			{
				if(room.getTile(j, i).GetImmutable())
				{
					lockCanvas.getGraphicsContext2D().drawImage(renderer.GetLock(mapView.scale * 3.0f, mapView.scale * 3.0f), j * mapView.scale, i * mapView.scale);
				}
			}
		}
	}
	
	private void redrawHeatMap(Room room)
	{
		int width = room.getColCount();
		int height = room.getRowCount();
		double pWidth = heatMapCanvas.getGraphicsContext2D().getCanvas().getWidth() / (double)Math.max(width, height);
		
		Color danger = Color.RED;
		Color treasure = Color.BLUE;
		Color unpassable = Color.CYAN;
		Color nothing = Color.GREEN;

		heatMapCanvas.getGraphicsContext2D().clearRect(0, 0, 420, 420);
		
		for(int i = 0; i < room.getRowCount(); ++i)
		{
			for(int j = 0; j < room.getColCount(); ++j)
			{
				float analyzed_cells = 0.0f;
				Point p = new Point(j,i);
				List<Point> neighbors = Arrays.asList(	p,
														new Point(p.getX(), p.getY() + 1),
														new Point(p.getX() + 1, p.getY()),
														new Point(p.getX(), p.getY() - 1),
														new Point(p.getX() - 1, p.getY()));	
//				Point p0 = new Point(j, i + 1);
//				Point p1 = new Point(j + 1, i);
//				Point p2 = new Point(j, i- 1);
//				Point p3 = new Point(j - 1, i);
//				
				Color final_color = Color.BLACK;
				float danger_percent = 0.0f;
				float resource_percent = 0.0f;
				float nothing_percent = 0.0f;
				float unpassable_percent = 0.0f;
				
//				for(Point neighbor: neighbors)
//				{
//					if(neighbor.getX() > -1 && neighbor.getX() < width && neighbor.getY() > -1 && neighbor.getY() < height)
//					{
//						analyzed_cells++;
//						TileTypes til = map.getTile(neighbor.getX(), neighbor.getY()).GetType();
//						
//						if(til == TileTypes.ENEMY) danger_percent += 1;
//						else if(til == TileTypes.TREASURE) resource_percent += 1;
//						else if(til == TileTypes.WALL) unpassable_percent += 1;
//						else nothing_percent += 1;
//					}
//				}
				
				analyzed_cells++;
				TileTypes til = room.getTile(p.getX(), p.getY()).GetType();
				
				if(til == TileTypes.ENEMY) danger_percent += 1;
				else if(til == TileTypes.TREASURE) resource_percent += 1;
				else if(til == TileTypes.WALL) unpassable_percent += 1;
				else nothing_percent += 1;
				
				danger_percent = danger_percent/analyzed_cells;
				resource_percent = resource_percent/analyzed_cells;
				nothing_percent = nothing_percent/analyzed_cells;
				unpassable_percent = unpassable_percent/analyzed_cells;
				
				final_color = final_color.interpolate(danger, danger_percent);
				final_color = final_color.interpolate(treasure, resource_percent);
				final_color = final_color.interpolate(nothing, nothing_percent);
				final_color = final_color.interpolate(unpassable, unpassable_percent);
				
				//final_color = helperPlus4(helperMultiplier(unpassable, unpassable_percent), helperMultiplier(danger, danger_percent), helperMultiplier(treasure, resource_percent), helperMultiplier(nothing, nothing_percent));
				
				heatMapCanvas.getGraphicsContext2D().setFill(final_color);
				heatMapCanvas.getGraphicsContext2D().fillRect(p.getX() * pWidth, p.getY() * pWidth, pWidth, pWidth);
//				if(map.getTile(j, i).GetImmutable())
//				{
//					heatMapCanvas.getGraphicsContext2D().drawImage(renderer.GetLock(mapView.scale * 0.75f, mapView.scale * 0.75f), j * mapView.scale, i * mapView.scale);
//				}
			}
		}
	}
	
	private Color helperMultiplier(Color c, float multiplier)
	{
		return new Color(c.getRed() * multiplier, c.getGreen() * multiplier, c.getBlue() * multiplier, 1.0f);
	}
	
	private Color helperPlus4(Color c, Color c1, Color c2, Color c3)
	{
		return new Color(c.getRed() + c1.getRed() + c2.getRed() + c3.getRed(), 
						c.getGreen() + c1.getGreen() + c2.getGreen() +  c3.getGreen(),
						c.getBlue() + c1.getBlue() + c2.getBlue() + c3.getBlue(), 1.0f)	;
	}
	
	private Color helperPlus(Color c, Color c1, Color c2)
	{
		return new Color(c.getRed() + c1.getRed() + c2.getRed(), 
						c.getGreen() + c1.getGreen() + c2.getGreen(),
						c.getBlue() + c1.getBlue() + c2.getBlue(), 1.0f)	;
	}
	
	/*
	 * Event handlers
	 */
	private class EditViewEventHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) 
		{
			
			if (event.getTarget() instanceof ImageView) {
				// Edit the map
				ImageView tile = (ImageView) event.getTarget();
				
//				//TODO: Super hack
//				if(!mapView.getMap().EveryRoomVisitable() && myBrush.GetModifierValue("Lock"))
//					return;
				
				//TODO: This should go to its own class or function at least
//				if(event.isControlDown())
//					lockBrush.setSelected(true);
//				else if()
				myBrush.UpdateModifiers(event);
//				mapView.updateTile(tile, brush, event.getButton() == MouseButton.SECONDARY, lockBrush.isSelected() || event.isControlDown());
				mapView.updateTile(tile, myBrush);
				mapView.getMap().forceReevaluation();
				mapIsFeasible(mapView.getMap().isIntraFeasible());
				redrawPatterns(mapView.getMap());
				redrawLocks(mapView.getMap());
//				redrawHeatMap(mapView.getMap());
			}
		}
		
	}
	
	/*
	 * Event handlers
	 */
	private class EditViewMouseHover implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) 
		{
			brushCanvas.setVisible(false);
			
			if (event.getTarget() instanceof ImageView) 
			{
				// Show the brush canvas
				ImageView tile = (ImageView) event.getTarget();
				myBrush.SetBrushSize((int)(zoneSlider.getValue()));
				brushCanvas.getGraphicsContext2D().clearRect(0, 0, 420, 420);
				brushCanvas.setVisible(true);
				util.Point p = mapView.CheckTile(tile);
				myBrush.Update(event, p, mapView.getMap());
				
				renderer.drawBrush(brushCanvas.getGraphicsContext2D(), mapView.getMap().toMatrix(), myBrush, Color.WHITE);
			}
		}
		
	}
	
}
