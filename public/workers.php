<?php
$dbOptional = true;
include '../includes/connectdb.php';
include '../includes/layout.php';

$nurses = [];
$doctors = [];
$clinics = [];
$notice = null;
$noticeType = 'success';
if ($connection instanceof PDO) {
    if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['create_worker'])) {
        $role = $_POST['Role'] ?? '';
        try {
            $connection->beginTransaction();
            if ($role === 'nurse') {
                $stmt = $connection->prepare('INSERT INTO Nurse (Id, FirstName, LastName) VALUES (:id, :first, :last)');
                $stmt->execute([
                    ':id' => trim($_POST['Id'] ?? ''),
                    ':first' => trim($_POST['FirstName'] ?? ''),
                    ':last' => trim($_POST['LastName'] ?? ''),
                ]);
                $stmt = $connection->prepare('INSERT INTO NurseCreds (NurseId, NurseCredials) VALUES (:id, :credential)');
                $stmt->execute([
                    ':id' => trim($_POST['Id'] ?? ''),
                    ':credential' => trim($_POST['Credential'] ?? ''),
                ]);
                $stmt = $connection->prepare('INSERT INTO NurseWork (VaxClinicName, NurseId) VALUES (:clinic, :id)');
                $stmt->execute([
                    ':clinic' => trim($_POST['VaxClinicName'] ?? ''),
                    ':id' => trim($_POST['Id'] ?? ''),
                ]);
            } elseif ($role === 'doctor') {
                $stmt = $connection->prepare('INSERT INTO Doctor (Id, FirstName, LastName) VALUES (:id, :first, :last)');
                $stmt->execute([
                    ':id' => trim($_POST['Id'] ?? ''),
                    ':first' => trim($_POST['FirstName'] ?? ''),
                    ':last' => trim($_POST['LastName'] ?? ''),
                ]);
                $stmt = $connection->prepare('INSERT INTO DoctorCreds (DoctorId, DoctorCredials) VALUES (:id, :credential)');
                $stmt->execute([
                    ':id' => trim($_POST['Id'] ?? ''),
                    ':credential' => trim($_POST['Credential'] ?? ''),
                ]);
                $stmt = $connection->prepare('INSERT INTO DoctorWork (VaxClinicName, DoctorId) VALUES (:clinic, :id)');
                $stmt->execute([
                    ':clinic' => trim($_POST['VaxClinicName'] ?? ''),
                    ':id' => trim($_POST['Id'] ?? ''),
                ]);
            } else {
                throw new PDOException('Worker role is required.');
            }
            $connection->commit();
            $notice = 'Worker assignment created successfully.';
        } catch (PDOException $e) {
            if ($connection->inTransaction()) {
                $connection->rollBack();
            }
            $notice = 'Unable to create worker assignment: ' . $e->getMessage();
            $noticeType = 'error';
        }
    }

    try {
        $clinics = $connection->query("SELECT Name FROM VaxClinic ORDER BY Name")->fetchAll(PDO::FETCH_ASSOC);
        $nurses = $connection->query("SELECT Nurse.Id, Nurse.FirstName, Nurse.LastName, NurseCreds.NurseCredials, NurseWork.VaxClinicName
                                      FROM Nurse
                                      JOIN NurseCreds ON NurseCreds.NurseId = Nurse.Id
                                      JOIN NurseWork ON NurseWork.NurseId = Nurse.Id
                                      ORDER BY NurseWork.VaxClinicName")->fetchAll(PDO::FETCH_ASSOC);
        $doctors = $connection->query("SELECT Doctor.Id, Doctor.FirstName, Doctor.LastName, DoctorCreds.DoctorCredials, DoctorWork.VaxClinicName
                                       FROM Doctor
                                       JOIN DoctorCreds ON DoctorCreds.DoctorId = Doctor.Id
                                       JOIN DoctorWork ON DoctorWork.DoctorId = Doctor.Id
                                       ORDER BY DoctorWork.VaxClinicName")->fetchAll(PDO::FETCH_ASSOC);
    } catch (PDOException $e) {
        $dbError = $e->getMessage();
    }
} elseif (isset($connectionError)) {
    $dbError = $connectionError;
}
?>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Workers | Healthcare Operations Dashboard</title>
  <link rel="stylesheet" href="assets/styles.css">
  <script src="assets/app.js" defer></script>
</head>
<body>
<div class="app-shell">
  <?php render_sidebar(current_page_name()); ?>
  <main class="main">
    <header class="page-header">
      <div>
        <p class="eyebrow">Clinical Staffing</p>
        <h1>Workers</h1>
      </div>
      <div class="header-actions">
        <a class="button" href="clinics.php">Clinics</a>
        <a class="button primary" href="reports.php">Reports</a>
      </div>
    </header>

    <?php if (isset($dbError)): ?>
      <div class="notice error">Database issue: <?php echo htmlspecialchars($dbError); ?></div>
    <?php endif; ?>
    <?php if ($notice): ?>
      <div class="notice <?php echo $noticeType === 'error' ? 'error' : ''; ?>"><?php echo htmlspecialchars($notice); ?></div>
    <?php endif; ?>

    <section class="grid two-column">
      <div class="panel">
        <div class="panel-header">
          <div>
            <h2>Nurse Assignments</h2>
          </div>
          <input type="search" placeholder="Filter nurses" data-table-filter="#nurses-table" aria-label="Filter nurses">
        </div>
        <div class="panel-body">
          <table class="data-table" id="nurses-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Worker</th>
                <th>Credential</th>
                <th>Clinic</th>
              </tr>
            </thead>
            <tbody>
              <?php foreach ($nurses as $nurse): ?>
                <tr>
                  <td><?php echo htmlspecialchars($nurse['Id']); ?></td>
                  <td><?php echo htmlspecialchars($nurse['FirstName'] . ' ' . $nurse['LastName']); ?></td>
                  <td><?php echo htmlspecialchars($nurse['NurseCredials']); ?></td>
                  <td><?php echo htmlspecialchars($nurse['VaxClinicName']); ?></td>
                </tr>
              <?php endforeach; ?>
            </tbody>
          </table>
        </div>
      </div>

      <div class="panel">
        <div class="panel-header">
          <div>
            <h2>Doctor Assignments</h2>
          </div>
          <input type="search" placeholder="Filter doctors" data-table-filter="#doctors-table" aria-label="Filter doctors">
        </div>
        <div class="panel-body">
          <table class="data-table" id="doctors-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Worker</th>
                <th>Credential</th>
                <th>Clinic</th>
              </tr>
            </thead>
            <tbody>
              <?php foreach ($doctors as $doctor): ?>
                <tr>
                  <td><?php echo htmlspecialchars($doctor['Id']); ?></td>
                  <td><?php echo htmlspecialchars($doctor['FirstName'] . ' ' . $doctor['LastName']); ?></td>
                  <td><?php echo htmlspecialchars($doctor['DoctorCredials']); ?></td>
                  <td><?php echo htmlspecialchars($doctor['VaxClinicName']); ?></td>
                </tr>
              <?php endforeach; ?>
            </tbody>
          </table>
        </div>
      </div>
    </section>

    <section class="panel" style="margin-top: 18px;">
      <div class="panel-header">
        <div>
          <h2>Add Worker Assignment</h2>
        </div>
      </div>
      <div class="panel-body">
        <form class="form-grid" method="POST" action="workers.php">
          <input type="hidden" name="create_worker" value="1">
          <div class="field">
            <label for="Role">Role</label>
            <select id="Role" name="Role" required>
              <option value="">Select role</option>
              <option value="nurse">Nurse</option>
              <option value="doctor">Doctor</option>
            </select>
          </div>
          <div class="field">
            <label for="Id">Worker ID</label>
            <input id="Id" name="Id" maxlength="5" required>
          </div>
          <div class="field">
            <label for="FirstName">First Name</label>
            <input id="FirstName" name="FirstName" maxlength="15" required>
          </div>
          <div class="field">
            <label for="LastName">Last Name</label>
            <input id="LastName" name="LastName" maxlength="15" required>
          </div>
          <div class="field">
            <label for="Credential">Credential</label>
            <input id="Credential" name="Credential" maxlength="20" placeholder="RN, MD, DO" required>
          </div>
          <div class="field">
            <label for="VaxClinicName">Clinic</label>
            <select id="VaxClinicName" name="VaxClinicName" required>
              <option value="">Select clinic</option>
              <?php foreach ($clinics as $clinic): ?>
                <option value="<?php echo htmlspecialchars($clinic['Name']); ?>"><?php echo htmlspecialchars($clinic['Name']); ?></option>
              <?php endforeach; ?>
            </select>
          </div>
          <button class="button primary full" type="submit">Create Worker Assignment</button>
        </form>
      </div>
    </section>
  </main>
</div>
</body>
</html>
<?php $connection = NULL; ?>
