<template>
    <grid-layout
        v-bind:extendable="extendable"
        v-bind:columns="[ 'min-content', '4fr', '1fr' ]"
        v-bind:headers="[ $t('category'), $t('title'), $t('author') ]"
        v-bind:actions="[ 2 ]"
        v-bind:items="categories"
        v-bind:grouped-items="( group ) => group.documents"
        v-bind:sort="( a, b ) => (a.name > b.name) ? 1 : -1"
        @selection-change="$emit('selection-change', $event)">

        <template v-slot:row.group="{ value: category }">
            <div tabindex="0"
                 class="card center-align">
                <div class="card-image light-color valign-wrapper">
                    <img v-bind:src="category.picture">
                </div>
                <div>
                    <h2 v-bind:title="category.name">{{ category.name }}</h2>
                </div>
                <div v-if="$scopedSlots['card.actions']" class="card-action">
                    <slot name="card.actions" v-bind:category="category"></slot>
                </div>
            </div>
        </template>

        <template v-if="$scopedSlots['side.actions']" v-slot:side.actions="{ value: document }">
            <slot name="side.actions" v-bind:document="document"></slot>
        </template>

        <template v-slot:column.1="{ value: document }">
            {{ document.name }}
        </template>

        <template v-slot:column.2="{ value: document }">
            {{ document.author }}
        </template>

        <template v-slot:action.1="{ value: document }">
            <router-link v-bind:to="'/documents/' + document.id" class="waves-effect waves-light btn">Aller au document</router-link>
        </template>

    </grid-layout>
</template>

<script>
    module.exports = {
        mixins: [VueGrid],
        props: ['categories', 'extendable']
    };
</script>