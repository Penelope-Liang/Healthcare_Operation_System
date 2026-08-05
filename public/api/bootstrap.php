<?php
$dbOptional = true;
include '../../includes/connectdb.php';
include '../../includes/validation.php';
include 'respond.php';

if (!($connection instanceof PDO)) {
    json_response(['error' => 'Database is not connected.'], 503);
}
?>
