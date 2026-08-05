<?php
include 'bootstrap.php';

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $lot = request_identifier('Lots');
    $clinic = request_identifier('Clinic');
    if ($lot !== '' && $clinic !== '') {
        $stmt = $connection->prepare('SELECT Lots, Clinic FROM ShipTo WHERE Lots = :lot AND Clinic = :clinic');
        $stmt->execute([':lot' => $lot, ':clinic' => $clinic]);
        $shipment = $stmt->fetch(PDO::FETCH_ASSOC);
        if (!$shipment) {
            json_response(['error' => 'Shipment not found.'], 404);
        }
        json_response(['shipment' => $shipment]);
    }

    $shipments = $connection->query('SELECT Lots, Clinic FROM ShipTo ORDER BY Clinic, Lots')->fetchAll(PDO::FETCH_ASSOC);
    json_response(['shipments' => $shipments]);
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $_POST = json_request_body();

    try {
        require_form_fields([
            'Lots' => 'Lot',
            'Clinic' => 'Clinic',
        ]);

        $stmt = $connection->prepare('INSERT INTO ShipTo (Lots, Clinic) VALUES (:lot, :clinic)');
        $stmt->execute([
            ':lot' => form_value('Lots'),
            ':clinic' => form_value('Clinic'),
        ]);

        json_response([
            'message' => 'Shipment assigned successfully.',
            'shipment' => [
                'Lots' => form_value('Lots'),
                'Clinic' => form_value('Clinic'),
            ],
        ], 201);
    } catch (InvalidArgumentException $e) {
        json_response(['error' => $e->getMessage()], 422);
    } catch (PDOException $e) {
        json_response(['error' => 'Unable to assign shipment: ' . $e->getMessage()], 409);
    }
}

if ($_SERVER['REQUEST_METHOD'] === 'DELETE') {
    $lot = request_identifier('Lots');
    $clinic = request_identifier('Clinic');
    if ($lot === '' || $clinic === '') {
        json_response(['error' => 'Lots and Clinic query parameters are required.'], 422);
    }

    try {
        $stmt = $connection->prepare('DELETE FROM ShipTo WHERE Lots = :lot AND Clinic = :clinic');
        $stmt->execute([':lot' => $lot, ':clinic' => $clinic]);

        if ($stmt->rowCount() === 0) {
            json_response(['error' => 'Shipment not found.'], 404);
        }

        json_response(['message' => 'Shipment deleted successfully.']);
    } catch (PDOException $e) {
        json_response(['error' => 'Unable to delete shipment: ' . $e->getMessage()], 409);
    }
}

json_response(['error' => 'Method not allowed.'], 405);
?>
