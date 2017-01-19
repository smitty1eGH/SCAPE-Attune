<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Timeline</title>

<style>
#timelineview-container {
	margin-right: 0px;
	margin-left: 0px;
	margin-top: 20px;
	width: 100%;
}

#select-entity-container {
	background-color: white;
	padding: 15px;
	font-size: 18px;
}

.timeline-header-tile {
	height: 5em;
	display: table;
	width: 100%;
	padding-top: 10px;
	padding-bottom: 10px;
	margin-bottom: 20px;
}

#configLink {
	margin-right: 15px;
}

.timeline-header-tile span {
	position: relative;
    vertical-align: middle;
    font-size: 3.4em;
    color: #252d32;
    white-space: nowrap;
    margin-left: 25px;
}

.timeline-header-tile small {
	position: absolute;
    top: 25%;
    font-size: 50%;
    color: #666;
    padding-left: 15px;
}

#timeline-container {
    margin-bottom: 25px;
}

#downloadTimelineAsPDF:hover {
	transition: all 0.2s ease;
	opacity: 0.5;
	cursor: pointer;
}

#timeline {
	width: 100%;
	height: 22em;
	transition: all 0.35s ease;
}

#timeline .baseballCardLink:hover {
	text-decoration: underline;
	cursor: pointer;
	color: #23527c;
	outline: 0;
}

#timeline .baseballCardLink:active {
	outline: 0;
}

#analysis-container .panel-heading {
	cursor: pointer;
	position: relative;
	background-color: #c8c8c8;
}

#exportTableToExcel:hover {
	transition: all 0.2s ease;
	cursor: pointer;
}

#analysis-content {
	
}

#analysis-report td {
	transition: all 0.2s ease;
}

#analysis-report td.updating {
	font-size: 110%;
	color: white;
	background-color: rgba(22, 80, 129, 0.8);
}

.timeline-button {
	float: right;
	height: 4em;
	width: 120px;
	background-color: #f1f1ef !important;
	border: 1px solid #cac9c9;
	color: #666;
	font-size: 16px;
	margin-top: -7px;
}

h4.timeline-entity-title {
    float: left;
    max-width: 85%;
}

#orderedListContainer {
    overflow-y: scroll;
    overflow-x: hidden;
    max-height: 350px;
}

#orderedList {
	margin: 0;
}
.unfunded {
	background-color: rgba(255, 100, 100, 0.2);
}

.vis-item {
	height: 20px;
}

.vis-range:not(.diamond):after, .vis-range:not(.diamond):before {
    content: ' ';
    position: absolute;
    width: 0;
    height: 0;
    top: 3px;
    border-left: 10px solid transparent;
    border-right: 10px solid transparent;
    border-bottom: 15px solid #666;
}

.vis-range:not(.diamond):before {
	left: -10px;
}

.vis-range:not(.diamond):after {
	right: -10px;
}

.vis-range .vis-item-overflow {
	height: 1px;
    margin-top: 10px;
    background: #666;
}

.diamond {
	z-index: 1;
}

.vis-range.diamond:before, .vis-range.diamond:after {
	content: " ";
    width: 12px;
    height: 12px;
    background: #666;
    position: absolute;
    top: 7px;
    -webkit-transform: rotate(-45deg);
    -moz-transform: rotate(-45deg);
    -ms-transform: rotate(-45deg);
    -o-transform: rotate(-45deg);
    transform: rotate(-45deg);
    -webkit-transform-origin: 0 100%;
    -moz-transform-origin: 0 100%;
    -ms-transform-origin: 0 100%;
    -o-transform-origin: 0 100%;
    transform-origin: 0 100%;
}

.vis-range.diamond:after {
	right: -12px;
}

.vis-point.diamond {
	content: " ";
    width: 12px;
    height: 12px;
    background: #666;
    position: absolute;
    -webkit-transform: rotate(-45deg);
    -moz-transform: rotate(-45deg);
    -ms-transform: rotate(-45deg);
    -o-transform: rotate(-45deg);
    transform: rotate(-45deg);
    -webkit-transform-origin: 0 100%;
    -moz-transform-origin: 0 100%;
    -ms-transform-origin: 0 100%;
    -o-transform-origin: 0 100%;
    transform-origin: 0 100%;
}

.vis-point.diamond.color1, .diamond.color1:before, .diamond.color1:after, .diamond .vis-item-overflow {
	background: #4c85b2;
}

.vis-point.diamond.color2, .diamond.color2:before, .diamond.color2:after, .diamond.color2 .vis-item-overflow {
	background: #9ac440;
}

.vis-point.diamond.color3, .diamond.color3:before, .diamond.color3:after, .diamond.color3 .vis-item-overflow {
	background: #9467bd;
}

.vis-point.diamond.color4, .diamond.color4:before, .diamond.color4:after, .diamond.color4 .vis-item-overflow {
	background: #c4a340;
}

.vis-point.diamond.color5, .diamond.color5:before, .diamond.color5:after, .diamond.color5 .vis-item-overflow {
	background: #BDBD9C;
}

.vis-point.diamond.color6, .diamond.color6:before, .diamond.color6:after, .diamond.color6 .vis-item-overflow {
	background: #dbdb8d;
}

.vis-point.diamond.color7, .diamond.color7:before, .diamond.color7:after, .diamond.color7 .vis-item-overflow {
	background: #17becf;
}

.vis-point.diamond.hasOverlap, .diamond.hasOverlap:before, .diamond.hasOverlap:after, .diamond.hasOverlap .vis-item-overflow {
	background: #990000;
}

.vis-item:hover {
	cursor: pointer;
}
</style>

<script type="text/javascript">
$(document).ready(function() 
{
	var model = new TimelineModel()
	model.init();
	
	// Expand the timeline's height when the analysis panel is collapsed
	$('#analysis-content').on('hide.bs.collapse', function() {
	    $('#timeline').css('height', '34em');
	});
	$('#analysis-content').on('show.bs.collapse', function() {
	    $('#timeline').css('height', '22em');
	});
	
	$('#downloadTimelineAsPDF').click(function() {
		$('#timelineHtml').val($('#timeline').html());
	    $('#timelinePdfForm').submit();
	});
	
	$('#configLink').click(function()
	{
	    $('#configModal').modal('show');
	});
	
	$('#saveLink').click(function() {
		model.saveTimeline(function(success) {
			if (success)
			{
				$('#saveModal .modal-title').text('Save Successful')
				$('#save-success').show();
				$('#save-failure').hide();
			}
			else
			{
				$('#saveModal .modal-title').text('Errors While Saving')
				$('#save-success').hide();
				$('#save-failure').show();
			}
			
			$('#saveModal').modal('show');
		});
	})
	
	$('#timeline').dblclick('.vis-item', function(event)
	{
		if ($(event.target).hasClass('vis-item'))
		{
			var baseballName = event.target.attributes['data-content'].nodeValue;
			var baseballEntity = event.target.attributes['data-entityLabel'].nodeValue;
		 	 
			localStorage.setItem("originalEntity", baseballEntity);
			localStorage.setItem("originalName", baseballName);
			
			$('#baseballCardModal').modal('show');
			
			var url = "baseballCard.exec?entity="+encodeURIComponent(baseballEntity)+"&name="+encodeURIComponent(baseballName);
			
			$("#baseballContent").load(url);
		}
		else if ($(event.target).hasClass('vis-item-overflow'))
		{
			var target = $(event.target).closest('.vis-item');
			var baseballName = $(target).attr('data-content');
			var baseballEntity = $(target).attr('data-entityLabel');
		 	 
			localStorage.setItem("originalEntity", baseballEntity);
			localStorage.setItem("originalName", baseballName);
			
			$('#baseballCardModal').modal('show');
			
			var url = "baseballCard.exec?entity="+encodeURIComponent(baseballEntity)+"&name="+encodeURIComponent(baseballName);
			
			$("#baseballContent").load(url);
		}
	});
	
	$('[data-toggle="tooltip"]').tooltip()
});
</script>
    
</head>

<body>
	<div id="timelineview-container" class="container">
		<div class="row">
			<div class="col-md-12">
				<h1 class="page-title-inline">Timeline</h1>
				<button class="timeline-button" id="saveLink">Save</button>
				<button class="timeline-button" id="configLink" title="Config">Configure</button>
			</div>
		</div>
		
		<div class="row" id="timeline-header" style="margin-top: 10px;">
			<div class="col-md-3">
				<div class="timeline-header-tile base-color3 top-border-accent-color-Dataset">
					<span id="selected-entity-count"></span>
				</div>
			</div>
			<div class="col-md-3">
				<div class="timeline-header-tile base-color3 top-border-accent-color-DataSource">
					<span id="event-count"></span>
				</div>
			</div>
			<div class="col-md-3">
				<div class="timeline-header-tile base-color3 top-border-accent-color-Application">
					<span id="total-budget"></span>
				</div>
			</div>
			<div class="col-md-3">
				<div class="timeline-header-tile base-color3 top-border-accent-color-Service">
					<span id="shortfalls"></span>
				</div>
			</div>
		</div>

		<div class="row">
			<div class="col-md-12">
				<div id="timeline-container" class="panel">
					<div class="panel-heading">
						<h2 class="panel-title">Milestones &amp; Dependencies</h2>
						<button class="panel-header-icon" id="timeline-info" data-toggle="tooltip" data-placement="top" title="Zoom into the timeline with a mouse scroll">
							<span class="glyphicon glyphicon-info-sign"></span>
						</button>
						<a class="panel-header-icon" id="downloadTimelineAsPDF" type="button" data-toggle="tooltip" data-placement="top" title="Download Timeline As PDF">
							<span class="glyphicon glyphicon-download-alt"></span>
						</a>
					</div>
					<div class="panel-body">
						<div id="timeline"></div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="row">
			<div class="col-md-12">
				<div class="panel">
					<div class="panel-heading" data-toggle="collapse" data-target="#analysis-content">
						<h4 class="panel-title">Analysis &amp; Outcomes</h4>
						<button class="panel-header-icon panel-collapse-flip">
							<span class="glyphicon glyphicon-chevron-down"></span>
						</button>
						<a class="panel-header-icon" id="exportTableToExcel" href="#" download="analysis-report.xls" onclick="return ExcellentExport.excel(this, 'analysis-report', 'Report');" data-toggle="tooltip" data-placement="top" title="Export Analysis To Excel">
					 		<span class="glyphicon glyphicon-export"></span>
						</a>
					</div>
					<div id="analysis-content" class="panel-collapse collapse in">
						<div class="panel-body">
							<table id="analysis-report" class="table table-bordered" style="margin-bottom: 0px"></table>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div>
			<form id="timelinePdfForm" action="getTimelinePDF.exec" method="post" target="_blank">
				<input type="hidden" name="html" id="timelineHtml">
			</form>
		</div>
		
		<div class="modal fade" id="addItemModal" role="dialog">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title" align="left">Add New Item</h4>
					</div>
					<div class="modal-body"
						style="background-color: rgba(240, 240, 240, 1)">
						<p>Select whether the item being added is a point or a range, then set the start and end dates (if applicable).</p>
						<div id="addItem-typeSelector" class="btn-group" data-toggle="buttons">
							<label class="btn accent-color1 active"> <input type="radio" name="options" id="addItem-point" autocomplete="off" checked>
							&nbsp;Point
							</label> 
							<label class="btn accent-color1"> <input type="radio" name="options" id="addItem-range" autocomplete="off">
							&nbsp;Range
							</label>
						</div>
						<div class="form-group" style="margin-top: 20px;">
							<label for="addItem-start">Start Date</label>
							<input id="addItem-start" class="form-control">
						</div>
						<div class="form-group" style="display: none;">
							<label for="addItem-end">End Date</label>
							<input id="addItem-end" class="form-control">
						</div>
					</div>
					<div class="modal-footer">
						<button style="color: white" type="button" class="btn accent-color1" data-dismiss="modal">Done</button>
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal-dialog -->
		</div>
		
		
	<!-- Config Modal -->
    <div class="modal fade" id="configModal" role="dialog">
        <div class="modal-dialog">
            <div class="modal-content base-colorALT1" id="configContent" style="width: 500px">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&#x2573;</span>
                    </button>
                    <h4 class="modal-title" align="left">Reorder Timeline Items</h4>
                </div>
                <div class="modal-body" id="configContent">
                    <div id="orderedListContainer">
                        <ul id="orderedList" class="list-group base-color1">
	                    </ul>
	                </div>
                </div>
                <div class="modal-footer">
                    <button class="btn-block apply" id="applyConfigLink" data-dismiss="modal">Apply</button>
                </div>
            </div>
            <!-- /.modal-content -->
        </div>
        <!-- /.modal-dialog -->
    </div>
    
    <!-- Save Message Modal -->
    <div class="modal fade" id="saveModal" role="dialog">
        <div class="modal-dialog">
            <div class="modal-content base-colorALT1">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&#x2573;</span>
                    </button>
                    <h4 class="modal-title" align="left">Save Successful</h4>
                </div>
                <div class="modal-body">
                    <div id="save-success">
                    	<p>Your changes have been saved successfully.</p>
	                </div>
	                <div id="save-failure" style="display: none;">
	                	<p>There was an error while saving your changes.</p>
	                </div>
                </div>
                <div class="modal-footer">
                    <button class="apply" data-dismiss="modal">Ok</button>
                </div>
            </div>
            <!-- /.modal-content -->
        </div>
        <!-- /.modal-dialog -->
    </div>
</div>

</body>
</html>