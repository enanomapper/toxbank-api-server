{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "text/n3",
        "application/rdf+xml",
        "text/uri-list"
    ],
    "resourcePath": "/protocol",
    "apis": [
        {
            "path": "/protocol",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Returns all protocols",
                    "notes": "Returns all protocols <a href='http://api.toxbank.net/index.php/Protocol#GET:_Retrieve_the_list_of_Protocols' target='opentox'>Protocol API</a>",
                    "type": "Protocol",
                    "nickname": "getAllProtocols",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
                        {
                            "name": "search",
                            "description": "Protocol name",
                            "required": false,
                            "type": "string",
                            "paramType": "query",
                            "allowMultiple": false
                        },		
                        	                                    {
                            "name": "modifiedSince",
                            "description": "timestamp",
                            "required": false,
                            "type": "long",
                            "paramType": "query",
                            "allowMultiple": false
                        },
						<#include "/apidocs/parameters_page.ftl" >	                        
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Protocol not found"
                        },
						<#include "/apidocs/error_500.ftl" >	                        
                    ]
                }
            ]
        },
 		{
            "path": "/protocol/{id}",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Retrieve Metadata of a single Protocol",
                    "notes": "Retrieve Metadata of a single Protocol <a href='http://opentox.org/dev/apis/api-1.2/AsyncTask' target='opentox'>Protocol API</a>",
                    "type": "Task",
                    "nickname": "getProtocolID",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
                        {
                            "name": "id",
                            "description": "Protocol identifier",
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
                    "summary": "Retrieve Metadata of a single Protocol",
                    "notes": "Retrieve Metadata of a single Protocol <a href='http://opentox.org/dev/apis/api-1.2/AsyncTask' target='opentox'>Protocol API</a>",
                    "type": "Task",
                    "nickname": "uploadProtocol",
					"consumes": [
		                       "multipart/form-data"
		                ],			                    
     				 <#include "/apidocs/authz.ftl" >
                    "parameters": [
							{
							    "name": "filename",
							    "description": "File to upload",
							    "required": true,
							    "type": "File",
							    "paramType": "form",
							    "allowMultiple": true
							},
							{
							    "name": "title",
							    "description": "title",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false
							},	
							                   							{
							    "name": "anabstract",
							    "description": "abstract",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false
							},
							{
							    "name": "author_uri",
							    "description": "author_uri",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							     "defaultValue" : "${ambit_root}/user/U1",
							    "allowMultiple": true
							},
							{
							    "name": "keywords",
							    "description": "keywords",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": true
							},
							{
							    "name": "summarySearchable",
							    "description": "summarySearchable",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false
							},		
							{
							    "name": "project_uri",
							    "description": "project_uri",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							     "defaultValue" : "${ambit_root}/project/G1",
							    "allowMultiple": true
							},	
							{
							    "name": "organisation_uri",
							    "description": "organisation_uri",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "defaultValue" : "${ambit_root}/organisation/G1",
							    "allowMultiple": false
							},	
							{
							    "name": "user_uri",
							    "description": "user_uri",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "defaultValue" : "${ambit_root}/user/U1",
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
                }                
            ]
        }        
    ],
    "models" : {
      "Protocol" : <#include "/apidocs/json_schema_protocol.ftl" >  ,
      "Task" : <#include "/apidocs/json_schema_task.ftl" >  	  
    },    
    <#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}