<template>
    <div id="contributors">
        <tabs-layout
            v-bind:tabs="[
            {label:$t('teams')},
            {label:$t('users')},
            {disabled: true},
            {disabled: true}
            ]"
            v-bind:tabs-template.camel="'max-content max-content 1fr max-content'">

            <template v-slot:tab.3.header>
                <institution-select @institution-change="institutionChanged($event)"></institution-select>
            </template>

            <template v-slot:tab.4.header>
                <search-box v-model="search"></search-box>
            </template>


            <template v-slot:tab.1>
                <teams-grid
                    v-bind:search="search"
                    v-bind:teams="institution.teams"
                    @selection-change="selection = $event">

                    <template v-slot:side.actions="{ team }">
                        <slot name="team.side.actions" v-bind:team="team"></slot>
                    </template>
                </teams-grid>
            </template>
            <template v-slot:tab.2>
                <users-grid
                    v-bind:search="search"
                    v-bind:people="institution.users"
                    @selection-change="selection = $event">

                    <template v-slot:side.actions="{ user }">
                        <slot name="user.side.actions" v-bind:user="user"></slot>
                    </template>
                </users-grid>
            </template>
        </tabs-layout>
    </div>
</template>

<script>
    module.exports = {
        data() {
            return {
                search: "",
                institution: new Institution()
            };
        },
        methods: {
            institutionChanged(institution) {
                this.institution = institution;
            }
        },
        components: {
            'teams-grid': vueLoader('components/grids/teamsGrid'),
            'users-grid': vueLoader('components/grids/usersGrid')
        }
    };
</script>
