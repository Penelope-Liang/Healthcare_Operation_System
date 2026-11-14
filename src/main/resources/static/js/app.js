/* HealthOps front-end behaviour: table filters, dialogs, delete confirmations, toasts. */
document.addEventListener("DOMContentLoaded", function () {
  // Live filter: <input data-table-filter="#table-id"> hides rows / cards that don't match.
  document.querySelectorAll("[data-table-filter]").forEach(function (filter) {
    var target = document.querySelector(filter.getAttribute("data-table-filter"));
    if (!target) {
      return;
    }

    filter.addEventListener("input", function () {
      var query = filter.value.trim().toLowerCase();
      var items = target.querySelectorAll("tbody tr, [data-filter-item]");
      var visible = 0;

      items.forEach(function (item) {
        var match = query.length === 0 || item.textContent.toLowerCase().includes(query);
        item.hidden = !match;
        if (match) {
          visible += 1;
        }
      });

      var empty = document.querySelector('[data-filter-empty="' + filter.getAttribute("data-table-filter") + '"]');
      if (empty) {
        empty.hidden = visible > 0;
      }
    });
  });

  // Dialogs: <button data-open-dialog="#id">, <button data-close-dialog>.
  function openDialog(dialog) {
    if (dialog && typeof dialog.showModal === "function" && !dialog.open) {
      dialog.showModal();
      var first = dialog.querySelector(".invalid, input:not([type=hidden]), select");
      if (first) {
        first.focus();
      }
    }
  }

  document.querySelectorAll("[data-open-dialog]").forEach(function (button) {
    button.addEventListener("click", function () {
      openDialog(document.querySelector(button.getAttribute("data-open-dialog")));
    });
  });

  document.querySelectorAll("dialog").forEach(function (dialog) {
    dialog.addEventListener("click", function (event) {
      // A click on the backdrop lands on the <dialog> element itself.
      if (event.target === dialog) {
        dialog.close();
      }
    });
    dialog.querySelectorAll("[data-close-dialog]").forEach(function (button) {
      button.addEventListener("click", function () {
        dialog.close();
      });
    });
    if (dialog.hasAttribute("data-auto-open")) {
      openDialog(dialog);
    }
  });

  // Destructive actions: <form data-confirm="Delete X?">.
  document.querySelectorAll("form[data-confirm]").forEach(function (form) {
    form.addEventListener("submit", function (event) {
      if (!window.confirm(form.getAttribute("data-confirm"))) {
        event.preventDefault();
      }
    });
  });

  // Toasts dismiss themselves after a few seconds.
  document.querySelectorAll(".toast").forEach(function (toast) {
    var dismiss = function () {
      toast.remove();
    };
    var close = toast.querySelector("[data-dismiss-toast]");
    if (close) {
      close.addEventListener("click", dismiss);
    }
    if (!toast.classList.contains("error")) {
      window.setTimeout(dismiss, 5000);
    }
  });
});
