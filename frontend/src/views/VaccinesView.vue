<script setup>
import { computed, ref } from "vue";
import AppIcon from "@/components/AppIcon.vue";
import FormField from "@/components/FormField.vue";
import LoadState from "@/components/LoadState.vue";
import ModalDialog from "@/components/ModalDialog.vue";
import SearchBox from "@/components/SearchBox.vue";
import { useForm } from "@/composables/useForm";
import { useResource } from "@/composables/useResource";
import { useToasts } from "@/composables/useToasts";
import { api } from "@/lib/api";
import { isPast, prettyDate } from "@/lib/dates";
import { matchesQuery } from "@/lib/text";

const toast = useToasts();
const { data, loading, error, reload } = useResource(async () => {
  const [lots, shipments, companies, clinics] = await Promise.all([
    api.vaccineLots(), api.shipments(), api.companies(), api.clinics()
  ]);
  return { lots, shipments, companies, clinics };
}, { lots: [], shipments: [], companies: [], clinics: [] });

const query = ref("");
const visibleLots = computed(() => data.value.lots.filter((v) =>
  matchesQuery(query.value, v.lot, v.manufacturer, prettyDate(v.expiryDate))));
const totalDoses = computed(() => data.value.lots.reduce((sum, v) => sum + v.doses, 0));
const expiredCount = computed(() => data.value.lots.filter((v) => isPast(v.expiryDate)).length);

// New vaccine lot
const lotDialogOpen = ref(false);
const lotForm = useForm({ lot: "", manufacturer: "", productionDate: "", expiryDate: "", doses: "" });

function openLotDialog() {
  lotForm.reset();
  lotDialogOpen.value = true;
}

async function createLot() {
  try {
    const lot = await lotForm.submit((v) => api.createVaccineLot({ ...v, doses: v.doses === "" ? null : Number(v.doses) }));
    toast.success(`Lot ${lot.lot} was added to inventory.`);
    lotDialogOpen.value = false;
    reload();
  } catch {
    // errors are shown inside the dialog
  }
}

async function removeLot(v) {
  if (!window.confirm(`Remove lot ${v.lot} and its shipments?`)) return;
  try {
    await api.deleteVaccineLot(v.lot);
    toast.success(`Lot ${v.lot} was removed.`);
    reload();
  } catch (e) {
    toast.error(e.message);
  }
}

// Shipment
const shipDialogOpen = ref(false);
const shipForm = useForm({ lot: "", clinicName: "" });

function openShipDialog() {
  shipForm.reset();
  shipDialogOpen.value = true;
}

async function createShipment() {
  try {
    const s = await shipForm.submit((v) => api.createShipment(v));
    toast.success(`Lot ${s.lot} is shipping to ${s.clinicName}.`);
    shipDialogOpen.value = false;
    reload();
  } catch {
    // errors are shown inside the dialog
  }
}

async function removeShipment(s) {
  if (!window.confirm(`Cancel the shipment of ${s.lot} to ${s.clinicName}?`)) return;
  try {
    await api.deleteShipment(s.lot, s.clinicName);
    toast.success(`Shipment of ${s.lot} to ${s.clinicName} was cancelled.`);
    reload();
  } catch (e) {
    toast.error(e.message);
  }
}
</script>

<template>
  <div class="content">
    <header class="page-header">
      <div>
        <p class="eyebrow">Logistics</p>
        <h1>Vaccine inventory</h1>
        <p class="subtitle">Track vaccine lots and where each one has shipped.</p>
      </div>
      <div class="header-actions">
        <button class="btn" type="button" @click="openShipDialog"><AppIcon name="truck" />Assign shipment</button>
        <button class="btn btn-primary" type="button" @click="openLotDialog"><AppIcon name="plus" />Add lot</button>
      </div>
    </header>

    <LoadState :loading="loading" :error="error" @retry="reload">
      <section class="grid grid-4" aria-label="Inventory metrics">
        <article class="card kpi">
          <div class="kpi-top"><span class="kpi-label">Vaccine lots</span><span class="icon-bubble"><AppIcon name="package" /></span></div>
          <div class="kpi-value">{{ data.lots.length }}</div>
        </article>
        <article class="card kpi">
          <div class="kpi-top"><span class="kpi-label">Doses in stock</span><span class="icon-bubble info"><AppIcon name="syringe" /></span></div>
          <div class="kpi-value">{{ totalDoses }}</div>
        </article>
        <article class="card kpi">
          <div class="kpi-top"><span class="kpi-label">Shipments</span><span class="icon-bubble violet"><AppIcon name="truck" /></span></div>
          <div class="kpi-value">{{ data.shipments.length }}</div>
        </article>
        <article class="card kpi">
          <div class="kpi-top"><span class="kpi-label">Expired lots</span><span class="icon-bubble warning"><AppIcon name="alert" /></span></div>
          <div class="kpi-value">{{ expiredCount }}</div>
        </article>
      </section>

      <section class="grid grid-main-side">
        <article class="card">
          <div class="card-header">
            <div>
              <h2>Lots</h2>
              <p>Production and expiry by lot</p>
            </div>
            <SearchBox v-model="query" placeholder="Search lots" />
          </div>
          <div class="table-wrap">
            <table class="table" id="vaccines-table">
              <thead>
                <tr>
                  <th>Lot</th>
                  <th>Manufacturer</th>
                  <th>Produced</th>
                  <th>Expires</th>
                  <th class="num">Doses</th>
                  <th class="actions"><span class="sr-only">Actions</span></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="v in visibleLots" :key="v.lot">
                  <td><span class="chip">{{ v.lot }}</span></td>
                  <td><strong>{{ v.manufacturer }}</strong></td>
                  <td>{{ prettyDate(v.productionDate) }}</td>
                  <td>
                    <div class="cell-stack">
                      <span>{{ prettyDate(v.expiryDate) }}</span>
                      <span>
                        <span v-if="isPast(v.expiryDate)" class="badge danger">Expired</span>
                        <span v-else class="badge success">Usable</span>
                      </span>
                    </div>
                  </td>
                  <td class="num">{{ v.doses }}</td>
                  <td class="actions">
                    <button class="btn btn-icon" type="button" :aria-label="`Delete lot ${v.lot}`"
                            :title="`Delete lot ${v.lot}`" @click="removeLot(v)">
                      <AppIcon name="trash" />
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
            <div v-if="data.lots.length === 0" class="empty"><strong>No vaccine lots</strong>Add a lot to start tracking inventory.</div>
            <div v-else-if="visibleLots.length === 0" class="empty"><strong>No matching lots</strong></div>
          </div>
        </article>

        <article class="card">
          <div class="card-header">
            <div>
              <h2>Shipments</h2>
              <p>Lots delivered to clinics</p>
            </div>
          </div>
          <div class="list">
            <div v-for="s in data.shipments" :key="s.lot + '/' + s.clinicName" class="list-row">
              <span class="icon-bubble violet"><AppIcon name="truck" /></span>
              <div class="grow cell-stack">
                <strong>{{ s.clinicName }}</strong>
                <span class="sub"><span class="mono">{{ s.lot }}</span> · {{ s.manufacturer }}</span>
              </div>
              <button class="btn btn-icon" type="button" :aria-label="`Cancel shipment of ${s.lot} to ${s.clinicName}`"
                      title="Cancel shipment" @click="removeShipment(s)">
                <AppIcon name="trash" />
              </button>
            </div>
            <div v-if="data.shipments.length === 0" class="empty"><strong>No shipments yet</strong>Assign a lot to a clinic.</div>
          </div>
        </article>
      </section>
    </LoadState>

    <ModalDialog v-model:open="lotDialogOpen" title="Add vaccine lot" description="Register a new lot received from a manufacturer."
                 submit-label="Create lot" :busy="lotForm.busy.value" :error="lotForm.formError.value" @submit="createLot">
      <div class="form-grid">
        <FormField label="Lot code" for="lot" :error="lotForm.errors.lot">
          <input id="lot" v-model="lotForm.values.lot" :class="{ invalid: lotForm.errors.lot }" placeholder="AB1234"
                 pattern="[A-Za-z0-9]{1,12}" maxlength="12" autocomplete="off" required>
        </FormField>
        <FormField label="Manufacturer" for="manufacturer" :error="lotForm.errors.manufacturer">
          <select id="manufacturer" v-model="lotForm.values.manufacturer" :class="{ invalid: lotForm.errors.manufacturer }" required>
            <option value="">Select manufacturer</option>
            <option v-for="c in data.companies" :key="c.name" :value="c.name">{{ c.name }}</option>
          </select>
        </FormField>
        <FormField label="Production date" for="productionDate" :error="lotForm.errors.productionDate">
          <input id="productionDate" v-model="lotForm.values.productionDate" type="date" :class="{ invalid: lotForm.errors.productionDate }" required>
        </FormField>
        <FormField label="Expiry date" for="expiryDate" :error="lotForm.errors.expiryDate || lotForm.errors.expiryAfterProduction">
          <input id="expiryDate" v-model="lotForm.values.expiryDate" type="date"
                 :class="{ invalid: lotForm.errors.expiryDate || lotForm.errors.expiryAfterProduction }" required>
        </FormField>
        <FormField label="Doses" for="doses" full :error="lotForm.errors.doses">
          <input id="doses" v-model="lotForm.values.doses" type="number" min="0" step="1" :class="{ invalid: lotForm.errors.doses }" required>
        </FormField>
      </div>
    </ModalDialog>

    <ModalDialog v-model:open="shipDialogOpen" title="Assign shipment" description="Send a vaccine lot to a clinic."
                 submit-label="Assign shipment" :busy="shipForm.busy.value" :error="shipForm.formError.value" @submit="createShipment">
      <div class="form-grid">
        <FormField label="Vaccine lot" for="shipment-lot" full :error="shipForm.errors.lot">
          <select id="shipment-lot" v-model="shipForm.values.lot" :class="{ invalid: shipForm.errors.lot }" required>
            <option value="">Select a lot</option>
            <option v-for="v in data.lots" :key="v.lot" :value="v.lot">{{ v.lot }} · {{ v.manufacturer }}</option>
          </select>
        </FormField>
        <FormField label="Destination clinic" for="shipment-clinic" full :error="shipForm.errors.clinicName">
          <select id="shipment-clinic" v-model="shipForm.values.clinicName" :class="{ invalid: shipForm.errors.clinicName }" required>
            <option value="">Select a clinic</option>
            <option v-for="c in data.clinics" :key="c.name" :value="c.name">{{ c.name }}</option>
          </select>
        </FormField>
      </div>
    </ModalDialog>
  </div>
</template>
