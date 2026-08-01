<?php
$dbOptional = true;
include '../includes/connectdb.php';
include '../includes/layout.php';

$notice = null;
$noticeType = 'success';

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['create_patient'])) {
    $ohip = trim($_POST['OHIP'] ?? '');
    $spouseOhip = trim($_POST['SpouseOHIP'] ?? '');
    $firstName = trim($_POST['FirstName'] ?? '');
    $lastName = trim($_POST['LastName'] ?? '');
    $dob = trim($_POST['DOB'] ?? '');

    if (!preg_match('/^[0-9]{4}-[0-9]{3}-[0-9]{3}$/', $ohip)) {
        $notice = 'OHIP must use the format 0000-000-000.';
        $noticeType = 'error';
    } elseif ($spouseOhip !== '' && !preg_match('/^[0-9]{4}-[0-9]{3}-[0-9]{3}$/', $spouseOhip)) {
        $notice = 'Spouse OHIP must use the format 0000-000-000 when provided.';
        $noticeType = 'error';
    } elseif (!preg_match('/^[A-Za-z]{1,30}$/', $firstName) || !preg_match('/^[A-Za-z]{1,30}$/', $lastName)) {
        $notice = 'First name and last name must contain letters only.';
        $noticeType = 'error';
    } elseif ($dob === '') {
        $notice = 'Date of birth is required.';
        $noticeType = 'error';
    } elseif (!($connection instanceof PDO)) {
        $notice = 'Database is not connected. Start MySQL and import database/covidDB.sql before creating records.';
        $noticeType = 'error';
    } else {
        try {
            $stmt = $connection->prepare('INSERT INTO Patient (OHIP, SpouseOHIP, FirstName, LastName, DOB) VALUES (:ohip, :spouse, :first, :last, :dob)');
            $stmt->execute([
                ':ohip' => $ohip,
                ':spouse' => $spouseOhip === '' ? null : $spouseOhip,
                ':first' => $firstName,
                ':last' => $lastName,
                ':dob' => $dob,
            ]);
            $notice = 'Patient record created successfully.';
        } catch (PDOException $e) {
            $notice = 'Unable to create patient record: ' . $e->getMessage();
            $noticeType = 'error';
        }
    }
}

$patients = [];
if ($connection instanceof PDO) {
try {
    $query = "SELECT Patient.OHIP, Patient.SpouseOHIP, Patient.FirstName, Patient.LastName, Patient.DOB,
              Vaccination.Date, Vaccination.Time, Vaccine.CompanyName, Vaccination.ClinicName
              FROM Patient
              LEFT JOIN Vaccination ON Patient.OHIP = Vaccination.OHIP
              LEFT JOIN Vaccine ON Vaccine.Lot = Vaccination.Lots
              ORDER BY Patient.LastName, Patient.FirstName";
    $patients = $connection->query($query)->fetchAll(PDO::FETCH_ASSOC);
} catch (PDOException $e) {
    $notice = 'Unable to load patient records: ' . $e->getMessage();
    $noticeType = 'error';
}
} elseif (isset($connectionError) && !$notice) {
    $notice = 'Database is not connected: ' . $connectionError;
    $noticeType = 'error';
}
?>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Patients | Healthcare Operations Dashboard</title>
  <link rel="stylesheet" href="assets/styles.css">
  <script src="assets/app.js" defer></script>
</head>
<body>
<div class="app-shell">
  <?php render_sidebar(current_page_name()); ?>
  <main class="main">
    <header class="page-header">
      <div>
        <p class="eyebrow">Patient Workflow</p>
        <h1>Patient records with validation and searchable vaccination history.</h1>
        <p class="lead">Use this workspace to manage patient identity details, vaccination status, clinic assignment, and appointment history.</p>
      </div>
      <div class="header-actions">
        <a class="button" href="reports.php">Reports</a>
        <a class="button primary" href="#add-patient">Add Patient</a>
      </div>
    </header>

    <?php if ($notice): ?>
      <div class="notice <?php echo $noticeType === 'error' ? 'error' : ''; ?>"><?php echo htmlspecialchars($notice); ?></div>
    <?php endif; ?>

    <section class="grid two-column" style="margin-top: 18px;">
      <div class="panel">
        <div class="panel-header">
          <div>
            <h2>Patient Records</h2>
            <p class="muted">Search by name, OHIP, vaccine manufacturer, or clinic.</p>
          </div>
          <input type="search" placeholder="Filter records" data-table-filter="#patients-table" aria-label="Filter patient records">
        </div>
        <div class="panel-body">
          <table class="data-table" id="patients-table">
            <thead>
              <tr>
                <th>OHIP</th>
                <th>Patient</th>
                <th>DOB</th>
                <th>Vaccine</th>
                <th>Clinic</th>
                <th>Appointment</th>
              </tr>
            </thead>
            <tbody>
              <?php foreach ($patients as $patient): ?>
                <tr>
                  <td><?php echo htmlspecialchars($patient['OHIP']); ?></td>
                  <td><?php echo htmlspecialchars($patient['FirstName'] . ' ' . $patient['LastName']); ?></td>
                  <td><?php echo htmlspecialchars($patient['DOB']); ?></td>
                  <td><?php echo htmlspecialchars($patient['CompanyName'] ?? 'Pending'); ?></td>
                  <td><?php echo htmlspecialchars($patient['ClinicName'] ?? 'Not assigned'); ?></td>
                  <td><?php echo htmlspecialchars(trim(($patient['Date'] ?? '') . ' ' . ($patient['Time'] ?? '')) ?: 'Not scheduled'); ?></td>
                </tr>
              <?php endforeach; ?>
            </tbody>
          </table>
        </div>
      </div>

      <aside class="panel" id="add-patient">
        <div class="panel-header">
          <div>
            <h2>Add Patient</h2>
            <p class="muted">Required field checks help keep patient records consistent.</p>
          </div>
        </div>
        <div class="panel-body">
          <form class="form-grid" method="POST" action="patients.php">
            <input type="hidden" name="create_patient" value="1">
            <div class="field full">
              <label for="OHIP">OHIP</label>
              <input id="OHIP" name="OHIP" placeholder="0000-000-000" pattern="[0-9]{4}-[0-9]{3}-[0-9]{3}" required>
            </div>
            <div class="field full">
              <label for="SpouseOHIP">Spouse OHIP</label>
              <input id="SpouseOHIP" name="SpouseOHIP" placeholder="Optional">
            </div>
            <div class="field">
              <label for="FirstName">First Name</label>
              <input id="FirstName" name="FirstName" pattern="[A-Za-z]{1,30}" required>
            </div>
            <div class="field">
              <label for="LastName">Last Name</label>
              <input id="LastName" name="LastName" pattern="[A-Za-z]{1,30}" required>
            </div>
            <div class="field full">
              <label for="DOB">Date of Birth</label>
              <input id="DOB" type="date" name="DOB" required>
            </div>
            <button class="button primary full" type="submit">Create Patient Record</button>
          </form>
          <div class="tag-row" style="margin-top: 16px;">
            <span class="tag green">Required Fields</span>
            <span class="tag">Format Checks</span>
            <span class="tag amber">Record Quality</span>
          </div>
        </div>
      </aside>
    </section>
  </main>
</div>
</body>
</html>
<?php $connection = NULL; ?>
