package me.drton.jmavsim;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
//import com.sun.j3d.utils.behaviors.mouse.MouseRotate;

//import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
//import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import java.io.IOException;
import java.net.URL;

/**
 * Abstract kinematic object class.
 * Stores all kinematic parameters (attitude, attitude rates, position, velocity, acceleration) but doesn't calculate it.
 * These parameters may be set directly for objects moving by fixed trajectory or simulated from external forces (see DynamicObject).
 */
public abstract class KinematicObject extends WorldObject {
    protected boolean ignoreGravity = false;
    protected boolean ignoreWind = false;
    protected volatile Vector3d position = new Vector3d();
    protected volatile Vector3d velocity = new Vector3d();
    protected volatile Vector3d acceleration = new Vector3d();
    protected volatile Matrix3d rotation = new Matrix3d();
    protected volatile Vector3d rotationRate = new Vector3d();
    protected volatile Vector3d attitude = new Vector3d();

    protected Transform3D transform;
    protected TransformGroup transformGroup;
    protected BranchGroup branchGroup;

    public KinematicObject(World world) {
        super(world);
        rotation.setIdentity();
        transformGroup = new TransformGroup();
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transform = new Transform3D();
        transformGroup.setTransform(transform);
        branchGroup = new BranchGroup();
        branchGroup.addChild(transformGroup);
    }

    /**
     * Helper method to create model from .obj file.
     *
     * @param modelFile file name
     * @throws java.io.FileNotFoundException
     */
    protected void modelFromFile(String modelFile) {
        URL file = null;
        try {
            file = new URL("file:./" + modelFile);
        
            ObjectFile objectFile = new ObjectFile();
            Scene scene = objectFile.load(file);

//          Shape3D shape = (Shape3D)bg.getChild(0);
//          shape.setPickable(true);
//          TransformGroup objRotate = new TransformGroup();
//          objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
//          objRotate.addChild(bg);
//          MouseRotate f1=new MouseRotate();
//          f1.setSchedulingBounds(new BoundingSphere());
//          f1.setTransformGroup(objRotate);
//          bg.addChild(f1);
//          transformGroup.addChild(objRotate);

            BranchGroup bg = scene.getSceneGroup();
            transformGroup.addChild(bg);
            
        } catch (IOException | IncorrectFormatException | ParsingErrorException e) {
            System.out.println("ERROR: could not load 3D model: " + modelFile);
            System.out.println("Error message:" + e.getLocalizedMessage());
        }
        
    }

    public BranchGroup getBranchGroup() {
        return branchGroup;
    }

    public void updateBranchGroup() {
        transform.setTranslation(position);
        transform.setRotationScale(rotation);
        transformGroup.setTransform(transform);
    }

    public void setIgnoreGravity(boolean ignoreGravity) {
        this.ignoreGravity = ignoreGravity;
    }

    public boolean isIgnoreWind() {
        return ignoreWind;
    }

    public void setIgnoreWind(boolean ignoreWind) {
        this.ignoreWind = ignoreWind;
    }

    public Vector3d getPosition() {
        return (Vector3d) position.clone();
    }

    public void setPosition(Vector3d position) {
        this.position = position;
    }

    public Vector3d getVelocity() {
        return (Vector3d) velocity.clone();
    }

    public void setVelocity(Vector3d vel) {
        this.velocity = vel;
    }

    public Vector3d getAcceleration() {
        return (Vector3d) acceleration.clone();
    }

    public void setAcceleration(Vector3d acc) {
        this.acceleration = acc;
    }

    public Matrix3d getRotation() {
        return (Matrix3d) rotation.clone();
    }

    public void setRotation(Matrix3d rotation) {
        this.rotation = rotation;
    }

    public Vector3d getRotationRate() {
        return (Vector3d) rotationRate.clone();
    }

    public void setRotationRate(Vector3d rate) {
        this.rotationRate = rate;
    }

    public void resetObjectParameters() {
        position = new Vector3d();
        velocity = new Vector3d();
        acceleration = new Vector3d();
        rotation = new Matrix3d();
        rotationRate = new Vector3d();
        
        rotation.rotX(0);
    }
    
    public static Vector3d utilMatrixToEulers(Matrix3d m) {
        Vector3d tv = new Vector3d();
        tv.y = Math.atan2(m.m21, m.m22);
        tv.z = Math.atan2(m.m10, m.m00);
        if (Math.abs(Math.cos(tv.y)) > 0.002)
            tv.x = Math.atan2(-m.m20, Math.sqrt(m.m21 * m.m21 + m.m22 * m.m22));
        return tv;
    }
}
