<!DOCTYPE html>
<html lang="en" style="height: 100%;">
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"  %>

<head>
<link rel="stylesheet" href="css/bootstrap.css" >
<link href='css/fonts.googleapis.com.open.sans.css' rel='stylesheet' type='text/css'>

<script src="js/jquery-2.1.3.js"></script>
<script src="js/bootstrap.js"></script>   
<script src="js/main.js"></script>

<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
	body {
		font-family: Open Sans;
		background-color: #3a4f60;
		background-image: url("images/background.png");
		background-size: 100%;
		color: rgb(101, 118, 132);
	}

	#login-container {
		margin-top: 20%;
		margin-left: auto;
		margin-right: auto;
		width: 32em;
		align-items: center;
		justify-content: center;
	}
	
	#login-form {
		clear: both;
		background-color: white;
		padding: 25px;
		margin-top: 35px;
	}
	
	input.form-control {
		padding: 10px;
		border-color: transparent !important;
		height: auto;
		font-size: 130%;
		border: 0 !important;
		color: rgb(101, 118, 132);
		box-shadow: 0 0 !important;
		outline: none !important;
		border-radius: 0px;
		background-color: rgb(240, 240, 240);
	}
	
	input[type="checkbox"] {
		border-radius: 0px;
	}
	
	.input-group-addon {
		border-radius: 0;
		border: 0;
		background-color: rgb(240, 240, 240);
		color: rgb(101, 118, 132);
	}
	
	button[type="submit"] {
		background-color: rgb(77, 133, 178);
		font-size: 130%;
		color: white !important;
		padding: 15px;
		border-radius: 0px;
	}
	
	.form-group {
		margin-bottom: 25px;
	}
</style>
</head>

<body style="height: 100%;">
							
	<div class="container" style="height: 100%;">
		<div class="row" style="height: 100%;">
			<div class="col-sm-12" style="height: 100%;">
				<div id="login-container">
					<img src="icons/attune_logo.png" style="width: 10em; display: block; margin: auto;"/>
					<div id="login-form">
						<form  role="form" action="<c:url value='j_spring_security_check'/>" method="post">
							<div class="form-group">
								<div class="input-group">
									<div class="input-group-addon"><span class="glyphicon glyphicon-user"></span></div>
									<input type="text"  class="form-control" placeholder="Username" name="j_username">
								</div>
							</div>
							<div class="form-group">
								<div class="input-group">
									<div class="input-group-addon"><span class="glyphicon glyphicon-lock"></span></div>
									<input type="password"  class="form-control" placeholder="Password" name="j_password">
								</div>
							</div>
							<div class="form-group">
								<div style="display: table; width: 100%">
									<div class="checkbox" style="display: table-cell; vertical-align: middle;">
										<label>
											<input id="rememberMe" type="checkbox"> Remember Me</input>
										</label>
									</div>
									<a id="forgotPassword" href="#" class="pull-right">Forgot Password</a>
								</div>
							</div>
							<button type="submit" id="submitLogin" class="btn btn-block">Log In</button>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	
	
</body>

</html>