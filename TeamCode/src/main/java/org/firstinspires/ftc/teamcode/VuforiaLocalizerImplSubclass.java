package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.RobotLog;
import com.vuforia.Frame;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.State;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.internal.VuforiaLocalizerImpl;

/**
 * Created by robotadmin on 1/24/17.
 */

public class VuforiaLocalizerImplSubclass extends VuforiaLocalizerImpl {

    //public CloseableFrame daFrame = new CloseableFrame(new Frame());
    public Image rgb;

    public class CloseableFrame extends Frame{
        public CloseableFrame(Frame other){
            super(other);
        }

        public void close(){
            super.delete();
        }
    }

    public class VuforiaCallbackSubClass extends VuforiaLocalizerImpl.VuforiaCallback {
        @Override public synchronized void Vuforia_onUpdate(State state){
            super.Vuforia_onUpdate(state);


            CloseableFrame frame = new CloseableFrame(state.getFrame());
            //daFrame = frame;
            long numImgs = frame.getNumImages();

            for(int i = 0; i < numImgs; i++){
                if(frame.getImage(i).getFormat() == PIXEL_FORMAT.RGB565){
                    rgb = frame.getImage(i);
                }
            }
            //RobotLog.vv(TAG, "recieved Vuforia frame#=%d", frame.getIndex());
            frame.close();
        }
    }

    public VuforiaLocalizerImplSubclass(VuforiaLocalizer.Parameters params){
        super(params);
        stopAR();
        clearGLSurface();

        this.vuforiaCallback = new VuforiaCallbackSubClass();
        startAR();

        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);
    }

    public void clearGLSurface(){
        if(this.glSurfaceParent != null){
            appUtil.synchronousRunOnUiThread(new Runnable() {
                @Override
                public void run() {
                    glSurfaceParent.removeAllViews();
                    glSurfaceParent.getOverlay().clear();
                    glSurface = null;
                }
            });
        }
    }
}
