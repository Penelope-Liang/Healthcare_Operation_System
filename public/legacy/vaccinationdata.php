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
      background-color: white;
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

<?php
include "../../includes/connectdb.php";
?>

<?php
$query = "SELECT * FROM Patient";
$query2 = "SELECT * FROM ShipTo";
$query3 = "SELECT * FROM Vaccine";
$result1 = $connection->query($query);
$result2 = $connection->query($query2);
$result3 = $connection->query($query3);
$result4 = $connection->query($query);

$options2 = "";
$options3 = "";
$options4 = "";

while ($row4 = $result4->fetch()){
    $options4 = $options4."<option>$row4[4]</option>";
}

?>

<body>  
<center>
<h1>Vaccination Data</h1>
<h2>OHIP
<form action="savedata.php" method="POST">
<div class="select">
  <select name="location" select id="standard-select">
    <?php while($row1 = $result1->fetch()):;?>
    <option value="<?php echo $row1[0];?>"><?php echo $row1[0];?></option>
    <?php endwhile;?>
</select>
</div>

</div>
<h2>Clinic Name
<form action="savedata.php" method="POST">
<div class="select">
  <select name="n" select id="standard-select">
    <?php while($row2 = $result2->fetch()):;?>
    <option value="<?php echo $row2[1];?>"><?php echo $row2[1];?></option>
    <?php endwhile;?>
</select>
</div>

</div>

<h3>Lots Number
<div class="select">
  <select name="number" select id="standard-select">
    <?php while($row3 = $result3->fetch()):;?>
    <option value="<?php echo $row3[0];?>"><?php echo $row3[0];?></option>
    <?php endwhile;?>
</select>
</div>

<h3>Date
<div class="select">

<input type="date" name="d" id="standard-select">
</div>
<h3>Time
<div class="select">

<input type="time" name="t" id="standard-select">
</div>

<br><br>

  <label></label>
  <button type="save" name="save" value="save">Save</button><br><br>
</form>
 
<?php
$connection = NULL;
?>
</center>
</body>
</html>
