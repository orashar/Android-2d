package com.bennyplo.graphics2d;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.view.View;

/**
 * Created by benlo on 09/05/2018.
 */

public class MyView extends View {
    private Paint redPaint, bluePaint, greenPaint, blackPaint, gradientPaint;
    private Path myLines;
    private LinearGradient linear;

    Point[] points, testPoints;

    Point center = new Point(550, 350);
    Point[] newPoint = new Point[4];

    private Path lineGraph;
    private int viewWidth, viewHeight;

    public MyView(Context context) {
        super(context, null);

        viewWidth = getResources().getDisplayMetrics().widthPixels;
        viewHeight = getResources().getDisplayMetrics().heightPixels - 70;
        //Add your initialisation code here
        redPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint.setStyle(Paint.Style.FILL);//stroke only no fill
        redPaint.setColor(0xffff0000);//color red
        redPaint.setStrokeWidth(5);//set the line stroke width to 5

        bluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bluePaint.setStyle(Paint.Style.STROKE);//stroke only no fill
        bluePaint.setARGB(255, 0, 0, 255);//color blue

        blackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blackPaint.setStyle(Paint.Style.STROKE);//stroke only no fill
        blackPaint.setARGB(255, 0, 0, 0);//color blue
        blackPaint.setStrokeWidth(10);

        greenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        greenPaint.setARGB(255, 0, 255, 0);//color blue

        myLines = new Path();

        points = new Point[5];
        points[0] = new Point(50, 300);
        points[1] = new Point(150, 400);
        points[2] = new Point(180, 340);
        points[3] = new Point(240, 420);
        points[4] = new Point(300, 200);

        myLines.moveTo(points[0].x, points[0].y);
        myLines.lineTo(points[1].x, points[1].y);
        myLines.lineTo(points[2].x, points[2].y);
        myLines.lineTo(points[3].x, points[3].y);
        myLines.lineTo(points[4].x, points[4].y);/*
        myLines.lineTo(x5, y5);
        myLines.lineTo(x6, y6);*/
        myLines.close();

        linear = new LinearGradient(points[0].x, points[0].y, points[4].x, points[4].y, Color.BLUE, Color.RED, Shader.TileMode.MIRROR);

        gradientPaint = new Paint();
        gradientPaint.setStyle(Paint.Style.FILL);
        gradientPaint.setShader(linear);

        testPoints = new Point[4];
        testPoints[0] = new Point(500, 300);
        testPoints[1] = new Point(500, 400);
        testPoints[2] = new Point(600, 400);
        testPoints[3] = new Point(600,  300);

        newPoint = new Point[4];
        newPoint[0] = new Point(500, 300);
        newPoint[1] = new Point(500, 400);
        newPoint[2] = new Point(600, 400);
        newPoint[3] = new Point(600,  300);

        double[] plotData = new double[51];
        for(int i = 0; i < 51; i++){
            plotData[i] = 10 * Math.sin(i);
        }

        lineGraph = createLineGraph(plotData, viewWidth, viewHeight);
    }

    private void updatePath(Point[] newPoints){
        myLines.reset();
        myLines.moveTo(newPoints[0].x, newPoints[0].y);
        for(int i = 1; i < newPoints.length; i++){
            myLines.lineTo(newPoints[i].x, newPoints[i].y);
        }
        myLines.close();
    }

    private Point[] affineTransformation(Point[] vertices, double[][] matrix){
        Point[] result = new Point[vertices.length];
        for(int i = 0; i < vertices.length; i++){
            int t = (int) (matrix[0][0] * vertices[i].x + matrix[0][1] * vertices[i].y + matrix[0][2]);
            int u = (int) (matrix[1][0] * vertices[i].x + matrix[1][1] * vertices[i].y + matrix[1][2]);
            result[i] = new Point(t, u);
        }
        return result;
    }

    private Point[] translate(Point[] input, double px, double py){
        double[][] matrix = new double[3][3];
        matrix[0][0] = 1; matrix[0][1] = 0; matrix[0][2] = px;
        matrix[1][0] = 0; matrix[1][1] = 1; matrix[1][2] = py;
        matrix[2][1] = 0; matrix[2][0] = 0; matrix[2][2] = 1;

        return affineTransformation(input, matrix);
    }

    private Point[] scale(Point[] input, double sx, double sy){
        double[][] matrix = new double[3][3];
        matrix[0][0] = sx; matrix[0][1] = 0; matrix[0][2] = 0;
        matrix[1][0] = 0; matrix[1][1] = sy; matrix[1][2] = 0;
        matrix[2][1] = 0; matrix[2][0] = 0; matrix[2][2] = 1;

        return affineTransformation(input, matrix);
    }
    private Point[] shear(Point[] input, int shx, int shy){
        double[][] matrix = new double[3][3];
        matrix[0][0] = 1; matrix[0][1] = shx; matrix[0][2] = 0;
        matrix[1][0] = shy; matrix[1][1] = 1; matrix[1][2] = 0;
        matrix[2][1] = 0; matrix[2][0] = 0; matrix[2][2] = 1;

        return affineTransformation(input, matrix);
    }

    private Point[] rotate(Point[] input, double theta){
        double[][] matrix = new double[3][3];
        matrix[0][0] = Math.cos(Math.toRadians(theta)); matrix[0][1] = -Math.sin(Math.toRadians(theta)); matrix[0][2] = 0;
        matrix[1][0] = Math.sin(Math.toRadians(theta)); matrix[1][1] = Math.cos(Math.toRadians(theta)); matrix[1][2] = 0;
        matrix[2][1] = 0; matrix[2][0] = 0; matrix[2][2] = 1;

        return affineTransformation(input, matrix);
    }

    private Path createLineGraph(double[] input, int width, int height){
        Point[] ptArray = new Point[input.length];
        double minValue = 999999, maxValue = -999999;
        for(int i = 0; i < input.length; i++){
            ptArray[i] = new Point(i, (int) input[i]);
            minValue = Math.min(minValue, input[i]);
            maxValue = Math.max(maxValue, input[i]);
        }

        ptArray = translate(ptArray, 0, -minValue);
        double sy = height/(double)(maxValue - minValue), sx = width/(double)(input.length);
        ptArray = scale(ptArray, sx, sy);
        Path result = new Path();
        result.moveTo(ptArray[0].x, ptArray[0].y);
        for(int i = 0; i < ptArray.length; i++){
            result.lineTo(ptArray[i].x, ptArray[i].y);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Add your drawing code here
        /*canvas.drawRect(10,30,200,200,redPaint);
        canvas.drawCircle(300, 300, 250, bluePaint);
        canvas.drawPath(myLines, blackPaint);*/

        canvas.drawPath(myLines, gradientPaint);
        Point[] newPoints = shear(points, 2, 0);
       newPoints = scale(newPoints, 0.5, 3);
        newPoints = rotate(newPoints, 45);
         newPoints = translate(newPoints, 550, 0);


        updatePath(newPoints);
        canvas.drawPath(myLines, gradientPaint);
        /*updatePath(testPoints);
        for(int i = 0; i < 4; i++){
            newPoint[i].x = testPoints[i].x - center.x;
            newPoint[i].y = testPoints[i].y - center.y;
        }
        newPoint = rotate(newPoint, 45);
        for(int i = 0; i < 4; i++){
            newPoint[i].x = newPoint[i].x + center.x;
            newPoint[i].y = newPoint[i].y + center.y;
        }
        updatePath(newPoint);

        linear = new LinearGradient(newPoint[0].x, newPoint[0].y, newPoint[3].x, newPoint[3].y, Color.BLUE, Color.RED, Shader.TileMode.MIRROR);

        gradientPaint = new Paint();
        gradientPaint.setStyle(Paint.Style.FILL);
        gradientPaint.setShader(linear);

        canvas.drawPath(myLines, gradientPaint);*/

        /*canvas.drawPath(lineGraph, blackPaint);*/
    }
}
