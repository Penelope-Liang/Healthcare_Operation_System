<?php
$dbOptional = true;
include '../../includes/connectdb.php';
include 'respond.php';

json_response([
    'status' => 'ok',
    'database' => $connection instanceof PDO ? 'connected' : 'unavailable',
]);
?>
