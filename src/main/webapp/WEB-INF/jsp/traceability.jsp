<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Traceability</title>
</head>

<style>
#traceability-container {
	margin-right: 0px;
    margin-left: 0px;
    margin-top: 0px;
    margin-bottom: 20px;
    width: 100%;
}

#traceability {
	position: relative;
    width: 100%;
    height: 53em;
    margin: auto;
    padding: 20px;
    overflow: hidden;
    background-color: #f1f1ef;
    border-top: 15px solid #aec9df;
    border-bottom: 15px solid #aec9df;
}

</style>

<script>
	$(document).ready(function() {
		$('#traceability').load('linkNode.exec');
	});
</script>

<body>
	<div id="traceability-container" class="container">
		<div class="row">
			<div class="col-md-12">
				<h1 class="page-title">Traceability</h1>
			</div>
		</div>
		<div class="tab-content">
			<div id="traceability"></div>
			<div id="log"></div>
		</div>
	</div>

</body>

<script type="text/javascript">
	
</script>

</html>