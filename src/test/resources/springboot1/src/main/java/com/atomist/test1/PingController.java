/*
    Generated by Atomist on 12 07 2016 
*/
package com.atomist.test1;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @RequestMapping(path = "/ping")
    public String ping() {
       return "pong";
    }
}