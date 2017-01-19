<!DOCTYPE html>
<html>
<head>
<title>Metadata View</title>
<meta charset=utf-8 />

<style type="text/css">
#metadata-container {
	padding: 0px 15px 0px 15px;
	margin-bottom: 20px;
}

#metadata-tab {
	margin-bottom: -3px;
	border-bottom: 0px;
	width: 25%;
	text-align: center;
}

#metadata-container a {
	border-width: 2px 2px 0px 2px;
	border-style: solid;
	color: #000;
	font-size: 1.4em;
	margin-right: 0px;
}

#metadata-container .tab-content {
    border-top: 15px solid #aec9df;
    border-bottom: 15px solid #aec9df;
	padding: 20px;
	background-color: #f1f1ef;
}

#metadata {
	height: 48em;
}

@media (max-width: 768px) {
	#metadata-container li {
		width: 100% !important;
	}
}
</style>

<script>
	var nodeList = null;
	var relationshipList = null;

	$(document).ready(function() {
		$.getJSON("getMetadata.exec", function(data) {
			nodeList = titleizeArray(data.nodeList);
			relationshipList = titleizeArray(data.relationshipList);
			drawMetadataModel();
		});

		// Title-izes each string in array.
		// Ex. old[0] = "abc def" -> new[0] = "Abc Def" 
		function titleizeArray(strArray) {
			for (var i = 0; i < strArray.length; i++) {
				strArray[i].data.name = strArray[i].data.name.titleize();
			}
			return strArray;
		}
	});
</script>

</head>

<body>
	
	<div id="metadata-container">
		<div class="row">
			<div class="col-md-12">
				<h1 class="page-title">Metadata Model</h1>
			</div>
		</div>
		<div class="tab-content">
			<div id="metadata"></div>
		</div>
	</div>
</body>
</html>
