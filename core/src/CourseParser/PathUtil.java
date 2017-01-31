package CourseParser;


import java.io.File;

/**
 * Created by Dorian on 09-Apr-16.
 * MiniGolf
 */
public final class PathUtil {
    private PathUtil(){}
    public static String assetsPath(){
        final File file = new File(PathUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String path = file.getAbsolutePath();
        path = file.getAbsolutePath().substring(0,path.lastIndexOf("/"));
        path = file.getAbsolutePath().substring(0,path.lastIndexOf("/"));
        path = file.getAbsolutePath().substring(0,path.lastIndexOf("/"));
        path = file.getAbsolutePath().substring(0,path.lastIndexOf("/"));
        return path + "/";
    }
}




