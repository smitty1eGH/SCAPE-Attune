<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Config Dashbaord</title>

<style>
/** CONFIG **/
#config-container {
	margin-right: 0px;
	margin-left: 0px;
	margin-top: 20px;
	width: 100%;
	height: 100%;
	padding: 0px 15px 5px 15px;
}

.config-panel {
	background-color: #f1f1ef;
	padding-top: 2px;
	padding-left: 25px;
	padding-right:25px;
	margin-top: 10px;
	margin-bottom: 20px;
}

.config-tile {
	margin-bottom: 25px;
}

.config-tile-header {
	display: table;
	padding: 5px 0px;
	margin-bottom: 20px;
	width: 100%;
	clear: both;
	text-align: center;
}

.config-tile-header form {
	display: table-cell;
	vertical-align: middle;
}

.config-tile-header form div.form-group {
	padding-right: 15px;
	padding-left: 15px;
	width: 100%;
	padding-top: 5px;
	padding-bottom: 5px;
	margin-bottom: 0px;
}

.config-tile-header form div.form-group label {
	width: 40%;
	float: left;
	margin-top: 5px;
	font-size: 17px;
	text-align: center;
	color: #000;
}

.config-tile-header form div.form-group select {
	width: 100%;
	font-size: 18px;
	background-color: inherit;
	border: 0;
	box-shadow: none;
}

.config-tile-content {
	font-size: 15px;
	border-left: 1px solid #cbccce;
	border-right: 1px solid #cbccce;
	border-bottom: 1px solid #cbccce;
}

.config-tile-content-options {
	padding: 15px;
}

.config-tile-content-options label {
	font-size: 18px;
}

.config-tile-content-options .col-lg-8 {
	width: 100%;
}

.config-tile-content-options select {
	font-size: 18px;
	background-color: inherit;
	border: 0;
}

.config-tile-content-options .form-control:focus {
	border: 0;
	box-shadow: none;
}

.selectionBox {
	border: 1px solid #ccc;
	margin-right: 0px !important;
	margin-left: 0px !important;
	padding: 5px 0px;
}

.alert-check-container {
	display: table;
}

.alert-check-value-container {
	display: table-cell;
    width: 4.5em;
}

.alert-check-value {
	width: 4.5em; 
	margin-right: 5px; 
	vertical-align: middle; 
	border: 1px solid #ccc !important;
}

.alert-check-value-input-container {
	display: table-cell; 
	vertical-align: middle; 
	position: relative;
}

.alertValue {
	background: transparent !important; 
	border: 1px solid #ccc !important; 
	font-size: 18px;
}

div.form-group span.form-control-feedback {
	display: none;
}

div.form-group.has-error span.form-control-feedback {
	display: block;
}

#configDashboardTabContent {
	height: 28em;
	overflow-x: none;
}

#configDashboardTabContent .panel {
	padding: 25px;
	background-color: #f8f8f7;
	border: 1px solid #e3e3e3;
}

#configDashboardTabContent .panel-heading {
	background: #eaeaea;
	color: #555;
	font-size: 16px;
}

#apply {
	color: #666;
	float: right;
	font-size: 16px;
	height: 4em;
	width: 120px;
	background-color: #f1f1ef;
	border: 1px solid #cac9c9;
	margin-top: -7px;
}

#cancel {
	color: #666;
	float: right;
	font-size: 16px;
	height: 4em;
	width: 120px;
	margin-right: 15px;
	background-color: #f1f1ef;
	border: 1px solid #cac9c9;
	margin-top: -7px;
}

@media ( max-width : 992px) {
	#selectAndOrderTab {
		width: 100%;
	}
}

@media (max-width: 1024px) {
	.config-tile-header label, .config-tile-header select {
		width: 50% !important;
	}
}

@media (max-width: 768px) {
	.config-tile-header label {
		width: 40% !important;
	}
	.config-tile-header select {
		width: 60% !important;
	}
}

h1.page-title-inline {
	font-weight: 100;
	display: inline;
}

.config-panel h3 {
	color: #666;
	margin: 15px 0;
}

#alert-modal .modal-footer button {
	color: white;
}
</style>

<script>
$(document).ready(function()
{  
	var entity1FieldListContainer = document.getElementById("entity1FieldList");
	var entity2FieldListContainer = document.getElementById("entity2FieldList");
	var entity3FieldListContainer = document.getElementById("entity3FieldList");
	
	var entity1Sorter = Sortable.create(entity1FieldListContainer, {
		animation: 250,
		draggable: 'li',
		scrollSensitivity: 15,
		scrollSpeed: 5,
	}); 
	var entity2Sorter = Sortable.create(entity2FieldListContainer, {
		animation: 250,
		draggable: 'li',
		scrollSensitivity: 15,
		scrollSpeed: 5,
	}); 
	var entity3Sorter = Sortable.create(entity3FieldListContainer, {
		animation: 250,
		draggable: 'li',
		scrollSensitivity: 15,
		scrollSpeed: 5,
	}); 
	
	$("[name='entitySelector']").change(function()
	{		
		target = $(this).attr('target');
		$("." + target).html($(this).val());
		
		$("#" + target + "GroupByList").empty();
		$("#" + target + "FieldList").empty();
		
		$.ajaxSetup({async: false});
		
		var entityType = $(this).val();

		$.getJSON("getFieldList.exec", {entity: entityType}, function(list)
		{	
			$.each(list, function(index) 
			{
				$("#" + target + "GroupByList").append
				(
				  	'<option value="' + list[index] + '">'+ list[index] + '</option>'
			    );	
				
				$("#" + target + "AlertList").append
				(
				  	'<option value="' + list[index] + '">'+ list[index] + '</option>'
			    );	
				
				$("#" + target + "FieldList").append
				(
					"<li class='list-group-item'>" + 
						"<div class='roundedCheckbox'>" + 
							"<input type='checkbox' checked id='roundedCheckbox${dashboardEntity.displayList}" + list[index] + "' " + "name='showAlert' style='visibility: hidden;'/>" + 
							"<label for='roundedCheckbox${dashboardEntity.displayList}" + list[index] +"' " + "></label>" +
							"<span class='roundedChkboxLabel'>" + list[index] + "</span>" +
						"</div>" +
						"<button type='button' class='navbar-toggle'>" +
							"<span class='icon-bar base-color4'></span>" + 
							"<span class='icon-bar base-color4'></span>" +
							"<span class='icon-bar base-color4'></span>" +
						"</button>" +
					"</li>"
			    );				    

			    var tile = $('#config-tile-content');
			    tile.removeClass();
			    tile.addClass('config-tile-content');
			    tile.addClass('top-border-accent-color-' + entityType);
			});
		});	


		$("#" + target + "AlertList").empty();		
		$.getJSON("getValueList.exec", {entity: $(this).val(), field: $("#" + target + "GroupByList").val()}, function(list)
		{	
			$.each(list, function(index) 
			{
				$("#" + target + "AlertList").append
				(
				  	'<option value="' + list[index] + '">'+ list[index] + '</option>'
			    );	
			});			
			
			$("#" + target + "AlertList").editableSelect();
		});	
	});
	
	$('.alert-check-container').click(function()
	{
		$(this).find('.form-group').removeClass('has-error');
	});
		
	$("#apply").click(function()
	{		
		// check if entites are unique
		if ( $('#entity1').val() == $('#entity2').val() || 
			 $('#entity1').val() == $('#entity3').val() || 
		     $('#entity2').val() == $('#entity3').val() )
		{
			$('#alert-modal').modal('show');
			$('#unique-error').show();
			return false;
		}
		
		// check if all alert values are set
		missingAlertValue = false;
		$.each($('[name="showAlert"]'), function()
		{
			if ( $(this).is(':checked' ))
			{
				var alertValueElement = $(this).closest('.form-group').next().find(".alertValue");
				if ( alertValueElement.val() == '' )
				{
					alertValueElement.parent().addClass('has-error');
					$('#alert-modal').modal('show');
					$('#alert-error').show();
					missingAlertValue = true;
				}
			}
		});
					
		if ( missingAlertValue )
			return false;
		
		// collect data, fill the index=0 with empty strings to sync index
		var list = [];	
		list.push("", "", "", "", "", "");
		 
		for (i=1; i<=3; i++)
		{	
			list.push($('#entity' + i).val());
			list.push($('#entity' + i + 'ChartTypeList').val());
			list.push($('#entity' + i + 'GroupByList').val());
			
			if ( $('#roundedCheckbox' + i).is(':checked') )
			{
				list.push($("#entity" + i + "AlertCheck").val());
				list.push($("#entity" + i + "AlertList").val());				
			}
			else
			{
				list.push("");
				list.push("");				
			}
	
			var fieldList = "";
			$.each($('#entity' + i + 'FieldList input:checkbox:checked'), function()
			{
				fieldList += $(this).parent().text() + ",";
			});
		
			fieldList = fieldList.substring(0, fieldList.length-1);
			
			list.push(fieldList);
		}
		
		$.ajax(
		{
			url: "saveDashboard.exec",
			type: 'POST',
			dataType: 'json',
			contentType:'application/json',
			data:JSON.stringify(list),

			success: function(data, textStatus )
			{
				$('#main').load('dashboard.exec',function()
				{
					  $('#main').fadeIn();	
				});
			},
			error: function(xhr, textStatus, error)
			{
				alert(error);
			}
			
		});
		
		// Remove the editable selector elements that cause height issues on other pages
		$('.es-list').remove();

	});
	
	
	$("#cancel").click(function()
	{
		$('#main').load('dashboard.exec',function(response, status, xhr )
		{
			// Remove the editable selector elements that cause height issues on other pages
			$('.es-list').remove();
			$('#main').fadeIn();	
		});
	});
	
	$('[name="showAlert"]').change(function()
	{
		if ($(this).is(':checked'))
			$('#' + $(this).attr('target')).css('visibility', 'visible');
		else
			$('#' + $(this).attr('target')).css('visibility', 'hidden');
	});
	
	var x = $('#entity1AlertList').editableSelect();
	var y = $('#entity2AlertList').editableSelect();
	var z = $('#entity3AlertList').editableSelect();
	
	<c:forEach items="${dashboardEntityList}" var="dashboardEntity" varStatus="status">	
		<c:if test="${dashboardEntity.alertValue != ''}">
			$("#entity${status.index+1}AlertCheckbox").prop( "checked", true );
			$("#entity${status.index+1}Alert").css('visibility', 'visible');
		</c:if>
	</c:forEach>
	
	$('[name="groupByField"]').change(function()
	{
		target = $(this).attr('target');
		$("#" + target + "AlertList").empty();
		
		$.getJSON("getValueList.exec", {entity: $("#" + target).val(), field: $("#" + target + "GroupByList").val()}, function(list)
		{	
			$.each(list, function(index) 
			{
				$("#" + target + "AlertList").append
				(
				  	'<option value="' + list[index] + '">'+ list[index] + '</option>'
			    );	
			});			
			
			$("#" + target + "AlertList").editableSelect();
		});			
	});
	
	// Hide both errors in modal
	$('#alert-modal').on('hide.bs.modal', function()
	{
		$('#alert-error').hide();
		$('#unique-error').hide();
	});

});	
</script>
</head>

<body>
	<!-- ---------------------------------------------------- Config Page ----------------------------------------------------------->
	<div id="config-container">
		<div class="row">
			<div class="col-md-12">
				<h1 class="page-title-inline">Dashboard Settings</h1>
				<button type="button" id="apply">Apply</button>
				<button type="button" id="cancel">Cancel</button>
			</div>
		</div>
	
		<div class="config-panel">
			<h3>Select the Entities to Personalize Your Dashboard</h3>
			<!-- Top row, contains entity and graph selectors -->
			<div class="row">
				<c:forEach items="${dashboardEntityList}" var="dashboardEntity" varStatus="status">
				<c:if test="${status.index lt 3}">
					<!-- Entity tile, allows selection of entity and it's graphs -->
					<div class="col-sm-4 config-tile">
						<!-- Header of entity tile, user can select entity -->
						<div class="config-tile-header header-color">
							<form class="form-inline">
								<div class="form-group">
									<select id="entity${status.index+1}" target="entity${status.index+1}" name="entitySelector" class="form-control">
										<c:forEach items="${allEntityList}" var="allEntity">
											<option value="${allEntity.name}"
												<c:if test="${allEntity.name == dashboardEntity.entity}">selected</c:if>>${allEntity.name}</option>
										</c:forEach>
									</select>
								</div>
							</form>
						</div>

						<!-- Content of the entity tile, user can select entity's graph -->
						<div id="config-tile-content" class="config-tile-content  top-border-accent-color-${dashboardEntity.entity}">
							<div class="config-tile-content-options">
								<form class="form-horizontal">
									<div class="form-group selectionBox">
										<div class="col-lg-8">
											<select class="form-control" id="entity${status.index+1}ChartTypeList">
												<c:forEach items="${allChartTypeList}" var="chartType">
													<option value="${chartType}"
														<c:if test="${chartType == dashboardEntity.chartType}">selected</c:if>>${chartType}</option>
												</c:forEach>
											</select>
										</div>
									</div>
									<div class="form-group selectionBox">
										<div class="col-lg-8">
											<select name="groupByField" target="entity${status.index+1}" class="form-control" id="entity${status.index+1}GroupByList">
												<c:forEach items="${dashboardEntity.fieldList}" var="property">
													<option value="${property}"
														<c:if test="${property == dashboardEntity.groupBy}">selected</c:if>>${property}</option>
												</c:forEach>
											</select>
										</div>
									</div>
									<div class="form-group">
										<div class="col-sm-6">
											<div class="roundedCheckbox">
												<input ${dashboardEntity.alertValue != '' ? 'checked' : ''} type="checkbox" value="None" id="roundedCheckbox${status.index+1}" target="entity${status.index+1}Alert" name="showAlert" style="visibility: hidden;"/>
												<label for="roundedCheckbox${status.index+1}"></label>
												<span class="roundedChkboxLabel">Show Alert</span>
											</div>
										</div>
									</div>
									<div class="alert-check-container" id="entity${status.index+1}Alert" style="visibility: hidden; width: 100%;">
										<div class="alert-check-value-container">
											<select id="entity${status.index+1}AlertCheck" class="form-control alert-check-value">
												<option
													<c:if test="${dashboardEntity.alertCheck == '='}">selected</c:if>>=</option>
												<option
													<c:if test="${dashboardEntity.alertCheck == '>'}">selected</c:if>>></option>
												<option
													<c:if test="${dashboardEntity.alertCheck == '<'}">selected</c:if>><</option>
											</select>
										</div>
										<div class="form-group alert-check-value-input-container">
											<select class="form-control alertValue" id="entity${status.index+1}AlertList">
												<c:set var="entityAlertValueFound" scope="session" value="false" />
												<c:forEach items="${dashboardEntity.fieldValueList}" var="property">
													<option value="${property}"
														<c:if test="${property == dashboardEntity.alertValue}">
								  						<c:set var="entityAlertValueFound" scope="session" value="true"/>selected</c:if>>${property}
								  					</option>
												</c:forEach>
												<c:if test="${ !entityAlertValueFound }">
													<option value="${dashboardEntity.alertValue}" selected>${dashboardEntity.alertValue}</option>
												</c:if>
											</select> 
											<span class="glyphicon glyphicon-remove form-control-feedback" aria-hidden="true"></span>
										</div>
									</div>
								</form>
							</div>
						</div>
					</div>
                </c:if>
				</c:forEach>
			</div>
		</div>

		<!-------------------------- Bottom row, contains editable list of each entity's fields -------------------------->
		<div class="config-panel">
			<div class="row">
				<div class="col-sm-12">
					<h3>Select and Order Display Fields</h3>
					<div class="tab-content" id="configDashboardTabContent">
						<div role="tabpanel" class="tab-pane fade in active">
							<div class="row">
								<c:forEach items="${dashboardEntityList}" var="dashboardEntity" varStatus="status">
									<div class="col-sm-4">
										<div class="panel top-border-accent-color-${dashboardEntity.entity}">
											<div id="entity${status.index+1}FieldListTitle" class="panel-heading">
												${dashboardEntity.entity}
											</div>
											<ul id="entity${status.index+1}FieldList" class="list-group base-color1">
												<c:forEach items="${dashboardEntity.displayList}" var="property">
													<li class="list-group-item">								
														<div class="roundedCheckbox">
															<input type="checkbox" checked id="roundedCheckbox${dashboardEntity.displayList}${property}" style="visibility: hidden;"/>
															<label for="roundedCheckbox${dashboardEntity.displayList}${property}"></label>
															<span class="roundedChkboxLabel">${property}</span>
														</div>													
														<button type="button" class="navbar-toggle">
															<span class="icon-bar base-color4"></span> <span
																class="icon-bar base-color4"></span> <span
																class="icon-bar base-color4"></span>
														</button>
													</li>
												</c:forEach>
												<c:forEach items="${dashboardEntity.nonDisplayList}" var="property">
													<li class="list-group-item">
														<div class="roundedCheckbox">
															<input type="checkbox" unchecked id="roundedCheckbox${dashboardEntity.nonDisplayList}${property}" style="visibility: hidden;"/>
															<label for="roundedCheckbox${dashboardEntity.nonDisplayList}${property}"></label>
															<span class="roundedChkboxLabel">${property}</span>
														</div>	
														<button type="button" class="navbar-toggle">
															<span class="icon-bar base-color1"></span> <span class="icon-bar base-color1"></span> <span class="icon-bar base-color1"></span>
														</button>
													</li>
												</c:forEach>
											</ul>
										</div>
									</div>
								</c:forEach>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- Validation Alert -->
	<div class="modal fade" id="alert-modal" role="dialog">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&#x2573;</span>
					</button>
					<h4 class="modal-title">Alert</h4>
				</div>
				<div class="modal-body base-colorALT1">
					<p id="alert-error" style="display: none;">You must set an
						alert value before saving. Please set an alert value.</p>
					<p id="unique-error" style="display: none;">All entities must
						be unique. Please select unique entities.</p>
				</div>
				<div class="modal-footer">
					<button type="button" class="accent-color2" data-dismiss="modal">OK</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->

	</div>
</body>
</html>