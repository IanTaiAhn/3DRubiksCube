package application;

import java.util.Arrays;
import java.util.List;

import edu.princeton.cs.algs4.Queue;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Generates a 3DRubik's Cube using triangle meshes. This cube can only rotate
 * clockwise, and only 2 sides at a time. For example blue / green, orange /
 * red, and white/ yellow. This is not intentional, a fully functioning cube was
 * preferred, but due to how the cube was generated, and how JavaFX's children
 * of nodes in a group act when placed in multiple groups, this implementation
 * wouldn't allow a fully functional Rubik's Cube.
 * 
 * CAVEAT: This Cube can only rotate clockwise, and isn't fully functional.
 * 
 * Big thanks to Jose Pereda whose stackoverflow posts on triangle meshes,
 * texturing, and overall knowledge in JavaFX proved to be most useful. It was
 * because of him that we realized that boxes(3D Cube Object in JavaFX) were
 * unable to be textured on all 6 sides which was needed for the creation of
 * this Rubik's Cube. Triangle meshes, and mesh objects in general allow us to
 * wrap it in a net which gives us the ability to color each face of the cube.
 * 
 * 
 * @author Ian Tai Ahn, Nicholas Gores
 */
public class RubiksCube extends Application {
	public static Queue<Integer> queue;
	public static Queue<String> queueColor;

	private Group blueSide;
	private Group orangeSide;
	private Group greenSide;
	private Group redSide;
	private Group yellowSide;
	private Group whiteSide;

	private Group hzMid;
	private Group vtMid;

	private Scene scene;
	private Group sceneRoot;

	private Group botThird;
	private MeshView[] botThirdArr;

	private Group midThird;
	private MeshView[] midThirdArr;

	private Group topThird;
	private MeshView[] topThirdArr;

	public static final int RED = 0;
	public static final int GREEN = 1;
	public static final int BLUE = 2;
	public static final int YELLOW = 3;
	public static final int ORANGE = 4;
	public static final int WHITE = 5;
	public static final int GRAY = 6;

	public static final float X_RED = 0.5f / 7f;
	public static final float X_GREEN = 1.5f / 7f;
	public static final float X_BLUE = 2.5f / 7f;
	public static final float X_YELLOW = 3.5f / 7f;
	public static final float X_ORANGE = 4.5f / 7f;
	public static final float X_WHITE = 5.5f / 7f;
	public static final float X_GRAY = 6.5f / 7f;

	private double mousePosX;
	private double mousePosY;
	private double mouseOldX;
	private double mouseOldY;

	private void createQueue() {
		queue = new Queue<>();
		queueColor = new Queue<>();
	}

	/**
	 * All JavaFX applications require this start method to build the GUI. The
	 * cubies(triangle meshes) are created and added into the scene here, and all of
	 * the event handling is done in this method.
	 */
	@Override
	public void start(Stage primaryStage) {
		sceneRoot = new Group();
		scene = new Scene(sceneRoot, 600, 600, true, SceneAntialiasing.BALANCED);
		scene.setFill(Color.BLACK);
		PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.setNearClip(0.1);
		camera.setFarClip(10000.0);
		camera.setTranslateZ(-12);
		scene.setCamera(camera);
		Image icon = new Image("application/rubiks.png");
		primaryStage.getIcons().add(icon);

		PhongMaterial mat = new PhongMaterial();
		mat.setDiffuseMap(new Image(getClass().getResourceAsStream("palette.png")));

		int j = 0;

		j = 0;
		botThird = new Group();
		botThirdArr = new MeshView[9];
		for (int i = 0; i < 3; i++) { // 0 - 2
			setCubies(mat, botThird, i, j, botThirdArr);
			j++;
		}
		j = 3;
		for (int i = 9; i < 12; i++) { // 3 - 5
			setCubies(mat, botThird, i, j, botThirdArr);
			j++;
		}
		j = 6;
		for (int i = 18; i < 21; i++) { // 6 - 9
			setCubies(mat, botThird, i, j, botThirdArr);
			j++;
		}

		j = 0;
		midThird = new Group();
		midThirdArr = new MeshView[9];
		for (int i = 3; i < 6; i++) { // 0 - 2
			setCubies(mat, midThird, i, j, midThirdArr);
			j++;
		}
		j = 3;
		for (int i = 12; i < 15; i++) { // 3 - 5
			setCubies(mat, midThird, i, j, midThirdArr);
			j++;
		}
		j = 6;
		for (int i = 21; i < 24; i++) { // 6 - 9
			setCubies(mat, midThird, i, j, midThirdArr);
			j++;
		}

		j = 0;
		topThird = new Group();
		topThirdArr = new MeshView[9];
		for (int i = 6; i < 9; i++) { // 0 - 2
			setCubies(mat, topThird, i, j, topThirdArr);
			j++;
		}
		j = 3;
		for (int i = 15; i < 18; i++) { // 3 - 5
			setCubies(mat, topThird, i, j, topThirdArr);
			j++;
		}
		j = 6;
		for (int i = 24; i < 27; i++) { // 6 - 9
			setCubies(mat, topThird, i, j, topThirdArr);
			j++;
		}

		j = 0;

		blueSide = new Group();
		orangeSide = new Group();
		greenSide = new Group();
		redSide = new Group();
		yellowSide = new Group();
		whiteSide = new Group();

		hzMid = new Group();
		vtMid = new Group();

		// Add meshGroups here to rotate cube with mouse
		Rotate rotateX = new Rotate(30, 0, 0, 0, Rotate.X_AXIS);
		Rotate rotateY = new Rotate(20, 0, 0, 0, Rotate.Y_AXIS);
		botThird.getTransforms().addAll(rotateX, rotateY);
		midThird.getTransforms().addAll(rotateX, rotateY);
		topThird.getTransforms().addAll(rotateX, rotateY);

		blueSide.getTransforms().addAll(rotateX, rotateY);
		orangeSide.getTransforms().addAll(rotateX, rotateY);
		greenSide.getTransforms().addAll(rotateX, rotateY);
		redSide.getTransforms().addAll(rotateX, rotateY);
		yellowSide.getTransforms().addAll(rotateX, rotateY);
		whiteSide.getTransforms().addAll(rotateX, rotateY);

		hzMid.getTransforms().addAll(rotateX, rotateY);
		vtMid.getTransforms().addAll(rotateX, rotateY);

		// Start EventHandling here
		rotations(sceneRoot, scene, botThird, botThirdArr, midThird, midThirdArr, topThird, topThirdArr, blueSide,
				orangeSide, greenSide, redSide, yellowSide, whiteSide, hzMid, vtMid);

		sceneRoot.getChildren().addAll(botThird, midThird, topThird, new AmbientLight(Color.WHITE));

		scene.setOnMousePressed(me -> {
			mouseOldX = me.getSceneX();
			mouseOldY = me.getSceneY();
		});
		scene.setOnMouseDragged(me -> {
			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
			rotateX.setAngle(rotateX.getAngle() - (mousePosY - mouseOldY));
			rotateY.setAngle(rotateY.getAngle() + (mousePosX - mouseOldX));
			mouseOldX = mousePosX;
			mouseOldY = mousePosY;
		});

		createQueue();
		primaryStage.setTitle("3D Rubik's Cube Model");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Private helper method that handles the rotations of the cube.
	 * 
	 * @param sceneRoot
	 * @param scene
	 * @param botThird
	 * @param botThirdArr
	 * @param midThird
	 * @param midThirdArr
	 * @param topThird
	 * @param topThirdArr
	 * @param blueSide
	 * @param orangeSide
	 * @param greenSide
	 * @param redSide
	 * @param yellowSide
	 * @param whiteSide
	 * @param hzMid
	 * @param vtMid
	 */
	private void rotations(Group sceneRoot, Scene scene, Group botThird, MeshView[] botThirdArr, Group midThird,
			MeshView[] midThirdArr, Group topThird, MeshView[] topThirdArr, Group blueSide, Group orangeSide,
			Group greenSide, Group redSide, Group yellowSide, Group whiteSide, Group hzMid, Group vtMid) {
		scene.setOnKeyPressed(e -> {
//			RotateTransition rot = new RotateTransition(Duration.seconds(3), blueSide);

//			rot.setAxis(Rotate.Z_AXIS);
//			rot.setFromAngle(blueSide.getRotate());
//			rot.setByAngle(90); // Determines how many degrees the node will rotate according to its specified
//
//			rot.setToAngle(blueSide.getRotate());
			// rot.se
			// System.out.println(blueSide.);
			// axis.
//			rot.setCycleCount(1); // sets the number of cycles it goes through
//			rot.setRate(10); // sets the rate at which it rotates
//			rot.setToAngle(90);
//	    			rot.setFromAngle(500); // determines what angle the object starts in
			// rot.setDuration(Duration.seconds(5));
//			rot.play();

//			System.out.println(blueSide.getRotate());
//			System.out.println(blueSide.getRotationAxis());
//			System.out.println(blueSide.rotateProperty().doubleValue());
//			System.out.println(blueSide.rotationAxisProperty());
//			System.out.println(blueSide.ro);

			if (e.getCode() == KeyCode.D) {
				System.out.println("Deletion has occured");
				sceneRoot.getChildren().clear();
				topThird.getChildren().clear();
				midThird.getChildren().clear();
				botThird.getChildren().clear();

				greenSide.getChildren().clear();
				blueSide.getChildren().clear();
				orangeSide.getChildren().clear();
				redSide.getChildren().clear();
				yellowSide.getChildren().clear();
				whiteSide.getChildren().clear();
				hzMid.getChildren().clear();
				vtMid.getChildren().clear();
			}

			// Blue / Green Construct
			if (e.getCode() == KeyCode.Q) { // back green side
				System.out.println("Green / Blue Create");
				greenSide.getChildren().addAll(botThirdArr[6], midThirdArr[6], topThirdArr[6], botThirdArr[7],
						midThirdArr[7], topThirdArr[7], botThirdArr[8], midThirdArr[8], topThirdArr[8]);
				blueSide.getChildren().addAll(botThirdArr[0], midThirdArr[0], topThirdArr[0], botThirdArr[1],
						midThirdArr[1], topThirdArr[1], botThirdArr[2], midThirdArr[2], topThirdArr[2]);
				hzMid.getChildren().addAll(botThirdArr[3], midThirdArr[3], topThirdArr[3], botThirdArr[4],
						midThirdArr[4], topThirdArr[4], botThirdArr[5], midThirdArr[5], topThirdArr[5]);
				sceneRoot.getChildren().addAll(greenSide, blueSide, hzMid, new AmbientLight(Color.WHITE));
			}

			if (e.getCode() == KeyCode.B) {
				System.out.println("Blue Side Clockwise Rotation");
				blueSide.getTransforms().add(new Rotate(90, Rotate.Z_AXIS));
				// Blue side clockwise rotation == 0
				queue.enqueue(0);
				queueColor.enqueue("blu");
			}
			if (e.getCode() == KeyCode.G) {
				System.out.println("Green Side Clockwise Rotation");
				greenSide.getTransforms().add(new Rotate(-90, Rotate.Z_AXIS));
				// Green side clockwise rotation == 2
				queue.enqueue(2);
				queueColor.enqueue("gre");
			}

			// Orange / Red Construct
			if (e.getCode() == KeyCode.A) { // orange red side
				System.out.println("Orange / Red Create");
				orangeSide.getChildren().addAll(botThirdArr[0], midThirdArr[0], topThirdArr[0], botThirdArr[3],
						midThirdArr[3], topThirdArr[3], botThirdArr[6], midThirdArr[6], topThirdArr[6]);
				redSide.getChildren().addAll(botThirdArr[2], midThirdArr[2], topThirdArr[2], botThirdArr[5],
						midThirdArr[5], topThirdArr[5], botThirdArr[8], midThirdArr[8], topThirdArr[8]);
				vtMid.getChildren().addAll(botThirdArr[1], midThirdArr[1], topThirdArr[1], botThirdArr[4],
						midThirdArr[4], topThirdArr[4], botThirdArr[7], midThirdArr[7], topThirdArr[7]);
				sceneRoot.getChildren().addAll(orangeSide, redSide, vtMid, new AmbientLight(Color.WHITE));
			}

			if (e.getCode() == KeyCode.O) { // left
				System.out.println("Orange Side Clockwise Rotation");

				orangeSide.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
				// Orange side clockwise rotation == 1
				queue.enqueue(1);
				queueColor.enqueue("ora");
			}
			if (e.getCode() == KeyCode.R) { // right
				System.out.println("Red Side Clockwise Rotation");
				redSide.getTransforms().add(new Rotate(-90, Rotate.X_AXIS));
				// Red side clockwise rotation == 3
				queue.enqueue(3);
				queueColor.enqueue("red");
			}

			// yellow / white construct
			if (e.getCode() == KeyCode.Z) {
				System.out.println("Yellow / White Create");
				for (int i = 0; i < botThirdArr.length; i++) {
					yellowSide.getChildren().addAll(topThirdArr[i]);
				}
				for (int i = 0; i < midThirdArr.length; i++) {
					midThird.getChildren().addAll(midThirdArr[i]);
				}
				for (int i = 0; i < botThirdArr.length; i++) {
					whiteSide.getChildren().addAll(botThirdArr[i]);
				}
				sceneRoot.getChildren().addAll(yellowSide, whiteSide, midThird, new AmbientLight(Color.WHITE));
			}

			if (e.getCode() == KeyCode.Y) {
				System.out.println("Yellow Clockwise Rotation");
				yellowSide.getTransforms().add(new Rotate(90, Rotate.Y_AXIS));
				// Yellow side rotation == 4
				queue.enqueue(4);
				queueColor.enqueue("yel");
			}
			if (e.getCode() == KeyCode.W) {
				System.out.println("White Clockwise Rotation");
				whiteSide.getTransforms().add(new Rotate(-90, Rotate.Y_AXIS));
				// White side rotation == 5
				queue.enqueue(5);
				queueColor.enqueue("whi");
			}

		});
	}

	/**
	 * Helper method that creates the triangle mesh objects, and wraps each one in
	 * the texture of the png.
	 * 
	 * @param mat
	 * @param group
	 * @param i
	 * @param j
	 * @param select
	 */
	private void setCubies(PhongMaterial mat, Group group, int i, int j, MeshView[] select) {
		MeshView meshP = new MeshView();
		meshP.setMesh(createCube(patternFaceF.get(i)));
		meshP.setMaterial(mat);
		Point3D pt = pointsFaceF.get(i);
		meshP.getTransforms().addAll(new Translate(pt.getX(), pt.getY(), pt.getZ()));
		select[j] = meshP;
		group.getChildren().add(meshP);
	}

	// Front / Blue faces
	private static final int[] FLD = new int[] { BLUE, GRAY, GRAY, GRAY, ORANGE, WHITE };
	private static final int[] FD = new int[] { BLUE, GRAY, GRAY, GRAY, GRAY, WHITE };
	private static final int[] FRD = new int[] { BLUE, RED, GRAY, GRAY, GRAY, WHITE };
	private static final int[] FL = new int[] { BLUE, GRAY, GRAY, GRAY, ORANGE, GRAY };
	private static final int[] F = new int[] { BLUE, GRAY, GRAY, GRAY, GRAY, GRAY };
	private static final int[] FR = new int[] { BLUE, RED, GRAY, GRAY, GRAY, GRAY };
	private static final int[] FLU = new int[] { BLUE, GRAY, YELLOW, GRAY, ORANGE, GRAY };
	private static final int[] FU = new int[] { BLUE, GRAY, YELLOW, GRAY, GRAY, GRAY };
	private static final int[] FRU = new int[] { BLUE, RED, YELLOW, GRAY, GRAY, GRAY };

	// Front / Blue 3Dpoints
	private static final Point3D pFLD = new Point3D(-1.1, 1.1, -1.1);
	private static final Point3D pFD = new Point3D(0, 1.1, -1.1);
	private static final Point3D pFRD = new Point3D(1.1, 1.1, -1.1);
	private static final Point3D pFL = new Point3D(-1.1, 0, -1.1);
	private static final Point3D pF = new Point3D(0, 0, -1.1);
	private static final Point3D pFR = new Point3D(1.1, 0, -1.1);
	private static final Point3D pFLU = new Point3D(-1.1, -1.1, -1.1);
	private static final Point3D pFU = new Point3D(0, -1.1, -1.1);
	private static final Point3D pFRU = new Point3D(1.1, -1.1, -1.1);

	// Center / inside faces
	private static final int[] CLD = new int[] { GRAY, GRAY, GRAY, GRAY, ORANGE, WHITE };
	private static final int[] CD = new int[] { GRAY, GRAY, GRAY, GRAY, GRAY, WHITE };
	private static final int[] CRD = new int[] { GRAY, RED, GRAY, GRAY, GRAY, WHITE };
	private static final int[] CL = new int[] { GRAY, GRAY, GRAY, GRAY, ORANGE, GRAY };
	private static final int[] C = new int[] { GRAY, GRAY, GRAY, GRAY, GRAY, GRAY };
	private static final int[] CR = new int[] { GRAY, RED, GRAY, GRAY, GRAY, GRAY };
	private static final int[] CLU = new int[] { GRAY, GRAY, YELLOW, GRAY, ORANGE, GRAY };
	private static final int[] CU = new int[] { GRAY, GRAY, YELLOW, GRAY, GRAY, GRAY };
	private static final int[] CRU = new int[] { GRAY, RED, YELLOW, GRAY, GRAY, GRAY };

	// Center / inside 3Dpoints
	private static final Point3D pCLD = new Point3D(-1.1, 1.1, 0);
	private static final Point3D pCD = new Point3D(0, 1.1, 0);
	private static final Point3D pCRD = new Point3D(1.1, 1.1, 0);
	private static final Point3D pCL = new Point3D(-1.1, 0, 0);
	private static final Point3D pC = new Point3D(0, 0, 0);
	private static final Point3D pCR = new Point3D(1.1, 0, 0);
	private static final Point3D pCLU = new Point3D(-1.1, -1.1, 0);
	private static final Point3D pCU = new Point3D(0, -1.1, 0);
	private static final Point3D pCRU = new Point3D(1.1, -1.1, 0);

	// Back / Green faces
	private static final int[] BLD = new int[] { GRAY, GRAY, GRAY, GREEN, ORANGE, WHITE };
	private static final int[] BD = new int[] { GRAY, GRAY, GRAY, GREEN, GRAY, WHITE };
	private static final int[] BRD = new int[] { GRAY, RED, GRAY, GREEN, GRAY, WHITE };
	private static final int[] BL = new int[] { GRAY, GRAY, GRAY, GREEN, ORANGE, GRAY };
	private static final int[] B = new int[] { GRAY, GRAY, GRAY, GREEN, GRAY, GRAY };
	private static final int[] BR = new int[] { GRAY, RED, GRAY, GREEN, GRAY, GRAY };
	private static final int[] BLU = new int[] { GRAY, GRAY, YELLOW, GREEN, ORANGE, GRAY };
	private static final int[] BU = new int[] { GRAY, GRAY, YELLOW, GREEN, GRAY, GRAY };
	private static final int[] BRU = new int[] { GRAY, RED, YELLOW, GREEN, GRAY, GRAY };

	// Back / Green 3Dpoints
	private static final Point3D pBLD = new Point3D(-1.1, 1.1, 1.1);
	private static final Point3D pBD = new Point3D(0, 1.1, 1.1);
	private static final Point3D pBRD = new Point3D(1.1, 1.1, 1.1);
	private static final Point3D pBL = new Point3D(-1.1, 0, 1.1);
	private static final Point3D pB = new Point3D(0, 0, 1.1);
	private static final Point3D pBR = new Point3D(1.1, 0, 1.1);
	private static final Point3D pBLU = new Point3D(-1.1, -1.1, 1.1);
	private static final Point3D pBU = new Point3D(0, -1.1, 1.1);
	private static final Point3D pBRU = new Point3D(1.1, -1.1, 1.1);

	private static final List<int[]> patternFaceF = Arrays.asList(FLD, FD, FRD, FL, F, FR, FLU, FU, FRU, CLD, CD, CRD,
			CL, C, CR, CLU, CU, CRU, BLD, BD, BRD, BL, B, BR, BLU, BU, BRU);

	private static final List<Point3D> pointsFaceF = Arrays.asList(pFLD, pFD, pFRD, pFL, pF, pFR, pFLU, pFU, pFRU, pCLD,
			pCD, pCRD, pCL, pC, pCR, pCLU, pCU, pCRU, pBLD, pBD, pBRD, pBL, pB, pBR, pBLU, pBU, pBRU);

	private TriangleMesh createCube(int[] face) {
		TriangleMesh m = new TriangleMesh();

		// POINTS
		m.getPoints().addAll(0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f,
				0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f);

		// TEXTURES
		m.getTexCoords().addAll(X_RED, 0.5f, X_GREEN, 0.5f, X_BLUE, 0.5f, X_YELLOW, 0.5f, X_ORANGE, 0.5f, X_WHITE, 0.5f,
				X_GRAY, 0.5f);

		// FACES
		m.getFaces().addAll(2, face[0], 3, face[0], 6, face[0], // F
				3, face[0], 7, face[0], 6, face[0],

				0, face[1], 1, face[1], 2, face[1], // R
				2, face[1], 1, face[1], 3, face[1],

				1, face[2], 5, face[2], 3, face[2], // U
				5, face[2], 7, face[2], 3, face[2],

				0, face[3], 4, face[3], 1, face[3], // B
				4, face[3], 5, face[3], 1, face[3],

				4, face[4], 6, face[4], 5, face[4], // L
				6, face[4], 7, face[4], 5, face[4],

				0, face[5], 2, face[5], 4, face[5], // D
				2, face[5], 6, face[5], 4, face[5]);
		return m;
	}

	/**
	 * Launches the GUI
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// System.out.println(queue.size());
		launch(args);
	}

	// changes the dimensions of each individual cubie.
//	m.getPoints().addAll(0.25f, 0.25f, 0.25f, 0.25f, -0.25f, 0.25f, 0.25f, 0.25f, -0.25f, 0.25f, -0.25f, -0.25f, -0.25f, 0.25f,
//			0.25f, -0.25f, -0.25f, 0.25f, -0.25f, 0.25f, -0.25f, -0.25f, -0.25f, -0.25f);
//
//	// TEXTURES
//	m.getTexCoords().addAll(X_RED, 0.25f, X_GREEN, 0.25f, X_BLUE, 0.25f, X_YELLOW, 0.25f, X_ORANGE, 0.25f, X_WHITE, 0.25f,
//			X_GRAY, 0.25f);

}