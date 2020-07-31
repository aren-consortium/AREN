<template>
    <grid-layout
        v-bind:columns="[ '1fr', '1fr', '1fr', '1fr', '1fr' ]"
        v-bind:headers="[ $t('first_name'), $t('last_name'), $t('authority'), $t('email') , $t('username') ]"
        v-bind:items="sortedPeople"
        @selection-change="$emit('selection-change', $event)">

        <template v-if="$scopedSlots['side.actions']" v-slot:side.actions="{ value: user }">
            <slot name="side.actions" v-bind:user="user"></slot>
        </template>

        <template v-slot:column.1="{ value: user }">
            {{ user.lastName }}
        </template>

        <template v-slot:column.2="{ value: user }">
            {{ user.firstName }}
        </template>

        <template v-slot:column.3="{ value: user }">
            <template v-if="editable && $root.user.is(user.authority)">
                <select class="browser-default"
                        v-model="user.authority"
                        @change="updateUser(user)">
                    <option value="USER">{{ $t('USER') }}</option>
                    <option v-if="$root.user.is('MODO')" value="MODO">{{ $t('MODO') }}</option>
                    <option v-if="$root.user.is('ADMIN')" value="ADMIN">{{ $t('ADMIN') }}</option>
                    <option v-if="$root.user.is('SUPERADMIN')" value="SUPERADMIN">{{ $t('SUPERADMIN') }}</option>
                </select>
            </template>
            <template v-else>
                {{ $t(user.authority) }}
            </template>
        </template>

        <template v-slot:column.4="{ value: user }">
            {{ user.email }}
        </template>

        <template v-slot:column.5="{ value: user }">
            {{ user.username }}
        </template>

    </grid-layout>
</template>

<script>
    module.exports = {
        mixins: [VueGrid],
        props: ['people', 'editable', 'search'],
        computed: {
            sortedPeople() {
                if (this.search) {
                    return this.people.filter((person) => {
                        return person.authority !== Authority.DELETED && (person.lastName + person.firstName).toLowerCase().includes(this.search.toLowerCase());
                    }).sort((a, b) => (a.lastName + a.firstName > b.lastName + b.firstName) ? 1 : -1);
                } else {
                    return this.people.sort((a, b) => (a.lastName + a.firstName > b.lastName + b.firstName) ? 1 : -1);
                }
            }
        },
        methods: {
            updateUser(user) {
                ArenService.Users.edit({
                    data: user
                });
            }
        }
    };
</script>
