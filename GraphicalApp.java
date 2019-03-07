import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Math;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.animation.AnimationTimer;


public class GraphicalApp extends Application {
	private static int scale = 4;
	private static Map currentMap;
    private static boolean showFpsOverlay = true;
    private static HashMap<StaticObject, Shape> objDisplay = new HashMap<StaticObject, Shape>();
	private static ArrayList<StaticObject> toUpdate = new ArrayList<StaticObject>();
	private static Car mainCar = new Car(50, 5, "Magic School Bus", Math.toRadians(90), 1, 1.5, 8.2, 0.3);


	public static void collisionStep(double time) {

	}

	public static void inputStep(double time) {

	}

	public static void tickStep(double time) {
		currentMap.tickAll(time);
	}

    private static void updateDisplayShape(Line l, Wall o) {
        l.setStartX(o.getX() * scale);
        l.setStartY(o.getY() * scale);
        l.setEndX(o.getX2() * scale);
        l.setEndY(o.getY2() * scale);
    }

    private static void updateDisplayShape (Rectangle r, StaticObject o) {
        r.setX((o.getX() - o.getHalfW()) * scale);
        r.setY((o.getY() - o.getHalfH()) * scale);
    }

	public static void displayStep() {
		if (!toUpdate.isEmpty()) {
			ArrayList<StaticObject> tempStaticObjList = currentMap.getStaticObjList();
	        ArrayList<DynamicObject> tempDynamicObjList = currentMap.getDynamicObjList();

	        for (StaticObject o: toUpdate) {
	            if (!tempStaticObjList.contains(o) && !tempDynamicObjList.contains(o)) {
	                objDisplay.remove(o);
	            }else {
	                if (o instanceof Wall) {
	                    updateDisplayShape((Line) objDisplay.get(o), (Wall) o);
	                }else {
	                    updateDisplayShape((Rectangle) objDisplay.get(o), o);
	                }
	            }
	        }
		}
	}

    private static HashMap<StaticObject, Shape> createDisplayShapes(ArrayList<? extends StaticObject> objList) {
        HashMap<StaticObject, Shape> temp = new HashMap<StaticObject, Shape>();
        for (StaticObject o: objList) {
            if (o instanceof Wall) {
                Line newShape = new Line();
				updateDisplayShape(newShape, (Wall) o);
                temp.put(o, newShape);
            } else {
                Rectangle newShape = new Rectangle();
				updateDisplayShape(newShape, o);
                newShape.setWidth(o.getHalfW() * 2 * scale);
                newShape.setHeight(o.getHalfH() * 2 * scale);
                temp.put(o, newShape);
            }
        }

        return(temp);
    }

	public void start (Stage primaryStage) throws Exception {
		Group root = new Group();

        Pane gameWindow = new Pane();

        objDisplay.putAll(createDisplayShapes(currentMap.getStaticObjList()));
        objDisplay.putAll(createDisplayShapes(currentMap.getDynamicObjList()));

        gameWindow.getChildren().addAll(objDisplay.values());

		System.out.println(mainCar.getHalfW());

        Group fpsOverlay = new Group();
        Label fpsLabel = new Label();
        Label gameFpsLabel = new Label();
        fpsOverlay.getChildren().add(fpsLabel);
        fpsOverlay.getChildren().add(gameFpsLabel);
        gameFpsLabel.setLayoutY(20);

		root.getChildren().add(fpsOverlay);
        root.getChildren().add(gameWindow);

        Scene scene = new Scene(root, currentMap.getWidth() * scale, currentMap.getHeight() * scale);
        primaryStage.setTitle("title");
        primaryStage.setScene(scene);
        primaryStage.show();

		System.out.println(((Rectangle)objDisplay.get(mainCar)).getWidth());

        AnimationTimer animator = new AnimationTimer(){
            private final int idealGameFps = 30; // 0 < fps <= 60
            private double gameFps;
            private double animFps;
            private long gameLast = 0;
            private long animLast = 0;
            private int count = 0;

            @Override
            public void handle(long now){
                //maintain game fps
                animFps = 1000000000d / (now - animLast);

                if (count > animFps / idealGameFps){
                    gameFps = 1000000000d / (now - gameLast);

                    //game frame
                    double time = (now - gameLast) / 1000000000d;

                    collisionStep(time);
                    inputStep(time);
                    tickStep(time);
                    displayStep();

                    //back to fps stuff
                    if (showFpsOverlay) {
                        gameFpsLabel.setText("" + (int)(gameFps));
	                    fpsLabel.setText("" + (int)(animFps));
                    }
                    gameLast = now;
                    count = 0;
                }
                animLast = now;
                count++;
            }
        };

        animator.start();
	}

	public static void main(String[] args) {

		ArrayList<Interface> interfaceList = new ArrayList<Interface>();
		interfaceList.add(new Interface(mainCar));

		currentMap = new Map(interfaceList, 150, 200);

		currentMap.addDynamicObject(mainCar);

		for (int i=0; i<5; i++) {
			currentMap.addStaticObject(new StaticObstacle(48, 10 + 10 * i, "RCone" + i, 0.15, 0.15));
			currentMap.addStaticObject(new StaticObstacle(52, 5 + 10 * i, "LCone" + i, 0.15, 0.15));
		}

		launch(args);

	}
}