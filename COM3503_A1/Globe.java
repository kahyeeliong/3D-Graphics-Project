import gmaths.*;
import com.jogamp.opengl.*; 

/**
* I declare that this code is my own work
*
* @author   Liong Kah Yee, kyliong1@sheffield.ac.uk
* 
*/

public class Globe {
    private Model sphere1, sphere2, cube; 
    private Shader shader;

    private SGNode globeRoot;
    private TransformNode translateX, translateZ, rotateGlobe;  // Rotation for the globe
    private float rotateGlobeAngle;
    private float xPosition = 3.0f;
    private float zPosition = 3.0f;
    private float pedestalHeight = 3.0f;
    private float AxisHeight = 4.0f;
    private static final float rotationSpeed = 5.0f;  // Speed of rotation

    public Globe(GL3 gl,Model cube,  Model sphere1,  Model sphere2) {
        this.cube = cube;
        this.sphere1 = sphere1;
        this.sphere2 = sphere2;
        this.shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
        globeRoot = new NameNode("globe root");
        setup();
    }

    private void setup() {
        SGNode globeBranch = makeGlobe();
        SGNode axisBranch = makeAxis();
        SGNode pedestalBranch = makePedestal();

        translateX = new TransformNode("translate("+xPosition+",0,0)", Mat4Transform.translate(xPosition,0,0)); 
        translateZ = new TransformNode("translate("+zPosition+",0,0)", Mat4Transform.translate(0,0,zPosition)); 
        rotateGlobe = new TransformNode("rotate globe", Mat4Transform.rotateAroundY(0));
        
        TransformNode translateToTop = new TransformNode("translate(0,"+ pedestalHeight +",0)",
            Mat4Transform.translate(0,pedestalHeight,0));

        TransformNode translateToGlobe = new TransformNode("translate(0,"+ AxisHeight / 2 +",0)",
            Mat4Transform.translate(0, AxisHeight / 2 ,0));

        globeRoot.addChild(translateX);
        translateX.addChild(translateZ);
        translateZ.addChild(pedestalBranch);
        pedestalBranch.addChild(translateToTop);
        translateToTop.addChild(axisBranch);
        axisBranch.addChild(translateToGlobe);
        translateToGlobe.addChild(rotateGlobe);
        rotateGlobe.addChild(globeBranch);

        globeRoot.update();
    }

    private SGNode makeGlobe() {
        NameNode globeName = new NameNode("globe");
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(2.5f, 2.5f, 2.5f));  
        m = Mat4.multiply(m, Mat4Transform.translate(0,-0.6f, 0));
        TransformNode globe = new TransformNode("scale(2.0, 2.0, 2.0)", m);
        ModelNode sphereNode = new ModelNode("globe(0)", sphere1);
        globeName.addChild(globe);
            globe.addChild(sphereNode);
        return globeName;
    }

    private SGNode makeAxis() {
        NameNode axisName = new NameNode("axis");
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(0.3f, 4.0f, 0.3f));  
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.1f, 0));
        TransformNode axis = new TransformNode("scale(0.1, 5.0, 0.1)", m);
        ModelNode sphereNode = new ModelNode("axis(0)", sphere2);  
        axisName.addChild(axis);
            axis.addChild(sphereNode);
        return axisName;
    }

    private SGNode makePedestal() {
        NameNode pedestalName = new NameNode("pedestal");
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(1.5f, 1.5f, 1.5f));  // Flat pedestal shape
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));  // Place it at the bottom
        TransformNode pedestal = new TransformNode("scale(3.0, 0.5, 3.0)", m);
        ModelNode cubeNode = new ModelNode("cube(0)", cube);
        pedestalName.addChild(pedestal);
        pedestal.addChild(cubeNode);
        return pedestalName;
    }

    public void updateBranches() {
        double elapsedTime = getSeconds()-startTime;
        startTime = getSeconds();
        rotateGlobeAngle = (rotateGlobeAngle + rotationSpeed * (float) elapsedTime) % 360.0f;
        rotateGlobe.setTransform(Mat4Transform.rotateAroundY(rotateGlobeAngle));
        globeRoot.update();
    }

    private double startTime;

    private double getSeconds() {
        return System.currentTimeMillis()/1000.0;
    }

    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        sphere1.dispose(gl);
        sphere2.dispose(gl);
        cube.dispose(gl);
      }

    public void render(GL3 gl) {
        updateBranches(); 
        globeRoot.draw(gl);
    }
}
