const fs = require("fs");
const path = require("path");

const script = fs.readFileSync(path.join(__dirname, "../../src/main/resources/static/js/app.js"), "utf8");

function boot(html) {
  document.body.innerHTML = html;
  window.eval(script);
  document.dispatchEvent(new Event("DOMContentLoaded"));
}

function type(input, value) {
  input.value = value;
  input.dispatchEvent(new Event("input"));
}

describe("table filter", () => {
  beforeEach(() => {
    boot(`
      <input data-table-filter="#patients-table" />
      <table id="patients-table">
        <tbody>
          <tr><td>Amy White</td></tr>
          <tr><td>John Smith</td></tr>
        </tbody>
      </table>
      <div data-filter-empty="#patients-table" hidden>No matches</div>
    `);
  });

  test("hides rows that do not match the search query", () => {
    const rows = document.querySelectorAll("tbody tr");
    type(document.querySelector("[data-table-filter]"), "amy");

    expect(rows[0].hidden).toBe(false);
    expect(rows[1].hidden).toBe(true);
  });

  test("shows all rows when the query is cleared", () => {
    const filter = document.querySelector("[data-table-filter]");
    const rows = document.querySelectorAll("tbody tr");
    type(filter, "john");
    type(filter, "");

    expect(rows[0].hidden).toBe(false);
    expect(rows[1].hidden).toBe(false);
  });

  test("shows the empty state only when nothing matches", () => {
    const filter = document.querySelector("[data-table-filter]");
    const empty = document.querySelector("[data-filter-empty]");

    type(filter, "nobody");
    expect(empty.hidden).toBe(false);
    type(filter, "smith");
    expect(empty.hidden).toBe(true);
  });
});

describe("card filter", () => {
  test("filters [data-filter-item] cards, not just table rows", () => {
    boot(`
      <input data-table-filter="#grid" />
      <section id="grid">
        <article data-filter-item>Rexall Kingston</article>
        <article data-filter-item>Shoppers Drug Mart</article>
      </section>
    `);
    const cards = document.querySelectorAll("[data-filter-item]");
    type(document.querySelector("[data-table-filter]"), "rexall");

    expect(cards[0].hidden).toBe(false);
    expect(cards[1].hidden).toBe(true);
  });
});

describe("delete confirmation", () => {
  test("cancelling the confirm prompt stops the form submission", () => {
    boot(`<form data-confirm="Delete?"><button type="submit">Delete</button></form>`);
    window.confirm = jest.fn(() => false);
    const event = new Event("submit", { cancelable: true });

    document.querySelector("form").dispatchEvent(event);

    expect(window.confirm).toHaveBeenCalledWith("Delete?");
    expect(event.defaultPrevented).toBe(true);
  });
});
