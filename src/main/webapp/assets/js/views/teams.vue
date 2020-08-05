<template>
    <base-layout id="teams">
        <template v-slot:title>
            <h1>{{manageUsers ? $t("menu.users") : $t('menu.teams')}} - </h1>
            <institution-select ref="institutionSelect" @institution-change="institutionChanged($event)"></institution-select>
        </template>

        <template v-slot:right v-if="institution">
            <action-button v-if="$root.user.is('MODO')"
                           class="primary-color"
                           @press="manageUsers = !manageUsers; scrollTop()"
                           v-bind:label="manageUsers ? $t('team_managment') : $t('user_managment')">
            </action-button>
            <action-button
                v-bind:tooltip-disabled="manageUsers ? $t('helper.cannot_create_institution_user') : $t('helper.cannot_create_institution_team')"
                v-bind:disabled="institution.id !== 0"
                @press="manageUsers ? createUser() : createOrUpdateTeam()"
                v-bind:label="manageUsers ? $t('new_user') : $t('new_team')">
            </action-button>
        </template>

        <div class="center">
            <search-box v-model="search"></search-box>
        </div>

        <users-grid v-if="institution && manageUsers"
                    ref="userGrid"
                    v-bind:editable="true"
                    v-bind:search="search"
                    v-bind:people="institution.users">
            <template v-slot:side.actions="{ user }">
                <action-button
                    v-bind:tooltip="$t('helper.remove_user')"
                    v-bind:tooltip-disabled="$t('helper.cannot_remove_institution_user')"
                    v-bind:disabled="institution.id !== 0"
                    @press="deleteUser(user)"
                    icon="delete">
                </action-button>
                <action-button
                    v-if="$root.user.is('ADMIN')"
                    v-bind:tooltip="$t('helper.remove_permanent_user')"
                    v-bind:tooltip-disabled="$t('helper.cannot_remove_institution_user')"
                    v-bind:disabled="institution.id !== 0"
                    @press="deleteUser(user, true)"
                    icon="delete_forever">
                </action-button>
            </template>
        </users-grid>

        <teams-grid v-else-if="institution"
                    ref="teamGrid"
                    v-bind:search="search"
                    v-bind:teams="institution.teams">
            <template v-slot:side.actions="{ team }">
                <router-link v-bind:to="'/teams/' + team.id" class="waves-effect waves-light btn">
                    {{ $t('display_team') }}
                </router-link>
                <action-button
                    v-bind:tooltip="$t('helper.edit_team')"
                    v-bind:tooltip-disabled="$t('helper.cannot_edit_institution_team')"
                    v-bind:disabled="team.institution.id !== 0"
                    @press="createOrUpdateTeam( team )"
                    icon="edit">
                </action-button>
                <action-button
                    v-bind:tooltip="$t('helper.remove_team')"
                    v-bind:tooltip-disabled="team.institution.id === 0 ? $t('helper.cannot_remove_team') : $t('helper.cannot_remove_institution_team')"
                    v-bind:disabled="team.institution.id !== 0 || team.debatesCount > 0"
                    @press="deleteTeam( team )"
                    icon="delete">
                </action-button>
            </template>
        </teams-grid>

        <template v-slot:addons>
            <team-modal ref="teamModal">
            </team-modal>
            <create-user-modal ref="createUserModal">
            </create-user-modal>
        </template>
    </base-layout>
</template>

<script>
    module.exports = {
        data: function ( ) {
            return {
                institution: false,
                manageUsers: false,
                search: ""
            };
        },
        methods: {
            institutionChanged(institution) {
                this.institution = institution;
            },
            createOrUpdateTeam(team = {}) {
                this.$refs.teamModal.team = new Team(team);
                this.$refs.teamModal.open((updatedTeam) => {
                    if (updatedTeam) {
                        ArenService.Teams.createOrUpdate({
                            data: updatedTeam,
                            onSuccess: (returnedTeam) => {
                                this.$nextTick(function ( ) {
                                    this.$refs.teamGrid.selectRow('row' + returnedTeam.id);
                                });
                            }
                        });
                    }
                });
            },
            deleteTeam(team) {
                this.$confirm({
                    title: this.$t("helper.delete_team", {teanName: team.name}),
                    message: this.$t('helper.do_continue'),
                    callback: (returnValue) => {
                        if (returnValue) {
                            ArenService.Teams.remove({
                                data: team
                            });
                        }
                    }
                });
            },
            createUser( ) {
                this.$refs.createUserModal.open((newUser) => {
                    if (newUser) {
                        ArenService.Users.create({
                            data: newUser,
                            onSuccess: (returnedUser) => {
                                this.$nextTick(function ( ) {
                                    this.$refs.userGrid.selectRow('row' + returnedUser.id);
                                });
                            }
                        });
                    }
                });
            },
            deleteUser(user, permanent = false) {
                this.$confirm({
                    title: this.$t(permanent ? "helper.delete_permanent_user" : "helper.delete_user", {userName: user.fullName()}),
                    message: this.$t(permanent ? 'helper.delete_permanent_user_warning' : 'helper.do_continue'),
                    callback: (returnValue) => {
                        if (returnValue) {
                            if (permanent) {
                                ArenService.Users.permanentRemove({
                                    data: user
                                });
                            } else {
                                ArenService.Users.remove({
                                    data: user
                                });
                            }
                        }
                    }
                });
            },
            scrollTop() {
                document.documentElement.scrollTop = 0;
            }
        },
        components: {
            'team-modal': vueLoader('components/modals/teamModal'),
            'teams-grid': vueLoader('components/grids/teamsGrid'),
            'users-grid': vueLoader('components/grids/usersGrid'),
            'create-user-modal': vueLoader('components/modals/createUserModal')
        }
    };
</script>