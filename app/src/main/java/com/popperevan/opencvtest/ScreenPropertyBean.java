package com.popperevan.opencvtest;

public class ScreenPropertyBean {
    private int width; // 屏幕宽度（像素）
    private int height; // 屏幕高度（像素）
    private float density;  // 屏幕密度（0.75 / 1.0 / 1.5）
    private int densityDpi; // 屏幕密度dpi（120 / 160 / 240）
    private int screenWidth; // 屏幕宽度(dp)
    private int screenHeight; // 屏幕高度(dp)

    public ScreenPropertyBean(int width, int height, float density, int densityDpi, int screenWidth, int screenHeight) {
        this.width = width;
        this.height = height;
        this.density = density;
        this.densityDpi = densityDpi;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public int getDensityDpi() {
        return densityDpi;
    }

    public void setDensityDpi(int densityDpi) {
        this.densityDpi = densityDpi;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }
}
