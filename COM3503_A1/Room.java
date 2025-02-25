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

public class Room {

  private static int NUM_WALLS = 5;
  private Model[] wall;
  private List<Model> windowWall;
  private Camera camera;
  private Light light;
  private Texture t0, t1, t2, t3, t4, t5, t6;
  private float size = 16f;
  // private lightSwitch switchOnRightWall;

  public Room(GL3 gl, Camera c, Light l, Texture t0, Texture t1, Texture t2, Texture t3, Texture t4, Texture t5, Texture t6) {
    camera = c;
    light = l;
    this.t0 = t0;
    this.t1 = t1;
    this.t2 = t2;
    this.t3 = t3;
    this.t4 = t4;
    this.t5 = t5;
    this.t6 = t6;
    wall = new Model[NUM_WALLS];
    wall[0] = makeWall0(gl);
    wall[1] = makeWall1(gl);
    wall[2] = null;
    windowWall = makeWall2(gl);
    wall[3] = makeWall3(gl);
    wall[4] = makeWall4(gl);
    // switchOnRightWall = new lightSwitch(gl, new Vec3(size * 0.5f, size * 0.5f - 2.0f, -1.0f), l);
  }
 
  private Model makeWall0(GL3 gl) {
    String name="floor";
    Vec3 basecolor = new Vec3(0.5f, 0.5f, 0.5f); // grey
    Material material = new Material(basecolor, basecolor, new Vec3(0.3f, 0.3f, 0.3f), 4.0f);
    //create floor
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size,1f,size), modelMatrix);
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_1t.txt");
    Model model = new Model(name, mesh, modelMatrix, shader, material, light, camera, t0);
    return model;
  }

  private Model makeWall1(GL3 gl) {
    String name="wall";
    Vec3 basecolor = new Vec3(0.5f, 0.5f, 0.5f); // grey
    Material material = new Material(basecolor, basecolor, new Vec3(0.3f, 0.3f, 0.3f), 4.0f);
    // back wall
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size,1f,size), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,size*0.5f,-size*0.5f), modelMatrix);
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    Model model = new Model(name, mesh, modelMatrix, shader, material, light, camera, t1, t2);
    return model;
  }

  private List<Model> makeWall2(GL3 gl) {
    String name = "wall_with_window";
    Material material = new Material(new Vec3(0.1f, 0.5f, 0.91f), new Vec3(0.1f, 0.5f, 0.91f), new Vec3(0.3f, 0.3f, 0.3f), 4.0f);
    List<Model> windowWall = new ArrayList<>();
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_1t.txt");
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());

    // Create the grid structure for the wall with a window
    for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 4; j++) {
            // Leave space for the window (e.g., skip center grid cells)
            if ((i == 1 && j == 1) || (i == 1 && j == 2)) continue;

            // Create a scaled and translated section of the wall
            Mat4 modelMatrix = new Mat4(1);
            modelMatrix = Mat4.multiply(Mat4Transform.scale(size / 4, 1f, size / 4), modelMatrix);
            modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
            modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), modelMatrix);
            modelMatrix = Mat4.multiply(
                Mat4Transform.translate(
                    -size * 0.5f, 
                    i * size / 4 + (size / 4) * 0.5f, 
                    (j - 1) * size / 4 - (size / 4) * 0.5f
                ), 
                modelMatrix
            );

            // Create and add the model
            Model section = new Model(name, mesh, modelMatrix, shader, material, light, camera, t3);
            windowWall.add(section);
        }
    }

    return windowWall;
}
  
  private Model makeWall3(GL3 gl) {
    String name="wall";
    Material material = new Material(new Vec3(0.1f, 0.5f, 0.91f), new Vec3(0.1f, 0.5f, 0.91f), new Vec3(0.3f, 0.3f, 0.3f), 4.0f);
    // right wall
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size,1f,size), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(-90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(size*0.5f,size*0.5f,0), modelMatrix);
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    mesh.setTextureScale(gl, 4f, 3f); 
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_1t.txt");
    Model model = new Model(name, mesh, modelMatrix, shader, material, light, camera, t4);
    return model;
  }

  private Model makeWall4(GL3 gl) {
    String name="ceiling";
    Vec3 basecolor = new Vec3(0.5f, 0.5f, 0.5f); // grey
    Material material = new Material(basecolor, basecolor, new Vec3(0.3f, 0.3f, 0.3f), 4.0f);
    //create ceiling
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size,1f,size), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(180), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,size*1.0f,0), modelMatrix);
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    Model model = new Model(name, mesh, modelMatrix, shader, material, light, camera, t5, t6);
    return model;
  }

  public void render(GL3 gl) {
    // Render all walls first (handle Wall2 with multiple models)
    for (int i = 0; i < NUM_WALLS; i++) {
        if (i == 2) {
            // Render all parts of wall2
            for (Model model : makeWall2(gl)) {
                model.render(gl);
            }
        } else if (wall[i] != null) {
            wall[i].render(gl);
        }
    }
    // switchOnRightWall.render(gl);
  }

  public void dispose(GL3 gl) {
    for (int i = 0; i < NUM_WALLS; i++) {
        if (i == 2) {
            // Dispose all parts of wall2
            for (Model model : makeWall2(gl)) {
                model.dispose(gl);
            }
        } else if (wall[i] != null) {
            wall[i].dispose(gl);
        }
    }
    // switchOnRightWall.dispose(gl);
  }
}