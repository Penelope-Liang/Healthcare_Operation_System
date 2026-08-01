<?php
function current_page_name() {
    return basename($_SERVER['PHP_SELF']);
}

function render_sidebar($activePage) {
    $links = [
        ['covid.php', 'Dashboard', 'D'],
        ['patients.php', 'Patients', 'P'],
        ['vaccines.php', 'Vaccines', 'V'],
        ['workers.php', 'Workers', 'W'],
        ['clinics.php', 'Clinics', 'C'],
        ['reports.php', 'Reports', 'R'],
    ];
?>
<aside class="sidebar">
  <div class="brand">
    <div class="brand-mark">HC</div>
    <div>
      <p class="brand-title">Healthcare Ops</p>
      <p class="brand-subtitle">Clinic Operations</p>
    </div>
  </div>
  <nav class="nav" aria-label="Primary navigation">
    <?php foreach ($links as $link): ?>
      <a class="nav-link <?php echo $activePage === $link[0] ? 'active' : ''; ?>" href="<?php echo $link[0]; ?>">
        <span class="nav-icon"><?php echo $link[2]; ?></span>
        <span><?php echo $link[1]; ?></span>
      </a>
    <?php endforeach; ?>
  </nav>
</aside>
<?php
}
?>
