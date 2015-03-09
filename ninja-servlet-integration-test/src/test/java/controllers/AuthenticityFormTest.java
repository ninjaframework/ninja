package controllers;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ninja.NinjaFluentLeniumTest;

public class AuthenticityFormTest extends NinjaFluentLeniumTest {
    
    @Test
    public void testAuthenticitySuccess() {
        goTo(getServerAddress() + "/authenticate");
        assertTrue(pageSource().contains("Login"));
        
        click("#login");
        
        assertTrue(pageSource().contains("only visible with valid authenticationToken"));
    }
    
    @Test
    public void testAuthenticityFails() {
        goTo(getServerAddress() + "/notauthenticate");
        assertTrue(pageSource().contains("Login"));
        
        click("#login");
        
        assertTrue(!pageSource().contains("only visible with valid authenticationToken"));
    }
}