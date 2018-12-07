package dk.aau.cs.ds302e18.app.controllers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JUnitControllerTest {

    @Test
    public void testHomeController() {
        HomeController homeController = new HomeController();
        String result = homeController.home();
        assertEquals(result, "Hello World!");
    }
}
