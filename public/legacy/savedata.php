<!doctype html>
<html>
<head>
<link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
<title>Vaccination Data</title>
<style>
    h1{
      color: black;
      font-size: 50px;
    }

    h2{
      color: black;
      font-size: 30px;
    }

    h3{
      color: black;
      font-size: 30px;
    }

    h4{
      color: black;
      font-size: 30px;
    }

    h5{
      color: black;
      font-size: 30px;
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


    *,
    *::before,
    *::after {
    box-sizing: border-box;
    }


select {
    text-align-last: center;
    width: 360px;
    padding: 5px;
    font-size: 20px;
    line-height: 1;
    border: 0;
    border-radius: 20px;
    height: 50px;
    background: url(http://cdn1.iconfinder.com/data/icons/cc_mono_icon_set/blacks/16x16/br_down.png) no-repeat right #ddd;
    -webkit-appearance: none;
    background-position-x: 335px;
}

select .lt { text-align: center; }

</style>
</head>

<body>  
<center>
<?php
if (isset($_POST['save'])){
  include "../../includes/connectdb.php";
  $location = $_POST['location'];
  $number = $_POST['number'];
  $d=$_POST['d'];
  $t=$_POST['t'];
  $n=$_POST['n'];
  $query = "SELECT * FROM Vaccination LEFT JOIN Patient p ON p.OHIP=Vaccination.OHIP WHERE Vaccination.OHIP='$location'";

$result1 = $connection->query($query);
$row= $result1->fetch();
  if(empty($row)){
    $s="INSERT INTO Vaccination VALUES('$location','$n','$number','$d','$t')";
    $connection->query($s);
    $result1 = $connection->query($query);
    $row= $result1->fetch();
  }
}
    
?>
<h1>Vaccination Search</h1>

<table>
    <tbody>
      <?php
          echo '</tr>';
          echo '<div><table border=1>';
          echo '<thead><tr><th>OHIP</th><th>Clinic Name</th><th>Lots</th><th>Date</th><th>Time</th><th>Spouse OHIP
          </th><th>First Name</th><th>Last Name</th><th>Date of Birth</th></tr></thead>';
          echo '<tbody>';
          echo "<tr><td>{$row['OHIP']}</td><td>{$row['1']}</td><td>{$row['2']}</td><td>{$row['3']}</td><td>{$row['4']}
          </td><td>{$row['SpouseOHIP']}</td><td>{$row['FirstName']}</td><td>{$row['LastName']}</td><td>{$row['DOB']}</td></tr>";
        ?>
    </tbody>
    </table>  

    <br>
<form action="../covid.php">
  <label></label>
  <button type="next" name="next" value="next">Return to Homepage</button><br><br>
</form>
</center>
</body>
</html>
