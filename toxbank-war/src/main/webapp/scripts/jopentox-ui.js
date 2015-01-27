/**
 * jOpenTox JavaScript Library v0.0.1 (alpha)
 * 
 * Copyright 2012-2013, IDEAconsult Ltd.
 * http://www.ideaconsult.net/
 * 
 * 
 */

function getMediaLink(uri, media) {
	return uri + "?media=" + encodeURIComponent(media);
}

function getDownloadLinksCompound(root,uri) {
	   val = uri;
	   var sOut = "<a href='"+getMediaLink(val,"chemical/x-mdl-sdfile")+"' id='sdf'><img src='"+root+"/images/sdf.jpg' alt='SDF' title='Download as SDF' ></a> ";
	   sOut += "<a href='"+getMediaLink(val,"text/csv")+"' id='csv'><img src='"+root+"/images/excel.png' alt='CSV' title='Download as CSV (Comma delimited file)'></a> ";
	   sOut += "<a href='"+getMediaLink(val,"text/plain")+"' id='txt'><img src='"+root+"/images/excel.png' alt='TXT' title='Download as TXT'></a> ";
	   sOut += "<a href='"+getMediaLink(val,"chemical/x-cml")+"' id='cml'><img src='"+root+"/images/cml.jpg' alt='CML' title='Download as CML (Chemical Markup Language)'></a> ";
	   sOut += "<a href='"+getMediaLink(val,"chemical/x-daylight-smiles")+"' id='smiles'><img src='"+root+"/images/smi.png' alt='SMILES' title='Download as SMILES'></a> ";
	   sOut += "<a href='"+getMediaLink(val,"chemical/x-inchi")+"' id='inchi'><img src='"+root+"/images/inchi.png' alt='InChI' title='Download as InChI'></a> ";
	   sOut += "<a href='"+getMediaLink(val,"text/x-arff")+"' id='arff'><img src='"+root+"/images/weka.png' alt='ARFF' title='Download as ARFF (Weka machine learning library I/O format)'></a> ";
	   sOut += "<a href='"+getMediaLink(val,"text/x-arff-3col")+"' id='arff3col'><img src='"+root+"/images/weka.png' alt='ARFF' title='Download as ARFF (Weka machine learning library I/O format)'></a> ";
	   sOut += "<a href='"+getMediaLink(val,"application/rdf+xml")+"' id='rdfxml'><img src='"+root+"/images/rdf.gif' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'></a> ";
	   sOut += "<a href='"+getMediaLink(val,"text/n3")+"' id='rdfn3'><img src='"+root+"/images/rdf.gif' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'></a> ";
	   sOut += "<a href='"+getMediaLink(val,"application/json")+"' id='json' target=_blank><img src='"+root+"/images/json.png' alt='json' title='Download as JSON'></a>";
	   return sOut;
}


/**
 * Autocomplete for feature URI field
 * @param id
 */
function featureAutocomplete(id,iddataset,featureroot,maxhits) {
	$( id ).autocomplete({
	      source: function( request, response ) {
	    	  var featureLookup = featureroot;
	    	  $(iddataset).each(function(a,b) {
	    		 try {  
	    			 var datauri = $(b).attr('value');
	    			 if ((datauri != undefined) && ("" != datauri)) { 
	    				 featureLookup=$(b).attr('value')+"/feature"; maxhits=0;
	    			 }
	    		 } catch (err) {featureLookup = featureroot;} 
	    	  });
	          $.ajax({
	            url: featureLookup,
	            dataType: "json",
	            data: {
	              media:"application/json",
	              max: maxhits,
	              search: "^"+request.term
	            },
	            success: function( data ) {
	              response( $.map( data.feature, function( item , index) {
			        var shortURI = index;
			        pos =  shortURI.lastIndexOf("/");
			        if (pos>=0) shortURI = "F" + shortURI.substring(pos+1) + ": "; 
			        else shortURI = "";
			        
	                return {
	                  label: shortURI + item.title + " " + item.units,
	                  value: index
	                }
	              }));
	            }
	          });
	        },
	        minLength: 1,
	        open: function() {
	        	  $('.ui-autocomplete').css('width', '450px');
		    }       
	});
}

/**
 * Lists OpenTox Features, as in /feature
 * @param root
 * @param url
 * @returns
 */
function defineProtocolsTable(root,url) {

	var oTable = $('#protocols').dataTable( {
		"sAjaxDataProp" : "protocols",
		"bProcessing": true,
		"bServerSide": false,
		"bStateSave": false,
		"aoColumnDefs": [
				{ "mDataProp": "identifier" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 0 ],	
					  "sWidth" : "10%",
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {

		                return "<a href='"+o.aData.uri +"' title='Click to view the protocol at " + 
		                		o.aData.uri+" '><span class='ui-icon ui-icon-link' style='float: left; margin: .1em;' ></span>" +
		                		val + "</a>";
					  }
				},
				
				{ "mDataProp": "title" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [1 ],	
					  "bSearchable" : true,
					  "sWidth" : "15%",
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
						  var sOut = "<span style='font-weight:bold'>" + val + "</span>";
						  sOut += "<br/>"
						  sOut += "<span style='font-style:italic'>Published: </span>";
						  sOut += o.aData.published?"Yes":"No";
						  sOut += "<br/>"						  
						  sOut += "<a href='"+o.aData.uri +"/document?media=application%2Fpdf' target='_download' title='Click to download the document' >Download</a>";
						  return sOut;
					  }
				},
				{ "mDataProp": "status" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [2 ],
					  "sWidth" : "5%",
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
						  var sOut = val + "<br/><br/>";
						  try {
							  if (o.aData.owner["uri"]!=null)
							  sOut += " <a href='"+o.aData.owner["uri"] + "' target='details'>Owner</a>";
						  } catch (err) {}
						  return sOut;
					  }
				},				
				{ "mDataProp": "abstract" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [3 ],	
					  "bSearchable" : true,
					  "sWidth" : "50%",
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
						  return val;
					  }
				},				
				{ "mDataProp": "project" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [4 ],	
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
						  var sOut = "";
						  try {
							  if (o.aData.project["uri"]!=null)
							  sOut += "<a href='"+o.aData.project["uri"] + "' target='details'>"+o.aData.project["name"]+"</a><br/>";
						  } catch (err) {}
				  
						  return sOut;
					  }
				},		
				{ "mDataProp": "organisation" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [5 ],	
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
						  var sOut = "";
						  try {
							  if (o.aData.organisation["uri"]!=null)
							  sOut += "<a href='"+o.aData.organisation["uri"] + "' target='details'>"+o.aData.organisation["name"]+"</a></br/>";
						  } catch (err) {}						  
						  return sOut;
					  }
				},						
				{ "mDataProp": "updated" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [6 ],	
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
						  var date = new Date(val);

						  return date.toDateString();
					  }
				},					


			],
		"sSearch": "Filter:",
		"bJQueryUI" : true,
		"bSearchable": true,
		"sAjaxSource": url,
		"sDom" : '<"help remove-bottom"i><"help"f>Trt<"help"lp>',
		"bPaginate" : true,
		"sPaginationType": "full_numbers",
		"sPaginate" : ".dataTables_paginate _paging",
		"oLanguage": {
	            "sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
	            "sLoadingRecords": "No protocols found.",
	            "sZeroRecords": "No protocols found.",
	            "sInfo": "Showing _TOTAL_ protocols (_START_ to _END_)",
	            "sLengthMenu": 'Display <select>' +
              '<option value="10">10</option>' +
              '<option value="20">20</option>' +
              '<option value="50">50</option>' +
              '<option value="100">100</option>' +
              '<option value="-1">all</option>' +
              '</select> protocols.'	            
	    }
	     
	} );
	return oTable;
}

function boolean2checkbox(val,yes,no) {
	var istrue = (val.toLowerCase() === 'true');
	return "<input type='checkbox' "+ (istrue?"checked":"") + " title="  + (istrue?yes:no) + " disabled> " + (istrue?yes:no) ;
}
/**
 * Features list box
 * @param root
 * @param selectTag
 * @param allelesTag
 */
function loadFeatureList(featureLookup,selectTag,maxhits) {
	  //clear the list	
  //$(selectTag).html("");
	  //get all features
  
  $.ajax({
      url: featureLookup,
      dataType: "json",
      data: {
        media:"application/json",
        max: maxhits
      },
      success: function( data ) {
    	  $.map( data.feature, function( item , index) {
    		  $("<option value='" + index + "'>" + item.title + "&nbsp;" +item.units + "</option>").appendTo(selectTag);
        	  return item.title;
          }
        );
      }
    });
  
}	

function downloadForm(query_uri) {
	$.each(_ambit.downloads,function(index,value) {
		var durl = query_uri +  ((query_uri.indexOf("?")<0)?"?":"&") + "media="+ encodeURIComponent(value.mime);
		$('#download #'+value.id).attr('href',durl);
	});
}

function defineFacetsTable(root,url,selector) {
	var oTable = $(selector).dataTable( {
		"sAjaxDataProp" : "facet",
		"bProcessing": true,
		"bServerSide": false,
		"bStateSave": false,
		"aoColumnDefs": [
 				{ "mData": "value" , 
 				  "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 0 ],	
				  "sWidth" : "45%",
				  "bSearchable" : true,
				  "bUseRendered" : false,
				  "bSortable" : true,
				  "fnRender" : function(o,val) {
					  var sOut = (o.aData["value"]===undefined)? o.aData["uri"]:o.aData["value"];
					  return "<a href='"+o.aData["uri"]+"' title='"+o.aData["uri"]+"'>"+sOut+"</a>";
				  }
				},
 				{ "mData": "subcategory" , 
	 				  "asSorting": [ "asc", "desc" ],
					  "aTargets": [1 ],
					  "sWidth" : "45%",
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
						  return val;
					  }
					},				
				{ "mDataProp": "count" , "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 2 ],
				  "sWidth" : "10%",
				  "bSearchable" : true,
				  "bSortable" : true
				}			
			],
		"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',	
		"bJQueryUI" : true,
		"bPaginate" : true,
		"sPaginationType": "full_numbers",
		"sPaginate" : ".dataTables_paginate _paging",
		"bDeferRender": true,
		"bSearchable": true,
		"sAjaxSource": url,
		"oLanguage": {
				"sSearch": "Filter:",
				"sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
	            "sLoadingRecords": "No records found."
	    }
	} );
	return oTable;
}



function definePolicyTable(root,url,selector) {
	var oTable = $(selector).dataTable( {
		"sAjaxDataProp" : "policy",
		"bProcessing": true,
		"bServerSide": false,
		"bStateSave": false,
		"aoColumnDefs": [
 				{ "mData": "uri" , 
 				  "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 0 ],	
				  "bSearchable" : true,
				  "bUseRendered" : false,
				  "bSortable" : true,
				  "fnRender" : function(o,val) {
					  return val;
				  }
				},
 				{ "mData": "id" , 
	 				  "asSorting": [ "asc", "desc" ],
					  "aTargets": [1 ],
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
						  return "<a href='"+root+"/admin/policy/"+ encodeURIComponent(val)+"'>"+val + "</a>";
					  }
					},				
				{ "mDataProp": "xml" ,
				  "aTargets": [ 2 ],
				  "sWidth" : "10%",
				  "bSortable" : false,
				  "bSearchable" : true
				},
				{ "mData": null , 
	 				  "asSorting": [ "asc", "desc" ],
					  "aTargets": [3 ],
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
						  return "<form action='"+root+"/admin/policy/"+o.aData['id']+"?method=DELETE' method='POST'><input type='submit' value='Delete policy'></a>";
					  }
					}					
			],
		"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',	
		"bJQueryUI" : true,
		"bPaginate" : true,
		"sPaginationType": "full_numbers",
		"sPaginate" : ".dataTables_paginate _paging",
		"bDeferRender": true,
		"bSearchable": true,
		"sAjaxSource": url,
		"oLanguage": {
				"sSearch": "Filter:",
				"sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
	            "sLoadingRecords": "No policies found."
	    }
	} );
	return oTable;
}

function defineRolesTable(root,url,selector) {
	var oTable = $(selector).dataTable( {
		"sAjaxDataProp" : "roles",
		"bProcessing": true,
		"bServerSide": false,
		"bStateSave": false,
		"aoColumnDefs": [
 				{ 
 				  "mData": "role" ,  						
 				  "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 0 ],	
				  "bSearchable" : true,
				  "bUseRendered" : false,
				  "bSortable" : true,
				  "fnRender" : function(o,val) {
					  return val;
				  }
				}
 								
			],
		"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',	
		"bJQueryUI" : true,
		"bPaginate" : true,
		"sPaginationType": "full_numbers",
		"sPaginate" : ".dataTables_paginate _paging",
		"bDeferRender": true,
		"bSearchable": true,
		"sAjaxSource": url,
		"oLanguage": {
				"sSearch": "Filter:",
				"sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
	            "sLoadingRecords": "No roles found."
	    },
	    "fnServerData" : function(sSource, aoData, fnCallback,oSettings) {
			oSettings.jqXHR = $.ajax({
				"type" : "GET",
				"url" : sSource,
				"data" : aoData,
				"dataType" : "json",
				"contentType" : "application/json",
				"cache" : true,
				"success": function(result) {
						var roles = $.map( result.roles, function( value,key ) {
							 	return {"role" : value};
							});
						console.log(roles);
						fnCallback({"roles" : roles});
				},
				"error" : function(xhr, textStatus, error) {
					switch (xhr.status) {
					case 403: {
			        	alert("Restricted access. You are not authorized to access the requested algorithms.");
						break;
					}
					case 404: {
						//not found
						break;
					}
					default: {
						//console.log(xhr.status + " " + xhr.statusText + " " + xhr.responseText);
			        	alert("Error loading algorithms " + xhr.status + " " + error);
					}
					}
					oSettings.oApi._fnProcessingDisplay(oSettings, false);
				}
			});
	    }
	} );
	return oTable;
}


function loadMetadata(root,dataseturi) {
$.ajax({
    url: dataseturi,
    dataType: "json",
    data: {
      media:"application/json",
      max: maxhits
    },
    success: function( data ) {
  	  
    }
  });

}	

