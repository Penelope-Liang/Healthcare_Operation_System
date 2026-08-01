<?php
try {
    $connection = new PDO('mysql:host=localhost; dbname=covidDB', "root", "");
} catch (PDOException $e) {
    if (!empty($dbOptional)) {
        $connection = NULL;
        $connectionError = $e->getMessage();
    } else {
        print "Error!: ". $e->getMessage(). "<br/>";
        die();
    }
}
?>
