<template>
    <modal-layout>

        <template v-slot:title>{{ $t('user_creation') }}</template>

        <div v-if="!loading" class="row">
            <text-input type="text" class="col s6"
                        v-model="user.username"
                        v-bind:label="$t('username')"
                        v-bind:helper="$t('helper.used_for_connection')"
                        v-bind:error-helper="$t('helper.username_exists')"
                        v-bind:validate="!exists['username']"
                        @input="testUserExistance('username')">
            </text-input>
            <text-input type="text" class="col s6"
                        v-model="user.email"
                        v-bind:label="$t('email')"
                        v-bind:validate="validEmail && !exists['email']"
                        v-bind:error-helper="!validEmail ? $t('helper.not_an_email') : $t('helper.email_exists')"
                        @input="testUserExistance('email')">
            </text-input>

            <text-input type="text" class="col s6"
                        v-model="user.firstName"
                        v-bind:label="$t('first_name')">
            </text-input>
            <text-input type="text" class="col s6"
                        v-model="user.lastName"
                        v-bind:label="$t('last_name')">
            </text-input>

            <text-input type="password" class="col s6"
                        v-model="user.password"
                        v-bind:label="$t('password')"
                        v-bind:error-helper="$t('helper.different_passwords')">
            </text-input>
            <text-input type="password" class="col s6"
                        v-model="passwordCheck"
                        v-bind:label="$t('password_check')"
                        v-bind:validate="samePasswords"
                        v-bind:error-helper="$t('helper.different_passwords')">
            </text-input>

            <div v-if="$root.user && $root.user.is('MODO')" class="col s6">
                <label>$t('authority')</label>
                <select class="browser-default" v-model="user.authority">
                    <option value="USER">{{ $t('USER') }}</option>
                    <option v-if="$root.user.is('MODO')" value="MODO">{{ $t('MODO') }}</option>
                    <option v-if="$root.user.is('ADMIN')" value="ADMIN">{{ $t('ADMIN') }}</option>
                </select>
            </div>
        </div>
        <div v-else class="row">
            {{ $t("helper.creating_user")}}
        </div>

        <template v-slot:footer>
            <button @click="close()" v-bind:disabled="loading" class="waves-effect waves-green btn-flat">{{ $t('cancel') }}</button>
            <button @click="signUp();" v-bind:disabled="loading || !samePasswords || exists.username || exists.email || !validEmail" class="waves-effect waves-green btn-flat">{{ $t('validate') }}</button>
        </template>

    </modal-layout>
</template>

<script>
    module.exports = {
        mixins: [VueModal],
        data: function ( ) {
            return {
                user: new User( ),
                passwordCheck: "",
                exists: {username: false, email: false},
                checkExistsTimeout: -1,
                loading: false
            };
        },
        computed: {
            samePasswords( ) {
                return this.user.password === this.passwordCheck;
            },
            validEmail() {
                return /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(this.user.email);
            }
        },
        methods: {
            afterOpen( ) {
                this.loading = false;
                this.user = false;
                Vue.nextTick(( ) => {
                    this.user = new User( );
                    this.user.authority = Authority.USER;
                    this.passwordCheck = "";
                });
            },
            testUserExistance(field) {
                clearTimeout(this.checkExistsTimeout);
                this.checkExistsTimeout = setTimeout(() => {
                    ArenService.Users.exists({
                        data: this.user[field],
                        onSuccess: (doExists) => {
                            this.exists[field] = doExists;
                        }
                    });
                }, 500);
            },
            signUp() {
                this.loading = true;
                ArenService.Users.create({
                    data: this.user,
                    query: {returnUrl: "?activation=true&token={token}"},
                    onSuccess: () => {
                        this.close();
                        this.$confirm({
                            title: this.$t('user_created'),
                            message: this.$t('helper.user_created_email'),
                            isInfo: true
                        });
                    }
                });
            }
        }
    };
</script>