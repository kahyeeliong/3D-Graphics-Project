import java.awt.*;
import java.awt.event.*;

import javax.swing.JFrame;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

/**
* I declare that this code is my own work
*
* @author   Liong Kah Yee, kyliong1@sheffield.ac.uk
* 
*/

public class Spacecraft extends JFrame {
  
  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
  private GLCanvas canvas;
  private GLEventListener glEventListener;
  private final FPSAnimator animator; 
  private Camera camera;

  public static void main(String[] args) {
    Spacecraft b1 = new Spacecraft("Spacecraft");
    b1.getContentPane().setPreferredSize(dimension);
    b1.pack();
    b1.setVisible(true);
  }

  public Spacecraft(String textForTitleBar) {
    super(textForTitleBar);
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    camera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    glEventListener = new Spacecraft_GLEventListener(camera);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(new MyMouseInput(camera));
    // canvas.addMouseListener(new MyMouseClickListener((Spacecraft_GLEventListener) glEventListener, camera));
    canvas.addKeyListener(new MyKeyboardInput(camera));
    getContentPane().add(canvas, BorderLayout.CENTER);
    
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        animator.stop();
        remove(canvas);
        dispose();
        System.exit(0);
      }
    });
    animator = new FPSAnimator(canvas, 60);
    animator.start();
    }
  }
  
  class MyKeyboardInput extends KeyAdapter  {
    private Camera camera;
    
    public MyKeyboardInput(Camera camera) {
      this.camera = camera;
    }
    
    public void keyPressed(KeyEvent e) {
      Camera.Movement m = Camera.Movement.NO_MOVEMENT;
      switch (e.getKeyCode()) {
        case KeyEvent.VK_LEFT:  m = Camera.Movement.LEFT;  break;
        case KeyEvent.VK_RIGHT: m = Camera.Movement.RIGHT; break;
        case KeyEvent.VK_UP:    m = Camera.Movement.UP;    break;
        case KeyEvent.VK_DOWN:  m = Camera.Movement.DOWN;  break;
        case KeyEvent.VK_A:  m = Camera.Movement.FORWARD;  break;
        case KeyEvent.VK_Z:  m = Camera.Movement.BACK;  break;
      }
      camera.keyboardInput(m);
    }
  }
  
  class MyMouseInput extends MouseMotionAdapter {
    private Point lastpoint;
    private Camera camera;
    
    public MyMouseInput(Camera camera) {
      this.camera = camera;
    }
    
      /**
     * mouse is used to control camera position
     *
     * @param e  instance of MouseEvent
     */    
    public void mouseDragged(MouseEvent e) {
      Point ms = e.getPoint();
      float sensitivity = 0.001f;
      float dx=(float) (ms.x-lastpoint.x)*sensitivity;
      float dy=(float) (ms.y-lastpoint.y)*sensitivity;
      //System.out.println("dy,dy: "+dx+","+dy);
      if (e.getModifiersEx()==MouseEvent.BUTTON1_DOWN_MASK)
        camera.updateYawPitch(dx, -dy);
      lastpoint = ms;
    }
  
    /**
     * mouse is used to control camera position
     *
     * @param e  instance of MouseEvent
     */  
    public void mouseMoved(MouseEvent e) {   
      lastpoint = e.getPoint(); 
    }
  }

  // class MyMouseClickListener extends MouseAdapter {
  //   private Spacecraft_GLEventListener eventListener;
  //   private Camera camera;

  //   public MyMouseClickListener(Spacecraft_GLEventListener eventListener, Camera camera) {
  //       this.eventListener = eventListener;
  //       this.camera = camera;
  //   }

  //   @Override
  //   public void mousePressed(MouseEvent e) {
  //       float mouseX = e.getX();
  //       float mouseY = e.getY();
  //       float normalizedMouseX = mouseX / WIDTH; 
  //       float normalizedMouseY = mouseY / HEIGHT; 

  //       if (eventListener.getSwitchOnRightWall().isClicked(normalizedMouseX, normalizedMouseY, camera)) {
  //         eventListener.getSwitchOnRightWall().toggle();
  //         eventListener.toggleGlobalLight();
  //     }
  //   }
// }

   