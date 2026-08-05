<?php
include 'bootstrap.php';

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $ohip = request_identifier('OHIP');
    if ($ohip !== '') {
        $stmt = $connection->prepare('SELECT OHIP, ClinicName, Lots, Date, Time FROM Vaccination WHERE OHIP = :ohip');
        $stmt->execute([':ohip' => $ohip]);
        $vaccination = $stmt->fetch(PDO::FETCH_ASSOC);
        if (!$vaccination) {
            json_response(['error' => 'Vaccination record not found.'], 404);
        }
        json_response(['vaccination' => $vaccination]);
    }

    $vaccinations = $connection->query('SELECT OHIP, ClinicName, Lots, Date, Time FROM Vaccination ORDER BY Date, Time')->fetchAll(PDO::FETCH_ASSOC);
    json_response(['vaccinations' => $vaccinations]);
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $_POST = json_request_body();

    try {
        require_form_fields([
            'OHIP' => 'Patient',
            'ClinicName' => 'Clinic',
            'Lots' => 'Vaccine lot',
            'Date' => 'Date',
            'Time' => 'Time',
        ]);
        require_date_value(form_value('Date'), 'Date');
        require_time_value(form_value('Time'), 'Time');

        $stmt = $connection->prepare('INSERT INTO Vaccination (OHIP, ClinicName, Lots, Date, Time) VALUES (:ohip, :clinic, :lot, :date, :time)');
        $stmt->execute([
            ':ohip' => form_value('OHIP'),
            ':clinic' => form_value('ClinicName'),
            ':lot' => form_value('Lots'),
            ':date' => form_value('Date'),
            ':time' => form_value('Time'),
        ]);

        json_response([
            'message' => 'Vaccination record created successfully.',
            'vaccination' => [
                'OHIP' => form_value('OHIP'),
                'ClinicName' => form_value('ClinicName'),
                'Lots' => form_value('Lots'),
                'Date' => form_value('Date'),
                'Time' => form_value('Time'),
            ],
        ], 201);
    } catch (InvalidArgumentException $e) {
        json_response(['error' => $e->getMessage()], 422);
    } catch (PDOException $e) {
        json_response(['error' => 'Unable to create vaccination record: ' . $e->getMessage()], 409);
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
            'ClinicName' => 'Clinic',
            'Lots' => 'Vaccine lot',
            'Date' => 'Date',
            'Time' => 'Time',
        ]);
        require_date_value(form_value('Date'), 'Date');
        require_time_value(form_value('Time'), 'Time');

        $stmt = $connection->prepare('UPDATE Vaccination SET ClinicName = :clinic, Lots = :lot, Date = :date, Time = :time WHERE OHIP = :ohip');
        $stmt->execute([
            ':ohip' => $ohip,
            ':clinic' => form_value('ClinicName'),
            ':lot' => form_value('Lots'),
            ':date' => form_value('Date'),
            ':time' => form_value('Time'),
        ]);

        if ($stmt->rowCount() === 0) {
            json_response(['error' => 'Vaccination record not found.'], 404);
        }

        json_response([
            'message' => 'Vaccination record updated successfully.',
            'vaccination' => [
                'OHIP' => $ohip,
                'ClinicName' => form_value('ClinicName'),
                'Lots' => form_value('Lots'),
                'Date' => form_value('Date'),
                'Time' => form_value('Time'),
            ],
        ]);
    } catch (InvalidArgumentException $e) {
        json_response(['error' => $e->getMessage()], 422);
    } catch (PDOException $e) {
        json_response(['error' => 'Unable to update vaccination record: ' . $e->getMessage()], 409);
    }
}

if ($_SERVER['REQUEST_METHOD'] === 'DELETE') {
    $ohip = request_identifier('OHIP');
    if ($ohip === '') {
        json_response(['error' => 'OHIP query parameter is required.'], 422);
    }

    try {
        $stmt = $connection->prepare('DELETE FROM Vaccination WHERE OHIP = :ohip');
        $stmt->execute([':ohip' => $ohip]);

        if ($stmt->rowCount() === 0) {
            json_response(['error' => 'Vaccination record not found.'], 404);
        }

        json_response(['message' => 'Vaccination record deleted successfully.']);
    } catch (PDOException $e) {
        json_response(['error' => 'Unable to delete vaccination record: ' . $e->getMessage()], 409);
    }
}

json_response(['error' => 'Method not allowed.'], 405);
?>
