<?php
try {
    $dbHost = getenv('DB_HOST') ?: 'localhost';
    $dbName = getenv('DB_NAME') ?: 'covidDB';
    $dbUser = getenv('DB_USER') ?: 'root';
    $dbPassword = getenv('DB_PASSWORD');
    if ($dbPassword === false) {
        $dbPassword = '';
    }

    $connection = new PDO("mysql:host=$dbHost;dbname=$dbName", $dbUser, $dbPassword);
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
