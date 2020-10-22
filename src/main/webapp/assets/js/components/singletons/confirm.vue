<template>
    <modal-layout id="confirmDialog" @mounted="init()">

        <template v-slot:title>
            {{ title }}
        </template>

        <div v-html="message"></div>

        <template v-slot:footer>
            <template v-if="!notCloseable">
                <template v-if="isInfo">
                    <button @click="close()" class="waves-effect waves-green btn-flat">{{ $t('close') }}</button>
                </template>
                <template v-else>
                    <button @click="close();" class="waves-effect waves-green btn-flat">{{ cancelLabel ? $t(cancelLabel) : $t('cancel') }}</button>
                    <button @click="close(true);" class="waves-effect waves-green btn-flat">{{ validateLabel ? $t(validateLabel) : $t('validate') }}</button>
                </template>
            </template>
        </template>

    </modal-layout>
</template>

<script>
    module.exports = {
        mixins: [VueModal],
        data() {
            return {
                title: "",
                message: "",
                validateLabel: "",
                cancelLabel: "",
                isInfo: "",
                notCloseable: ""
            }
        },
        methods: {
            init() {
                if (!Vue.prototype.$confirm) {
                    Vue.prototype.$confirm = ({title, message, validateLabel, cancelLabel, callback, isInfo, notCloseable} = {}) => {
                        this.title = title;
                        this.message = message;
                        this.validateLabel = validateLabel;
                        this.cancelLabel = cancelLabel;
                        this.isInfo = isInfo;
                        this.notCloseable = notCloseable;
                        this.open(callback);
                    }
                    Vue.prototype.$closeConfirm = this.close;
                } else {
                    throw "This is a singleton component you cannot have many of if in the same app";
                }
            }
        },
    };
</script>