<template>
    <grid-layout 
        v-bind:columns="[ '1fr', '1fr', '1fr' ]"
        v-bind:actions="[ '1' ]"
        v-bind:headers="[  $t('title'), $t('users'), $t('debates') ]"
        v-bind:items="sortedTeams"
        @selection-change="$emit('selection-change', $event)">

        <template v-slot:column.1="{ value: team }">
            {{ team.name }}
        </template>

        <template v-slot:column.2="{ value: team }">
            {{ $tc('nb_users', team.usersCount) }}
        </template>

        <template v-slot:column.3="{ value: team }">
            {{ $tc('nb_debates', team.debatesCount) }}
        </template>

        <template v-if="$scopedSlots['side.actions']" v-slot:side.actions="{ value: team }">
            <slot name="side.actions" v-bind:team="team"></slot>
        </template>

    </grid-layout>
</template>

<script>
    module.exports = {
        mixins: [VueGrid],
        props: ['teams', 'search'],
        computed: {
            sortedTeams( ) {
                if (this.search) {
                    return this.teams.filter((team) => {
                        return team.name.toLowerCase( ).includes(this.search.toLowerCase( ));
                    }).sort((a, b) => (a.name > b.name) ? 1 : -1);
                } else {
                    return this.teams.sort((a, b) => (a.name > b.name) ? 1 : -1);
                }
            }
        }
    };
</script>