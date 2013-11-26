<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>FixFm Service</title>
    <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="style.css">
    <script src="//code.jquery.com/jquery-2.0.3.js"></script>
    <script src="script.js"></script>
</head>
<body>

<div id="header"><h1>FixFm Service</h1></div>
<div><h4>A simple way to fix your wrong Last.fm tags.</h4></div>
<div class="form-block">
    <form role="form">
        <div class="form-group">
            <label for="login">Login</label>
            <input type="text" class="form-control" id="login" placeholder="Enter login">
        </div>
        <div class="form-group">
            <label for="artist">Artist</label>
            <input type="text" class="form-control" id="artist" placeholder="Enter artist">
        </div>
        <div class="form-group">
            <label for="artist">Album</label>
            <input type="text" class="form-control" id="album" placeholder="Enter album">
        </div>
        <div class="form-group">
            <label for="old">Old Song Tag</label>
            <input type="text" class="form-control" id="old" placeholder="Enter old tag">
        </div>
        <div class="form-group">
            <label for="new">New Song Tag</label>
            <input type="text" class="form-control" id="new" placeholder="Enter new tag">
        </div>
        <button type="button" class="btn btn-success btn-lg" style="background-color: #16A085;">Fix</button>
    </form>
</div>

</body>
</html>