package com.Jetvac2.MicroJam.Util;

public class Utils {

    public static float[] interpolateColor(float[] color, float[] desiredColor, float interpTime, float currentTime) {
        float[] colorDelta = new float[] {
            color[0] - desiredColor[0],
            color[1] - desiredColor[1],
            color[2] - desiredColor[2],
            color[3] - desiredColor[3]};
        float[] colorDeltaPerSecond = new float[] {
            colorDelta[0]/interpTime,
            colorDelta[1]/interpTime,
            colorDelta[2]/interpTime,
            colorDelta[3]/interpTime};
        return new float[] {
            color[0] - colorDeltaPerSecond[0] * currentTime,
            color[1] - colorDeltaPerSecond[1] * currentTime,
            color[2] - colorDeltaPerSecond[2] * currentTime,
            color[3] - colorDeltaPerSecond[3] * currentTime};
    }
}
