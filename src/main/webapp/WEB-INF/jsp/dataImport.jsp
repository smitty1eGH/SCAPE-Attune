<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<title>Data Import</title>

<style>
#uploadResult {
	font-size: 18px;
	color: #666;
	padding-bottom: 20px;
}

#uploadResult ul {
    margin: 0.75em 0;
    padding: 0 1em;
    list-style: none;
}

#uploadResult li:before { 
    content: "";
    border-color: transparent #666;
    border-style: solid;
    border-width: 0.35em 0 0.35em 0.45em;
    display: block;
    height: 0;
    width: 0;
    left: -1em;
    top: 0.9em;
    position: relative;
}

.form-control.file-caption {
	height: 40px;
	width: 99%;
	border-radius: 0px;
	background-color: inherit;
	border-color: #ccc;
}
	
.input-group-btn a, .input-group-btn .btn:not(button) {
	height: 40px;
	padding-top: 9px;
	border-radius: 0px;
}
	
.progress {
	border-radius: 0;
}
	
.progress-bar-success {
	background-color: #9ac440;
}
	
.progress-bar-success:not(.progress-bar-striped) {
	background-image: linear-gradient(to bottom, #9ac440 0%, #9ac440 100%);
}
</style>

<script>
$(document).ready(function() 
{
	$('#fileUpload').fileinput(
	{
		allowedFileExtensions : [ 'xls' ],
		uploadAsync : true,
		multiple : true,
		maxFileSize : 50000000
	});

	$('#fileUpload').on('fileuploaded', function(event, data, previewId, index) 
	{
		$("#uploadResult").empty();
		$("#errorMessages").empty();
		var errorMessages = data.response;

		if (errorMessages.length == 0) 
		{
			$("#uploadResult").html("<span style='color:green;font-size:18px'>Dataset upload successful. "
					+ "Go to <a href='#' id='dashboard'>Dashboard</a> to view the newly uploaded data.<br><br></span>");
			
		     $.getJSON("getTimelineCreated.exec", function(timelineCreated)
		     {   
		    	 if ( timelineCreated )
		    		 $("#timelineMenu").show();
		    	 else
	                 $("#timelineMenu").hide();
		     });
		} 
		else 
		{
			$("#uploadResult").html("<span style='color:red;font-size:18px'>"
					+ "Data import failed due to the error in the excel file:<br><br></span>");

			$.each(errorMessages, function(index) 
			{
				$("#errorMessages").append('<li style="color:red">' + errorMessages[index]	+ '</li>')
			});
		}
	});

	$('#fileUpload').on('filesuccessremove', function(event, data, previewId, index) 
	{
		$("#uploadResult").empty();
		$("#errorMessages").empty();

		$("#uploadResult").append("Import your data set into the system by uploading files. Supported files are:<br><br>"
				+ "<ul>"
				+ "<li>An excel file (.xls) containing the data set</li>"
				+ "</ul>");
	});

	$('body').on('click', '#dashboard', function() 
	{
		$('#main').load('dashboard.exec', function() 
		{
			  $('#main').fadeIn();
	    });
	});
});
</script>
</head>

<body>
<div class="row">
	<div class="col-md-12">
		<h1 class="page-title" style="padding-left: 15px;">Data Import</h1>
	</div>
</div>
<div class="container kv-main top-border-accent-color-Dataset bottom-border-accent-color-Dataset"
	style="padding-top: 15px; width: 98%; background: #f1f1ef">
	<div id="uploadResult">
		Import your data set into the system by uploading files. Supported files are: <br>
		<ul>
			<li>An excel file (.xls) containing the data set</li>
		</ul>
	</div>
       <br>
	<ul id="errorMessages"></ul>

	<form enctype="multipart/form-data">
		<div class="form-group">
			<input id="fileUpload" name="file[]" class="file" type="file"
				multiple="multiple" data-preview-file-type="any"
				data-upload-url="upload.exec" data-preview-file-icon="">
		</div>
	</form>

	<br>
</div>
</body>
</html>