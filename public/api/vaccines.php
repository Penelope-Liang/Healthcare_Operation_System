<?php
include 'bootstrap.php';

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $lot = request_identifier('Lot');
    if ($lot !== '') {
        $stmt = $connection->prepare('SELECT Lot, CompanyName, Prodcution, Expiry, Doses FROM Vaccine WHERE Lot = :lot');
        $stmt->execute([':lot' => $lot]);
        $vaccine = $stmt->fetch(PDO::FETCH_ASSOC);
        if (!$vaccine) {
            json_response(['error' => 'Vaccine lot not found.'], 404);
        }
        json_response(['vaccine' => $vaccine]);
    }

    $vaccines = $connection->query('SELECT Lot, CompanyName, Prodcution, Expiry, Doses FROM Vaccine ORDER BY CompanyName, Lot')->fetchAll(PDO::FETCH_ASSOC);
    json_response(['vaccines' => $vaccines]);
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $_POST = json_request_body();

    try {
        require_form_fields([
            'Lot' => 'Lot',
            'CompanyName' => 'Manufacturer',
            'Prodcution' => 'Production date',
            'Expiry' => 'Expiry date',
            'Doses' => 'Doses',
        ]);
        require_date_value(form_value('Prodcution'), 'Production date');
        require_date_value(form_value('Expiry'), 'Expiry date');
        require_min_integer(form_value('Doses'), 0, 'Doses');

        $stmt = $connection->prepare('INSERT INTO Vaccine (Lot, CompanyName, Prodcution, Expiry, Doses) VALUES (:lot, :company, :production, :expiry, :doses)');
        $stmt->execute([
            ':lot' => form_value('Lot'),
            ':company' => form_value('CompanyName'),
            ':production' => form_value('Prodcution'),
            ':expiry' => form_value('Expiry'),
            ':doses' => (int) form_value('Doses'),
        ]);

        json_response([
            'message' => 'Vaccine lot created successfully.',
            'vaccine' => [
                'Lot' => form_value('Lot'),
                'CompanyName' => form_value('CompanyName'),
                'Prodcution' => form_value('Prodcution'),
                'Expiry' => form_value('Expiry'),
                'Doses' => (int) form_value('Doses'),
            ],
        ], 201);
    } catch (InvalidArgumentException $e) {
        json_response(['error' => $e->getMessage()], 422);
    } catch (PDOException $e) {
        json_response(['error' => 'Unable to create vaccine lot: ' . $e->getMessage()], 409);
    }
}

if ($_SERVER['REQUEST_METHOD'] === 'PUT') {
    $_POST = json_request_body();
    $lot = request_identifier('Lot');

    if ($lot === '') {
        json_response(['error' => 'Lot query parameter is required.'], 422);
    }

    try {
        require_form_fields([
            'CompanyName' => 'Manufacturer',
            'Prodcution' => 'Production date',
            'Expiry' => 'Expiry date',
            'Doses' => 'Doses',
        ]);
        require_date_value(form_value('Prodcution'), 'Production date');
        require_date_value(form_value('Expiry'), 'Expiry date');
        require_min_integer(form_value('Doses'), 0, 'Doses');

        $stmt = $connection->prepare('UPDATE Vaccine SET CompanyName = :company, Prodcution = :production, Expiry = :expiry, Doses = :doses WHERE Lot = :lot');
        $stmt->execute([
            ':lot' => $lot,
            ':company' => form_value('CompanyName'),
            ':production' => form_value('Prodcution'),
            ':expiry' => form_value('Expiry'),
            ':doses' => (int) form_value('Doses'),
        ]);

        if ($stmt->rowCount() === 0) {
            json_response(['error' => 'Vaccine lot not found.'], 404);
        }

        json_response([
            'message' => 'Vaccine lot updated successfully.',
            'vaccine' => [
                'Lot' => $lot,
                'CompanyName' => form_value('CompanyName'),
                'Prodcution' => form_value('Prodcution'),
                'Expiry' => form_value('Expiry'),
                'Doses' => (int) form_value('Doses'),
            ],
        ]);
    } catch (InvalidArgumentException $e) {
        json_response(['error' => $e->getMessage()], 422);
    } catch (PDOException $e) {
        json_response(['error' => 'Unable to update vaccine lot: ' . $e->getMessage()], 409);
    }
}

if ($_SERVER['REQUEST_METHOD'] === 'DELETE') {
    $lot = request_identifier('Lot');
    if ($lot === '') {
        json_response(['error' => 'Lot query parameter is required.'], 422);
    }

    try {
        $stmt = $connection->prepare('DELETE FROM Vaccine WHERE Lot = :lot');
        $stmt->execute([':lot' => $lot]);

        if ($stmt->rowCount() === 0) {
            json_response(['error' => 'Vaccine lot not found.'], 404);
        }

        json_response(['message' => 'Vaccine lot deleted successfully.']);
    } catch (PDOException $e) {
        json_response(['error' => 'Unable to delete vaccine lot: ' . $e->getMessage()], 409);
    }
}

json_response(['error' => 'Method not allowed.'], 405);
?>
