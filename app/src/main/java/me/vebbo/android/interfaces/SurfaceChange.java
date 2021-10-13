package me.vebbo.android.interfaces;

import androidx.camera.core.ImageProxy;

public interface SurfaceChange{
    void onSurfaceChanged(ImageProxy img, int rotationDeg, boolean isBack);
}