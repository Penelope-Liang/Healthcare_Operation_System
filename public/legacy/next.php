<!doctype html>
<html>
<head>
<link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
<title>Covid Information System</title>
<style>
    h1{
      color: black;
      font-size: 60px;
    }

    body{
      background: url("back.jpg");
      background-repeat:no-repeat;
			background-size: COVER;
      font-size: 30px;
      font-style: Roboto;
    }


    input {
      width: 15%;
      height: 5%;
      border-radius: 05px;
      padding: 8px 15px 8px 15px;
      margin: 10px 0px 15px 0px;
      box-shadow: 1px 1px 2px 1px grey;
      font-size: 80%;
    }

    button {
      background-color: grey;
      border: none;
      color: white;
      padding: 9px 20px;
      text-align: center;
      text-decoration: none;
      display: inline-block;
      font-size: 25px;
      margin: 4px 2px;
      cursor: pointer;
    }
    </style>
</head>

<body>  
<?php
include '../../includes/connectdb.php';
?>
<center>
<h1>Please Enter Your OHIP</h1>
<form action="getpatient.php" method="POST">
  <label></label>
  OHIP: <input type="text" name = "OHIP" placeholder="0000-000-000" pattern="[0-9]{4}-[0-9]{3}-[0-9]{3}"/>
  <button type="search" name="search" value="search">Search</button>

</form>
<?php
$connection = NULL;
?>
</center>
</body>
</html>
