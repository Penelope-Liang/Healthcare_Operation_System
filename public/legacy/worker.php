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
$query = "SELECT * FROM VaxClinic";
$result = $connection->query($query);
?>

<form action="" method="POST">
    <select name="VaxClinic">
    <option>Choose Vaccine Clinic</option>
      <?php while($row = $result->fetch()):;?>
      <option><?php echo $row[0];?></option>
      <?php endwhile;?>
    </select>
    <br>
    <input type="submit" name="submit" value="Submit">
</form>

<?php
if (isset($_POST['submit'])) {
    include "../../includes/connectdb.php";
    $VaxClinic = $_POST['VaxClinic'];
    $query = "SELECT Nurse.FirstName, Nurse.LastName, NurseWork.VaxClinicName,NurseCreds.NurseCredials FROM Nurse 
        JOIN NurseCreds ON NurseCreds.NurseId = Nurse.Id 
        JOIN NurseWork ON NurseWork.NurseId = Nurse.Id 
        where NurseWork.VaxClinicName = '{$VaxClinic}'" ;
    $result = $connection->query($query);
    $rows = $result->fetchAll();
    echo '<div><table border=1>';
    echo '<thead><tr><th>First Name</th><th>Last Name</th><th>NurseCredials</th><th>VaxClinicName</th></tr></thead>';
    echo '<tbody>';
    foreach($rows as $val) {
       echo "<tr><td>{$val['FirstName']}</td><td>{$val['LastName']}</td><td>{$val['NurseCredials']}</td><td>{$val['VaxClinicName']}</td></tr>";
    }
    echo '</tbody>';

    $VaxClinic = $_POST['VaxClinic'];
    $query = "SELECT Doctor.FirstName, Doctor.LastName, DoctorWork.VaxClinicName,DoctorCreds.DoctorCredials FROM Doctor 
       JOIN DoctorWork ON DoctorWork.DoctorId = Doctor.Id
       JOIN DoctorCreds ON DoctorCreds.DoctorId = Doctor.Id
       where DoctorWork.VaxClinicName = '{$VaxClinic}'" ;
    $result = $connection->query($query);
    $rows = $result->fetchAll();
    echo '<div><table border=1>';
    echo '<thead><tr><th>First Name</th><th>Last Name</th><th>DoctorCredials</th><th>VaxClinicName</th></tr></thead>';
    echo '<tbody>';
    foreach($rows as $val) {
       echo "<tr><td>{$val['FirstName']}</td><td>{$val['LastName']}</td><td>{$val['DoctorCredials']}</td><td>{$val['VaxClinicName']}</td></tr>";
    }
    echo '</tbody>';
}
?>

<br>
<form action="../covid.php" method="POST">
  <label></label>
  <button type="next" name="next" value="next">Return to Homepage</button><br><br>
</form>

<?php
$connection = NULL;
?>
</center>
</body>
</html>
