import com.jogamp.opengl.GL3;

import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
import gmaths.Vec4;

/**
* I declare that this code is my own work
*
* @author   Liong Kah Yee, kyliong1@sheffield.ac.uk
* 
*/

public class lightSwitch {
    private Model switchModel;
    private TransformNode transformNode;
    private boolean isOn;
    private Vec3 position;
    private Light globalLight;
    
    public lightSwitch(GL3 gl, Vec3 position, Light globalLight) {
        this.position = position; 
        this.globalLight = globalLight;

        Mat4 translation = Mat4Transform.translate(position.x, position.y, position.z);
        Mat4 rotation = Mat4Transform.rotateAroundY(-90);
        Mat4 transform = Mat4.multiply(translation, rotation);
        
        this.switchModel = createSwitchModel(gl);
        this.transformNode = new TransformNode("SwitchTransform", transform);

        ModelNode switchModelNode = new ModelNode("SwitchModel", switchModel);
        this.transformNode.addChild(switchModelNode);
        this.isOn = false;
    }

    private Model createSwitchModel(GL3 gl) {
        Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_1t.txt");
        Material material = new Material(new Vec3(0.8f, 0.8f, 0.2f), new Vec3(0.8f, 0.8f, 0.2f), new Vec3(0.8f, 0.8f, 0.8f), 32.0f);
        return new Model("Switch", mesh, new Mat4(1), shader, material, null, null, null, null);
    }

    public void render(GL3 gl) {
        if (switchModel != null) { 
            switchModel.render(gl);
        } else {
            System.out.println("switchModel is null!");
        }
      }

    public void toggle() {
        isOn = !isOn;
        if (globalLight != null) {
            if (isOn) {
                globalLight.setMaterial(new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), 32.0f));
            } else {
                globalLight.setMaterial(new Material(new Vec3(0.0f, 0.0f, 0.0f), new Vec3(0.0f, 0.0f, 0.0f), new Vec3(0.0f, 0.0f, 0.0f), 32.0f));
            }
        }
    }

    public TransformNode getTransformNode() {
        return this.transformNode;
    }

    public boolean isClicked(float mouseX, float mouseY, Camera camera) {
        // Convert world position to clip space
        Mat4 viewProjection = Mat4.multiply(camera.getPerspectiveMatrix(), camera.getViewMatrix());
        Vec4 clipSpace = viewProjection.multiply(new Vec4(position, 1.0f));
        if (clipSpace.w != 0) {
            clipSpace.x /= clipSpace.w; // Normalize x
            clipSpace.y /= clipSpace.w; // Normalize y
        }
    
        // Convert to normalized device coordinates (NDC)
        float ndcX = clipSpace.x;
        float ndcY = clipSpace.y;
    
        // Convert mouse coordinates to NDC
        float mouseNdcX = 2.0f * mouseX - 1.0f; // Assuming mouseX is normalized between 0 and 1
        float mouseNdcY = 1.0f - 2.0f * mouseY; // Assuming mouseY is normalized between 0 and 1
    
        // Check if the mouse click is within a small region around the switch's NDC position
        float switchNdcSize = 0.1f; // Adjust for the size of the clickable area
        return mouseNdcX >= ndcX - switchNdcSize &&
               mouseNdcX <= ndcX + switchNdcSize &&
               mouseNdcY >= ndcY - switchNdcSize &&
               mouseNdcY <= ndcY + switchNdcSize;
    }
    
    

    public boolean isOn() {
        return this.isOn;
    }

    public void dispose(GL3 gl) {
        switchModel.dispose(gl);  
    }
}
