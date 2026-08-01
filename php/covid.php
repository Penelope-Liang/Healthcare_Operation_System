<?php
$dbOptional = true;
include 'connectdb.php';
include 'layout.php';

$stats = [
    'patients' => 0,
    'vaccinations' => 0,
    'clinics' => 0,
    'workers' => 0,
];

if ($connection instanceof PDO) {
try {
    $stats['patients'] = (int) $connection->query("SELECT COUNT(*) FROM Patient")->fetchColumn();
    $stats['vaccinations'] = (int) $connection->query("SELECT COUNT(*) FROM Vaccination")->fetchColumn();
    $stats['clinics'] = (int) $connection->query("SELECT COUNT(*) FROM VaxClinic")->fetchColumn();
    $nurses = (int) $connection->query("SELECT COUNT(*) FROM Nurse")->fetchColumn();
    $doctors = (int) $connection->query("SELECT COUNT(*) FROM Doctor")->fetchColumn();
    $stats['workers'] = $nurses + $doctors;
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
  <title>Healthcare Operations Dashboard</title>
  <link rel="stylesheet" href="styles.css">
  <script src="app.js" defer></script>
</head>
<body>
<div class="app-shell">
  <?php render_sidebar(current_page_name()); ?>
  <main class="main">
    <header class="page-header">
      <div>
        <p class="eyebrow">Healthcare Operations Dashboard</p>
        <h1>COVID-19 patient, vaccine, and staff operations in one clinical workspace.</h1>
        <p class="lead">A modern operations dashboard for patient records, vaccination logistics, clinic sites, and clinical staffing across the COVID-19 care network.</p>
      </div>
      <div class="header-actions">
        <a class="button" href="patients.php">View Patients</a>
        <a class="button primary" href="reports.php">View Reports</a>
      </div>
    </header>

    <?php if (isset($dbError)): ?>
      <div class="notice error">Database connection issue: <?php echo htmlspecialchars($dbError); ?></div>
    <?php endif; ?>

    <section class="grid stats-grid" aria-label="System metrics">
      <article class="card">
        <div class="stat-value"><?php echo $stats['patients']; ?></div>
        <div class="stat-label">Patient Records</div>
        <p class="stat-note">Validated by search, add, and record integrity workflows.</p>
      </article>
      <article class="card">
        <div class="stat-value"><?php echo $stats['vaccinations']; ?></div>
        <div class="stat-label">Vaccination Events</div>
        <p class="stat-note">Tracks clinic, lot, date, time, and manufacturer links.</p>
      </article>
      <article class="card">
        <div class="stat-value"><?php echo $stats['clinics']; ?></div>
        <div class="stat-label">Clinic Sites</div>
        <p class="stat-note">Supports vaccine shipment and worker assignment checks.</p>
      </article>
      <article class="card">
        <div class="stat-value"><?php echo $stats['workers']; ?></div>
        <div class="stat-label">Clinical Workers</div>
        <p class="stat-note">Combines nurse and doctor assignments by clinic location.</p>
      </article>
    </section>

    <section class="grid two-column">
      <div class="panel">
        <div class="panel-header">
          <div>
            <h2>Core Workflows</h2>
            <p class="muted">Daily tools for patient, vaccine, clinic, and staffing operations.</p>
          </div>
        </div>
        <div class="panel-body">
          <div class="module-list">
            <a class="module-card" href="patients.php">
              <div>
                <h3>Patient Management</h3>
                <p class="muted">Search patient records, review vaccination history, and add new patients.</p>
              </div>
              <div class="tag-row">
                <span class="tag green">Records</span>
                <span class="tag">Appointments</span>
              </div>
            </a>
            <a class="module-card" href="vaccines.php">
              <div>
                <h3>Vaccine Logistics</h3>
                <p class="muted">Review lots, manufacturers, clinics, dose counts, and shipment relationships.</p>
              </div>
              <div class="tag-row">
                <span class="tag green">Inventory</span>
                <span class="tag">Shipments</span>
              </div>
            </a>
            <a class="module-card" href="workers.php">
              <div>
                <h3>Worker Assignments</h3>
                <p class="muted">Validate nurse and doctor assignments across vaccination clinic locations.</p>
              </div>
              <div class="tag-row">
                <span class="tag">Nurses</span>
                <span class="tag">Doctors</span>
              </div>
            </a>
            <a class="module-card" href="clinics.php">
              <div>
                <h3>Clinic Network</h3>
                <p class="muted">Review clinic addresses, operating dates, and linked vaccine shipments.</p>
              </div>
              <div class="tag-row">
                <span class="tag green">Sites</span>
                <span class="tag">Schedules</span>
              </div>
            </a>
          </div>
        </div>
      </div>

      <aside class="panel">
        <div class="panel-header">
          <div>
            <h2>Operations Snapshot</h2>
            <p class="muted">What staff should monitor today.</p>
          </div>
        </div>
        <div class="panel-body coverage-list">
          <div class="coverage-item">
            <div>
              <h3>Patient records ready for review</h3>
              <p class="muted">Confirm scheduled patients have clinic, vaccine, date, and time data.</p>
            </div>
            <span class="tag green">Normal</span>
          </div>
          <div class="coverage-item">
            <div>
              <h3>Vaccine lots linked to clinics</h3>
              <p class="muted">Monitor manufacturer lots and assigned clinic shipment records.</p>
            </div>
            <span class="tag green">Active</span>
          </div>
          <div class="coverage-item">
            <div>
              <h3>Clinical staffing assignments</h3>
              <p class="muted">Review nurse and doctor assignments for each vaccination clinic.</p>
            </div>
            <span class="tag amber">Review</span>
          </div>
          <div class="quick-links">
            <a class="button" href="clinics.php">Clinics</a>
            <a class="button" href="reports.php">Reports</a>
          </div>
        </div>
      </aside>
    </section>
  </main>
</div>
</body>
</html>
<?php $connection = NULL; ?>
