<script setup>
import { computed, ref } from "vue";
import { useRoute } from "vue-router";
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
import { fullName, groupStaff, matchesQuery } from "@/lib/text";

const route = useRoute();
const toast = useToasts();
const { data, loading, error, reload } = useResource(async () => {
  const [workers, clinics] = await Promise.all([api.workers(), api.clinics()]);
  return { staff: groupStaff(workers), clinics };
}, { staff: [], clinics: [] });

const roleFilter = computed(() => (["nurse", "doctor"].includes(route.query.role) ? route.query.role : "all"));
const counts = computed(() => ({
  nurse: data.value.staff.filter((m) => m.role === "nurse").length,
  doctor: data.value.staff.filter((m) => m.role === "doctor").length
}));

const query = ref("");
const visible = computed(() => data.value.staff.filter((m) =>
  (roleFilter.value === "all" || m.role === roleFilter.value)
  && matchesQuery(query.value, fullName(m), m.id, m.credential, ...m.clinics)));

const tabs = [
  { key: "all", label: "All", to: { path: "/staff" } },
  { key: "nurse", label: "Nurses", to: { path: "/staff", query: { role: "nurse" } } },
  { key: "doctor", label: "Doctors", to: { path: "/staff", query: { role: "doctor" } } }
];

const dialogOpen = ref(false);
const form = useForm({ role: "nurse", id: "", firstName: "", lastName: "", credential: "", clinicName: "" });

function openDialog() {
  form.reset();
  dialogOpen.value = true;
}

async function create() {
  try {
    const worker = await form.submit((values) => api.createWorker(values));
    toast.success(`${fullName(worker)} was assigned to ${worker.clinicName}.`);
    dialogOpen.value = false;
    reload();
  } catch {
    // errors are shown inside the dialog
  }
}

async function remove(m) {
  if (!window.confirm(`Remove ${fullName(m)} and all of their clinic assignments?`)) return;
  try {
    await api.deleteWorker(m.role, m.id);
    toast.success(`Staff member ${m.id} was removed.`);
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
        <h1>Clinical staff</h1>
        <p class="subtitle">Nurses and doctors assigned to vaccination clinics.</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-primary" type="button" @click="openDialog"><AppIcon name="plus" />Add staff member</button>
      </div>
    </header>

    <LoadState :loading="loading" :error="error" @retry="reload">
      <section class="card">
        <div class="card-header">
          <nav class="segmented" aria-label="Filter by role">
            <RouterLink v-for="tab in tabs" :key="tab.key" :to="tab.to" :class="{ active: roleFilter === tab.key }">
              {{ tab.label }}
              <span class="count">{{ tab.key === "all" ? counts.nurse + counts.doctor : counts[tab.key] }}</span>
            </RouterLink>
          </nav>
          <SearchBox v-model="query" placeholder="Search staff" />
        </div>
        <div class="table-wrap">
          <table class="table" id="staff-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Role</th>
                <th>Credential</th>
                <th>Clinics</th>
                <th class="actions"><span class="sr-only">Actions</span></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="m in visible" :key="m.role + '/' + m.id">
                <td>
                  <div class="person">
                    <PersonAvatar :first="m.firstName" :last="m.lastName" />
                    <div class="cell-stack">
                      <strong>{{ fullName(m) }}</strong>
                      <span class="sub mono">{{ m.id }}</span>
                    </div>
                  </div>
                </td>
                <td>
                  <span class="badge" :class="m.role === 'nurse' ? 'info' : 'violet'">{{ m.role === "nurse" ? "Nurse" : "Doctor" }}</span>
                </td>
                <td><span class="chip">{{ m.credential ?? "—" }}</span></td>
                <td>
                  <div v-if="m.clinics.length" class="cell-stack">
                    <span v-for="clinic in m.clinics" :key="clinic">{{ clinic }}</span>
                  </div>
                  <span v-else class="badge warning">Unassigned</span>
                </td>
                <td class="actions">
                  <button class="btn btn-icon" type="button" :aria-label="`Delete ${fullName(m)}`"
                          :title="`Delete ${fullName(m)}`" @click="remove(m)">
                    <AppIcon name="trash" />
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-if="data.staff.length === 0" class="empty"><strong>No staff found</strong>Add a nurse or doctor.</div>
          <div v-else-if="visible.length === 0" class="empty"><strong>No matching staff</strong></div>
        </div>
      </section>
    </LoadState>

    <ModalDialog v-model:open="dialogOpen" title="Add staff member" description="Create a nurse or doctor and assign them to a clinic."
                 submit-label="Save staff member" :busy="form.busy.value" :error="form.formError.value" @submit="create">
      <div class="form-grid">
        <FormField label="Role" for="role" :error="form.errors.role">
          <select id="role" v-model="form.values.role" :class="{ invalid: form.errors.role }" required>
            <option value="nurse">Nurse</option>
            <option value="doctor">Doctor</option>
          </select>
        </FormField>
        <FormField label="Staff ID" for="id" :error="form.errors.id">
          <input id="id" v-model="form.values.id" :class="{ invalid: form.errors.id }" placeholder="AB123"
                 pattern="[A-Za-z0-9]{1,5}" maxlength="5" autocomplete="off" required>
        </FormField>
        <FormField label="First name" for="firstName" :error="form.errors.firstName">
          <input id="firstName" v-model="form.values.firstName" :class="{ invalid: form.errors.firstName }"
                 pattern="[A-Za-z]{1,15}" maxlength="15" required>
        </FormField>
        <FormField label="Last name" for="lastName" :error="form.errors.lastName">
          <input id="lastName" v-model="form.values.lastName" :class="{ invalid: form.errors.lastName }"
                 pattern="[A-Za-z]{1,15}" maxlength="15" required>
        </FormField>
        <FormField label="Credential" for="credential" :error="form.errors.credential">
          <input id="credential" v-model="form.values.credential" :class="{ invalid: form.errors.credential }"
                 maxlength="20" list="credential-options" placeholder="RN, MD…" required>
          <datalist id="credential-options">
            <option value="RN" /><option value="BSN" /><option value="CCRN" />
            <option value="MD" /><option value="DO" /><option value="PA" />
          </datalist>
        </FormField>
        <FormField label="Clinic" for="clinicName" :error="form.errors.clinicName">
          <select id="clinicName" v-model="form.values.clinicName" :class="{ invalid: form.errors.clinicName }" required>
            <option value="">Select a clinic</option>
            <option v-for="c in data.clinics" :key="c.name" :value="c.name">{{ c.name }}</option>
          </select>
        </FormField>
      </div>
    </ModalDialog>
  </div>
</template>
