package org.toxbank.rest.user.resource;

import java.sql.Connection;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.db.convertors.RDFJenaConvertor;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.rdf.FactoryTaskConvertorRDF;
import net.toxbank.client.io.rdf.TOXBANK;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.FreemarkerQueryResource;
import org.toxbank.rest.user.CallableUserCreator;
import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.db.ReadUser;

/**
 * Protocol resource
 * 
 * @author nina
 * 
 * @param <Q>
 */
public class UserDBResource<T> extends FreemarkerQueryResource<ReadUser<T>, DBUser> {
	public static final String resourceKey = "user";

	protected boolean singleItem = false;
	protected boolean editable = true;

	@Override
	public String getTemplateName() {
		return "users.ftl";
	}

	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		/*
		 * if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
		 * 
		 * return new StringConvertor(new
		 * PropertyValueReporter(),MediaType.TEXT_PLAIN);
		 * 
		 * } else
		 */
		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor(
					new UserURIReporter<IQueryRetrieval<DBUser>>(getRequest()),
					MediaType.TEXT_URI_LIST, filenamePrefix);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			return new OutputWriterConvertor(
					new UserJSONReporter<IQueryRetrieval<DBUser>>(getRequest()),
					MediaType.APPLICATION_JSON);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML)
				|| variant.getMediaType().equals(
						MediaType.APPLICATION_RDF_TURTLE)
				|| variant.getMediaType().equals(MediaType.TEXT_RDF_N3)
				|| variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)
				|| variant.getMediaType().equals(MediaType.APPLICATION_JSON)
				|| variant.getMediaType().equals(MediaType.TEXT_CSV)

		) {
			return new RDFJenaConvertor<DBUser, IQueryRetrieval<DBUser>>(
					new UserRDFReporter<IQueryRetrieval<DBUser>>(getRequest(),
							variant.getMediaType(), getDocumentation()),
					variant.getMediaType(), filenamePrefix) {
				private static final long serialVersionUID = -1599773662253590223L;

				@Override
				protected String getDefaultNameSpace() {
					return TOXBANK.URI;
				}
			};
		} else if (variant.getMediaType().equals(MediaType.TEXT_HTML))
			return new OutputWriterConvertor(new UserHTMLReporter(getRequest(),
					!singleItem, editable), MediaType.TEXT_HTML);
		else
			throw new ResourceException(
					Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
	}

	protected ReadUser getUserQuery(Object key, String search_name,
			Object search_value) throws ResourceException {
		if (key == null) {
			ReadUser query = new ReadUser();
			if (search_value != null) {
				if ("search".equals(search_name)) {
					DBUser user = new DBUser();
					String s = String.format("^%s", search_value.toString());
					user.setLastname(s);
					user.setFirstname(s);
					query.setValue(user);
				} else if ("username".equals(search_name)) {
					DBUser user = new DBUser();
					query.setCondition(EQCondition.getInstance());
					user.setUserName(search_value.toString());
					query.setValue(user);
				}
			}
			return query;
		} else {
			if (key.toString().startsWith("U")) {
				singleItem = true;
				return new ReadUser(new Integer(Reference.decode(key.toString()
						.substring(1))));
			} else
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
	}

	@Override
	protected ReadUser createQuery(Context context, Request request,
			Response response) throws ResourceException {
		Form form = request.getResourceRef().getQueryAsForm();
		String search_name = null;
		Object search_value = null;
		try {
			search_name = "search";
			search_value = form.getFirstValue(search_name);
		} catch (Exception x) {
			search_value = null;
		}
		if (search_value == null)
			try {
				search_name = "username";
				search_value = form.getFirstValue(search_name);
			} catch (Exception x) {
				search_value = null;
			}
		try {
			String n = form.getFirstValue("new");
			editable = n == null ? false : Boolean.parseBoolean(n);
		} catch (Exception x) {
			editable = false;
		}
		Object key = request.getAttributes().get(UserDBResource.resourceKey);
		try {
			return getUserQuery(key, search_name, search_value);
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Invalid user id %d", key), x);
		}
	}

	@Override
	protected QueryURIReporter<DBUser, ReadUser<T>> getURIReporter(
			Request baseReference) throws ResourceException {
		return new UserURIReporter(getRequest());
	}


	@Override
	protected CallableProtectedTask<String> createCallable(Method method,
			Form form, DBUser item) throws ResourceException {
		Connection conn = null;
		try {
			UserURIReporter reporter = new UserURIReporter(getRequest(), "");
			DBConnection dbc = new DBConnection(getApplication().getContext(),
					getConfigFile());
			conn = dbc.getConnection();
			return new CallableUserCreator(method, item, reporter, form,
					getRequest().getRootRef().toString(), conn, getToken());
		} catch (Exception x) {
			try {
				conn.close();
			} catch (Exception xx) {
			}
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x);
		}
	}

	protected String getObjectURI(Form queryForm) throws ResourceException {
		return null;
	}

	@Override
	protected ReadUser<T> createUpdateQuery(Method method, Context context,
			Request request, Response response) throws ResourceException {
		Object key = request.getAttributes().get(UserDBResource.resourceKey);
		if (Method.POST.equals(method)) {
			if (key == null)
				return null;// post allowed only on /user level, not on /user/id
		} else {
			if (key != null) {

				try {
					return getUserQuery(key, null, null);
				} catch (ResourceException x) {
					throw x;
				} catch (Exception x) {
					throw new ResourceException(
							Status.CLIENT_ERROR_BAD_REQUEST, String.format(
									"Invalid user id %d", key), x);
				}
			}
		}
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected FactoryTaskConvertor getFactoryTaskConvertor(ITaskStorage storage)
			throws ResourceException {
		return new FactoryTaskConvertorRDF(storage);
	}

	@Override
	protected boolean isAllowedMediaType(MediaType mediaType)
			throws ResourceException {
		return MediaType.APPLICATION_WWW_FORM.equals(mediaType);
	}
}
