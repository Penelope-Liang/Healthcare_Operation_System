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
import { prettyDate } from "@/lib/dates";
import { matchesQuery } from "@/lib/text";

const toast = useToasts();
const { data: clinics, loading, error, reload } = useResource(api.clinicOverview, []);

const query = ref("");
const visible = computed(() => clinics.value.filter(({ clinic: c, lots }) =>
  matchesQuery(query.value, c.name, c.city, c.street, c.pc, ...lots)));

const dialogOpen = ref(false);
const form = useForm({ name: "", street: "", city: "", prov: "", pc: "", operatingDate: "" });

function openDialog() {
  form.reset();
  dialogOpen.value = true;
}

async function create() {
  try {
    const clinic = await form.submit((values) => api.createClinic(values));
    toast.success(`${clinic.name} was added.`);
    dialogOpen.value = false;
    reload();
  } catch {
    // errors are shown inside the dialog
  }
}

async function remove(c) {
  if (!window.confirm(`Remove ${c.name}? Staff assignments and shipments for this clinic are removed too.`)) return;
  try {
    await api.deleteClinic(c.name);
    toast.success(`${c.name} was removed.`);
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
        <h1>Clinics</h1>
        <p class="subtitle">{{ clinics.length }} vaccination sites</p>
      </div>
      <div class="header-actions">
        <SearchBox v-model="query" placeholder="Search clinics" />
        <button class="btn btn-primary" type="button" @click="openDialog"><AppIcon name="plus" />Add clinic</button>
      </div>
    </header>

    <LoadState :loading="loading" :error="error" @retry="reload">
      <section class="clinic-grid" id="clinic-grid" aria-label="Clinic directory">
        <article v-for="o in visible" :key="o.clinic.name" class="card clinic-card">
          <div class="card-body">
            <div class="clinic-head">
              <div class="cell-stack">
                <h3>{{ o.clinic.name }}</h3>
                <span class="sub">{{ o.clinic.city }}, {{ o.clinic.prov }}</span>
              </div>
              <button class="btn btn-icon" type="button" :aria-label="`Delete ${o.clinic.name}`"
                      :title="`Delete ${o.clinic.name}`" @click="remove(o.clinic)">
                <AppIcon name="trash" />
              </button>
            </div>
            <p class="meta-line"><AppIcon name="pin" /><span>{{ o.clinic.street }}, {{ o.clinic.pc }}</span></p>
            <p class="meta-line"><AppIcon name="calendar" /><span>Operating {{ prettyDate(o.clinic.operatingDate) }}</span></p>
            <div class="chips">
              <span v-for="lot in o.lots" :key="lot" class="chip">{{ lot }}</span>
              <span v-if="o.lots.length === 0" class="badge warning">No vaccine supply</span>
            </div>
          </div>
          <div class="clinic-stats">
            <div><strong>{{ o.lots.length }}</strong><span>Lots</span></div>
            <div><strong>{{ o.nurses + o.doctors }}</strong><span>Staff</span></div>
            <div><strong>{{ o.vaccinations }}</strong><span>Doses given</span></div>
          </div>
        </article>
      </section>
      <div v-if="clinics.length === 0" class="card empty"><strong>No clinics yet</strong>Add your first vaccination site.</div>
      <div v-else-if="visible.length === 0" class="card empty"><strong>No matching clinics</strong>Try another name or city.</div>
    </LoadState>

    <ModalDialog v-model:open="dialogOpen" title="Add clinic" description="Open a new vaccination site."
                 submit-label="Create clinic" :busy="form.busy.value" :error="form.formError.value" @submit="create">
      <div class="form-grid">
        <FormField label="Clinic name" for="name" full :error="form.errors.name">
          <input id="name" v-model="form.values.name" :class="{ invalid: form.errors.name }" maxlength="30" required>
        </FormField>
        <FormField label="Street" for="street" full :error="form.errors.street">
          <input id="street" v-model="form.values.street" :class="{ invalid: form.errors.street }" maxlength="30" required>
        </FormField>
        <FormField label="City" for="city" :error="form.errors.city">
          <input id="city" v-model="form.values.city" :class="{ invalid: form.errors.city }" maxlength="30" required>
        </FormField>
        <FormField label="Province" for="prov" :error="form.errors.prov">
          <input id="prov" v-model="form.values.prov" :class="{ invalid: form.errors.prov }" maxlength="30" placeholder="ON" required>
        </FormField>
        <FormField label="Postal code" for="pc" :error="form.errors.pc">
          <input id="pc" v-model="form.values.pc" :class="{ invalid: form.errors.pc }" maxlength="30" placeholder="K7L 1A1" required>
        </FormField>
        <FormField label="Operating date" for="operatingDate" :error="form.errors.operatingDate">
          <input id="operatingDate" v-model="form.values.operatingDate" type="date" :class="{ invalid: form.errors.operatingDate }" required>
        </FormField>
      </div>
    </ModalDialog>
  </div>
</template>
