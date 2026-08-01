<?php
$dbOptional = true;
include '../includes/connectdb.php';
include '../includes/layout.php';

$nurses = [];
$doctors = [];
if ($connection instanceof PDO) {
try {
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
        <h1>Nurse and doctor assignments by vaccination clinic.</h1>
        <p class="lead">Staffing data helps clinic coordinators review provider availability across patient registration and vaccine inventory workflows.</p>
      </div>
      <div class="header-actions">
        <a class="button" href="clinics.php">Clinics</a>
        <a class="button primary" href="reports.php">Reports</a>
      </div>
    </header>

    <?php if (isset($dbError)): ?>
      <div class="notice error">Database issue: <?php echo htmlspecialchars($dbError); ?></div>
    <?php endif; ?>

    <section class="grid two-column">
      <div class="panel">
        <div class="panel-header">
          <div>
            <h2>Nurse Assignments</h2>
            <p class="muted">Clinical role, credential, and assigned clinic.</p>
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
            <p class="muted">Provider assignments by clinic location.</p>
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
  </main>
</div>
</body>
</html>
<?php $connection = NULL; ?>
