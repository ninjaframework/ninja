package controllers;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ninja.NinjaFluentLeniumTest;

public class AuthenticityFormTest extends NinjaFluentLeniumTest {
    
    @Test
    public void testAuthenticitySuccess() {
        goTo("/authenticate");
        assertTrue(pageSource().contains("Login"));
        
        $("#login").click();
        
        assertTrue(pageSource().contains("only visible with valid authenticationToken"));
    }
    
    @Test
    public void testAuthenticityFails() {
        goTo("/notauthenticate");
        assertTrue(pageSource().contains("Login"));
        
        $("#login").click();
        
        assertTrue(!pageSource().contains("only visible with valid authenticationToken"));
    }
}