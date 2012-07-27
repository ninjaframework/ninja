package ninja.postoffice.commonsmail;

import com.google.inject.ImplementedBy;
import ninja.postoffice.Mail;
import ninja.postoffice.common.Tuple;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import javax.mail.internet.AddressException;
import java.util.Collection;
import java.util.List;

@ImplementedBy(CommonsmailHelperImpl.class)
public interface CommonsmailHelper {

    public void doPopulateMultipartMailWithContent(MultiPartEmail multiPartEmail, Mail mail)
            throws AddressException, EmailException;

    /**
     * Creates a MultiPartEmail. Selects the correct implementation
     * regarding html (MultiPartEmail) and/or txt content or both.
     * 
     * Populates the mutlipart email accordingly with the txt / html content.
     */
    public MultiPartEmail createMultiPartEmailWithContent(Mail mail) throws EmailException;

    public void doSetServerParameter(MultiPartEmail multiPartEmail, String smtpHost,
            Integer smtpPort, Boolean smtpSsl, String smtpUser, String smtpPassword,
            Boolean smtpDebug);

    public List<Tuple<String, String>> createListOfAddresses(Collection<String> emails)
            throws AddressException;

    public Tuple<String, String> createValidEmailFromString(String email) throws AddressException;

}
