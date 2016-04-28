<%@page import="com.digitald4.common.model.Company" %>
<%Company company = Company.get();%>
<!doctype html>
<!--[if lt IE 8 ]><html lang="en" class="no-js ie ie7"><![endif]-->
<!--[if IE 8 ]><html lang="en" class="no-js ie"><![endif]-->
<!--[if (gt IE 8)|!(IE)]><!--><html lang="en" class="no-js"><!--<![endif]-->
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	
	<title><%=company.getName()%></title>
	<meta name="description" content="">
	<meta name="author" content="">
	
	<!-- Global stylesheets -->
	<link href="css/reset.css" rel="stylesheet" type="text/css">
	<link href="css/common.css" rel="stylesheet" type="text/css">
	<link href="css/form.css" rel="stylesheet" type="text/css">
	<link href="css/standard.css" rel="stylesheet" type="text/css">
	<link href="css/special-pages.css" rel="stylesheet" type="text/css">
	
	<!-- Favicon -->
	<link rel="shortcut icon" type="image/x-icon" href="favicon.ico">
	<link rel="icon" type="image/png" href="favicon-large.png">
	
	<!-- Modernizr for support detection, all javascript libs are moved right above </body> for better performance -->
	<script src="js/libs/modernizr.custom.min.js"></script>
	
</head>

<!-- the 'special-page' class is only an identifier for scripts -->
<body class="special-page login-bg dark">
	
	<% String message = (String)request.getAttribute("message");
	if (message != null) {%>
	<section id="message">
		<div class="block-border"><div class="block-content no-title dark-bg">
			<p class="mini-infos"><%=message%></p>
		</div></div>
	</section>
	<%}%>
	
	<section id="login-block">
		<div class="block-border"><div class="block-content">
			
			<!--
			IE7 compatibility: if you want to remove the <h1>,
			add style="zoom:1" to the above .block-content div
			-->
			<h1><%=company.getName()%></h1>
			<div class="block-header">Please login</div>
			
			
		  <% String error = (String) request.getAttribute("error");
		   if (error != null) {%>
		   	<p class="message error no-margin"><%=error%></p>
		   <%}%>
			<form class="form with-margin" name="login-form" id="login-form" method="post" action="login">
				<p class="inline-small-label">
					<label for="username"><span class="big">Username or Email</span></label>
					<input type="text" name="username" id="username" class="full-width" value="<%=request.getAttribute("username")!=null?request.getAttribute("username"):""%>">
				</p>
				<p class="inline-small-label">
					<label for="pass"><span class="big">Password</span></label>
					<input type="password" name="pass" id="pass" class="full-width" value="">
				</p>
				
				<button type="submit" class="float-right">Login</button>
			</form>
			
			<form class="form" id="password-recovery" method="post" action="login">
				<fieldset class="grey-bg no-margin collapse">
					<legend><a href="#">Lost password?</a></legend>
					<p class="input-with-button">
						<label for="recovery-mail">Enter your e-mail address</label>
						<input type="text" name="recovery-mail" id="recovery-mail" value="">
						<button type="button">Send</button>
					</p>
				</fieldset>
			</form>
		</div></div>
	</section>
	
	<!--
	
	Updated as v1.5:
	Libs are moved here to improve performance
	
	-->
	
	<!-- Generic libs -->
	<script src="js/libs/jquery-1.10.2.min.js"></script>
	
	<!-- Template libs -->
	<script src="js/common.js"></script>
	<script src="js/standard.js"></script>
	<script src="js/jquery.tip.js"></script>
	
<!-- login script -->
	<script>
	
		$(document).ready(function()
		{
			// We'll catch form submission to do it in AJAX, but this works also with JS disabled
			$('#login-form').submit(function(event)
			{
				// Stop full page load
				event.preventDefault();
				
				// Check fields
				var login = $('#username').val();
				var pass = $('#pass').val();
				
				if (!login || login.length == 0)
				{
					$('#login-block').removeBlockMessages().blockMessage('Please enter your user name', {type: 'warning'});
				}
				else if (!pass || pass.length == 0)
				{
					$('#login-block').removeBlockMessages().blockMessage('Please enter your password', {type: 'warning'});
				}
				else
				{
					var submitBt = $(this).find('button[type=submit]');
					submitBt.disableBt();
					
					// Target url
					var target = $(this).attr('action');
					if (!target || target == '') {
						// Page url without hash
						target = document.location.href.match(/^([^#]+)/)[1];
					}
					
					// Request
					var data = {
							username: login,
							pass: pass,
							'keep-logged': $('#keep-logged').attr('checked') ? 1 : 0
						},
						redirect = $('#redirect'),
						sendTimer = new Date().getTime();
					
					if (redirect.length > 0) {
						data.redirect = redirect.val();
					}
					
					// Send
					$.ajax({
						url: target,
						dataType: 'json',
						type: 'POST',
						data: data,
						success: function(data, textStatus, XMLHttpRequest) {
							if (data.valid) {
								// Small timer to allow the 'checking login' message to show when server is too fast
								var receiveTimer = new Date().getTime();
								if (receiveTimer - sendTimer < 500) {
									setTimeout(function() {
										document.location.href = data.redirect;
									}, 500 - (receiveTimer - sendTimer));
								} else {
									document.location.href = data.redirect;
								}
							} else {
								// Message
								$('#login-block').removeBlockMessages().blockMessage(data.error || 'An unexpected error occured, please try again', {type: 'error'});
								submitBt.enableBt();
							}
						},
						error: function(XMLHttpRequest, textStatus, errorThrown) {
							// Message
							$('#login-block').removeBlockMessages().blockMessage('Error while contacting server, please try again', {type: 'error'});
							submitBt.enableBt();
						}
					});
					
					// Message
					$('#login-block').removeBlockMessages().blockMessage('Please wait, checking login...', {type: 'loading'});
				}
			});
		});
	
	</script>
	
</body>
</html>
