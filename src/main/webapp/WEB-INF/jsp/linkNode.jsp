<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<script src="js/jit.js"></script>
</head>

<style>

.modal p {
	font-size: 1.2em;
}

#graph-spinner {
    width: 100%;
    position: absolute;
    top: 45%;
}

@-webkit-keyframes graph-spinner {
    0% {
        -webkit-transform: rotate(0deg);
        transform: rotate(0deg)
    }
    100% {
        -webkit-transform: rotate(360deg);
        transform: rotate(360deg)
    }
}
@keyframes graph-spinner {
    0% {
        -ms-transform: rotate(0deg);
        -webkit-transform: rotate(0deg);
        transform: rotate(0deg)
    }
    100% {
        -ms-transform: rotate(360deg);
        -webkit-transform: rotate(360deg);
        transform: rotate(360deg)
    }
}

.graph-spinner {
	-webkit-animation: graph-spinner 1250ms infinite linear;
    animation: graph-spinner 1250ms infinite linear;
    border: 4px solid #808080;
    border-right-color: transparent;
    border-radius: 16px;
    -moz-box-sizing: border-box;
    box-sizing: border-box;
    display: inline-block;
    position: absolute;
    top: 45%;
    left: 49%;
    overflow: hidden;
    text-indent: -9999px;
    width: 32px;
    height: 32px;
}

#graph-options-container {
	position: absolute;
    top: 10px;
    right: 10px;
    background-color: white;
    box-shadow: 0 0 3px rgba(0, 0, 0, 0.2);
    z-index: 2;
    transition: all 0.35s ease;
    height: 7.6em;
    width: 25em;
}

#graph-options-container.closed {
	transition: all 0.35s ease;
	width: 2em;
	height: 2em;
}

#graph-options-container.closed #graph-options {
	visibility: hidden;
}

#graph-options-container.closed #graph-options * {
	visibility: hidden;
}

#graph-options {
    padding: 15px;
    width: 25em;
    position: absolute;
    left: 0;
    bottom: 0;
    transition: all 0.35s ease;
    transition-delay: 0.35s;
}

#graph-options-control-button {
	position: absolute;
    top: 0;
    right: 0;
    width: 2em;
    height: 2em;
    text-align: center;
    color: #444;
}

#graph-options-control-button:hover {
	cursor: pointer;
}

#graph-options-control-button span.glyphicon {
	color: #444;
	vertical-align: middle;
	top: 3px;
}

#search-text {
	background-color: white;
}

#search {
	color: white;
	border: 0;
}

#search:hover {
	cursor: pointer;
	opacity: 0.75;
}

#home {
	color: #808080;
	border-radius: 0;
	border: 0;
}

.node::selection {
	background: transparent;
}

#checkbox-container {
	position: relative;
    clear: both;
    padding-right: 12em;
}
</style>

<script>
	currentSuggestions = [];
	currentGraph = null;
	$(document).ready(function() {
		// Load tooltips
		$('[data-toggle="tooltip"]').tooltip({container: 'body'});
		
		// Load the 1st level when first loading page
		$.getJSON("getLinkNodeData.exec", {id: '1', label: null, name: null, levels: 1}, function(response) {
			currentGraph = loadTree(response);
			$('#graph-spinner').hide();
		});
		
		// Expand/Collapse handler
		$('#graph-options-control-button').click(function() {
			var graphOptions = $('#graph-options-container');
			
			if (graphOptions.hasClass('closed')) {
				graphOptions.find('#graph-options-control-button span.glyphicon').addClass('glyphicon-resize-small');
				graphOptions.find('#graph-options-control-button span.glyphicon').removeClass('glyphicon-resize-full');
			}
			else {
				graphOptions.find('#graph-options-control-button span.glyphicon').removeClass('glyphicon-resize-small');
				graphOptions.find('#graph-options-control-button span.glyphicon').addClass('glyphicon-resize-full');
			}
			
			graphOptions.toggleClass('closed');
		});
		
		// Checkbox handler
		$('#showGaps').on('change', function() {
			if ($(this).is(':checked')) {
				$('#graph').fadeOut('fast');
				$('#graph-spinner').show();
				
				$.getJSON("getLinkNodeGaps.exec", function(response) {
					if (response.children && response.children.length === 0) {
						$('#gapMessageModal .modal-body p').html('No gaps have been found.');
						$('#gapMessageModal').modal('show');
						$('#graph-spinner').hide();
					}
					else {
						currentGraph.config.constrained = false;
						currentGraph.config.noRequest = false;
						currentGraph.config.levelsToShow = 10;
						
						currentGraph.loadJSON(response);
						currentGraph.reposition();
						
						$('#graph-spinner').hide('fast');
						currentGraph.onClick(currentGraph.root, {
							Move : {
								enable : true,
								offsetX : 500,
								offsetY : -20
							}
						});
						$('#graph').delay(750).fadeIn();
					}
				});
			}
			else {
				$('#graph').fadeOut('fast');
				$('#graph-spinner').show();
				
				loadDefaultTree();
				
				$('#graph-spinner').hide('fast');
				$('#graph').delay(750).fadeIn();
			}
		});
		
		// Searchbar handling for intellisense
		$('#search-text').keyup(function(event) {
			var currentSearch = $(this).val();
			
			// Only do a search if they haven't keyyed an arrow, and the text is over 2 chars
			if (currentSearch.length > 1 && event.keyCode !== 37
					&& event.keyCode !== 38
					&& event.keyCode !== 39
					&& event.keyCode !== 40) {
				
				$.getJSON('getSearchSuggestions.exec', {search: currentSearch}, function(response) {
					currentSuggestions = response;
					var html = '';
					
					for (i = 0; i < currentSuggestions.length; i++) {
						html += '<option value="' + currentSuggestions[i] + '">\n';
					}
					
					$('#search-suggestions').html(html);
				});
			}
		});
		
		// Search Button handler
		$('#search').click(function() {
			var value = $('#search-text').val();
			
			// The typed value must be a suggestion, otherwise we're looking for an entity that doesn't exist
			if (currentSuggestions.indexOf(value) !== -1) {
				$('#graph').fadeOut('fast');
				$('#graph-spinner').show();
				
				$.getJSON("getNodePath.exec", {nodeName: value}, function(response) {
					if (response.children && response.children.length === 0) {
						$('#gapMessageModal .modal-body p').html('There is no path to <b>' + value + '</b>.');
						$('#gapMessageModal').modal('show');
						$('#graph-spinner').hide();
					}
					else {
						currentGraph.config.noRequest = false;
						currentGraph.config.constrained = true;
						currentGraph.config.levelsToShow = 1;
						
						currentGraph.loadJSON(response);
						currentGraph.reposition();
						
						var node = getNodeByName(value, response);
						
						$('#graph-spinner').hide('fast');
						currentGraph.onClick(node.id, {
							Move : {
								enable : true,
								offsetX : 0,
								offsetY : -20
							}
						});
						$('#graph').delay(750).fadeIn();
					}
				});
			}
			else if (value.length > 0) {
				$('#gapMessageModal .modal-body p').html('No entities found with the name <b>' + value + '</b>');
				$('#gapMessageModal').modal('show');
			}
		});
		
		$('#home').click(function() {
			$('#graph').fadeOut('fast');
			$('#graph-spinner').show();
			
			loadDefaultTree();
			
			$('#graph-spinner').hide('fast');
			$('#graph').delay(750).fadeIn();
		});
		
	});
	
	function loadDefaultTree() {
		$.getJSON("getLinkNodeData.exec", {id: '1', label: null, name: null, levels: 1}, function(response) {
			currentGraph.config.noRequest = false;
			currentGraph.config.constrained = true;
			currentGraph.config.levelsToShow = 1;
			
			currentGraph.loadJSON(response);
			currentGraph.reposition();
			
			currentGraph.onClick(currentGraph.root, {
				Move : {
					enable : true,
					offsetX : 500,
					offsetY : -20
				}
			});
			
		});
	}

	function getNodeByName(name, json) {
		return findNodeByName(name, json);
	}

	// Recursive find function that uses name to find
	function findNodeByName(name, currentNode) {
		var i, currentChild, result;
		
		if (name === currentNode.name)
			return currentNode;
		else {
			for (i = 0; i < currentNode.children.length; i++) {
				currentChild = currentNode.children[i];
				
				result = findNodeByName(name, currentChild);
				
				if (result !== false)
					return result;
			}
		}
		
		return false;
	}
	
	var labelType, useGradients, nativeTextSupport, animate;

	(function() {
		var ua = navigator.userAgent, iStuff = ua.match(/iPhone/i)
				|| ua.match(/iPad/i), typeOfCanvas = typeof HTMLCanvasElement, nativeCanvasSupport = (typeOfCanvas == 'object' || typeOfCanvas == 'function'), textSupport = nativeCanvasSupport
				&& (typeof document.createElement('canvas').getContext('2d').fillText == 'function');

		//I'm setting this based on the fact that ExCanvas provides text support for IE
		//and that as of today iPhone/iPad current text support is lame
		labelType = (!nativeCanvasSupport || (textSupport && !iStuff)) ? 'Native'
				: 'HTML';
		nativeTextSupport = labelType == 'Native';
		useGradients = nativeCanvasSupport;
		animate = !(iStuff || !nativeCanvasSupport);
	})();

	function loadTree(json) {
		// Catch blank json data
		if (!json || !json.id) {
			var html = "<p>No data set found. Please use the <a href='#' id='import'>Data Import</a> to upload one.</p>"
			$('#graph').append(html);
		} else {

			// Adjust the graph's height based on the height of the doc
			$('#graph').height($('#traceability').height() + 'px');

			// Create a new ST instance
			// 
			// Note: the ST json data that is initially loaded, is never
			// automatically updated when the graph/view is updated. They
			// are two independent objects. Therefore, whenever nodes/subtrees 
			// are added (after a click), we must manually update it.
			var st = new $jit.ST({
				
				constrained: true,
				
				levelsToShow: 1,
				
				//id of viz container element
				injectInto : 'graph',
				//set duration for the animation
				duration : 500,
				//set animation transition type
				transition : $jit.Trans.Quart.easeInOut,
				//set distance between node and its children
				levelDistance : 80,
				//enable panning
				Navigation : {
					enable : true,
					panning : true
				},

				Node : {
					height : 35,
					width : 150,
					type : 'rectangle',
					color : '#11618f',
					overridable : true,
				},

				Edge : {
					type : 'bezier',
					overridable : true,
					lineWidth : 4,
					color : '#ccc'
				},

				onCreateLabel : function(label, node) {
					label.id = node.id;
					label.innerHTML = node.name;
					label.onclick = function() {
						// TODO: Collapse the node if the node is already selected
						st.onClick(node.id);
					};
					
					//set label styles
					var style = label.style;
					style.width = 150 + 'px';
					style.height = 26 + 'px';
					style.cursor = 'pointer';
					style.color = '#fff';
					style.fontSize = '1.1em';
					style.textAlign = 'center';
					style.paddingTop = '7px';
					style.whiteSpace = "nowrap";
					style.overflow = "hidden";
					style.textOverflow = "ellipsis";
					style.fontFamily = "Open Sans";
				},

				// This function is called just before a node is drawn. The
				// node object being passed here is not the exact object that
				// was loaded into the graph (the json). Rather this is the
				// view object, which has little in common with the json object.
				// Therefore, to use properties that are used to colorize the node,
				// we must grab the original object that we passed in (which holds
				// information about how many children it has)
				onBeforePlotNode : function(node) {
					//add some color to the nodes in the path between the
					//root node and the selected node.
					if (node.selected) {
						node.data.$color = "#4b85b1"; // focused nodes
					} else {
						node.data.$color = "#808080"; // non-focused nodes
						var nodeData = getNode(node.id)['data'];
						
						var childCount = 0;
						var isGap = false;
						
						for (i = 0; i < nodeData.length; i++) {
							if (nodeData[i]['name'] === 'childCount')
								childCount = parseInt(nodeData[i]['value']);
							
							if (nodeData[i]['name'] === 'isGap' && nodeData[i]['value'] === 'true')
								isGap = true;
						}
						// end node
						if (childCount === 0)
							node.data.$color = "#aaa";
						
						// node that has gap
						if (isGap)
							node.data.$color = "#990000"; 
					}
				},

				onBeforePlotLine : function(adj) {
					if (adj.nodeFrom.selected && adj.nodeTo.selected) {
						adj.data.$color = "#4b85b1"; // focused nodes
						adj.data.$lineWidth = 4;
					} else {
						delete adj.data.$color;
						delete adj.data.$lineWidth;
					}
				},

				Tips : {
					enable : true,
					type : 'auto',
					offsetX : 40,
					offsetY : 0,
					onShow : function(tip, node) {
						tip.innerHTML = '<p style="color:#333;font-family:Open Sans;">' + node.name + '</p>';
					}
				},
				
				// Request function. Called when a node is clicked.
				// The onComplete will pass through the result, which is the
				// subtree of the selected node, which is then drawn.
				request: function(nodeId, level, onComplete) {
					if (!currentGraph.config.noRequest) {
						$('#graph-spinner').show();
						
						var node = getNode(nodeId)
						var isGap = false;
						
						for (i = 0; i < node.data.length; i++) {
							if (node.data[i]['name'] === 'isGap' && node.data[i]['value'] === 'true')
								isGap = true;
						}
						
						var id = node.id;
						var name = node.name;
						var label = '';
						
						for (i = 0; i < node.data.length; i++)
							if (node.data[i].name === 'label')
								label = node.data[i].value;
						
						$.getJSON("getLinkNodeData.exec", {id: node.id, label: label, name: name, levels: 2}, function(response) {
							response.data.push({name: 'isGap', value: '' + isGap});
							setNode(response.id, response);
							onComplete.onComplete(nodeId, response);
							
							$('#graph-spinner').hide();
						});
					}
					else
						onComplete.onComplete(nodeId, {});
				},

			});
			
			// load json data
			st.loadJSON(json);
			
			//compute node positions and layout
			st.compute();

			st.onClick(st.root, {
				Move : {
					enable : true,
					offsetX : 500,
					offsetY : -20
				}
			});
			
			// Function that gets the node from the dataset
			function getNode(id) {
				return findNode(id, st.json);
			}
			
			// Recursive function that finds the node with the specified id
			function findNode(id, currentNode) {
				var i, currentChild, result;
				
				if (id === currentNode.id)
					return currentNode;
				else {
					for (i = 0; i < currentNode.children.length; i++) {
						currentChild = currentNode.children[i];
						
						result = findNode(id, currentChild);
						
						if (result !== false)
							return result;
					}
				}
				
				return false;
			}
			
			// Find the node in the data with the specified id,
			// and sets it to the specified node.
			function setNode(nodeId, newNode) {
				var node = getNode(nodeId);
				
				for (var prop in node)
					if (node.hasOwnProperty(prop) && newNode.hasOwnProperty(prop))
						node[prop] = newNode[prop];
			}
			
			st.config.clickable = true;
			
			return st;
		}
	}
</script>

<body style="position: relative;">
	<div id="graph-options-container">
		<div id="graph-options">
			<div id="checkbox-container">
				<div class="roundedCheckbox">
					<input type="checkbox" value="None" id="showGaps" style="visibility: hidden;"/>
					<label for="showGaps"></label>
					<span class="roundedChkboxLabel">Show Gaps</span>
				</div>
			</div>
			<hr>
			<div id="search-container">
				<div class="input-group">
					<input id="search-text" list="search-suggestions" type="text" class="form-control" placeholder="Search for an entity...">
					<datalist id="search-suggestions">
					</datalist>
					<div class="input-group-btn">
						<button id="search" type="button" class="accent-color2 btn"><span class="glyphicon glyphicon-search"></span></button>
						<button id="home" type="button" class="btn" data-toggle="tooltip" data-placement="bottom" title="Return to Original View"><span class="glyphicon glyphicon-home"></span></button>
					</div>
				</div>
			</div>
		</div>
		<div id="graph-options-control-button"><span class="glyphicon glyphicon-resize-small"></span></div>
	</div>
	<div id="graph" style="margin: auto;"></div>
	<div id="graph-spinner"><div class="graph-spinner"></div></div>
	
	<div class="modal fade" id="gapMessageModal" role="dialog">
		<div class="modal-dialog" style="margin: 10% auto;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&#x2573;</span>
					</button>
					<h4 class="modal-title" align="left">Notice</h4>
				</div>
				<div class="modal-body">
					<p>No gaps have been found.</p>
				</div>
				<div class="modal-footer">
					<button style="color: white;" type="button" class="accent-color2" data-dismiss="modal">OK</button>
				</div>
			</div>
		</div>
	</div>
	
</body>

</html>