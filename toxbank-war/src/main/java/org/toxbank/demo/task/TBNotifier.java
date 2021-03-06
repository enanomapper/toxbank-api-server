package org.toxbank.demo.task;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.idea.restnet.aa.opensso.OpenSSOServicesConfig;

import org.opentox.aa.exception.AAException;
import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class TBNotifier implements Callable<String> {
	protected OpenSSOToken ssoToken;
	private final Logger logger ;
	
	public TBNotifier() {
		super();
		logger = Logger.getLogger(getClass().getName());
	}
	private Properties loadProperties() throws Exception {
   		InputStream in = null;
		try {
			Properties properties = new Properties();
			in = this.getClass().getClassLoader().getResourceAsStream("org/toxbank/rest/config/toxbank.properties");
			properties.load(in);
			return properties;
		} catch (Exception x) {
			logger.log(Level.SEVERE, "Error reading toxbank.properties", x);
			throw x;	
		} finally {
			try {in.close(); } catch (Exception xx) {}
		}
	}	
	protected void login() throws ResourceException, AAException, Exception {
		if (ssoToken == null) 
			ssoToken = new OpenSSOToken(OpenSSOServicesConfig.getInstance().getOpenSSOService());
		try {
			if (ssoToken.isTokenValid()) return; 
		} catch (Exception x) {
			logger.log(Level.INFO, "Invalid token, will login a new.");
		}
		Properties properties = loadProperties();
		String username=properties.getProperty("toxbank.aa.user");
		String pass=properties.getProperty("toxbank.aa.pass");
		if (!ssoToken.login(username,pass)) 
			throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN, "Notifier: error loggin in!");
		logger.log(Level.INFO, "Login attempt successful "+ username);		
	}
	/**
	 * Sends POST to the /notification resource
	 * @throws ResourceException
	 * @throws AAException
	 * @throws Exception
	 */
	public String call() throws Exception {
		
		login();
		ClientResource cr = null;
		Representation repr = null;
		try {
			Form form = new Form();
			form.add("search", "");
			
			cr = new ClientResource("riap://component/notification");
			Form headers = (Form) cr.getRequest().getAttributes().get("org.restlet.http.headers");
			if (headers == null) {
			    headers = new Form();
			    cr.getRequest().getAttributes().put("org.restlet.http.headers", headers);
			}
			headers.add("subjectid", ssoToken.getToken());
			logger.log(Level.INFO, ssoToken.getToken());
			repr = cr.post(form.getWebRepresentation(),MediaType.TEXT_URI_LIST);
			return repr.getText();
		} catch (ResourceException x) {
			if (Status.CLIENT_ERROR_NOT_FOUND.equals(x.getStatus())) {
				logger.log(Level.INFO, "No active alerts found");
				return x.getStatus().toString();
			}
			else throw x;
		} catch (Exception x) {
			throw x;
		} finally {
			try { repr.release(); } catch (Exception x) {}
			try {cr.release();} catch (Exception x) {}
		}
	}	
}
