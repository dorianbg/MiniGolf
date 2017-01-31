package CourseParser;

import java.io.*;

/**
 * Created by Dorian on 13-Mar-16.
 * MiniGolfExecutor
 */
public class CourseWriter {
    private static String location = PathUtil.assetsPath();
    private File file;
    private FileWriter fileWriter;

    public static void write(String fileName, boolean dontRewrite,String gameObstacleType, String modelType,
                        float xDim, float yDim, float zDim,float xPos, float yPos, float zPos){
        try(PrintWriter fileWriter = new PrintWriter(new FileWriter(new File(location + fileName + ".txt"),dontRewrite))){
                fileWriter.print(gameObstacleType);
                fileWriter.append(" ");
                fileWriter.print(modelType);
                fileWriter.append(" ");

                fileWriter.print(xDim);
                fileWriter.append(" ");
                fileWriter.print(yDim);
                fileWriter.append(" ");
                fileWriter.print(zDim);
                fileWriter.append(" ");

                fileWriter.print(xPos);
                fileWriter.append(" ");
                fileWriter.print(yPos);
                fileWriter.append(" ");
                fileWriter.print(zPos);
                fileWriter.append(" ");

                fileWriter.println();

                fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void write(String fileName, boolean dontRewrite,String gameObstacleType, String modelType,
                             float xDim, float yDim, float zDim,
                             float xPos, float yPos, float zPos,
                             float xRot, float yRot, float zRot, float degRotated){
        try(PrintWriter fileWriter = new PrintWriter(new FileWriter(new File(location + fileName + ".txt"),dontRewrite))){
            fileWriter.print(gameObstacleType);
            fileWriter.append(" ");
            fileWriter.print(modelType);
            fileWriter.append(" ");

            fileWriter.print(xDim);
            fileWriter.append(" ");
            fileWriter.print(yDim);
            fileWriter.append(" ");
            fileWriter.print(zDim);
            fileWriter.append(" ");

            fileWriter.print(xPos);
            fileWriter.append(" ");
            fileWriter.print(yPos);
            fileWriter.append(" ");
            fileWriter.print(zPos);
            fileWriter.append(" ");

            fileWriter.print(xRot);
            fileWriter.append(" ");
            fileWriter.print(yRot);
            fileWriter.append(" ");
            fileWriter.print(zRot);
            fileWriter.append(" ");
            fileWriter.print(degRotated);
            fileWriter.append(" ");

            fileWriter.println();

            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

