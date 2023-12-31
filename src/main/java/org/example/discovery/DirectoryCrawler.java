package org.example.discovery;

import org.example.annotations.*;
import org.example.dependencies.DIEngine;
import org.example.dependencies.DependencyContainer;
import org.example.main.MainClass;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DirectoryCrawler {

    private List<Class> controllerClasses = new ArrayList<>();
    private List<Class> implementationClasses = new ArrayList<>();

    public void discover() throws Exception {
        File file = new File("src/main/java/org/example");
        if(file.exists() && file.isDirectory()){
            crawl(file);
            mapAnotationRoutes();
        }

    }

    private void mapAnotationRoutes() throws Exception {

        //ovde sacuvamo implementacije
        for(Class cl: implementationClasses){
            DependencyContainer.getInstance().setImplementation(cl);
        }

        //ovde inicijalizujemo kontrolere i sve njegove dependency-je, i takodje nasetujemo metode na rute
        for(Class cl: controllerClasses){

            //inicijalizujemo kontroler
            Object obj = DIEngine.getInstance().initializeController(cl);

            Method[] methods = cl.getDeclaredMethods();
            for(Method method: methods){
                //Vezi metodu za instancu njene klase
                DIEngine.getInstance().getMethodMap().put(method, obj);

                if(method.isAnnotationPresent(GET.class) && method.isAnnotationPresent(Path.class)){
                    Path pathInfo = method.getAnnotation(Path.class);
                    if(!MainClass.getInstance().getRouteMap().containsKey("GET@" + pathInfo.value())) MainClass.getInstance().getRouteMap().put("GET@" + pathInfo.value(), method);
                    else System.err.println("Route already exists!");
                }
                else if(method.isAnnotationPresent(POST.class) && method.isAnnotationPresent(Path.class)){
                    Path pathInfo = method.getAnnotation(Path.class);
                    if(!MainClass.getInstance().getRouteMap().containsKey("GET@" + pathInfo.value())) MainClass.getInstance().getRouteMap().put("GET@" + pathInfo.value(), method);
                    else System.err.println("Route already exists!");
                }
                else{
                    System.err.println("Annotation is missing!");
                }

            }

        }
    }

    private void crawl(File f) throws ClassNotFoundException {
        if (f.isDirectory()) {
            File[] subFiles = f.listFiles();
            for (int i = 0; i < subFiles.length; i++) {
                crawl(subFiles[i]);
            }
        }
        else{
            String path = f.getPath().replace('\\','.').replace("src.main.java.", "").replace(".java","");
            Class cl = Class.forName(path);

            if(!cl.isAnnotation() && cl.isAnnotationPresent(Controller.class)){
                controllerClasses.add(cl);
            }
            if(!cl.isAnnotation() && cl.isAnnotationPresent(Qualifier.class)){
                implementationClasses.add(cl);
            }
        }
    }
}
