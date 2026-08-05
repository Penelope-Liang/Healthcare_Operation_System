<?php
include 'bootstrap.php';

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $ohip = request_identifier('OHIP');
    if ($ohip !== '') {
        $stmt = $connection->prepare('SELECT OHIP, SpouseOHIP, FirstName, LastName, DOB FROM Patient WHERE OHIP = :ohip');
        $stmt->execute([':ohip' => $ohip]);
        $patient = $stmt->fetch(PDO::FETCH_ASSOC);
        if (!$patient) {
            json_response(['error' => 'Patient not found.'], 404);
        }
        json_response(['patient' => $patient]);
    }

    $patients = $connection->query("SELECT OHIP, SpouseOHIP, FirstName, LastName, DOB FROM Patient ORDER BY LastName, FirstName")->fetchAll(PDO::FETCH_ASSOC);
    json_response(['patients' => $patients]);
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $_POST = json_request_body();

    try {
        require_form_fields([
            'OHIP' => 'OHIP',
            'FirstName' => 'First name',
            'LastName' => 'Last name',
            'DOB' => 'Date of birth',
        ]);
        require_pattern(form_value('OHIP'), '/^[0-9]{4}-[0-9]{3}-[0-9]{3}$/', 'OHIP must use the format 0000-000-000.');
        if (form_value('SpouseOHIP') !== '') {
            require_pattern(form_value('SpouseOHIP'), '/^[0-9]{4}-[0-9]{3}-[0-9]{3}$/', 'Spouse OHIP must use the format 0000-000-000.');
        }
        require_pattern(form_value('FirstName'), '/^[A-Za-z]{1,30}$/', 'First name must contain letters only.');
        require_pattern(form_value('LastName'), '/^[A-Za-z]{1,30}$/', 'Last name must contain letters only.');
        require_date_value(form_value('DOB'), 'Date of birth');

        $stmt = $connection->prepare('INSERT INTO Patient (OHIP, SpouseOHIP, FirstName, LastName, DOB) VALUES (:ohip, :spouse, :first, :last, :dob)');
        $stmt->execute([
            ':ohip' => form_value('OHIP'),
            ':spouse' => form_value('SpouseOHIP') === '' ? null : form_value('SpouseOHIP'),
            ':first' => form_value('FirstName'),
            ':last' => form_value('LastName'),
            ':dob' => form_value('DOB'),
        ]);

        json_response([
            'message' => 'Patient created successfully.',
            'patient' => [
                'OHIP' => form_value('OHIP'),
                'FirstName' => form_value('FirstName'),
                'LastName' => form_value('LastName'),
                'DOB' => form_value('DOB'),
            ],
        ], 201);
    } catch (InvalidArgumentException $e) {
        json_response(['error' => $e->getMessage()], 422);
    } catch (PDOException $e) {
        json_response(['error' => 'Unable to create patient: ' . $e->getMessage()], 409);
    }
}

if ($_SERVER['REQUEST_METHOD'] === 'PUT') {
    $_POST = json_request_body();
    $ohip = request_identifier('OHIP');

    if ($ohip === '') {
        json_response(['error' => 'OHIP query parameter is required.'], 422);
    }

    try {
        require_form_fields([
            'FirstName' => 'First name',
            'LastName' => 'Last name',
            'DOB' => 'Date of birth',
        ]);
        require_pattern(form_value('FirstName'), '/^[A-Za-z]{1,30}$/', 'First name must contain letters only.');
        require_pattern(form_value('LastName'), '/^[A-Za-z]{1,30}$/', 'Last name must contain letters only.');
        if (form_value('SpouseOHIP') !== '') {
            require_pattern(form_value('SpouseOHIP'), '/^[0-9]{4}-[0-9]{3}-[0-9]{3}$/', 'Spouse OHIP must use the format 0000-000-000.');
        }
        require_date_value(form_value('DOB'), 'Date of birth');

        $stmt = $connection->prepare('UPDATE Patient SET SpouseOHIP = :spouse, FirstName = :first, LastName = :last, DOB = :dob WHERE OHIP = :ohip');
        $stmt->execute([
            ':ohip' => $ohip,
            ':spouse' => form_value('SpouseOHIP') === '' ? null : form_value('SpouseOHIP'),
            ':first' => form_value('FirstName'),
            ':last' => form_value('LastName'),
            ':dob' => form_value('DOB'),
        ]);

        if ($stmt->rowCount() === 0) {
            json_response(['error' => 'Patient not found.'], 404);
        }

        json_response([
            'message' => 'Patient updated successfully.',
            'patient' => [
                'OHIP' => $ohip,
                'FirstName' => form_value('FirstName'),
                'LastName' => form_value('LastName'),
                'DOB' => form_value('DOB'),
            ],
        ]);
    } catch (InvalidArgumentException $e) {
        json_response(['error' => $e->getMessage()], 422);
    } catch (PDOException $e) {
        json_response(['error' => 'Unable to update patient: ' . $e->getMessage()], 409);
    }
}

if ($_SERVER['REQUEST_METHOD'] === 'DELETE') {
    $ohip = request_identifier('OHIP');
    if ($ohip === '') {
        json_response(['error' => 'OHIP query parameter is required.'], 422);
    }

    try {
        $stmt = $connection->prepare('DELETE FROM Patient WHERE OHIP = :ohip');
        $stmt->execute([':ohip' => $ohip]);

        if ($stmt->rowCount() === 0) {
            json_response(['error' => 'Patient not found.'], 404);
        }

        json_response(['message' => 'Patient deleted successfully.']);
    } catch (PDOException $e) {
        json_response(['error' => 'Unable to delete patient: ' . $e->getMessage()], 409);
    }
}

json_response(['error' => 'Method not allowed.'], 405);
?>
