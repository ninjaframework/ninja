package ninja.postoffice;

/**
 * Simply takes a Mail and sends it.
 * 
 * Can be used for instance with the implementation that uses
 * Apache CommonsMail. Or the Mockmailer. Or your own implementation.
 * 
 * @author rbauer
 * 
 */
public interface Postoffice {

    void send(Mail mail) throws Exception;

}
