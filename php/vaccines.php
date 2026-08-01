<?php
$dbOptional = true;
include 'connectdb.php';
include 'layout.php';

$vaccines = [];
$shipments = [];
if ($connection instanceof PDO) {
try {
    $vaccines = $connection->query("SELECT Lot, CompanyName, Prodcution, Expiry, Doses FROM Vaccine ORDER BY CompanyName, Lot")->fetchAll(PDO::FETCH_ASSOC);
    $shipments = $connection->query("SELECT ShipTo.Lots, ShipTo.Clinic, Vaccine.CompanyName, Vaccine.Doses
                                     FROM ShipTo
                                     JOIN Vaccine ON Vaccine.Lot = ShipTo.Lots
                                     ORDER BY ShipTo.Clinic")->fetchAll(PDO::FETCH_ASSOC);
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
  <title>Vaccines | Healthcare Operations Dashboard</title>
  <link rel="stylesheet" href="styles.css">
  <script src="app.js" defer></script>
</head>
<body>
<div class="app-shell">
  <?php render_sidebar(current_page_name()); ?>
  <main class="main">
    <header class="page-header">
      <div>
        <p class="eyebrow">Vaccine Logistics</p>
        <h1>Vaccine lots, manufacturers, dose counts, and clinic shipment links.</h1>
        <p class="lead">The logistics view supports data consistency tests across vaccine inventory and clinic shipment relationships.</p>
      </div>
      <div class="header-actions">
        <a class="button" href="reports.php">Reports</a>
        <a class="button primary" href="clinics.php">Clinic Shipments</a>
      </div>
    </header>

    <?php if (isset($dbError)): ?>
      <div class="notice error">Database issue: <?php echo htmlspecialchars($dbError); ?></div>
    <?php endif; ?>

    <section class="grid two-column">
      <div class="panel">
        <div class="panel-header">
          <div>
            <h2>Vaccine Inventory</h2>
            <p class="muted">Filter by lot number or manufacturer.</p>
          </div>
          <input type="search" placeholder="Filter inventory" data-table-filter="#vaccines-table" aria-label="Filter vaccine inventory">
        </div>
        <div class="panel-body">
          <table class="data-table" id="vaccines-table">
            <thead>
              <tr>
                <th>Lot</th>
                <th>Manufacturer</th>
                <th>Production</th>
                <th>Expiry</th>
                <th>Doses</th>
              </tr>
            </thead>
            <tbody>
              <?php foreach ($vaccines as $vaccine): ?>
                <tr>
                  <td><?php echo htmlspecialchars($vaccine['Lot']); ?></td>
                  <td><?php echo htmlspecialchars($vaccine['CompanyName']); ?></td>
                  <td><?php echo htmlspecialchars($vaccine['Prodcution']); ?></td>
                  <td><?php echo htmlspecialchars($vaccine['Expiry']); ?></td>
                  <td><?php echo htmlspecialchars($vaccine['Doses']); ?></td>
                </tr>
              <?php endforeach; ?>
            </tbody>
          </table>
        </div>
      </div>

      <aside class="panel">
        <div class="panel-header">
          <div>
            <h2>Shipment Coverage</h2>
            <p class="muted">Clinic assignment checks for each vaccine lot.</p>
          </div>
        </div>
        <div class="panel-body">
          <table class="data-table">
            <thead>
              <tr>
                <th>Lot</th>
                <th>Clinic</th>
                <th>Type</th>
              </tr>
            </thead>
            <tbody>
              <?php foreach ($shipments as $shipment): ?>
                <tr>
                  <td><?php echo htmlspecialchars($shipment['Lots']); ?></td>
                  <td><?php echo htmlspecialchars($shipment['Clinic']); ?></td>
                  <td><?php echo htmlspecialchars($shipment['CompanyName']); ?></td>
                </tr>
              <?php endforeach; ?>
            </tbody>
          </table>
          <div class="tag-row" style="margin-top: 16px;">
            <span class="tag green">Inventory Lookup</span>
            <span class="tag">Foreign Key Coverage</span>
            <span class="tag amber">Empty State Tests</span>
          </div>
        </div>
      </aside>
    </section>
  </main>
</div>
</body>
</html>
<?php $connection = NULL; ?>
