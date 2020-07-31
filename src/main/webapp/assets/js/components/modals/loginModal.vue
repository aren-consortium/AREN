<template>
    <modal-layout>

        <template v-slot:title>{{ $t('connection') }}</template>

        <text-input type="text"
                    v-model="credentials.username"
                    v-bind:label="$t('username')"
                    v-bind:invalid="error"
                    v-bind:error-helper="$t('error.bad_credentials')">
        </text-input>

        <text-input type="password"
                    @keyup.enter="login()"
                    v-model="credentials.password"
                    v-bind:label="$t('password')"
                    v-bind:invalid="error"
                    v-bind:error-helper="$t('error.bad_credentials')">
        </text-input>
        <label>
            <input type="checkbox" name="rememberMe" v-model="credentials.rememberMe"/>
            <span>{{ $t('remember_me') }}</span>
        </label>
        <label @click="close(); $emit('forgot-password');">
            <a class="clickable right">{{ $t("password_forgotten") }}</a>
        </label>

        <template v-slot:footer>
            <tooltiped class="left" v-bind:value="$t('helper.cas_login')">
                <a href="caslogin" class="waves-effect waves-light btn-flat">{{ $t('cas_connection') }}</a>
            </tooltiped>
            <button @click="close()" class="waves-effect waves-green btn-flat">{{ $t('cancel') }}</button>
            <button @click="login()" class="waves-effect waves-green btn">{{ $t('connect') }}</button>
        </template>

    </modal-layout>
</template>

<script>
    module.exports = {
        mixins: [VueModal],
        data( ) {
            return {
                error: false,
                credentials: {username: "", password: "", rememberMe: false}
            };
        },
        methods: {
            login() {
                ArenService.Users.login({
                    data: this.credentials,
                    onSuccess: () => this.close(true),
                    onError: error => this.error = true
                });
            }
        }
    };
</script>