<!DOCTYPE html>
<html>
<head>
<title>ADD</title>
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
      border: 1px;
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
<form action="../covid.php" method="post">
<center>

</form>
<h1>Patient Information System</h1>
<form action="insertpatient.php" method="POST">
OHIP: <input type="text" name="OHIP" pattern="[0-9]{4}-[0-9]{3}-[0-9]{3}"><br>
SpouseOHIP: <input type="text" name="SpouseOHIP" pattern="[0-9]{4}-[0-9]{3}-[0-9]{3}"><br>
First Name: <input type="text" name="FirstName" pattern="[a-zA-Z]+"><br>
Last Name: <input type="text" name="LastName" pattern="[a-zA-Z]+"><br>
Date of Birth: <input type="Date" name="DOB"><br>
<button type="add" name="add" value="add">Add</button>
</form>
<?php
$connection = NULL;
?>
</center>
</body>
