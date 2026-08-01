<?php
$dbOptional = true;
include '../includes/connectdb.php';
include '../includes/layout.php';

$report = [
    'patients' => 0,
    'vaccinated' => 0,
    'clinics' => 0,
    'vaccineLots' => 0,
    'nurses' => 0,
    'doctors' => 0,
];
$upcoming = [];
$patients = [];
$clinics = [];
$vaccines = [];
$notice = null;
$noticeType = 'success';

if ($connection instanceof PDO) {
    if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['create_vaccination'])) {
        try {
            $stmt = $connection->prepare('INSERT INTO Vaccination (OHIP, ClinicName, Lots, Date, Time) VALUES (:ohip, :clinic, :lot, :date, :time)');
            $stmt->execute([
                ':ohip' => trim($_POST['OHIP'] ?? ''),
                ':clinic' => trim($_POST['ClinicName'] ?? ''),
                ':lot' => trim($_POST['Lots'] ?? ''),
                ':date' => trim($_POST['Date'] ?? ''),
                ':time' => trim($_POST['Time'] ?? ''),
            ]);
            $notice = 'Vaccination record created successfully.';
        } catch (PDOException $e) {
            $notice = 'Unable to create vaccination record: ' . $e->getMessage();
            $noticeType = 'error';
        }
    }

    try {
        $report['patients'] = (int) $connection->query("SELECT COUNT(*) FROM Patient")->fetchColumn();
        $report['vaccinated'] = (int) $connection->query("SELECT COUNT(*) FROM Vaccination")->fetchColumn();
        $report['clinics'] = (int) $connection->query("SELECT COUNT(*) FROM VaxClinic")->fetchColumn();
        $report['vaccineLots'] = (int) $connection->query("SELECT COUNT(*) FROM Vaccine")->fetchColumn();
        $report['nurses'] = (int) $connection->query("SELECT COUNT(*) FROM Nurse")->fetchColumn();
        $report['doctors'] = (int) $connection->query("SELECT COUNT(*) FROM Doctor")->fetchColumn();
        $upcoming = $connection->query("SELECT Patient.FirstName, Patient.LastName, Vaccination.Date, Vaccination.Time,
                                        Vaccination.ClinicName, Vaccine.CompanyName
                                        FROM Vaccination
                                        JOIN Patient ON Patient.OHIP = Vaccination.OHIP
                                        JOIN Vaccine ON Vaccine.Lot = Vaccination.Lots
                                        ORDER BY Vaccination.Date, Vaccination.Time")->fetchAll(PDO::FETCH_ASSOC);
        $patients = $connection->query("SELECT OHIP, FirstName, LastName FROM Patient ORDER BY LastName, FirstName")->fetchAll(PDO::FETCH_ASSOC);
        $clinics = $connection->query("SELECT Name FROM VaxClinic ORDER BY Name")->fetchAll(PDO::FETCH_ASSOC);
        $vaccines = $connection->query("SELECT Lot, CompanyName FROM Vaccine ORDER BY CompanyName, Lot")->fetchAll(PDO::FETCH_ASSOC);
    } catch (PDOException $e) {
        $notice = 'Unable to load report data: ' . $e->getMessage();
        $noticeType = 'error';
    }
} elseif (isset($connectionError)) {
    $notice = 'Database is not connected: ' . $connectionError;
    $noticeType = 'error';
}
?>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Reports | Healthcare Operations Dashboard</title>
  <link rel="stylesheet" href="assets/styles.css">
  <script src="assets/app.js" defer></script>
</head>
<body>
<div class="app-shell">
  <?php render_sidebar(current_page_name()); ?>
  <main class="main">
    <header class="page-header">
      <div>
        <p class="eyebrow">Reports</p>
        <h1>Activity Summary</h1>
      </div>
      <div class="header-actions">
        <a class="button" href="patients.php">Patients</a>
        <a class="button primary" href="clinics.php">Clinics</a>
      </div>
    </header>

    <?php if ($notice): ?>
      <div class="notice <?php echo $noticeType === 'error' ? 'error' : ''; ?>"><?php echo htmlspecialchars($notice); ?></div>
    <?php endif; ?>

    <section class="grid stats-grid">
      <article class="card">
        <div class="stat-value"><?php echo $report['patients']; ?></div>
        <div class="stat-label">Registered Patients</div>
      </article>
      <article class="card">
        <div class="stat-value"><?php echo $report['vaccinated']; ?></div>
        <div class="stat-label">Vaccination Records</div>
      </article>
      <article class="card">
        <div class="stat-value"><?php echo $report['clinics']; ?></div>
        <div class="stat-label">Clinic Sites</div>
      </article>
      <article class="card">
        <div class="stat-value"><?php echo $report['vaccineLots']; ?></div>
        <div class="stat-label">Vaccine Lots</div>
      </article>
    </section>

    <section class="grid two-column">
      <div class="panel">
        <div class="panel-header">
          <div>
            <h2>Vaccination Schedule</h2>
          </div>
          <input type="search" placeholder="Filter schedule" data-table-filter="#schedule-table" aria-label="Filter schedule">
        </div>
        <div class="panel-body">
          <table class="data-table" id="schedule-table">
            <thead>
              <tr>
                <th>Patient</th>
                <th>Date</th>
                <th>Time</th>
                <th>Clinic</th>
                <th>Vaccine</th>
              </tr>
            </thead>
            <tbody>
              <?php foreach ($upcoming as $row): ?>
                <tr>
                  <td><?php echo htmlspecialchars($row['FirstName'] . ' ' . $row['LastName']); ?></td>
                  <td><?php echo htmlspecialchars($row['Date']); ?></td>
                  <td><?php echo htmlspecialchars($row['Time']); ?></td>
                  <td><?php echo htmlspecialchars($row['ClinicName']); ?></td>
                  <td><?php echo htmlspecialchars($row['CompanyName']); ?></td>
                </tr>
              <?php endforeach; ?>
            </tbody>
          </table>
        </div>
      </div>

      <aside class="panel">
        <div class="panel-header">
          <div>
            <h2>Staffing Summary</h2>
          </div>
        </div>
        <div class="panel-body coverage-list">
          <div class="coverage-item">
            <div>
              <h3>Nurses</h3>
            </div>
            <span class="tag green"><?php echo $report['nurses']; ?></span>
          </div>
          <div class="coverage-item">
            <div>
              <h3>Doctors</h3>
            </div>
            <span class="tag green"><?php echo $report['doctors']; ?></span>
          </div>
        </div>
      </aside>
    </section>

    <section class="panel" style="margin-top: 18px;">
      <div class="panel-header">
        <div>
          <h2>Add Vaccination Record</h2>
        </div>
      </div>
      <div class="panel-body">
        <form class="form-grid" method="POST" action="reports.php">
          <input type="hidden" name="create_vaccination" value="1">
          <div class="field">
            <label for="OHIP">Patient</label>
            <select id="OHIP" name="OHIP" required>
              <option value="">Select patient</option>
              <?php foreach ($patients as $patient): ?>
                <option value="<?php echo htmlspecialchars($patient['OHIP']); ?>"><?php echo htmlspecialchars($patient['LastName'] . ', ' . $patient['FirstName'] . ' - ' . $patient['OHIP']); ?></option>
              <?php endforeach; ?>
            </select>
          </div>
          <div class="field">
            <label for="ClinicName">Clinic</label>
            <select id="ClinicName" name="ClinicName" required>
              <option value="">Select clinic</option>
              <?php foreach ($clinics as $clinic): ?>
                <option value="<?php echo htmlspecialchars($clinic['Name']); ?>"><?php echo htmlspecialchars($clinic['Name']); ?></option>
              <?php endforeach; ?>
            </select>
          </div>
          <div class="field">
            <label for="Lots">Vaccine Lot</label>
            <select id="Lots" name="Lots" required>
              <option value="">Select lot</option>
              <?php foreach ($vaccines as $vaccine): ?>
                <option value="<?php echo htmlspecialchars($vaccine['Lot']); ?>"><?php echo htmlspecialchars($vaccine['Lot'] . ' - ' . $vaccine['CompanyName']); ?></option>
              <?php endforeach; ?>
            </select>
          </div>
          <div class="field">
            <label for="Date">Date</label>
            <input id="Date" type="date" name="Date" required>
          </div>
          <div class="field full">
            <label for="Time">Time</label>
            <input id="Time" type="time" name="Time" required>
          </div>
          <button class="button primary full" type="submit">Create Vaccination Record</button>
        </form>
      </div>
    </section>
  </main>
</div>
</body>
</html>
<?php $connection = NULL; ?>
