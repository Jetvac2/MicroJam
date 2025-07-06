package com.Jetvac2.MicroJam.Enemies;

public class DistData {
    public float dis;
    public int index;

    public DistData(float dis, int index) {
        this.dis = dis;
        this.index = index;
    }


    public int compare(DistData b) {
       return (int)Math.signum(dis - b.dis);
    }
}
