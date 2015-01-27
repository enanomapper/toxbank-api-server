package org.toxbank.rest.protocol.resource.db;

import java.io.Writer;
import java.net.URL;
import java.util.List;

import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.idea.modbcum.p.MasterDetailsProcessor;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;
import org.toxbank.rest.groups.DBOrganisation;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.groups.IDBGroup;
import org.toxbank.rest.groups.resource.GroupQueryURIReporter;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.projects.db.ReadProjectMembership;
import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.author.db.ReadAuthor;
import org.toxbank.rest.user.resource.UserURIReporter;

import ambit2.base.json.JSONUtils;

public class ProtocolJSONReporter extends
		QueryReporter<DBProtocol, IQueryRetrieval<DBProtocol>, Writer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3537339785122677311L;
	protected String comma = null;
	protected QueryURIReporter uriReporter;

	public QueryURIReporter getUriReporter() {
		return uriReporter;
	}

	protected GroupQueryURIReporter<IQueryRetrieval<IDBGroup>> groupReporter;
	protected UserURIReporter<IQueryRetrieval<DBUser>> userReporter;

	public ProtocolJSONReporter(Request request) {
		super();
		uriReporter = new ProtocolQueryURIReporter<IQueryRetrieval<DBProtocol>>(
				request);

		groupReporter = new GroupQueryURIReporter<IQueryRetrieval<IDBGroup>>(
				request);

		userReporter = new UserURIReporter<IQueryRetrieval<DBUser>>(request);
		getProcessors().clear();
		IQueryRetrieval<DBUser> queryA = new ReadAuthor(null, null);
		MasterDetailsProcessor<DBProtocol, DBUser, IQueryCondition> authorsReader = new MasterDetailsProcessor<DBProtocol, DBUser, IQueryCondition>(
				queryA) {
			@Override
			protected DBProtocol processDetail(DBProtocol target, DBUser detail)
					throws Exception {

				detail.setResourceURL(new URL(userReporter.getURI(detail)));
				target.addAuthor(detail);
				return target;
			}
		};
		getProcessors().add(authorsReader);

		IQueryRetrieval<DBProject> queryP = new ReadProjectMembership(null,
				new DBProject());
		MasterDetailsProcessor<DBProtocol, DBProject, IQueryCondition> projectsReader = new MasterDetailsProcessor<DBProtocol, DBProject, IQueryCondition>(
				queryP) {
			@Override
			protected DBProtocol processDetail(DBProtocol target,
					DBProject detail) throws Exception {
				detail.setResourceURL(new URL(groupReporter.getURI(detail)));
				target.addProject(detail);
				return target;
			}
		};
		getProcessors().add(projectsReader);

		processors.add(new DefaultAmbitProcessor<DBProtocol, DBProtocol>() {
			@Override
			public DBProtocol process(DBProtocol target) throws AmbitException {
				processItem(target);
				return target;
			};
		});

	}

	@Override
	public void header(Writer output, IQueryRetrieval<DBProtocol> query) {
		try {
			output.write("{\"protocols\": [\n");
		} catch (Exception x) {
		}

	}

	private static String format = "\n{\n\t\"uri\":\"%s\",\n\t\"identifier\": \"%s\",\n\t\"title\": %s,\n\t\"abstract\": %s,\n\t\"owner\": {\"uri\" : %s},\n\t\"project\": {\"uri\" : %s, \"name\" : %s},\n\t\"organisation\": {\"uri\" : %s, \"name\" : %s},\n\t\"submitted\": %d,\n\t\"updated\": %d,\n\t\"status\": \"%s\",\n\t\"published\": %s\n\t}";

	@Override
	public Object processItem(DBProtocol item) throws AmbitException {
		try {
			if (comma != null)
				getOutput().write(comma);
			String uri = uriReporter.getURI(item);
			/*
			 * if ((item.getProject() != null) &&
			 * (item.getProject().getResourceURL() == null))
			 * item.getProject().setResourceURL( new
			 * URL(groupReporter.getURI((DBProject) item .getProject())));
			 */
			if ((item.getOrganisation() != null)
					&& (item.getOrganisation().getResourceURL() == null))
				item.getOrganisation().setResourceURL(
						new URL(groupReporter.getURI((DBOrganisation) item
								.getOrganisation())));

			if ((item.getOwner() != null)
					&& (item.getOwner().getResourceURL() == null))
				item.getOwner().setResourceURL(
						new URL(userReporter.getURI((DBUser) item.getOwner())));

			List<String> comments = item.getKeywords();

			URL pUri = null;
			String pName = null;
			if (item.getProject() != null) {
				pUri = item.getProject().getResourceURL();
				pName = item.getProject().getTitle();
			}
			URL oUri = null;
			String oName = null;
			if (item.getOrganisation() != null) {
				oUri = item.getOrganisation().getResourceURL();
				oName = item.getOrganisation().getTitle();
			}
			getOutput().write(
					String.format(format, uri, item.getIdentifier(), JSONUtils
							.jsonQuote(JSONUtils.jsonEscape(item.getTitle())),
							JSONUtils.jsonQuote(JSONUtils.jsonEscape(item
									.getAbstract())),
							JSONUtils.jsonQuote(JSONUtils.jsonEscape((item
									.getOwner() == null || item.getOwner()
									.getResourceURL() == null) ? null : item
									.getOwner().getResourceURL().toString())),
							JSONUtils.jsonQuote(JSONUtils
									.jsonEscape(pUri == null ? null : pUri
											.toExternalForm())), JSONUtils
									.jsonQuote(JSONUtils.jsonEscape(pName)),
							JSONUtils.jsonQuote(JSONUtils
									.jsonEscape(oUri == null ? null : oUri
											.toExternalForm())), JSONUtils
									.jsonQuote(JSONUtils.jsonEscape(oName)),
							item.getSubmissionDate(), item.getTimeModified(),
							item.getStatus(), item.isPublished()));

			comma = ",";
		} catch (Exception x) {
			x.printStackTrace();
		}
		return item;
	}

	@Override
	public void footer(Writer output, IQueryRetrieval<DBProtocol> query) {
		try {
			output.write("\n]\n}");
		} catch (Exception x) {
		}
	}

	public static String jsonEscape(String value) {
		return value.replace("\\", "\\\\").replace("/", "\\/")
				.replace("\b", "\\b").replace("\f", "\\f").replace("\n", "\\n")
				.replace("\r", "\\r").replace("\t", "\\t")
				.replace("\"", "\\\"");
	}

}
