<template>
    <modal-layout>

        <template v-slot:title>{{ $t('connection') }}</template>

        <text-input v-if="!$route.query.resetPassword"
                    type="password"
                    v-model="oldPassword"
                    v-bind:label="$t('old_password')">
        </text-input>

        <text-input type="password"
                    v-model="newPassword"
                    v-bind:label="$t('new_password')"
                    v-bind:validate="samePasswords">
        </text-input>

        <text-input type="password"
                    v-model="passwordCheck"
                    v-bind:label="$t('password_check')"
                    v-bind:validate="samePasswords">
        </text-input>

        <template v-slot:footer>
            <button @click="close()" class="waves-effect waves-green btn-flat">{{ $t('cancel') }}</button>
            <button @click="changePasswd(); close()" class="waves-effect waves-green btn-flat">{{ $t('validate') }}</button>
        </template>

    </modal-layout>
</template>

<script>
    module.exports = {
        mixins: [VueModal],
        data( ) {
            return {
                passwd: false,
                oldPassword: "",
                newPassword: "",
                passwordCheck: ""
            };
        },
        mounted() {
            this.$nextTick(() => {
                if (this.$route.query.resetPassword && this.$route.query.token) {
                    this.open();
                }
            });
        },
        computed: {
            samePasswords( ) {
                return this.newPassword === this.passwordCheck;
            }
        },
        methods: {
            changePasswd() {
                ArenService.Users.passwd({
                    query: this.$route.query.token ? {token: this.$route.query.token} : {},
                    data: {password: this.oldPassword, newPassword: this.newPassword},
                    onSuccess: () => {
                        this.$confirm({
                            title: this.$t("helper.password_changed"),
                            message: this.$t('helper.password_changed_loged_out'),
                            isInfo: true,
                            callback: () => {
                                if (!this.$route.query.resetPassword && !this.$route.query.token) {
                                    this.$router.replace({query: null});
                                    this.$root.logout();
                                } else {
                                    this.$router.replace({query: null});
                                }
                            }
                        });
                    },
                    onError: error => {
                        this.$confirm({
                            title: this.$t('error.' + error.title),
                            message: this.$t('error.' + error.message, error.details),
                            isInfo: true,
                            callback: () => {
                                this.$router.replace({query: null});
                                this.$root.logout();
                            }
                        });
                    }
                });
            },
            afterClose() {
                this.oldPassword = "";
                this.newPassword = "";
                this.passwordCheck = "";
                this.passwd = false;
            }
        }
    };
</script>