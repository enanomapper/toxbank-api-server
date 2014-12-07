{
    "apiVersion":  "${ambit_version_short}",
    "swaggerVersion": "1.2",
    "apis": [
        {
            "path": "/protocol",
            "description": "protocol"
        },
 		{
            "path": "/user",
            "description": "user"
        },           
 		{
            "path": "/project",
            "description": "Project"
        },        
        {
            "path": "/organisation",
            "description": "Organisation"
        },
      
        {
            "path": "/task",
            "description": "OpenTox Task service (asynchronous jobs)"
        }
        
                                 
    ],
	<#include "/apidocs/authz.ftl" >
	<#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}