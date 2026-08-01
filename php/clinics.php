<?php
$dbOptional = true;
include 'connectdb.php';
include 'layout.php';

$clinics = [];
$notice = null;
if ($connection instanceof PDO) {
    try {
        $clinics = $connection->query("SELECT VaxClinic.Name, VaxClinic.Street, VaxClinic.City, VaxClinic.Prov, VaxClinic.PC, VaxClinic.date,
                                      ShipTo.Lots, Vaccine.CompanyName, Vaccine.Doses
                                      FROM VaxClinic
                                      LEFT JOIN ShipTo ON ShipTo.Clinic = VaxClinic.Name
                                      LEFT JOIN Vaccine ON Vaccine.Lot = ShipTo.Lots
                                      ORDER BY VaxClinic.Name")->fetchAll(PDO::FETCH_ASSOC);
    } catch (PDOException $e) {
        $notice = 'Unable to load clinic records: ' . $e->getMessage();
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
  <title>Clinics | Healthcare Operations Dashboard</title>
  <link rel="stylesheet" href="styles.css">
  <script src="app.js" defer></script>
</head>
<body>
<div class="app-shell">
  <?php render_sidebar(current_page_name()); ?>
  <main class="main">
    <header class="page-header">
      <div>
        <p class="eyebrow">Clinic Network</p>
        <h1>Clinic sites, operating dates, and assigned vaccine shipments.</h1>
        <p class="lead">Use this view to monitor where vaccine lots are shipped and which clinics are active in the COVID-19 network.</p>
      </div>
      <div class="header-actions">
        <a class="button" href="vaccines.php">Vaccine Inventory</a>
        <a class="button primary" href="workers.php">Staffing</a>
      </div>
    </header>

    <?php if ($notice): ?>
      <div class="notice error"><?php echo htmlspecialchars($notice); ?></div>
    <?php endif; ?>

    <section class="panel">
      <div class="panel-header">
        <div>
          <h2>Clinic Directory</h2>
          <p class="muted">Filter by clinic, city, postal code, manufacturer, or lot.</p>
        </div>
        <input type="search" placeholder="Filter clinics" data-table-filter="#clinics-table" aria-label="Filter clinic records">
      </div>
      <div class="panel-body">
        <table class="data-table" id="clinics-table">
          <thead>
            <tr>
              <th>Clinic</th>
              <th>Address</th>
              <th>Operating Date</th>
              <th>Assigned Lot</th>
              <th>Vaccine</th>
              <th>Doses</th>
            </tr>
          </thead>
          <tbody>
            <?php foreach ($clinics as $clinic): ?>
              <tr>
                <td><?php echo htmlspecialchars($clinic['Name']); ?></td>
                <td><?php echo htmlspecialchars($clinic['Street'] . ', ' . $clinic['City'] . ', ' . $clinic['Prov'] . ' ' . $clinic['PC']); ?></td>
                <td><?php echo htmlspecialchars($clinic['date']); ?></td>
                <td><?php echo htmlspecialchars($clinic['Lots'] ?? 'Not assigned'); ?></td>
                <td><?php echo htmlspecialchars($clinic['CompanyName'] ?? 'Pending'); ?></td>
                <td><?php echo htmlspecialchars($clinic['Doses'] ?? '-'); ?></td>
              </tr>
            <?php endforeach; ?>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</div>
</body>
</html>
<?php $connection = NULL; ?>
