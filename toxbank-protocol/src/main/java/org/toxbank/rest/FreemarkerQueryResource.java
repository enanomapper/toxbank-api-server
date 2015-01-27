package org.toxbank.rest;

import java.io.Serializable;
import java.util.Map;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.config.AMBITConfig;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.aa.opensso.OpenSSOServicesConfig;
import net.idea.restnet.aa.opensso.OpenSSOUser;
import net.idea.restnet.db.QueryResource;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

public abstract class FreemarkerQueryResource<Q extends IQueryRetrieval<T>, T extends Serializable>
		extends QueryResource<Q, T> {
	public FreemarkerQueryResource() {
		super();
		setHtmlbyTemplate(true);
	}

	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		/*
		 * map.put(AMBITDBRoles.ambit_admin.name(), Boolean.FALSE);
		 * map.put(AMBITDBRoles.ambit_datasetmgr.name(), Boolean.FALSE); if
		 * (getClientInfo()!=null) { if (getClientInfo().getUser()!=null)
		 * map.put("username", getClientInfo().getUser().getIdentifier()); if
		 * (getClientInfo().getRoles()!=null) { if
		 * (DBRoles.isAdmin(getClientInfo().getRoles()))
		 * map.put(AMBITDBRoles.ambit_admin.name(),Boolean.TRUE); if
		 * (DBRoles.isDatasetManager(getClientInfo().getRoles()))
		 * map.put(AMBITDBRoles.ambit_datasetmgr.name(), Boolean.TRUE); if
		 * (DBRoles.isUser(getClientInfo().getRoles()))
		 * map.put(AMBITDBRoles.ambit_user.name(), Boolean.TRUE); } }
		 */
		if (getClientInfo().getUser() != null) {
			// map.put("username", getClientInfo().getUser().getIdentifier());
			try {
				map.put("openam_token", ((OpenSSOUser) getClientInfo()
						.getUser()).getToken());
			} catch (Exception x) {
				map.remove("openam_token");
			}
		}
		try {
			map.put("openam_service", OpenSSOServicesConfig.getInstance()
					.getOpenSSOService());
		} catch (Exception x) {
			map.remove("openam_service");
		}
		map.put(AMBITConfig.creator.name(), "IdeaConsult Ltd.");
		map.put(AMBITConfig.ambit_root.name(), getRequest().getRootRef()
				.toString());
		map.put(AMBITConfig.ambit_version_short.name(), app.getVersionShort());
		map.put(AMBITConfig.ambit_version_long.name(), app.getVersionLong());
		map.put(AMBITConfig.googleAnalytics.name(), app.getGACode());
		map.put(AMBITConfig.menu_profile.name(), app.getProfile());

		// remove paging
		Form query = getRequest().getResourceRef().getQueryAsForm();
		// query.removeAll("page");query.removeAll("pagesize");query.removeAll("max");
		query.removeAll("media");
		Reference r = cleanedResourceRef(getRequest().getResourceRef());
		r.setQuery(query.getQueryString());

		map.put(AMBITConfig.ambit_request.name(), r.toString());
		if (query.size() > 0)
			map.put(AMBITConfig.ambit_query.name(), query.getQueryString());
		// json
		query.removeAll("media");
		query.add("media", MediaType.APPLICATION_JSON.toString());
		r.setQuery(query.getQueryString());
		map.put(AMBITConfig.ambit_request_json.name(), r.toString());
		// csv
		query.removeAll("media");
		query.add("media", MediaType.TEXT_CSV.toString());
		r.setQuery(query.getQueryString());
		map.put(AMBITConfig.ambit_request_csv.name(), r.toString());

	}
}
