<?php
include 'bootstrap.php';

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $name = request_identifier('Name');
    if ($name !== '') {
        $stmt = $connection->prepare('SELECT Name, Street, City, Prov, PC, date FROM VaxClinic WHERE Name = :name');
        $stmt->execute([':name' => $name]);
        $clinic = $stmt->fetch(PDO::FETCH_ASSOC);
        if (!$clinic) {
            json_response(['error' => 'Clinic not found.'], 404);
        }
        json_response(['clinic' => $clinic]);
    }

    $clinics = $connection->query('SELECT Name, Street, City, Prov, PC, date FROM VaxClinic ORDER BY Name')->fetchAll(PDO::FETCH_ASSOC);
    json_response(['clinics' => $clinics]);
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $_POST = json_request_body();

    try {
        require_form_fields([
            'Name' => 'Clinic name',
            'Street' => 'Street',
            'City' => 'City',
            'Prov' => 'Province',
            'PC' => 'Postal code',
            'date' => 'Operating date',
        ]);
        require_date_value(form_value('date'), 'Operating date');

        $stmt = $connection->prepare('INSERT INTO VaxClinic (Name, Street, City, Prov, PC, date) VALUES (:name, :street, :city, :prov, :pc, :date)');
        $stmt->execute([
            ':name' => form_value('Name'),
            ':street' => form_value('Street'),
            ':city' => form_value('City'),
            ':prov' => form_value('Prov'),
            ':pc' => form_value('PC'),
            ':date' => form_value('date'),
        ]);

        json_response([
            'message' => 'Clinic created successfully.',
            'clinic' => [
                'Name' => form_value('Name'),
                'Street' => form_value('Street'),
                'City' => form_value('City'),
                'Prov' => form_value('Prov'),
                'PC' => form_value('PC'),
                'date' => form_value('date'),
            ],
        ], 201);
    } catch (InvalidArgumentException $e) {
        json_response(['error' => $e->getMessage()], 422);
    } catch (PDOException $e) {
        json_response(['error' => 'Unable to create clinic: ' . $e->getMessage()], 409);
    }
}

if ($_SERVER['REQUEST_METHOD'] === 'PUT') {
    $_POST = json_request_body();
    $name = request_identifier('Name');

    if ($name === '') {
        json_response(['error' => 'Name query parameter is required.'], 422);
    }

    try {
        require_form_fields([
            'Street' => 'Street',
            'City' => 'City',
            'Prov' => 'Province',
            'PC' => 'Postal code',
            'date' => 'Operating date',
        ]);
        require_date_value(form_value('date'), 'Operating date');

        $stmt = $connection->prepare('UPDATE VaxClinic SET Street = :street, City = :city, Prov = :prov, PC = :pc, date = :date WHERE Name = :name');
        $stmt->execute([
            ':name' => $name,
            ':street' => form_value('Street'),
            ':city' => form_value('City'),
            ':prov' => form_value('Prov'),
            ':pc' => form_value('PC'),
            ':date' => form_value('date'),
        ]);

        if ($stmt->rowCount() === 0) {
            json_response(['error' => 'Clinic not found.'], 404);
        }

        json_response([
            'message' => 'Clinic updated successfully.',
            'clinic' => [
                'Name' => $name,
                'Street' => form_value('Street'),
                'City' => form_value('City'),
                'Prov' => form_value('Prov'),
                'PC' => form_value('PC'),
                'date' => form_value('date'),
            ],
        ]);
    } catch (InvalidArgumentException $e) {
        json_response(['error' => $e->getMessage()], 422);
    } catch (PDOException $e) {
        json_response(['error' => 'Unable to update clinic: ' . $e->getMessage()], 409);
    }
}

if ($_SERVER['REQUEST_METHOD'] === 'DELETE') {
    $name = request_identifier('Name');
    if ($name === '') {
        json_response(['error' => 'Name query parameter is required.'], 422);
    }

    try {
        $stmt = $connection->prepare('DELETE FROM VaxClinic WHERE Name = :name');
        $stmt->execute([':name' => $name]);

        if ($stmt->rowCount() === 0) {
            json_response(['error' => 'Clinic not found.'], 404);
        }

        json_response(['message' => 'Clinic deleted successfully.']);
    } catch (PDOException $e) {
        json_response(['error' => 'Unable to delete clinic: ' . $e->getMessage()], 409);
    }
}

json_response(['error' => 'Method not allowed.'], 405);
?>
