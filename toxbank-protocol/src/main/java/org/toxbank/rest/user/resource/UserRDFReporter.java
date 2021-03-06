package org.toxbank.rest.user.resource;

import java.net.URL;

import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.idea.modbcum.p.MasterDetailsProcessor;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.QueryRDFReporter;
import net.toxbank.client.Resources;
import net.toxbank.client.io.rdf.TOXBANK;
import net.toxbank.client.io.rdf.UserIO;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.toxbank.rest.groups.DBOrganisation;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.groups.db.ReadOrganisation;
import org.toxbank.rest.groups.db.ReadProject;
import org.toxbank.rest.groups.resource.GroupQueryURIReporter;
import org.toxbank.rest.user.DBUser;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.XSD;

/**
 * Generates RDF representation of query results for users
 * @author nina
 *
 * @param <Q>
 */
public class UserRDFReporter<Q extends IQueryRetrieval<DBUser>> extends QueryRDFReporter<DBUser, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8857789530109166243L;
	protected UserIO ioClass = new UserIO();
	protected GroupQueryURIReporter groupURIReporter ;
	
	public UserRDFReporter(Request request,MediaType mediaType,ResourceDoc doc) {
		super(request,mediaType,doc);
		groupURIReporter = new GroupQueryURIReporter(request);
		getProcessors().clear();
		IQueryRetrieval<DBOrganisation> queryO = new ReadOrganisation(new DBOrganisation()); 
		MasterDetailsProcessor<DBUser,DBOrganisation,IQueryCondition> orgReader = new MasterDetailsProcessor<DBUser,DBOrganisation,IQueryCondition>(queryO) {
			private static final long serialVersionUID = 6058051886584582349L;

			@Override
			protected DBUser processDetail(DBUser target, DBOrganisation detail)
					throws Exception {
				detail.setResourceURL(new URL(groupURIReporter.getURI(detail)));
				target.addOrganisation(detail);
				return target;
			}
		};
		IQueryRetrieval<DBProject> queryP = new ReadProject(new DBProject()); 
		MasterDetailsProcessor<DBUser,DBProject,IQueryCondition> projectReader = new MasterDetailsProcessor<DBUser,DBProject,IQueryCondition>(queryP) {
			private static final long serialVersionUID = -4012021031885627727L;

			@Override
			protected DBUser processDetail(DBUser target, DBProject detail)
					throws Exception {
				detail.setResourceURL(new URL(groupURIReporter.getURI(detail)));
				target.addProject(detail);
				return target;
			}
		};
		getProcessors().add(orgReader);
		getProcessors().add(projectReader);
		processors.add(new DefaultAmbitProcessor<DBUser,DBUser>() {
			private static final long serialVersionUID = 7565709317843220408L;

			public DBUser process(DBUser target) throws AmbitException {
				processItem(target);
				return target;
			};
		});		
	}
	
	@Override
	protected QueryURIReporter createURIReporter(Request reference,ResourceDoc doc) {
		return new UserURIReporter(reference);
	}
	@Override
	public void setOutput(Model output) throws AmbitException {
		this.output = output;
		if (output!=null) {
			output.setNsPrefix("tb", TOXBANK.URI);
			output.setNsPrefix("dcterms", DCTerms.getURI());
			output.setNsPrefix("xsd", XSD.getURI());
			output.setNsPrefix("foaf", FOAF.NS);
			output.setNsPrefix("tbpt", String.format("%s%s/",uriReporter.getBaseReference().toString(),Resources.project));
			output.setNsPrefix("tbo", String.format("%s%s/",uriReporter.getBaseReference().toString(),Resources.organisation));
			output.setNsPrefix("tbu", String.format("%s%s/",uriReporter.getBaseReference().toString(),Resources.user));
			
		}
	}
	@Override
	public Object processItem(DBUser item) throws AmbitException {
		try {
			item.setResourceURL(new URL(uriReporter.getURI(item)));
			ioClass.objectToJena(
				getJenaModel(), // create a new class
				item
			);
			return item;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	
	public void open() throws DbAmbitException {
		
	}

}
