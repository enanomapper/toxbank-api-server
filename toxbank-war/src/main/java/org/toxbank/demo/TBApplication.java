package org.toxbank.demo;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import net.idea.modbcum.i.config.Preferences;
import net.idea.restnet.aa.opensso.OpenSSOAuthenticator;
import net.idea.restnet.aa.opensso.OpenSSOAuthorizer;
import net.idea.restnet.aa.opensso.OpenSSOVerifierSetUser;
import net.idea.restnet.c.ChemicalMediaType;
import net.idea.restnet.c.freemarker.FreeMarkerApplication;
import net.idea.restnet.c.routers.MyRouter;
import net.idea.restnet.c.task.TaskStorage;
import net.toxbank.client.Resources;

import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.ClientInfo;
import org.restlet.data.Protocol;
import org.restlet.resource.Directory;
import org.restlet.resource.Finder;
import org.restlet.routing.Filter;
import org.restlet.routing.Route;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.Enroler;
import org.restlet.security.Verifier;
import org.restlet.service.TunnelService;
import org.restlet.util.RouteList;
import org.toxbank.demo.aa.ProtocolAuthorizer;
import org.toxbank.demo.aa.TBLoginResource;
import org.toxbank.demo.aa.UserAuthorizer;
import org.toxbank.demo.task.TBAdminResource;
import org.toxbank.demo.task.TBAdminRouter;
import org.toxbank.demo.task.TBTaskResource;
import org.toxbank.demo.task.TBTaskRouter;
import org.toxbank.demo.task.TBTaskStorage;
import org.toxbank.rest.FreeMarkerStatusService;
import org.toxbank.rest.groups.OrganisationRouter;
import org.toxbank.rest.groups.ProjectRouter;
import org.toxbank.rest.protocol.ProtocolRouter;
import org.toxbank.rest.user.UserRouter;
import org.toxbank.rest.user.alerts.notification.NotificationResource;
import org.toxbank.rest.user.alerts.resource.AlertRouter;
import org.toxbank.rest.user.resource.MyAccountResource;

/**
 * AMBIT implementation of OpenTox REST services as described in
 * http://opentox.org/development/wiki/ http://opentox.org/wiki/1/Dataset
 * 
 * @author nina
 */

/*
 * http://www.slideshare.net/guest7d0e11/creating-a-web-of-data-with-restlet-
 * presentation
 * http://stackoverflow.com/questions/810171/how-to-read-context-parameters
 * -from-a-restlet
 */
public class TBApplication extends FreeMarkerApplication<String> {
	public static final String _AAENABLED_PROPERTY = "toxbank.protected";
	protected Hashtable<String, Properties> properties = new Hashtable<String, Properties>();
	protected boolean aaenabled = false;
	static final String version = "ambit.version";
	static final String version_build = "ambit.build";
	static final String version_timestamp = "ambit.build.timestamp";
	static final String googleAnalytics = "google.analytics";
	public static final String ALLOWED_ORIGINS = "allowed.origins";
	static final String protocolProperties = "org/toxbank/rest/config/toxbank.properties";

	public TBApplication() {
		super();
		setName("enanoMapper Protocol service");
		setDescription("enanoMapper Protocol service");
		setOwner("Ideaconsult Ltd.");
		setAuthor("Ideaconsult Ltd.");

		setProfile(getMenuProfile());

		versionShort = readVersionShort();
		versionLong = readVersionLong();
		gaCode = readGACode();

		/*
		 * String tmpDir = System.getProperty("java.io.tmpdir"); File logFile =
		 * new File(tmpDir,"ambit2-www.log");
		 * System.setProperty("java.util.logging.config.file"
		 * ,logFile.getAbsolutePath());
		 */

		// setStatusService(new TBRESTStatusService());
		setStatusService(new FreeMarkerStatusService(this,getStatusReportLevel()));
		setTunnelService(new TunnelService(true, true) {
			@Override
			public Filter createInboundFilter(Context context) {
				return new TBRESTTunnelFilter(context);
			}
		});
		getTunnelService().setUserAgentTunnel(true);
		getTunnelService().setExtensionsTunnel(false);
		getTunnelService().setMethodTunnel(true);

		Preferences.setProperty(Preferences.MAXRECORDS, "0");

		getMetadataService().setEnabled(true);
		getMetadataService().addExtension("sdf",
				ChemicalMediaType.CHEMICAL_MDLSDF, true);
		getMetadataService().addExtension("mol",
				ChemicalMediaType.CHEMICAL_MDLMOL, true);
		getMetadataService().addExtension("inchi",
				ChemicalMediaType.CHEMICAL_INCHI, true);
		getMetadataService().addExtension("cml",
				ChemicalMediaType.CHEMICAL_CML, true);
		getMetadataService().addExtension("smiles",
				ChemicalMediaType.CHEMICAL_SMILES, true);

	}

	protected synchronized String getProperty(String name, String config) {
		try {
			Properties p = properties.get(config);
			if (p == null) {
				p = new Properties();
				InputStream in = this.getClass().getClassLoader()
						.getResourceAsStream(config);
				p.load(in);
				in.close();
				properties.put(config, p);
			}
			return p.getProperty(name);

		} catch (Exception x) {
			return null;
		}
	}

	public synchronized String readVersionShort() {
		try {
			return getProperty(version, protocolProperties);
		} catch (Exception x) {
			return "Unknown";
		}
	}

	public synchronized String readVersionLong() {
		try {
			String v1 = getProperty(version, protocolProperties);
			String v2 = getProperty(version_build, protocolProperties);
			String v3 = getProperty(version_timestamp, protocolProperties);
			return String.format("%s r%s built %s", v1, v2,
					new Date(Long.parseLong(v3)));
		} catch (Exception x) {
			return "Unknown";
		}
	}

	public synchronized String readGACode() {
		try {
			String ga = getProperty(googleAnalytics, protocolProperties);
			if ("".equals(ga))
				return null;
			return ga;
		} catch (Exception x) {
			return null;
		}
	}

	@Override
	public Restlet createInboundRoot() {
		initFreeMarkerConfiguration();
		aaenabled = isProtected();
		Router router = new MyRouter(this.getContext());
		// router.attach("/help", AmbitResource.class);

		/**
		 * OpenSSO login / logout Sets a cookie with OpenSSO token
		 */
		Restlet login = createOpenSSOLoginRouter();
		router.attach("/login", login);

		router.attach("/", login);
		router.attach("", login);

		/**
		 * Points to the Ontology service /sparqlendpoint
		 */
		// router.attach(SPARQLPointerResource.resource,
		// SPARQLPointerResource.class);

		/**
		 * * /admin Various admin tasks, like database creation
		 */

		router.attach(String.format("/%s", TBAdminResource.resource),
				createProtectedResource(createAdminRouter(), "admin"));

		/** /policy - used for testing only */
		// router.attach(String.format("/%s",PolicyResource.resource),PolicyResource.class);

		/**
		 * List of datasets /dataset , /datasets
		 * 
		 * Router allDatasetsRouter = new MyRouter(getContext());
		 * allDatasetsRouter.attachDefault(DatasetsResource.class);
		 * 
		 * router.attach(DatasetsResource.datasets,
		 * createProtectedResource(allDatasetsRouter,"datasets"));
		 */
		/** /task */
		router.attach(TBTaskResource.resource,
				createOpenSSOVerifiedResource(new TBTaskRouter(getContext())));

		ProtocolRouter protocols = new ProtocolRouter(getContext());
		/** /protocol */
		// router.attach(Resources.protocol, createProtectedResource(new
		// ProtocolRouter(getContext()),"",false));
		OrganisationRouter org_router = new OrganisationRouter(getContext());
		ProjectRouter projectRouter = new ProjectRouter(getContext());
		AlertRouter alertRouter = new AlertRouter(getContext());
		MyRouter notificationRouter = new MyRouter(getContext());
		notificationRouter.attachDefault(NotificationResource.class);
		if (aaenabled) {
			router.attach(
					Resources.protocol,
					createProtectedResource(protocols, "protocol",
							new ProtocolAuthorizer("protocol")));
			router.attach(Resources.project,
					createProtectedResource(projectRouter, "project", true));
			router.attach(Resources.organisation,
					createProtectedResource(org_router, "organisation", true));
			router.attach(Resources.alert,
					createProtectedResource(org_router, "alert", true));
			router.attach(
					Resources.user,
					createProtectedResource(new UserRouter(getContext(),
							protocols, org_router, projectRouter, alertRouter),
							"user", new UserAuthorizer()));
			router.attach(
					NotificationResource.resourceKey,
					createProtectedResource(notificationRouter, "notification",
							new OpenSSOAuthorizerRIAP()));

		} else {
			router.attach(Resources.protocol,
					createOpenSSOVerifiedResource(protocols));
			router.attach(Resources.project,
					createOpenSSOVerifiedResource(projectRouter));
			router.attach(Resources.organisation,
					createOpenSSOVerifiedResource(org_router));
			router.attach(Resources.alert,
					createOpenSSOVerifiedResource(alertRouter));
			router.attach(Resources.user,
					createOpenSSOVerifiedResource(new UserRouter(getContext(),
							protocols, org_router, projectRouter, alertRouter)));
			router.attach(NotificationResource.resourceKey,
					createOpenSSOVerifiedResource(notificationRouter));

		}
		router.attach("/myaccount",
				createOpenSSOVerifiedResource(MyAccountResource.class));

		/**
		 * Queries /query
		 * 
		 */
		// Router queryRouter = createQueryRouter();
		// router.attach(QueryResource.query_resource,queryRouter);

		/**
		 * API extensions from this point on
		 */

		router.attach("/api-docs", new APIDocsRouter(getContext()));
		/**
		 * 
		 * Images, styles, favicons, applets
		 */
		attachStaticResources(router);

		/**
		 * login/logout for local users . TODO refactor to use cookies as in
		 * /opentoxuser
		 */
		// router.attach(SwitchUserResource.resource,createGuardGuest(SwitchUserResource.class));

		router.setDefaultMatchingMode(Template.MODE_STARTS_WITH);
		router.setRoutingMode(Router.MODE_BEST_MATCH);
		/*
		 * StringWriter w = new StringWriter();
		 * TBApplication.printRoutes(router,">",w);
		 * System.out.println(w.toString());
		 */
		return addOriginFilter(router);
	}

	protected Restlet createOpenSSOVerifiedResource(Restlet next) {
		Filter userAuthn = new OpenSSOAuthenticator(getContext(), !aaenabled,
				"opentox.org", new OpenSSOVerifierSetUser(false));
		userAuthn.setNext(next);
		return userAuthn;
	}

	protected Restlet createOpenSSOVerifiedResource(Class clazz) {
		Filter userAuthn = new OpenSSOAuthenticator(getContext(), !aaenabled,
				"opentox.org", new OpenSSOVerifierSetUser(false));
		userAuthn.setNext(clazz);
		return userAuthn;
	}

	protected Restlet createOpenSSOLoginRouter() {
		return createOpenSSOVerifiedResource(TBLoginResource.class);
	}

	protected Restlet createProtectedResource(Restlet router) {
		return createProtectedResource(router, null);
	}

	protected Restlet createProtectedResource(Restlet router, String prefix) {
		return createProtectedResource(router, prefix, true);
	}

	protected Restlet createProtectedResource(Restlet router, String prefix,
			boolean authzEnabled) {
		return createProtectedResource(router, prefix,
				authzEnabled ? new OpenSSOAuthorizer() : null);
	}

	protected Restlet createProtectedResource(Restlet router, String prefix,
			OpenSSOAuthorizer authz) {
		Filter authN = new OpenSSOAuthenticator(getContext(), false,
				"opentox.org", new OpenSSOVerifierSetUser(false));
		if (authz != null) {
			authz.setPrefix(prefix);
			authN.setNext(authz);
			authz.setNext(router);
		} else {
			authN.setNext(router);
		}
		return authN;
	}

	/**
	 * Everything under /query
	 * 
	 * @return
	 */
	protected Router createQueryRouter() {

		Router queryRouter = new MyRouter(getContext());
		// queryRouter.attachDefault(QueryListResource.class);

		/**
		 * PubChem query
		 */
		/*
		 * Router pubchem = new MyRouter(getContext());
		 * queryRouter.attach(PubchemResource.resource,pubchem);
		 * pubchem.attachDefault(PubchemResource.class);
		 * pubchem.attach(PubchemResource.resourceID,PubchemResource.class);
		 */
		/**
		 * CIR query
		 * 
		 * Router cir = new MyRouter(getContext());
		 * queryRouter.attach(CSLSResource.resource,cir);
		 * cir.attachDefault(CSLSResource.class);
		 * cir.attach(CSLSResource.resourceID,CSLSResource.class);
		 * cir.attach(CSLSResource
		 * .resourceID+CSLSResource.representationID,CSLSResource.class);
		 */
		/**
		 * ChEBI query
		 * 
		 * Router chebi = new MyRouter(getContext());
		 * queryRouter.attach(ChEBIResource.resource,chebi);
		 * chebi.attachDefault(ChEBIResource.class);
		 * chebi.attach(ChEBIResource.resourceID,ChEBIResource.class);
		 */
		/**
		 * Compound search /query/compound/lookup
		 * 
		 * Router lookup = new MyRouter(getContext());
		 * queryRouter.attach(CompoundLookup.resource,lookup);
		 * lookup.attachDefault(CompoundLookup.class);
		 * lookup.attach(CompoundLookup.resourceID,CompoundLookup.class);
		 * lookup.
		 * attach(CompoundLookup.resourceID+CompoundLookup.representationID
		 * ,CompoundLookup.class);
		 */
		return queryRouter;
	}

	/**
	 * Check for OpenSSO token and set the user, if available but don't verify
	 * the policy
	 * 
	 * @return
	 */
	protected Restlet createAuthenticatedOpenResource(Router router) {
		Filter algAuthn = new OpenSSOAuthenticator(getContext(), false,
				"opentox.org", new OpenSSOVerifierSetUser(false));
		algAuthn.setNext(router);
		return algAuthn;
	}

	protected TaskStorage<String> createTaskStorage() {
		return new TBTaskStorage(getName(), getLogger());

	}

	/**
	 * Resource /bookmark
	 * 
	 * @return
	 * 
	 *         protected Restlet createBookmarksRouter() { BookmarksRouter
	 *         bookmarkRouter = new BookmarksRouter(getContext());
	 * 
	 *         Filter bookmarkAuth = new
	 *         OpenSSOAuthenticator(getContext(),false,"opentox.org"); Filter
	 *         bookmarkAuthz = new BookmarksAuthorizer();
	 *         bookmarkAuth.setNext(bookmarkAuthz);
	 *         bookmarkAuthz.setNext(bookmarkRouter); return bookmarkAuth; }
	 */
	/**
	 * Resource /admin
	 * 
	 * @return
	 */
	protected Restlet createAdminRouter() {
		return new TBAdminRouter(getContext());
		// DBCreateAllowedGuard dbguard = new DBCreateAllowedGuard();
		// dbguard.setNext(adminRouter);
		// return dbguard;
	}

	/**
	 * /ontology RDF playground, not used currently
	 * 
	 * @return
	 */
	protected Restlet createRDFPlayground() {
		// test removed, there is ontology service
		// router.attach(RDFGraphResource.resource,RDFGraphResource.class);
		// router.attach(RDFGraphResource.resource+"/test",OntologyPlayground.class);

		// router.attach(OntologyResource.resource, OntologyResource.class);
		// router.attach(OntologyResource.resourceID, OntologyResource.class);
		// router.attach(OntologyResource.resourceTree, OntologyResource.class);
		return null;
	}

	/**
	 * Resource protection via local MySQL/ Ambit database users. Not used
	 * currenty;
	 * 
	 * @return
	 */
	protected Restlet createLocalUsersGuard() {

		//

		/**
		 * // These are users from the DB // /user DBVerifier verifier = new
		 * DBVerifier(this); Router usersRouter = new MyRouter(getContext());
		 * usersRouter.attachDefault(UserResource.class); // /user/{userid}
		 * Router userRouter = new MyRouter(getContext());
		 * userRouter.attachDefault(UserResource.class);
		 * usersRouter.attach(UserResource.resourceID,userRouter); //
		 * authentication mandatory for users resource Filter guard =
		 * createGuard(verifier,false); // Simple authorizer MethodAuthorizer
		 * authorizer = new MethodAuthorizer();
		 * authorizer.getAnonymousMethods().add(Method.GET);
		 * authorizer.getAnonymousMethods().add(Method.HEAD);
		 * authorizer.getAnonymousMethods().add(Method.OPTIONS);
		 * authorizer.getAuthenticatedMethods().add(Method.PUT);
		 * authorizer.getAuthenticatedMethods().add(Method.DELETE);
		 * authorizer.getAuthenticatedMethods().add(Method.POST);
		 * authorizer.getAuthenticatedMethods().add(Method.OPTIONS);
		 * authorizer.setNext(usersRouter); guard.setNext(authorizer);
		 * router.attach(UserResource.resource, guard);
		 * router.attach(UserResource.resource, usersRouter);
		 */
		return null;
	}

	/**
	 * Images, styles, icons Works if packaged as war only!
	 * 
	 * @return
	 */
	protected void attachStaticResources(Router router) {
		/*
		 * router.attach("/images",new Directory(getContext(),
		 * LocalReference.createFileReference("/webapps/images")));
		 */

		Directory metaDir = new Directory(getContext(), "war:///META-INF");
		Directory imgDir = new Directory(getContext(), "war:///images");
		Directory jmolDir = new Directory(getContext(), "war:///jmol");
		Directory jmeDir = new Directory(getContext(), "war:///jme");
		Directory styleDir = new Directory(getContext(), "war:///style");
		Directory jquery = new Directory(getContext(), "war:///jquery");
		Directory scripts = new Directory(getContext(), "war:///scripts");

		router.attach("/meta/", metaDir);
		router.attach("/images/", imgDir);
		router.attach("/jmol/", jmolDir);
		router.attach("/jme/", jmeDir);
		router.attach("/jquery/", jquery);
		router.attach("/scripts/", scripts);
		router.attach("/style/", styleDir);
		router.attach("/favicon.ico", FavIconResource.class);
		router.attach("/favicon.png", FavIconResource.class);
	}

	/**
	 * Standalone, for testing mainly
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// Create a component
		Component component = new TBRESTComponent();
		final Server server = component.getServers().add(Protocol.HTTP, 8080);
		component.start();

		System.out.println("Server started on port " + server.getPort());
		System.out.println("Press key to stop server");
		System.in.read();
		System.out.println("Stopping server");
		component.stop();
		System.out.println("Server stopped");
	}

	protected ChallengeAuthenticator createGuard(Verifier verifier,
			boolean optional) {

		Enroler enroler = new Enroler() {
			public void enrole(ClientInfo subject) {
				System.out.println(subject);

			}
		};
		// Create a Guard
		ChallengeAuthenticator guard = new ChallengeAuthenticator(getContext(),
				optional, ChallengeScheme.HTTP_BASIC, "ambit2") {
			@Override
			protected boolean authenticate(Request request, Response response) {
				return super.authenticate(request, response);
			}
		};
		guard.setVerifier(verifier);
		guard.setEnroler(enroler);

		return guard;
	}

	public static String printRoutes(Restlet re, String delimiter,
			StringWriter b) {

		while (re != null) {

			b.append(re.getClass().toString());
			b.append('\t');
			if (re instanceof Finder) {
				b.append(((Finder) re).getTargetClass().getName());
				b.append('\n');
				re = null;
			} else if (re instanceof Filter)
				re = ((Filter) re).getNext();
			else if (re instanceof Router) {
				b.append('\n');
				RouteList list = ((Router) re).getRoutes();
				for (Route r : list) {

					b.append(delimiter);
					b.append(r.getTemplate().getPattern());
					b.append('\t');
					b.append(r.getTemplate().getVariableNames().toString());
					printRoutes(r.getNext(), '\t' + delimiter
							+ r.getTemplate().getPattern(), b);
				}

				break;
			} else {
				break;
			}

		}

		return b.toString();

	}

	protected boolean isProtected() {
		InputStream in = null;
		try {
			Properties properties = new Properties();
			in = this
					.getClass()
					.getClassLoader()
					.getResourceAsStream(
							"org/toxbank/rest/config/toxbank.properties");
			properties.load(in);

			boolean aa = Boolean.parseBoolean(properties.get(
					_AAENABLED_PROPERTY).toString());

			if ((getContext() != null)
					&& (getContext().getParameters() != null)
					&& (getContext().getParameters().getFirstValue(
							_AAENABLED_PROPERTY) != null))
				aa = Boolean.parseBoolean(getContext().getParameters()
						.getFirstValue(_AAENABLED_PROPERTY));
			return aa;
		} catch (Exception x) {
			x.printStackTrace();
			try {
				in.close();
			} catch (Exception xx) {
			}
		}
		return false;
	}

	protected synchronized String getAllowedOrigins() {
		try {
			return getProperty(ALLOWED_ORIGINS, protocolProperties);
		} catch (Exception x) {
			return null;
		}
	}

	protected Restlet addOriginFilter(Restlet router) {
		String allowedOrigins = getAllowedOrigins();
		getLogger().info("CORS: Origin filter attached:\t" + allowedOrigins);
		OriginFilter originFilter = new OriginFilter(getContext(),
				allowedOrigins);
		originFilter.setNext(router);
		/*
		 * StringWriter w = new StringWriter(); printRoutes(router,"\t",w);
		 * System.out.println(w);
		 */
		return originFilter;
	}

	public synchronized String getMenuProfile() {
		String prefix = getProperty("ambit.profile", protocolProperties);
		if (prefix == null || "".equals(prefix) || prefix.contains("${"))
			prefix = "default";
		return prefix;
	}
	
	/**
	 * Reads the status report level from ambit.pref ${ambit.report.level}
	 * If debug the status will include the stack trace 
	 * @return
	 */
	   	protected FreeMarkerStatusService.REPORT_LEVEL getStatusReportLevel() {
			try {
				FreeMarkerStatusService.REPORT_LEVEL aa = FreeMarkerStatusService.REPORT_LEVEL.valueOf(getProperty(FreeMarkerStatusService.report_level,protocolProperties));
				if ((getContext()!=null) && 
					(getContext().getParameters()!=null) && 
					(getContext().getParameters().getFirstValue(FreeMarkerStatusService.report_level))!=null)
					aa = FreeMarkerStatusService.REPORT_LEVEL.valueOf(getContext().getParameters().getFirstValue(FreeMarkerStatusService.report_level));
				return aa;
			} catch (Exception x) {	}
			return FreeMarkerStatusService.REPORT_LEVEL.production;
		}
}

class OpenSSOAuthorizerRIAP extends OpenSSOAuthorizer {
	@Override
	protected boolean authorize(Request request, Response response) {
		return Protocol.RIAP.equals(request.getProtocol()) ? true : super
				.authorize(request, response);
	}
}

class APIDocsRouter extends MyRouter {

	public APIDocsRouter(Context context) {
		super(context);
		init();
	}

	protected void init() {
		attachDefault(APIdocsResource.class);
		attach("/{key1}", APIdocsResource.class);
		attach("/{key1}/{key2}", APIdocsResource.class);
	}
}