package org.toxbank.rest.protocol.db.test;

import java.net.URL;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.toxbank.client.resource.Template;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.db.template.UpdateDataTemplate;

public class DataTemplate_crud_test  extends CRUDTest<Object,DBProtocol> {
	String template = "http://localhost/protocol/"+DBProtocol._PREFIX+"-Protocol-1-1/dataTemplate";
	
	@Override
	protected IQueryUpdate<Object,DBProtocol> createQuery() throws Exception {
		return null;
	}

	@Override
	protected void createVerify(IQueryUpdate<Object,DBProtocol> query)
			throws Exception {

	}

	@Override
	public void testCreate() throws Exception {

	}
	@Override
	public void testDelete() throws Exception {
		//TODO Not iplemented
	}

	
	@Override
	protected IQueryUpdate<Object,DBProtocol> updateQuery() throws Exception {
		DBProtocol protocol = new DBProtocol(1,1);
		protocol.setDataTemplate(new Template(new URL(template)));
		return new UpdateDataTemplate(protocol);
	}

	@Override
	protected void updateVerify(IQueryUpdate<Object,DBProtocol> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT idprotocol,template as t from protocol where idprotocol=1"));
		
		Assert.assertEquals(1,table.getRowCount());
		//dbunit is confused ...
		//Assert.assertEquals("ABCDEFGH",table.getValue(0,"t"));
		c.close();
	}

	@Override
	protected IQueryUpdate<Object, DBProtocol> createQueryNew()
			throws Exception {

		return null;
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<Object, DBProtocol> query)
			throws Exception {
		
		
	}
	@Override
	public void testCreateNew() throws Exception {
	}

	@Override
	protected IQueryUpdate<Object, DBProtocol> deleteQuery() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void deleteVerify(IQueryUpdate<Object, DBProtocol> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
