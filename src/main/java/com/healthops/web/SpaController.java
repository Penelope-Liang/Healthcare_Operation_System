package com.healthops.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The Vue app uses HTML5 history routing, so a refresh or deep link on a client-side route
 * must still return index.html. Static assets and /api/** are served normally.
 */
@Controller
public class SpaController {

    @GetMapping({"/patients", "/vaccinations", "/vaccines", "/clinics", "/staff"})
    public String app() {
        return "forward:/index.html";
    }
}
