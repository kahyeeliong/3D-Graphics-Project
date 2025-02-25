import gmaths.*;
import com.jogamp.opengl.*;

/**
* I declare that this code is my own work
*
* @author   Liong Kah Yee, kyliong1@sheffield.ac.uk
* 
*/

public class Robot2 {
    private Model cube;
    private Model sphere;
    private Shader shader;
    private Light spotlight;

    private SGNode robot2Root;
    private TransformNode translateX, rotateLight, rotateBody, bulb;
    private float xPosition = 6.0f;
    private float rotateLightAngle;
    public Vec4 lightPos;
    public Vec3 shadeDirection = new Vec3(0,-1,0);

    private float moveSpeed = 0.02f;
    private float currentX = 6.0f; 
    private float currentZ = -6.0f; 
    private float targetX = 6.0f; 
    private float targetZ = 6.0f;
    private boolean isTranslating = true;
    private boolean isRotating = false;
    private float currentBodyAngle = 0.0f; 
    private float targetBodyAngle = 0.0f; 
    private static final float rotationSpeed = 1.0f;
    private boolean isBouncing = false; 
    private float bounceScale = 1.0f; 
    private float targetBounceScale = 0.5f; 
    private static final float bounceSpeed = 0.02f; 

    private Vec3 spotlightPosition; 
    private Vec3 spotlightDirection; 
    private float spotlightCutoff = 15.0f; 
    private float spotlightIntensity = 1.0f;
    
    private float cubeLength = 2.0f;
    private float cubeWidth = 1.0f;
    private float sphereHeight = 1.5f;
    private float sphereWidth = 0.3f;
    private float leftEyesSize = 0.3f;
    private float rightEyesSize = 0.3f;
    private float casingSize = 0.3f;
    private float bulbSize = 0.2f;

    public Robot2(GL3 gl, Model cube, Model sphere, Light spotlight){
        this.cube = cube;
        this.sphere = sphere;
        this.spotlight = spotlight;
        spotlight = new Light(gl, true);
        spotlight.setSpotlightCutoff(15.0f);
        spotlight.setSpotlightIntensity(1.0f);
        this.shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
        robot2Root = new NameNode("robot2 structure");
        setup();  
    }

    public float getCurrentX() {
        return currentX;
    }
    
    public float getCurrentZ() {
        return currentZ;
    }

    private SGNode makeLowerCube(){
        NameNode lowerCubeName = new NameNode("lower cube");
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(cubeWidth, cubeWidth, cubeLength));
        m =  Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode lowerCube = new TransformNode("scale(1.0, 2.0, 2.5); translate(0, 0.5, 0)", m);
        ModelNode cubeNode = new ModelNode("cube(0)", cube);
        lowerCubeName.addChild(lowerCube);
            lowerCube.addChild(cubeNode);
        return lowerCubeName;
    }

    private SGNode makeLeftEyes(){
        NameNode leftEyesName = new NameNode("left eyes branch");
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(leftEyesSize, leftEyesSize, leftEyesSize));
        m =  Mat4.multiply(m, Mat4Transform.translate(-0.8f, cubeWidth * 2 ,3.5f));
        TransformNode leftEyes = new TransformNode("scale(0.3, 0.3, 0.3); translate(-0.8, cubeWidth * 2 ,3.5)", m);
        ModelNode sphereNode = new ModelNode("Sphere(0)", sphere);
        leftEyesName.addChild(leftEyes); 
            leftEyes.addChild(sphereNode);
        return leftEyesName;
    }

    private SGNode makeRightEyes(){
        NameNode rightEyesName = new NameNode("righ eyes branch");
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(rightEyesSize, rightEyesSize, rightEyesSize));
        m =  Mat4.multiply(m, Mat4Transform.translate(0.8f, cubeWidth * 2,3.5f));
        TransformNode rightEyes = new TransformNode("scale(0.3, 0.3, 0.3); translate(0.8, cubeWidth * 2,3.5)", m);
        ModelNode sphereNode = new ModelNode("Sphere(1)", sphere);
        rightEyesName.addChild(rightEyes); 
            rightEyes.addChild(sphereNode);
        return rightEyesName;
    }

    private SGNode makeupperSphere(){
        NameNode upperSphereName = new NameNode("upper sphere");
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(sphereWidth, sphereHeight, sphereWidth));
        m =  Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode upperSphere = new TransformNode("scale(0.3, 1.5, 0.3); translate(0, 0.5, 0)", m);
        ModelNode sphereNode = new ModelNode("sphere(2)", sphere);
        upperSphereName.addChild(upperSphere);
            upperSphere.addChild(sphereNode);
        return upperSphereName;
    }

    private SGNode makeCasing(){
        NameNode casingName = new NameNode("casing branch");
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(casingSize, casingSize, casingSize));
        m =  Mat4.multiply(m, Mat4Transform.translate(0, 0.5f,0));
        TransformNode casing = new TransformNode("scale(0.3, 0.3, 0.3); translate(0.8f, 0.8f,0.4f)", m);
        ModelNode sphereNode = new ModelNode("Sphere(3)", sphere);
        casingName.addChild(casing); 
            casing.addChild(sphereNode);
        return casingName;
    }

    private SGNode makeBulb(Light spotlight){
        NameNode bulbName = new NameNode("bulb branch");
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(Mat4Transform.scale(bulbSize, bulbSize, 0.4f), Mat4Transform.translate(0, 0.5f, 0.5f));
        bulb = new TransformNode("scale(0.2, 0.3, 0.2); translate(0.8f, 0.8f, 0.4f)", m);  // Store bulb reference
        ModelNode sphereNode = new ModelNode("Sphere(4)", sphere);
        bulbName.addChild(bulb);
        bulb.addChild(sphereNode);
        
    
        // Calculate the bulb's world position
        Vec4 bulbPosition = bulb.worldTransform.multiply(new Vec4(0, 0, 0, 1));
        spotlight.setSpotlightPosition(new Vec3(bulbPosition.x, bulbPosition.y, bulbPosition.z));
    
        // Set the spotlight's direction (downward)
        Vec4 spotlightDirection = bulb.worldTransform.multiply(new Vec4(0, -1, 0, 0));  // Direction pointing downward
        spotlight.setSpotlightDirection(new Vec3(spotlightDirection.x, spotlightDirection.y, spotlightDirection.z));
    
        return bulbName;
    }

    private void setup() {
        SGNode cubeBranch = makeLowerCube();
        SGNode leftEyesBranch = makeLeftEyes();
        SGNode rightEyesBranch = makeRightEyes();
        SGNode upperSphereBranch = makeupperSphere();
        SGNode casingBranch = makeCasing();
        SGNode bulbBranch = makeBulb(spotlight);

        translateX = new TransformNode("translate(" + xPosition + ",0,0)", Mat4Transform.translate(xPosition, 0, 0));
        rotateBody = new TransformNode("rotateBody", Mat4Transform.rotateAroundY(0));

        TransformNode translateToTop = new TransformNode("translate(0,"+ cubeWidth+",0)",
            Mat4Transform.translate(0,cubeWidth,0));

        TransformNode translateToCasing = new TransformNode("translate(0,"+ sphereHeight+",0)",
            Mat4Transform.translate(0,sphereHeight,0));
        
        TransformNode tiltBulb = new TransformNode("tiltbulb(40)", Mat4Transform.rotateAroundX(40));
        rotateLight = new TransformNode("rotateLight", Mat4Transform.rotateAroundY(0)); 
        
        robot2Root.addChild(translateX);
        translateX.addChild(rotateBody); 
        rotateBody.addChild(cubeBranch);
        cubeBranch.addChild(leftEyesBranch);
        cubeBranch.addChild(rightEyesBranch);
        cubeBranch.addChild(translateToTop);
        translateToTop.addChild(upperSphereBranch);
        upperSphereBranch.addChild(translateToCasing);
        translateToCasing.addChild(rotateLight);
        rotateLight.addChild(casingBranch);
        casingBranch.addChild(tiltBulb);
        tiltBulb.addChild(bulbBranch);
        
        robot2Root.update();
    }

    private enum Direction {
        FORWARD, RIGHT, BACKWARD, LEFT
    }
    
    private Direction currentDirection = Direction.FORWARD;
    
    private void moveForward() {
        if (isTranslating) {
            rotateLightAngle = (rotateLightAngle + rotationSpeed) % 360.0f;
            rotateLight.setTransform(Mat4Transform.rotateAroundY(rotateLightAngle));
            switch (currentDirection) {
                case FORWARD:
                    if (currentZ < targetZ) {
                        currentZ += moveSpeed;
                        if (currentZ > targetZ) currentZ = targetZ;
                        translateX.setTransform(Mat4Transform.translate(currentX, 0, currentZ));
                    } else {
                        // Reached target, rotate and prepare for the next move
                        isTranslating = false;
                        rotateAndChangeDirection(Direction.LEFT); // Rotate to face left
                        targetX = -6.0f; // Move to the left-front corner
                    }
                    break;
                case LEFT:
                    if (currentX > targetX) {
                        currentX -= moveSpeed;
                        if (currentX < targetX) currentX = targetX;
                        translateX.setTransform(Mat4Transform.translate(currentX, 0, currentZ));
                    } else {
                        isTranslating = false;
                        rotateAndChangeDirection(Direction.BACKWARD); // Rotate to face backward
                        targetZ = -6.0f; // Move to the left-back corner
                    }
                    break;
                case BACKWARD:
                    if (currentZ > targetZ) {
                        currentZ -= moveSpeed;
                        if (currentZ < targetZ) currentZ = targetZ;
                        translateX.setTransform(Mat4Transform.translate(currentX, 0, currentZ));
                    } else {
                        isTranslating = false;
                        rotateAndChangeDirection(Direction.RIGHT); // Rotate to face right
                        targetX = 6.0f; // Move to the right-back corner
                    }
                    break;
                case RIGHT:
                    if (currentX < targetX) {
                        currentX += moveSpeed;
                        if (currentX > targetX) currentX = targetX;
                        translateX.setTransform(Mat4Transform.translate(currentX, 0, currentZ));
                    } else {
                        isTranslating = false;
                        rotateAndChangeDirection(Direction.FORWARD); // Rotate to face forward
                        targetZ = 6.0f; // Move to the right-front corner
                    }
                    break;
            }
        }
    }
       
    
    private void rotateAndChangeDirection(Direction newDirection) {
        // Start bounce effect
        isBouncing = true;
        targetBounceScale = 0.5f;

        // Update target body angle for rotation
        switch (currentDirection) {
            case FORWARD:
                if (newDirection == Direction.RIGHT) targetBodyAngle = currentBodyAngle + 90.0f;
                else if (newDirection == Direction.LEFT) targetBodyAngle = currentBodyAngle - 90.0f;
                break;
            case RIGHT:
                if (newDirection == Direction.BACKWARD) targetBodyAngle = currentBodyAngle + 90.0f;
                else if (newDirection == Direction.FORWARD) targetBodyAngle = currentBodyAngle - 90.0f;
                break;
            case BACKWARD:
                if (newDirection == Direction.LEFT) targetBodyAngle = currentBodyAngle + 90.0f;
                else if (newDirection == Direction.RIGHT) targetBodyAngle = currentBodyAngle - 90.0f;
                break;
            case LEFT:
                if (newDirection == Direction.FORWARD) targetBodyAngle = currentBodyAngle + 90.0f;
                else if (newDirection == Direction.BACKWARD) targetBodyAngle = currentBodyAngle - 90.0f;
                break;
        }

        // Normalize the target angle to stay within [0, 360) degrees
        targetBodyAngle = (targetBodyAngle + 360.0f) % 360.0f;

        // Set state for rotation
        isRotating = true;
        currentDirection = newDirection;
    }

    private void updateBounceEffect() {
        if (isBouncing) {
            if (Math.abs(targetBounceScale - bounceScale) > bounceSpeed) {
                bounceScale += Math.signum(targetBounceScale - bounceScale) * bounceSpeed;
            } else {
                bounceScale = targetBounceScale;
                if (bounceScale < 1.0f) {
                    targetBounceScale = 1.0f; // Reset to normal after squashing
                } else {
                    isBouncing = false; // End bounce effect
                }
            }
    
            // Apply bounce to rotateBody without interfering with rotateLight
            rotateBody.setTransform(
                Mat4.multiply(
                    Mat4Transform.scale(1.0f, bounceScale, 1.0f),
                    Mat4Transform.rotateAroundY(currentBodyAngle)
                )
            );
        }
    }

    private void updateRotation() {
        if (isRotating) {
            if (Math.abs(targetBodyAngle - currentBodyAngle) > rotationSpeed) {
                // Rotate towards the target angle incrementally
                currentBodyAngle += Math.signum(targetBodyAngle - currentBodyAngle) * rotationSpeed;
                currentBodyAngle = (currentBodyAngle + 360.0f) % 360.0f; // Normalize angle
            } else {
                // Snap to the target angle and stop rotation
                currentBodyAngle = targetBodyAngle;
                isRotating = false;
                isTranslating = true; // Resume translating after rotation
            }
    
            // Update the rotateBody transformation
            rotateBody.setTransform(Mat4Transform.rotateAroundY(currentBodyAngle));
        }
    }
    
    private void updateSpotlightFromBulb() {
        Vec4 bulbPosition = bulb.worldTransform.multiply(new Vec4(0, 0, 0, 1));
        spotlight.setSpotlightPosition(new Vec3(bulbPosition.x, bulbPosition.y, bulbPosition.z));
    
        Vec4 spotlightDirection = bulb.worldTransform.multiply(new Vec4(0, -1, 0, 0));
        spotlight.setSpotlightDirection(new Vec3(spotlightDirection.x, spotlightDirection.y, spotlightDirection.z));
    }

    public void updateBranches() {
        robot2Root.update();
        updateSpotlightFromBulb();
        double elapsedTime = getSeconds()-startTime;
        startTime = getSeconds();
        rotateLightAngle = (rotateLightAngle + rotationSpeed * (float) elapsedTime) % 360.0f;
        rotateLight.setTransform(Mat4Transform.rotateAroundY(rotateLightAngle));
        robot2Root.update();

        updateRotation();
        updateBounceEffect();
    
        if (!isRotating) moveForward();
    }

    private double startTime;

    private double getSeconds() {
        return System.currentTimeMillis()/1000.0;
      }

    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        sphere.dispose(gl);
        cube.dispose(gl);
      }

    public void render(GL3 gl) {
        spotlight.render(gl);
        updateBranches();
        robot2Root.draw(gl);
      }
}