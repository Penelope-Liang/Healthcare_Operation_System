<?php
$dbOptional = true;
include '../includes/connectdb.php';
include '../includes/layout.php';

$vaccines = [];
$shipments = [];
$companies = [];
$clinics = [];
$notice = null;
$noticeType = 'success';
if ($connection instanceof PDO) {
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        try {
            if (isset($_POST['create_vaccine'])) {
                $stmt = $connection->prepare('INSERT INTO Vaccine (Lot, CompanyName, Prodcution, Expiry, Doses) VALUES (:lot, :company, :production, :expiry, :doses)');
                $stmt->execute([
                    ':lot' => trim($_POST['Lot'] ?? ''),
                    ':company' => trim($_POST['CompanyName'] ?? ''),
                    ':production' => trim($_POST['Prodcution'] ?? ''),
                    ':expiry' => trim($_POST['Expiry'] ?? ''),
                    ':doses' => (int) ($_POST['Doses'] ?? 0),
                ]);
                $notice = 'Vaccine lot created successfully.';
            } elseif (isset($_POST['assign_shipment'])) {
                $stmt = $connection->prepare('INSERT INTO ShipTo (Lots, Clinic) VALUES (:lot, :clinic)');
                $stmt->execute([
                    ':lot' => trim($_POST['Lots'] ?? ''),
                    ':clinic' => trim($_POST['Clinic'] ?? ''),
                ]);
                $notice = 'Vaccine shipment assigned successfully.';
            }
        } catch (PDOException $e) {
            $notice = 'Unable to save vaccine data: ' . $e->getMessage();
            $noticeType = 'error';
        }
    }

    try {
        $companies = $connection->query("SELECT Name FROM Company ORDER BY Name")->fetchAll(PDO::FETCH_ASSOC);
        $clinics = $connection->query("SELECT Name FROM VaxClinic ORDER BY Name")->fetchAll(PDO::FETCH_ASSOC);
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
  <link rel="stylesheet" href="assets/styles.css">
  <script src="assets/app.js" defer></script>
</head>
<body>
<div class="app-shell">
  <?php render_sidebar(current_page_name()); ?>
  <main class="main">
    <header class="page-header">
      <div>
        <p class="eyebrow">Vaccine Logistics</p>
        <h1>Vaccines</h1>
      </div>
      <div class="header-actions">
        <a class="button" href="reports.php">Reports</a>
        <a class="button primary" href="clinics.php">Clinic Shipments</a>
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
            <h2>Vaccine Inventory</h2>
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
            <h2>Add Vaccine Lot</h2>
          </div>
        </div>
        <div class="panel-body">
          <form class="form-grid" method="POST" action="vaccines.php">
            <input type="hidden" name="create_vaccine" value="1">
            <div class="field">
              <label for="Lot">Lot</label>
              <input id="Lot" name="Lot" maxlength="30" required>
            </div>
            <div class="field">
              <label for="CompanyName">Manufacturer</label>
              <select id="CompanyName" name="CompanyName" required>
                <option value="">Select company</option>
                <?php foreach ($companies as $company): ?>
                  <option value="<?php echo htmlspecialchars($company['Name']); ?>"><?php echo htmlspecialchars($company['Name']); ?></option>
                <?php endforeach; ?>
              </select>
            </div>
            <div class="field">
              <label for="Prodcution">Production</label>
              <input id="Prodcution" name="Prodcution" placeholder="2026-08-01" maxlength="11" required>
            </div>
            <div class="field">
              <label for="Expiry">Expiry</label>
              <input id="Expiry" name="Expiry" placeholder="2027-08-01" maxlength="11" required>
            </div>
            <div class="field full">
              <label for="Doses">Doses</label>
              <input id="Doses" type="number" name="Doses" min="0" required>
            </div>
            <button class="button primary full" type="submit">Create Vaccine Lot</button>
          </form>
        </div>
      </aside>
    </section>

    <section class="grid two-column" style="margin-top: 18px;">
      <div class="panel">
        <div class="panel-header">
          <div>
            <h2>Shipments</h2>
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
        </div>
      </div>

      <aside class="panel">
        <div class="panel-header">
          <div>
            <h2>Assign Shipment</h2>
          </div>
        </div>
        <div class="panel-body">
          <form class="form-grid" method="POST" action="vaccines.php">
            <input type="hidden" name="assign_shipment" value="1">
            <div class="field full">
              <label for="Lots">Lot</label>
              <select id="Lots" name="Lots" required>
                <option value="">Select lot</option>
                <?php foreach ($vaccines as $vaccine): ?>
                  <option value="<?php echo htmlspecialchars($vaccine['Lot']); ?>"><?php echo htmlspecialchars($vaccine['Lot'] . ' - ' . $vaccine['CompanyName']); ?></option>
                <?php endforeach; ?>
              </select>
            </div>
            <div class="field full">
              <label for="Clinic">Clinic</label>
              <select id="Clinic" name="Clinic" required>
                <option value="">Select clinic</option>
                <?php foreach ($clinics as $clinic): ?>
                  <option value="<?php echo htmlspecialchars($clinic['Name']); ?>"><?php echo htmlspecialchars($clinic['Name']); ?></option>
                <?php endforeach; ?>
              </select>
            </div>
            <button class="button primary full" type="submit">Assign Shipment</button>
          </form>
        </div>
      </aside>
    </section>
  </main>
</div>
</body>
</html>
<?php $connection = NULL; ?>
