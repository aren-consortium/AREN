<template>
    <modal-layout>

        <template v-slot:title>{{ $t('password_forgotten') }}</template>

        <span v-html="$t('helper.password_forgotten')"></span>

        <text-input type="text"
                    v-model="username"
                    v-bind:label="$t('username')">
        </text-input>

        <template v-slot:footer>
            <button @click="close()" class="waves-effect waves-green btn-flat">{{ $t('cancel') }}</button>
            <button @click="resetPasswd(); close();" class="waves-effect waves-green btn">{{ $t('validate') }}</button>
        </template>

    </modal-layout>
</template>

<script>
    module.exports = {
        mixins: [VueModal],
        data( ) {
            return {
                username: ""
            };
        },
        methods: {
            resetPasswd() {
                ArenService.Users.resetPasswd({
                    data: this.username,
                    query: {returnUrl: "?resetPassword=true&token={token}"},
                    onSuccess: () => this.$confirm({
                            title: this.$t('password_forgotten'),
                            message: this.$t('helper.password_forgotten_email'),
                            isInfo: true
                        })
                });
            }
        }
    };
</script>