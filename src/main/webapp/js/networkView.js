var nodeHeight = "70";
var nodeWidth = "70";
var nodeColor = "rgb(32, 139, 133)";
var nodeLabelColor = "white";
var nodeLabelOutlineColor = "rgb(32, 139, 133)";
var nodeLabelOutlineWidth = "3px";

var relationshipColor = "white";
var relationshipLabelColor =  "rgb(102, 80, 116)";
var relationshipLineStyle = "solid";

var height = $('#metadata').height();

function drawNetworkView()
{
	$('#metadata').cytoscape(
	{
		layout: 
		{ 
			name: 'springy',
			animate: true,
			fit: true,
			padding: 30,
			random: false,
			infinite: false,
			
			stiffness: 100,
			repulsion: 100,
			damping: 0.2,
		},
		style: getStyleSheet(),
		elements: 
		{
			nodes: nodeList,
			edges: relationshipList
		},
		zoomingEnabled: false,
		userPanningEnabled: false,
		boxSelectionEnabled: true,
		
	    ready: function()
	    {
	        window.cy = this;
	        
	        //remove selected node on right mouse click
	        cy.on('cxttapend', function(e) 
            {
                node = e.cyTarget; 
                if ( node !== cy )
                    node.remove();
            });
	        
	        cy.nodes().on("click", function(event){
	             var node = this;
	 			$.getJSON( "getNetworkView.exec", {entity: node.data('entity'), name: node.data('id')}, function( data ) 
						{
							nodeList = titleizeArray(data.nodeList);
							relationshipList = titleizeArray(data.relationshipList);
							drawNetworkView();
						});
	 			entity = node.data('entity');
	 			name = node.data('id');
	 			
	 			$("#baseballTitle").html(name);

				// Title-izes each string in array.
				// Ex. old[0] = "abc def" -> new[0] = "Abc Def" 
				function titleizeArray(strArray)
				{
					for(var i = 0; i < strArray.length; i++)
					{
						strArray[i].data.name = strArray[i].data.name.titleize();
					}
					return strArray;
				}
	        });
		}
	});
}


function getStyleSheet() 
{	
	// Check here for configuration details:
	// http://cytoscape.github.io/cytoscape.js/#style/properties
	
	return cytoscape.stylesheet()
		.selector('node')
		.css({
			'content': 'data(name)',
			'text-valign': 'center',
			'color': nodeLabelColor,
			'text-outline-width': nodeLabelOutlineWidth,
			'text-outline-color': nodeLabelOutlineColor,
            'font-family': 'Myriad Pro Bold',
            'font-size': 16,
			'background-color': nodeColor,
			'height': nodeHeight,
			'width': nodeWidth,
			'visibility': 'visible'
		})
		.selector(":selected")
		.css({
			'border-width': 3,
			'border-color': '#fff'
		})
		.selector('edge')
		.css({
			'content': 'data(name)',
			'target-arrow-shape': 'triangle',
			'background-color': relationshipColor,
	        'width': '3px',
	        'curve-style': 'bezier',
			'line-color': relationshipColor,
			'line-style': relationshipLineStyle,
			'color': relationshipLabelColor,
            'font-family': 'Myriad Pro Bold',
            'font-size': 16,
			'target-arrow-color': relationshipColor,
			'source-arrow-color': relationshipColor
                        
		})
		.selector('.faded')
		.css({
			'opacity': 0.25,
			'text-opacity': 0
		})
		.selector('.disabled')
		.css({
			'background-color': '#F5F5F5',
			'color': '#d3d3d3',
			'fill-color': '#F5F5F5',
			'line-color': '#d3d3d3',
			'border-color': '#d3d3d3'
		});
}
