package org.toxbank.rest.protocol.resource.db;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.db.QueryResource;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.FileResource;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.db.ReadProtocol;
import org.toxbank.rest.protocol.db.template.ReadFilePointers;

public class ProtocolDocumentResource extends QueryResource<IQueryRetrieval<DBProtocol>,DBProtocol> {

	
	@Override
	public IProcessor<IQueryRetrieval<DBProtocol>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		return new DownloadDocumentConvertor(new FileReporter());
	}
	
	@Override
	protected IQueryRetrieval<DBProtocol> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		final Object key = request.getAttributes().get(FileResource.resourceKey);		
		try {
			if (key==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			else {
				int id[] = ReadProtocol.parseIdentifier(Reference.decode(key.toString()));
				return new ReadFilePointers(id[0],id[1]);
			}
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(
					Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Invalid protocol id %d",key),
					x
					);
		}
	}
}
