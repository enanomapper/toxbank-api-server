package org.toxbank.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

import junit.framework.Assert;
import net.toxbank.client.io.rdf.UserIO;
import net.toxbank.client.resource.User;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.opentox.dsl.task.RemoteTask;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.toxbank.resource.Resources;
import org.toxbank.rest.user.db.ReadUser;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class UserResourceTest extends ResourceTest {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		setUpDatabase(dbFile);
		
	}
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d%s", port,Resources.user);
	}
	
	@Test
	public void testURI() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_URI_LIST);
	}
	/**
	 * The URI should be /user/U*
	 */
	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = r.readLine())!= null) {
			Assert.assertTrue(line.startsWith(String.format("http://localhost:%d%s/U",port,Resources.user)));
			count++;
		}
		return count==3;
	}	
	
	
	@Test
	public void testRDF() throws Exception {
		testGet(String.format("http://localhost:%d%s/U1", port,Resources.user),MediaType.APPLICATION_RDF_XML);
	}
	
	@Override
	public OntModel verifyResponseRDFXML(String uri, MediaType media,
			InputStream in) throws Exception {
		
		OntModel model = ModelFactory.createOntologyModel();
		model.read(in,null);
		
		UserIO ioClass = new UserIO();
		List<User> users = ioClass.fromJena(model);
		Assert.assertEquals(1,users.size());
		Assert.assertEquals(String.format("http://localhost:%d%s/U1",port,Resources.user),
													users.get(0).getResourceURL().toString());
		Assert.assertEquals("Mr.", users.get(0).getTitle());
		Assert.assertEquals("http://example.com/blog", users.get(0).getWeblog().toString());
		Assert.assertEquals("http://mypage.com", users.get(0).getHomepage().toString());
		Assert.assertEquals("abcdef", users.get(0).getFirstname());
		Assert.assertEquals("ABCDEF", users.get(0).getLastname());
		return model;
	}	
	
	@Override
	public Object verifyResponseJavaObject(String uri, MediaType media,
			Representation rep) throws Exception {
		Object o = super.verifyResponseJavaObject(uri, media, rep);
		Assert.assertTrue(o instanceof ReadUser);

		return o;
	}
	
	@Test
	public void testCreateEntryFromWebForm() throws Exception {
		Form form = new Form();
		for (ReadUser.fields field : ReadUser.fields.values()) {
			switch (field) {
			case iduser: continue;
			default: {
				form.add(field.name(),field.name());
			}
			}
		}

        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM user");
		Assert.assertEquals(3,table.getRowCount());
		c.close();

		RemoteTask task = testAsyncPoll(new Reference(String.format("http://localhost:%d%s", port,
				Resources.user)),
				MediaType.TEXT_URI_LIST, form.getWebRepresentation(),
				Method.POST);
		//wait to complete
		while (!task.isDone()) {
			task.poll();
			Thread.sleep(100);
			Thread.yield();
		}
		Assert.assertTrue(task.getResult().toString().startsWith(String.format("http://localhost:%d/user/U",port)));

        c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM user");
		Assert.assertEquals(4,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT iduser,title from user where iduser>3 and username='username'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();

	}	
}