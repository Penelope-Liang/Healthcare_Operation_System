<?php
include 'bootstrap.php';

function worker_tables($role) {
    if ($role === 'nurse') {
        return [
            'worker' => 'Nurse',
            'creds' => 'NurseCreds',
            'work' => 'NurseWork',
            'id' => 'NurseId',
            'credential' => 'NurseCredials',
        ];
    }
    if ($role === 'doctor') {
        return [
            'worker' => 'Doctor',
            'creds' => 'DoctorCreds',
            'work' => 'DoctorWork',
            'id' => 'DoctorId',
            'credential' => 'DoctorCredials',
        ];
    }
    throw new InvalidArgumentException('Role must be nurse or doctor.');
}

function validate_worker_payload($includeRole = true, $includeId = true) {
    $fields = [
        'FirstName' => 'First name',
        'LastName' => 'Last name',
        'Credential' => 'Credential',
        'VaxClinicName' => 'Clinic',
    ];
    if ($includeId) {
        $fields = ['Id' => 'Worker ID'] + $fields;
    }
    if ($includeRole) {
        $fields = ['Role' => 'Role'] + $fields;
    }
    require_form_fields($fields);
    if ($includeId) {
        require_pattern(form_value('Id'), '/^[A-Za-z0-9]{1,5}$/', 'Worker ID must be 1 to 5 letters or numbers.');
    }
    require_pattern(form_value('FirstName'), '/^[A-Za-z]{1,15}$/', 'First name must contain letters only.');
    require_pattern(form_value('LastName'), '/^[A-Za-z]{1,15}$/', 'Last name must contain letters only.');
}

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $role = request_identifier('Role');
    $id = request_identifier('Id');

    if ($role !== '') {
        try {
            $tables = worker_tables($role);
        } catch (InvalidArgumentException $e) {
            json_response(['error' => $e->getMessage()], 422);
        }

        $sql = "SELECT '{$role}' AS Role, {$tables['worker']}.Id, {$tables['worker']}.FirstName, {$tables['worker']}.LastName, {$tables['creds']}.{$tables['credential']} AS Credential, {$tables['work']}.VaxClinicName
                FROM {$tables['worker']}
                JOIN {$tables['creds']} ON {$tables['creds']}.{$tables['id']} = {$tables['worker']}.Id
                JOIN {$tables['work']} ON {$tables['work']}.{$tables['id']} = {$tables['worker']}.Id";
        if ($id !== '') {
            $stmt = $connection->prepare($sql . " WHERE {$tables['worker']}.Id = :id");
            $stmt->execute([':id' => $id]);
            $worker = $stmt->fetch(PDO::FETCH_ASSOC);
            if (!$worker) {
                json_response(['error' => 'Worker not found.'], 404);
            }
            json_response(['worker' => $worker]);
        }

        $workers = $connection->query($sql . " ORDER BY {$tables['work']}.VaxClinicName, {$tables['worker']}.LastName")->fetchAll(PDO::FETCH_ASSOC);
        json_response(['workers' => $workers]);
    }

    $nurses = $connection->query("SELECT 'nurse' AS Role, Nurse.Id, Nurse.FirstName, Nurse.LastName, NurseCreds.NurseCredials AS Credential, NurseWork.VaxClinicName
                                  FROM Nurse
                                  JOIN NurseCreds ON NurseCreds.NurseId = Nurse.Id
                                  JOIN NurseWork ON NurseWork.NurseId = Nurse.Id")->fetchAll(PDO::FETCH_ASSOC);
    $doctors = $connection->query("SELECT 'doctor' AS Role, Doctor.Id, Doctor.FirstName, Doctor.LastName, DoctorCreds.DoctorCredials AS Credential, DoctorWork.VaxClinicName
                                   FROM Doctor
                                   JOIN DoctorCreds ON DoctorCreds.DoctorId = Doctor.Id
                                   JOIN DoctorWork ON DoctorWork.DoctorId = Doctor.Id")->fetchAll(PDO::FETCH_ASSOC);
    json_response(['workers' => array_merge($nurses, $doctors)]);
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $_POST = json_request_body();

    try {
        validate_worker_payload();
        $role = form_value('Role');
        $tables = worker_tables($role);

        $connection->beginTransaction();
        $stmt = $connection->prepare("INSERT INTO {$tables['worker']} (Id, FirstName, LastName) VALUES (:id, :first, :last)");
        $stmt->execute([
            ':id' => form_value('Id'),
            ':first' => form_value('FirstName'),
            ':last' => form_value('LastName'),
        ]);
        $stmt = $connection->prepare("INSERT INTO {$tables['creds']} ({$tables['id']}, {$tables['credential']}) VALUES (:id, :credential)");
        $stmt->execute([
            ':id' => form_value('Id'),
            ':credential' => form_value('Credential'),
        ]);
        $stmt = $connection->prepare("INSERT INTO {$tables['work']} (VaxClinicName, {$tables['id']}) VALUES (:clinic, :id)");
        $stmt->execute([
            ':clinic' => form_value('VaxClinicName'),
            ':id' => form_value('Id'),
        ]);
        $connection->commit();

        json_response([
            'message' => 'Worker assignment created successfully.',
            'worker' => [
                'Role' => $role,
                'Id' => form_value('Id'),
                'FirstName' => form_value('FirstName'),
                'LastName' => form_value('LastName'),
                'Credential' => form_value('Credential'),
                'VaxClinicName' => form_value('VaxClinicName'),
            ],
        ], 201);
    } catch (InvalidArgumentException $e) {
        json_response(['error' => $e->getMessage()], 422);
    } catch (PDOException $e) {
        if ($connection->inTransaction()) {
            $connection->rollBack();
        }
        json_response(['error' => 'Unable to create worker assignment: ' . $e->getMessage()], 409);
    }
}

if ($_SERVER['REQUEST_METHOD'] === 'PUT') {
    $_POST = json_request_body();
    $role = request_identifier('Role');
    $id = request_identifier('Id');

    if ($role === '' || $id === '') {
        json_response(['error' => 'Role and Id query parameters are required.'], 422);
    }

    try {
        validate_worker_payload(false, false);
        $tables = worker_tables($role);

        $connection->beginTransaction();
        $stmt = $connection->prepare("UPDATE {$tables['worker']} SET FirstName = :first, LastName = :last WHERE Id = :id");
        $stmt->execute([
            ':id' => $id,
            ':first' => form_value('FirstName'),
            ':last' => form_value('LastName'),
        ]);
        if ($stmt->rowCount() === 0) {
            $connection->rollBack();
            json_response(['error' => 'Worker not found.'], 404);
        }
        $stmt = $connection->prepare("UPDATE {$tables['creds']} SET {$tables['credential']} = :credential WHERE {$tables['id']} = :id");
        $stmt->execute([
            ':id' => $id,
            ':credential' => form_value('Credential'),
        ]);
        $stmt = $connection->prepare("UPDATE {$tables['work']} SET VaxClinicName = :clinic WHERE {$tables['id']} = :id");
        $stmt->execute([
            ':id' => $id,
            ':clinic' => form_value('VaxClinicName'),
        ]);
        $connection->commit();

        json_response([
            'message' => 'Worker assignment updated successfully.',
            'worker' => [
                'Role' => $role,
                'Id' => $id,
                'FirstName' => form_value('FirstName'),
                'LastName' => form_value('LastName'),
                'Credential' => form_value('Credential'),
                'VaxClinicName' => form_value('VaxClinicName'),
            ],
        ]);
    } catch (InvalidArgumentException $e) {
        json_response(['error' => $e->getMessage()], 422);
    } catch (PDOException $e) {
        if ($connection->inTransaction()) {
            $connection->rollBack();
        }
        json_response(['error' => 'Unable to update worker assignment: ' . $e->getMessage()], 409);
    }
}

if ($_SERVER['REQUEST_METHOD'] === 'DELETE') {
    $role = request_identifier('Role');
    $id = request_identifier('Id');

    if ($role === '' || $id === '') {
        json_response(['error' => 'Role and Id query parameters are required.'], 422);
    }

    try {
        $tables = worker_tables($role);
        $connection->beginTransaction();
        $stmt = $connection->prepare("DELETE FROM {$tables['work']} WHERE {$tables['id']} = :id");
        $stmt->execute([':id' => $id]);
        $stmt = $connection->prepare("DELETE FROM {$tables['creds']} WHERE {$tables['id']} = :id");
        $stmt->execute([':id' => $id]);
        $stmt = $connection->prepare("DELETE FROM {$tables['worker']} WHERE Id = :id");
        $stmt->execute([':id' => $id]);

        if ($stmt->rowCount() === 0) {
            $connection->rollBack();
            json_response(['error' => 'Worker not found.'], 404);
        }

        $connection->commit();
        json_response(['message' => 'Worker assignment deleted successfully.']);
    } catch (InvalidArgumentException $e) {
        json_response(['error' => $e->getMessage()], 422);
    } catch (PDOException $e) {
        if ($connection->inTransaction()) {
            $connection->rollBack();
        }
        json_response(['error' => 'Unable to delete worker assignment: ' . $e->getMessage()], 409);
    }
}

json_response(['error' => 'Method not allowed.'], 405);
?>
