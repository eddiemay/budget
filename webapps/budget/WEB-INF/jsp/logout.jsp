<div>

	<h2 id="nav">&nbsp;Navigation .: <a href="home">Home</a>&nbsp;>&nbsp;Logout</h2>

	<h4>Logout Complete</h4>
	<div class="bgGrey">
		You have successfully logged out.
	</div>

	<h4>&nbsp;Login</h4>
	<form class="bgGrey" name="loginF" action="login" method="post" onLoad="readCookieSetInput('login','u')" onSubmit="if(getElementById('rem').checked){makeCookie('login', getElementById('u').value, { expires: 30 });}else{rmCookie('login');}">

		<label for="usr">Enter your Email Address):</label>
		   <input type="text" name="u" id="usr" size="30" value=""/>
		<br/>
		<label for="pss">Enter your Password:</label>
		   <input type="password" name="key" id="pss" size="30" />

		<br/>
		<input type="checkbox" name="rem" checked/>Remember me on this computer<br/>
		<input type="image" src="img/continue.gif" />

	</form>

</div>

