import javax.tools.*;
import java.io.*;

class eg3psp
{
public static void main(String args[])
{
JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.out.println("Error: JDK required.");
            return;
        }

        // Define files to verify paths
        File jarFile = new File("../../../../../../lib/gson.jar");
        File classesDir = new File("../../../../../../classes/");
        File sourceFile = new File("eg2.java");

        // Print path verification to the console
        System.out.println("--- Path Verification ---");
        System.out.println("gson.jar exists: " + jarFile.exists() + " (" + jarFile.getAbsolutePath() + ")");
        System.out.println("classes/ exists: " + classesDir.exists() + " (" + classesDir.getAbsolutePath() + ")");
        System.out.println("eg2.java exists: " + sourceFile.exists() + " (" + sourceFile.getAbsolutePath() + ")");
        System.out.println("-------------------------\n");

        String sep = System.getProperty("path.separator");
        String classpath = jarFile.getPath() + sep + classesDir.getPath() + sep + ".";

        String[] params = new String[]{
            "-cp", classpath,
            sourceFile.getPath()
        };

        System.out.println("Running compiler...");
        // Any syntax errors inside eg2.java will print automatically to the console here
        int result = compiler.run(null, null, null, params);

        if (result == 0) {
            System.out.println("\nResult: compiled success");
        } else {
            System.out.println("\nResult: Unable to compile. Check the compiler error log above.");
        }
}
}
