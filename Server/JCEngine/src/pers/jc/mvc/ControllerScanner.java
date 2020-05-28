package pers.jc.mvc;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ControllerScanner {
	private static String controllerPackage;
	private static File rootFile;
	private static ClassLoader classLoader;
	
    public static List<Class<?>> getControllerClasses(String controllerPackage) {
    	init(controllerPackage);
        List<Class<?>> allClasses = new ArrayList<Class<?>>();
        if (rootFile.getPath().endsWith(".jar")) {
        	findAllClassesInJar(rootFile, allClasses);
        } else {
        	findAllClassesInDirectory(rootFile, allClasses);
        }
        List<Class<?>> controllerClasses = new ArrayList<Class<?>>();
        for (Class<?> singleClass : allClasses) {
        	if (singleClass.getAnnotation(Controller.class) != null) {
        		controllerClasses.add(singleClass);
        	}
        }
        return controllerClasses;
    }
    
    private static void init(String controllerPackage) {
    	ControllerScanner.controllerPackage = controllerPackage;
		String rootPath = null;
		try {
			rootPath = ControllerHandler.class.getResource("/").getPath();
		} catch(Exception e) {
			rootPath = ControllerScanner.class
				.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.getPath();
		}
		rootFile = new File(rootPath);
		classLoader = ControllerScanner.class.getClassLoader();
	}
    
    private static void findAllClassesInJar(File file, List<Class<?>> list) {
    	try {
			JarInputStream jarInputStream = new JarInputStream(new FileInputStream(file));
			JarEntry jarEntry = null;
			while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
	            String className = jarEntry.getName();
	            if (className.endsWith(".class") && !className.contains("$")) {
	            	className = className.replace("/", ".").replace(".class", "");
	            	if (className.startsWith(controllerPackage)) {
	            		list.add(classLoader.loadClass(className));
	            	}
                }
	        }
			jarInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private static void findAllClassesInDirectory(File file, List<Class<?>> list) {
        if (file.isFile() 
    		&& file.getName().endsWith(".class") 
    		&& !file.getName().contains("$")) 
        {
            try {
                String className = file.getPath()
                		.substring(rootFile.getPath().length() + 1)
                		.replace('/', '.')
                		.replace('\\', '.')
                		.replace(".class", "");
                if (className.startsWith(controllerPackage)) {
                	list.add(classLoader.loadClass(className));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            File[] fs = file.listFiles();
            if (fs == null) {
            	return;
            }
            for (File f : fs) {
            	findAllClassesInDirectory(f, list);
            }
        }
    }
}
