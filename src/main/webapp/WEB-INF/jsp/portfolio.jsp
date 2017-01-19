<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page import="java.util.List, com.bah.attune.data.PortfolioBean"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Portfolio</title>
<style>
/** Main container **/
#portfolio-container {
	margin-right: 0px;
	margin-left: 0px;
	margin-top: 20px;
	width: 100%;
}

#portfolio-header-container {
	padding: 10px 10px 10px 25px;
}

#portfolio-header-container label {
	color: #666;
	font-size: 18px;
	font-weight: normal;
	text-align: center;
}

#entity-selector-container {
	padding: 15px;
}

#entity-selector-container label {
	padding-top: 5px;
	color: #666;
	font-size: 18px;
}

#entity-selector-container select {
	font-size: 16px;
}

#entity-selector-container .form-group {
	margin-bottom: 0px;
	margin-top: 10px;
}

#compareLink.comparing {
	opacity: 0.75;
}

.entity-ribbon-tile {
	display: table;
	width: 100%;
	padding-top: 20px;
	padding-bottom: 5px;
	background-color: #f1f1ef;
	opacity: 0.85;
}

.entity-ribbon-tile-content {
	display: inline;
	vertical-align: middle;
	margin: 0px auto;
}

.entity-ribbon-tile h1 {
	position: relative;
	vertical-align: middle;
	font-size: 3.4em;
	color: #252d32;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
	margin-left: 20px;
	margin-top: 0px;
}

.entity-ribbon-tile small {
	position: absolute;
	top: 25%;
	font-size: 50%;
	color: #666;
	padding-left: 15px;
}

.#portfolio-container .panel-title {
	color: #666;
	font-size: 18px;
	text-transform: capitalize;
}

/** Chicklets & Chicklet Groups **/
.chiclet-group-1 {
	/* Override Bootstraps width for better spacing of chiclets */
	width: 15em;
}

.chiclet-group-2 {
	/* Override Bootstraps width for better spacing of chiclets */
	width: 29em;
}

.chiclet-group-3 {
	/* Override Bootstraps width for better spacing of chiclets */
	width: 42em;
}

.chiclet-group-4 {
	/* Override Bootstraps width for better spacing of chiclets */
	width: 56em;
}

.chiclet {
	position: relative;
	transition: all 0.5s ease;
	width: 12em;
	height: 11em;
	float: left;
	margin: 9px;
	overflow: hidden;
	text-overflow: ellipsis;
	float: left;
	border-bottom: 1px solid #ccc;
	border-left: 1px solid #ccc;
	border-right: 1px solid #ccc;
}

.chiclet-group-title {
	font-size: 18px;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
	text-align: center;
}

.chiclet-group-content {
	width: 100%;
}

.chiclet:hover {
	transition: all 0.35s ease;
	cursor: pointer;
}

.chiclet:active {
	background-color: #f1f1ef;
	top: 0px;
}

.chiclet.comparable {
	transition: all 0.2s ease;
	top: -5px;
	box-shadow: 0px 4px 0px 0px rgba(0, 0, 0, 0.6);
}

.chiclet.comparable:active {
	transition: all 0.2s ease;
	top: 0px;
	box-shadow: 0px 0px 0px 0px rgba(0, 0, 0, 0);
}

.chiclet.comparing-target {
	transition: all 0.2s ease;
	top: 0px;
	box-shadow: 0px 0px 0px 0px rgba(0, 0, 0, 0);
	background-color: rgba(0, 0, 0, 0.2);
}

.comparison-check {
	display: none;
	position: absolute;
	bottom: 5px;
	right: 5px;
	top: inherit;
	color: green;
}

.chiclet.comparable.comparing-target .comparison-check {
	display: block;
}

.chiclet.isGap .panel-heading {
	background-color: #990000;
}

.chiclet .panel-heading {
	background-color: #4b85b1;
	padding: 5px;
	height: 3.4em;
	border-bottom: 1px solid #bbb;
	display: flex;
	align-items: center;
	justify-content: center;
	text-align: left;
}

.chiclet .panel-title {
    font-size: 12px;
    color: white;
    display: -webkit-box;
	line-height: 1.2;
	-webkit-line-clamp: 2;
	overflow: hidden;
	-webkit-box-orient: vertical;
	text-overflow: ellipsis;
}

.chiclet .panel-body {
	position: relative;
	display: table;
	height: 69%;
	padding: 10px;
	width: 100%;
	background-color: #f8f8f7;
	transition: all 0.35s ease;
}

.chiclet:hover .panel-body {
	background-color: #ddd;'
	transition: all 0.35s ease;
}

.chiclet div.chiclet-details {
	display: table-cell;
	vertical-align: middle;
	bottom: 0;
	text-align: center;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
	width: 100%;
	transition: all 0.35s ease;
}

.chiclet .panel-body h4 {
	margin: 10px 0px;
	color: #666;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
	font-weight: bold;
	font-size: 20px;
}

.chiclet .panel-body small {
	color: #666;
	font-size: 60%;
}

.chiclet .view-details {
	position: absolute !important;
	height: 25px;
	color: transparent;
	font-size: 15px;
	left: 0px;
	right: 0;
	bottom: 4px !important;
	text-align: center;
	display: none;
	transition: all 0.35s ease;
}

.chiclet:hover:not(.comparable ) .view-details {
	display: block;
	color: #666;
	transition: all 0.35s ease;
}

/* Compare Modal */
#compareModal .modal-dialog {
	width: 800px;
}

#compare-container {
	margin: 10px -10px;
    overflow-y: auto;
    max-height: 38em;
}

.comparing-entity-container {
	float: left;
    width: 50%;
    padding: 0 10px;
}

.comparing-entity {
    padding: 0 20px;
    border: 1px solid #bbb;
}

.comparing-entity-header {
	padding: 15px 0;
    border-bottom: 1px solid #bbb;
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: left;
    height: 10em;
}

h2.comparing-entity-title {
    display: -webkit-box;
    line-height: 1.2;
    -webkit-line-clamp: 3;
    overflow: hidden;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    font-size: 26px;
}

.comparing-entity-body {
	
}

.comparing-property-container {
	height: 10em;
    margin-top: 10px;
    overflow: hidden;
}

div.comparing-entity-body > div.comparing-property-container {
	border-bottom: 1px solid #bbb;
}

div.comparing-entity-body > div.comparing-property-container:last-of-type {
	border: 0;
}

h3.comparing-property {

}

p.comparing-value {
	display: -webkit-box;
    line-height: 1.2;
    -webkit-line-clamp: 3;
    overflow: hidden;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
}

/* Config Modal */
.portfolio-button {
	float: right;
	height: 4em;
	width: 120px;
	background-color: #f1f1ef !important;
	border: 1px solid #cac9c9;
	color: #666;
	font-size: 16px;
	margin-top: -7px;
}

/** Target IE 10+ **/
@media screen and (-ms-high-contrast: active) , ( -ms-high-contrast :
	none) {
	.entityTitle {
		vertical-align: middle;
		width: 80%;
		margin: 0 auto;
	}
}

/** Target Firefox **/
@-moz-document url-prefix () { 
	.entityTitle {
		vertical-align: middle;
		width: 70%;
		margin: 0 auto;
	}
}

/* Adjust dimensions for different sized screens - This needs reworking */
@media ( max-width : 1324px) {
	.chiclet {
		width: 11em;
		height: 11em;
	}
	.chiclet-group-1 {
		width: 14em;
	}
	.chiclet-group-2 {
		width: 25em;
	}
	.chiclet-group-3 {
		width: 37em;
	}
	.chiclet-group-4 {
		width: 48em;
	}
}
</style>

<script>
	var chiclet = "${chiclet}";
	var grouping = "${groupingEntity}";
	var content = "${content}";

	$(document).ready(function() {
		// Remove the .modal-open class from the body element that sometimes
		// gets left behind from modals when switching views
		$('body.modal-open').removeClass('modal-open');
		
		$('[data-toggle="tooltip"]').tooltip();

		$("#entity").change(function() {
			target = document.getElementById("entity").value;

			var url = "portfolio.exec?entity="+ encodeURIComponent(target);

			$('#main').empty().load(url, function() {
				$('#main').fadeIn();
			});
		});

		$(function() {
			$('[data-toggle="tooltip"]').tooltip();
		});

		// Toggles the comparison view
		$('#compareLink').click(function() {
			$(this).toggleClass('comparing');

			// Remove 'selected' attribute from all chiclets
			$('.chiclet').removeClass('comparing-target');

			// Toggle the active container (whether grouping/subGrouping/orphan)
			getActiveContainer().toggleClass('comparable');

			// Toggle all the chiclets on the page
			$(".chiclet").toggleClass('comparable');

			// Toggle the text of the Compare button to reflect what the user can do
			if (getActiveContainer().hasClass('comparable'))
				$('#compareLink').text('Cancel');
			else
				$('#compareLink').text('Compare');
		});

		// Click listener for chiclets
		$('#portfolio-container').on('click', '.chiclet', function(event) {
			var url = "";
			var target = $(event.target).closest('.chiclet');
			
			// Only open the baseball card if we're not trying to compare chiclets
			if (target.attr('data-entity') && !getActiveContainer().hasClass('comparable')) {
				var baseballEntity = target.attr('data-entity');
				var baseballName = target.attr('data-name');

				localStorage.setItem("originalEntity", baseballEntity);
				localStorage.setItem("originalName", baseballName);

				url = "baseballCard.exec?entity=" + encodeURIComponent(baseballEntity);
				url += "&name=" + encodeURIComponent(baseballName);

				$("#baseballContent").load(url);

				$('#baseballCardModal').modal('show');
			} else {
				// Toggle a chiclet to be the targeted one to compare to,
				// and show the comparison if 2 are selected
				target.toggleClass('comparing-target');

				if ($('.chiclet.comparing-target').length == 2) {
					var first = $('.chiclet.comparing-target').first();
					var second = $('.chiclet.comparing-target').last();
					
					var entity1 = first.attr('data-entity');
					var entity1Name = first.attr('data-name');
					var entity2 = second.attr('data-entity');
					var entity2Name = second.attr('data-name');
					
					$('#compareContent').load('portfolioCompare.exec', {entityOne: entity1, nameOne: entity1Name,
																		entityTwo: entity2, nameTwo: entity2Name});
					$("#compareModal").modal('show');
				}
			}
		});

		// Reset the view to not be a comparison when the comparison modal closes/hides
		$('#compareModal').on('hidden.bs.modal', function(e) {
			resetCompare();
		});

		// Register tooltips
		$('[data-toggle="tooltip"]').tooltip();

		$('#configLink').click(function() {
			upateSubGroupingChildList();

			$('#configModal').modal('show');

		});

		$('#selectChiclet').change(function() {
			upateSubGroupingChildList();
		});

		$('#cancelLink').click(function() {
			$('#configModal').modal('hide');
		});

		$('#applyConfigLink').click(function() {
			chiclet = $('#selectChiclet').val() !== null ? $('#selectChiclet').val().trim().split(' ').join('+') : '';
			grouping = $('#selectGrouping').val() !== null ? $('#selectGrouping').val().trim().split(' ').join('+') : '';
			content = $('#selectContent').val() !== null ? $('#selectContent').val().trim().split(' ').join('+') : '';

			$('#configModal').modal('hide');
			
			$('#spinner').show();

			$('#main').empty().load( 'portfolio.exec?chiclet=' + chiclet + '&grouping=' + grouping + '&content=' + content, function() {
				$('#spinner').hide();
			});

			//return false;
		});
		
		// Rearrange the chiclets if they're arranged in a subgroup fashion
		$('.subGrouping-container > .panel').each(function() {
			var panelWidth = $(this).find('div > div.panel-body').width();
			var subgroups = $(this).find('div > div > div.col-sm-1');
			var currentWidth = 0;
			
			var html = '<div style="float: left; clear: both;">\n';
			
			subgroups.each(function() {
				var subgroupWidth = $(this).width() + 30;
				currentWidth += subgroupWidth;
				
				if (currentWidth >= panelWidth) {
					html += '</div>\n';
					html += '<div style="float: left; clear: both;margin-top: 15px;">\n' + $(this).wrap('<div>').parent().html();
					
					currentWidth = $(this).width();
				}
				else
					html += $(this).wrap('<div>').parent().html();
			});
			
			html += '</div>\n';
			
			$(this).find('div > div').html(html);
		});

	});

	function upateSubGroupingChildList() {
		$.getJSON("getSubGroupingList.exec", {entity : $('#selectChiclet').val()}, function(list) {
			$('#selectGrouping').empty();

			if (list.length == 0)
				$('#selectGrouping').append('<Option>' + $('#selectChiclet').val() + '</Option)');
			else {
				for (i = 0; i < list.length; i++) {
					if (list[i] === grouping) {
						$('#selectGrouping').append('<Option selected="selected">' + list[i] + '</Option)');
					} else {
						$('#selectGrouping').append('<Option>' + list[i] + '</Option)');
					}
				}
			}

		});

		$.getJSON("getChildList.exec", {entity : $('#selectChiclet').val()}, function(list) {
			$('#selectContent').empty();

			$.each(list, function(index) {
				if (list[index] === content) {
					$('#selectContent').append('<Option selected="selected">' + list[index] + '</Option)');
				} else {
					$('#selectContent').append('<Option>' + list[index] + '</Option)');
				}
			});
		});
	}

	// Resets the view to not be comparable
	function resetCompare() {
		$('.chiclet').removeClass('comparing-target').removeClass('comparable');
		getActiveContainer().removeClass('comparable');
		$('#compareLink').text('Compare').removeClass('comparing').tooltip('hide');
	}

	// Returns the 'Active' Container - either the Grouping/Sub Grouping/Orphan
	function getActiveContainer() {
		if ($('.grouping-container').length == 1)
			return $('.grouping-container');
		else if ($('.subGrouping-container').length == 1)
			return $('.subGrouping-container');
		else
			return $('.orphan-container');
	}

	// Pluralize the entity/children appropriately
	$('.entity-ribbon-tile h1 small').each(function() {
		var count = $(this).parent().text().split(' ')[0];
		var word = $(this).text();
		$(this).text(count == 1 ? word : word.pluralize());
	});
</script>
</head>

<body>
	<div id="portfolio-container" class="container">
		<div class="row">
			<div class="col-md-12">
				<h1 class="page-title-inline">Portfolio</h1>
				<button class="portfolio-button " id="configLink" title="Config">Configure</button>
				<button class="portfolio-button " id="compareLink" data-toggle="tooltip" data-placement="left" data-trigger="click" title="Select Two Chiclets" style="margin-right: 15px">Compare</button>
			</div>
		</div>
		<!------------------------------------------------ Top Row ------------------------------------------------------->
		<div class="row" style="margin-bottom: 20px; margin-top: 10px;">
			<c:if test="${empty entityList}">
				<div style="margin-top: 20px">
					<span style="color: red; font-size: 24px; margin: auto;"> No
						data set found. Please use the <a class="execLink"
						href="dataImport.exec" id="import">Data Import</a> link to upload
						one.
					</span>
				</div>
			</c:if>

			<c:if test="${not empty entityList}">
				<div class="col-md-12">
					<div class="row">

						<!-- Selected Entity & Count -->
						<c:if test="${not empty gapCount}">
							<div style="margin-left: 0px; width: 25%; float:left;padding: 0 15px;">
						</c:if>
						<c:if test="${empty gapCount}">
							<div class='col-md-3' style="margin-left: 0px; width: 33.33333333%;">
						</c:if>
							<div class="entity-ribbon-tile top-border-accent-color-${selectedEntity}" >
								<div class="entity-ribbon-tile-content">
									<h1>${selectedEntityCount} <small>${selectedEntity}</small></h1>
								</div>
							</div>
						</div>

						<!-- Group Entity & Count -->
						<c:if test="${not empty groupingEntity && selectedEntity != groupingEntity}">
							<c:if test="${not empty gapCount}">
								<div style="margin-left: 0px; width: 25%; float:left;padding: 0 15px;">
							</c:if>
							<c:if test="${empty gapCount}">
								<div class='col-md-3' style="margin-left: 0px; width: 33.33333333%;">
							</c:if>
								<div class="entity-ribbon-tile top-border-accent-color-${groupingEntity}">
									<div class="entity-ribbon-tile-content">
										<h1>${groupingEntityCount} <small>${groupingEntity}</small></h1>
									</div>
								</div>
							</div>
						</c:if>

						<!-- Content Entity & Count -->
						<c:if test="${not empty content}">
							<c:if test="${not empty gapCount}">
								<div style="margin-left: 0px; width: 25%; float:left;padding: 0 15px;">
							</c:if>
							<c:if test="${empty gapCount}">
								<div class='col-md-3' style="margin-left: 0px; width: 33.33333333%;">
							</c:if>
								<div class="entity-ribbon-tile top-border-accent-color-${content}">
									<div class="entity-ribbon-tile-content">
										<h1>${contentCount} <small>${content}</small></h1>
									</div>
								</div>
							</div>
						</c:if>

                        <!-- Gap Count -->
                        <c:if test="${not empty gapCount}">
                            <div style="margin-left: 0px; width: 25%; float:left;padding: 0 15px;">
                                <div class="entity-ribbon-tile top-border-accent-color-${content}">
                                    <div class="entity-ribbon-tile-content">
                                        <h1>${gapCount} <small>Gap</small></h1>
                                    </div>
                                </div>
                            </div>
                        </c:if>
					</div>
				</div>
			</c:if>
		</div>


		<c:if test="${chicletRelation == 'isParent'}">
			<!------------------------------------------------ Chiclets with Groupings ------------------------------------------------------->
			<div class="grouping-container">
				<c:forEach items="${portfolioBeanList}" var="group" varStatus="status">
					<div class="row">
						<div class="col-sm-12">
							<div class="panel">
								<div class="panel-heading" data-toggle="collapse" data-target="#panel-${status.index}">
									<h3 class="panel-title">${group.label}: ${group.name}</h3>
									<button class="panel-header-icon panel-collapse-flip">
										<span class="glyphicon glyphicon-chevron-down"></span>
									</button>
								</div>
								<div id="panel-${status.index}" class="panel-collapse collapse in">
									<div class="panel-body">
										<c:forEach items="${group.children}" var="chiclet">
											<div class="chiclet panel baseballCardLink ${chiclet.isGap ? 'isGap' : '' }" data-entity="${chiclet.label}" data-name="${chiclet.name}">
												<div class="panel-heading chiclet-title-tooltip">
													<div style="display: flex;align-items: center;justify-content: center;text-align: center;height: 100%;">
														<h4 class="panel-title">${chiclet.name}</h4>
													</div>
												</div>
												<div class="panel-body">
													<div class="chiclet-details">
														<c:forEach items="${chiclet.counts}" var="count">
															<h4>${count.value}
																<small>${count.name}(s)</small>
															</h4>
														</c:forEach>
													</div>
													<div class="view-details">View Details</div>
													<span class="glyphicon glyphicon-ok-circle comparison-check"></span>
												</div>
											</div>
										</c:forEach>
									</div>
								</div>
							</div>
						</div>
					</div>
				</c:forEach>
			</div>
		</c:if>

		<c:if test="${chicletRelation == 'isGrandparent'}">
		<!------------------------------------------------ Chiclets with Sub Groupings ------------------------------------------------------->
		<div class="row">
		<div class="col-sm-12">
			<div class="subGrouping-container">
				<c:forEach items="${portfolioBeanList}" var="group" varStatus="status">
					<div class="panel">
						<div class="panel-heading" data-toggle="collapse" data-target="#panel-${status.index}">
							<h3 class="panel-title">${group.label}: ${group.name}</h3>
							<button class="panel-header-icon panel-collapse-flip">
								<span class="glyphicon glyphicon-chevron-down"></span>
							</button>
						</div>
						<div id="panel-${status.index}" class="panel-collapse collapse in">
							<div class="panel-body">
								<c:forEach items="${group.children}" var="subGroup">
									<c:if test="${fn:length(subGroup.children) == 1}">
										<div class="col-sm-1 chiclet-group-1">
									</c:if>
									<c:if
										test="${fn:length(subGroup.children) > 1 && fn:length(subGroup.children) <= 6}">
										<div class="col-sm-1 chiclet-group-2">
									</c:if>
									<c:if
										test="${fn:length(subGroup.children) > 6 && fn:length(subGroup.children) < 12}">
										<div class="col-sm-1 chiclet-group-3">
									</c:if>
									<c:if test="${fn:length(subGroup.children) >= 12}">
										<div class="col-sm-1 chiclet-group-4">
									</c:if>

									<div class="chiclet-group-container">
										<div class="chiclet-group-title chiclet-tooltip">${subGroup.label}:
											${subGroup.name}
										</div>
										<div class="chiclet-group-content">
											<c:forEach items="${subGroup.children}" var="child">
												<div class="chiclet panel baseballCardLink ${child.isGap ? 'isGap' : '' }" data-entity="${selectedEntity}" data-name="${child.name}">
													<div class="panel-heading chiclet-title-tooltip">
														<div style="display: flex;align-items: center;justify-content: center;text-align: center;height: 100%;">
															<h4 class="panel-title">${child.name}</h4>
														</div>
													</div>
													<div class="panel-body">
														<div class="chiclet-details">
															<c:forEach items="${child.counts}" var="count">
																<h4>${count.value}
																	<small>${count.name}(s)</small>
																</h4>
															</c:forEach>
															<c:if test="${fn:length(child.counts) == 0}">
																<h4 style="font-size: 14px;"></h4>
															</c:if>
														</div>
														<div class="view-details">View Details</div>
														<span class="glyphicon glyphicon-ok-circle comparison-check"></span>
													</div>
												</div>
											</c:forEach>
										</div>
									</div>
								</div>
							</c:forEach>
						</div>
					</div>		
				</div>
			</c:forEach>
			</div>
		</div>
	</div>
	</c:if>

	<c:if test="${chicletRelation == 'isOrphan'}">
		<!------------------------------------------------ Orphan Chiclets ------------------------------------------------------->
		<div class="row">
			<div class="col-sm-12">
			<div class="orphan-container">
			<c:forEach items="${portfolioBeanList}" var="orphan" varStatus="index">
				<div class="panel">
					<div class="panel-heading" data-toggle="collapse" data-target="#panel-${status.index}">
						<h4 class="panel-title">${orphan.name}</h4>
						<button class="panel-header-icon panel-collapse-flip">
							<span class="glyphicon glyphicon-chevron-down"></span>
						</button>
					</div>
					<div id="panel-${status.index}" class="panel-collapse collapse in">
						<div class="panel-body">
							<c:forEach items="${orphan.children}" var="chiclet">
								<div class="chiclet panel baseballCardLink ${chiclet.isGap ? 'isGap' : '' }" data-entity="${chiclet.label}" data-name="${chiclet.name}">
									<div class="panel-heading chiclet-title-tooltip">
										<div style="display: flex;align-items: center;justify-content: center;text-align: center;height: 100%;">
											<h4 class="panel-title">${chiclet.name}</h4>
										</div>
									</div>
									<div class="panel-body">
										<div class="chiclet-details">
											<c:forEach items="${chiclet.counts}" var="count">
												<h4>${count.value}
													<small>${count.name}(s)</small>
												</h4>
											</c:forEach>
										</div>
										<div class="view-details">View Details</div>
										<span class="glyphicon glyphicon-ok-circle comparison-check"></span>
									</div>
								</div>
							</c:forEach>
						</div>
					</div>
				</div>
			</c:forEach>
			</div>
			</div>
		</div>
	</c:if>

	<!-- Compare Modal -->
	<div class="modal fade" id="compareModal" role="dialog">
		<div class="modal-dialog" style="margin: 10% auto;">
			<div class="modal-content" id="compareModalContent">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&#x2573;</span>
					</button>
					<h4 class="modal-title" align="left">Comparison View</h4>
				</div>
				<div class="modal-body" id="compareContent"></div>
				<div class="modal-footer">
					<button style="color: white; width: 100%;" type="button" class="accent-color2" data-dismiss="modal">OK</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->
	</div>

	<!-- Config Modal -->
	<div class="modal fade" id="configModal" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content base-colorALT1" id="configContent" style="width: 500px">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&#x2573;</span>
					</button>
					<h4 class="modal-title" align="left">Configure Portfolio View</h4>
				</div>
				<div class="modal-body" id="configContent">
					<form>
						<div class="form-group">
							<label for="selectChiclet">Select Chiclet</label>
							<select class="form-control" id="selectChiclet">
								<c:forEach items="${entityList}" var="entity">
									<c:if test="${entity == selectedEntity}">
										<option value="${entity}" selected="selected">${entity}</option>
									</c:if>
									<c:if test="${entity != selectedEntity}">
										<option value="${entity}">${entity}</option>
									</c:if>
								</c:forEach>
							</select>
						</div>
						<div class="form-group">
							<label for="entity" class="control-label">Select Grouping</label>
							<select class="form-control" id="selectGrouping">
							</select>
						</div>
						<div class="form-group">
							<label for="selectContent" class="control-label">Select Content</label>
							<select class="form-control" id="selectContent">
 							</select>
						</div>
					</form>
				</div>
				<div class="modal-footer" style="padding: 15px;">
					<button class="btn-block apply" id="applyConfigLink">Apply</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>


</body>

</html>