import gmaths.*;
import com.jogamp.opengl.*; 

/**
* I declare that this code is my own work
*
* @author   Liong Kah Yee, kyliong1@sheffield.ac.uk
* 
*/

public class Robot1 {
    
    private Model sphere;
    private boolean isDancing = false;

    private SGNode robot1Root;
    private TransformNode translateX, translateZ, rotateAll, rotateUpper, rotateBody, rotateLeftArm, rotateRightArm, rotateLeftArmZ, rotateRightArmZ, scaleHead, rotateHeadPoint;
    private float xPosition = -3.0f;
    private float zPosition = -3.0f;
    private float rotateAllAngleStart = 25, rotateAllAngle = rotateAllAngleStart;
    private float rotateUpperAngleStart = -60, rotateUpperAngle = rotateUpperAngleStart; 
    private float rotateLeftArmAngleStart = 120, rotateLeftArmAngle = rotateLeftArmAngleStart;
    private float rotateRightArmAngleStart = -120, rotateRightArmAngle = rotateRightArmAngleStart; 
    private float rotateBodyAngleStart = 40, rotateBodyAngle = rotateBodyAngleStart; 
    private float rotateLeftArmAngleZStart = 50, rotateLeftArmAngleZ = rotateLeftArmAngleZStart;
    private float rotateRightArmAngleZStart = 50, rotateRightArmAngleZ = rotateRightArmAngleZStart;
    private float scaleFactor;
    private float rotateHeadPointAngleStart = -30, rotateHeadPointAngle = rotateHeadPointAngleStart;

    private float baseHeight = 0.1f;
    private float baseWidth = 2.5f;
    private float upperLegHeight = 1.3f;
    private float upperLegWidth = 0.4f;
    private float lowerLegHeight = 1.3f;
    private float lowerLegWidth = 0.4f;
    private float bodyHeight = 2.0f;
    private float bodyWidth = 0.4f;
    private float leftArmHeight = 1.5f;
    private float leftArmWidth = 0.4f;
    private float rightArmHeight = 1.5f;
    private float rightArmWidth = 0.4f;
    private float headHeight = 0.4f;
    private float headWidth = 2.0f;
    private float headPointHeight = 1.0f;
    private float headPointWidth = 0.2f;
    private float leftEyesSize = 0.3f;
    private float rightEyesSize = 0.3f;

    public Robot1(Model sphere){
        this.sphere = sphere;
        robot1Root = new NameNode("robot1 structure");
        setup();
    }

    public void setDancing(boolean dancing) {
        this.isDancing = dancing;
    }

    public float getXPosition() {
        return xPosition;
    }
    
    public float getZPosition() {
        return zPosition;
    }

    private SGNode makeBase(){
        NameNode baseName = new NameNode("base");
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(baseWidth, baseHeight, baseWidth));
        m =  Mat4.multiply(m, Mat4Transform.translate(0, 0, 0));
        TransformNode base = new TransformNode("scale(2.5, 0.1, 2.5); translate(0, 0, 0)", m);
        ModelNode sphereNode = new ModelNode("Sphere(0)", sphere);
        baseName.addChild(base);
            base.addChild(sphereNode);
        return baseName;
    }

    private SGNode makeLowerLeg(){
        NameNode lowerLegName = new NameNode("lower leg branch");
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(lowerLegWidth, lowerLegHeight, lowerLegWidth));
        m =  Mat4.multiply(m, Mat4Transform.translate(0, 0.5f,0));
        TransformNode lowerLeg = new TransformNode("scale(0.4, 1.3, 0.4); translate(0, 0.5f, 0)", m);
        ModelNode sphereNode = new ModelNode("Sphere(1)", sphere);
        lowerLegName.addChild(lowerLeg);
            lowerLeg.addChild(sphereNode);
        return lowerLegName;
    }

    private SGNode makeUpperLeg(){
        NameNode upperLegName = new NameNode("upper leg branch");
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(upperLegWidth, upperLegHeight, upperLegWidth));
        m =  Mat4.multiply(m, Mat4Transform.translate(0, 0.5f,0));
        TransformNode upperLeg = new TransformNode("scale(0.4, 1.3, 0.4); translate(0, 0.5f, 0)", m);
        ModelNode sphereNode = new ModelNode("Sphere(2)", sphere);
        upperLegName.addChild(upperLeg);
            upperLeg.addChild(sphereNode);
        return upperLegName;
    }

    private SGNode makeBody(){
        NameNode bodyName = new NameNode("body branch");
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(bodyWidth, bodyHeight, bodyWidth));
        m =  Mat4.multiply(m, Mat4Transform.translate(0, 0.5f,0));
        TransformNode body = new TransformNode("scale(0.4, 2.0, 0.4); translate(0, 0.5f, 0)", m);
        ModelNode sphereNode = new ModelNode("Sphere(3)", sphere);
        bodyName.addChild(body);
        body.addChild(sphereNode);
        return bodyName;
    }

    private SGNode makeLeftArm(){
        NameNode leftArmName = new NameNode("left arm branch");
        rotateLeftArm = new TransformNode("rotateAroundZ(" + rotateLeftArmAngle + ")", Mat4Transform.rotateAroundZ(rotateLeftArmAngle));
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(leftArmWidth, leftArmHeight, leftArmWidth));
        m =  Mat4.multiply(m, Mat4Transform.translate(0.5f, 0.5f,0f));
        TransformNode leftArm = new TransformNode("scale(0.4, 1.5, 0.4); translate(-(leftArmWidth*0.5f) + 3.0f, 0.2f,0)", m);
        ModelNode sphereNode = new ModelNode("Sphere(4)", sphere);
        leftArmName.addChild(rotateLeftArm);
        rotateLeftArm.addChild(leftArm);
        leftArm.addChild(sphereNode);
        return leftArmName;
    }

    private SGNode makeRightArm(){
        NameNode rightArmName = new NameNode("right arm branch");
        rotateRightArm = new TransformNode("rotateAroundZ(" + rotateRightArmAngle + ")", Mat4Transform.rotateAroundZ(rotateRightArmAngle));
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(rightArmWidth, rightArmHeight, rightArmWidth));
        m =  Mat4.multiply(m, Mat4Transform.translate(-0.5f, 0.5f,0));
        TransformNode rightArm = new TransformNode("scale(0.4, 1.5, 0.4); translate((rightArmWidth*0.5f) - 3.0f, 0.2f, 0)", m);
        ModelNode sphereNode = new ModelNode("Sphere(5)", sphere);
        rightArmName.addChild(rotateRightArm); 
        rotateRightArm.addChild(rightArm); 
        rightArm.addChild(sphereNode);
        return rightArmName;
    }

    private SGNode makeHead(){
        NameNode headName = new NameNode("head branch");
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(headWidth, headHeight, 0.4f));
        m =  Mat4.multiply(m, Mat4Transform.translate(0, 0.5f,0));
        TransformNode head = new TransformNode("scale(0.4, 2.0, 0.4); translate(0, 0.5f, 0)", m);
        ModelNode sphereNode = new ModelNode("Sphere(6)", sphere);
        headName.addChild(head); 
        head.addChild(sphereNode);
        return headName;
    }

    private SGNode makeLeftEyes(){
        NameNode leftEyesName = new NameNode("left eyes branch");
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(leftEyesSize, leftEyesSize, leftEyesSize));
        m =  Mat4.multiply(m, Mat4Transform.translate(-0.8f, 0.8f,0.4f));
        TransformNode leftEyes = new TransformNode("scale(0.3, 0.3, 0.3); translate(-0.8f, 0.8f,0.4f)", m);
        ModelNode sphereNode = new ModelNode("Sphere(7)", sphere);
        leftEyesName.addChild(leftEyes); 
        leftEyes.addChild(sphereNode);
        return leftEyesName;
    }

    private SGNode makeRightEyes(){
        NameNode rightEyesName = new NameNode("righ eyes branch");
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(rightEyesSize, rightEyesSize, rightEyesSize));
        m =  Mat4.multiply(m, Mat4Transform.translate(0.8f, 0.8f,0.4f));
        TransformNode rightEyes = new TransformNode("scale(0.3, 0.3, 0.3); translate(0.8f, 0.8f,0.4f)", m);
        ModelNode sphereNode = new ModelNode("Sphere(8)", sphere);
        rightEyesName.addChild(rightEyes); 
        rightEyes.addChild(sphereNode);
        return rightEyesName;
    }

    private SGNode makeHeadPoint(){
        NameNode headPointName = new NameNode("righ eyes branch");
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(headPointWidth, headPointHeight, headPointWidth));
        m =  Mat4.multiply(m, Mat4Transform.translate(0, 0.5f,0));
        TransformNode headPoint = new TransformNode("scale(0.2, 1.0, 0.2); translate(0, 0.5f,0)", m);
        ModelNode sphereNode = new ModelNode("Sphere(9)", sphere);
        headPointName.addChild(headPoint); 
        headPoint.addChild(sphereNode);
        return headPointName;
    }

    private void setup() {
        SGNode baseBranch = makeBase();
        SGNode lowerLegBranch = makeLowerLeg();
        SGNode upperLegBranch = makeUpperLeg();
        SGNode bodyBranch = makeBody();
        SGNode leftArmBranch = makeLeftArm();
        SGNode rightArmBranch = makeRightArm();
        SGNode headBranch = makeHead();
        SGNode leftEyesBranch = makeLeftEyes();
        SGNode rightEyesBranch = makeRightEyes();
        SGNode headPointBranch = makeHeadPoint();

        TransformNode translateToTop = new TransformNode("translate(0,"+ lowerLegHeight+",0)",
            Mat4Transform.translate(0,lowerLegHeight,0));

        TransformNode translateToBody = new TransformNode("translate(0,"+ upperLegHeight+",0)",
            Mat4Transform.translate(0,upperLegHeight,0));

        TransformNode translateToLeftBody = new TransformNode("translate(0,"+ upperLegHeight+",0)",
            Mat4Transform.translate(0,upperLegHeight,0));

        TransformNode translateToRightBody = new TransformNode("translate(0,"+ upperLegHeight+",0)",
            Mat4Transform.translate(0,upperLegHeight,0));
        
        TransformNode translateToHead = new TransformNode("translate(0,"+ bodyHeight+",0)",
            Mat4Transform.translate(0,bodyHeight,0));
        
        TransformNode translateToHeadPoint = new TransformNode("translate(0,"+ headHeight+",0)",
            Mat4Transform.translate(0,headHeight,0));
    
        translateX = new TransformNode("translate("+xPosition+",0,0)", Mat4Transform.translate(xPosition,0,0)); 
        translateZ = new TransformNode("translate("+zPosition+",0,0)", Mat4Transform.translate(0,0,zPosition)); 
        rotateAll = new TransformNode("rotateAroundZ("+rotateAllAngle+")", Mat4Transform.rotateAroundZ(rotateAllAngle));
        rotateUpper = new TransformNode("rotateAroundZ("+rotateUpperAngle+")",Mat4Transform.rotateAroundZ(rotateUpperAngle));
        rotateBody = new TransformNode("rotateAroundZ("+rotateBodyAngle+")",Mat4Transform.rotateAroundZ(rotateBodyAngle));
        rotateLeftArmZ = new TransformNode("rotateAroundZ("+rotateLeftArmAngleZ+")",Mat4Transform.rotateAroundZ(rotateLeftArmAngleZ));
        rotateRightArmZ = new TransformNode("rotateAroundZ("+rotateRightArmAngleZ+")",Mat4Transform.rotateAroundZ(rotateRightArmAngleZ));     
        scaleHead = new TransformNode ("scale head", Mat4Transform.scale(1, 1, 1));
        rotateHeadPoint =  new TransformNode("rotateAroundZ("+rotateHeadPointAngle+")",Mat4Transform.rotateAroundZ(rotateHeadPointAngle));     

        robot1Root.addChild(translateX);
            translateX.addChild(translateZ);
            translateZ.addChild(baseBranch);
            translateZ.addChild(rotateAll);
            rotateAll.addChild(lowerLegBranch);
                lowerLegBranch.addChild(translateToTop);
                translateToTop.addChild(rotateUpper);
                rotateUpper.addChild(upperLegBranch);
                upperLegBranch.addChild(translateToBody);
                translateToBody.addChild(rotateBody);
                rotateBody.addChild(bodyBranch);
                    bodyBranch.addChild(translateToLeftBody);
                    translateToLeftBody.addChild(rotateLeftArmZ);
                    rotateLeftArmZ.addChild(leftArmBranch);
                    bodyBranch.addChild(translateToRightBody);
                    translateToRightBody.addChild(rotateRightArmZ);
                    rotateRightArmZ.addChild(rightArmBranch);
                    bodyBranch.addChild(translateToHead);
                    translateToHead.addChild(scaleHead);
                    scaleHead.addChild(headBranch);
                    headBranch.addChild(leftEyesBranch);
                    headBranch.addChild(rightEyesBranch);
                    headBranch.addChild(translateToHeadPoint);
                    translateToHeadPoint.addChild(rotateHeadPoint);
                    rotateHeadPoint.addChild(headPointBranch);

        robot1Root.update();
    }

    public void updateBranches() {
        if (isDancing) {
            double elapsedTime = getSeconds() - startTime * 1.5;
            rotateAllAngle = rotateAllAngleStart * (float) Math.sin(elapsedTime);
            rotateUpperAngle = rotateUpperAngleStart * (float) Math.sin(elapsedTime * 0.7f);
            rotateBodyAngle = rotateBodyAngleStart * (float) Math.sin(elapsedTime * 0.7f);
            rotateLeftArmAngleZ = rotateLeftArmAngleZStart * (float) Math.sin(elapsedTime * 0.7f);
            rotateRightArmAngleZ = rotateRightArmAngleZStart * (float) Math.sin(elapsedTime * 0.7f);
            rotateHeadPointAngle = rotateHeadPointAngleStart * (float) Math.sin(elapsedTime * 0.4f);
            rotateLeftArmAngle = rotateLeftArmAngleStart;
            rotateRightArmAngle = rotateRightArmAngleStart;
            scaleFactor = 1.0f + 0.2f * (float) Math.sin(getSeconds());
            rotateAll.setTransform(Mat4Transform.rotateAroundZ(rotateAllAngle));
            rotateUpper.setTransform(Mat4Transform.rotateAroundZ(rotateUpperAngle));
            rotateBody.setTransform(Mat4Transform.rotateAroundZ(rotateBodyAngle));
            rotateLeftArmZ.setTransform(Mat4Transform.rotateAroundZ(rotateLeftArmAngleZ));
            rotateRightArmZ.setTransform(Mat4Transform.rotateAroundZ(rotateRightArmAngleZ));
            scaleHead.setTransform(Mat4Transform.scale(scaleFactor, scaleFactor, scaleFactor));
            rotateHeadPoint.setTransform(Mat4Transform.rotateAroundZ(rotateHeadPointAngle));
        }
        robot1Root.update(); 
    }
    

    private double startTime;

    private double getSeconds() {
        return System.currentTimeMillis()/1000.0;
      }

    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        sphere.dispose(gl);
      }

    public void render(GL3 gl) {
        updateBranches();
        robot1Root.draw(gl);
      }
}