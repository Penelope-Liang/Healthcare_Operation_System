<?php
$dbOptional = true;
include '../includes/connectdb.php';
include '../includes/layout.php';

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
  <link rel="stylesheet" href="assets/styles.css">
  <script src="assets/app.js" defer></script>
</head>
<body>
<div class="app-shell">
  <?php render_sidebar(current_page_name()); ?>
  <main class="main">
    <header class="page-header">
      <div>
        <p class="eyebrow">Healthcare Operations</p>
        <h1>Dashboard</h1>
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
      </article>
      <article class="card">
        <div class="stat-value"><?php echo $stats['vaccinations']; ?></div>
        <div class="stat-label">Vaccination Events</div>
      </article>
      <article class="card">
        <div class="stat-value"><?php echo $stats['clinics']; ?></div>
        <div class="stat-label">Clinic Sites</div>
      </article>
      <article class="card">
        <div class="stat-value"><?php echo $stats['workers']; ?></div>
        <div class="stat-label">Clinical Workers</div>
      </article>
    </section>

    <section class="grid two-column">
      <div class="panel">
        <div class="panel-header">
          <div>
            <h2>Modules</h2>
          </div>
        </div>
        <div class="panel-body">
          <div class="module-list">
            <a class="module-card" href="patients.php">
              <div>
                <h3>Patients</h3>
                <span class="module-action">Open</span>
              </div>
            </a>
            <a class="module-card" href="vaccines.php">
              <div>
                <h3>Vaccines</h3>
                <span class="module-action">Open</span>
              </div>
            </a>
            <a class="module-card" href="workers.php">
              <div>
                <h3>Workers</h3>
                <span class="module-action">Open</span>
              </div>
            </a>
            <a class="module-card" href="clinics.php">
              <div>
                <h3>Clinics</h3>
                <span class="module-action">Open</span>
              </div>
            </a>
          </div>
        </div>
      </div>

      <aside class="panel">
        <div class="panel-header">
          <div>
            <h2>Operations Snapshot</h2>
          </div>
        </div>
        <div class="panel-body coverage-list">
          <div class="coverage-item">
            <div>
              <h3>Patients</h3>
            </div>
            <span class="tag green">Normal</span>
          </div>
          <div class="coverage-item">
            <div>
              <h3>Vaccines</h3>
            </div>
            <span class="tag green">Active</span>
          </div>
          <div class="coverage-item">
            <div>
              <h3>Staffing</h3>
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
