{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "text/n3",
        "application/rdf+xml",
        "text/uri-list"
    ],
    "resourcePath": "/project",
    "apis": [
        {
            "path": "/project",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Retrieve list of projects",
                    "notes": "Retrieve list of project <a href='http://api.toxbank.net/index.php/Project' target='opentox'>Project API</a>",
                    "type": "Project",
                    "nickname": "getAllProject",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
                        {
                            "name": "search",
                            "description": "prefix_name",
                            "required": false,
                            "type": "string",
                            "paramType": "query",
                            "allowMultiple": false
                        },		    
						<#include "/apidocs/parameters_page.ftl" >	                        
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "User not found"
                        },
						<#include "/apidocs/error_500.ftl" >	                        
                    ]
                }
            ]
        },
 		{
            "path": "/project/{id}",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Retrieves a single project",
                    "notes": "Retrieve projects <a href='http://api.toxbank.net/index.php/Project' target='opentox'>Project API</a>",
                    "type": "Project",
                    "nickname": "getProjectID",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
                        {
                            "name": "id",
                            "description": "Project identifier",
                            "required": false,
                            "type": "string",
                            "paramType": "path",
                            "allowMultiple": false
                        }                       
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Task not found"
                        },
                        <#include "/apidocs/error_task.ftl" >,                                   
						<#include "/apidocs/error_500.ftl" >	     
                    ]
                },
                {
                    "method": "POST",
                    "summary": "Creates a project",
                    "notes": "Creates a project <a href='http://api.toxbank.net/index.php/Project' target='opentox'>Project API</a>",
                    "type": "Task",
                    "nickname": "createProject",
					"consumes": [
		                       "application/x-www-form-urlencoded"
		                ],			                    
     				 <#include "/apidocs/authz.ftl" >
                    "parameters": [
							{
							    "name": "title",
							    "description": "title",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false
							},	
							{
							    "name": "ldapgroup",
							    "description": "ldapgroup",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false
							}
																				
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Project not found"
                        },
                        <#include "/apidocs/error_task.ftl" >,                                   
						<#include "/apidocs/error_500.ftl" >	     
                    ]
                }                
            ]
        }        
    ],
    "models" : {
      "Project" : <#include "/apidocs/json_schema_project.ftl" >  ,
      "Task" : <#include "/apidocs/json_schema_task.ftl" >  	  
    },    
    <#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}