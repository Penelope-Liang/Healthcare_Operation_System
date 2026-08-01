<?php
$dbOptional = true;
include 'connectdb.php';
include 'layout.php';

$report = [
    'patients' => 0,
    'vaccinated' => 0,
    'clinics' => 0,
    'vaccineLots' => 0,
    'nurses' => 0,
    'doctors' => 0,
];
$upcoming = [];
$notice = null;

if ($connection instanceof PDO) {
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
    } catch (PDOException $e) {
        $notice = 'Unable to load report data: ' . $e->getMessage();
    }
} elseif (isset($connectionError)) {
    $notice = 'Database is not connected: ' . $connectionError;
}
?>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Reports | Healthcare Operations Dashboard</title>
  <link rel="stylesheet" href="styles.css">
  <script src="app.js" defer></script>
</head>
<body>
<div class="app-shell">
  <?php render_sidebar(current_page_name()); ?>
  <main class="main">
    <header class="page-header">
      <div>
        <p class="eyebrow">Operational Reports</p>
        <h1>Vaccination activity, clinic capacity, and staffing summary.</h1>
        <p class="lead">A reporting view for reviewing system totals and scheduled vaccination records across the care network.</p>
      </div>
      <div class="header-actions">
        <a class="button" href="patients.php">Patients</a>
        <a class="button primary" href="clinics.php">Clinics</a>
      </div>
    </header>

    <?php if ($notice): ?>
      <div class="notice error"><?php echo htmlspecialchars($notice); ?></div>
    <?php endif; ?>

    <section class="grid stats-grid">
      <article class="card">
        <div class="stat-value"><?php echo $report['patients']; ?></div>
        <div class="stat-label">Registered Patients</div>
        <p class="stat-note">Total patient records in the system.</p>
      </article>
      <article class="card">
        <div class="stat-value"><?php echo $report['vaccinated']; ?></div>
        <div class="stat-label">Vaccination Records</div>
        <p class="stat-note">Completed or scheduled vaccination entries.</p>
      </article>
      <article class="card">
        <div class="stat-value"><?php echo $report['clinics']; ?></div>
        <div class="stat-label">Clinic Sites</div>
        <p class="stat-note">Active clinic records in the network.</p>
      </article>
      <article class="card">
        <div class="stat-value"><?php echo $report['vaccineLots']; ?></div>
        <div class="stat-label">Vaccine Lots</div>
        <p class="stat-note">Tracked vaccine inventory lots.</p>
      </article>
    </section>

    <section class="grid two-column">
      <div class="panel">
        <div class="panel-header">
          <div>
            <h2>Vaccination Schedule</h2>
            <p class="muted">Patient appointments by date, clinic, and manufacturer.</p>
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
            <p class="muted">Clinical workforce currently tracked in the system.</p>
          </div>
        </div>
        <div class="panel-body coverage-list">
          <div class="coverage-item">
            <div>
              <h3>Nurse records</h3>
              <p class="muted">Nurses assigned across vaccination clinics.</p>
            </div>
            <span class="tag green"><?php echo $report['nurses']; ?></span>
          </div>
          <div class="coverage-item">
            <div>
              <h3>Doctor records</h3>
              <p class="muted">Doctors assigned across vaccination clinics.</p>
            </div>
            <span class="tag green"><?php echo $report['doctors']; ?></span>
          </div>
          <div class="coverage-item">
            <div>
              <h3>Operational status</h3>
              <p class="muted">Review clinic, vaccine, and staffing records before patient intake.</p>
            </div>
            <span class="tag amber">Review</span>
          </div>
        </div>
      </aside>
    </section>
  </main>
</div>
</body>
</html>
<?php $connection = NULL; ?>
