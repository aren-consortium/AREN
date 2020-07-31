<template>
    <div class="input-field">

        <i v-if="icon" class="material-icons prefix">{{ icon }}</i>

        <template v-if="type === 'textarea'">
            <div ref="input"
                 v-bind:contentEditable="!disabled"
                 class="materialize-textarea"
                 v-bind:id="id"
                 v-bind:placeholder="placeholder"
                 @keyup="$emit('keyup', $event)"
                 @keydown="$emit('keydown', $event)"
                 @keypress="$emit('keypress', $event)"
                 @input="selfEdit = true; $emit('input', $event.target.innerText);">
            </div>
        </template>

        <template v-else>
            <input ref="input"
                   v-bind:type="type"
                   v-bind:placeholder="placeholder"
                   v-bind:disabled="disabled"
                   v-bind:value="value"
                   v-bind:id="id"
                   v-bind:class="{valid: isValid, invalid: isInvalid}"
                   @keyup="$emit('keyup', $event)"
                   @keydown="$emit('keydown', $event)"
                   @keypress="$emit('keypress', $event)"
                   @input="$emit('input', $event.target.value)">
        </template>

        <label v-if="label" v-bind:class="{active: active}" v-bind:for="id">{{ label }}</label>
        <span v-if="(helper+errorHelper+successHelper).length > 0" v-bind:class="{'helper-text': true, flash: flashHelper}" v-bind:data-error="errorHelper" v-bind:data-success="successHelper">{{ helper }}</span>

        <slot></slot>
    </div>
</template>

<script>
    module.exports = {
        props: ['value', 'label', 'placeholder', 'icon', 'type', 'validate', 'errorHelper', 'successHelper', 'disabled', 'helper', 'flashHelper', 'valid', 'invalid'],
        data() {
            if (typeof Vue.prototype.$textInputId === 'undefined') {
                Vue.prototype.$textInputId = 0;
            }
            Vue.prototype.$textInputId++;
            return {
                id: "text-input-" + this.$textInputId,
                selfEdit: false
            }
        },
        watch: {
            value(val) {
                if (this.type === "textarea" && !this.selfEdit) {
                    this.$refs.input.innerText = val && val.length > 0 ? val : this.placeholder ? this.placeholder : "";
                }
                this.selfEdit = false;
            }
        },
        mounted() {
            /*if (this.type === "textarea") {
             this.$refs.input.innerText = (this.value && this.value.length > 0) ? this.value : (this.placeholder ? this.placeholder : "");
             }*/
        },
        computed: {
            active() {
                return (this.value && this.value.length > 0) || (this.placeholder && this.placeholder.length > 0);
            },
            isValid() {
                return this.valid !== undefined ? this.valid : (this.value && this.value.length > 0 && this.validate !== undefined && this.validate);
            },
            isInvalid() {
                return this.invalid !== undefined ? this.invalid : (this.value && this.value.length > 0 && this.validate !== undefined && !this.validate);
            }
        }
    };
</script>