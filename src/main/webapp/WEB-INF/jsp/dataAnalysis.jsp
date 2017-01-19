<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	
	<style>
		#dataAnalysis-container {
			margin-right: 0px;
			margin-left: 0px;
			width: 100%;
		}
		
		#filter-container {
			height: 210px;
			overflow-y: auto;
			overflow-x: hidden;
		}
		
		#filter-container label {
		    float: left;
		    padding-left: 15px;
		    padding-right: 15px;
		    padding-top: 10px;
		    height: 40px;
		    overflow: hidden;
		    white-space: nowrap;
		    text-overflow: ellipsis;
		}
		
		#parameter-container {
			transition: all 0.3s ease;
		}
		
		.parameter {
			padding-top: 20px;
			padding-bottom: 20px;
		}
		
		.panel-heading span.glyphicon {
			color: #666;
			float: right;
    		top: -1.2em
		}
		
		.control-label {
			font-weight: normal;
			padding-top: 9px;
		}
		
		#submitAnalysis {
			transition: all 0.2s ease;
			margin-top: 20px;
			background: #9ac440;
			border-radius: 0px;
			border: 1px solid #9ac440;
			color: white;
			height: 40px;
		}
		
		#submitAnalysis:hover {
			transition: all 0.2s ease;
			background-color: transparent;
			color: #9ac440;
		}
		
		#results-container {
			transition: all 0.3s ease;
			height: 37em;
			overflow-y: auto;
			overflow-x: hidden;
		}
		
		#results {
			transition: all 0.3s ease;
		}
		
		#results table th {
			font-weight: normal;
			font-size: 15px;
    		color: #333;
    		cursor: pointer;
		}
		
		#results table th:active {
			outline: 0;
		}
		
		#results table th a {
			color: #807777;
		}
		
		#results table td {
			color: #807777;
		}
		
		#result-spinner {
			position: absolute;
			left: 48%;
    		top: -140%;
		}
		
		.divider {
			 position: relative;
		left: 15%;
		height: 1px;
		}
		
		#filter-container > form {
			background-color: #ddd;
			padding: 10px 0;
		}
		
		#filter-container > form > div.form-group {
			margin-bottom: 0;
		}
		
		#parameter-container > div > form > div.form-group {
			margin-top: 10px;
			margin-bottom: 0;
		}
	</style>
	
	<script>
		$(document).ready(function() {
			var currentParameters = [];
			var currentFields = [];
			
			$('#results-container').hide();
			
			$('#result-spinner').hide()
			
			$('#entityType').change(function() {
				var container = $('#results-container')
				container.show();
				var entityListSelector = $('#entityList');
				var entityType = $(this).val();
				
				$.getJSON("getFieldList.exec", {entity: entityType}, function(list) {
					
					currentParameters.forEach(function(param) {
						param.remove();
					});
					currentParameters = [];
					currentFields = list;
					
					list.forEach(function(param, index) {
						addParameter(param, index);
					});
					
					$('#result-spinner').show();
					
					getResults();

                    container.removeClass();
					container.addClass("top-border-accent-color-" + entityType);
				});
			})
			
			function addParameter(values) {
				var id = currentParameters.length + 1;
				var newParam = new Parameter(values, id);
				newParam.init();
				currentParameters.push(newParam);
			}
			
			function removeParameter(id) {
				var newArray = []
				currentParameters.forEach(function(param) {
					if (param.id !== id) {
						newArray.push(param)
					}
				});
				currentParameters = newArray;
			}
			
			var Parameter = function(param, id) {
				this.value = function() {
					if ($('#parameter-input-' + that.id)[0].value)
						return $('#parameter-input-' + that.id)[0].value;
					else
						return "Any";
				};
				this.property = param;
				this.id = id;
				var that = this;
				
				this.init = function() {
					html = '';
					
					html += '<div class="col-md-6">\n';
					html += '	<form id="parameter-' + that.id + '" class="form-horizontal">\n';
					html += '		<div class="form-group">\n';
					html += '			<label id="parameter-label-' + that.id + '" for="parameter-input-' + that.id + '" class="control-label col-md-4"></label>\n';
					html += '			<div class="col-md-8">\n';
					html += '				<select id="parameter-input-' + that.id + '" class="form-control">\n';
					html += '				</select>\n';
					html += '			</div>\n'
					html += '		</div>\n';
					html += '	</form>\n';
					html += '</div>\n';
					
					
					$('#parameter-container').prepend(html);
					setParameter(that.property);
					setSuggestions();
					attachListeners();
					setDisabled(false);
					
					// Correctly set the styling of the parameters
					$('.parameter').css('padding-top', '20px');
					$('.parameter').first().css('padding-top', '0px');
					$('.parameter').last().css('margin-bottom', '20px');
				};
				
				var setParameter = function(param) {
					$('#parameter-label-' + id).text(param);
				}
				
				var attachListeners = function() {					
					$('#parameter-input-' + that.id).change(function() {
						getResults();
					});
				};
				
				this.remove = function() {
					$('#parameter-' + that.id).remove();
					removeParameter(that.id);
				}
				
				var setDisabled = function(disabled) {
					$('#parameter-input-' + id).attr('disabled', disabled);
				};
				
				this.setDisabled = function(disabled) {
					$('#parameter-select-' + id).attr('disabled', disabled);
					$('#parameter-input-' + id).attr('disabled', disabled);
				};
				var setSuggestions = function(suggestions) {
					html = '';
					
					html += '<option value="Any">Any</option>\n';
					suggestions.forEach(function(suggestion) {
						html += '<option value="' + suggestion + '">' + suggestion + '</option>\n';
					});
					
					$('#parameter-input-' + that.id).html(html);
				};
				
				var setSuggestions = function() {
					var field = that.property;
					$.getJSON("getValueList.exec", {entity: $('#entityType').val(), field: field}, function(list) {
						html = '';
						
						html += '<option value="Any">Any</option>\n';
						list.forEach(function(suggestion) {
							html += '<option value="' + suggestion + '">' + suggestion + '</option>\n';
						});
						
						$('#parameter-input-' + that.id).html(html);
					});
				};
			}
			
			function getResults() {
				$('#results').hide();
				$('#result-spinner').show();
				
				var analysisParameters = {
						entityType: '',
						additionalParameters: []
				};
				var parameters = [];
				currentParameters.forEach(function(parameter) {
					var p = {
							property: parameter.property,
							value: parameter.value()
					}
					
					parameters.push(p);
				});
				
				analysisParameters.additionalParameters = parameters;
				analysisParameters.entityType = $('#entityType').val();
				
				$.ajaxSetup({dataType: 'json', type: 'POST', contentType : 'application/json; charset=utf-8'})
				$('#results').load('runAnalysis.exec', JSON.stringify(analysisParameters));
				$('#results').show();
			};
			
			$('#results').click('.baseballCardLink', function(event) {
		    	if (event.target.getAttribute('data-entity')) {
		    		var baseballEntity = event.target.getAttribute('data-entity');
			    	var baseballName = event.target.getAttribute('data-name');
			    	
			    	localStorage.setItem("originalEntity", baseballEntity);
			    	localStorage.setItem("originalName", baseballName);   	
			    	
					$('#baseballCardModal').modal('show');
					
					var url = "baseballCard.exec?entity="+encodeURIComponent(baseballEntity)+"&name="+encodeURIComponent(baseballName);
					
					$("#baseballContent").load(url);
		    	}
		    });
		});
	</script>
	
</head>
<body>
	<div id="dataAnalysis-container" class="container">
		<div class="row">
			<div class="col-md-12">
				<h1 class="page-title">Data Analysis</h1>	
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="panel">
					<div id="filters" class="top-border-accent-color-Dataset">
						<div class="panel-body">
							<div class="row">
								<div class="col-md-12">
									<div id="filter-container">
										<form class="form-horizontal">
											<div class="form-group">
												<label for="entityType" class="control-label col-md-2">Select Entity</label>
												<div class="col-md-4">
													<select id="entityType" class="form-control">
														<option value="" disabled selected hidden>Select Entity...</option>
														<c:forEach items="${entities}" var="entity">
															<option value="${entity.name}">${entity.name}</option>
														</c:forEach>
													</select>
												</div>
											</div>
										</form>
										<div id="parameter-container" class="row">
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="panel">
					<div id="results-container">
						<div class="panel-body">
							<div id="results">
								<p>Run the analysis to display results.</p>
							</div>
							<div id="result-spinner" class="pulser"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>