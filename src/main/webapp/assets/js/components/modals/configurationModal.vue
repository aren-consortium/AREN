
<template>
  <modal-layout>

    <template v-slot:title>
      {{ $t('application_settings') }}
      <toggle-action-button v-model="displayAdvanced" :off-label="$t('configuration.advanced')"></toggle-action-button>
    </template>

    <div>
      <template v-for="(prop, i) in properties">
        <template
          v-if="(getGroupName(prop) == 'base' || displayAdvanced) && ((prop.key != 'smtp.username' && prop.key != 'smtp.password' && prop.key != 'smtp.sender') || smtpNeedAuth)">
          <h2 v-if="i == 0 || getGroupName(properties[i]) != getGroupName(properties[i - 1])">
            {{ ($t(`configuration.${getGroupName(prop)}.title`)) }}
          </h2>
          <toggle-action-button v-if="isBool(prop.value)" v-model="prop.value"
            :off-label="$t(`configuration.${prop.key}`)"></toggle-action-button>
          <text-input v-else :type="prop.key.includes('password') ? 'password' : 'text'" v-model="prop.value" :label="$t(`configuration.${prop.key}`)">
          </text-input>
        </template>
      </template>
    </div>

    <template v-slot:footer>
      <button @click="close()" class="waves-effect waves-green btn-flat">{{ $t('close') }}</button>
      <button @click="save(); close()" class="waves-effect waves-green btn-flat">{{ $t('save') }}</button>
    </template>

  </modal-layout>
</template>

<script>
module.exports = {
  mixins: [VueModal],
  name: "configurationModal",
  data() {
    return {
      properties: [
        { key: "production", value: "" },
        { key: "admin-mail", value: "" },
        { key: "reverse-proxy", value: "" },
        { key: "platform", value: "" },
        { key: "rules.remove.categoryWithDocuments", value: "" },
        { key: "rules.remove.documentWithDebates", value: "" },
        { key: "rules.remove.debatesWithComments", value: "" },
        { key: "smtp.server", value: "" },
        { key: "smtp.port", value: "" },
        { key: "smtp.tls", value: "" },
        { key: "smtp.ssl", value: "" },
        { key: "smtp.auth", value: "" },
        { key: "smtp.username", value: "" },
        { key: "smtp.password", value: "" },
        { key: "smtp.sender", value: "" },
        { key: "url.cas", value: "" },
        { key: "url.scalar", value: "" },
        { key: "url.theme", value: "" },
        { key: "url.idefix", value: "" },
        { key: "authentication.jwt.secret", value: "" },
        { key: "authentication.jwt.issuer", value: "" },
        { key: "authentication.jwt.audience", value: "" },
        { key: "authentication.jwt.validFor", value: "" },
        { key: "authentication.jwt.clockSkew", value: "" },
        { key: "default.institution-id", value: "" }
      ],
      displayAdvanced: false
    }
  },
  computed: {
    smtpNeedAuth() {
      let val = this.properties.find(e => e.key == "smtp.auth").value
      return val === true || val == 'true'
    }
  },
  methods: {
    afterOpen() {
      for (const prop of this.properties) {
        prop.value = ""
      }
      ArenService.Configurations.getAll({
        onSuccess: (result) => {
          for (const resProp of result) {
            let myProp = this.properties.find(e => e.key == resProp.key)
            if (myProp) {
              myProp.value = resProp.value
            }
          }
        }
      });
    },
    save() {
      ArenService.Configurations.editAll({
        data: this.properties
      })
    },
    getGroupName(prop) {
      let index = prop.key.indexOf('.')
      if (index != -1)
        return prop.key.substring(0, index)
      else {
        return 'base'
      }
    },
    isBool(val) {
      return typeof val === 'boolean' || val === 'true' || val == 'false'
    }
  }
}
</script>