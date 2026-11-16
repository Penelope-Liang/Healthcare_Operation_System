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
import { ageInYears, prettyDate } from "@/lib/dates";
import { fullName, matchesQuery } from "@/lib/text";

const toast = useToasts();
const { data: patients, loading, error, reload } = useResource(api.patientOverview, []);

const query = ref("");
const visible = computed(() => patients.value.filter((p) =>
  matchesQuery(query.value, fullName(p), p.ohip, p.clinicName, p.manufacturer, p.lot)));
const vaccinatedCount = computed(() => patients.value.filter((p) => p.vaccinationDate).length);

const dialogOpen = ref(false);
const form = useForm({ ohip: "", spouseOhip: "", firstName: "", lastName: "", dob: "" });

function openDialog() {
  form.reset();
  dialogOpen.value = true;
}

async function create() {
  try {
    const patient = await form.submit((values) => api.createPatient(values));
    toast.success(`${fullName(patient)} was added.`);
    dialogOpen.value = false;
    reload();
  } catch {
    // errors are shown inside the dialog
  }
}

async function remove(p) {
  if (!window.confirm(`Remove ${fullName(p)} from the registry?`)) return;
  try {
    await api.deletePatient(p.ohip);
    toast.success(`Patient ${p.ohip} was removed.`);
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
        <h1>Patients</h1>
        <p class="subtitle">{{ patients.length }} registered · {{ vaccinatedCount }} vaccinated</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-primary" type="button" @click="openDialog"><AppIcon name="plus" />Add patient</button>
      </div>
    </header>

    <LoadState :loading="loading" :error="error" @retry="reload">
      <section class="card">
        <div class="card-header">
          <div>
            <h2>Patient directory</h2>
            <p>Search by name, OHIP, clinic or manufacturer</p>
          </div>
          <SearchBox v-model="query" placeholder="Search patients" />
        </div>
        <div class="table-wrap">
          <table class="table" id="patients-table">
            <thead>
              <tr>
                <th>Patient</th>
                <th>Date of birth</th>
                <th>Status</th>
                <th>Vaccine</th>
                <th>Clinic</th>
                <th>Appointment</th>
                <th class="actions"><span class="sr-only">Actions</span></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="p in visible" :key="p.ohip">
                <td>
                  <div class="person">
                    <PersonAvatar :first="p.firstName" :last="p.lastName" />
                    <div class="cell-stack">
                      <strong>{{ fullName(p) }}</strong>
                      <span class="sub mono">{{ p.ohip }}</span>
                    </div>
                  </div>
                </td>
                <td>
                  <div class="cell-stack">
                    <span>{{ prettyDate(p.dob) }}</span>
                    <span v-if="ageInYears(p.dob) !== null" class="sub">{{ ageInYears(p.dob) }} years</span>
                  </div>
                </td>
                <td>
                  <span v-if="p.vaccinationDate" class="badge success">Vaccinated</span>
                  <span v-else class="badge warning">Not vaccinated</span>
                </td>
                <td>
                  <div v-if="p.vaccinationDate" class="cell-stack">
                    <span>{{ p.manufacturer }}</span>
                    <span class="sub mono">{{ p.lot }}</span>
                  </div>
                  <span v-else class="cell-muted">—</span>
                </td>
                <td :class="{ 'cell-muted': !p.clinicName }">{{ p.clinicName ?? "—" }}</td>
                <td>
                  <div v-if="p.vaccinationDate" class="cell-stack">
                    <span>{{ prettyDate(p.vaccinationDate) }}</span>
                    <span class="sub">{{ p.vaccinationTime }}</span>
                  </div>
                  <RouterLink v-else class="link-arrow" to="/vaccinations">Schedule <AppIcon name="arrow-right" /></RouterLink>
                </td>
                <td class="actions">
                  <button class="btn btn-icon" type="button" :aria-label="`Delete ${fullName(p)}`"
                          :title="`Delete ${fullName(p)}`" @click="remove(p)">
                    <AppIcon name="trash" />
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-if="patients.length === 0" class="empty"><strong>No patients yet</strong>Add the first patient to get started.</div>
          <div v-else-if="visible.length === 0" class="empty">
            <strong>No matching patients</strong>Try a different name or OHIP number.
          </div>
        </div>
      </section>
    </LoadState>

    <ModalDialog v-model:open="dialogOpen" title="Add patient"
                 description="Register a patient in the provincial vaccination registry."
                 submit-label="Create patient" :busy="form.busy.value" :error="form.formError.value" @submit="create">
      <div class="form-grid">
        <FormField label="OHIP number" for="ohip" :error="form.errors.ohip">
          <input id="ohip" v-model="form.values.ohip" :class="{ invalid: form.errors.ohip }" placeholder="0000-000-000"
                 pattern="[0-9]{4}-[0-9]{3}-[0-9]{3}" required>
        </FormField>
        <FormField label="Spouse OHIP" for="spouseOhip" optional :error="form.errors.spouseOhip">
          <input id="spouseOhip" v-model="form.values.spouseOhip" :class="{ invalid: form.errors.spouseOhip }"
                 placeholder="0000-000-000" pattern="[0-9]{4}-[0-9]{3}-[0-9]{3}">
        </FormField>
        <FormField label="First name" for="firstName" :error="form.errors.firstName">
          <input id="firstName" v-model="form.values.firstName" :class="{ invalid: form.errors.firstName }"
                 pattern="[A-Za-z]{1,30}" maxlength="30" autocomplete="off" required>
        </FormField>
        <FormField label="Last name" for="lastName" :error="form.errors.lastName">
          <input id="lastName" v-model="form.values.lastName" :class="{ invalid: form.errors.lastName }"
                 pattern="[A-Za-z]{1,30}" maxlength="30" autocomplete="off" required>
        </FormField>
        <FormField label="Date of birth" for="dob" full :error="form.errors.dob">
          <input id="dob" v-model="form.values.dob" type="date" :class="{ invalid: form.errors.dob }" required>
        </FormField>
      </div>
    </ModalDialog>
  </div>
</template>
