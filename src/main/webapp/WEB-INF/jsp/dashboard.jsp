<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Dashboard</title>

<style>
/** Main container **/
#dashboard-container {
	margin-right: 0px;
	margin-left: 0px;
	margin-top: 20px;
	width: 100%;
}

#dashboard-panel {
	margin-top: 10px;
}

.dashboard-tile {
	padding-bottom: 20px;
}

.tile-header {
	width: 100%;
	padding-top: 10px;
	padding-bottom: 10px;
	display: table;
	background-color: #f1f1ef;
}

.entityTitle {
	vertical-align: middle;
	margin: 0 auto;
	display: inline;
}

.entityTitle span {
	position: relative;
	vertical-align: middle;
	font-size: 3.1em;
	color: #252d32;
	margin-left: 25px;
	white-space: nowrap;
}

.entityTitle span small {
	position: absolute;
	top: 25%;
	font-size: 50%;
	color: #666;
}

.tile-content {
	margin-top: 20px;
	height: 20em;
}

.tile-content-title {
	display: table;
	height: 15%;
	width: 100.0668%;
	padding-top: 10px;
	padding-bottom: 10px;
	padding-left: 25px;
}

.tile-content-title h3 {
	color: #343f47;
	display: table-cell;
	vertical-align: middle;
	font-size: 1.3em;
}

.tile-content-graph {
	width: 100%;
	height: 85%;
	overflow: hidden;
	background-color: #f1f1ef;
}

#tabRow li {
	width: 16.6666667%;
	margin-bottom: 0px;
}

#tabRow li a {
	margin: 0px;
	font-size: 1.3em;
	color: #252d32;
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
	text-align: center;
	border-radius: 0px;
	border-left: 0px;
    border-right: 0px;
}

#tabRow li a:focus {
	outline: 0;
}

.tab {
	background-color: #f1f1ef;
}

#detailed-view-container div.tab-content {
	overflow-y: scroll;
	overflow-x: scroll;
	height: 22em;
	transition: all 0.5s ease;
	background-color: white;
}

#detailed-view-container [role="tabpanel"] {
	padding: 15px;
}

@media ( max-width : 992px) {
	#tabRow li {
		width: 33.333333333%
	}
	.entityTitle {
		width: 50%;
	}
}

@media ( max-width : 1024px) {
	.entityTitle {
		width: 70%;
	}
}

/** Target IE 10+ **/
@media screen and (-ms-high-contrast: active) , ( -ms-high-contrast : none) {
	.tile-content {
		height: 22em;
	}
	.entityTitle {
		vertical-align: middle;
		width: 80%;
		margin: 0 auto;
	}
}

/** Target Firefox **/
@-moz-document url-prefix () { 
	.tile-content { height:20em;
		
	}
	
	.entityTitle {
		vertical-align: middle;
		width: 70%;
		margin: 0 auto;
	}
}

#config {
	color: #666;
    float: right;
    font-size: 16px;
    height: 4em;
    width: 120px;
    background-color: #f1f1ef;
    border: 1px solid #cac9c9;
    margin-top: -7px;
}

tr.isAlert * {
	color: #D62728;
}

tr.entity-summary-table * {
	font-size: 15px;
}
</style>

<script>
$(document).ready(function()
{  
	
	$("#import").click(function()
	{
		$('#main').load('dataImport.exec',function()
		{
			  $('#main').fadeIn();	
		});
	});
	 
	// $('.tab-content').css('border', '1px solid #C36330');
	
	$('#tabRow li a').on('click', function(e) {
		e.preventDefault();
		$('.tab-content').css($(this).css('background-color'));
	});
	
	onReady('.c3 svg', 100, function()
	{	
		<c:forEach items="${dashboardEntityList}" var="dashboardEntity" varStatus="status">
		
			var alertList = [];
			<c:forEach items="${dashboardEntity.groupByList}" var="pair">
				
				<c:set var="showAlert" scope="session" value=
					"${(dashboardEntity.alertCheck == '=' && pair.name == dashboardEntity.alertValue) || 
					 (dashboardEntity.alertCheck == '>' && pair.name  > dashboardEntity.alertValue) ||
					 (dashboardEntity.alertCheck == '<' && pair.name  < dashboardEntity.alertValue)} " />
			
	  			alertList.push('${pair.name}');
				
				if ( ${showAlert} )
					alertList.push(1);
				
				if ( ! ${showAlert} )
					alertList.push(0);
					
			</c:forEach>
			
			c3.generate({
			bindto: '#chart${status.index+1}',
			data: 
			{
		        columns:
		        [ 
		         	<c:if test="${dashboardEntity.chartTypeValue == 'pie' || 
		         				  dashboardEntity.chartTypeValue == 'donut'}">
		     		  	<c:forEach items="${dashboardEntity.groupByList}" var="pair">
		     		  		['${pair.name}', ${pair.value}],
	                  	</c:forEach>   
	     		  	</c:if>
	     		  	
		         	<c:if test="${dashboardEntity.chartTypeValue == 'bar' ||  
		         				  dashboardEntity.chartTypeValue == 'hbar' ||
		         				  dashboardEntity.chartTypeValue == 'line' }">
					['${dashboardEntity.groupBy}', 
			     		<c:forEach items="${dashboardEntity.groupByList}" var="pair">
		                 	${pair.value},
		                 </c:forEach>
	           		],
	           		</c:if>
		        ],
		        type: '${dashboardEntity.chartTypeValue}',
	
		        color: function (color, d) 
		        {	
		        	for (i=0; i<alertList.length; i=i+2)
		        	{
		        		if ( d == alertList[i] && alertList[i+1] == 1 )
		        			return '#D62728';
		        	}
			        return alertList[d.index*2+1] == 1 ? '#D62728': color;	
		        },
			},
			color:
			{
				pattern:['#4c85b2', '#9ac440', '#ff9896', '#9467bd', '#c5b0d5', '#8c564b', '#c49c94', '#e377c2', '#f7b6d2', '#7f7f7f', '#c7c7c7', '#bcbd22', '#dbdb8d', '#17becf', '#9edae5']
			},
	        axis: 
	        {
	        	<c:if test="${dashboardEntity.chartType == 'Horizontal Bar Chart'}">
	            	rotated: true,	
	            </c:if>
	  			x: 
	  			{
		            type: 'category',
		            categories: 
		            [
			  		  	<c:forEach items="${dashboardEntity.groupByList}" var="pair">
			 		  	'${pair.name}',
			          	</c:forEach>
				       
		            ] 			
				},
	        },
	        <c:if test="${dashboardEntity.chartTypeValue == 'bar' ||  
				  chartTypeValueList[status.index] == 'hbar' ||
				  chartTypeValueList[status.index] == 'line' }">
				legend:
				{
					show: false,
				},
			</c:if>
			});
		
		</c:forEach>
	});
    
    $('[data-toggle="popover"]').popover(
    {
        placement : 'left',
        trigger: "hover click"
    });
    
    // Move the graphs slightly to left once they're rendered
    onReady('.c3 svg', 300, function()
	{
    	$('.c3 svg').css('margin-left', '-1em');  
	});
    
   	// Function that waits for an element to be defined
   	// based on it's CSS selector
    function onReady(selector, time, callback)
    {
    	var intervalID = window.setInterval(function() 
    	{
    		if ($(selector) !== undefined) {
   				window.clearInterval(intervalID);
   				callback.call(this);
   			} else {
   			    onReady(selector, time, callback);
   			}
    	}, time);
    }
   	
    $('.baseballCardLink').click(function(){
    	
    	var baseballEntity = $(this).attr('data-entity');
    	var baseballName = $(this).attr('data-name');
    	
    	localStorage.setItem("originalEntity", baseballEntity);
    	localStorage.setItem("originalName", baseballName);   	
    	
		$('#baseballCardModal').modal('show');
		
		var url = "baseballCard.exec?entity="+encodeURIComponent(baseballEntity)+"&name="+encodeURIComponent(baseballName);
		
		$("#baseballContent").load(url);
    });
	
 	// Correctly pluralize the title of each entity
	$('.entityTitle small').each(function()
	{
		var count = parseInt($(this).parent().text().split(' ')[0]);
		var word = $(this).text().split(' ').pop();
		$(this).text(count > 1 ? word.pluralize() : word);
	});
 	
 	$('.tab-content table').each(function() {
 		$(this).tablesorter();
 	});
});	

</script>
</head>
<body>

<!------------------------------------------ Beggining of Dashboard page ----------------------------->
<div id="dashboard-container" class="container">
	
	<div class="row">
		<div class="col-md-12">
			<h1 class="page-title-inline">Dashboard</h1>
			<!-- <button type="button" id="config" href="configDashboard.exec" class="execLink">Configure</button> -->
		</div>
	</div>
	
	<div id="dashboard-panel">
	
	<!-- First row, contains Title and Graph of each entity -->
	<div class="row">
		<c:if test="${empty dashboardEntityList}">
			<div style="margin-top:20px">
			<span style="color:#990000; font-size:24px; margin:auto;">
			No data set found. Please use the <a href="#" id="import">Data Import</a> link to upload one.</span>
			</div>
		</c:if>
		
		<c:forEach items="${dashboardEntityList}" var="dashboardEntity" varStatus="status">
		    <c:if test="${status.index lt 3}">

			<!-- Header of Entity -->
			<div class="col-sm-4 dashboard-tile">
                    <div class="tile-header top-border-accent-color-${dashboardEntity.entity}">
					<div class="entityTitle">
						<span data-toggle="popover" data-html="true" data-content=
						'
							<table>
							<c:forEach items="${dashboardEntity.groupByList}" var="pair">
							
							<tr class="entity-summary-table
								<c:if test="${  (dashboardEntity.alertCheck == '=' && pair.name == dashboardEntity.alertValue) || 
												(dashboardEntity.alertCheck == '>' && pair.name  > dashboardEntity.alertValue) ||
												(dashboardEntity.alertCheck == '<' && pair.name  < dashboardEntity.alertValue) }"> isAlert</c:if>">
								<td style="text-align:right">${pair.name}:&nbsp;</td><td style="text-align:left">${pair.value} ${dashboardEntity.entity}(s) </td>
							</tr>
							</c:forEach>
							</table>
						'>${fn:length(dashboardEntity.elementList)}&nbsp;<small>${dashboardEntity.entity}</small></span>
					</div>
				</div>
			
				<!-- Entity Content (graph) -->
				<div class="tile-content">
					<div class="tile-content-title header-color">
						<h3>${dashboardEntity.entity} By ${dashboardEntity.groupBy}</h3>
					</div>
					<div class="tile-content-graph" id="chart${status.index+1}"></div>
				</div>
			</div>

            </c:if>
		</c:forEach>
		
	</div>

	<!-- Second Row, dislays detailed view of entities -->
	<div class="row " style="margin-top: 15px; margin-bottom: 15px; ">
		<c:if test="${not empty dashboardEntityList}">
			<div id="detailed-view-container" class="col-sm-12 ">
				<!-- Nav Tabls-->
				<ul class="nav nav-tabs detail-tabs" role="tablist" id="tabRow">
					<c:forEach items="${dashboardEntityList}" var="dashboardEntity" varStatus="status">
					    <li class='<c:if test="${status.index == 0 }">active</c:if>' id="tab-${dashboardEntity.entity}" role="presentation">
					    	<a href="#entity${status.index+1}Tab" data-toggle="tab">${dashboardEntity.entity}</a></li>
					</c:forEach>
				</ul>
			
				<!-- Tab panes -->
				<div class="tab-content" >
					<c:forEach items="${dashboardEntityList}" var="dashboardEntity" varStatus="status">
						<div role="tabpanel" class="tab-pane fade <c:if test="${status.index==0}">in active</c:if>" id="entity${status.index+1}Tab">
						
						<table class="table table-bordered table-striped" >
						<thead><tr>
						<c:forEach items="${dashboardEntity.displayList}" var="prop">
							<th>${prop}<div class="table-sorter-icon"></div></th>
						</c:forEach>
						</tr>
						</thead>
						<tbody>
						<c:forEach items="${dashboardEntity.elementList}" var="entityMap">
						<tr <c:if test="${(dashboardEntity.alertCheck == '=' && entityMap[dashboardEntity.groupBy] == dashboardEntity.alertValue) || 
										  (dashboardEntity.alertCheck == '>' && entityMap[dashboardEntity.groupBy] > dashboardEntity.alertValue) || 
										  (dashboardEntity.alertCheck == '<' && entityMap[dashboardEntity.groupBy] < dashboardEntity.alertValue) }">
							     class="isAlert"</c:if>>
							<c:forEach items="${dashboardEntity.displayList}" var="prop">
							<c:choose>
								<c:when test="${prop == 'name'}">
            						<td><a href="#" data-name="${entityMap[prop]}" data-entity="${dashboardEntity.entity}" class="baseballCardLink">${entityMap[prop]}</a></td>
								</c:when>
								<c:when test="${prop == 'location'}">
                                    <td><a href="${entityMap[prop]}" target="_blank">${entityMap[prop]}</a></td>
                                </c:when>
                                <c:when test="${prop == 'schema'}">
                                    <td><a mimetype="application/json" download="schema.json" href="${entityMap[prop]}" target="_blank">${entityMap[prop]}</a></td>
                                </c:when>
                                <c:when test="${prop == 'queue'}">
                                    <td><a href="${entityMap[prop]}" target="_blank">${entityMap[prop]}</a></td>
                                </c:when>
                                <c:when test="${prop == 'description'}">
                                    <td>${fn:substring(entityMap[prop], 0, 90)}<c:if test="${fn:length(entityMap[prop]) > 90}">...</c:if></td>
                                </c:when>
								<c:otherwise>
									<td>${entityMap[prop]}</td>
								</c:otherwise>
							</c:choose>
							</c:forEach>
						</tr>
						</c:forEach>
						</tbody>
						</table>
						
						</div>
					</c:forEach>
				</div>
			</div>
		</c:if>
	
	</div>
	</div>
</div>
</body>
</html>