/**
 * GLOBALS
 */

// Tables
var vulnTableName = "vuln-table-data";
var requestTableName = "comp-request-table-data";
// Div classes
var divRowSelectedName = "row_selected";
var divFilterInfoBoxName = ".filter-info-box";

/**
 * GLOBAL ENDS
 */

/**
 * Joins a smaller array to a larger one
 * @param subList
 * @param masterList
 */
function addToMasterList(subList, masterList)
{	
	for(var i=0; i < subList.length; i++)
	{
		masterList.push(subList[i]);	
	}
}

/**
 * Injects a background-color css parameter into a css class
 * Sets the value of the div as well
 * @param cssBlock
 * @param colorStr
 * @param intValue
 */
function setColorAndValue(cssBlock, colorStr, intValue)
{
	cssBlock.css("background-color", colorStr);
	cssBlock.append(intValue);
}

/**
 * Filters the position of the column for a particular table
 * @param value
 * @param oTable
 */
function filterColumnByValue(pos, cssTableName, cssToHighlite)
{
	// inner text does not work in Mozilla
	var value = cssToHighlite.innerText || cssToHighlite.textContent;
	if(value == null)
		console.log("Unable to determine filter text");
	
	value = value.trim();
	value = value.toUpperCase();
	var oTable = $j(cssTableName).dataTable();
	var oSettings = oTable.fnSettings();	 
	var searchValue = oSettings.aoPreSearchCols[ pos ].sSearch;
	console.log("Previous filter: " + searchValue);
	
	if(searchValue === value)
	{
		oSettings.aoPreSearchCols[ pos ].sSearch = '';
		oTable.fnDraw();
	}
	else
	{
	   	oTable.fnFilter(value, pos);
	}   	
	
	highliteCss(cssToHighlite);
}	  

/**
 * Clears selection for name, version
 * @param cssTableName
 */
function clearFilterSelection(cssTableName)
{
	var oTable = $j(cssTableName).dataTable();
	
	oTable.fnFilter('', 0);
	oTable.fnFilter('', 1);

	$j(function()
  	{
		// Hide the div box
		var filterBoxCss = $j(divFilterInfoBoxName);
		filterBoxCss.hide();
		// Clear selection from approval table
		var approvalTable = $j("#comp-request-table-data").dataTable();
		$j("td."+divRowSelectedName, approvalTable.fnGetNodes()).removeClass(divRowSelectedName);	
  	});
}


/**
 * Filters by version and component name
 * @param compName
 * @param compPos
 * @param versionNumber
 * @param versionPos
 * @param cssTableName
 */
function filterByComponentAndVersion(compName, compPos, versionNumber, versionPos, cssTableName)
{
	var oTable = $j(cssTableName).dataTable();
	var oSettings = oTable.fnSettings();	 
	var compFilter = oSettings.aoPreSearchCols[ compPos ].sSearch;
	var versionFilter = oSettings.aoPreSearchCols[ versionPos ].sSearch;
	
	if(compFilter === compName && versionFilter === versionNumber)
	{
		oSettings.aoPreSearchCols[ compPos ].sSearch = '';
		oSettings.aoPreSearchCols[ versionPos ].sSearch = '';
		oTable.fnDraw();
	}
	else
	{
	   	oTable.fnFilter(compName, compPos);
	   	oTable.fnFilter(versionNumber, versionPos);
	}   
	
}

function highliteCss(cssElement)
{
	var statusWidget = false;
	var vulnWidget = false;
	var highLiteOurCss = cssElement.className;
	
	//	//$j('[class^=status][class$=-title]')
	if(highLiteOurCss.indexOf('status') === 0)
	{
		statusWidget = true;
	}	
	else
		vulnWidget = true;

	// Perform the highlite
	$j(function()
  	{
		// Previously filtered div (if any)
		var prevSelectedDiv = $j('[class$=title-highlite]');
		var prevElement = prevSelectedDiv[0];
		if(prevElement != null)
		{
			// Only remove the highliter from the appropriate widget
			var prevSelectedDiv;
			if(statusWidget)
			{
				prevSelectedDiv = $j('[class^=status][class$=title-highlite]');
	
			}
			else if(vulnWidget)
			{
				prevSelectedDiv = $j('[class^=vuln][class$=title-highlite]');
			}
			prevSelectedDiv.removeClass('title-highlite');	
		}
				
		var newSelectedDiv = $j('.' + highLiteOurCss);
		newSelectedDiv.addClass('title-highlite');
  	});
}


function filterTable(value, pos, cssName)
{
	alert("Filtering: " + value + " name: " + cssName);
}


/**
 * Creates a jquery datatable, sets the table variable into the global window var.
 * @param tableClassName
 * @param fullDataList
 * @param columnData
 * @param defaultSortPos - The defaulted column to sort by
 * @returns
 */
jQuery.fn.createTable = function(tableClassName, dataList, columnData, defaultSortPos)
{
	// Overwrite classes
	$j.fn.dataTableExt.oJUIClasses = 'page-pulldown';
	/**
		 * 	For sDom:
		The following options are allowed:
		'l' - Length changing
		'f' - Filtering input
		't' - The table!
		'i' - Information
		'p' - Pagination
		'r' - pRocessing
		The following constants are allowed:
		'H' - jQueryUI theme "header" classes ('fg-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix')
		'F' - jQueryUI theme "footer" classes ('fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix')
		The following syntax is expected:
		'<' and '>' - div elements
		'<"class" and '>' - div with a class
		'<"#id" and '>' - div with an ID	
	**/

	return $j(document).ready(function() {
		var tableCss = $j(tableClassName);
	    oTable = tableCss.dataTable(
	    {
	    	"iDisplayLength": 25,
	    	
	    	"bAutoWidth": false,
	    	"bFilter": true,
	    	"sScrollY": "95",
	    	//"sScrollXInner": "200%",
	    	//"sScrollX": "100%",
	    	"aaData": dataList,
	    	"aaSorting": [[defaultSortPos, "asc"]], 
	    	"aLengthMenu": [25, 50, 100, 500],
	    	"bJQueryUI": true,    
	   		"sDom":'trlpi',	    
	
	        "aoColumns": columnData
	        
	    });		
	    
	    // Tooltip
		 oTable.$('td').smallipop(
			{
		        popupYOffset: 5, // Bubble has a 20px vertical offset
		        popupDistance: 2, // Bubble travels vertically 
		        popupOffset: 0, // No horizontal offset
		        theme: 'blue fat-shadow',
		        preferredPosition: "right",
		        popupAnimationSpeed: "100"
		    });	 

	});
}

