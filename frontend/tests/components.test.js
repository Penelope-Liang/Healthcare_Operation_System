import { describe, expect, test } from "vitest";
import { mount } from "@vue/test-utils";
import PersonAvatar from "@/components/PersonAvatar.vue";
import SearchBox from "@/components/SearchBox.vue";

describe("PersonAvatar", () => {
  test("shows initials with a colour derived from the name", () => {
    const wrapper = mount(PersonAvatar, { props: { first: "Amy", last: "White" } });
    expect(wrapper.text()).toBe("AW");
    expect(wrapper.classes()).toContain("tone-2");
  });
});

describe("SearchBox", () => {
  test("is an accessible search input bound with v-model", async () => {
    const wrapper = mount(SearchBox, {
      props: { placeholder: "Search patients", modelValue: "", "onUpdate:modelValue": (v) => wrapper.setProps({ modelValue: v }) }
    });
    const input = wrapper.get("input[type=search]");
    expect(input.attributes("aria-label")).toBe("Search patients");

    await input.setValue("amy");
    expect(wrapper.props("modelValue")).toBe("amy");
  });
});
