<script setup>
import { computed, ref } from "vue";
import AppIcon from "@/components/AppIcon.vue";
import FormField from "@/components/FormField.vue";
import LoadState from "@/components/LoadState.vue";
import ModalDialog from "@/components/ModalDialog.vue";
import PersonAvatar from "@/components/PersonAvatar.vue";
import SearchBox from "@/components/SearchBox.vue";
import { useForm } from "@/composables/useForm";
import { useResource } from "@/composables/useResource";
import { useToasts } from "@/composables/useToasts";
import { api } from "@/lib/api";
import { prettyDate } from "@/lib/dates";
import { fullName, matchesQuery, plural } from "@/lib/text";

const toast = useToasts();
const { data, loading, error, reload } = useResource(async () => {
  const [records, patients, clinics, lots] = await Promise.all([
    api.vaccinationDetails(), api.patients(), api.clinics(), api.vaccineLots()
  ]);
  return { records, patients, clinics, lots };
}, { records: [], patients: [], clinics: [], lots: [] });

const query = ref("");
const visible = computed(() => data.value.records.filter((v) =>
  matchesQuery(query.value, fullName(v), v.ohip, v.clinicName, v.manufacturer, v.lot)));

const vaccinatedPatients = computed(() => new Set(data.value.records.map((v) => v.ohip)).size);

const dialogOpen = ref(false);
const form = useForm({ ohip: "", clinicName: "", lot: "", date: "", time: "" });

function openDialog() {
  form.reset();
  dialogOpen.value = true;
}

async function create() {
  try {
    const record = await form.submit((values) => api.createVaccination(values));
    toast.success(`Vaccination recorded for ${record.ohip}.`);
    dialogOpen.value = false;
    reload();
  } catch {
    // errors are shown inside the dialog
  }
}

async function remove(v) {
  if (!window.confirm(`Delete dose ${v.doseNumber} for ${fullName(v)}?`)) return;
  try {
    await api.deleteVaccination(v.id);
    toast.success(`Dose ${v.doseNumber} for ${fullName(v)} was removed.`);
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
        <p class="eyebrow">Care</p>
        <h1>Vaccinations</h1>
        <p class="subtitle">
          {{ plural(data.records.length, "dose") }} administered · {{ plural(vaccinatedPatients, "patient") }} vaccinated
        </p>
      </div>
      <div class="header-actions">
        <button class="btn btn-primary" type="button" @click="openDialog"><AppIcon name="syringe" />Record vaccination</button>
      </div>
    </header>

    <LoadState :loading="loading" :error="error" @retry="reload">
      <section class="card">
        <div class="card-header">
          <div>
            <h2>Vaccination records</h2>
            <p>Newest first</p>
          </div>
          <SearchBox v-model="query" placeholder="Search vaccinations" />
        </div>
        <div class="table-wrap">
          <table class="table" id="vaccinations-table">
            <thead>
              <tr>
                <th>Patient</th>
                <th>Date</th>
                <th>Clinic</th>
                <th>Vaccine</th>
                <th class="actions"><span class="sr-only">Actions</span></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="v in visible" :key="v.id">
                <td>
                  <div class="person">
                    <PersonAvatar :first="v.firstName" :last="v.lastName" />
                    <div class="cell-stack">
                      <strong>{{ fullName(v) }}</strong>
                      <span class="sub mono">{{ v.ohip }}</span>
                    </div>
                  </div>
                </td>
                <td>
                  <div class="cell-stack">
                    <span>{{ prettyDate(v.date) }}</span>
                    <span class="sub">{{ v.time }}</span>
                  </div>
                </td>
                <td>{{ v.clinicName }}</td>
                <td>
                  <div class="cell-stack">
                    <span>{{ v.manufacturer }} <span class="badge info plain">Dose {{ v.doseNumber }}</span></span>
                    <span class="sub mono">Lot {{ v.lot }}</span>
                  </div>
                </td>
                <td class="actions">
                  <button class="btn btn-icon" type="button" :aria-label="`Delete dose ${v.doseNumber} for ${fullName(v)}`"
                          :title="`Delete dose ${v.doseNumber} for ${fullName(v)}`" @click="remove(v)">
                    <AppIcon name="trash" />
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-if="data.records.length === 0" class="empty">
            <strong>No vaccinations recorded</strong>Use “Record vaccination” to log the first dose.
          </div>
          <div v-else-if="visible.length === 0" class="empty">
            <strong>No matching records</strong>Try a different patient or clinic.
          </div>
        </div>
      </section>
    </LoadState>

    <ModalDialog v-model:open="dialogOpen" title="Record vaccination" description="Log a dose — first doses, second doses and boosters alike."
                 submit-label="Save vaccination" :busy="form.busy.value" :error="form.formError.value" @submit="create">
      <div class="form-grid">
        <FormField label="Patient" for="ohip" full :error="form.errors.ohip">
          <select id="ohip" v-model="form.values.ohip" :class="{ invalid: form.errors.ohip }" required>
            <option value="">Select a patient</option>
            <option v-for="p in data.patients" :key="p.ohip" :value="p.ohip">
              {{ p.lastName }}, {{ p.firstName }} · {{ p.ohip }}
            </option>
          </select>
        </FormField>
        <FormField label="Clinic" for="clinicName" full :error="form.errors.clinicName">
          <select id="clinicName" v-model="form.values.clinicName" :class="{ invalid: form.errors.clinicName }" required>
            <option value="">Select a clinic</option>
            <option v-for="c in data.clinics" :key="c.name" :value="c.name">{{ c.name }}</option>
          </select>
        </FormField>
        <FormField label="Vaccine lot" for="lot" full :error="form.errors.lot">
          <select id="lot" v-model="form.values.lot" :class="{ invalid: form.errors.lot }" required>
            <option value="">Select a lot</option>
            <option v-for="l in data.lots" :key="l.lot" :value="l.lot">{{ l.lot }} · {{ l.manufacturer }}</option>
          </select>
        </FormField>
        <FormField label="Date" for="date" :error="form.errors.date">
          <input id="date" v-model="form.values.date" type="date" :class="{ invalid: form.errors.date }" required>
        </FormField>
        <FormField label="Time" for="time" :error="form.errors.time">
          <input id="time" v-model="form.values.time" type="time" :class="{ invalid: form.errors.time }" required>
        </FormField>
      </div>
    </ModalDialog>
  </div>
</template>
