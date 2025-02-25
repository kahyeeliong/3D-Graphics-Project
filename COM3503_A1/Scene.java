import gmaths.*;

import java.util.ArrayList;
import java.util.List;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;

/**
* I declare that this code is my own work
*
* @author   Liong Kah Yee, kyliong1@sheffield.ac.uk
* 
*/

public class Scene {

    private Camera camera;
    private Light light;
    private Texture diffuse;
    private Material material;
    private Mat4 modelMatrix;

    public Scene(GL3 gl, Mat4 modelMatrix, Material material, Light l, Camera camera, Texture diffuse) {
        this.camera = camera;
        light = l;
        this.diffuse = diffuse;
        this.material = material;
        this.modelMatrix = modelMatrix;
    }

    public static Scene createScene(GL3 gl, Camera camera, Light light, Texture diffuse) {
        float size = 16f;
        Mat4 modelMatrix = Mat4Transform.scale(size,size,size);
        Material material = new Material(
            new Vec3(1.0f,1.0f,1.0f), 
            new Vec3(1.0f,1.0f,1.0f), 
            new Vec3(1.0f,1.0f,1.0f), 
            16.0f
        );

        return new Scene(gl, modelMatrix, material, light, camera, diffuse);
    }


    public List<Model> getScene(GL3 gl) {
        List<Model> sceneModels = new ArrayList<>();
        float size = 16f;

        Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_1t.txt");

        // floor
        Mat4 floorModelMatrix = Mat4Transform.translate(-size*1.0f,-size * 0.5f + size / 2,0);
        floorModelMatrix = Mat4.multiply(floorModelMatrix, Mat4Transform.scale(size, 1f, size));
        sceneModels.add(new Model("floor", mesh, floorModelMatrix, shader, material, light, camera, diffuse));
        
        // backwall
        Mat4 backWallModelMatrix  = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
        backWallModelMatrix  = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), backWallModelMatrix );
        backWallModelMatrix  = Mat4.multiply(Mat4Transform.translate(-size*1.5f,size*0.5f,0), backWallModelMatrix );
        sceneModels.add(new Model("backWall", mesh, backWallModelMatrix, shader, material, light, camera, diffuse));

        // rightwall
        Mat4 rightWallModelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(-90), modelMatrix);
        rightWallModelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(180), rightWallModelMatrix);
        rightWallModelMatrix  =  Mat4.multiply(Mat4Transform.translate(-size*1.0f,size*0.5f, -size*0.5f), rightWallModelMatrix );
        sceneModels.add(new Model("rightWall", mesh, rightWallModelMatrix, shader, material, light, camera, diffuse));
        
        // leftwall
        Mat4 leftWallModelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
        leftWallModelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(180), leftWallModelMatrix);
        leftWallModelMatrix  =  Mat4.multiply(Mat4Transform.translate(-size*1.0f,size*0.5f, size*0.5f), leftWallModelMatrix );
        sceneModels.add(new Model("rightWall", mesh, leftWallModelMatrix, shader, material, light, camera, diffuse));
        
        // ceiling
        Mat4 ceilingModelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(180), modelMatrix); 
        ceilingModelMatrix = Mat4.multiply(Mat4Transform.translate(-size*1.0f,size * 0.5f + size / 2,0), ceilingModelMatrix);
        sceneModels.add(new Model("floor", mesh, ceilingModelMatrix, shader, material, light, camera, diffuse));
        
        return sceneModels;
    }
    
}