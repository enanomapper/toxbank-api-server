package org.toxbank.demo.task;

import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.aa.opensso.OpenSSOServicesConfig;
import net.idea.restnet.aa.opensso.OpenSSOUser;
import net.idea.restnet.c.AbstractResource;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.SimpleTaskResource;
import net.idea.restnet.c.html.HTMLBeauty;
import net.idea.restnet.c.reporters.TaskHTMLReporter;
import net.idea.restnet.c.resource.TaskResource;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.i.task.TaskStatus;

import org.owasp.encoder.Encode;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.toxbank.rest.protocol.TBHTMLBeauty;

import ambit2.base.config.AMBITConfig;

public class TBTaskResource extends TaskResource<String> {

	public TBTaskResource() {
		super();
		setHtmlbyTemplate(true);
	}
	@Override
	public String getTemplateName() {
		return "task.ftl";
	}
	@Override
	protected HTMLBeauty getHTMLBeauty() {
		return new TBHTMLBeauty();
	}
	
	@Override
	protected FactoryTaskConvertor getFactoryTaskConvertor(ITaskStorage storage)
			throws ResourceException {
		return new FactoryTaskConvertor<Object>(storage,getHTMLBeauty()) {
			@Override
			public synchronized Reporter<Iterator<UUID>, Writer> createTaskReporterRDF(
					Variant variant, Request request, ResourceDoc doc)
					throws AmbitException, ResourceException {
				return new TBTaskRDFReporter(storage,request,variant.getMediaType(),doc);
			}
			@Override
			public synchronized Reporter<Iterator<UUID>, Writer> createTaskReporterHTML(
					Request request, ResourceDoc doc, HTMLBeauty htmlbeauty)
					throws AmbitException, ResourceException {
				return	new TaskHTMLReporter(storage,request,doc,htmlbeauty) {
					
					@Override
					public void header(Writer output, Iterator query) {
						try {
							String max = getRequest().getResourceRef().getQueryAsForm().getFirstValue(AbstractResource.max_hits);
							max = max==null?"10":max;
							
							if (htmlBeauty==null) htmlBeauty = new TBHTMLBeauty();
							htmlBeauty.writeHTMLHeader(output, htmlBeauty.getTitle(), getRequest(),"",
									getDocumentation()
									);//,"<meta http-equiv=\"refresh\" content=\"10\">");
							output.write("\n<h3>Tasks:\n");
							for (TaskStatus status :TaskStatus.values())
								output.write(String.format("<a href='%s%s?search=%s&%s=%s'>%s</a>&nbsp;\n",
										baseReference,SimpleTaskResource.resource,status,AbstractResource.max_hits,max,status));
							output.write("</h3>\n");
							output.write("<table class='datatable' id='tasktable'>\n");
							output.write("<thead><th>Start time</th><th>Elapsed time,ms</th><th>Task</th><th>Name</th><th></th><th>Status</th><th>Status Message</th><th></th></thead>\n");
							output.write("<tbody>\n");
						} catch (Exception x) {
							
						}
					}
					@Override
					public void footer(Writer output, Iterator query) {
						try {
							output.write("</tbody>\n");
						} catch (Exception x) {
							
						}
						super.footer(output, query);
					}
				};
			}
		};
	}
	
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
        /*
		map.put(AMBITDBRoles.ambit_admin.name(), Boolean.FALSE);
		map.put(AMBITDBRoles.ambit_datasetmgr.name(), Boolean.FALSE);
		if (getClientInfo()!=null) {
			if (getClientInfo().getUser()!=null)
				map.put("username", getClientInfo().getUser().getIdentifier());
			if (getClientInfo().getRoles()!=null) {
				if (DBRoles.isAdmin(getClientInfo().getRoles()))
					map.put(AMBITDBRoles.ambit_admin.name(),Boolean.TRUE);
				if (DBRoles.isDatasetManager(getClientInfo().getRoles()))
					map.put(AMBITDBRoles.ambit_datasetmgr.name(), Boolean.TRUE);
				if (DBRoles.isUser(getClientInfo().getRoles()))
					map.put(AMBITDBRoles.ambit_user.name(), Boolean.TRUE);	
			}
		}
		*/
        if (getClientInfo().getUser()!=null) {
        //	map.put("username", getClientInfo().getUser().getIdentifier());
        	try {
        		map.put("openam_token",((OpenSSOUser) getClientInfo().getUser()).getToken());  
        	} catch (Exception x) {
        		map.remove("openam_token");
        	}
        }
        try {
        	map.put("openam_service", OpenSSOServicesConfig.getInstance().getOpenSSOService());
        } catch (Exception x) {
        	map.remove("openam_service");
        }		
        map.put(AMBITConfig.creator.name(),"IdeaConsult Ltd.");
        map.put(AMBITConfig.ambit_root.name(),getRequest().getRootRef().toString());
        map.put(AMBITConfig.ambit_version_short.name(),app.getVersionShort());
	    map.put(AMBITConfig.ambit_version_long.name(),app.getVersionLong());
	    map.put(AMBITConfig.googleAnalytics.name(),app.getGACode());
	    map.put(AMBITConfig.menu_profile.name(),app.getProfile());

	    
        //remove paging
        Form query = getRequest().getResourceRef().getQueryAsForm();
        //query.removeAll("page");query.removeAll("pagesize");query.removeAll("max");
        query.removeAll("media");
        Reference r = cleanedResourceRef(getRequest().getResourceRef());
        r.setQuery(query.getQueryString());
        
        map.put(AMBITConfig.ambit_request.name(),r.toString()) ;
        if (query.size()>0)
        	map.put(AMBITConfig.ambit_query.name(),query.getQueryString()) ;
        //json
        query.removeAll("media");query.add("media", MediaType.APPLICATION_JSON.toString());
        r.setQuery(query.getQueryString());
        map.put(AMBITConfig.ambit_request_json.name(),r.toString());
        //csv
        query.removeAll("media");query.add("media", MediaType.TEXT_CSV.toString());
        r.setQuery(query.getQueryString());
        map.put(AMBITConfig.ambit_request_csv.name(),r.toString());
 
        map.put(AMBITConfig.ambit_version_short.name(),app.getVersionShort());
	    map.put(AMBITConfig.ambit_version_long.name(),app.getVersionLong());
	    map.put(AMBITConfig.googleAnalytics.name(),app.getGACode());
	    map.put(AMBITConfig.menu_profile.name(),app.getProfile());
	}
	protected Reference cleanedResourceRef(Reference ref) {
		return new Reference(Encode.forJavaScriptSource(ref.toString()));
	}	
}
