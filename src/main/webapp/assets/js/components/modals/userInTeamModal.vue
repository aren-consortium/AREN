<template>
    <modal-layout>

        <template v-slot:title>Ajouter au groupe - {{ team.name }}</template>

        <div id="sort-options">
            <institution-select ref="institutionSelect" @institution-change="institutionChanged($event)"></institution-select>
            <search-box v-model="search"></search-box>
        </div>

        <users-grid
            v-bind:search="search"
            v-bind:people="peopleNotInTeam"
            @remove="removeUser($event)">
            <template v-slot:side.actions="{ user }">
                <action-button
                    v-bind:tooltip="$t('helper.add_user_in_team')"
                    v-bind:tooltip-disabled="$t('helper.add_user_in_team')"
                    v-bind:disabled="team.users.includes( user )"
                    @press="$emit('add-user', user )"
                    icon="person_add">
                </action-button>
            </template>
        </users-grid>

        <template v-slot:footer>
            <button @click="close()" class="waves-effect waves-green btn-flat">Fermer</button>
        </template>

    </modal-layout>
</template>

<script>
    module.exports = {
        mixins: [VueModal],
        props: ['team'],
        data: function ( ) {
            return {
                institution: new Institution( ),
                search: ""
            };
        },
        computed: {
            peopleNotInTeam( ) {
                return this.institution.users.filter(user => !this.team.users.includes(user));
            }
        },
        methods: {
            institutionChanged(institution) {
                this.institution = institution;
            }
        },
        components: {
            'users-grid': vueLoader('components/grids/usersGrid')
        }
    };
</script>
