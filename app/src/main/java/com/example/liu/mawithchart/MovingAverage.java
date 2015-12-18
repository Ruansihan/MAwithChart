package com.example.liu.mawithchart;

/**
 * Created by liu on 2015/11/29.
 */
public class MovingAverage {
    private float movingWindow[];
    private float avg;
    private int curr;    // current position in the window
    private int count;

    public MovingAverage(int size) {
        movingWindow = new float[size];
        count = 0;
        curr = 0;
    }

    public float initWindow(float value) {
        for (int i=0; i<movingWindow.length; i++)
            movingWindow[i] = value;

        avg = value;

        return value;
    }

    public int nextPos(int curr) {
        if (curr < movingWindow.length-1) {
            return curr + 1;
        }

        // over the boundary, back to the head of the window
        return 0;
    }


    public float lowPass(float newValue) {

        if (count++ == 0) {
            initWindow(newValue);
            return newValue;
        }

        float replaced = movingWindow[curr];

        movingWindow[curr] = newValue;
        curr = nextPos(curr);

        return avg += (newValue-replaced)/movingWindow.length;
    }


//// only for testing the correctness

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//
//		float values[] = {10, 10, 60, 20, 20};
//
//		MovingAverage mv = new MovingAverage(5);
//
//		System.out.println("\n1st average: " + mv.lowPass(values[0]));
//		System.out.println("\n2nd average: " + mv.lowPass(values[1]));
//		System.out.println("\n3rd average: " + mv.lowPass(values[2]));
//		System.out.println("\n4th average: " + mv.lowPass(values[3]));
//		System.out.println("\n5th average: " + mv.lowPass(values[4]));
//
//
//	}
}
