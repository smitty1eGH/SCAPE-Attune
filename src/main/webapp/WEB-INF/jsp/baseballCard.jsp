<head>
<style>
#baseballCardModal div.modal-dialog {
	width: 850px;
	margin: 5% auto;
}
	
#baseballContent {
	padding: 0px;
}

#baseballCardModal div div div.modal-header {
	color: white;
	background-color: transparent;
	position: relative;
	border: 0;
}

#baseballCardModal div div div.modal-header:after {
    width: 100%;
    display: block;
    text-align: center;
    background-color: white;
    height: 1px;
    margin-top: 10px;
    content: '';
}

#baseballCardModal .modal-header button.close {
	margin-top: -0.4em;
	text-shadow: 0 0;
	margin-right: 0.1em;
	font-weight: normal;
	color: white;
	opacity: 1;
}

.modal-header button.close:hover {
	opacity: 0.75;
}

#baseballHeader {
    text-align: left;
    background-color: #343F47;
    color: white;
    background: url('./icons/baseball_card.png');
    background-repeat: no-repeat;
    background-size: cover;
    background-position-y: 70%;
    position: relative;
}

#baseballHeader:before {
	position: absolute;
	background-color: black;
	opacity: 0.55;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	content: '';
}

#baseballTitle {
    padding-top: 30px;
    padding-bottom: 35px;
    padding-left: 25px;
    padding-right: 25px;
    font-size: 30px;
    position: relative;
    width: 100%;
    font-weight: 100;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

#baseballTabRow {
	padding-left: 225px;
}

#baseballNav {
	text-align: left;
    line-height:30px;
    background-color:#f1f1ef;
    height:545px;
    width:225px;
    float:left;
    padding: 10px 20px;
    overflow-x: hidden;
	font-size: 17px;
	position: relative;
}

#propertiesSection:before {
	content: '';
	height: 90%;
	display: block;
	width: 1px;
	background-color: #ddd;
	position: absolute;
	top: 5%;
	left: 0;
}

#propertiesSection {
	position: relative;
	text-align: left;
    height:545px;
    width:625px;
    padding:10px 20px;
    overflow: scroll;	 	
  	overflow-x: hidden;	
	font-size: 17px;
	background-color: #f1f1ef;
}

#baseballCardModal a:link {
    text-decoration: none;
}

#baseballCardModal a:visited {
    text-decoration: none;
}

#propertiesList, #longPropertiesList {
	padding-left: 10px;
	margin: 0;
}

#longPropertiesList {
	margin-bottom: 20px;
}

#propertiesList li {
    float: left;
    width: 50%;
}

#baseballCardModal ul {
    list-style-type: none;
}

#baseballCardModal #parentLinks, #childrenLinks {
	margin-bottom: 20px;
}

#baseballCardModal #metadata-container {
	padding: 15px 15px 15px 15px;
}

#baseballCardModal #metadata-container a {
	border-color: rgba(195, 99, 48, 1);
	border-width: 2px 2px 0px 2px;
	border-style: solid;
	color: white;
	font-size: 1.4em;
	margin-right: 0px;
}

#baseballCardModal #metadata {
    height:515px;
}

#baseballCardModal #metadata div canvas {
	position: absolute;
	left: 0px;
}

@media (max-width: 768px) {
	#metadata-container li {
		width: 100% !important;
	}
}
#baseballTabRow li {
	margin-bottom: 0px;
	border-radius: 0;
}

#baseballTabRow li a {
	margin: 0px;
	font-size: 1.3em;
	/* color: white; */
	border: 0 !important;
	border-radius: 0;
	text-decoration: none;
	cursor: pointer;
}

#detailsViewTab {
  	text-align: center;
  	border-left: 1px solid black;
  	border-top: 1px solid black;
  	border-right: 1px solid black;
  	background-color: white;
	width: 28%;
}

#detailsViewTab.active a {
	opacity: 1;
	background-color: transparent;
}

#detailsViewTab a {
	opacity: 0.35;
	background-color: transparent;
}

#detailsViewTab:not(.active) a:hover {
	opacity: 0.5;
	background-color: transparent;
}

#networkViewTab {
  	text-align: center;
	width: 28%;
  	border-left: 1px solid black;
  	border-top: 1px solid black;
  	border-right: 1px solid black;
  	background-color: white;
}

#networkViewTab.active a {
	opacity: 1;
	background-color: transparent;
}

#networkViewTab a {
	opacity: 0.35;
	background-color: transparent;
}

#networkViewTab:not(.active) a:hover {
	opacity: 0.5;
	background-color: transparent;
}

#ratingsViewTab {
  	text-align: center;
	width: 28%;
  	border-left: 1px solid black;
  	border-top: 1px solid black;
  	border-right: 1px solid black;
  	background-color: white;
}

#ratingsViewTab.active a {
	opacity: 1;
	background-color: transparent;
}

#ratingsViewTab a {
	opacity: 0.35;
	background-color: transparent;
}

#ratingsViewTab:not(.active) a:hover {
	opacity: 0.5;
	background-color: transparent;
}

#originalViewTab {
	padding-top: 5px;
	padding-bottom: 5px;
	padding-left: 15px;
	padding-right: 15px;
}

#originalViewButton {
	width: 1.75em;
	height: 1.75em;
	position: relative;
	float: right;
	margin-top: -2.1em;
	margin-right: 1.4em;
	background-color: transparent;
	padding-top: 5px;
	padding-right: 6px;
	padding-bottom: 1px;
	padding-left: 6px;
	cursor: pointer;
}

h3.baseball-property {
	font-size: 22px;
    margin: 10px 0;
    width: 100%;
    overflow: hidden;
    white-space: nowrap;
    color: #444;
    text-overflow: ellipsis;
}

h3.baseball-property + p {
	font-size: 16px;
	color: #666;
}

h3.baseball-property + a {
	font-size: 16px;
}

</style>

<script>
var nodeList = null;
var relationshipList = null;
var ratings = {};
var entity = '${entity}';
var name = '${name}';	

    $(document).ready(function()
    {	
		$.getJSON("getAttributesMap.exec", {entity: entity, name: name}, function(map)
		{	
			$.each(map, function( key, val ) 
			{
				var output = '';
				output += '<li><h3 class="baseball-property">';
				output += key;
				output += '</h3>';
				if (key == 'Attachments')
				{							
					output = '';
					output += '<h3 class="baseball-property">';
					output += key;
					output += '</h3>';
					output += '<a href="/attune/attachments/' +val+ '" target="_blank">';
					output += val;
					output += '</a><br>';
					output += '';

					$("#attachmentsLinks").append(output);	
				}
				if (key == 'location')
                {
                    output += '<a href=' + val + ' target="_blank">' + val + '</a>';
                    output += '</li>';
                    $("#propertiesList").append(output);
                }
                else if (key == 'schema')
                {
                    output += '<a mimetype="application/json" download="schema.json" href=' + val + ' target="_blank">' + val + '</a>';
                    output += '</li>';
                    $("#propertiesList").append(output);
                }
                else if (key == 'queue')
                {
                    output += '<a href="' + val + '" target="_blank">' + val + '</a>';
                    output += '</li>';
                    $("#propertiesList").append(output);
                }
				else
				{
					output += '<p>' + val + '</p>';
					output += '</li>';
					if (val.length > 30)
						$("#longPropertiesList").append(output);
					else 
					{
						$("#propertiesList").append(output);
					}
						
				}					
			});			
		});	
		
		$.getJSON("getParentMap.exec", {entity: entity, name: name}, function(map)
		{	
			if (!jQuery.isEmptyObject(map))
			{
				$.each(map, function( key, val ) 
				{
					var output = '';
					output += '<h3 class="baseball-property">';
					output += key;
					output += '</h3>';
					for (i = 0; i < val.length; i++)
					{
						output += '<a href="#" onclick="baseballCardLink(&apos;' +key+ '&apos;, &apos;' +val[i]+ '&apos;)"">';
						output += val[i];
					output += '</a><br>';	
					}

					$("#parentLinks").append(output);
				});	
			}
		});
		
		$.getJSON("getChildrenMap.exec", {entity: entity, name: name}, function(map)
		{
			if (!jQuery.isEmptyObject(map))
			{
				$.each(map, function( key, val ) 
				{
				    // Do not show ratings on the left-side nav
				    if(key !== 'Rating') {
					var output = '';
					output += '<h3 class="baseball-property">';
					output += key;
					output += '</h3>';
					for (i = 0; i < val.length; i++)
					{
						output += '<a href="#" onclick="baseballCardLink(&apos;' +key+ '&apos;, &apos;' +val[i]+ '&apos;)"">';
						output += val[i];
						output += '</a><br>';						
					}
					$("#childrenLinks").append(output);					
                    }
				});	
			}
		});
		
        $.getJSON("getRatings.exec", {objectName: name}, function(map)
        {
            ratings = map;
        });
		
	    $("#networkViewTab").click(function() {
	        $("#baseballSection").hide();
	        $("#ratingsSection").hide();
			$.getJSON( "getNetworkView.exec", {entity: entity, name: name}, function( data ) 
			{
				nodeList = titleizeArray(data.nodeList);
				relationshipList = titleizeArray(data.relationshipList);
				
				drawNetworkView();
			});
			
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

	        $("#metadata-container").show();
	    });
	    
	    $("#ratingsViewTab").click(function() {
            $("#baseballSection").hide();
            $("#metadata-container").hide();

            var container = $("#ratingsInnerSection");

            // Only make the objects once
            if(container.children().length === 0) {

                // create user rating section
                var userRating = null;
                var user = getUser();

                console.log(ratings);

                if(typeof ratings[user] !== 'undefined') {
                    userRating = baseballCard.createUserRatingDisplay(user, ratings[user]);
                } else {
                    userRating = baseballCard.createUserRatingDisplay(user);
                }
                container.append(userRating);

                var i = 1;
                $.each(ratings, function( key, val )
                {
                    if(key !== user) {
                        container.append(baseballCard.createRatingDisplay(val, i));
                        i++;
                    }
                });
			}

			container.css('max-height', window.innerHeight - 340);

            $("#ratingsSection").show();
        });
	    
	    $("#detailsViewTab").click(function(){
	    	var url = "baseballCard.exec?entity="+encodeURIComponent(entity)+"&name="+encodeURIComponent(name);

			$("#baseballContent").load(url);
	        $("#baseballSection").show();
	        $("#metadata-container").hide();
	        $("#ratingsSection").hide();
	    });
	    

        $("#metadata-container").hide();
        $("#ratingsSection").hide();

		$('[data-toggle="tooltip"]').tooltip();

		if(! (entity === 'Dataset' || entity === 'Application' || entity === 'Service') ) {
		    $("#ratingsViewTab").hide();
		}
    });
    
    function baseballCardLink(entity, name) {

    	var url = "baseballCard.exec?entity="+encodeURIComponent(entity)+"&name="+encodeURIComponent(name);

		$("#baseballContent").load(url);
    }
 
    function originalLink() {

    	var url = "baseballCard.exec?entity="+encodeURIComponent(localStorage.getItem("originalEntity"))+"&name="+encodeURIComponent(localStorage.getItem("originalName"));

		$("#baseballContent").load(url);
    }
    
    function submit() {
        $('#ratingForm').removeClass("has-error");
        $('#ratingForm').removeClass("has-success");
        $('#errorTextRating').text('');
        $('#errorTextTitle').text('');
        $('#errorTextComment').text('');
        $('#successMessage').text('');


        var rating = $('input[name=rating0]:checked').val();
        var title = $('#titleText').val();
        var comment = $('#comment').val();

        hasError = false;
        if(typeof rating === 'undefined') {
            hasError = true;
            $('#errorTextRating').text("Please select a star rating between 1 and 5");
        }
        if(!title) {
            hasError = true;
            $('#errorTextTitle').text("Please fill in a title for your review");
        }
        if(!comment) {
            hasError = true;
            $('#errorTextComment').text("Please leave a comment with your review");
        }

        if(hasError) {
            $('#ratingForm').addClass("has-error");
            return;
        }

        var data = {
            entity: entity,
            user: getUser(),
            rating: rating,
            title: title,
            comment: comment,
            objectName: $('#baseballTitle').text()
        };

        var operation = $('#ratingButton').text().toLowerCase();

        $.ajax({
            url: "createRating.exec",
            method: "POST",
            data: data,
            success: function( data ) {
                if(operation == 'submit') { operation += 'e'; }

                $('#successMessage').text('Rating successfully ' + operation + 'd');
                $('#ratingForm').addClass("has-success");
                $('#ratingButton').text('Update');
                $('.starLegend').first().text("Your review:");

                setTimeout(function() {
                    $('#ratingForm').removeClass("has-success");
                    $('#successMessage').text('');
                }, 2000);
            },
            error: function(data) {
                $('#errorTextTitle').text("Failed to " + operation + " rating, please try again");
                $('#ratingForm').addClass("has-error");
            }
        });
    }

    function getUser() {
        // TODO: do we know the user another way?
        return $('#profile').text().trim();
    }
    
</script>
</head>

<body>
	<div id="baseballHeader">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&#x2573;</span></button>
			<h4 class="modal-title" align="left">Details View</h4>
		</div>
		<div id="baseballTitle">${name}</div>
		<ul class="nav nav-tabs" role="tablist" id="baseballTabRow">
			<li class='active' id="detailsViewTab" role="presentation">
				<a data-toggle="tab">Details View</a>					    
			</li>
			<li class='inactive' id="networkViewTab" role="presentation">
				<a data-toggle="tab" aria-expanded="false">Network View</a>					    
			</li>
			<li class='inactive' id="ratingsViewTab" role="presentation">
                <a data-toggle="tab" aria-expanded="false">Ratings</a>
            </li>
			<li id="originalViewTab">
				<a id="originalViewButton" onclick="originalLink()" data-toggle="tooltip" data-placement="right" title="Original View"><span class="glyphicon glyphicon-home"></span></a>
			</li>
		</ul>
	</div>
	
	<div class="tab-content">
		<div id="baseballSection">
			<div id="baseballNav">
				<div id ="parentLinks"></div>
				<div id ="childrenLinks"></div>
				<div id ="attachmentsLinks"></div>	
			</div>
			<div id="propertiesSection">
				<ul id="longPropertiesList"></ul>
				<ul id="propertiesList"></ul>
			</div>
		</div>
		<div id="metadata-container">
			<div class="metadata-content">
				<div id="metadata" class="base-color1"></div>
			</div>
		</div>
        <div id="ratingsSection">
            <ul id="ratingsInnerSection"/>
        </div>
	</div>
</body>
