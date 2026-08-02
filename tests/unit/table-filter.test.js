const fs = require("fs");
const path = require("path");

describe("table filter", () => {
  beforeEach(() => {
    document.body.innerHTML = `
      <input data-table-filter="#patients-table" />
      <table id="patients-table">
        <tbody>
          <tr><td>Amy White</td></tr>
          <tr><td>John Smith</td></tr>
        </tbody>
      </table>
    `;

    const script = fs.readFileSync(path.join(__dirname, "../../public/assets/app.js"), "utf8");
    window.eval(script);
    document.dispatchEvent(new Event("DOMContentLoaded"));
  });

  test("hides rows that do not match the search query", () => {
    const filter = document.querySelector("[data-table-filter]");
    const rows = document.querySelectorAll("tbody tr");

    filter.value = "amy";
    filter.dispatchEvent(new Event("input"));

    expect(rows[0].hidden).toBe(false);
    expect(rows[1].hidden).toBe(true);
  });

  test("shows all rows when the query is cleared", () => {
    const filter = document.querySelector("[data-table-filter]");
    const rows = document.querySelectorAll("tbody tr");

    filter.value = "john";
    filter.dispatchEvent(new Event("input"));
    filter.value = "";
    filter.dispatchEvent(new Event("input"));

    expect(rows[0].hidden).toBe(false);
    expect(rows[1].hidden).toBe(false);
  });
});
