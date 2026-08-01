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
<center>
<h1>Patient Information System</h1>
<?php
$OHIP="";
$SpouseOHIP="";
$FirstName="";
$LastName="";
$DOB="";

if ((isset($_POST["add"]))) {
    include '../../includes/connectdb.php';    
    if (empty($_POST['OHIP']) || empty($_POST['FirstName']) || empty($_POST['LastName']) || empty($_POST['DOB'])) {
        echo "<script> location.href='addpatient.php'; alert('Please finish the empty field');
             </script>";
    } else {
        $OHIP = $_POST['OHIP'];
        $SpouseOHIP = $_POST['SpouseOHIP'];
        $FirstName = $_POST['FirstName'];
        $LastName = $_POST['LastName'];
        $DOB = $_POST['DOB'];
        $sql = "INSERT INTO Patient (OHIP, SpouseOHIP, FirstName, LastName, DOB) VALUES('$OHIP', 
        '$SpouseOHIP', '$FirstName', '$LastName', '$DOB')";
        if ($connection->query($sql) == True) {
            echo "New record created successfully";
        } 
        $connection = NULL;
    }
}
?>
<br><br>
<form action="vaccinationdata.php" method="POST">
  <label></label>
  <button type="next" name="next" value="next">Next</button><br><br>
</form>
</center>
</body>
