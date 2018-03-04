

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class BoardProcess {
    // public final static int SOCKET_PORT = 13267;      // you may change this
    // final static String SERVER = "127.0.0.1";  // localhost
    //
    //ServerSocket serverSocket;
    //  Socket socket;
    public static void main(String args[]) throws IOException, ClassNotFoundException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // Declare the output variables
        
        
        
        String default_file = "/Users/islamamin/Documents/StarterHacks/finalDest.jpg";
        // Load an image
        Mat src = Imgcodecs.imread(default_file, Imgcodecs.IMREAD_COLOR);
        // Check if image is loaded fine
        
        if( src.empty() ) {
            System.out.println("Error opening image!");
            System.out.println("Program Arguments: [image_name -- default "
                               + default_file +"] \n");
            System.exit(-1);
        }
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(default_file));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Mat gray = new Mat();
        Mat result = src;
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.medianBlur(gray, gray, 5);
        Mat circles = new Mat();
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0,
                             (double)gray.rows()/30, // change this value to detect circles with different distances to each other
                             100.0, 30.0, 10, 100); // change the last two parameters
        // (min_radius & max_radius) to detect larger circles
        
        
        
        
        int [] x_c_array = new int[circles.cols()];
        int [] y_c_array = new int[circles.cols()];
        int N = 19;
        int [][] board_matrix = new int [N][N];
        for (int x = 0; x < circles.cols(); x++) {
            
            double[] c = circles.get(0, x);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            // circle center
            Imgproc.circle(result, center, 1, new Scalar(0,0,255), 3, 8, 0);
            //p[x] = center;
            // circle outline
            int radius = (int) Math.round(c[2]);
            System.out.println("Radius: "  + radius);
            Imgproc.circle(result, center, radius, new Scalar(0,0,255), 3, 8, 0);
            
            int x_c = (int) Math.round(c[0]);
            int y_c = (int) Math.round(c[1]);
            x_c_array[x] = x_c;
            y_c_array[x] = y_c;
            
            
            System.out.println("x: " + x_c + "  y: " + y_c);
            Color co = new Color(img.getRGB(x_c, y_c));
            String color = "";
            if(co.getBlue() < 100) {
                System.out.println("Black");
                color = "Black";
                
            }else {
                System.out.println("White");
                color = "White";
            }
            color = "x: " + x_c + "  y: " + y_c;
            //Imgproc.putText(result, color, center, Core.FONT_ITALIC, 2,  new Scalar(0,0,0));
            
        }
        
        int lowest_x = 1000000;
        int second_lowest_x = 100000;
        int highest_x = -1;
        int second_highest_x = -1;
        int [] index = new int[4];
        for(int i=0; i < x_c_array.length; i++) {
            if(x_c_array[i]>highest_x) {
                second_highest_x = highest_x;
                highest_x=x_c_array[i];
            }
            else if(x_c_array[i]>second_highest_x) {
                second_highest_x = x_c_array[i];
            }
            else if(x_c_array[i]<lowest_x) {
                second_lowest_x = lowest_x;
                lowest_x = x_c_array[i];
                System.out.println("New lowest: " + lowest_x);
                System.out.println("New slowest: " + second_lowest_x);
            }
            else if(x_c_array[i]<second_lowest_x) {
                second_lowest_x = x_c_array[i];
                System.out.println("slowest: " + second_lowest_x);
            }
        }
        
        int [] corner_x_values = {lowest_x, second_lowest_x, second_highest_x, highest_x};
        for(int i = 0; i < 4; i++) {
            System.out.println(corner_x_values[i]);
        }
        int [] corner_y_values = new int[4];
        for(int i=0; i < x_c_array.length; i++) {
            for(int j=0; j < 4; j++) {
                if(corner_x_values[j]==x_c_array[i]) {
                    corner_y_values[j] = y_c_array[i];
                    //System.out.println(x_c_array[i]);
                }
            }
        }
        
        if(distance(corner_x_values[0],corner_y_values[0],corner_x_values[2],corner_y_values[2]) >  distance(corner_x_values[0],corner_y_values[0],corner_x_values[3],corner_y_values[3]) ) {
            int tmp_x = corner_x_values[2];
            int tmp_y = corner_y_values[2];
            corner_x_values[2] = corner_x_values[3];
            corner_y_values[2] = corner_y_values[3];
            corner_x_values[3] = tmp_x;
            corner_y_values[3] = tmp_y;
            System.out.println("I'm here");
        }
        
        for(int i = 0; i < 4; i++) {
            System.out.println(corner_y_values[i]);
        }
        
        int x_distance_1 = (corner_x_values[2]-corner_x_values[0]);
        int x_distance_2 = (corner_x_values[1]-corner_x_values[0]);
        int y_distance_1 = (corner_y_values[2]-corner_y_values[0]);
        int y_distance_2 = (corner_y_values[1]-corner_y_values[0]);
        
        int [][] board_output = new int[N][N];
        double [][][] board_coordinates = new double[N][N][2];
        for(double i=0; i<N; i++) {
            for(double j=0; j<N; j++) {
                board_coordinates[(int)i][(int)j][0] = corner_x_values[0] + ( x_distance_1 * (i/(N-1)) + x_distance_2 * (j/(N-1))  );
                board_coordinates[(int)i][(int)j][1] = corner_y_values[0] + ( y_distance_1 * (i/(N-1)) + y_distance_2 * (j/(N-1)) );
                Imgproc.circle(result, new Point(board_coordinates[(int)i][(int)j][0],board_coordinates[(int)i][(int)j][1]), 1, new Scalar(0,0,255), 3, 8, 0);
                System.out.println("i: " + i + " x: " + board_coordinates[(int)i][(int)j][0] + " y: " + board_coordinates[(int)i][(int)j][1]);
            }
        }
        
        
        for(int x=0;  x < circles.cols(); x++) {
            int min = 1000000;
            int x_index = -1;
            int y_index = -1;
            int xx = -1;
            for(int i=0; i<N; i++) {
                for(int j=0; j<N; j++) {
                    double dist = distance(board_coordinates[i][j][0],board_coordinates[i][j][1],x_c_array[x],y_c_array[x]);
                    
                    if(dist < min) {
                        min = (int) dist;
                        x_index = i;
                        y_index = j;
                        xx = x;
                    }
                }
            }
            System.out.println(min);
            //Color co = new Color(img.getRGB((int)board_coordinates[x_index][y_index][0], (int)board_coordinates[x_index][y_index][1]));
            Color co = new Color(img.getRGB(x_c_array[xx], y_c_array[xx]));
            if(co.getBlue() < 100) {
                board_output[x_index][y_index] = 1;
            }else{
                board_output[x_index][y_index] = -1;
            }
            
        }
        board_output[0][0] = 0;
        board_output[0][N-1] = 0;
        board_output[N-1][0] = 0;
        board_output[N-1][N-1] = 0;
        
        int [][] x_abc = new int [N][N];
        for(int i=0; i<N; i++) {
            for(int j=0; j<N; j++) {
                x_abc[i][j] = (int)board_coordinates[i][j][0];
            }
        }
        
        
        //print_2D(x_abc,N);
        print_2D(board_output,N);
        write2D(board_output,N);
        Imgcodecs.imwrite("/Users/islamamin/Documents/result5.jpg", result);
        System.exit(0);
    }
    
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }
    
    
    
    public int min_diff(int a, int nums[]) {
        int diff = Math.abs(nums[0] - a);
        int min_index = 0;
        for(int i = 0; i < nums.length; i++) {
            if(Math.abs(nums[i] - a) < diff ) {
                diff = Math.abs(nums[i]-a);
                min_index = i;
            }
        }
        return min_index;
    }
    
    
    public static void print_2D(int p[][], int N) {
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                System.out.print(p[j][i] + " ");
            }
            System.out.println();
        }
    }
    
    public static void write2D(int p[][], int N) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("/Users/islamamin/Documents/StarterHacks/matrix.txt", "UTF-8");
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(j != N - 1) {
                    writer.print(p[j][i] + ",");
                }else {
                    writer.print(p[j][i]);
                }
                
            }
            writer.println();
        }
        writer.close();
    }
    
}

