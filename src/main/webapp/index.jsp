<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>FixFm Service</title>
    <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="style.css">
</head>
<body>

<div id="header"><h1>FixFm Service</h1></div>
<div><h4>A simple way to fix your wrong Last.fm tags.</h4></div>
<div class="form-block">
    <form id="form" role="form" method="post" action="auth/">
        <div class="form-group">
            <label for="login">Login</label>
            <input type="text" class="form-control" id="login" name="login" placeholder="Enter login">
        </div>
        <input type="submit" class="btn btn-success btn-lg" style="background-color: #16A085;">Connect to the app</button>
    </form>
</div>

</body>
</html>