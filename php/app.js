document.addEventListener("DOMContentLoaded", function () {
  var filters = document.querySelectorAll("[data-table-filter]");

  filters.forEach(function (filter) {
    var table = document.querySelector(filter.getAttribute("data-table-filter"));
    if (!table) {
      return;
    }

    filter.addEventListener("input", function () {
      var query = filter.value.trim().toLowerCase();
      var rows = table.querySelectorAll("tbody tr");

      rows.forEach(function (row) {
        row.hidden = query.length > 0 && !row.textContent.toLowerCase().includes(query);
      });
    });
  });
});
