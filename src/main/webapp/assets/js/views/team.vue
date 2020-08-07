<template>
    <base-layout id="team" v-if="team">
        <template v-slot:title>
            <h1> <span v-if="team.institution.id !== 0">{{ team.institution.academy }} - {{ team.institution.type }} {{ team.institution.name }} - </span>{{ team.name }}</h1>
        </template>

        <template v-slot:right>
            <action-button
                v-bind:tooltip-disabled="$t('helper.cannot_add_institution_user')"
                v-bind:disabled="team.institution.id !== 0"
                @press="$refs.addUser.open()"
                v-bind:label="$t('add_user')">
            </action-button>
        </template>

        <users-grid
            v-bind:people="team.users"
            v-bind:editable="true">
            <template v-slot:side.actions="{ user }">
                <action-button
                    v-bind:tooltip="$t('helper.remove_user_from_team')"
                    v-bind:tooltip-disabled="$t('helper.cannot_remove_institution_user')"
                    v-bind:disabled="team.institution.id !== 0"
                    @press="removeUser( user )"
                    icon="delete">
                </action-button>
            </template>
        </users-grid>

        <template v-slot:addons>
            <user-in-team
                ref="addUser"
                v-bind:team="team"
                @add-user="addUser($event)">
            </user-in-team>
        </template>
    </base-layout>
</template>

<script>
    module.exports = {
        data() {
            return {
                team: false
            };
        },
        created() {
            if (!this.$root.user.is('MODO')) {
                this.$router.push("/404");
                return;
            }
            this.fetchData( );
        },
        methods: {
            fetchData( ) {
                ArenService.Teams.get({
                    id: this.$route.params.id,
                    onSuccess: (team) => this.team = team
                });
            },
            removeUser(user) {
                ArenService.Teams.removeUser({
                    id: this.team.id,
                    data: user
                });
            },
            addUser(user) {
                ArenService.Teams.addUser({
                    id: this.team.id,
                    data: user
                });
            }
        },
        components: {
            'user-in-team': vueLoader('components/modals/userInTeamModal'),
            'users-grid': vueLoader('components/grids/usersGrid')
        }
    };
</script>