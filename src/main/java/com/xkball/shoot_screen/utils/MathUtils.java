package com.xkball.shoot_screen.utils;

import org.joml.Matrix3f;
import org.joml.Vector2f;

//Some code adapted from https://github.com/zomb-676/HologramPanel/blob/3f90903e293c103bb7c8e6d4e31819b140340b43/src/main/kotlin/com/github/zomb_676/hologrampanel/util/MousePositionManager.kt under GPLv3.0
public class MathUtils {
    
    public static Vector2f centerPoint(Vector2f leftDown, Vector2f rightDown, Vector2f rightUp, Vector2f leftUp){
        float[][] a = new float[][]{
                {rightUp.x- leftDown.x, rightDown.x- leftUp.x},
                {rightUp.y- leftDown.y, rightDown.y- leftUp.y},
        };
        float[] b = new float[]{rightDown.x - leftDown.x, rightDown.y- leftDown.y};
        float[] p = resolveLinearSystem(a,b);
        return new Vector2f(leftDown.x + p[0] * (rightUp.x - leftDown.x),leftDown.y + p[0] * (rightUp.y - leftDown.y));
    }
    
    /**
     * @param from point list 2*4
     * @param to point list 2*4
     * @return a homography matrix, col major.
     */
    @SuppressWarnings("DuplicatedCode")
    public static Matrix3f resolveHomography(float[] from, float[] to) {
        var x0 = from[0];
        var y0 = from[1];
        var x1 = from[2];
        var y1 = from[3];
        var x2 = from[4];
        var y2 = from[5];
        var x3 = from[6];
        var y3 = from[7];
        var u0 = to[0];
        var v0 = to[1];
        var u1 = to[2];
        var v1 = to[3];
        var u2 = to[4];
        var v2 = to[5];
        var u3 = to[6];
        var v3 = to[7];
        float[][] a = new float[][]{
                {x0,y0,1,0, 0, 0,-x0*u0,-y0*u0},
                {0, 0, 0,x0,y0,1,-x0*v0,-y0*v0},
                {x1,y1,1,0, 0, 0,-x1*u1,-y1*u1},
                {0, 0, 0,x1,y1,1,-x1*v1,-y1*v1},
                {x2,y2,1,0, 0, 0,-x2*u2,-y2*u2},
                {0, 0, 0,x2,y2,1,-x2*v2,-y2*v2},
                {x3,y3,1,0, 0, 0,-x3*u3,-y3*u3},
                {0, 0, 0,x3,y3,1,-x3*v3,-y3*v3}
        };
        float[] b = new float[]{
                u0,v0,u1,v1,u2,v2,u3,v3
        };
        float[] x = resolveLinearSystem(a,b);
        return new Matrix3f(
                x[0],x[3],x[6],
                x[1],x[4],x[7],
                x[2],x[5],1);
    }
    
    /**
     * resolve Ax=B
     * @param a coefficient vector
     * @param b constant vector
     * @return x
     */
    public static float[] resolveLinearSystem(float[][] a, float[] b){
        var n = b.length;
        float[][] aug = new float[n][n + 1];
        
        //增广矩阵
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, aug[i], 0, n);
            aug[i][n] = b[i];
        }
        
        for (int i = 0; i < n; i++) {
            //列主元选取（列主消元）
            int maxRow = i;
            for (int k = i; k < n; k++) {
                if (Math.abs(aug[k][i]) > Math.abs(aug[maxRow][i])) {
                    maxRow = k;
                }
            }
            
            //交换当前行和最大主元行
            float[] temp = aug[i];
            aug[i] = aug[maxRow];
            aug[maxRow] = temp;
            
            //主元不能为零
            float pivot = aug[i][i];
            if (Math.abs(pivot) <= 1e-6f) {
                throw new IllegalArgumentException("Matrix is singular, check if points are collinear");
            }
            
            //当前行归一化
            for (int j = i; j <= n; j++) {
                aug[i][j] /= pivot;
            }
            
            for (int k = i + 1; k < n; k++) {
                float factor = aug[k][i];
                for (int j = i; j <= n; j++) {
                    aug[k][j] -= factor * aug[i][j];
                }
            }
        }
        
        //回代求解结果
        float[] x = new float[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = aug[i][n];
            for (int j = i + 1; j < n; j++) {
                x[i] -= aug[i][j] * x[j];
            }
        }
        
        return x;
    }
}
