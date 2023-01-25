<template>
  <ul v-if="debate">
    <li class="collection-item" v-for="option, i of options" :key="i">
      <tooltiped v-bind:value="$t(`helper.${toSnakeCase(option)}`)">
        <label>
          <input type="checkbox" v-model="debate[option]" @change="$emit('change', option)" :disabled="disabled" />
          <span>{{ $t(toSnakeCase(option)) }}</span>
        </label>
      </tooltiped>
    </li>
  </ul>
</template>

<script>
module.exports = {
  props: ['debate'],
  data() {
    return {
      options: [
        "withHypostases",
        "reformulationCheck",
        "reformulationMandatory",
        "idefixLink",
      ]
    }
  },
  computed: {
    disabled() {
      return !this.$root.user.is('MODO');
    },
  },
  methods: {
    toSnakeCase(str) {
      return str.replace(/[A-Z]/g, letter => `_${letter.toLowerCase()}`)
    }
  }
};
</script>