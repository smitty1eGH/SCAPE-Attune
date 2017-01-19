var nodeHeight = "70";
var nodeWidth = "70";
var nodeColor = "#4b85b1";
var nodeLabelColor = "white";
var nodeLabelOutlineColor = "grey";
var nodeLabelOutlineWidth = "2px";

var relationshipColor = "#ccc";
var relationshipLabelColor = "rgb(102, 80, 116)";
var relationshipLineStyle = "solid";

var height = $('#metadata').height();

function drawMetadataModel()
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
		}
	});
}
/*

//Method to toggle the node's edges on and off
function toggleNodeEdges(nodeId, showHide)
{
        var sourced = cy.edges("[source='" + nodeId + "']");
        var targeted = cy.edges("[target='" + nodeId + "']");

        if(showHide === 'show')
        {
            //If trying to show edges, go through each source and 
            //target node to make sure they are not disabled
            for(var x = 0; x < sourced.length; x++)
            {
                var id = sourced[x].data('target');
                if(cy.$("#" + id).hasClass('disabled') !== true){
                    sourced[x].show();
                }
            }
            
            for(var x = 0; x < targeted.length; x++)
            {
                var id = targeted[x].data('source');
                if(cy.$("#" + id).hasClass('disabled') !== true){
                    targeted[x].show();
                }
            }
        } 
        else 
        {
            //If hiding edges, just hide them all for this node
            sourced.hide();
            targeted.hide();
        }
}

*/
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
            'font-family': 'Open Sans',
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
	        'width': '5px',
	        'curve-style': 'bezier',
			'line-color': relationshipColor,
			'line-style': relationshipLineStyle,
			'color': relationshipLabelColor,
            'font-family': 'Open Sans',
            'font-size': 17,
            'font-weight': 'bold',
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
