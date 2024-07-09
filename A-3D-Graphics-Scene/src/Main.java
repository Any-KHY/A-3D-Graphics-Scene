/*
Start up package for 159.235 Assignment 3 (Semester 2, 2022)
 */
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    /* Draw black lines at the centre of the image. Call this to verify the camera
    pointing direction
     */
    public static void putAxes(BufferedImage image) {
        int npixx = image.getWidth();
        int npixy = image.getHeight();
        int L = (int) (Math.floor(0.0625 * npixx));
        for (int k = 0; k < L; ++k) {
            int rgb = 0;
            image.setRGB(k + (npixx - 1) / 2, (npixy - 1) / 2, rgb);
            image.setRGB((npixx - 1) / 2, k + (npixy - 1) / 2, rgb);
        }
    }

    /* Generate a test planar surface with a uniform colour
        pD : x,y,z position in scene
        pA : rotation angles around x, y, z-axes
        pS : scaling in x,y,z
     */

    // Ian's Demo
    public static ThreeDSurface makeAPlaneSurface(Material marterial, SurfaceColour surfaceColour, Point4 pD, Point4 pA, Point4 pS) {
        return new ThreeDSurface( new Square(), marterial, surfaceColour, Placement.placeModel(pD, pA, pS));
    }

    public static ArrayList<ThreeDSurface> makeACube(int numberOfFace, Material material, SurfaceColour[] surfaceColour, Point4[] placementW) {

        ArrayList<ThreeDSurface> cube = new ArrayList<>(); // ArrayList to store cube's surface

        double faceLength = 1.0;
        int maxFaceNumber = 6;
        Placement[] facePlacements = new Placement[maxFaceNumber];

        Point4[] faceDisplacements = { //pD Lecture RayTracing P.56
                Point4.createPoint(0, -faceLength/2, 0),   // Bottom face
                Point4.createPoint(0, 0, -faceLength/2),   // Back face
                Point4.createPoint(-faceLength/2, 0, 0),   // Left face
                Point4.createPoint(faceLength/2, 0, 0),    // Right face
                Point4.createPoint(0, faceLength/2, 0),   // Top face
                Point4.createPoint(0, 0, faceLength/2)     // Front face
        };

        Point4[] faceRotations = { //pA Lecture RayTracing P.56
                Point4.createPoint(Math.toRadians(-90), 0, 0), // Bottom face
                Point4.createPoint(0, Math.toRadians(180), 0), // Back face
                Point4.createPoint(0, Math.toRadians(-90), 0), // Left face
                Point4.createPoint(0, Math.toRadians(90), 0), // Right face
                Point4.createPoint(Math.toRadians(90), 0, 0), // Top face
                Point4.createPoint(0, 0, 0) // Front face
        };

        Point4 facesScaling = Point4.createPoint(faceLength, faceLength, faceLength);

        // combined faces to make a cube
        for (int i = 0; i < numberOfFace; i++) {
            facePlacements[i] = Placement.placeModel(faceDisplacements[i], faceRotations[i], facesScaling);
            ThreeDSurface face = new ThreeDSurface(new Square(), material, surfaceColour[i], facePlacements[i]);
            cube.add(face);
        }

        Placement finalPlacement = Placement.placeModel(placementW[0],placementW[1],placementW[2]);
        for (ThreeDSurface cubeFace : cube ) { // Update Placement details with final placement (cube's position, rotation, scale)
            cubeFace.placement.tWL = cubeFace.placement.tWL.times(finalPlacement.tWL);
            cubeFace.placement.tLW = finalPlacement.tLW.times(cubeFace.placement.tLW);
        }
        return cube;
    }

    public static ThreeDSurface makeASphere(Material material, SurfaceColour colour, Point4 pD, Point4 pA, Point4 pS) {
        return new ThreeDSurface(new Sphere(), material, colour, Placement.placeModel(pD, pA, pS));
    }

    public static void main(String[] args) throws IOException {

        // Position of light source in world coordinates.
        Point4 pLightW = Point4.createPoint(50, 50, 100);

        // Get a camera with field of view 15 degrees in y (default)
        double fovy = Math.toRadians(15);
        int npixx = 801;
        int npixy = 801;
        Camera camera = Camera.standardCamera(fovy, npixx, npixy);

        // Position and orientation of the camera in the world scene.
        Point4 pCam  = Point4.createPoint(20, 20, 200);
        Point4 pTarg = Point4.createPoint(2, 2, 2); // moved from default (0, 0, 0)
        Point4 vUp   = Point4.createVector(0, -1, 0); // default

        camera.rePoint(pCam, pTarg, vUp);

        // Get a scene graph that manages the list of surfaces to be rendered
        SceneGraph scene = new SceneGraph();

        // Solid wall behind the Demo Room
        Point4 pDConcreteWall = Point4.createPoint(-10, 10,-39); // position
        Point4 pAConcreteWall = Point4.createPoint(0,0,0);  // rotation
        Point4 pSConcreteWall = Point4.createPoint(85, 85, 85); // Scaling factors
        scene.add(makeAPlaneSurface(
                new Material(0.8, 0.5, 10),
                new TextureColour(new TextureMap("res/cardboardTexture.jpg")),
                pDConcreteWall, pAConcreteWall, pSConcreteWall));

        // Demo Room (A cube with 5 faces -- without Front Face)
        TextureColour[] roomColour = {
                new TextureColour(new TextureMap("res/wood.jpg")), // Floor
                new TextureColour(new TextureMap("res/concrete.JPG")), // back
                new TextureColour(new TextureMap("res/wallpaper.jpg")), // Wall L
                new TextureColour(new TextureMap("res/wallpaper.jpg")), // Wall R
                new TextureColour(new TextureMap("res/wallpaper.jpg")), // Top
        };

        Point4[] roomPlacement = { // club placement
                Point4.createPoint(-5, 7, -20), // position
                Point4.createPoint(0,0,0), // rotation
                Point4.createPoint(35,35,35) // scaling
        };
        ArrayList<ThreeDSurface> room = makeACube(5, new Material(0.6, 0.0, 10.0), roomColour, roomPlacement);
        scene.add(room);

        // Ball on the Demo Room
        Point4 pDBallOnTheRoom = Point4.createPoint(12, 25.75,-5.8); // position
        Point4 pABallOnTheRoom = Point4.createPoint(Math.toRadians(45),Math.toRadians(30),Math.toRadians(185));  // rotation
        Point4 pSBallOnTheRoom = Point4.createPoint(1.5, 1.5, 1.5); // Scaling
        scene.add(makeASphere(
                new Material(0.3, 0.5, 7),
                new UniformColour(Color.red),
                pDBallOnTheRoom, pABallOnTheRoom, pSBallOnTheRoom)
        );

        // Drawing on the wall - in Demo room
        Point4 pDDrawing = Point4.createPoint(-3, -1.5,-37.45); // position
        Point4 pADrawing = Point4.createPoint(0,0,0);  // rotation
        Point4 pSDrawing = Point4.createPoint(10, 10, 10); // Scaling factors
        scene.add(makeAPlaneSurface(
                new Material(0.8, 0.5, 10),
                new TextureColour(new TextureMap("res/A2Cow.JPG")),
                pDDrawing, pADrawing, pSDrawing));

        // Poster on the wall  - outside
        Point4 pDPoster = Point4.createPoint(20, 10,-38.5); // position
        Point4 pAPoster = Point4.createPoint(0,0,0);  // rotation
        Point4 pSPoster = Point4.createPoint(10, 10, 10); // Scaling factors
        scene.add(makeAPlaneSurface(
                new Material(0.8, 0.5, 10),
                new TextureColour(new TextureMap("res/Cardboard＿Robot.jpg")),
                pDPoster, pAPoster, pSPoster));

        // Uniform Coloured Sphere
        Point4 pDSphere = Point4.createPoint(2, -9.5,-15.8); // position
        Point4 pASphere = Point4.createPoint(0,0,0);  // rotation
        Point4 pSSphere = Point4.createPoint(1, 1, 1); // Scaling factors
        scene.add(makeASphere(
                new Material(0.4, 0.5, 10),
                new UniformColour(new Color(241, 38, 38)),
                pDSphere, pASphere, pSSphere));

        // Ball In the other's Shadow
        Point4 pDBallInShadowOfTheWallR = Point4.createPoint(9, -8.5,-20.8); // position
        Point4 pABallInShadowOfTheWallR = Point4.createPoint(Math.toRadians(45),Math.toRadians(30),Math.toRadians(185));  // rotation
        Point4 pSBallInShadowOfTheWallR = Point4.createPoint(2, 2, 2); // Scaling factors
        scene.add(makeASphere(
                new Material(0.2, 0.5, 5),
                new TextureColour(new TextureMap("res/beachball.jpg")),
                pDBallInShadowOfTheWallR, pABallInShadowOfTheWallR, pSBallInShadowOfTheWallR));

        // Soccer ball
        Point4 pDSoccer = Point4.createPoint(-2, -8.5,-30); // position
        Point4 pASoccer = Point4.createPoint(0,0,0);  // rotation
        Point4 pSSoccer = Point4.createPoint(2, 2, 2); // Scaling factors
        scene.add(makeASphere(
                new Material(0.3, 0.5, 10),
                new TextureColour(new TextureMap("res/soccer.jpg")),
                pDSoccer, pASoccer, pSSoccer));

        // Cube at the corner
        UniformColour[] largestCubeColours = new UniformColour[6];
        for (int i = 0; i < 6; i++) {
            largestCubeColours[i] = new UniformColour(new Color(83, 250, 44));
        }

        Point4[] largestCubePlacement = {
                Point4.createPoint(-16, -7.99, -25), // position
                Point4.createPoint(0,Math.toRadians(45),0), // rotation
                Point4.createPoint(5,5,5) // scaling
        };

        ArrayList<ThreeDSurface> cube = makeACube(6, new Material(0.4, 0.4, 6), largestCubeColours, largestCubePlacement);
        scene.add(cube);

        // small cube on a big cube - texture coloured
        TextureColour[] upperCubeColours = new TextureColour[6];
        for (int i = 0; i < 6; i++) {
            upperCubeColours[i] = new TextureColour(new TextureMap("res/scifi.jpg"));
        }

        Point4[] upperCubePlacement = {
                Point4.createPoint(-16, -7.99+4, -25), // position
                Point4.createPoint(0,Math.toRadians(60),0), // rotation
                Point4.createPoint(3,3,3) // scaling
        };
        ArrayList<ThreeDSurface> upperCube = makeACube(6, new Material(0.4, 0.4, 6), upperCubeColours, upperCubePlacement);
        scene.add(upperCube);

        // Small cube
        UniformColour[] smallCubeColours = new UniformColour[6];
        for (int i = 0; i < 6; i++) {
            smallCubeColours[i] = new UniformColour(new Color(102,0,255));
        }

        Point4[] cubePlacement2 = {
                Point4.createPoint(-16, -7.99-1, -15), // position
                Point4.createPoint(0,Math.toRadians(45),0), // rotation
                Point4.createPoint(3,3,3) // scaling
        };

        ArrayList<ThreeDSurface> smallCube = makeACube(6, new Material(0.3, 0.5, 6), smallCubeColours, cubePlacement2);
        scene.add(smallCube);

        // Little Robot
        int facingAngle = 15;
        UniformColour[] RobotColours = new UniformColour[6];
        for (int i = 0; i < 6; i++) {
            RobotColours[i] = new UniformColour(Color.GRAY);
        }

        // Bottom Part (L/R Foot)
        Point4[] footLPlacement = {
                Point4.createPoint(-5+1, -9.5, -5), // position
                Point4.createPoint(0,Math.toRadians(-facingAngle),0), // rotation
                Point4.createPoint(1,2,1) // scaling
        };

        Point4[] footRPlacement = {
                Point4.createPoint(-5-1, -9.5, -5), // position
                Point4.createPoint(0,Math.toRadians(-facingAngle),0), // rotation
                Point4.createPoint(1,2,1) // scaling
        };

        ArrayList<ThreeDSurface> footL = makeACube(6, new Material(0.3, 0.5, 50), RobotColours, footLPlacement);
        scene.add(footL);

        ArrayList<ThreeDSurface> footR = makeACube(6, new Material(0.3, 0.5, 50), RobotColours, footRPlacement);
        scene.add(footR);

        // Body Part
        Point4[] bodyPlacement = {
                Point4.createPoint(-5, -9.5+3, -5), // position
                Point4.createPoint(0,Math.toRadians(-facingAngle),0), // rotation
                Point4.createPoint(3,4,2) // scaling
        };
        ArrayList<ThreeDSurface> body = makeACube(6, new Material(0.3, 0.5, 50), RobotColours, bodyPlacement);
        scene.add(body);

        Point4[] handLPlacement = {
                Point4.createPoint(-5+2, -9.5+3, -5), // position
                Point4.createPoint(Math.toRadians(-180),Math.toRadians(-facingAngle),Math.toRadians(-180)), // rotation
                Point4.createPoint(1,3,1) // scaling
        };

        Point4[] handRPlacement = {
                Point4.createPoint(-5-2.3, -9.5+3+0.1, -5+1), // position
                Point4.createPoint(Math.toRadians(120),Math.toRadians(-facingAngle+15),Math.toRadians(10)), // rotation
                Point4.createPoint(1,3,1) // scaling
        };

        ArrayList<ThreeDSurface> handL = makeACube(6, new Material(0.3, 0.5, 50), RobotColours, handLPlacement);
        scene.add(handL);

        ArrayList<ThreeDSurface> handR = makeACube(6, new Material(0.3, 0.5, 50), RobotColours, handRPlacement);
        scene.add(handR);

        // Head
        Point4[] headPlacement = {
                Point4.createPoint(-5, -9.5+3+3, -5), // position
                Point4.createPoint(0,Math.toRadians(-facingAngle),0), // rotation
                Point4.createPoint(4,3,3) // scaling
        };
        ArrayList<ThreeDSurface> head = makeACube(6, new Material(0.3, 0.5, 50), RobotColours, headPlacement);
        scene.add(head);


        // Render the scene at the given camera and light source
        scene.render(camera, pLightW);
        // Uncomment if you want to verify the camera target point in the scene
        //putAxes(camera.image);

        // Display image in a JPanel/JFrame
        Display.show(camera.image);

        // Uncomment if you want to save your scene in an image file
        Display.write(camera.image, "scene.png");
    }
}
