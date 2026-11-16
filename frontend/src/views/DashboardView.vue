<script setup>
import { computed } from "vue";
import AppIcon from "@/components/AppIcon.vue";
import PersonAvatar from "@/components/PersonAvatar.vue";
import LoadState from "@/components/LoadState.vue";
import { useResource } from "@/composables/useResource";
import { api } from "@/lib/api";
import { prettyDate } from "@/lib/dates";

const { data: s, loading, error, reload } = useResource(api.dashboard);

const today = new Intl.DateTimeFormat("en-US", { weekday: "long", month: "long", day: "numeric" }).format(new Date());
const staff = computed(() => s.value.nurses + s.value.doctors);
const coverage = computed(() => (s.value.patients === 0 ? 0 : Math.round((s.value.vaccinated * 100) / s.value.patients)));
const maxDoses = computed(() => Math.max(1, ...s.value.dosesByManufacturer.map((m) => m.doses)));

function clinicStatus(c) {
  if (c.lots.length === 0) return { label: "No supply", tone: "warning" };
  if (c.nurses === 0 || c.doctors === 0) return { label: "Understaffed", tone: "danger" };
  return { label: "Ready", tone: "success" };
}
</script>

<template>
  <div class="content">
    <header class="page-header">
      <div>
        <p class="eyebrow">{{ today }}</p>
        <h1>Dashboard</h1>
        <p class="subtitle">Vaccination coverage, inventory and staffing across every clinic.</p>
      </div>
      <div class="header-actions">
        <RouterLink class="btn" to="/patients"><AppIcon name="users" />View patients</RouterLink>
        <RouterLink class="btn btn-primary" to="/vaccinations"><AppIcon name="syringe" />Record vaccination</RouterLink>
      </div>
    </header>

    <LoadState :loading="loading" :error="error" @retry="reload">
      <template v-if="s">
        <section class="grid grid-4" aria-label="Key metrics">
          <article class="card kpi">
            <div class="kpi-top">
              <span class="kpi-label">Registered patients</span>
              <span class="icon-bubble info"><AppIcon name="users" /></span>
            </div>
            <div class="kpi-value">{{ s.patients }}</div>
            <p class="kpi-foot">{{ s.patients - s.vaccinated }} awaiting a first dose</p>
          </article>
          <article class="card kpi">
            <div class="kpi-top">
              <span class="kpi-label">Vaccination coverage</span>
              <span class="icon-bubble success"><AppIcon name="shield" /></span>
            </div>
            <div class="kpi-value">{{ coverage }}<small>%</small></div>
            <div class="progress" role="progressbar" aria-label="Vaccination coverage"
                 :aria-valuenow="coverage" aria-valuemin="0" aria-valuemax="100">
              <span :style="{ width: coverage + '%' }"></span>
            </div>
          </article>
          <article class="card kpi">
            <div class="kpi-top">
              <span class="kpi-label">Clinic sites</span>
              <span class="icon-bubble"><AppIcon name="building" /></span>
            </div>
            <div class="kpi-value">{{ s.clinics }}</div>
            <p class="kpi-foot">{{ s.vaccineLots }} vaccine lots in circulation</p>
          </article>
          <article class="card kpi">
            <div class="kpi-top">
              <span class="kpi-label">Clinical staff</span>
              <span class="icon-bubble violet"><AppIcon name="stethoscope" /></span>
            </div>
            <div class="kpi-value">{{ staff }}</div>
            <p class="kpi-foot">{{ s.nurses }} nurses · {{ s.doctors }} doctors</p>
          </article>
        </section>

        <section class="grid grid-main-side">
          <article class="card">
            <div class="card-header">
              <div>
                <h2>Recent vaccinations</h2>
                <p>Latest doses administered across all clinics</p>
              </div>
              <RouterLink class="link-arrow" to="/vaccinations">View all <AppIcon name="arrow-right" /></RouterLink>
            </div>
            <div class="list">
              <div v-for="v in s.recentVaccinations" :key="v.ohip" class="list-row">
                <PersonAvatar :first="v.firstName" :last="v.lastName" />
                <div class="grow cell-stack">
                  <strong>{{ v.firstName }} {{ v.lastName }}</strong>
                  <span class="sub">{{ v.clinicName }}</span>
                </div>
                <span class="badge info plain">{{ v.manufacturer }}</span>
                <div class="meta cell-stack">
                  <span>{{ prettyDate(v.date) }}</span>
                  <span class="sub">{{ v.time }}</span>
                </div>
              </div>
              <div v-if="s.recentVaccinations.length === 0" class="empty">
                <strong>No vaccinations yet</strong>Record the first dose from the Vaccinations page.
              </div>
            </div>
          </article>

          <article class="card">
            <div class="card-header">
              <div>
                <h2>Doses by manufacturer</h2>
                <p>Current inventory</p>
              </div>
            </div>
            <div class="card-body">
              <div class="bars">
                <div v-for="m in s.dosesByManufacturer" :key="m.manufacturer" class="bar-row">
                  <div class="bar-head">
                    <span>{{ m.manufacturer }}</span>
                    <span>{{ m.doses }} doses</span>
                  </div>
                  <div class="progress"><span :style="{ width: (m.doses * 100) / maxDoses + '%' }"></span></div>
                </div>
              </div>
              <div class="summary-line">
                <span>Total doses in stock</span>
                <strong>{{ s.dosesInInventory }}</strong>
              </div>
              <div class="spaced">
                <RouterLink class="link-arrow" to="/vaccines">Manage inventory <AppIcon name="arrow-right" /></RouterLink>
              </div>
            </div>
          </article>
        </section>

        <section class="card">
          <div class="card-header">
            <div>
              <h2>Clinic coverage</h2>
              <p>Supply and staffing at each vaccination site</p>
            </div>
            <RouterLink class="link-arrow" to="/clinics">All clinics <AppIcon name="arrow-right" /></RouterLink>
          </div>
          <div class="table-wrap">
            <table class="table">
              <thead>
                <tr>
                  <th>Clinic</th>
                  <th>Vaccine lots</th>
                  <th class="num">Nurses</th>
                  <th class="num">Doctors</th>
                  <th class="num">Doses given</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="c in s.clinicCoverage" :key="c.clinic.name">
                  <td>
                    <div class="cell-stack">
                      <strong>{{ c.clinic.name }}</strong>
                      <span class="sub">{{ c.clinic.city }}, {{ c.clinic.prov }}</span>
                    </div>
                  </td>
                  <td>
                    <div class="chips">
                      <span v-for="lot in c.lots" :key="lot" class="chip">{{ lot }}</span>
                      <span v-if="c.lots.length === 0" class="cell-muted">None</span>
                    </div>
                  </td>
                  <td class="num">{{ c.nurses }}</td>
                  <td class="num">{{ c.doctors }}</td>
                  <td class="num">{{ c.vaccinations }}</td>
                  <td><span class="badge" :class="clinicStatus(c).tone">{{ clinicStatus(c).label }}</span></td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>
      </template>
    </LoadState>
  </div>
</template>
