import gmaths.*;
import java.util.ArrayList;
import java.util.List;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.Texture;

/**
* I declare that this code is my own work
*
* @author   Liong Kah Yee, kyliong1@sheffield.ac.uk
* 
*/
  
public class Spacecraft_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  public Spacecraft_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,6f,15f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }

  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    // Get positions of Robot1 and Robot2
    Vec3 robot1Position = new Vec3(robot1.getXPosition(), 0, robot1.getZPosition());
    Vec3 robot2Position = new Vec3(robot2.getCurrentX(), 0, robot2.getCurrentZ());
    float distance = calculateDistance(robot1Position, robot2Position);
    if (distance < 5.0f) {
      robot1.setDancing(true);
    } else {
        robot1.setDancing(false);
    }
    robot1.updateBranches();
    robot2.updateBranches();
    globe.updateBranches();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    room.dispose(gl);
    globalLight.dispose(gl);
    // spotlight.dispose(gl);
    for (Model model : scene1Model) {
      model.dispose(gl);
    }
    for (Model model : scene2Model) {
      model.dispose(gl);
    }
    sphere.dispose(gl);
    robot1.dispose(drawable);
    cube.dispose(gl);
    robot2.dispose(drawable);
    globe.dispose(drawable);
  }

  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */
  private TextureLibrary textures;
  private Camera camera;
  private Light globalLight, spotlight;
  private Room room;
  private List<Model> scene1Model = new ArrayList<>();
  private List<Model> scene2Model = new ArrayList<>();
  private Robot1 robot1;
  private Robot2 robot2;
  private Globe globe;
  private Model sphere,  sphere1, sphere2, cube;
  // private lightSwitch switchOnRightWall;

  public void initialise(GL3 gl) {
    textures = new TextureLibrary();
    textures.add(gl, "floor", "assets/textures/floor.jpg", false);
    textures.add(gl, "grey", "assets/textures/Grey.jpg", false);
    textures.add(gl, "blue", "assets/textures/blueTexture.jpg", false);
    textures.add(gl, "shiny", "assets/textures/shiny.jpg", false);
    textures.add(gl, "bird", "assets/textures/bird.jpg", true);
    textures.add(gl, "name1", "assets/textures/diffuse_liong.jpg", false);
    textures.add(gl, "name2", "assets/textures/specular_liong.jpg", false);
    textures.add(gl, "scene1", "assets/textures/space.jpg", false);
    textures.add(gl, "scene2", "assets/textures/space2.jpg", false);
    textures.add(gl, "shinySpecular", "assets/textures/shiny_specular.jpg", false);
    textures.add(gl, "metallic", "assets/textures/metallic.jpg", false);
    textures.add(gl, "metallic_specular", "assets/textures/metallic_specular.jpg", false);
    textures.add(gl, "globe", "assets/textures/globe.jpg", false);
    textures.add(gl, "metal", "assets/textures/metal.jpg", false);
    textures.add(gl, "metal_specular", "assets/textures/metal_specular.jpg", false);
    textures.add(gl, "fun", "assets/textures/fun.jpg", false);
    textures.add(gl, "surface", "assets/textures/surface_specular.jpg", false);

    globalLight = new Light(gl, false);
    globalLight.setCamera(camera);

    spotlight = new Light(gl, true); 
    spotlight.setCamera(camera);

    // lightSwitch switchOnRightWall = new lightSwitch(gl, new Vec3(8.0f, 3.0f, -1.0f), globalLight);

    room = new Room(gl, camera, globalLight, textures.get("floor"), textures.get("name1"), textures.get("name2"), 
      textures.get("blue"), textures.get("bird"), textures.get("shiny"), textures.get("shinySpecular"));

    // scene (skybox)
    Scene scene1 = Scene.createScene(gl, camera, globalLight, textures.get("scene1"));
    Scene scene2 = Scene.createScene(gl, camera, globalLight, textures.get("scene2"));
    scene1Model = scene1.getScene(gl);
    scene2Model = scene2.getScene(gl);
    
    sphere = makeSphere(gl, textures.get("metallic"), textures.get("metallic_specular"));
    robot1 = new Robot1(sphere);

    cube = makeCube(gl, textures.get("grey"), textures.get("metallic_specular"));
    sphere = makeSphere(gl, textures.get("grey"), textures.get("metallic_specular"));
    robot2 = new Robot2(gl, cube, sphere, spotlight);

    sphere1 = makeSphere(gl, textures.get("globe"), textures.get("globe"));
    sphere2 = makeSphere(gl, textures.get("grey"), textures.get("metallic_specular"));
    cube = makeCube(gl, textures.get("fun"), textures.get("surface"));
    globe  = new Globe(gl, cube, sphere1, sphere2);
  }

  private Model makeSphere(GL3 gl, Texture t1, Texture t2) {
    String name= "sphere";
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Model sphere = new Model(name, mesh, new Mat4(1), shader, material, globalLight, camera, t1, t2);
    return sphere;
  } 

  private Model makeCube(GL3 gl, Texture t1, Texture t2) {
    String name= "cube";
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Model cube = new Model(name, mesh, new Mat4(1), shader, material, globalLight, camera, t1, t2);
    return cube;
  } 

  public void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    room.render(gl);
    if (globalLightOn) {
      globalLight.setPosition(getLightPosition());
      globalLight.render(gl);
  }
    // switchOnRightWall.render(gl);
    sceneRender(gl);
    robot1.render(gl);
    robot2.render(gl);
    globe.render(gl);
    spotlight.render(gl);
    gl.glDisable(GL.GL_BLEND);
  }

  // The scene's texture changes by time 
  private void sceneRender(GL3 gl){
    double elapsedTime = getSeconds()-startTime;
    if (Math.sin(Math.toRadians(elapsedTime*25)) >= 0){
      for (Model model : scene1Model) {
        model.render(gl);
      }
    } else {
        for (Model model : scene2Model) {
          model.render(gl);
        }
    }
  }

  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 8.0f*(float)(Math.sin(Math.toRadians(elapsedTime*80)));
    float y = 7.4f;
    float z = 3.0f*(float)(Math.cos(Math.toRadians(elapsedTime*80)));
    return new Vec3(x,y,z);
  }

  private float calculateDistance(Vec3 position1, Vec3 position2) {
    float dx = position1.x - position2.x;
    float dy = position1.y - position2.y;
    float dz = position1.z - position2.z;
    return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
}


  // ***************************************************
  /* INTERACTION
   *
   *
   */

  // my own work
  private boolean globalLightOn = true;
  
  // public lightSwitch getSwitchOnRightWall() {
  //   return switchOnRightWall;
  // }
  
  // public void toggleGlobalLight() {
  //   globalLightOn = !globalLightOn;
  // }

  // public void spotlightSwitch(){
  //   if (spotlightStatus){
  //     spotlight.offSpotlight();
  //     spotlightStatus = false;
  //   } else {
  //     spotlight.onSpotlight();
  //     spotlightStatus = true;
  //   }
  // }

  // ***************************************************
  /* TIME
   */ 
  
   private double startTime;
  
   private double getSeconds() {
     return System.currentTimeMillis()/1000.0;
   }
  
} 