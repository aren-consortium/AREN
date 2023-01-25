<template>
  <modal-layout>

    <template v-slot:title><i class="material-icons">settings_applications</i>{{ $t('advanced_options') }}</template>

    <div id="debateOptionsModal" v-if="debate">
      <debate-options :debate="debate" @change="update"></debate-options>

      <div>
        <p>{{ $t('proposed_by') }} : {{ debate.owner.fullName() }}</p>
        <p v-if="debate.teams.length > 0">{{ $t('with') }} : {{ debate.teams.map(t => t.name).join(", ") }}</p>
      </div>
    </div>

    <template v-slot:footer>
      <button @click="close()" class="waves-effect waves-green btn-flat">{{ $t('close') }}</button>
    </template>

  </modal-layout>
</template>

<script>
module.exports = {
  mixins: [VueModal],
  data() {
    return {
      debate: false
    }
  },
  methods: {
    update(option) {
      ArenService.Debates.updateOption({
        id: this.debate.id,
        data: { option, value: this.debate[option] }
      });
    }
  },
  components: {
    'debate-options': vueLoader('components/widgets/debateOptions'),
  }
};
</script>