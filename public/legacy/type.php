<!DOCTYPE html>
<html>
<head>
<link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
<title>Covid Information System</title>
<style>
    img {
      width: 50%;
    }

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
<h1>Vaccine Information</h1>
<?php
	include "../../includes/connectdb.php";
?>
<?php
$query = "SELECT * FROM ShipTo";
$result = $connection->query($query);
?>

<form action="" method="POST">
    <select name="Lots">
    <option>Choose Lots Numbers</option>
      <?php while($row = $result->fetch()):;?>
      <option><?php echo $row[0];?></option>
      <?php endwhile;?>
    </select>
    <br>
    <input type="submit" name="submit" value="Submit">
</form>

<br>
<form action="../covid.php" method="POST">
  <label></label>
  <button type="next" name="next" value="next">Return to Homepage</button><br><br>
</form>

<?php 
if (isset($_POST['Lots'])) {
  $query = "SELECT * FROM ShipTo JOIN Vaccine ON Vaccine.Lot = ShipTo.Lots AND ShipTo.Lots = '{$_POST['Lots']}'";
  
  $result = $connection->query($query);
  $rows = $result->fetchAll();
  echo '<div><table border=1>';
  echo '<thead><tr><th>Vaccine Type</th><th>Clinic</th><th>Doses</th></tr></thead>';
  echo '<tbody>';
  foreach($rows as $val) {
    echo "<tr><td>{$val['CompanyName']}</td><td>{$val['Clinic']}</td><td>{$val['Doses']}</td></tr>";
  }
  echo '</tbody>';
}
?>


<?php
$connection = NULL;
?>
</center>
</body>
</html>
