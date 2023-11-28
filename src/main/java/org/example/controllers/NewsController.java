package org.example.controllers;

import org.example.annotations.Controller;
import org.example.annotations.GET;
import org.example.annotations.Path;

@Controller
public class NewsController extends MyAbstractController{

    @GET
    @Path("/news")
    public int allNews(){
        return 0;
    }
}
