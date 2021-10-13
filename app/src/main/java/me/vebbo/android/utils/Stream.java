package me.vebbo.android.utils;

import android.content.Context;
import android.graphics.Matrix;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.util.Rational;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraX;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.SensorOrientedMeteringPointFactory;

import org.json.JSONException;
import org.json.JSONObject;

import me.vebbo.android.App;
import me.vebbo.android.interfaces.SurfaceChange;

import static androidx.camera.core.CameraX.bindToLifecycle;
import static androidx.camera.core.CameraX.unbindAll;
import static me.vebbo.android.activities.HomeActivity.isFacingBack;
import static me.vebbo.android.activities.HomeActivity.isLive;
import static me.vebbo.android.activities.HomeActivity.mShouldContinue;
import static me.vebbo.android.utils.Constant.AUDIO;
import static me.vebbo.android.utils.Constant.CAMERA;
import static me.vebbo.android.utils.Constant.DATA;
import static me.vebbo.android.utils.Constant.ENV;
import static me.vebbo.android.utils.Constant.IS_BACK;
import static me.vebbo.android.utils.Constant.PLATFORM;
import static me.vebbo.android.utils.Constant.ROTATION;
import static me.vebbo.android.utils.Translation.NV21toJPEG;
import static me.vebbo.android.utils.Translation.YUV420toNV21;
import static me.vebbo.android.utils.Translation.compressor;

public class Stream implements SurfaceChange {

    private App app;
    private AppCompatActivity context;

    //======================= Camera ============================
    public static Preview preview = null;
    protected SurfaceChange surfaceChange;
    protected static final int QUALITY = 50;

    //======================= Audio =============================
    protected static final int RATE = 44100;
    protected AudioRecord audioRecord = null;

    public static AudioTrack audioTrack = null;
    protected byte[] buffer = new byte[RATE];
    protected int deviceCallVol;
    protected AudioManager audioManager;
    protected static AutomaticGainControl agc;
    protected static NoiseSuppressor suppressor;
    protected final int bufferSize = AudioRecord.getMinBufferSize(RATE,
            AudioFormat.CHANNEL_IN_DEFAULT,
            AudioFormat.ENCODING_PCM_16BIT);

    public Stream(AppCompatActivity context) {
        this.context = context;
        app = (App) context.getApplication();
        surfaceChange = this;
    }

    public void startCamera(TextureView mCameraView) {
        try {
            unbindAll();

            Rational aspectRatio = new Rational(mCameraView.getWidth(), mCameraView.getHeight());
            Size screen = new Size(mCameraView.getWidth(), mCameraView.getHeight());
            PreviewConfig pConfig;
            ImageAnalysisConfig iAC;
            MeteringPointFactory factory;
            boolean[] isBack = new boolean[1];
            isBack[0] = false;

            CameraControl cameraControl = CameraX.getCameraControl(CameraX.LensFacing.BACK);
            factory = new SensorOrientedMeteringPointFactory(1.0f, 1.0f);

            mCameraView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() != MotionEvent.ACTION_UP) {
                        return true;
                    }

                    cameraControl.cancelFocusAndMetering();

                    MeteringPoint point = factory.createPoint(motionEvent.getX(), motionEvent.getY());
                    FocusMeteringAction action = FocusMeteringAction.Builder.from(point).build();
                    cameraControl.startFocusAndMetering(action);

                    return true;
                }
            });

            if (isFacingBack) {
                isBack[0] = isFacingBack;
                pConfig = new PreviewConfig.Builder().setLensFacing(CameraX.LensFacing.BACK)
                        .setTargetAspectRatio(aspectRatio).setTargetResolution(screen).build();
                preview = new Preview(pConfig);
                iAC = new ImageAnalysisConfig.Builder()
                        .setLensFacing(CameraX.LensFacing.BACK)
                        .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                        .build();
            } else {
                isBack[0] = isFacingBack;
                pConfig = new PreviewConfig.Builder().setLensFacing(CameraX.LensFacing.FRONT)
                        .setTargetAspectRatio(aspectRatio).setTargetResolution(screen).build();
                preview = new Preview(pConfig);
                iAC = new ImageAnalysisConfig.Builder()
                        .setLensFacing(CameraX.LensFacing.FRONT)
                        .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                        .build();
            }
            preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
                @Override
                public void onUpdated(Preview.PreviewOutput output) {
                    ViewGroup parent = (ViewGroup) mCameraView.getParent();
                    parent.removeView(mCameraView);
                    parent.addView(mCameraView, 0);
                    mCameraView.setSurfaceTexture(output.getSurfaceTexture());
                    updateTransform(mCameraView);
                }
            });

            MeteringPoint point = factory.createPoint(mCameraView.getWidth(), mCameraView.getHeight());
            FocusMeteringAction action = FocusMeteringAction.Builder.from(point).build();
            cameraControl.startFocusAndMetering(action);

            ImageAnalysis imageAnalyzer =
                    new ImageAnalysis(iAC);

            imageAnalyzer.setAnalyzer(
                    new ImageAnalysis.Analyzer() {
                        @Override
                        public void analyze(ImageProxy image, int rotationDegrees) {
                            if (isLive) {
                                surfaceChange.onSurfaceChanged(image, rotationDegrees, isBack[0]);
                            }
                        }
                    });

            bindToLifecycle(context, imageAnalyzer, preview);
        } catch (CameraInfoUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void openStream() {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        deviceCallVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, RATE,
                AudioFormat.CHANNEL_OUT_DEFAULT,
                MediaRecorder.OutputFormat.MPEG_4,
                bufferSize, AudioTrack.MODE_STREAM);

        audioTrack.setPlaybackRate(RATE);
        audioTrack.play();
    }

    public void startStream() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        //============================== Starting ===========================================
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, RATE,
                AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize * 10);

        buffer = new byte[bufferSize];
        audioRecord.startRecording();

        suppressor = NoiseSuppressor.create(audioRecord.getAudioSessionId());
        agc = AutomaticGainControl.create(audioRecord.getAudioSessionId());

        Thread audio_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mShouldContinue) {
                    try {
                        audioRecord.read(buffer, 0, bufferSize);
                        //byte[] compressed = compressor(buffer);

                        //audioTrack.write(buffer, 0, buffer.length);
                        JSONObject obj = new JSONObject();
                        obj.put(DATA, ((Object) buffer));
                        obj.put(PLATFORM, ENV);
                        app.getSocketStream().emit(AUDIO, obj);
                    } catch (JSONException js) {
                        js.printStackTrace();
                    }
                }
            }
        });
        audio_thread.start();
    }

    protected void updateTransform(TextureView mCameraView) {
        Matrix mx = new Matrix();
        float w = mCameraView.getMeasuredWidth();
        float h = mCameraView.getMeasuredHeight();
        float cX = w / 2f;
        float cY = h / 2f;
        int rotationDgr;
        int rotation = (int) mCameraView.getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }
        mx.postRotate((float) rotationDgr, cX, cY);
        mCameraView.setTransform(mx);
    }

    @Override
    public void onSurfaceChanged(ImageProxy img, int rotationDeg, boolean isBack) {
        try {
            if (img.getImage() != null) {
                byte[] original = NV21toJPEG(YUV420toNV21(img.getImage()), img.getImage().getWidth(), img.getImage().getHeight(), QUALITY);
                JSONObject obj = new JSONObject();
                /*BitmapFactory.Options bitmap_options = new BitmapFactory.Options();
                bitmap_options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bm = BitmapFactory.decodeByteArray(original, 0, original.length, bitmap_options);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bm1 = resizeBitmap(bm, img.getImage().getWidth(), img.getImage().getHeight());
                bm1.compress(Bitmap.CompressFormat.JPEG, QUALITY, baos);
                byte[] data = compressor(baos.toByteArray()); */

                //byte[] data = compressor(original);
                obj.put(DATA, original);
                obj.put(PLATFORM, ENV);
                obj.put(IS_BACK, isBack);
                obj.put(ROTATION, rotationDeg);
                app.getSocketStream().emit(CAMERA, obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeStream() {
        if (audioManager != null) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, deviceCallVol, 0);
        }
        if (audioTrack != null) {
            audioTrack.release();
        }
    }

    public void stopStream() {
        mShouldContinue = false;
        if (agc != null) {
            agc.release();
        }
        if (suppressor != null) {
            suppressor.release();
        }
        if (audioRecord != null) {
            audioRecord.release();
        }
    }
}
